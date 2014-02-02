package open.dolphin.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.helper.ComponentMemory;
import open.dolphin.helper.WindowSupport;
import open.dolphin.helper.MenuSupport;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.TaskProgressMonitor;
import open.dolphin.helper.WorkerService;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.project.*;
import open.dolphin.server.PVTServer;
import open.dolphin.plugin.PluginLoader;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * アプリケーションのメインウインドウクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Dolphin extends Application implements MainWindow {

    // ProgressMonitorの規定値
    private static final int DEFAULT_MAX = 120*1000; // 120 sec
    private static final int DEFAULT_DELAY = 300;   // 300msec

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
    private Properties saveEnv;

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

    // URL クラスローダ
    private URLClassLoader pluginClassLoader;

    // ResourceMap
    private ResourceMap resource;

    // 終了可能フラグ
    private boolean canExit;

    // timerTask 関連
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private int maxEstimation = DEFAULT_MAX; // 120 秒
    private int delay = DEFAULT_DELAY;   // 300 mmsec

    // VIEW
    private MainView view;

    /**
     * Creates new MainWindow
     */
    public Dolphin() {
    }

    @Override
    protected void initialize(String[] args) {

        ApplicationContext ctxt = getContext();

        //------------------------------
        // ClientContext を生成する
        //------------------------------
        ClientContextStub stub = new ClientContextStub();
        ClientContext.setClientContextStub(stub);
        ClientContext.setApplicationContext(ctxt);

        //------------------------------
        // プロジェクトスタブを生成する
        //------------------------------
        Project.setProjectStub(new ProjectStub());

        //------------------------------
        // Resource 関連
        //------------------------------
        ResourceManager mgr = ctxt.getResourceManager();
        resource = mgr.getResourceMap(Dolphin.class);

        //------------------------------
        // PluginClassLoader を取得する
        //------------------------------
        pluginClassLoader = ClientContext.getPluginClassLoader();

//        //------------------------------
//        // Mac Application Menu
//        //------------------------------
//        if (ClientContext.isMac()) {
//
//            com.apple.eawt.Application fApplication = com.apple.eawt.Application.getApplication();
//
//            // About
//            fApplication.setAboutHandler(new com.apple.eawt.AboutHandler() {
//
//                @Override
//                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
//                    showAbout();
//                }
//            });
//
//            // Preference
//            fApplication.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
//
//                @Override
//                public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {
//                    doPreference();
//                }
//            });
//
//            // Quit
//            fApplication.setQuitHandler(new com.apple.eawt.QuitHandler() {
//
//                @Override
//                public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent qe, com.apple.eawt.QuitResponse qr) {
//                    processExit();
//                }
//            });
//        }
    }

    @Override
    protected void startup() {

        // ExitListner を登録する
        this.addExitListener(new ExitListener() {

            @Override
            public boolean canExit(EventObject e) {
                return isCanExit();
            }

            @Override
            public void willExit(EventObject event) {
            }
        });

        //------------------------------
        // ログインダイアログを表示する
        //------------------------------
        final LoginDialog login = new LoginDialog();
        login.addPropertyChangeListener("LOGIN_PROP", new PropertyChangeListener() {
           
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
                        setCanExit(true);
                        exit();
                        break;
                    case CANCELD:
                        setCanExit(true);
                        exit();
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

        // プラグインのプロバイダマップを生成する
        setProviders(new HashMap<String, MainService>());

        // 環境設定ダイアログで変更される場合があるので保存する
        saveEnv = new Properties();

        // PVT Sever を起動する
        if (Project.getUseAsPVTServer()) {
            startPvtServer();

        } else {
            saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_NOT_RUNNING);
        }

        // CLAIM送信を生成する
        if (Project.getSendClaim()) {
            startSendClaim();

        } else {
            saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_NOT_RUNNING);
        }
        if (Project.getClaimAddress() != null) {
            saveEnv.put(GUIConst.ADDRESS_CLAIM, Project.getClaimAddress());
        }

        // MML送信を生成する
        if (Project.getSendMML()) {
            startSendMml();

        } else {
            saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_NOT_RUNNING);
        }
        if (Project.getCSGWPath() != null) {
            saveEnv.put(GUIConst.CSGW_PATH, Project.getCSGWPath());
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
                if (treeList != null || treeList.size() > 0) {
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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "SHIFT_JIS"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while( (line = reader.readLine()) != null ) {
                        sb.append(line);
                    }

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
                    tm.setTreeXml(sb.toString());
                    in.close();
                    reader.close();

                    // 一度登録する
                    long treePk = stampDel.putTree(tm);

                    tm.setId(treePk);

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
                ClientContext.getBootLogger().fatal(fatalMsg);
                ClientContext.getBootLogger().fatal(e.getMessage());
                JOptionPane.showMessageDialog(null, fatalMsg, ClientContext.getFrameTitle("初期化"), JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }

            @Override
            protected void cancelled() {
                ClientContext.getBootLogger().debug("cancelled");
                System.exit(0);
            }
        };

        String message = "初期化";
        String note = "プラグインとスタンプツリーを読み込んでいます...";
        Component c = null;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

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

        WorkerService service = new WorkerService() {

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

        service.execute(worker);
    }

    /**
     * GUIを初期化する。
     */
    private void initComponents(List<IStampTreeModel> result) {

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

        //
        // mainWindowのコンテントを生成しFrameに追加する
        //
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

        //
        // タブペインに格納する Plugin をロードする
        //
        PluginLoader<MainComponent> loader = PluginLoader.load(MainComponent.class, pluginClassLoader);
        Iterator<MainComponent> iter = loader.iterator();

        MainComponent[] top = new MainComponent[2];
        List<MainComponent> list = new ArrayList<MainComponent>(3);

        // mainWindow のタブに、受付リスト、患者検索 ... の純に格納する
        while (iter.hasNext()) {
            MainComponent plugin = iter.next();
            if (plugin.getName().equals("受付リスト")) {
                top[0] = plugin;
            } else if (plugin.getName().equals("患者検索")) {
                top[1] = plugin;
            } else {
                list.add(plugin);
            }
        }
        list.add(0, top[0]);
        list.add(1, top[1]);

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

        //
        // タブの切り替えで plugin.enter() をコールする
        //
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
        stampBox.getFrame().setVisible(true);
        providers.put("stampBox", stampBox);

        windowSupport.getFrame().setVisible(true);
    }
    
    public boolean isCanExit() {
        return canExit;
    }

    public void setCanExit(boolean canExit) {
        this.canExit = canExit;
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
        PluginLoader<Chart> loader = PluginLoader.load(Chart.class, pluginClassLoader);
        Iterator<Chart> iter = loader.iterator();
        Chart chart = null;
        if (iter.hasNext()) {
            chart = iter.next();
        }
        chart.setContext(this);
        chart.setPatientVisit(pvt);                 //
        chart.setReadOnly(Project.isReadOnly());    // RedaOnlyProp
        chart.start();
    }

    /**
     * 新規診療録を作成する。
     */
    @Override
    public void addNewPatient() {

        PluginLoader<NewKarte> loader = PluginLoader.load(NewKarte.class, pluginClassLoader);
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
        PluginLoader<PVTServer> loader = PluginLoader.load(PVTServer.class, pluginClassLoader);
        Iterator<PVTServer> iter = loader.iterator();
        if (iter.hasNext()) {
            pvtServer = iter.next();
            pvtServer.setContext(this);
            pvtServer.setBindAddress(Project.getBindAddress());
            pvtServer.start();
            providers.put("pvtServer", pvtServer);
            saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_RUNNING);
        }
    }

    /**
     * CLAIM 送信を開始する。
     */
    private void startSendClaim() {
        PluginLoader<ClaimMessageListener> loader = PluginLoader.load(ClaimMessageListener.class, pluginClassLoader);
        Iterator<ClaimMessageListener> iter = loader.iterator();
        if (iter.hasNext()) {
            sendClaim = iter.next();
            sendClaim.setContext(this);
            sendClaim.start();
            providers.put("sendClaim", sendClaim);
            saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_RUNNING);
        }
    }

    /**
     * MML送信を開始する。
     */
    private void startSendMml() {
        PluginLoader<MmlMessageListener> loader = PluginLoader.load(MmlMessageListener.class, pluginClassLoader);
        Iterator<MmlMessageListener> iter = loader.iterator();
        if (iter.hasNext()) {
            sendMml = iter.next();
            sendMml.setContext(this);
            sendMml.start();
            providers.put("sendMml", sendMml);
            saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_RUNNING);
        }
    }

    /**
     * プリンターをセットアップする。
     */
    public void printerSetup() {

        Runnable r = new Runnable() {

            @Override
            public void run() {

                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                PrinterJob pj = PrinterJob.getPrinterJob();

                try {
                    pageFormat = pj.pageDialog(aset);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    /**
     * カルテの環境設定を行う。
     */
    public void setKarteEnviroment() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        sd.addPropertyChangeListener("SETTING_PROP", new PreferenceListener());
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.start();
    }

    public void nimbusLookAndFeel() {
        try {
            String nimbus = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"; //UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(nimbus);
            SwingUtilities.updateComponentTreeUI(getFrame());
            SwingUtilities.updateComponentTreeUI(stampBox.getFrame());
            Project.setString("lookAndFeel", nimbus);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void nativeLookAndFeel() {
        try {
            String nativeLaf = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLaf);
            SwingUtilities.updateComponentTreeUI(getFrame());
            SwingUtilities.updateComponentTreeUI(stampBox.getFrame());
            Project.setString("lookAndFeel", nativeLaf);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * 環境設定を行う。
     */
    public void doPreference() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        sd.addPropertyChangeListener("SETTING_PROP", new PreferenceListener());
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject(null);
        sd.start();
    }

    /**
     * 環境設定のリスナクラス。環境設定が終了するとここへ通知される。
     */
    class PreferenceListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {

            if (e.getPropertyName().equals("SETTING_PROP")) {

                boolean valid = ((Boolean) e.getNewValue()).booleanValue();

                if (valid) {

                    // 設定の変化を調べ、サービスの制御を行う
                    ArrayList<String> messages = new ArrayList<String>(2);

                    // PvtServer
                    boolean oldRunning = saveEnv.getProperty(GUIConst.KEY_PVT_SERVER).equals(GUIConst.SERVICE_RUNNING) ? true : false;
                    boolean newRun = Project.getUseAsPVTServer();
                    boolean start = ((!oldRunning) && newRun) ? true : false;
                    boolean stop = ((oldRunning) && (!newRun)) ? true : false;

                    if (start) {
                        startPvtServer();
                        messages.add("受付受信を開始しました。");
                    } else if (stop && pvtServer != null) {
                        pvtServer.stop();
                        pvtServer = null;
                        saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_NOT_RUNNING);
                        messages.add("受付受信を停止しました。");
                    }

                    // SendClaim
                    oldRunning = saveEnv.getProperty(GUIConst.KEY_SEND_CLAIM).equals(GUIConst.SERVICE_RUNNING) ? true : false;
                    newRun = Project.getSendClaim();
                    start = ((!oldRunning) && newRun) ? true : false;
                    stop = ((oldRunning) && (!newRun)) ? true : false;

                    boolean restart = false;
                    String oldAddress = saveEnv.getProperty(GUIConst.ADDRESS_CLAIM);
                    String newAddress = Project.getClaimAddress();
                    if (oldAddress != null && newAddress != null && (!oldAddress.equals(newAddress)) && newRun) {
                        restart = true;
                    }

                    if (start) {
                        startSendClaim();
                        saveEnv.put(GUIConst.ADDRESS_CLAIM, newAddress);
                        messages.add("CLAIM送信を開始しました。(送信アドレス=" + newAddress + ")");

                    } else if (stop && sendClaim != null) {
                        sendClaim.stop();
                        sendClaim = null;
                        saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_NOT_RUNNING);
                        saveEnv.put(GUIConst.ADDRESS_CLAIM, newAddress);
                        messages.add("CLAIM送信を停止しました。");

                    } else if (restart) {
                        sendClaim.stop();
                        sendClaim = null;
                        startSendClaim();
                        saveEnv.put(GUIConst.ADDRESS_CLAIM, newAddress);
                        messages.add("CLAIM送信をリスタートしました。(送信アドレス=" + newAddress + ")");
                    }

                    // SendMML
                    oldRunning = saveEnv.getProperty(GUIConst.KEY_SEND_MML).equals(GUIConst.SERVICE_RUNNING) ? true : false;
                    newRun = Project.getSendMML();
                    start = ((!oldRunning) && newRun) ? true : false;
                    stop = ((oldRunning) && (!newRun)) ? true : false;

                    restart = false;
                    oldAddress = saveEnv.getProperty(GUIConst.CSGW_PATH);
                    newAddress = Project.getCSGWPath();
                    if (oldAddress != null && newAddress != null && (!oldAddress.equals(newAddress)) && newRun) {
                        restart = true;
                    }

                    if (start) {
                        startSendMml();
                        saveEnv.put(GUIConst.CSGW_PATH, newAddress);
                        messages.add("MML送信を開始しました。(送信アドレス=" + newAddress + ")");

                    } else if (stop && sendMml != null) {
                        sendMml.stop();
                        sendMml = null;
                        saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_NOT_RUNNING);
                        saveEnv.put(GUIConst.CSGW_PATH, newAddress);
                        messages.add("MML送信を停止しました。");

                    } else if (restart) {
                        sendMml.stop();
                        sendMml = null;
                        startSendMml();
                        saveEnv.put(GUIConst.CSGW_PATH, newAddress);
                        messages.add("MML送信をリスタートしました。(送信アドレス=" + newAddress + ")");
                    }

                    if (messages.size() > 0) {
                        String[] msgArray = messages.toArray(new String[messages.size()]);
                        Object msg = msgArray;
                        Component cmp = null;
                        String title = ClientContext.getString("settingDialog.title");

                        JOptionPane.showMessageDialog(
                                cmp,
                                msg,
                                ClientContext.getFrameTitle(title),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }

    private boolean isDirty() {

        // 未保存のカルテがある場合は警告しリターンする
        // カルテを保存または破棄してから再度実行する
        boolean dirty = false;

        // Chart を調べる
        ArrayList<ChartImpl> allChart = ChartImpl.getAllChart();
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
            return false;
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

        // Stamp 保存
        final IStampTreeModel treeTosave = stampBox.getUsersTreeTosave();

        SimpleWorker simple = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ClientContext.getBootLogger().debug("stampTask doInBackground");
                // Stamp 保存
                StampDelegater dl = new StampDelegater();
                dl.putTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                ClientContext.getBootLogger().debug("stampTask succeeded");
                setCanExit(true);
                exit();
            }

            @Override
            protected void failed(Throwable cause) {
                doStoppingAlert();
                ClientContext.getBootLogger().warn("stampTask failed");
                ClientContext.getBootLogger().warn(cause);
            }
        };

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

        WorkerService service = new WorkerService() {

            @Override
            protected void startProgress() {
                delayCount = 0;
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
            }
        };

        service.execute(simple);
    }

    /**
     * 未保存のドキュメントがある場合の警告を表示する。
     */
    private void alertDirty() {
        String msg0 = resource.getString("exitDolphin.msg0"); //"未保存のドキュメントがあります。";
        String msg1 = resource.getString("exitDolphin.msg1"); //"保存または破棄した後に再度実行してください。";
        String taskTitle = resource.getString("exitDolphin.taskTitle");
        JOptionPane.showMessageDialog(
                (Component) null,
                new Object[]{msg0, msg1},
                ClientContext.getFrameTitle(taskTitle),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 終了処理中にエラーが生じた場合の警告をダイアログを表示する。
     * @param errorTask エラーが生じたタスク
     * @return ユーザの選択値
     */
    private void doStoppingAlert() {

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

        if (option == 1) {
            setCanExit(true);
            exit();
        }
    }

    @Override
    protected void shutdown() {

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
        ClientContext.getBootLogger().info("アプリケーションを終了します");
        System.exit(0);
    }

    /**
     * ユーザのパスワードを変更する。
     */
    public void changePassword() {

        PluginLoader<ChangeProfile> loader = PluginLoader.load(ChangeProfile.class, pluginClassLoader);
        Iterator<ChangeProfile> iter = loader.iterator();
        if (iter.hasNext()) {
            ChangeProfile cp = iter.next();
            cp.setContext(this);
            cp.start();
        }
    }

    /**
     * ユーザ登録を行う。管理者メニュー。
     */
    public void addUser() {

        PluginLoader<AddUser> loader = PluginLoader.load(AddUser.class, pluginClassLoader);
        Iterator<AddUser> iter = loader.iterator();
        if (iter.hasNext()) {
            AddUser au = iter.next();
            au.setContext(this);
            au.start();
        }
    }

    /**
     * Pluginを起動する。
     * @param pluginClass 起動するプラグインクラス。
     */
    public void invokeToolPlugin(String pluginClass) {

        try {
            MainTool tool = (MainTool) Class.forName(pluginClass, true, pluginClassLoader).newInstance();
            tool.setContext(this);
            tool.start();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    //--------------------------------------------------
    // 指定されたディレクトリ内の jar ファイルをリストアップする
    //--------------------------------------------------
    private void listJars(List<UpdateObject> list, File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                listJars(list, file);
            } else if (file.isFile()) {
                String name = file.getName();
                if (name.toLowerCase().endsWith(".jar")) {
                    UpdateObject o = new UpdateObject(name);
                    o.setLocalPath(file.getPath());
                    o.setLocalLast(file.lastModified());
                    list.add(o);
                }
            }
        }
    }

    //----------------------------------------------------------------------
    // インストールされている OpenDolphin.jar, lib 内のjar, plugin内の jar を得る
    //----------------------------------------------------------------------
    private List<UpdateObject> listCurrentJars() {

        List<UpdateObject> list = new ArrayList<UpdateObject>();

        //----------------------------------
        // OpenDolphin.jar の情報を得る
        //----------------------------------
        String dolphinJar = resource.getString("dolphin.jar");
        String dolphinDir = ClientContext.getDolphinJarDirectory();

        // dir と name から pathを作る
        StringBuilder sb = new StringBuilder();
        sb.append(dolphinDir);
        sb.append(File.separator);
        sb.append(dolphinJar);
        String path = sb.toString();

        File dolphin = new File(path);

        if (dolphin.exists()) {
            UpdateObject o = new UpdateObject(dolphin.getName());
            o.setLocalPath(dolphin.getPath());
            o.setLocalLast(dolphin.lastModified());
            list.add(o);

        } else {
            System.err.println("dolphin dose not exists. " + path);
        }

        //-----------------------------------
        // lib 内の jar
        //-----------------------------------
        File libDir = new File(ClientContext.getLibDirectory());
        listJars(list, libDir);

        //-----------------------------------
        // pluginの情報を得る
        //-----------------------------------
        File pluginDir = new File(ClientContext.getPluginsDirectory());
        listJars(list, pluginDir);

        return list;
    }

    /**
     * 更新対象JARの RemoteURLを得る。
     * @param resName   更新対象JARファイル（リソース）
     * @return          リソースへのURL
     * @throws MalformedURLException
     */
    private URL getRemoteURL(String resName) throws MalformedURLException {

        StringBuilder sb = new StringBuilder();

        if (ClientContext.isMac()) {
            sb.append(resource.getString("update.url.mac"));
        } else if (ClientContext.isWin()) {
            sb.append(resource.getString("update.url.win"));
        } else if (ClientContext.isLinux()) {
            sb.append(resource.getString("update.url.linux"));
        } else {
            sb.append(resource.getString("update.url.linux"));
        }

        sb.append(resName);
        String urlStr = sb.toString();

        // http://www.digital-globe.co.jp/.../OpenDolphin-1.4 etc
        ClientContext.getBootLogger().debug("remote url = " + urlStr);

        return new URL(urlStr);
    }

    //----------------------------------------------------------
    // 古いJarを新しいJarで書き換える。
    //----------------------------------------------------------
    private void writeUpdates(final List<UpdateObject> list) {

        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();

        Task task = new Task<Boolean, Void>(app) {

            @Override
            protected Boolean doInBackground() throws Exception {
                ClientContext.getBootLogger().debug("writeUpdates task doInBackground");

                for (UpdateObject uo : list) {
                    if (!uo.isNew()) {
                        continue;
                    }
                    //-----------------------------------------
                    // ローカルマシン内の更新jarファイルへ書き込む
                    //-----------------------------------------
                    File dest = new File(uo.getLocalPath());
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
                    out.write(uo.getBytes());
                    out.flush();
                    out.close();
                    ClientContext.getBootLogger().debug(dest.getPath() + " を更新しました");
                }

                return true;
            }

            @Override
            protected void succeeded(Boolean result) {
                ClientContext.getBootLogger().debug("writeUpdates task succeeded");
                if (result.booleanValue()) {

                    String title = resource.getString("update.title.text");
                    StringBuilder sb = new StringBuilder();
                    sb.append(resource.getString("update.success.msg1"));
                    sb.append("\n");
                    sb.append(resource.getString("update.success.msg2"));
                    String msg = sb.toString();

                    // Show succeeded message
                    JOptionPane.showMessageDialog(
                            null,
                            msg,
                            ClientContext.getFrameTitle(title),
                            JOptionPane.INFORMATION_MESSAGE);

                    shutdown();
                }
            }

            @Override
            protected void failed(Throwable cause) {
                ClientContext.getBootLogger().warn("writeUpdates task failed");
                ClientContext.getBootLogger().warn(cause);
                String errorMsg = resource.getString("update.failed.msg");
                String title = resource.getString("update.title.text");
                JOptionPane.showMessageDialog(
                        null,
                        errorMsg,
                        ClientContext.getFrameTitle(title),
                        JOptionPane.ERROR_MESSAGE);
            }
        };

        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = resource.getString("update.title.text");
        String note = resource.getString("update.writing.note");
        Component c = null;
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, 200, 120 * 1000);
        appCtx.getTaskService().execute(task);
    }

    /**
     * ソフトウェア更新メニューが選択された場合のエントリポイント。
     * 更新の有無を調べユーザーへ通知する。
     */
    public void update() {

        // 更新する JAR ファイル =
        // OpenDolphin.jar or OpenDolphin-version.jar　+
        // Pluginディレクトリ内の jar
        // lib ディレクトリの jar

        //----------------------------------------------
        // 更新するJARファイルのリストとそのlastModifiedを得る
        //----------------------------------------------
        final List<UpdateObject> list = listCurrentJars();

        //-------------------------------------------------
        // リモートサーバ上にある対応する JAR のlastModifiedを得る
        // 異なっているものが更新の対象となる
        //-------------------------------------------------
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();
        Task task = new Task<Boolean, Void>(app) {

            @Override
            protected Boolean doInBackground() throws Exception {
                ClientContext.getBootLogger().debug("update check doInBackground");
                for (UpdateObject uo : list) {

                    //-------------------------------------
                    // 更新対象JARファイル（リソース）のURLを得る
                    //-------------------------------------
                    URL url = getRemoteURL(uo.getName());

                    //-------------------------------------
                    // Open する
                    //-------------------------------------
                    URLConnection con = url.openConnection();

                    //-------------------------------------
                    // リソースの lastModifiedを設定する
                    //-------------------------------------
                    long remote = con.getLastModified();
                    uo.setRemoteLast(remote);

                    //-------------------------------------
                    // 長さを設定する
                    //-------------------------------------
                    int length = con.getContentLength();
                    uo.setContentLength(length);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Remote Info: ");
                    sb.append(uo.getName());
                    sb.append(",");
                    sb.append(remote);
                    sb.append(",");
                    sb.append(length);
                    ClientContext.getBootLogger().debug(sb.toString());
                }
                return true;
            }

            @Override
            protected void succeeded(Boolean update) {
                ClientContext.getBootLogger().debug("update check succeeded");

                // Progress Monitor を終了させるため
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public  void run( ) {
                        showUpdateStatus(list);
                    }
                });
            }

            @Override
            protected void failed(Throwable cause) {
                ClientContext.getBootLogger().warn("update check failed");
                ClientContext.getBootLogger().warn(cause);
            }
        };

        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = resource.getString("update.title.text");
        String note = resource.getString("update.checking.note");
        Component c = null; //getFrame();
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, 200, 60 * 1000);
        appCtx.getTaskService().execute(task);
    }

    /**
     * 更新の必要ありなしを表示する。
     * @param list 更新対象の情報
     */
    private void showUpdateStatus(List<UpdateObject> list) {

        boolean update = false;
        for (UpdateObject uo : list) {
            if (uo.isNew()) {
                update = true;
                break;
            }
        }

        if (update) {
            //-------------------------
            // 更新可能なものがある場合
            //-------------------------
            String updateYes = resource.getString("update.yes.text");
            String updateNo = resource.getString("update.no.text");
            String title = resource.getString("update.title.text");
            Object[] cstOptions = new Object[]{updateYes, updateNo};

            String msg = resource.getString("update.available.msg");
            ClientContext.getBootLogger().info(msg);
            int select = JOptionPane.showOptionDialog(
                    getFrame(),
                    msg,
                    ClientContext.getFrameTitle(title),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    ClientContext.getImageIcon(resource.getString("update.dialog.icon")),
                    cstOptions,
                    updateYes);
            if (select == 0) {
                //-------------------------
                // 更新する場合、環境を保存する
                //-------------------------
                updateSaveEnv(list);
            }

        } else {
            // 更新可能なものがない場合
            String msg = resource.getString("update.noUpdate.msg");
            String title = resource.getString("update.title.tex");
            ClientContext.getBootLogger().info(msg);
            JOptionPane.showMessageDialog(null, msg, title,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * OpenDolphin Clinet を更新する。
     */
    private void updateSaveEnv(final List<UpdateObject> list) {

        //--------------------------------------------
        // 保存されていないカルテがある場合は警告しリターンする
        //--------------------------------------------
        if (isDirty()) {
            alertDirty();
            return;
        }

        //--------------------------------------------
        // StampTreeを保存する
        //--------------------------------------------
        final IStampTreeModel treeTosave = stampBox.getUsersTreeTosave();

        SimpleWorker simple = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ClientContext.getBootLogger().debug("stampTask doInBackground");
                StampDelegater dl = new StampDelegater();
                dl.putTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                // モニターを切るため....
                Runnable r = new Runnable() {
                    @Override
                    public  void run( ) {
                        downloadUpdates(list);
                    }
                };
                SwingUtilities.invokeLater(r);
            }

            @Override
            protected void failed(Throwable cause) {
                doStoppingAlert();
                ClientContext.getBootLogger().warn("stampTask failed");
                ClientContext.getBootLogger().warn(cause);
            }
        };

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

        WorkerService service = new WorkerService() {

            @Override
            protected void startProgress() {
                delayCount = 0;
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
            }
        };

        service.execute(simple);
    }

    //-------------------------------------
    // 更新する JAR ファイルをダウンロードする
    //-------------------------------------
    private void downloadUpdates(final List<UpdateObject> list) {

        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();

        Task task = new Task<Void, Integer>(app) {

            @Override
            protected Void doInBackground() throws Exception {

                ClientContext.getBootLogger().debug("downloadUpdates doInBackground");

                int total = 0;

                for (UpdateObject uo : list) {
                    if (!uo.isNew()) {
                        continue;
                    }
                    int contentLength = uo.getContentLength();
                    if (contentLength > 0) {
                        total += contentLength;
                    }
                }

                int current = 0;
                ClientContext.getBootLogger().debug("total length = " + total);

                for (UpdateObject uo : list) {

                    if (!uo.isNew()) {
                        continue;
                    }

                    URL url = getRemoteURL(uo.getName());
                    URLConnection con = url.openConnection();
                    int contentLength = con.getContentLength();

                    // Create streams
                    DataInputStream din = new DataInputStream(new BufferedInputStream(con.getInputStream()));
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    BufferedOutputStream bout = new BufferedOutputStream(bo);
                    byte aByte;
                    int cur = 0;
                    setMessage(uo.getName() + "をダウンロードしています...   ");

                    // Read untill EOF
                    while (cur < contentLength) {
                        // Read byte
                        aByte = din.readByte();
                        bout.write(aByte);
                        cur++;
                        current++;
                        int percent = (int) (((float) current / (float) total) * 100F);
                        setProgress(new Integer(percent));
                    }

                    bout.flush();

                    // 更新オブジェクトのbyteへretain
                    uo.setBytes(bo.toByteArray());

                    din.close();
                    bo.close();
                    bout.close();
                }

                return null;
            }

            @Override
            protected void succeeded(Void result) {
                ClientContext.getBootLogger().debug("downloadUpdates succeeded");
                Runnable r = new Runnable() {
                    @Override
                    public  void run( ) {
                        writeUpdates(list);
                    }
                };
                SwingUtilities.invokeLater(r);
            }

            @Override
            protected void failed(Throwable cause) {
                ClientContext.getBootLogger().warn("downloadUpdates failed");
                ClientContext.getBootLogger().warn(cause);
            }

            @Override
            protected void cancelled() {
                ClientContext.getBootLogger().debug("downloadUpdates cancelled");
            }
        };
        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = resource.getString("update.title.text");
        String note = resource.getString("update.downloading.note");
        Component c = null; //getFrame();
        int min = 0;
        int max = 100;

        TaskProgressMonitor tpm = new TaskProgressMonitor(task, taskMonitor, c, message, note, min, max);
        appCtx.getTaskService().execute(task);
    }

    /**
     * ドルフィンサポートをオープンする。
     */
    public void browseDolphinSupport() {
        browseURL(resource.getString("menu.dolphinSupportUrl"));
    }

    /**
     * ドルフィンプロジェクトをオープンする。
     */
    public void browseDolphinProject() {
        browseURL(resource.getString("menu.dolphinUrl"));
    }

    /**
     * MedXMLをオープンする。
     */
    public void browseMedXml() {
        browseURL(resource.getString("menu.medXmlUrl"));
    }

    /**
     * SGをオープンする。
     */
    public void browseSeaGaia() {
        browseURL(resource.getString("menu.seaGaiaUrl"));
    }

    /**
     * URLをオープンする。
     * @param url URL
     */
    private void browseURL(String url) {

        try {
            if (ClientContext.isMac()) {
                ProcessBuilder builder = new ProcessBuilder("open", url);
                builder.start();

            } else if (ClientContext.isWin()) {
                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", url);
                builder.start();

            } else {
                String[] browsers = {
                    "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"
                };
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                    if (browser == null) {
                        throw new Exception("Could not find web browser");
                    } else {
                        Runtime.getRuntime().exec(new String[]{browser, url});
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * About を表示する。
     */
    public void showAbout() {
        AbstractProjectFactory f = Project.getProjectFactory();
        f.createAboutDialog();
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
        // メインウインドウなので閉じるだけは無効にする
        //getAction(GUIConst.ACTION_WINDOW_CLOSING).setEnabled(false);
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
                GUIConst.ACTION_SOFTWARE_UPDATE,
                GUIConst.ACTION_BROWS_DOLPHIN,
                GUIConst.ACTION_BROWS_DOLPHIN_PROJECT,
                GUIConst.ACTION_BROWS_MEDXML,
                GUIConst.ACTION_SHOW_ABOUT
            };
            mediator.enableMenus(enables);
            //mediator.enabledAction("metalLookAndFeel", true);
            mediator.enabledAction("nimbusLookAndFeel", true);
            mediator.enabledAction("nativeLookAndFeel", true);

            Action addUserAction = mediator.getAction(GUIConst.ACTION_ADD_USER);
            boolean admin = false;
            Collection<RoleModel> roles = Project.getUserModel().getRoles();
            for (RoleModel model : roles) {
                if (model.getRole().equals(GUIConst.ROLE_ADMIN)) {
                    admin = true;
                    break;
                }
            }
            addUserAction.setEnabled(admin);
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

        private MainWindowState loginState = new LoginState();
        private MainWindowState logoffState = new LogoffState();
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
        Application.launch(Dolphin.class, args);
    }
}
