package open.dolphin.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.delegater.PatientDelegater;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.ComponentMemory;
import open.dolphin.helper.MenuSupport;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.WindowSupport;
import open.dolphin.impl.labrcv.NLaboTestImporter;
import open.dolphin.impl.login.LoginDialog;
import open.dolphin.impl.pvt.WatingListImpl;
import open.dolphin.impl.schedule.PatientScheduleImpl;
import open.dolphin.infomodel.ActivityModel;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.ProgressCourse;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.letter.KartePDFImpl2;
import open.dolphin.plugin.PluginLoader;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectSettingDialog;
import open.dolphin.project.ProjectStub;
import open.dolphin.relay.PVTRelayProxy;
import open.dolphin.server.PVTServer;
import open.dolphin.stampbox.StampBoxPlugin;
import open.dolphin.util.Log;

/**
 * アプリケーションのメインウインドウクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Dolphin implements MainWindow {

    // Window と Menu サポート
    private WindowSupport windowSupport;

    // Mediator
    private Mediator mediator;

    // 状態制御
    private StateManager stateMgr;

    // プラグインのプロバイダ
    private HashMap<String, MainService> providers;

    // プリンターセットアップはMainWindowのみで行い、設定された PageFormat各プラグインが使用する
    private PageFormat pageFormat;

    // 環境設定用の Properties
    //private Properties saveEnv;

    // BlockGlass
    private BlockGlass blockGlass;

    // StampBox
    private StampBoxPlugin stampBox;

    // 受付受信サーバ
    private PVTServer pvtServer;

    // CLAIM リスナ
    private ClaimMessageListener sendClaim;

    // MML リスナ
    private MmlMessageListener sendMml;

    // timerTask 関連
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private final int maxEstimation = 120*1000; // 120 秒
    private final int delay = 300;      // 300 mmsec

    // VIEW
    private MainView view;
    
//masuda^    
    // 状態変化リスナー
    private ChartEventHandler scl;
    
    // clientのUUID
    private String clientUUID;
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    private ProgressMonitor progress;
    private int patCounter;
    private int patTotal;
//s.oh$

    public String getClientUUID() {
        return clientUUID;
    }
    
//s.oh^ 2014/10/03 排他処理のID表示
    public void setClientUUID(String uuid) {
        clientUUID = uuid;
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "New Owner UUID:", clientUUID);
    }
//s.oh$
    
    // Dolphinをstatic instanceにする
    private static Dolphin instance = new Dolphin();
    
    public static Dolphin getInstance() {
        return instance;
    }
//masuda$
    /**
     * Creates new MainWindow
     */
    private Dolphin() {
    }

    public void start(String mode) {
        
//masuda^         
         // 排他処理用のUUIDを決める
        clientUUID = UUID.randomUUID().toString();
//masuda$

        // ClientContext を生成する
        ClientContextStub stub = new ClientContextStub(mode);
        ClientContext.setClientContextStub(stub);

        // プロジェクトスタブを生成する
        Project.setProjectStub(new ProjectStub());
        
//s.oh^ Log出力対応 2013/07/04
        Log.createLogSet(ClientContext.getSettingDirectory() + File.separator,
                         ClientContext.getLogDirectory() + File.separator,
                         (Project.getString("log.server.dir") == null) ? null : Project.getString("log.server.dir") + File.separator);
        Log.setOtherName(System.getProperty("user.name"));
        Project.getProjectStub().outputUserDefaults();
        //Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "_/_/_/_/_/ Dolphin開始 _/_/_/_/_/");
        Log.outputOperLogOper(null, Log.LOG_LEVEL_0, "_/_/_/_/_/ Dolphin開始 _/_/_/_/_/");
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "Owner UUID:", clientUUID);
//s.oh$
        
        // Project作成後、Look&Feel を設定する
        stub.setupUI();

        //------------------------------
        // ログインダイアログを表示する
        //------------------------------
        PluginLoader<ILoginDialog> loader = PluginLoader.load(ILoginDialog.class);
        Iterator<ILoginDialog> iter = loader.iterator();
        final ILoginDialog login = iter.next();
        login.addPropertyChangeListener(LoginDialog.LOGIN_PROP, new PropertyChangeListener() {
           
            @Override
            public void propertyChange(PropertyChangeEvent e) {

                LoginDialog.LoginStatus result = (LoginDialog.LoginStatus) e.getNewValue();
                login.close();

                switch (result) {
                    case AUTHENTICATED:
                        startServices();
                        loadStampTree();
                        break;
                    case NOT_AUTHENTICATED:
                        shutdown();
                        break;
                    case CANCELD:
                        shutdown();
                        break;
                }
            }
            
        });
        login.start();
    }

    /**
     * 起動時のバックグラウンドで実行されるべきタスクを行う。
     */
    private void startServices() {
        
//masuda^        
         // ChartStateListenerを開始する
        scl = ChartEventHandler.getInstance();
        scl.start();
//masuda$      

        // プラグインのプロバイダマップを生成する
        setProviders(new HashMap<String, MainService>());
        
//minagawa^ Server-ORCA連携
        // Client-ORCA接続の時のみ起動
        if (Project.claimSenderIsClient()) {
            
            // 受付受信
            if (Project.getBoolean(Project.USE_AS_PVT_SERVER)) {
                startPvtServer();
                Project.setBoolean(GUIConst.PVT_SERVER_IS_RUNNING, true);
            } else {
                Project.setBoolean(GUIConst.PVT_SERVER_IS_RUNNING, false);
            }
            
            // CLAIM送信を生成する
            if (Project.getBoolean(Project.SEND_CLAIM) && Project.getString(Project.CLAIM_ADDRESS)!=null) {
                startSendClaim();
                Project.setBoolean(GUIConst.SEND_CLAIM_IS_RUNNING, true);
            } else {
                Project.setBoolean(GUIConst.SEND_CLAIM_IS_RUNNING, false);
            }
        }
        
//minagawa$

        // MML送信を生成する
        if (Project.getBoolean(Project.SEND_MML) && Project.getString(Project.SEND_MML_DIRECTORY)!=null) {
            startSendMml();
            Project.setBoolean(GUIConst.SEND_MML_IS_RUNNING, true);
        } else {
            Project.setBoolean(GUIConst.SEND_MML_IS_RUNNING, false);
        }
        
        // 受付リレー
        if (Project.getBoolean(Project.PVT_RELAY) && Project.getString(Project.PVT_RELAY_DIRECTORY)!=null) {
            PVTRelayProxy pvtRelay = new PVTRelayProxy();
            scl.addPropertyChangeListener(pvtRelay);
            Project.setBoolean(GUIConst.PVT_RELAY_IS_RUNNING, true);
        } else {
            Project.setBoolean(GUIConst.PVT_RELAY_IS_RUNNING, false);
        }
        
        // HTTP Server
        if (Project.getBoolean("visit.use")) {
            startHttpServer();
        }
    }

    /**
     * ユーザーのStampTreeをロードする。
     */
    private void loadStampTree() {

        final SimpleWorker worker = new SimpleWorker<List<IStampTreeModel>, Void>() {

            @Override
            protected List<IStampTreeModel> doInBackground() throws Exception {

                // ログインユーザーの PK
                long userPk = Project.getUserModel().getId();

                // ユーザーのStampTreeを検索する
                StampDelegater stampDel = new StampDelegater();
                List<IStampTreeModel> treeList = stampDel.getTrees(userPk);

                // User用のStampTreeが存在しない新規ユーザの場合、そのTreeを生成する
                boolean hasTree = false;
                //if (treeList != null || treeList.size() > 0) {
                if (treeList != null && treeList.size() > 0) {
                    for (IStampTreeModel tree : treeList) {
                        if (tree != null) {
                            long id = tree.getUserModel().getId();
                            if (id == userPk && tree instanceof open.dolphin.infomodel.StampTreeModel) { // 注意
                                hasTree = true;
                                break;
                            }
                        }
                    }
                }

                // 新規ユーザでデータベースに個人用のStampTreeが存在しなかった場合
                if (!hasTree) {
                    ClientContext.getBootLogger().debug("新規ユーザー、スタンプツリーをリソースから構築");

                    InputStream in = ClientContext.getResourceAsStream("stamptree-seed.xml");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8")); // DhiftJIS->UTF-8
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String treeXml = sb.toString();

                    // Tree情報を設定し保存する
                    IStampTreeModel tm = new open.dolphin.infomodel.StampTreeModel();       // 注意
                    tm.setUserModel(Project.getUserModel());
                    tm.setName(ClientContext.getString("stampTree.personal.box.name"));
                    tm.setDescription(ClientContext.getString("stampTree.personal.box.tooltip"));
                    FacilityModel facility = Project.getUserModel().getFacilityModel();
                    tm.setPartyName(facility.getFacilityName());
                    String url = facility.getUrl();
                    if (url != null) {
                        tm.setUrl(url);
                    }
                    tm.setTreeXml(treeXml);
                    in.close();
                    reader.close();

//minagawa^ 先勝ちの制御を行うため sysnc する
                    // 一度登録する
                    String pkAndVersion = stampDel.syncTree(tm);
                    String[] params = pkAndVersion.split(",");
                    tm.setId(Long.parseLong(params[0]));
                    ((StampTreeModel)tm).setVersionNumber(params[1]);
//minagawa$
                    // リストの先頭へ追加する
                    treeList.add(0, tm);
                }

                return treeList;
            }

            @Override
            protected void succeeded(final List<IStampTreeModel> result) {
                initComponents(result);
            }

            @Override
            protected void failed(Throwable e) {
                String fatalMsg = e.getMessage();
                fatalMsg = fatalMsg!=null ? fatalMsg : "初期化に失敗しました。";
                ClientContext.getBootLogger().fatal(fatalMsg);
                ClientContext.getBootLogger().fatal(e.getMessage());
                JOptionPane.showMessageDialog(null, fatalMsg, ClientContext.getFrameTitle("初期化"), JOptionPane.WARNING_MESSAGE);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, ClientContext.getFrameTitle("初期化"), fatalMsg);
                System.exit(1);
            }

            @Override
            protected void cancelled() {
                ClientContext.getBootLogger().debug("cancelled");
                System.exit(0);
            }

            @Override
            protected void startProgress() {
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                taskTimer = null;
                monitor = null;
            }
        };

        String message = "初期化";
        String note = "スタンプを読み込んでいます...";
        Component c = null;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation/delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        worker.execute();
    }

    /**
     * GUIを初期化する。
     */
    private void initComponents(List<IStampTreeModel> result) {

        // /open/dolphin/client/resources/Dolphin.properties
        ResourceBundle resource = ClientContext.getBundle(this.getClass());

        // 設定に必要な定数をコンテキストから取得する
        String windowTitle = resource.getString("title");
        Rectangle setBounds = new Rectangle(0, 0, 1000, 690);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defaultX = (screenSize.width - setBounds.width) / 2;
        int defaultY = (screenSize.height - setBounds.height) / 2;
        int defaultWidth = 666;
        int defaultHeight = 678;

        // WindowSupport を生成する この時点で Frame,WindowMenu を持つMenuBar が生成されている
        String title = ClientContext.getFrameTitle(windowTitle);
        windowSupport = WindowSupport.create(title);
        JFrame myFrame = windowSupport.getFrame();		// MainWindow の JFrame
        JMenuBar myMenuBar = windowSupport.getMenuBar();	// MainWindow の JMenuBar

        // Windowにこのクラス固有の設定をする
        Point loc = new Point(defaultX, defaultY);
        Dimension size = new Dimension(defaultWidth, defaultHeight);
        myFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                processExit();
            }
        });
        ComponentMemory cm = new ComponentMemory(myFrame, loc, size, this);
        cm.setToPreferenceBounds();

        // BlockGlass を設定する
        blockGlass = new BlockGlass();
        myFrame.setGlassPane(blockGlass);

        // mainWindowのメニューを生成しメニューバーに追加する
        mediator = new Mediator(this);
        AbstractMenuFactory appMenu = AbstractMenuFactory.getFactory();
        appMenu.setMenuSupports(mediator, null);
        appMenu.build(myMenuBar);
        mediator.registerActions(appMenu.getActionMap());

        // mainWindowのコンテントを生成しFrameに追加する
        StringBuilder sb = new StringBuilder();
        sb.append("ログイン ");
        sb.append(Project.getUserModel().getCommonName());
        sb.append("  ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d(EEE) HH:mm");
        sb.append(sdf.format(new Date()));
        String loginInfo = sb.toString();
        view = new MainView();
        view.getDateLbl().setText(loginInfo);
        view.setOpaque(true);
        myFrame.setContentPane(view); 

        //----------------------------------------
        // タブペインに格納する Plugin をロードする
        //----------------------------------------
        List<MainComponent> list = new ArrayList<MainComponent>(3);
        PluginLoader<MainComponent> loader = PluginLoader.load(MainComponent.class);
        Iterator<MainComponent> iter = loader.iterator();

        // mainWindow のタブに、受付リスト、患者検索 ... の純に格納する
        while (iter.hasNext()) {
            MainComponent plugin = iter.next();
//s.oh^ 2013/02/27 (予定カルテ対応)
             //とりあえずOFFにしとく
            if(plugin instanceof PatientScheduleImpl) {
                if(!Project.getBoolean(Project.USE_SCHEDULE_KARTE)) {
                    plugin.stop();
                    plugin = null;
                    continue;
                }
            }
//s.oh$
//s.oh^ 2014/08/19 ID権限
            if(Project.isOtherCare()) {
                if(plugin instanceof WatingListImpl || plugin instanceof PatientScheduleImpl || plugin instanceof NLaboTestImporter) {
                    plugin.stop();
                    plugin = null;
                    continue;
                }
            }
//s.oh$
            list.add(plugin);
        }
        ClientContext.getBootLogger().debug("main window plugin did load");

