package open.dolphin.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
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
import open.dolphin.project.StubFactory;
import open.dolphin.relay.PVTRelayProxy;
import open.dolphin.server.PVTServer;
import open.dolphin.stampbox.StampBoxPlugin;

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
    private final int delay = 300;              // 300 mmsec

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
    }
//s.oh$
    
    // Dolphinをstatic instanceにする
    private static final Dolphin instance = new Dolphin();
    
    public static Dolphin getInstance() {
        return instance;
    }
//masuda$
    /**
     * Creates new Dolphin
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
        ProjectStub projectStub = StubFactory.create(mode);
        Project.setProjectStub(projectStub);
        
        // Project作成後、Look&Feel を設定する
        stub.setupUI();

        //------------------------------
        // ログインダイアログを表示する
        //------------------------------
        PluginLoader<ILoginDialog> loader = PluginLoader.load(ILoginDialog.class);
        Iterator<ILoginDialog> iter = loader.iterator();
        final ILoginDialog login = iter.next();
        login.addPropertyChangeListener(LoginDialog.LOGIN_PROP, (PropertyChangeEvent e) -> {
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
        setProviders(new HashMap<>());
        
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
        
//        // PHR
//        PHRProxy phr = new PHRProxy();
//        phr.setContext(this);
//        phr.start();
//        getProviders().put("PHRProxy", phr);
        
    }

    /**
     * ユーザーのStampTreeをロードする。
     */
    private void loadStampTree() {
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(Dolphin.class);

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
                    java.util.logging.Logger.getLogger(this.getClass().getName()).info("Creates a stamp tree from the resource for a new user.");

                    BufferedReader reader;
                    IStampTreeModel tm; 
                    try (InputStream in = ClientContext.getResourceAsStream("stamptree-seed.xml")) {
                        reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while((line = reader.readLine()) != null) {
                            sb.append(line);
                        }   
                        String treeXml = sb.toString();
                        // Tree情報を設定し保存する
                        tm = new open.dolphin.infomodel.StampTreeModel(); // 注意
                        tm.setUserModel(Project.getUserModel());
                        java.util.ResourceBundle stbundle = ClientContext.getMyBundle(StampBoxPlugin.class);
                        tm.setName(stbundle.getString("stampTree.personal.box.name"));
                        tm.setDescription(stbundle.getString("stampTree.personal.box.tooltip"));
                        FacilityModel facility = Project.getUserModel().getFacilityModel();
                        tm.setPartyName(facility.getFacilityName());
                        String url = facility.getUrl();
                        if (url != null) {
                            tm.setUrl(url);
                        }   
                        tm.setTreeXml(treeXml);
                    }
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
                String tmpErr = bundle.getString("error.initialize");
                String title = bundle.getString("title.optionPane.initialize");
                String fatalMsg = e.getMessage();
                fatalMsg = fatalMsg!=null ? fatalMsg : tmpErr;
                java.util.logging.Logger.getLogger(this.getClass().getName()).severe(fatalMsg);
                JOptionPane.showMessageDialog(null, fatalMsg, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }

            @Override
            protected void cancelled() {
                java.util.logging.Logger.getLogger(this.getClass().getName()).info("cancelled");
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

        String message = bundle.getString("message.initialize");
        String note = bundle.getString("note.readeingStamp");
        Component c = null;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation/delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            
            if (monitor.isCanceled() && (!worker.isCancelled())) {
                worker.cancel(true);
                
            } else {
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }

    /**
     * GUIを初期化する。
     */
    private void initComponents(List<IStampTreeModel> result) {
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(Dolphin.class);

        // 設定に必要な定数をコンテキストから取得する
        String windowTitle = bundle.getString("title.mainWindow");
        
        // i18n Change the default size of ChartImple
        Rectangle placeBounds = new Rectangle(0, 0, 1024, 768);
        int defaultWidth =  787;    // Mac で調整した値
        int defaultHeight = 690;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defaultX = (screenSize.width - placeBounds.width) / 2;
        int defaultY = (screenSize.height - defaultHeight) / 2;
        
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d(EEE) HH:mm");
        String fmt = bundle.getString("messageFormat.loginInfo");
        MessageFormat msf = new MessageFormat(fmt);
        String loginInfo = msf.format(new Object[]{
            Project.getUserModel().getCommonName(),
            sdf.format(new Date())});
        view = new MainView();
        view.getDateLbl().setText(loginInfo);
        view.setOpaque(true);
        myFrame.setContentPane(view); 

        //----------------------------------------
        // タブペインに格納する Plugin をロードする
        //----------------------------------------
        List<MainComponent> list = new ArrayList<>(3);
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
                    continue;
                }
            }
//s.oh$
//s.oh^ 2014/08/19 ID権限
            if(Project.isOtherCare()) {
                if(plugin instanceof WatingListImpl || plugin instanceof PatientScheduleImpl || plugin instanceof NLaboTestImporter) {
                    plugin.stop();
                    continue;
                }
            }
//s.oh$
            list.add(plugin);
        }
        java.util.logging.Logger.getLogger(this.getClass().getName()).info("main window plugin did load");

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
        getTabbedPane().addChangeListener((ChangeEvent e) -> {
            getStatusLabel().setText("");
            int index1 = getTabbedPane().getSelectedIndex();
            MainComponent plugin = (MainComponent) providers.get(String.valueOf(index1));
            if (plugin.getContext() == null) {
                plugin.setContext(Dolphin.this);
                plugin.start();
                getTabbedPane().setComponentAt(index1, plugin.getUI());
            } else {
                plugin.enter();
            }
            mediator.addChain(plugin);
        });

        // StateMagrを使用してメインウインドウの状態を制御する
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
            fApplication.setAboutHandler((com.apple.eawt.AppEvent.AboutEvent ae) -> {
                showAbout();
            });
        
            // Preference
            fApplication.setPreferencesHandler((com.apple.eawt.AppEvent.PreferencesEvent pe) -> {
                doPreference();
            });
        
            // Quit
            fApplication.setQuitHandler((com.apple.eawt.AppEvent.QuitEvent qe, com.apple.eawt.QuitResponse qr) -> {
                processExit();
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
            return;
        }
        if (pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
            return;
        }
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(Dolphin.class);
        
//s.oh^ 2014/10/03 インスペクタの制御
        int max = Project.getInt("inspector.open.max", 0);
        if(max > 0) {
            if(ChartImpl.getAllChart() != null && ChartImpl.getAllChart().size() >= max) {
                String msg = bundle.getString("message.overlimit.karteOpen");
                String title = bundle.getString("title.optionPane.karteOpen");
                JOptionPane.showMessageDialog(getFrame(), msg, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
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
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        // まだ開いていない場合
        
        boolean readOnly = Project.isReadOnly();
        
        if (!readOnly) {
            if (pvt.getPatientModel().getOwnerUUID()!= null) {
                // ダイアログで確認する
                String ptName = pvt.getPatientName();
//minagawa^ jdk7               
                String optionBrowseOnly = bundle.getString("option.browseOnly");
                String optionUnlock = bundle.getString("option.unlock");
                String[] options = {
                    optionBrowseOnly, 
                    optionUnlock, 
                    GUIFactory.getCancelButtonText()};
//minagawa$                
//s.oh^ 2014/10/03 排他処理のID表示
                //String msg = ptName + " 様のカルテは他の端末で編集中です。\n" +
                //        "ロック解除は編集中の端末がクラッシュした場合等に使用してください。";
                String[] uuid = pvt.getPatientModel().getOwnerUUID().split(":");
                String uid = null;
                if(uuid.length > 1) {
                    uid = uuid[0];
                }
//                String msg = ptName + " 様のカルテは他の端末" + ((uid != null) ? "(" + uid + ")" : "") + "で編集中です。\n" +
//                        "ロック解除は編集中の端末がクラッシュした場合等に使用してください。";
                String fmt = bundle.getString("messageFormat.exclusiveControl");
                MessageFormat msf = new MessageFormat(fmt);
                String obj = uid != null ? uid : "";
                String msg = msf.format(new Object[]{ptName,obj});
                String title = bundle.getString("title.optionPane.karteOpen");
//s.oh$

                int val = JOptionPane.showOptionDialog(
                        getFrame(), msg, ClientContext.getFrameTitle(title),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                switch (val) {
                    case 0:     // 閲覧のみは編集不可で所有権を設定しない
                        readOnly = true;
                        break;

                    case 1:     // 強制的に編集するときは所有権横取り ロック解除
                        pvt.getPatientModel().setOwnerUUID(clientUUID);
                        break;

                    case 2:     // キャンセル
                    case JOptionPane.CLOSED_OPTION:
                        return; 
                }
            } else {
                // 誰も開いていないときは自分が所有者
                pvt.getPatientModel().setOwnerUUID(clientUUID);
            }
        }
        PluginLoader<Chart> loader = PluginLoader.load(Chart.class);
        Iterator<Chart> iter = loader.iterator();
        Chart chart = null;
        if (iter.hasNext()) {
            chart = iter.next();
        }
        chart.setContext(this);
        chart.setPatientVisit(pvt);
        chart.setReadOnly(readOnly);    // RedaOnlyProp
        chart.start();
        // publish state
        scl.publishKarteOpened(pvt);
//masuda$         
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
            java.util.logging.Logger.getLogger(this.getClass().getName()).info("pvtServer did  start");
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
            java.util.logging.Logger.getLogger(this.getClass().getName()).info("sendClaim did  start");
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
            java.util.logging.Logger.getLogger(this.getClass().getName()).info("sendMml did  start");
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
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.start();
    }

    /**
     * 環境設定を行う。
     */
    public void doPreference() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.start();
    }

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
                java.util.logging.Logger.getLogger(this.getClass().getName()).info("stampTask doInBackground");
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).info("stampTask succeeded");
                shutdown();
            }

            @Override
            protected void failed(Throwable cause) {
//minagawa^ First Commit Win Control
                String test = (cause!=null && cause.getMessage()!=null) ? cause.getMessage() : null;
                if (test!=null && test.contains("First Commit Win")) {
                    SwingUtilities.invokeLater(() -> {
                        doFirstCommitWinAlert(treeTosave);
                    });
                    
                } else {
                    SwingUtilities.invokeLater(() -> {
                        doStoppingAlert();
                    });
                }
//minagawa$                
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning("stampTask failed");
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
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

        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String message = resource.getString("title.optionPane.saveEnv");
        String note = resource.getString("note.savingEv");
        Component c = getFrame();
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            monitor.setProgress(delayCount);
        });

        worker.execute();
    }

    /**
     * 未保存のドキュメントがある場合の警告を表示する。
     */
    private void alertDirty() {
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String msg0 = resource.getString("message.unsavedDocument");
        String msg1 = resource.getString("message.instraction.unsavedDocument");
        String taskTitle = resource.getString("title.optionPane.saveEnv");
        JOptionPane.showMessageDialog(
                (Component) null,
                new Object[]{msg0, msg1},
                ClientContext.getFrameTitle(taskTitle),
                JOptionPane.INFORMATION_MESSAGE);
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
        
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String optionExit = resource.getString("option.exit");
        String optionForceWrite = resource.getString("option.foceWrite");
        String[] options = {optionExit, optionForceWrite, GUIFactory.getCancelButtonText()};
        String msg = resource.getString("message.firstCommitWin");
        String title = resource.getString("title.optionPane.saveEnv");
        title = ClientContext.getFrameTitle(title);

        int option = JOptionPane.showOptionDialog(
                getFrame(), msg, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        
        switch (option) {
            case 0:
                shutdown();
                break;
                
            case 1:
                syncTreeAndShutDown(treeTosave);
                break;
                
            case 2:
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).info("stampTask doInBackground");
                // Stamp 保存
                StampDelegater dl = new StampDelegater();
                dl.forceSyncTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                java.util.logging.Logger.getLogger(this.getClass().getName()).info("stampTask succeeded");
                shutdown();
            }

            @Override
            protected void failed(Throwable cause) {
                SwingUtilities.invokeLater(() -> {
                    doStoppingAlert();
                });            
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning("stampTask failed");
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
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

        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String message = resource.getString("title.optionPane.saveEnv");
        String note = resource.getString("note.savingEv");
        Component c = getFrame();
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            monitor.setProgress(delayCount);
        });

        worker.execute();
    }

    /**
     * 終了処理中にエラーが生じた場合の警告をダイアログを表示する。
     * @param errorTask エラーが生じたタスク
     * @return ユーザの選択値
     */
    private void doStoppingAlert() {
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String msg1 = resource.getString("error.savingEnv1");
        String msg2 = resource.getString("error.savingEnv2");
        String msg3 = resource.getString("error.savingEnv3");
        String msg4 = resource.getString("error.savingEnv4");
        Object message = new Object[]{msg1, msg2, msg3, msg4};

        // 終了する
        String exitOption = resource.getString("option.exit");

        // キャンセルする
        String cancelOption = GUIFactory.getCancelButtonText();

        // 環境保存
        String taskTitle = resource.getString("title.optionPane.saveEnv");

        String title = ClientContext.getFrameTitle(taskTitle);

        String[] options = new String[]{cancelOption, exitOption};

        int option = JOptionPane.showOptionDialog(
                null, message, title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        
        if (option == 1) {
            shutdown();
        }
    }

    private void shutdown() {
        
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(e.toString());
            }
        }

        if (windowSupport != null) {
            JFrame myFrame = windowSupport.getFrame();
            myFrame.setVisible(false);
            myFrame.dispose();
        }
        java.util.logging.Logger.getLogger(this.getClass().getName()).info("Exits application");
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
                        String err = ClientContext.getMyBundle(Dolphin.class).getString("error.nullReturn");
                        throw new RuntimeException(err);
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
                        String err = ClientContext.getMyBundle(Dolphin.class).getString("error.nullReturn");
                        throw new RuntimeException(err);
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
        
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String msg_0 = resource.getString("message.readingFacilityCode1");
        String msg_1 = resource.getString("message.readingFacilityCode2");
        String msg_2 = resource.getString("message.readingFacilityCode3");
        String title = resource.getString("title.optionPane.readingFacilityCode");
        
        String[] msg = new String[3];
        msg[0] = msg_0;
        msg[1] = msg_1 + Project.getString(Project.FACILITY_CODE_OF_INSURNCE_SYSTEM);
        msg[2] = msg_2 + Project.getString(Project.JMARI_CODE);
        
        JOptionPane.showMessageDialog(null, 
                msg, ClientContext.getFrameTitle(title), 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showReadFacilityCodeError(Throwable e) {
        
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String[] msg = new String[3];
        msg[0] = resource.getString("error.readingFacilityCode");
        msg[1] = e.getMessage();
        
        String title = resource.getString("title.optionPane.readingFacilityCode");
        JOptionPane.showMessageDialog(null, 
                msg, ClientContext.getFrameTitle(title), 
                JOptionPane.WARNING_MESSAGE);
    }
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    public void outputAllKartePdf() {

        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String title = resource.getString("title.optionPane.batch.outputPDF");
        String msg1 = resource.getString("question.batch.outputPDF");
        String msg2 = resource.getString("comment.batch.outputPDF");
        String[] msg = new String[]{msg1,msg2};
        String ok = resource.getString("option.batch.PDF");
        String cancel = (String)UIManager.get("OptionPane.cancelButtonText");
        
        int option = JOptionPane.showOptionDialog(
                null, 
                msg, 
                ClientContext.getFrameTitle(title), 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE,
                null, 
                new String[]{ok, cancel}, 
                ok);
        if(option == 0) {
            
        }else{
            return;
        }
        
        patCounter = 0;
        patTotal = 1;
        progress = new ProgressMonitor(getFrame(), title, "", 0, 100);
        progress.setProgress(0);
        final SimpleWorker worker = new SimpleWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                PatientDelegater pdl = new PatientDelegater();
                List<PatientModel> pList = pdl.getAllPatient();
                patTotal = pList.size();
                for(PatientModel pm : pList) {
                    if(progress.isCanceled()) {
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
                String fmt = resource.getString("messageFormat.batch.outputPDFDone");
                MessageFormat msf = new MessageFormat(fmt);
                String msg = msf.format(new Object[]{ClientContext.getTempDirectory()});
                String title = resource.getString("title.optionPane.batch.outputPDF");
                title = ClientContext.getFrameTitle(title);
                JOptionPane.showMessageDialog((Component) null, msg, title, JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            protected void failed(Throwable e) {
                setProgress(100);
                String fmt = resource.getString("meesageFormat.batch.outputPDFError");
                MessageFormat msf = new MessageFormat(fmt);
                String msg = msf.format(new Object[]{ClientContext.getTempDirectory()});
                String title = resource.getString("title.optionPane.batch.outputPDF");
                title = ClientContext.getFrameTitle(title);
                JOptionPane.showMessageDialog((Component) null, msg, title, JOptionPane.ERROR_MESSAGE);
            }
        };

        worker.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals("progress")) {
                int val = (Integer)evt.getNewValue();
                progress.setProgress(val);
                String fmt = resource.getString("messageFormat.outputingPDF");
                String msg3 = String.format(fmt, patCounter, patTotal);
                progress.setNote(msg3);
            }
        });

        worker.execute();
    }
    
    private void outputAllKartePdfForPatient(PatientModel pm) {
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
        }
    }
    
    private void outputPdf(PatientModel pm, DocumentModel model, String dir) {
        if(model.getModules() != null) {
            KartePaneDumper_2 dumper = new KartePaneDumper_2();
            KartePaneDumper_2 pdumper = new KartePaneDumper_2();
            List<ModuleModel> soaModules = new ArrayList<>();
            List<ModuleModel> pModules = new ArrayList<>();
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
            java.util.ResourceBundle mBundle = ClientContext.getBundle();
            String timeStamp = ModelUtils.getDateAsFormatString(model.getDocInfoModel().getFirstConfirmDate(), mBundle.getString("KARTE_DATE_FORMAT"));
            sbTitle.append(timeStamp);
            if (Project.getUserModel().getCommonName()!=null && !Project.getBoolean("karte.title.username.hide")) {
                sbTitle.append(" ");
                sbTitle.append(Project.getUserModel().getCommonName());
            }
            java.util.ResourceBundle clBundle = ClientContext.getClaimBundle();
            if(model.getDocInfoModel().getHealthInsurance().startsWith(clBundle.getString("INSURANCE_SELF_PREFIX"))) {
                String selfIns = ClientContext.getMyBundle(Dolphin.class).getString("text.selfIns");
                sbTitle.append(selfIns);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            //KartePDFImpl2 pdf = new KartePDFImpl2(dir, sdf.format(model.getConfirmed()),
            KartePDFImpl2 pdf = new KartePDFImpl2(dir, sdf.format(model.getStarted()),
                                                  pm.getPatientId(), pm.getFullName(),
                                                  sbTitle.toString(),
                                                  new Date(), dumper, pdumper, null);
            String path = pdf.create();
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

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * ドルフィンサポートをオープンする。
     */
    public void browseDolphinSupport() {
        String url = ClientContext.getMyBundle(Dolphin.class).getString("url.openDolphin");
        browseURL(url);
    }

    /**
     * ドルフィンプロジェクトをオープンする。
     */
    public void browseDolphinProject() {
        String url = ClientContext.getMyBundle(Dolphin.class).getString("url.orca");
        browseURL(url);
    }

    /**
     * MedXMLをオープンする。
     */
    public void browseMedXml() {
        String url = ClientContext.getMyBundle(Dolphin.class).getString("url.medXML");
        browseURL(url);
    }

    /**
     * SGをオープンする。
     */
    public void browseSeaGaia() {
        String url = ClientContext.getMyBundle(Dolphin.class).getString("url.seaGaia");
        browseURL(url);
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
                } catch (IOException | URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
                }
            }
        }
    }

    /**
     * About を表示する。
     */
    public void showAbout() {
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
            Project.setString("lookAndFeel", nativeLaf);
 //masuda    再起動を促す           
            requestReboot();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    private boolean changeLookAndFeel() {
        
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String msg1 = resource.getString("question.changeLAF");
        String msg2 = resource.getString("message.changeLAF");
        String[] msg = new String[] {
            msg1,
            msg2
        };
        String change = resource.getString("option.change");
        String cancel = (String)UIManager.get("OptionPane.cancelButtonText");
        
        String title = resource.getString("title.optionPane.changeLAF");
        
        int option = JOptionPane.showOptionDialog(
                null, 
                msg, 
                ClientContext.getFrameTitle(title), 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE,
                null, 
                new String[]{change, cancel}, 
                change);
        
        return (option==0);
    }
    
    private void requestReboot() {
        // LAFの変更やPropertyのインポート・初期化はいったん再起動させることとする
        ResourceBundle resource = ClientContext.getMyBundle(Dolphin.class);
        String msg = resource.getString("instraction.changeLAF");
        String title = resource.getString("title.optionPane.changeLAF");
        title = ClientContext.getFrameTitle(title);
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
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

    /**
     * OpnDolphin entry point.
     * @param args project name
     */
    public static void main(String[] args) {
        Dolphin.getInstance().start(args.length==1 ? args[0] : "i18n");
        //Dolphin.getInstance().start(args.length==1 ? args[0] : "dolphin");
    }
}
