package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import open.dolphin.plugin.PluginLoader;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.StripeRenderer;
import open.dolphin.helper.WindowSupport;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.VersionModel;
import open.dolphin.plugin.PluginLister;
import open.dolphin.project.*;
import open.dolphin.util.GUIDGenerator;

import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * 2号カルテ、傷病名、検査結果履歴等、患者の総合的データを提供するクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ChartImpl extends AbstractMainTool implements Chart, IInfoModel {

    /** カルテ状態の束縛プロパティ名 */
    public static final String CHART_STATE = "chartStateProp";
    
    /** 診察未終了で閉じている状態 */
    public static final int CLOSE_NONE = 0;
    
    /** 診察が終了し閉じている状態  */
    public static final int CLOSE_SAVE = 1;
    
    /** 診察未終了でオープンしている状態 */
    public static final int OPEN_NONE = 2;
    
    /** 診察が終了しオープンしている状態 */
    public static final int OPEN_SAVE = 3;
    
    /** 受付キャンセル */
    public static final int CANCEL_PVT = -1;
    
    //  Chart インスタンスを管理するstatic 変数
    private static ArrayList<ChartImpl> allCharts = new ArrayList<ChartImpl>(3);
    
    // Chart 状態の通知を行うための static 束縛サポート
    private static PropertyChangeSupport boundSupport = new PropertyChangeSupport(new Object());
    
    // Document Plugin を格納する TabbedPane
    private JTabbedPane tabbedPane;
    
    // Active になっているDocument Plugin
    private Hashtable<String, ChartDocument> providers;
    
    // 患者インスペクタ 
    private PatientInspector inspector;
    
    // Window Menu をサポートする委譲クラス
    private WindowSupport windowSupport;
    
    // Toolbar
    private JPanel myToolPanel;
    
    // 検索状況等を表示する共通のパネル
    private IStatusPanel statusPanel;
    
    // 患者来院情報 
    private PatientVisitModel pvt;
    
    // Read Only の時 true
    private boolean readOnly;
    
    // Chart のステート 
    private int chartState;
    
    // Chart内のドキュメントに共通の MEDIATOR 
    private ChartMediator mediator;
    
    // State Mgr
    private StateMgr stateMgr;
    
    // MML送信 listener
    private MmlMessageListener mmlListener;
    
    // CLAIM 送信 listener 
    private ClaimMessageListener claimListener;
    
    // このチャートの KarteBean
    private KarteBean karte;
    
    // GlassPane 
    private BlockGlass blockGlass;
    
    // Resource Map
    private ResourceMap resMap;
    
    // Logger
    private Logger logger;

    /**
     * Creates new ChartService
     */
    public ChartImpl() {
        logger = ClientContext.getBootLogger();
    }

    /**
     * このチャートのカルテを返す。
     * @return カルテ
     */
    public KarteBean getKarte() {
        return karte;
    }

    /**
     * このチャートのカルテを設定する。
     * @param karte このチャートのカルテ
     */
    public void setKarte(KarteBean karte) {
        this.karte = karte;
    }

    /**
     * Chart の JFrame を返す。
     * @return チャートウインドウno JFrame
     */
    public JFrame getFrame() {
        return windowSupport.getFrame();
    }

    /**
     * Chart内ドキュメントが共通に使用する Status パネルを返す。
     * @return IStatusPanel
     */
    public IStatusPanel getStatusPanel() {
        return statusPanel;
    }

    /**
     * Chart内ドキュメントが共通に使用する Status パネルを設定する。
     * @param statusPanel IStatusPanel
     */
    public void setStatusPanel(IStatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    /**
     * 来院情報を設定する。
     * @param pvt 来院情報
     */
    public void setPatientVisit(PatientVisitModel pvt) {
        this.pvt = pvt;
    }

    /**
     * 来院情報を返す。
     * @return 来院情報
     */
    public PatientVisitModel getPatientVisit() {
        return pvt;
    }

    /**
     * ReadOnly かどうかを返す。
     * @return ReadOnlyの時 true
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * ReadOnly 属性を設定する。
     * @param readOnly ReadOnly user の時 true
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * このチャートが対象としている患者モデルを返す。
     * @return チャートが対象としている患者モデル
     */
    public PatientModel getPatient() {
        return getKarte().getPatient();
    }

    /**
     * このチャートが対象としている患者モデルを設定する。
     * @param patientModel チャートが対象とする患者モデル
     */
    public void setPatientModel(PatientModel patientModel) {
        this.getKarte().setPatient(patientModel);
    }

    /**
     * チャートのステート属性を返す。
     * @return チャートのステート属性
     */
    public int getChartState() {
        return chartState;
    }

    /**
     * チャートのステートを設定する。
     * @param chartState チャートステート
     */
    public void setChartState(int chartState) {
        this.chartState = chartState;
        //
        // インスタンスを管理する static オブジェクト
        // を使用し束縛リスナへ通知する
        //
        ChartImpl.fireChanged(this);
    }

    /**
     * チャート内で共通に使用する Mediator を返す。
     * @return ChartMediator
     */
    public ChartMediator getChartMediator() {
        return mediator;
    }

    /**
     * チャート内で共通に使用する Mediator を設定する。
     * @param mediator ChartMediator
     */
    public void setChartMediator(ChartMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Menu アクションを制御する。
     */
    public void enabledAction(String name, boolean enabled) {
        Action action = mediator.getAction(name);
        if (action != null) {
            action.setEnabled(enabled);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * 文書ヒストリオブジェクトを返す。
     * @return 文書ヒストリオブジェクト DocumentHistory
     */
    public DocumentHistory getDocumentHistory() {
        return inspector.getDocumentHistory();
    }

    /**
     * 引数で指定されたタブ番号のドキュメントを表示する。
     * @param 表示するドキュメントのタブ番号
     */
    public void showDocument(int index) {
        int cnt = tabbedPane.getTabCount();
        if (index >= 0 && index <= cnt - 1 && index != tabbedPane.getSelectedIndex()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    /**
     * チャート内に未保存ドキュメントがあるかどうかを返す。
     * @return 未保存ドキュメントがある時 true
     */
    public boolean isDirty() {

        boolean dirty = false;

        if (providers != null && providers.size() > 0) {
            Collection<ChartDocument> docs = providers.values();
            for (ChartDocument doc : docs) {
                if (doc.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }
        return dirty;
    }

    @Override
    public void start() {

        // ResourceMap を保存する
        resMap = ClientContext.getResourceMap(ChartImpl.class);

        // パラメータをリソースから設定する
        String sama = resMap.getString("sama");
        int maxEstimation = resMap.getInteger("maxEstimation").intValue();
        int delay = resMap.getInteger("timerDelay").intValue();

        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();

        Task task = new Task<KarteBean, Void>(app) {

            @Override
            protected KarteBean doInBackground() throws Exception {
                logger.debug("CahrtImpl start task doInBackground");
                //
                // Database から患者のカルテを取得する
                //
                int past = Project.getPreferences().getInt(Project.DOC_HISTORY_PERIOD, -12);
                GregorianCalendar today = new GregorianCalendar();
                today.add(GregorianCalendar.MONTH, past);
                today.clear(Calendar.HOUR_OF_DAY);
                today.clear(Calendar.MINUTE);
                today.clear(Calendar.SECOND);
                today.clear(Calendar.MILLISECOND);
                DocumentDelegater ddl = new DocumentDelegater();
                KarteBean karteBean = ddl.getKarte(getPatientVisit().getPatient().getId(), today.getTime());
                return karteBean;
            }

            @Override
            protected void succeeded(KarteBean karteBean) {
                logger.debug("CahrtImpl start task succeeded");
                karteBean.setPatient(null);
                karteBean.setPatient(getPatientVisit().getPatient());
                setKarte(karteBean);
                initComponents();
                logger.debug("initComponents end");
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        getDocumentHistory().showHistory();
                    }
                });
            }

            @Override
            protected void cancelled() {
                logger.debug("Task cancelled");
            }

            @Override
            protected void failed(java.lang.Throwable cause) {
                logger.warn("Task failed");
                logger.warn(cause.getMessage());
            }

            @Override
            protected void interrupted(java.lang.InterruptedException e) {
                logger.warn("Task interrupted");
                logger.warn(e.getMessage());
            }
        };

        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = "カルテオープン";
        String patientName = getPatientVisit().getPatient().getFullName() + sama;
        String note = patientName + "を開いています...";
        Component c = null;
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, delay, maxEstimation);
        taskMonitor.addPropertyChangeListener(w);

        appCtx.getTaskService().execute(task);
    }

    /**
     * 患者のカルテを検索取得し、GUI を構築する。
     * このメソッドはバックグランドスレッドで実行される。
     */
    public void initComponents() {

        //
        // このチャート の Frame を生成し初期化する。
        // Frame のタイトルを
        // 患者氏名(カナ):患者ID に設定する
        //
        String Inspector = resMap.getString("inspector");
        StringBuilder sb = new StringBuilder();
        sb.append(getPatient().getFullName());
        sb.append("(");
        String kana = getPatient().getKanaName();
        kana = kana.replace("　", " ");
        sb.append(kana);
        sb.append(")");
        sb.append(" : ");
        sb.append(getPatient().getPatientId());
        sb.append(Inspector);

        // Frame と MenuBar を生成する
        windowSupport = WindowSupport.create(sb.toString());

        // チャート用のメニューバーを得る
        JMenuBar myMenuBar = windowSupport.getMenuBar();

        // チャートの JFrame オブジェクトを得る
        JFrame frame = windowSupport.getFrame();
        frame.setName("chartFrame");

        // 患者インスペクタを生成する
        inspector = new PatientInspector(this);
        inspector.getPanel().setBorder(BorderFactory.createEmptyBorder(7, 7, 5, 2)); // カット&トライ

        // Status パネルを生成する
        statusPanel = new StatusPanel();

        // Status パネルに表示する情報を生成する
        // カルテ登録日 Status パネルの右側に配置する
        String rdFormat = resMap.getString("rdFormat");         // yyyy-MM-dd
        String rdPrifix = resMap.getString("rdDatePrefix");     // カルテ登録日:
        String patienIdPrefix = resMap.getString("patientIdPrefix"); // 患者ID:
        Date date = getKarte().getCreated();
        SimpleDateFormat sdf = new SimpleDateFormat(rdFormat);
        String created = sdf.format(date);
        statusPanel.setRightInfo(rdPrifix + created);           // カルテ登録日:yyyy/mm/dd

        // 患者ID Status パネルの左に配置する
        statusPanel.setLeftInfo(patienIdPrefix + getKarte().getPatient().getPatientId()); // 患者ID:xxxxxx

        // ChartMediator を生成する
        mediator = new ChartMediator(this);

        // Menu を生成する
        AbstractMenuFactory appMenu = AbstractMenuFactory.getFactory();
        appMenu.setMenuSupports(getContext().getMenuSupport(), mediator);
        appMenu.build(myMenuBar);
        mediator.registerActions(appMenu.getActionMap());
        myToolPanel = appMenu.getToolPanelProduct();
        myToolPanel.add(inspector.getBasicInfoInspector().getPanel(), 0);

        //
        // このクラス固有のToolBarを生成する
        //
        JToolBar toolBar = new JToolBar();
        myToolPanel.add(toolBar);

        // テキストツールを生成する
        Action action = mediator.getActions().get(GUIConst.ACTION_INSERT_TEXT);
        JButton textBtn = new JButton();
        textBtn.setName("textBtn");
        textBtn.setAction(action);
        textBtn.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                mediator.addTextMenu(popup);
                if (!e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        toolBar.add(textBtn);

        // シェーマツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_INSERT_SCHEMA);
        JButton schemaBtn = new JButton();
        schemaBtn.setName("schemaBtn");
        schemaBtn.setAction(action);
        schemaBtn.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                getContext().showSchemaBox();
            }
        });
        toolBar.add(schemaBtn);

        // スタンプツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_INSERT_STAMP);
        JButton stampBtn = new JButton();
        stampBtn.setName("stampBtn");
        stampBtn.setAction(action);
        stampBtn.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                mediator.addStampMenu(popup);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        toolBar.add(stampBtn);

        // 保険選択ツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_SELECT_INSURANCE);
        JButton insBtn = new JButton();
        insBtn.setName("insBtn");
        insBtn.setAction(action);
        insBtn.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                PVTHealthInsuranceModel[] insurances = getHealthInsurances();
                for (PVTHealthInsuranceModel hm : insurances) {
                    ReflectActionListener ra = new ReflectActionListener(mediator,
                            "applyInsurance",
                            new Class[]{hm.getClass()},
                            new Object[]{hm});
                    JMenuItem mi = new JMenuItem(hm.toString());
                    mi.addActionListener(ra);
                    popup.add(mi);
                }

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        toolBar.add(insBtn);

        // Document プラグインのタブを生成する
        tabbedPane = loadDocuments();

        // 全体をレイアウトする
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inspector.getPanel(), tabbedPane);
        splitPane.setName("splitPane");

        JPanel myPanel = new JPanel();
        myPanel.setOpaque(true);
        myPanel.setLayout(new BorderLayout(0, 7));
        JPanel tmp = new JPanel(new BorderLayout());
        tmp.add(myToolPanel, BorderLayout.NORTH);
        tmp.add(splitPane, BorderLayout.CENTER);
        myPanel.add(tmp, BorderLayout.CENTER);
        myPanel.add((JPanel) statusPanel, BorderLayout.SOUTH);
        frame.setContentPane(myPanel);

        // Injection
        resMap.injectComponents(myPanel);

        // StateMgr を生成する
        stateMgr = new StateMgr();

        // BlockGlass を設定する
        blockGlass = new BlockGlass();
        frame.setGlassPane(blockGlass);

        // このチャートの Window にリスナを設定する
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // CloseBox の処理を行う
                processWindowClosing();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // Window がオープンされた時の処理を行う
                ChartImpl.windowOpened(ChartImpl.this);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // Window がクローズされた時の処理を行う
                ChartImpl.windowClosed(ChartImpl.this);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                //
                // 文書履歴へフォーカスする
                //
                getDocumentHistory().requestFocus();
            }
        });

        // Frame の大きさをストレージからロードする
        Rectangle bounds = null;
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        try {
            bounds = (Rectangle) appCtx.getLocalStorage().load("chartBounds.xml");

        } catch (IOException ex) {
            logger.warn(ex);
        }

        if (bounds == null) {
            int x = resMap.getInteger("frameX").intValue();
            int y = resMap.getInteger("frameY").intValue();
            int width = resMap.getInteger("frameWidth").intValue();
            int height = resMap.getInteger("frameHeight").intValue();
            bounds = new Rectangle(x, y, width, height);
        }
        
        // フレームの表示位置を決める J2SE 5.0
        boolean locByPlatform = Project.getPreferences().getBoolean(Project.LOCATION_BY_PLATFORM, false);

        if (locByPlatform) {
            frame.setLocationByPlatform(true);
            frame.setSize(bounds.width, bounds.height);

        } else {
            frame.setLocationByPlatform(false);
            frame.setBounds(bounds);
        }
        
        // MML 送信 Queue
        if (Project.getSendMML()) {
            mmlListener = (MmlMessageListener) getContext().getPlugin("sendMml");
        }

        // CLAIM 送信 Queue
        if (Project.getSendClaim()) {
            claimListener = (ClaimMessageListener) getContext().getPlugin("sendClaim");
        }

        getFrame().setVisible(true);
    }

    /**
     * MML送信リスナを返す。
     * @return MML送信リスナ
     */
    public MmlMessageListener getMMLListener() {
        return mmlListener;
    }

    /**
     * CLAIM送信リスナを返す。
     * @return CLAIM送信リスナ
     */
    public ClaimMessageListener getCLAIMListener() {
        return claimListener;
    }

    /**
     * メニューを制御する。
     */
    public void controlMenu() {
        stateMgr.controlMenu();
    }

    /**
     * ドキュメントタブを生成する。
     */
    private JTabbedPane loadDocuments() {

        // ドキュメントプラグインをロードする
        PluginLoader<ChartDocument> loader = PluginLoader.load(ChartDocument.class, ClientContext.getPluginClassLoader());
        Iterator<ChartDocument> iterator = loader.iterator();

        int index = 0;
        providers = new Hashtable<String, ChartDocument>();
        JTabbedPane tab = new JTabbedPane();

        while (iterator.hasNext()) {

            try {
                ChartDocument plugin = iterator.next();

                if (index == 0) {
                    plugin.setContext(this);
                    plugin.start();
                }

                tab.addTab(plugin.getTitle(), plugin.getUI());
                providers.put(String.valueOf(index), plugin);

                index += 1;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ゼロ番目を選択しておき changeListener を機能させる
        tab.setSelectedIndex(0);

        //
        // tab に プラグインを遅延生成するためのの ChangeListener を追加する
        //
        tab.addChangeListener((ChangeListener) EventHandler.create(ChangeListener.class, this, "tabChanged", ""));

        return tab;
    }

    /**
     * ドキュメントタブにプラグインを遅延生成し追加する。
     */
    public void tabChanged(ChangeEvent e) {

        //
        // 選択されたタブ番号に対応するプラグインをテーブルから検索する
        //
        int index = tabbedPane.getSelectedIndex();
        String key = String.valueOf(index);
        ChartDocument plugin = (ChartDocument) providers.get(key);

        if (plugin.getContext() == null) {
            //
            // まだ生成されていないプラグインを生成する
            //
            plugin.setContext(ChartImpl.this);
            plugin.start();
            tabbedPane.setComponentAt(index, plugin.getUI());

        } else {
            //
            // 既に生成済みプラグインの場合は enter() をコールする
            //
            plugin.enter();
        }
    }

    
//    public void newKarteOrg() {
//
//        logger.debug("entering newKarte");
//        String dept = getPatientVisit().getDeptNoTokenize();
//        String deptCode = getPatientVisit().getDepartmentCode();
//        String insuranceUid = getPatientVisit().getInsuranceUid();
//
//        // 新規ドキュメントのタイプと可能なオプションを設定する
//        String docType = null;
//        Chart.NewKarteOption option = null;
//        KarteViewer base = null;
//
//        ChartDocument bridgeOrViewer = (ChartDocument) providers.get("0");
//
//        if (bridgeOrViewer instanceof DocumentBridgeImpl) {
//            logger.debug("bridgeOrViewer instanceof DocumentBridgeImpl");
//            DocumentBridgeImpl bridge = (DocumentBridgeImpl) bridgeOrViewer;
//            base = bridge.getBaseKarte();
//
//        } else if (bridgeOrViewer instanceof KarteDocumentViewer) {
//            logger.debug("bridgeOrViewer instanceof KarteDocumentViewer");
//            KarteDocumentViewer viwer = (KarteDocumentViewer) bridgeOrViewer;
//            base = viwer.getBaseKarte();
//        } else {
//            return;
//        }
//
//        if (base != null) {
//            // 作成するドキュメントのタイプはベースに合わせる
//            logger.debug("base != null");
//            if (base.getDocType().equals(IInfoModel.DOCTYPE_KARTE)) {
//                logger.debug("base.getDocType().equals(IInfoModel.DOCTYPE_KARTE");
//                option = Chart.NewKarteOption.BROWSER_COPY_NEW;
//                docType = IInfoModel.DOCTYPE_KARTE;
//            } else {
//                logger.debug("base.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE");
//                option = Chart.NewKarteOption.BROWSER_COPY_NEW;
//                docType = IInfoModel.DOCTYPE_S_KARTE;
//            }
//
//        } else {
//            // ベースのカルテがない場合、２号カルテをデフォルトで選択する
//            logger.debug("base == null");
//
//            Preferences prefs = Project.getPreferences();
//            boolean b2 = prefs.getBoolean(Project.DOUBLE_KARTE, true);
//            if (b2) {
//                option = Chart.NewKarteOption.BROWSER_NEW;
//                docType = IInfoModel.DOCTYPE_KARTE;
//            } else {
//                option = Chart.NewKarteOption.BROWSER_NEW;
//                docType = IInfoModel.DOCTYPE_S_KARTE;
//            }
//        }
//
//        //
//        // 新規カルテ作成時に確認ダイアログを表示するかどうか
//        //
//        NewKarteParams params = null;
//        Preferences prefs = Project.getPreferences();
//
//        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
//
//            // 新規カルテダイアログへパラメータを渡し、コピー新規のオプションを制御する
//            logger.debug("show newKarteDialog");
//            params = getNewKarteParams(docType, option, null, dept, deptCode, insuranceUid);
//
//        } else {
//            // 保険、作成モード、配置方法を手動で設定する
//            params = new NewKarteParams(option);
//            params.setDocType(docType);
//            params.setDepartment(dept);
//            params.setDepartmentCode(deptCode);
//
//            //
//            // 保険
//            //
//            PVTHealthInsuranceModel[] ins = getHealthInsurances();
//            params.setPVTHealthInsurance(ins[0]);
//            if (insuranceUid != null) {
//                for (int i = 0; i < ins.length; i++) {
//                    if (ins[i].getGUID() != null) {
//                        if (insuranceUid.equals(ins[i].getGUID())) {
//                            params.setPVTHealthInsurance(ins[i]);
//                            break;
//                        }
//                    }
//                }
//            }
//
//            //
//            // 作成モード
//            //
//            switch (option) {
//
//                case BROWSER_NEW:
//                    params.setCreateMode(Chart.NewKarteMode.EMPTY_NEW);
//                    break;
//
//                case BROWSER_COPY_NEW:
//                    int cMode = prefs.getInt(Project.KARTE_CREATE_MODE, 0);
//                    if (cMode == 0) {
//                        params.setCreateMode(Chart.NewKarteMode.EMPTY_NEW);
//                    } else if (cMode == 1) {
//                        params.setCreateMode(Chart.NewKarteMode.APPLY_RP);
//                    } else if (cMode == 2) {
//                        params.setCreateMode(Chart.NewKarteMode.ALL_COPY);
//                    }
//                    break;
//            }
//
//            //
//            // 配置方法
//            //
//            params.setOpenFrame(prefs.getBoolean(Project.KARTE_PLACE_MODE, true));
//
//        }
//
//        // キャンセルした場合はリターンする
//        if (params == null) {
//            return;
//        }
//
//        logger.debug("returned newKarteDialog");
//        DocumentModel editModel = null;
//        KarteEditor editor = null;
//
//        // シングルドキュメントを生成する場合
//        if (params.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
//            logger.debug("シングルドキュメントを生成する");
//            // Baseになるカルテがあるかどうかでモデルの生成が異なる
//            if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
//                logger.debug("empty new is selected");
//                editModel = getKarteModelToEdit(params);
//            } else {
//                logger.debug("copy new is selected");
//                editModel = getKarteModelToEdit(base.getModel(), params);
//            }
//
//            editor = createEditor();
//            editor.setModel(editModel);
//            editor.setEditable(true);
//            editor.setMode(KarteEditor.SINGLE_MODE);
//
//        } else {
//            logger.debug("2号カルテを生成する");
//            // Baseになるカルテがあるかどうかでモデルの生成が異なる
//            if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
//                logger.debug("empty new is selected");
//                editModel = getKarteModelToEdit(params);
//            } else {
//                logger.debug("copy new is selected");
//                editModel = getKarteModelToEdit(base.getModel(), params);
//            }
//            editor = createEditor();
//            editor.setModel(editModel);
//            editor.setEditable(true);
//            editor.setMode(KarteEditor.DOUBLE_MODE);
//        }
//
//        if (params.isOpenFrame()) {
//            EditorFrame editorFrame = new EditorFrame();
//            editorFrame.setChart(this);
//            editorFrame.setKarteEditor(editor);
//            editorFrame.start();
//        } else {
//            editor.setContext(this);
//            editor.initialize();
//            editor.start();
//            this.addChartDocument(editor, params);
//        }
//    }
    
    /**
     * 新規カルテを作成する。
     */    
    public void newKarte() {

        logger.debug("newKarte()");
        String dept = getPatientVisit().getDeptNoTokenize();
        String deptCode = getPatientVisit().getDepartmentCode();
        String insuranceUid = getPatientVisit().getInsuranceUid();

        // 新規ドキュメントのタイプ=2号カルテと可能なオプションを設定する
        String docType = IInfoModel.DOCTYPE_KARTE;
        Chart.NewKarteOption option = null;
        KarteViewer base = null;

        ChartDocument bridgeOrViewer = (ChartDocument) providers.get("0");

        if (bridgeOrViewer instanceof DocumentBridgeImpl) {
            // Chart画面のタブパネル
            logger.debug("bridgeOrViewer instanceof DocumentBridgeImpl");
            DocumentBridgeImpl bridge = (DocumentBridgeImpl) bridgeOrViewer;
            base = bridge.getBaseKarte();

        } else if (bridgeOrViewer instanceof KarteDocumentViewer) {
            logger.debug("bridgeOrViewer instanceof KarteDocumentViewer");
            KarteDocumentViewer viwer = (KarteDocumentViewer) bridgeOrViewer;
            base = viwer.getBaseKarte();
        } else {
            return;
        }

        if (base != null) {
            logger.debug("base != null");
            if (base.getDocType().equals(IInfoModel.DOCTYPE_KARTE)) {
                logger.debug("base.getDocType().equals(IInfoModel.DOCTYPE_KARTE");
                option = Chart.NewKarteOption.BROWSER_COPY_NEW;
            } else {
                // ベースがあても２号カルテでない場合
                logger.debug("base.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE");
                option = Chart.NewKarteOption.BROWSER_NEW;
            }

        } else {
            // ベースのカルテがない場合
            logger.debug("base == null");
            option = Chart.NewKarteOption.BROWSER_NEW;
        }

        //
        // 新規カルテ作成時に確認ダイアログを表示するかどうか
        //
        NewKarteParams params = null;
        Preferences prefs = Project.getPreferences();

        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {

            // 新規カルテダイアログへパラメータを渡し、コピー新規のオプションを制御する
            logger.debug("show newKarteDialog");
            params = getNewKarteParams(docType, option, null, dept, deptCode, insuranceUid);

        } else {
            // 保険、作成モード、配置方法を手動で設定する
            params = new NewKarteParams(option);
            params.setDocType(docType);
            params.setDepartment(dept);
            params.setDepartmentCode(deptCode);

            // 保険
            PVTHealthInsuranceModel[] ins = getHealthInsurances();
            params.setPVTHealthInsurance(ins[0]);
            if (insuranceUid != null) {
                for (int i = 0; i < ins.length; i++) {
                    if (ins[i].getGUID() != null) {
                        if (insuranceUid.equals(ins[i].getGUID())) {
                            params.setPVTHealthInsurance(ins[i]);
                            break;
                        }
                    }
                }
            }

            // 作成モード
            switch (option) {

                case BROWSER_NEW:
                    params.setCreateMode(Chart.NewKarteMode.EMPTY_NEW);
                    break;

                case BROWSER_COPY_NEW:
                    int cMode = prefs.getInt(Project.KARTE_CREATE_MODE, 0);
                    if (cMode == 0) {
                        params.setCreateMode(Chart.NewKarteMode.EMPTY_NEW);
                    } else if (cMode == 1) {
                        params.setCreateMode(Chart.NewKarteMode.APPLY_RP);
                    } else if (cMode == 2) {
                        params.setCreateMode(Chart.NewKarteMode.ALL_COPY);
                    }
                    break;
            }

            // 配置方法
            params.setOpenFrame(prefs.getBoolean(Project.KARTE_PLACE_MODE, true));

        }

        // キャンセルした場合はリターンする
        if (params == null) {
            return;
        }

        logger.debug("returned newKarteDialog");
        DocumentModel editModel = null;
        KarteEditor editor = null;
        
        // Baseになるカルテがあるかどうかでモデルの生成が異なる
        if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
            logger.debug("empty new is selected");
            editModel = getKarteModelToEdit(params);
        } else {
            logger.debug("copy new is selected");
            editModel = getKarteModelToEdit(base.getModel(), params);
        }
        editor = createEditor();
        editor.setModel(editModel);
        editor.setEditable(true);
        editor.setMode(KarteEditor.DOUBLE_MODE);
       
        if (params.isOpenFrame()) {
            EditorFrame editorFrame = new EditorFrame();
            editorFrame.setChart(this);
            editorFrame.setKarteEditor(editor);
            editorFrame.start();
        } else {
            editor.setContext(this);
            editor.initialize();
            editor.start();
            this.addChartDocument(editor, params);
        }
    }

    /**
     * EmptyNew 新規カルテのモデルを生成する。
     * @param params 作成パラメータセット
     * @return 新規カルテのモデル
     */
    public DocumentModel getKarteModelToEdit(NewKarteParams params) {

        // カルテモデルを生成する
        DocumentModel model = new DocumentModel();

        //
        // DocInfoを設定する
        //
        DocInfoModel docInfo = model.getDocInfo();

        // docId 文書ID
        docInfo.setDocId(GUIDGenerator.generate(docInfo));

        // 生成目的
        docInfo.setPurpose(PURPOSE_RECORD);

        // DocumentType
        docInfo.setDocType(params.getDocType());

//        //
//        // 診療科を設定する
//        // 受付情報から得ている
//        //
//        String dept = params.getDepartment();
//        docInfo.setDepartmentDesc(dept); // department
//        
//        // 診療科コード
//        // 受付からとっていない場合....
//        String deptCode = params.getDepartmentCode();
//        if (deptCode == null) {
//            docInfo.setDepartment(MMLTable.getDepartmentCode(dept)); // dept.code
//        }
        docInfo.setDepartmentDesc(getPatientVisit().getDeptNoTokenize());
        docInfo.setDepartment(getPatientVisit().getDepartmentCode());


        // 健康保険を設定する
        PVTHealthInsuranceModel insurance = params.getPVTHealthInsurance();
        docInfo.setHealthInsurance(insurance.getInsuranceClassCode());
        docInfo.setHealthInsuranceDesc(insurance.toString());
        docInfo.setHealthInsuranceGUID(insurance.getGUID());

        // Versionを設定する
        VersionModel version = new VersionModel();
        version.initialize();
        docInfo.setVersionNumber(version.getVersionNumber());

        //
        // Document の Status を設定する
        // 新規カルテの場合は none
        //
        docInfo.setStatus(STATUS_NONE);

        return model;
    }

    /**
     * コピーして新規カルテを生成する場合のカルテモデルを生成する。
     * @param oldModel コピー元のカルテモデル
     * @param params 生成パラメータセット
     * @return 新規カルテのモデル
     */
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel, NewKarteParams params) {

        //
        // 新規モデルを作成し、表示されているモデルの内容をコピーする
        //
        DocumentModel newModel = new DocumentModel();
        boolean applyRp = params.getCreateMode() == Chart.NewKarteMode.APPLY_RP ? true : false;
        copyModel(oldModel, newModel, applyRp);

        //
        // 新規カルテの DocInfo を設定する
        //
        DocInfoModel docInfo = newModel.getDocInfo();

        // 文書ID
        docInfo.setDocId(GUIDGenerator.generate(docInfo));

        // 生成目的
        docInfo.setPurpose(PURPOSE_RECORD);

        // DocumentType
        docInfo.setDocType(params.getDocType());

        //
        // 診療科を設定する 受付情報から設定する
        //
        String dept = params.getDepartment();
        docInfo.setDepartmentDesc(dept);

//        // 診療科コード
//        // 受付からとっていない場合....
//        String deptCode = params.getDepartmentCode();
//        if (deptCode == null) {
//            docInfo.setDepartment(MMLTable.getDepartmentCode(dept)); // dept.code
//        }        
        docInfo.setDepartmentDesc(getPatientVisit().getDeptNoTokenize());
        docInfo.setDepartment(getPatientVisit().getDepartmentCode());

        // 健康保険を設定する
        PVTHealthInsuranceModel insurance = params.getPVTHealthInsurance();
        docInfo.setHealthInsurance(insurance.getInsuranceClassCode());
        //docInfo.setHealthInsuranceDesc(insurance.getInsuranceClass());
        docInfo.setHealthInsuranceDesc(insurance.toString());
        docInfo.setHealthInsuranceGUID(insurance.getGUID());

        // Versionを設定する
        VersionModel version = new VersionModel();
        version.initialize();
        docInfo.setVersionNumber(version.getVersionNumber());

        //
        // Document の Status を設定する
        // 新規カルテの場合は none
        //
        docInfo.setStatus(STATUS_NONE);

        return newModel;
    }

    /**
     * 修正の場合のカルテモデルを生成する。
     * @param oldModel 修正対象のカルテモデル
     * @return 新しい版のカルテモデル
     */
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel) {

        // 修正対象の DocInfo を取得する
        DocInfoModel oldDocInfo = oldModel.getDocInfo();

        // 新しい版のモデルにモジュールと画像をコピーする
        DocumentModel newModel = new DocumentModel();
        copyModel(oldModel, newModel, false);

        //
        // 新しい版の DocInfo を設定する
        //
        DocInfoModel newInfo = newModel.getDocInfo();

        // 文書ID
        newInfo.setDocId(GUIDGenerator.generate(newInfo));

        // 新しい版の firstConfirmDate = 元になる版の firstConfirmDate
        newInfo.setFirstConfirmDate(oldDocInfo.getFirstConfirmDate());

        // docType = old one
        newInfo.setDocType(oldDocInfo.getDocType());

        // purpose = old one
        newInfo.setPurpose(oldDocInfo.getPurpose());

        //
        // タイトルも引き継ぐ
        //
        newInfo.setTitle(oldDocInfo.getTitle());

        //
        // 診療科を設定する 
        // 元になる版の情報を利用する
        //
        newInfo.setDepartmentDesc(oldDocInfo.getDepartmentDesc());
        newInfo.setDepartment(oldDocInfo.getDepartment());

        //
        // 健康保険を設定する
        // 元になる版の情報を利用する
        // 
        newInfo.setHealthInsuranceDesc(oldDocInfo.getHealthInsuranceDesc());
        newInfo.setHealthInsurance(oldDocInfo.getHealthInsurance());
        newInfo.setHealthInsuranceGUID(oldDocInfo.getHealthInsuranceGUID());

        //
        // 親文書IDを設定する
        //
        newInfo.setParentId(oldDocInfo.getDocId());
        newInfo.setParentIdRelation(PARENT_OLD_EDITION);

        //
        // old PK を設定する
        //
        newInfo.setParentPk(oldModel.getId());

        //
        // Versionを設定する
        // new = old + 1.0
        VersionModel newVersion = new VersionModel();
        newVersion.setVersionNumber(oldDocInfo.getVersionNumber());
        newVersion.incrementNumber(); // version number ++
        newInfo.setVersionNumber(newVersion.getVersionNumber());

        //
        // Document Status を設定する
        // 元になる版の status (Final | Temporal | Modified)
        //
        newInfo.setStatus(oldDocInfo.getStatus());

        return newModel;
    }

    /**
     * カルテエディタを生成する。
     * @return カルテエディタ
     */
    public KarteEditor createEditor() {
        KarteEditor editor = null;
        try {
            editor = new KarteEditor();
            editor.addMMLListner(mmlListener);
            editor.addCLAIMListner(claimListener);
        } catch (Exception e) {
            logger.warn(e);
            editor = null;
        }
        return editor;
    }

    //
    // モデルをコピーする
    // ToDO 参照ではいけない
    // DocInfo の設定はない
    // 
    private void copyModel(DocumentModel oldModel, DocumentModel newModel, boolean applyRp) {

        //
        // 前回処方を適用する場合
        //
        if (applyRp) {
            Collection<ModuleModel> modules = oldModel.getModules();
            if (modules != null) {
                Collection<ModuleModel> apply = new ArrayList<ModuleModel>(5);
                for (ModuleModel bean : modules) {
                    IInfoModel model = bean.getModel();
                    if (model instanceof ClaimBundle) {
                        //
                        // 処方かどうかを判定する
                        //
                        if (((ClaimBundle) model).getClassCode().startsWith("2")) {
                            apply.add(bean);
                        }
                    }
                }

                if (apply.size() != 0) {
                    newModel.setModules(apply);
                }
            }

        } else {
            // 全てコピー
            newModel.setModules(oldModel.getModules());
            newModel.setSchema(oldModel.getSchema());
        }
    }

    /**
     * カルテ作成時にダアイログをオープンし、保険を選択させる。
     *
     * @return NewKarteParams
     */
    public NewKarteParams getNewKarteParams(String docType, Chart.NewKarteOption option, JFrame f, String dept, String deptCode, String insuranceUid) {

        NewKarteParams params = new NewKarteParams(option);
        params.setDocType(docType);
        params.setDepartment(dept);
        params.setDepartmentCode(deptCode);

        // 患者の健康保険コレクション
        Collection<PVTHealthInsuranceModel> insurances = pvt.getPatient().getPvtHealthInsurances();

        // コレクションが null の場合は自費保険を追加する
        if (insurances == null || insurances.size() == 0) {
            insurances = new ArrayList<PVTHealthInsuranceModel>(1);
            PVTHealthInsuranceModel model = new PVTHealthInsuranceModel();
            model.setInsuranceClass(INSURANCE_SELF);
            model.setInsuranceClassCode(INSURANCE_SELF_CODE);
            model.setInsuranceClassCodeSys(INSURANCE_SYS);
            insurances.add(model);
        }

        // 保険コレクションを配列に変換し、パラメータにセットする
        // ユーザがこの中の保険を選択する
        PVTHealthInsuranceModel[] insModels = (PVTHealthInsuranceModel[]) insurances.toArray(new PVTHealthInsuranceModel[insurances.size()]);
        params.setInsurances(insModels);
        int index = 0;
        if (insuranceUid != null) {
            for (int i = 0; i < insModels.length; i++) {
                if (insModels[i].getGUID() != null) {
                    if (insModels[i].getGUID().equals(insuranceUid)) {
                        index = i;
                        break;
                    }
                }
            }
        }
        params.setInitialSelectedInsurance(index);

        String text = option == Chart.NewKarteOption.BROWSER_MODIFY
                ? resMap.getString("modifyKarteTitle")
                : resMap.getString("newKarteTitle");

        text = ClientContext.getFrameTitle(text);

        // モーダルダイアログを表示する
        JFrame frame = f != null ? f : getFrame();
        NewKarteDialog od = new NewKarteDialog(frame, text);
        od.setValue(params);
        od.start();

        // 戻り値をリターンする
        params = (NewKarteParams) od.getValue();

        return params;
    }

    /**
     * 患者の健康保険を返す。
     * @return 患者の健康保険配列
     */
    public PVTHealthInsuranceModel[] getHealthInsurances() {

        // 患者の健康保険
        Collection<PVTHealthInsuranceModel> insurances = pvt.getPatient().getPvtHealthInsurances();

        if (insurances == null || insurances.size() == 0) {
            insurances = new ArrayList<PVTHealthInsuranceModel>(1);
            PVTHealthInsuranceModel model = new PVTHealthInsuranceModel();
            model.setInsuranceClass(INSURANCE_SELF);
            model.setInsuranceClassCode(INSURANCE_SELF_CODE);
            model.setInsuranceClassCodeSys(INSURANCE_SYS);
            insurances.add(model);
        }

        return (PVTHealthInsuranceModel[]) insurances.toArray(new PVTHealthInsuranceModel[insurances.size()]);
    }

    /**
     * タブにドキュメントを追加する。
     * @param doc 追加するドキュメント
     * @param params 追加するドキュメントの情報を保持する NewKarteParams
     */
    public void addChartDocument(ChartDocument doc, NewKarteParams params) {
        String title = null;
        if (params.getPVTHealthInsurance() != null) {
            title = getTabTitle(params.getDepartment(), params.getPVTHealthInsurance().getInsuranceClass());
        } else {
            title = getTabTitle(params.getDepartment(), null);
        }
        tabbedPane.addTab(title, doc.getUI());
        int index = tabbedPane.getTabCount() - 1;
        providers.put(String.valueOf(index), doc);
        tabbedPane.setSelectedIndex(index);
    }

    /**
     * タブにドキュメントを追加する。
     * @param title タブタイトル
     */
    public void addChartDocument(ChartDocument doc, String title) {
        tabbedPane.addTab(title, doc.getUI());
        int index = tabbedPane.getTabCount() - 1;
        providers.put(String.valueOf(index), doc);
        tabbedPane.setSelectedIndex(index);
    }

    /**
     * 新規カルテ用のタブタイトルを作成する
     * @param insurance 保険名
     * @return タブタイトル
     */
    public String getTabTitle(String dept, String insurance) {
        String[] depts = dept.split("\\s*,\\s*");
        StringBuilder buf = new StringBuilder();
        buf.append(resMap.getString("newKarteTabTitle"));
        if (insurance != null) {
            buf.append("(");
            buf.append(depts[0]);
            buf.append("・");
            buf.append(insurance);
            buf.append(")");
        }
        return buf.toString();
    }

    /**
     * 新規文書作成で選択されたプラグインを起動する。
     * 
     * @param pluginClass 起動するプラグインのクラス名
     */ 
    private void invokePlugin(String pluginClass) {

        try {
            NChartDocument doc = (NChartDocument) Class.forName(
                    pluginClass,
                    true,
                    ClientContext.getPluginClassLoader()).newInstance();
            
            if (doc instanceof KarteEditor) {
                String dept = getPatientVisit().getDeptNoTokenize();
                String deptCode = getPatientVisit().getDepartmentCode();
                String insuranceUid = getPatientVisit().getInsuranceUid();
                Chart.NewKarteOption option = Chart.NewKarteOption.BROWSER_NEW;
                String docType = IInfoModel.DOCTYPE_S_KARTE;
                NewKarteParams params = new NewKarteParams(option);
                params.setDocType(docType);
                params.setDepartment(dept);
                params.setDepartmentCode(deptCode);

                //
                // 保険
                //
                PVTHealthInsuranceModel[] ins = getHealthInsurances();
                params.setPVTHealthInsurance(ins[0]);
                if (insuranceUid != null) {
                    for (int i = 0; i < ins.length; i++) {
                        if (ins[i].getGUID() != null) {
                            if (insuranceUid.equals(ins[i].getGUID())) {
                                params.setPVTHealthInsurance(ins[i]);
                                break;
                            }
                        }
                    }
                }

                DocumentModel editModel = getKarteModelToEdit(params);
                KarteEditor editor = (KarteEditor) doc;
                editor.setModel(editModel);
                editor.setEditable(true);
                editor.setContext(this);
                editor.setMode(KarteEditor.SINGLE_MODE);
                editor.initialize();
                editor.start();
                this.addChartDocument(editor, params);
                
            } else {
                doc.setContext(this);
                doc.start();
                addChartDocument(doc, doc.getTitle());
            }

        } catch (Exception e) {
            logger.warn(e);
        }
    }

    /**
     * カルテ以外の文書を作成する。
     */
    public void newDocument() {

        // 拡張ポイント新規文書のプラグインをリストアップし、
        // リストで選択させる
        ArrayList<NameValuePair> documents = new ArrayList<NameValuePair>(3);
        PluginLister<NChartDocument> lister = PluginLister.list(NChartDocument.class, ClientContext.getPluginClassLoader());
        LinkedHashMap<String, String> nproviders = lister.getProviders();
        if (nproviders != null) {
            Iterator<String> iter = nproviders.keySet().iterator();
            while (iter.hasNext()) {
                String cmd = iter.next();
                String clsName = nproviders.get(cmd);
                NameValuePair pair = new NameValuePair(cmd, clsName);
                documents.add(pair);
                logger.debug(cmd + " = " + clsName);
            }
        }

        if (documents.size() == 0) {
            logger.debug("No plugins");
            return;
        }
        
        // docs アイコンをラベルへInjectionする
        JLabel newDocsLabel = new JLabel();
        newDocsLabel.setName("newDocsLabel");
        resMap.injectComponent(newDocsLabel);

        final JList docList = new JList(documents.toArray());
        docList.setCellRenderer(new StripeRenderer());
        
        JPanel panel = new JPanel(new BorderLayout(7, 0));
        panel.add(newDocsLabel,BorderLayout.WEST);
        panel.add(docList, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(6,6,5,5));
        JPanel content = new JPanel(new BorderLayout());
        content.add(panel, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createTitledBorder("作成する文書"));

        final JButton okButton = new JButton("了解");
        final JButton cancelButton = new JButton("取消し");
        Object[] options = new Object[]{okButton, cancelButton};

        JOptionPane jop = new JOptionPane(
                content,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                okButton);

        final JDialog dialog = jop.createDialog(getFrame(), ClientContext.getFrameTitle("新規文書作成"));
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                docList.requestFocusInWindow();
            }
        });

        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
                NameValuePair pair = (NameValuePair) docList.getSelectedValue();
                String clsName = pair.getValue();
                invokePlugin(clsName);
            }
        });
        okButton.setEnabled(false);

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        docList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int index = docList.getSelectedIndex();
                    if (index >= 0) {
                        okButton.setEnabled(true);
                    }
                }
            }
        });

        dialog.setVisible(true);
    }

    /**
     * 全てのドキュメントを保存する。
     * @param dirtyList 未保存ドキュメントのリスト
     */
    private void saveAll(java.util.List<UnsavedDocument> dirtyList) {

        if (dirtyList == null || dirtyList.size() == 0) {
            return;
        }

        try {
            for (UnsavedDocument undoc : dirtyList) {
                if (undoc.isNeedSave()) {
                    ChartDocument doc = (ChartDocument) providers.get(String.valueOf(undoc.getIndex()));
                    if (doc != null && doc.isDirty()) {
                        tabbedPane.setSelectedIndex(undoc.getIndex());
                        doc.save();
                    }
                }
            }

        } catch (Exception e) {
            logger.warn(e);
        }
    }

    /**
     * ドキュメントのなかにdirtyのものがあるかどうかを返す。
     * @return dirtyの時true
     */
    private java.util.List<UnsavedDocument> dirtyList() {
        java.util.List<UnsavedDocument> ret = null;
        int count = tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            ChartDocument doc = (ChartDocument) providers.get(String.valueOf(i));
            if (doc != null && doc.isDirty()) {
                if (ret == null) {
                    ret = new ArrayList<UnsavedDocument>(3);
                }
                ret.add(new UnsavedDocument(i, doc));
            }
        }
        return ret;
    }

    /**
     * CloseBox がクリックされた時の処理を行う。
     */
    public void processWindowClosing() {
        close();
    }

    /**
     * チャートウインドウを閉じる。
     */
    public void close() {

        //
        // 未保存ドキュメントがある場合はダイアログを表示し
        // 保存するかどうかを確認する
        //
        java.util.List<UnsavedDocument> dirtyList = dirtyList();

        if (dirtyList != null && dirtyList.size() > 0) {

            String saveAll = resMap.getString("unsavedtask.saveText");     // 保存;
            String discard = resMap.getString("unsavedtask.discardText");  // 破棄;
            String question = resMap.getString("unsavedtask.question");    // 未保存のドキュメントがあります。保存しますか ?
            String title = resMap.getString("unsavedtask.title");          // 未保存処理
            String cancelText = (String) UIManager.get("OptionPane.cancelButtonText");

            Object[] message = new Object[dirtyList.size() + 1];
            message[0] = (Object) question;
            int index = 1;
            for (UnsavedDocument doc : dirtyList) {
                message[index++] = doc.getCheckBox();
            }

            int option = JOptionPane.showOptionDialog(
                    getFrame(),
                    message,
                    ClientContext.getFrameTitle(title),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{saveAll, discard, cancelText},
                    saveAll);

            switch (option) {
                case 0:
                    // save
                    saveAll(dirtyList);
                    stop();
                    break;

                case 1:
                    // discard
                    stop();
                    break;

                case 2:
                    // cancel
                    break;
            }
        } else {
            stop();
        }
    }

    @Override
    public void stop() {
        if (providers != null) {
            for (Iterator<String> iter = providers.keySet().iterator(); iter.hasNext();) {
                ChartDocument doc = providers.get(iter.next());
                if (doc != null) {
                    doc.stop();
                }
            }
            providers.clear();
        }
        mediator.dispose();
        inspector.dispose();
        try {
            ClientContext.getLocalStorage().save(getFrame().getBounds(), "chartBounds.xml");

        } catch (IOException ex) {
            logger.warn(ex);
        }
        getFrame().setVisible(false);
        getFrame().setJMenuBar(null);
        getFrame().dispose();
    }

    protected abstract class ChartState {

        public ChartState() {
        }

        public abstract void controlMenu();
    }

    /**
     * ReadOnly ユーザの State クラス。
     */
    protected final class ReadOnlyState extends ChartState {

        public ReadOnlyState() {
        }

        /**
         * 新規カルテ作成及び修正メニューを disable にする。
         */
        public void controlMenu() {
            mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_MODIFY_KARTE).setEnabled(false);
        }
    }

    /**
     * 保険証がない場合の State クラス。
     */
    protected final class NoInsuranceState extends ChartState {

        public NoInsuranceState() {
        }

        public void controlMenu() {
            mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(false);
        }
    }

    /**
     * 通常の State クラス。
     */
    protected final class OrdinalyState extends ChartState {

        public OrdinalyState() {
        }

        public void controlMenu() {
            mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(true);
        }
    }

    /**
     * State Manager クラス。
     */
    protected final class StateMgr {

        private ChartState readOnlyState = new ReadOnlyState();
        private ChartState noInsuranceState = new NoInsuranceState();
        private ChartState ordinalyState = new OrdinalyState();
        private ChartState currentState;

        public StateMgr() {
            if (isReadOnly()) {
                enterReadOnlyState();
            } else {
                enterOrdinalyState();
            }
        }

        public void enterReadOnlyState() {
            currentState = readOnlyState;
            currentState.controlMenu();
        }

        public void enterNoInsuranceState() {
            currentState = noInsuranceState;
            currentState.controlMenu();
        }

        public void enterOrdinalyState() {
            currentState = ordinalyState;
            currentState.controlMenu();
        }

        public void controlMenu() {
            currentState.controlMenu();
        }
    }

    /**** Chart Instance を管理するための static クラス **/
    /**
     * オープンしている全インスタンスを保持するリストを返す。
     * @return オープンしている ChartPlugin のリスト
     */
    public static ArrayList<ChartImpl> getAllChart() {
        return allCharts;
    }

    /**
     * チャートステートの束縛リスナを登録する。
     * @param prop 束縛プロパティ名
     * @param l 束縛リスナ
     */
    public static void addPropertyChangeListener(String prop,
            PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }

    /**
     * チャートステートの束縛リスナを削除する。
     * @param prop 束縛プロパティ名
     * @param l 束縛リスナ
     */
    public static void removePropertyChangeListener(String prop,
            PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }

    /**
     * チャートウインドウのオープンを通知する。
     * @param opened オープンした ChartPlugin
     */
    public static void windowOpened(ChartImpl opened) {
        //
        // インスタンスを保持するリストへ追加する
        //
        allCharts.add(opened);

        //
        // PVT (Chart) の状態を設定する
        //
        PatientVisitModel model = opened.getPatientVisit();
        int oldState = model.getState();
        int newState = 0;

        switch (oldState) {

            case CLOSE_NONE:
                newState = OPEN_NONE;
                break;

            case CLOSE_SAVE:
                newState = OPEN_SAVE;
                break;

            default:
                throw new RuntimeException("Invalid Chart State");
        }

        //opened.getDocumentHistory().requestFocus();

        //
        // 通知する
        //
        model.setState(newState);
        boundSupport.firePropertyChange(ChartImpl.CHART_STATE, null, model);
    }

    /**
     * チャートウインドウのクローズを通知する。
     * @param closed クローズした ChartPlugin
     */
    public static void windowClosed(ChartImpl closed) {

        //
        // インスタンスリストから取り除く
        //
        if (allCharts.remove(closed)) {

            //
            // チャートの状態を PVT に設定する
            //
            PatientVisitModel model = closed.getPatientVisit();
            int oldState = model.getState();
            int newState = 0;

            switch (oldState) {

                case OPEN_NONE:
                    newState = CLOSE_NONE;
                    break;

                case OPEN_SAVE:
                    newState = CLOSE_SAVE;
                    break;

                default:
                    throw new RuntimeException("Invalid Chart State");

            }

            //
            // 通知する
            //
            model.setState(newState);
            boundSupport.firePropertyChange(ChartImpl.CHART_STATE, null, model);
            closed = null;
        }
    }

    /**
     * チャート状態の変化を通知する。
     * @param 変化のあった ChartPlugin
     */
    public static void fireChanged(ChartImpl changed) {
        PatientVisitModel model = changed.getPatientVisit();
        model.setState(changed.getChartState());
    }
}