//        loader = PluginLoader.load(MainComponent.class, ClientContext.getPluginClassLoader());
//        iter = loader.iterator();
//
//        // mainWindow のタブに、受付リスト、患者検索 ... の純に格納する
//        while (iter.hasNext()) {
//            MainComponent plugin = iter.next();
//            list.add(plugin);
//        }

        // プラグインプロバイダに格納する
        // index=0 のプラグイン（受付リスト）は起動する
        int index = 0;
        for (MainComponent plugin : list) {

            if (index == 0) {
                plugin.setContext(this);
                plugin.start();
                getTabbedPane().addTab(plugin.getName(), plugin.getUI());
                providers.put(String.valueOf(index), plugin);
                mediator.addChain(plugin);

            } else {
                getTabbedPane().addTab(plugin.getName(), plugin.getUI());
                providers.put(String.valueOf(index), plugin);
            }

            index++;
        }
        list.clear();

        //-------------------------------------------
        // タブの切り替えで plugin.enter() をコールする
        //-------------------------------------------
        getTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                getStatusLabel().setText("");
                int index = getTabbedPane().getSelectedIndex();
                MainComponent plugin = (MainComponent) providers.get(String.valueOf(index));
                if (plugin.getContext() == null) {
                    plugin.setContext(Dolphin.this);
                    plugin.start();
                    getTabbedPane().setComponentAt(index, plugin.getUI());
                } else {
                    plugin.enter();
                }
                mediator.addChain(plugin);
            }
        });

        // StaeMagrを使用してメインウインドウの状態を制御する
        stateMgr = new StateManager();
        stateMgr.processLogin(true);

        // ログインユーザーの StampTree を読み込む
        stampBox = new StampBoxPlugin();
        stampBox.setContext(Dolphin.this);
        stampBox.setStampTreeModels(result);
        stampBox.start();
//s.oh^ 2014/08/19 ID権限
        //stampBox.getFrame().setVisible(true);
        if(Project.isOtherCare()) {
            stampBox.getFrame().setVisible(false);
        }else{
            stampBox.getFrame().setVisible(true);
        }
//s.oh$
        providers.put("stampBox", stampBox);

        //------------------------------
        // Mac Application Menu
        //------------------------------
        if (ClientContext.isMac()) {
        
            com.apple.eawt.Application fApplication = com.apple.eawt.Application.getApplication();
        
            // About
            fApplication.setAboutHandler(new com.apple.eawt.AboutHandler() {
        
                @Override
                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                    showAbout();
                }
            });
        
            // Preference
            fApplication.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
        
                @Override
                public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {
                    doPreference();
                }
            });
        
            // Quit
            fApplication.setQuitHandler(new com.apple.eawt.QuitHandler() {
        
                @Override
                public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent qe, com.apple.eawt.QuitResponse qr) {
                    processExit();
                }
            });
        }
        windowSupport.getFrame().setVisible(true);
    }

    @Override
    public JLabel getStatusLabel() {
        return view.getStatusLbl();
    }

    @Override
    public JProgressBar getProgressBar() {
        return view.getProgressBar();
    }

    @Override
    public JLabel getDateLabel() {
        return view.getDateLbl();
    }

    @Override
    public JTabbedPane getTabbedPane() {
        return view.getTabbedPane();
    }

    @Override
    public Component getCurrentComponent() {
        return getTabbedPane().getSelectedComponent();
    }

    @Override
    public BlockGlass getGlassPane() {
        return blockGlass;
    }

    @Override
    public MainService getPlugin(String id) {
        return providers.get(id);
    }

    @Override
    public HashMap<String, MainService> getProviders() {
        return providers;
    }

    @Override
    public void setProviders(HashMap<String, MainService> providers) {
        this.providers = providers;
    }

    /**
     * カルテをオープンする。
     * @param pvt 患者来院情報
     */
    @Override
    public void openKarte(PatientVisitModel pvt) {
        
//masuda^   すでにChart, EditorFrameが開いていた時の処理はここで行う
        if (pvt == null) {
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "カルテの表示", "PVTがありません");
            return;
        }
        if (pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "カルテの表示", "キャンセル済みの患者");
            return;
        }
        
//s.oh^ 2014/10/03 インスペクタの制御
        int max = Project.getInt("inspector.open.max", 0);
        if(max > 0) {
            if(ChartImpl.getAllChart() != null && ChartImpl.getAllChart().size() >= max) {
                String msg = "閲覧可能患者数を超えました。";
                JOptionPane.showMessageDialog(getFrame(), msg, ClientContext.getFrameTitle("カルテオープン"), JOptionPane.WARNING_MESSAGE);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, msg);
                return;
            }
        }
//s.oh$
        
        // このクライアントでChartImplとEditorFrameを開いていた場合の処理
        boolean opened = false;
        long ptId = pvt.getPatientModel().getId();
        for (ChartImpl chart : ChartImpl.getAllChart()) {
            if (chart.getPatient().getId() == ptId) {
                chart.getFrame().setExtendedState(java.awt.Frame.NORMAL);
                chart.getFrame().toFront();
                opened = true;
                break;
            }
        }

        //for (EditorFrame ef : allEditorFrames) {
        for (Chart ef : EditorFrame.getAllEditorFrames()) {    
            if (ef.getPatient().getId() == ptId) {
                ef.getFrame().setExtendedState(java.awt.Frame.NORMAL);
                ef.getFrame().toFront();
                break;
            }
        }
        if (opened) {
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "カルテの表示", "既に開いている");
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        Log.outputOperLogOper(null, Log.LOG_LEVEL_0, "カルテの表示", "表示開始", pvt.getPatientId(), String.valueOf(ptId));
        
        // まだ開いていない場合
        
        boolean readOnly = Project.isReadOnly();
        
        if (!readOnly) {
            if (pvt.getPatientModel().getOwnerUUID()!= null) {
                // ダイアログで確認する
                String ptName = pvt.getPatientName();
//minagawa^ jdk7               
                //String[] options = {"閲覧のみ", "ロック解除", "キャンセル"};
                String[] options = {"閲覧のみ", "ロック解除", GUIFactory.getCancelButtonText()};
//minagawa$                
//s.oh^ 2014/10/03 排他処理のID表示
                //String msg = ptName + " 様のカルテは他の端末で編集中です。\n" +
                //        "ロック解除は編集中の端末がクラッシュした場合等に使用してください。";
                String[] uuid = pvt.getPatientModel().getOwnerUUID().split(":");
                String uid = null;
                if(uuid.length > 1) {
                    uid = uuid[0];
                }
                String msg = ptName + " 様のカルテは他の端末" + ((uid != null) ? "(" + uid + ")" : "") + "で編集中です。\n" +
                        "ロック解除は編集中の端末がクラッシュした場合等に使用してください。";
//s.oh$

                int val = JOptionPane.showOptionDialog(
                        getFrame(), msg, ClientContext.getFrameTitle("カルテオープン"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, ClientContext.getFrameTitle("カルテオープン"), msg, pvt.getPatientModel().getOwnerUUID());

                switch (val) {
                    case 0:     // 閲覧のみは編集不可で所有権を設定しない
                        Log.outputOperLogDlg(null, Log.LOG_LEVEL_0,  "閲覧のみは編集不可で所有権を設定しない");
                        readOnly = true;
                        break;

                    case 1:     // 強制的に編集するときは所有権横取り ロック解除
                        Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, "強制的に編集(ロック解除)", clientUUID);
                        pvt.getPatientModel().setOwnerUUID(clientUUID);
                        break;

                    case 2:     // キャンセル
                    case JOptionPane.CLOSED_OPTION:
                        Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, msg, "キャンセル");
                        return; 
                }
            } else {
                // 誰も開いていないときは自分が所有者
                pvt.getPatientModel().setOwnerUUID(clientUUID);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "カルテUUID", clientUUID);
            }
        }
        PluginLoader<Chart> loader = PluginLoader.load(Chart.class);
        Iterator<Chart> iter = loader.iterator();
        Chart chart = null;
        if (iter.hasNext()) {
            chart = iter.next();
        }
        chart.setContext(this);
        chart.setPatientVisit(pvt);     //
        chart.setReadOnly(readOnly);    // RedaOnlyProp
        chart.start();
        // publish state
        scl.publishKarteOpened(pvt);
//masuda$         
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "カルテの表示", "表示終了");
    }

    /**
     * 新規診療録を作成する。
     */
    @Override
    public void addNewPatient() {

        PluginLoader<NewKarte> loader = PluginLoader.load(NewKarte.class);
        Iterator<NewKarte> iter = loader.iterator();
        if (iter.hasNext()) {
            NewKarte newKarte = iter.next();
            newKarte.setContext(this);
            newKarte.start();
        }
    }

    @Override
    public MenuSupport getMenuSupport() {
        return mediator;
    }

    /**
     * MainWindow のアクションを返す。
     * @param name Action名
     * @return Action
     */
    @Override
    public Action getAction(String name) {
        return mediator.getAction(name);
    }

    @Override
    public JMenuBar getMenuBar() {
        return windowSupport.getMenuBar();
    }

    @Override
    public void registerActions(ActionMap actions) {
        mediator.registerActions(actions);
    }

    @Override
    public void enabledAction(String name, boolean b) {
        mediator.enabledAction(name, b);
    }

    public JFrame getFrame() {
        return windowSupport.getFrame();
    }

    @Override
    public PageFormat getPageFormat() {
        if (pageFormat == null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob != null) {
                pageFormat = printJob.defaultPage();
            }
        }
        return pageFormat;
    }

    /**
     * ブロックする。
     */
    @Override
    public void block() {
        blockGlass.block();
    }

    /**
     * ブロックを解除する。
     */
    @Override
    public void unblock() {
        blockGlass.unblock();
    }

    /**
     * PVTServer を開始する。
     */
    private void startPvtServer() {
        PluginLoader<PVTServer> loader = PluginLoader.load(PVTServer.class);
        Iterator<PVTServer> iter = loader.iterator();
        if (iter.hasNext()) {
            pvtServer = iter.next();
            pvtServer.setContext(this);
            pvtServer.setBindAddress(Project.getString(Project.CLAIM_BIND_ADDRESS));
            pvtServer.start();
            providers.put("pvtServer", pvtServer);
            ClientContext.getBootLogger().debug("pvtServer did  start");
        }
    }

    /**
     * CLAIM 送信を開始する。
     */
    private void startSendClaim() {
        PluginLoader<ClaimMessageListener> loader = PluginLoader.load(ClaimMessageListener.class);
        Iterator<ClaimMessageListener> iter = loader.iterator();
        if (iter.hasNext()) {
            sendClaim = iter.next();
            sendClaim.setContext(this);
            sendClaim.start();
            providers.put("sendClaim", sendClaim);
            ClientContext.getBootLogger().debug("sendClaim did  start");
        }
    }

    /**
     * MML送信を開始する。
     */
    private void startSendMml() {
        PluginLoader<MmlMessageListener> loader = PluginLoader.load(MmlMessageListener.class);
        Iterator<MmlMessageListener> iter = loader.iterator();
        if (iter.hasNext()) {
            sendMml = iter.next();
            sendMml.setContext(this);
            // 出力先ディレクトリ
            sendMml.setCSGWPath(Project.getString(Project.SEND_MML_DIRECTORY));
            sendMml.start();
            providers.put("sendMml", sendMml);
            ClientContext.getBootLogger().debug("sendMml did  start");
        }
    }
    
    private void startHttpServer() {
        String test = Project.getString(Project.CLAIM_BIND_ADDRESS);
        if (test==null || test.equals("")) {
            return;
        }
        int port = Project.getInt("visit.http.port");
        String ctx = Project.getString("visit.http.context");
        try {
            InetAddress addr = InetAddress.getByName(test);
            InetSocketAddress socket = new InetSocketAddress(addr, port);
            HttpServer server = HttpServer.create(socket, 0);
            server.createContext(ctx, new HttpDolphinHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            ClientContext.getBootLogger().info("HTTP server is binded at " + socket.getHostName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
//    private static String getIPAddress() throws IOException{
//        
//        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//
//        while(interfaces.hasMoreElements()){
//            NetworkInterface network = interfaces.nextElement();
//            //System.err.println(network);
//            Enumeration<InetAddress> addresses = network.getInetAddresses();
//
//            while(addresses.hasMoreElements()){
//                
//                InetAddress test = addresses.nextElement();
//                if (test instanceof Inet6Address) {
//                    continue;
//                }
//                String address = test.getHostAddress();
//
//                //127.0.0.1と0.0.0.0以外のアドレスが見つかったらそれを返す
//                if(!"127.0.0.1".equals(address) && !"0.0.0.0".equals(address)){
//                    return address;
//                }
//            }
//        }
//
//        return "127.0.0.1";
//    }
    
    class HttpDolphinHandler implements HttpHandler {
        
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            InputStreamReader r = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(r);
            String line;
            StringBuilder buf = new StringBuilder();
            while ((line=br.readLine())!=null) {
                buf.append(line);
            }
            String text = buf.toString();
            
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            br.close();
            
            // Input text Karte
            if (text==null || text.equals("")) {
                return;
            }
            
            List<KarteEditor> list = KarteEditor.getAllKarte();
            if (!list.isEmpty()) {
                KarteEditor karte = list.get(0);
                karte.addDictation(text);
            }
        }
   }

    /**
     * プリンターをセットアップする。
     */
    public void printerSetup() {

//masuda^        
        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                PrinterJob pj = PrinterJob.getPrinterJob();

                try {
                    pageFormat = pj.pageDialog(aset);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }
        };
        worker.execute();
//masuda$
    }

    /**
     * カルテの環境設定を行う。
     */
    public void setKarteEnviroment() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
//minagawa^ Server-ORCA連携^ 複雑さを避けるため
        //sd.addPropertyChangeListener("SETTING_PROP", new PreferenceListener());
//minagawa$
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.start();
    }

    /**
     * 環境設定を行う。
     */
    public void doPreference() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        //sd.addPropertyChangeListener("SETTING_PROP", new PreferenceListener());
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.start();
    }

//    /**
//     * 環境設定のリスナクラス。環境設定が終了するとここへ通知される。
//     */
//    class PreferenceListener implements PropertyChangeListener {
//
//        @Override
//        public void propertyChange(PropertyChangeEvent e) {
//
//            if (e.getPropertyName().equals("SETTING_PROP")) {
//
//                boolean valid = ((Boolean) e.getNewValue()).booleanValue();
//
//                if (valid) {
//
//                    // 設定の変化を調べ、サービスの制御を行う
//                    ArrayList<String> messages = new ArrayList<String>(2);
//
//                    // PvtServer
//                    boolean oldRunning = Project.getBoolean(GUIConst.PVT_SERVER_IS_RUNNING);
//                    boolean newRun = Project.getBoolean(Project.USE_AS_PVT_SERVER);
//                    boolean start = ((!oldRunning) && newRun) ? true : false;
//                    boolean stop = ((oldRunning) && (!newRun)) ? true : false;
//
//                    if (start) {
//                        startPvtServer();
//                        messages.add("受付受信を開始しました。");
//                    } else if (stop && pvtServer != null) {
//                        pvtServer.stop();
//                        pvtServer = null;
//                        Project.setBoolean(GUIConst.PVT_SERVER_IS_RUNNING, false);
//                        messages.add("受付受信を停止しました。");
//                    }
//
//                    // SendClaim
//                    oldRunning = Project.getBoolean(GUIConst.SEND_CLAIM_IS_RUNNING);
//                    newRun = Project.getBoolean(Project.SEND_CLAIM);
//                    start = ((!oldRunning) && newRun) ? true : false;
//                    stop = ((oldRunning) && (!newRun)) ? true : false;
//
//                    boolean restart = false;
//                    String oldAddress = Project.getString(GUIConst.ADDRESS_CLAIM);
//                    String newAddress = Project.getString(Project.CLAIM_ADDRESS);
//                    if (oldAddress != null && newAddress != null && (!oldAddress.equals(newAddress)) && newRun) {
//                        restart = true;
//                    }
//
//                    if (start) {
//                        startSendClaim();
//                        Project.setString(GUIConst.ADDRESS_CLAIM, newAddress);
//                        messages.add("CLAIM送信を開始しました。(送信アドレス=" + newAddress + ")");
//
//                    } else if (stop && sendClaim != null) {
//                        sendClaim.stop();
//                        sendClaim = null;
//                        Project.setBoolean(GUIConst.SEND_CLAIM_IS_RUNNING, false);
//                        Project.setString(GUIConst.ADDRESS_CLAIM, newAddress);
//                        messages.add("CLAIM送信を停止しました。");
//
//                    } else if (restart) {
//                        sendClaim.stop();
//                        sendClaim = null;
//                        startSendClaim();
//                        Project.setString(GUIConst.ADDRESS_CLAIM, newAddress);
//                        messages.add("CLAIM送信をリスタートしました。(送信アドレス=" + newAddress + ")");
//                    }
//
//                    // SendMML
//                    oldRunning = Project.getBoolean(GUIConst.SEND_MML_IS_RUNNING);
//                    newRun = Project.getBoolean(Project.SEND_MML);
//                    start = ((!oldRunning) && newRun) ? true : false;
//                    stop = ((oldRunning) && (!newRun)) ? true : false;
//
//                    restart = false;
//                    oldAddress = Project.getString(GUIConst.CSGW_PATH);
//                    newAddress = Project.getCSGWPath();
//                    if (oldAddress != null && newAddress != null && (!oldAddress.equals(newAddress)) && newRun) {
//                        restart = true;
//                    }
//
//                    if (start) {
//                        startSendMml();
//                        Project.setString(GUIConst.CSGW_PATH, newAddress);
//                        messages.add("MML送信を開始しました。(送信アドレス=" + newAddress + ")");
//
//                    } else if (stop && sendMml != null) {
//                        sendMml.stop();
//                        sendMml = null;
//                        Project.setBoolean(GUIConst.SEND_MML_IS_RUNNING, false);
//                        Project.setString(GUIConst.CSGW_PATH, newAddress);
//                        messages.add("MML送信を停止しました。");
//
//                    } else if (restart) {
//                        sendMml.stop();
//                        sendMml = null;
//                        startSendMml();
//                        Project.setString(GUIConst.CSGW_PATH, newAddress);
//                        messages.add("MML送信をリスタートしました。(送信アドレス=" + newAddress + ")");
//                    }
//
//                    if (messages.size() > 0) {
//                        String[] msgArray = messages.toArray(new String[messages.size()]);
//                        Object msg = msgArray;
//                        Component cmp = null;
//                        String title = ClientContext.getString("settingDialog.title");
//
//                        JOptionPane.showMessageDialog(
//                                cmp,
//                                msg,
//                                ClientContext.getFrameTitle(title),
//                                JOptionPane.INFORMATION_MESSAGE);
//                    }
//                }
//            }
//        }
//    }

    private boolean isDirty() {

        // 未保存のカルテがある場合は警告しリターンする
        // カルテを保存または破棄してから再度実行する
        boolean dirty = false;

        // Chart を調べる
        List<ChartImpl> allChart = ChartImpl.getAllChart();
        if (allChart != null && allChart.size() > 0) {
            for (ChartImpl chart : allChart) {
                if (chart.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }

        // 保存してないものがあればリターンする
        if (dirty) {
            return true;
        }

        // EditorFrameのチェックを行う
        java.util.List<Chart> allEditorFrames = EditorFrame.getAllEditorFrames();
        if (allEditorFrames != null && allEditorFrames.size() > 0) {
            for (Chart chart : allEditorFrames) {
                if (chart.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }

        return dirty;
    }

    public void processExit() {

        if (isDirty()) {
            alertDirty();
            return;
        }
        
//s.oh^ 不具合修正(一括終了時のステータスクリア)
        setAllChartKarteClosedStatus();
//s.oh$
        
        // Stamp 保存
        final IStampTreeModel treeTosave = stampBox.getUsersTreeTosave();

        SimpleWorker worker = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ClientContext.getBootLogger().debug("stampTask doInBackground");
//s.oh^ 2014/08/19 ID権限
                if(Project.isOtherCare()) {
                    return null;
                }
//s.oh$
                // Stamp 保存
                StampDelegater dl = new StampDelegater();
                dl.putTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                ClientContext.getBootLogger().debug("stampTask succeeded");
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "StampTreeは正常に保存した。");
                shutdown();
            }

            @Override
            protected void failed(Throwable cause) {
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, "既にStampTreeが保存されている。");
//minagawa^ First Commit Win Control
                String test = (cause!=null && cause.getMessage()!=null) ? cause.getMessage() : null;
                //if (cause instanceof FirstCommitWinException) {
                if (test!=null && test.indexOf("First Commit Win")>=0) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            doFirstCommitWinAlert(treeTosave);
                        }
                    });
                    
                } else {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            doStoppingAlert();
                        }
                    });
                }
//minagawa$                
                ClientContext.getBootLogger().warn("stampTask failed");
                ClientContext.getBootLogger().warn(cause);
            }

            @Override
            protected void startProgress() {
                delayCount = 0;
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                taskTimer = null;
                monitor = null;
            }
        };

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String message = resource.getString("exitDolphin.taskTitle");
        String note = resource.getString("exitDolphin.savingNote");
        Component c = getFrame();
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }

    /**
     * 未保存のドキュメントがある場合の警告を表示する。
     */
    private void alertDirty() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String msg0 = resource.getString("exitDolphin.msg0"); //"未保存のドキュメントがあります。";
        String msg1 = resource.getString("exitDolphin.msg1"); //"保存または破棄した後に再度実行してください。";
        String taskTitle = resource.getString("exitDolphin.taskTitle");
        JOptionPane.showMessageDialog(
                (Component) null,
                new Object[]{msg0, msg1},
                ClientContext.getFrameTitle(taskTitle),
                JOptionPane.INFORMATION_MESSAGE);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, ClientContext.getFrameTitle(taskTitle), msg0, msg1);
    }
    
//s.oh^ 不具合修正(一括終了時のステータスクリア)
    private void setAllChartKarteClosedStatus() {
        // Chart を調べる
        List<ChartImpl> allChart = ChartImpl.getAllChart();
        if (allChart != null && allChart.size() > 0) {
            for (ChartImpl chart : allChart) {
                chart.publishKarteClosed();
//s.oh^ 2013/08/13
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e) {}
//s.oh$
            }
        }
    }
//s.oh$
    
    /**
     * 先勝ち制御アラート
     */
    private void doFirstCommitWinAlert(IStampTreeModel treeTosave) {
//minagawa^ mac jdk7        
        //String[] options = {"終 了", "強制書き込み", "キャンセル"};
        String[] options = {"終 了", "強制書き込み", GUIFactory.getCancelButtonText()};
//minagawa$        
        StringBuilder sb = new StringBuilder();
        sb.append("スタンプツリーは他の端末によって先に保存されています。").append("\n");
        sb.append("強制書き込みを選択すると先のツリーを上書きします。");
        String msg = sb.toString();
        String title = ClientContext.getFrameTitle("環境保存");

        int option = JOptionPane.showOptionDialog(
                getFrame(), msg, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, title, msg);
        
        switch (option) {
            case 0:
                Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, "終了");
                shutdown();
                break;
                
            case 1:
                Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, "強制書き込み");
                syncTreeAndShutDown(treeTosave);
                break;
                
            case 2:
                Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, "キャンセル");
                break;
        }
    }
    
    /**
     * StampTree 強制保存
     * @param treeTosave 
     */
    private void syncTreeAndShutDown(final IStampTreeModel treeTosave) {
        
        SimpleWorker worker = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ClientContext.getBootLogger().debug("stampTask doInBackground");
                // Stamp 保存
                StampDelegater dl = new StampDelegater();
                dl.forceSyncTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                ClientContext.getBootLogger().debug("stampTask succeeded");
                shutdown();
            }

            @Override
            protected void failed(Throwable cause) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        doStoppingAlert();
                    }
                });            
                ClientContext.getBootLogger().warn("stampTask failed");
                ClientContext.getBootLogger().warn(cause);
            }

            @Override
            protected void startProgress() {
                delayCount = 0;
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                taskTimer = null;
                monitor = null;
            }
        };

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String message = resource.getString("exitDolphin.taskTitle");
        String note = resource.getString("exitDolphin.savingNote");
        Component c = getFrame();
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }

    /**
     * 終了処理中にエラーが生じた場合の警告をダイアログを表示する。
     * @param errorTask エラーが生じたタスク
     * @return ユーザの選択値
     */
    private void doStoppingAlert() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String msg1 = resource.getString("exitDolphin.err.msg1");
        String msg2 = resource.getString("exitDolphin.err.msg2");
        String msg3 = resource.getString("exitDolphin.err.msg3");
        String msg4 = resource.getString("exitDolphin.err.msg4");
        Object message = new Object[]{msg1, msg2, msg3, msg4};

        // 終了する
        String exitOption = resource.getString("exitDolphin.exitOption");

        // キャンセルする
        String cancelOption = resource.getString("exitDolphin.cancelOption");

        // 環境保存
        String taskTitle = resource.getString("exitDolphin.taskTitle");

        String title = ClientContext.getFrameTitle(taskTitle);

        String[] options = new String[]{cancelOption, exitOption};

        int option = JOptionPane.showOptionDialog(
                null, message, title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, title, msg1, msg2, msg3, msg4);
        Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, options[option]);

        if (option == 1) {
            shutdown();
        }
    }

    private void shutdown() {
        Log.outputOperLogOper(null, Log.LOG_LEVEL_0, "_/_/_/_/_/ Dolphin終了 _/_/_/_/_/");
        //Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "_/_/_/_/_/ Dolphin終了 _/_/_/_/_/");
        
        // ChartEvenrHandler 終了
        try {
            if (scl!=null) {
                scl.stop();
            }
        } catch (Exception e) {
            //
        }

        if (providers != null) {

            try {
                Iterator iter = providers.values().iterator();
                while (iter != null && iter.hasNext()) {
                    MainService pl = (MainService) iter.next();
                    pl.stop();
                }

                //----------------------------------------
                // UserDefaults 保存 stop で保存するものあり
                //----------------------------------------
                Project.saveUserDefaults();

            } catch (Exception e) {
                e.printStackTrace(System.err);
                ClientContext.getBootLogger().warn(e.toString());
            }
        }

        if (windowSupport != null) {
            JFrame myFrame = windowSupport.getFrame();
            myFrame.setVisible(false);
            myFrame.dispose();
        }
        ClientContext.getBootLogger().debug("アプリケーションを終了します");
        System.exit(0);
    }

    /**
     * ユーザのパスワードを変更する。
     */
    public void changePassword() {

        PluginLoader<ChangeProfile> loader = PluginLoader.load(ChangeProfile.class);
        Iterator<ChangeProfile> iter = loader.iterator();
        if (iter.hasNext()) {
            ChangeProfile cp = iter.next();
            cp.setContext(this);
            cp.start();
        }
    }
    
    /**
     * 施設情報を編集する。管理者メニュー。
     */
    public void editFacilityInfo() {

        PluginLoader<AddUser> loader = PluginLoader.load(AddUser.class);
        Iterator<AddUser> iter = loader.iterator();
        if (iter.hasNext()) {
            AddUser au = iter.next();
            au.setContext(this);
            au.start();
        }
    }

    /**
     * ユーザ登録を行う。管理者メニュー。
     */
    public void addUser() {

        PluginLoader<AddUser> loader = PluginLoader.load(AddUser.class);
        Iterator<AddUser> iter = loader.iterator();
        if (iter.hasNext()) {
            AddUser au = iter.next();
            au.setContext(this);
            au.setStartIndex(1);
            au.start();
        }
    }
    
//s.oh^ 2014/07/08 クラウド0対応
    /**
     * 統計情報を取得する。
     */
    public void fetchActivities() {
        
        SwingWorker worker;
        worker = new SwingWorker<ActivityModel[], Void>() {
            
            @Override
            protected ActivityModel[] doInBackground() throws Exception {
                UserDelegater sdl = new UserDelegater();
                return sdl.fetchActivities();
            }
            
            @Override
            protected void done() {
                try {
                    ActivityModel[] am = get();
                    if (am==null) {
                        throw new RuntimeException("ヌル値がリターンされました。");
                    }
                    
                    AboutActivities aac = new AboutActivities(am);
                    aac.start();
                    
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };
        
        worker.execute();
    }
//s.oh$
    
    /**
     * 保険医療機関コードとJMARIコードを取得する
     */
    public void fetchFacilityCode() {
        
        SwingWorker worker = new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                OrcaDelegater odl = OrcaDelegaterFactory.create();
                return odl.getFacilityCodeBy1001();
            }
            
            @Override
            protected void done() {
                try {
                    String line = get();
                    if (line==null) {
                        throw new RuntimeException("ヌル値がリターンされました。");
                    }
                    
                    String insCode = line.substring(0, 10);
                    String jmari = line.substring(10);
                    
                    Project.setString(Project.FACILITY_CODE_OF_INSURNCE_SYSTEM, insCode);
                    Project.setString(Project.JMARI_CODE, jmari);
                    
                    showReadFacilityCodeResults();
                    
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                    showReadFacilityCodeError(ex);
                }
            }
        };
        
        worker.execute();
    }
    
    private void showReadFacilityCodeResults() {
        
        String[] msg = new String[3];
        msg[0] = "医療機関コードの読み込みに成功しました。";
        msg[1] = "保険医療機関コード: " + Project.getString(Project.FACILITY_CODE_OF_INSURNCE_SYSTEM);
        msg[2] = "JMARIコード: " + Project.getString(Project.JMARI_CODE);
        
        JOptionPane.showMessageDialog(null, 
                msg, ClientContext.getFrameTitle("医療機関コード読み込み"), 
                JOptionPane.INFORMATION_MESSAGE);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, ClientContext.getFrameTitle("医療機関コード読み込み"), msg[0], msg[1], msg[2]);
    }
    
    private void showReadFacilityCodeError(Throwable e) {
        
        String[] msg = new String[3];
        msg[0] = "医療機関コードの読み込みに失敗しました。";
        msg[1] = e.getMessage();
        
        JOptionPane.showMessageDialog(null, 
                msg, ClientContext.getFrameTitle("医療機関コード読み込み"), 
                JOptionPane.WARNING_MESSAGE);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, ClientContext.getFrameTitle("医療機関コード読み込み"), msg[0], msg[1]);
    }
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    public void outputAllKartePdf() {
////        JDialog dialog = new JDialog(new JFrame(), "一括カルテPDF出力", false);
////        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
////        JPanel panel = new JPanel();
////        panel.add(new JLabel("カルテのPDF出力中..."));
////        panel.setPreferredSize(new Dimension(200, 50));
////        dialog.getContentPane().add(panel, BorderLayout.CENTER);
////        dialog.pack();
////        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
////        int x = (size.width - dialog.getPreferredSize().width) / 2;
////        int y = (size.height - dialog.getPreferredSize().height) / 2;
////        dialog.setLocation(x, y);
////        dialog.setVisible(true);
//        block();
//        PatientDelegater pdl = new PatientDelegater();
//        try {
//            List<PatientModel> pList = pdl.getAllPatient();
//            for(PatientModel pm : pList) {
//                outputAllKartePdfForPatient(pm);
//            }
////            dialog.setVisible(false);
//            unblock();
//            JOptionPane.showMessageDialog((Component) null, "PDF出力が終わりました。", "一括カルテPDF出力", JOptionPane.INFORMATION_MESSAGE);
//            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "PDF出力が終わりました。");
//        } catch (Exception ex) {
////            dialog.setVisible(false);
//            unblock();
//            JOptionPane.showMessageDialog((Component) null, "PDF出力に失敗しました。", "一括カルテPDF出力", JOptionPane.ERROR_MESSAGE);
//            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "PDF出力に失敗しました。", ex.toString());
//        }
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "一括カルテPDF出力");
        
        String[] msg = new String[] {"全患者のカルテPDFを出力しますか？", "※PDF出力には時間がかかります。"};
        String ok = "はい";
        String cancel = (String)UIManager.get("OptionPane.cancelButtonText");
        
        int option = JOptionPane.showOptionDialog(
                null, 
                msg, 
                "一括カルテPDF出力", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE,
                null, 
                new String[]{ok, cancel}, 
                ok);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "一括カルテPDF出力", msg[0], msg[1]);
        if(option == 0) {
            Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, ok);
        }else{
            Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, cancel);
            return;
        }
        
        patCounter = 0;
        patTotal = 1;
        progress = new ProgressMonitor(getFrame(), "一括カルテPDF出力", "", 0, 100);
        progress.setProgress(0);
        final SimpleWorker worker = new SimpleWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                PatientDelegater pdl = new PatientDelegater();
                List<PatientModel> pList = pdl.getAllPatient();
                patTotal = pList.size();
                for(PatientModel pm : pList) {
                    if(progress.isCanceled()) {
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "キャンセルされました。");
                        return null;
                    }
                    patCounter += 1;
                    double tmp = 100 * ((double)patCounter / (double)patTotal);
                    if(patCounter >= patTotal) {
                        setProgress(100);
                    }else{
                        setProgress((int)tmp);
                    }
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException ie) {}
                    outputAllKartePdfForPatient(pm);
                }
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog((Component) null, "PDF出力が終わりました。\n" + ClientContext.getTempDirectory(), "一括カルテPDF出力", JOptionPane.INFORMATION_MESSAGE);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "PDF出力が終わりました。", ClientContext.getTempDirectory());
            }

            @Override
            protected void failed(Throwable e) {
                setProgress(100);
                JOptionPane.showMessageDialog((Component) null, "PDF出力に失敗しました。\n" + ClientContext.getTempDirectory(), "一括カルテPDF出力", JOptionPane.ERROR_MESSAGE);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "PDF出力に失敗しました。", e.toString());
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("progress")) {
                    int val = (Integer)evt.getNewValue();
                    progress.setProgress(val);
                    String msg = String.format("%d / %d 患者のカルテPDFを出力しています...", patCounter, patTotal);
                    progress.setNote(msg);
                }
            }
        });

        worker.execute();
    }
    
    private void outputAllKartePdfForPatient(PatientModel pm) {
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, String.valueOf(pm.getId()), pm.getPatientId(), pm.getFacilityId());
        DocumentDelegater ddl = new DocumentDelegater();
        StringBuilder sb = new StringBuilder();
        sb.append(ClientContext.getTempDirectory());
        sb.append(File.separator);
        sb.append(pm.getPatientId());
        File dir = new File(sb.toString());
        if(!dir.exists()) {
            dir.mkdirs();
        }
        try {
            List<DocumentModel> docList= ddl.getAllDocument(String.valueOf(pm.getId()));
            for(DocumentModel model : docList) {
                outputPdf(pm, model, dir.getPath());
            }
        } catch (Exception ex) {
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, ex.toString());
        }
    }
    
    private void outputPdf(PatientModel pm, DocumentModel model, String dir) {
        if(model.getModules() != null) {
            KartePaneDumper_2 dumper = new KartePaneDumper_2();
            KartePaneDumper_2 pdumper = new KartePaneDumper_2();
            List<ModuleModel> soaModules = new ArrayList<ModuleModel>();
            List<ModuleModel> pModules = new ArrayList<ModuleModel>();
            String soaSpec = null;
            String pSpec = null;
            for (ModuleModel bean : model.getModules()) {
                String role = bean.getModuleInfoBean().getStampRole();
                if(role.equals(IInfoModel.ROLE_SOA)) {
                    soaModules.add(bean);
                }else if(role.equals(IInfoModel.ROLE_SOA_SPEC)) {
                    soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                }else if(role.equals(IInfoModel.ROLE_P)) {
                    pModules.add(bean);
                }else if(role.equals(IInfoModel.ROLE_P_SPEC)) {
                    pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                }else if(bean.getModel() instanceof ProgressCourse) {
                    if(soaSpec==null) {
                        soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                    }else if(pSpec==null) {
                        pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                    }

                }else{
                    pModules.add(bean);
                }
            }
            if(soaSpec == null || soaSpec.length() <= 0) {
                soaSpec = "<section><paragraph><content><text></text></content></paragraph></section>";
            }
            if(pSpec == null || pSpec.length() <= 0) {
                pSpec = "<section><paragraph><content><text></text></content></paragraph></section>";
            }
            dumper.setSpec(soaSpec);
            dumper.setModuleList((ArrayList<ModuleModel>) soaModules);
            dumper.setSchemaList((ArrayList<SchemaModel>) model.getSchema());
            dumper.setAttachmentList((ArrayList<AttachmentModel>) model.getAttachment());
            pdumper.setSpec(pSpec);
            pdumper.setModuleList((ArrayList<ModuleModel>) pModules);
            
            StringBuilder sbTitle = new StringBuilder();
            String timeStamp = ModelUtils.getDateAsFormatString(model.getDocInfoModel().getFirstConfirmDate(), IInfoModel.KARTE_DATE_FORMAT);
            sbTitle.append(timeStamp);
            if (Project.getUserModel().getCommonName()!=null && !Project.getBoolean("karte.title.username.hide")) {
                sbTitle.append(" ");
                sbTitle.append(Project.getUserModel().getCommonName());
            }
            if(model.getDocInfoModel().getHealthInsurance().startsWith(IInfoModel.INSURANCE_SELF_PREFIX)) {
                sbTitle.append("（自費）");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            //KartePDFImpl2 pdf = new KartePDFImpl2(dir, sdf.format(model.getConfirmed()),
            KartePDFImpl2 pdf = new KartePDFImpl2(dir, sdf.format(model.getStarted()),
                                                  pm.getPatientId(), pm.getFullName(),
                                                  sbTitle.toString(),
                                                  new Date(), dumper, pdumper, null);
            String path = pdf.create();
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, "カルテPDF作成", path);
        }else{
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, "カルテ情報が存在しない", dir);
        }
    }
//s.oh$

    /**
     * Pluginを起動する。
     * @param pluginClass 起動するプラグインクラス。
     */
    public void invokeToolPlugin(String pluginClass) {

        try {
            MainTool tool = (MainTool) Class.forName(pluginClass).newInstance();
            tool.setContext(this);
            tool.start();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * ドルフィンサポートをオープンする。
     */
    public void browseDolphinSupport() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        browseURL(resource.getString("menu.dolphinSupportUrl"));
    }

    /**
     * ドルフィンプロジェクトをオープンする。
     */
    public void browseDolphinProject() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        browseURL(resource.getString("menu.dolphinUrl"));
    }

    /**
     * MedXMLをオープンする。
     */
    public void browseMedXml() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        browseURL(resource.getString("menu.medXmlUrl"));
    }

    /**
     * SGをオープンする。
     */
    public void browseSeaGaia() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        browseURL(resource.getString("menu.seaGaiaUrl"));
    }

    /**
     * URLをオープンする。
     * @param url URL
     */
    private void browseURL(String url) {

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                } catch (IOException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (URISyntaxException ex) {
                    ClientContext.getBootLogger().warn(ex);
                }
            }
        }
    }

    /**
     * About を表示する。
     */
    public void showAbout() {
        //AbstractProjectFactory f = Project.getProjectFactory();
        //f.createAboutDialog();
        AboutDolphin about = new AboutDolphin();
        about.start();
    }

    /**
     * シェーマボックスを表示する。
     */
    @Override
    public void showSchemaBox() {
        ImageBox imageBox = new ImageBox();
        imageBox.setContext(this);
        imageBox.start();
    }

    /**
     * スタンプボックスを表示する。
     */
    @Override
    public void showStampBox() {
        if (stampBox != null) {
            stampBox.enter();
        }
    }
    
 //masuda^ LokkAndFeel
    /**
     * NimbusLookAndFeelに設定する。
     */
    public void nimbusLookAndFeel() {
        
        // Look & Feel を変更するには再起動が必要であることを表示する
        if (!changeLookAndFeel()) {
            return;
        }
        
        try {
            String nimbus = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            /*UIManager.setLookAndFeel(nimbus);
            SwingUtilities.updateComponentTreeUI(getFrame());
            SwingUtilities.updateComponentTreeUI(stampBox.getFrame());*/
            Project.setString("lookAndFeel", nimbus);
 //masuda    再起動を促す 
            requestReboot();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * SystemLookAndFeeに設定する。
     */
    public void nativeLookAndFeel() {
        // Look & Feel を変更するには再起動が必要であることを表示する
        if (!changeLookAndFeel()) {
            return;
        }
        try {
            String nativeLaf = UIManager.getSystemLookAndFeelClassName();
            /*UIManager.setLookAndFeel(nativeLaf);
            SwingUtilities.updateComponentTreeUI(getFrame());
            SwingUtilities.updateComponentTreeUI(stampBox.getFrame());*/
            Project.setString("lookAndFeel", nativeLaf);
 //masuda    再起動を促す           
            requestReboot();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    private boolean changeLookAndFeel() {
        
        String[] msg = new String[] {
            "ルック & フィールを変更しますか?",
            "変更を反映するにはOpenDolphin/Proを再起動する必要があります。"
        };
        String change = "変更する";
        String cancel = (String)UIManager.get("OptionPane.cancelButtonText");
        
        int option = JOptionPane.showOptionDialog(
                null, 
                msg, 
                ClientContext.getFrameTitle("LAF変更"), 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE,
                null, 
                new String[]{change, cancel}, 
                change);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, ClientContext.getFrameTitle("LAF変更"), msg[0], msg[1]);
        if(option == 0) {
            Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, change);
        }else{
            Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, cancel);
        }
        
        return (option==0);
    }
    
    private void requestReboot() {

        // LAFの変更やPropertyのインポート・初期化はいったん再起動させることとする
        final String msg = "LAFの設定を変更しました。再起動してください。";
        final String title = ClientContext.getFrameTitle("設定変更");
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, title, msg);
        processExit();
    }
 //masuda$   

    /**
     * Mediator
     */
    protected final class Mediator extends MenuSupport {

        public Mediator(Object owner) {
            super(owner);
        }

        // global property の制御
        @Override
        public void menuSelected(MenuEvent e) {
        }

        @Override
        public void registerActions(ActionMap actions) {
            super.registerActions(actions);
        }
    }

    /**
     * MainWindowState
     */
    abstract class MainWindowState {

        public MainWindowState() {
        }

        public abstract void enter();

        public abstract boolean isLogin();
    }

    /**
     * LoginState
     */
    class LoginState extends MainWindowState {

        public LoginState() {
        }

        @Override
        public boolean isLogin() {
            return true;
        }

        @Override
        public void enter() {

            // Menuを制御する
            mediator.disableAllMenus();

            String[] enables = new String[]{
                GUIConst.ACTION_PRINTER_SETUP,
                GUIConst.ACTION_PROCESS_EXIT,
                GUIConst.ACTION_SET_KARTE_ENVIROMENT,
                GUIConst.ACTION_SHOW_STAMPBOX,
                GUIConst.ACTION_NEW_PATIENT,
                GUIConst.ACTION_SHOW_SCHEMABOX,
                GUIConst.ACTION_CHANGE_PASSWORD,
                GUIConst.ACTION_CONFIRM_RUN,
                GUIConst.ACTION_BROWS_DOLPHIN,
                GUIConst.ACTION_BROWS_DOLPHIN_PROJECT,
                GUIConst.ACTION_BROWS_MEDXML,
                GUIConst.ACTION_SHOW_ABOUT,
                GUIConst.ACTION_NIMBUS_LOOK_AND_FEEL,
                GUIConst.ACTION_NATIVE_LOOK_AND_FEEL,
//s.oh^ 2014/07/08 クラウド0対応
//minagawa^ 統計情報               
                GUIConst.ACTION_FETCH_ACTIVITIES,
//minagawa$                    
//s.oh$
//s.oh^ 2014/08/19 受付バーコード対応
                GUIConst.ACTION_RECEIPT_BARCODE,
//s.oh$
//s.oh^ 2014/07/22 一括カルテPDF出力
                GUIConst.ACTION_OUTPUT_ALLKARTEPDF
//s.oh$
            };
            mediator.enableMenus(enables);

            boolean admin = false;
            Collection<RoleModel> roles = Project.getUserModel().getRoles();
            for (RoleModel model : roles) {
                if (model.getRole().equals(GUIConst.ROLE_ADMIN)) {
                    admin = true;
                    break;
                }
            }
            
            // 施設情報編集
            Action editFacilityAction = mediator.getAction(GUIConst.ACTION_EDIT_FACILITY_INFO);
            editFacilityAction.setEnabled(admin);
            
            // 院内ユーザー登録
            Action addUserAction = mediator.getAction(GUIConst.ACTION_ADD_USER);
            addUserAction.setEnabled(admin);
            
            // 医療機関コード取得
            Action fetchFacilityCode = mediator.getAction(GUIConst.ACTION_FETCH_FACILITY_CODE);
            fetchFacilityCode.setEnabled(Project.canSearchMaster());
            
//s.oh^ 2014/08/19 ID権限
            if(Project.isOtherCare()) {
                Action printerSetup = mediator.getAction(GUIConst.ACTION_PRINTER_SETUP);
                printerSetup.setEnabled(false);
            }
//s.oh$
        }
    }

    /**
     * LogoffState
     */
    class LogoffState extends MainWindowState {

        public LogoffState() {
        }

        @Override
        public boolean isLogin() {
            return false;
        }

        @Override
        public void enter() {
            mediator.disableAllMenus();
        }
    }

    /**
     * StateManager
     */
    class StateManager {

        private final MainWindowState loginState = new LoginState();
        private final MainWindowState logoffState = new LogoffState();
        private MainWindowState currentState = logoffState;

        public StateManager() {
        }

        public boolean isLogin() {
            return currentState.isLogin();
        }

        public void processLogin(boolean b) {
            currentState = b ? loginState : logoffState;
            currentState.enter();
        }
    }

    public static void main(String[] args) {
        //String mode = (args.length==1) ? args[0] : null;
        String mode = (args.length==1) ? args[0] : "pro";
        Dolphin.getInstance().start(mode);
    }
}
