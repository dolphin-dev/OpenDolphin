package open.dolphin.client;

import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.PdfOfficeIconRenderer;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.helper.WindowSupport;
import open.dolphin.impl.genesys.GenesysLinkDocument;
import open.dolphin.infomodel.*;
import open.dolphin.plugin.PluginLister;
import open.dolphin.plugin.PluginLoader;
import open.dolphin.project.Project;
import open.dolphin.util.AgeCalculater;
import open.dolphin.util.GUIDGenerator;
import open.dolphin.util.MMLDate;
import org.apache.log4j.Level;

/**
 * 2号カルテ、傷病名、検査結果履歴等、患者の総合的データを提供するクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ChartImpl extends AbstractMainTool implements Chart, IInfoModel {
    
    private static final long DELAY = 10L;

    /** カルテ状態の束縛プロパティ名 */
    public static final String CHART_STATE = "chartStateProp";

    public static final int BIT_OPEN            = 0;
    public static final int BIT_SAVE_CLAIM      = 1;
    public static final int BIT_MODIFY_CLAIM    = 2;
    
    private static final String EXT_ODT_TEMPLATE = ".odt";

    //  Chart インスタンスを管理するstatic 変数
    //private static ArrayList<ChartImpl> allCharts = new ArrayList<ChartImpl>(3);
    // masuda 
    private static List<ChartImpl> allCharts = new CopyOnWriteArrayList<ChartImpl>();
    
    // Chart 状態の通知を行うための static 束縛サポート
    //private static PropertyChangeSupport boundSupport = new PropertyChangeSupport(new Object());

    private static final String PROP_FRMAE_BOUNDS = "chartFrame.bounds";
    
    // Document Plugin を格納する TabbedPane
    private JTabbedPane tabbedPane;
    
    // Active になっているDocument Plugin
    private HashMap<String, ChartDocument> providers;
    
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
    
    // PPane に Dropされた病名タンプ
    private List<ModuleInfoBean> droppedDiagnosis;
    
    // MML送信 listener
    private MmlMessageListener mmlListener;
    
    // CLAIM 送信 listener 
    private ClaimMessageListener claimListener;
    
    // このチャートの KarteBean
    private KarteBean karte;
    
    // GlassPane 
    private BlockGlass blockGlass;
    
    // Logger
    private boolean DEBUG=true;
    
    // タイマー
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> beeperHandle;
    private long statred;
    private long delay = DELAY;

    // task
    private int delayCount;
    private ProgressMonitor monitor;
    private Timer taskTimer;
    
//minagawa^ lsctest    
    private List<UnsavedDocument> dirtyList;
//minagawa$    
    
//s.oh^ カルテの画像連携
    private ScheduledExecutorService imageScheduler;
    private ImageWatcher imageWatcher;
    private ScheduledFuture<?> imageHandler;
    private boolean firstImageWatcher;
    private ArrayList karteEditorList;
//s.oh$

    /**
     * Creates new ChartService
     */
    public ChartImpl() {
        DEBUG = (ClientContext.getBootLogger().getLevel()==Level.DEBUG);
    }
    
    /**
     * オープンしている全インスタンスを保持するリストを返す。
     * @return オープンしている ChartPlugin のリスト
     */
    public static List<ChartImpl> getAllChart() {
        return allCharts;
    }

    /**
     * このチャートのカルテを返す。
     * @return カルテ
     */
    @Override
    public KarteBean getKarte() {
        return karte;
    }

    /**
     * このチャートのカルテを設定する。
     * @param karte このチャートのカルテ
     */
    @Override
    public void setKarte(KarteBean karte) {
        this.karte = karte;
    }

    /**
     * Chart の JFrame を返す。
     * @return チャートウインドウno JFrame
     */
    @Override
    public JFrame getFrame() {
        return windowSupport.getFrame();
    }

    /**
     * Chart内ドキュメントが共通に使用する Status パネルを返す。
     * @return IStatusPanel
     */
    @Override
    public IStatusPanel getStatusPanel() {
        return statusPanel;
    }

    /**
     * Chart内ドキュメントが共通に使用する Status パネルを設定する。
     * @param statusPanel IStatusPanel
     */
    @Override
    public void setStatusPanel(IStatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    /**
     * 来院情報を設定する。
     * @param pvt 来院情報
     */
    @Override
    public void setPatientVisit(PatientVisitModel pvt) {
        this.pvt = pvt;
    }

    /**
     * 来院情報を返す。
     * @return 来院情報
     */
    @Override
    public PatientVisitModel getPatientVisit() {
        return pvt;
    }

    /**
     * ReadOnly かどうかを返す。
     * @return ReadOnlyの時 true
     */
    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * ReadOnly 属性を設定する。
     * @param readOnly ReadOnly user の時 true
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * このチャートが対象としている患者モデルを返す。
     * @return チャートが対象としている患者モデル
     */
    @Override
    public PatientModel getPatient() {
        return getKarte().getPatientModel();
    }

    /**
     * チャートのステート属性を返す。
     * @return チャートのステート属性
     */
    @Override
    public int getChartState() {
        return chartState;
    }

    /**
     * チャートのステートを設定する。
     * @param chartState チャートステート
     */
    @Override
    public void setChartState(int chartState) {
    }

    /**
     * チャート内で共通に使用する Mediator を返す。
     * @return ChartMediator
     */
    @Override
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
    @Override
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
    @Override
    public DocumentHistory getDocumentHistory() {
        return inspector.getDocumentHistory();
    }

    /**
     * 引数で指定されたタブ番号のドキュメントを表示する。
     * @param 表示するドキュメントのタブ番号
     */
    @Override
    public void showDocument(int index) {
        int cnt = tabbedPane.getTabCount();
        if (index >= 0 && index <= cnt - 1 && index != tabbedPane.getSelectedIndex()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
    
    /**
     * Ppane にDropされた病名スタンプをリストに保存する。
     * @param dropped Ppane にDropされた病名スタンプ
     */
    @Override
    public void addDroppedDiagnosis(ModuleInfoBean dropped) {
        if (droppedDiagnosis==null) {
            droppedDiagnosis = new ArrayList<ModuleInfoBean>(2);
        }
        droppedDiagnosis.add(dropped);
        
        int index = tabbedPane.getSelectedIndex();
        String key = String.valueOf(index);
        ChartDocument plugin = (ChartDocument) providers.get(key);
        if (plugin.getContext() != null && plugin instanceof DiagnosisDocument) {
            ((DiagnosisDocument)plugin).addDroppedDiagnosis();
        }
    }
    
    /**
     * Ppane にDropされた病名スタンプをリストを返す。
     * @return 病名スタンプリスト
     */
    @Override
    public List<ModuleInfoBean> getDroppedDiagnosisList() {
        return droppedDiagnosis;
    }

    /**
     * チャート内に未保存ドキュメントがあるかどうかを返す。
     * @return 未保存ドキュメントがある時 true
     */
    @Override
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

        final SimpleWorker worker = new SimpleWorker<KarteBean, Void>() {

            @Override
            protected KarteBean doInBackground() throws Exception {
                if (DEBUG) {
                    ClientContext.getBootLogger().debug("CahrtImpl doInBackground did start");
                }
                // Database から患者のカルテを取得する
                int past = Project.getInt(Project.DOC_HISTORY_PERIOD, -12);
                GregorianCalendar today = new GregorianCalendar();
                today.add(GregorianCalendar.MONTH, past);
                today.clear(Calendar.HOUR_OF_DAY);
                today.clear(Calendar.MINUTE);
                today.clear(Calendar.SECOND);
                today.clear(Calendar.MILLISECOND);
                DocumentDelegater ddl = new DocumentDelegater();
                KarteBean karteBean = ddl.getKarte(getPatientVisit().getPatientModel().getId(), today.getTime());
                return karteBean;
            }

            @Override
            protected void succeeded(KarteBean karteBean) {
                if (DEBUG) {
                    ClientContext.getBootLogger().debug("CahrtImpl succeeded");
                }
                //-------------------------------------------------------------
                karteBean.setPatientModel(null);
                karteBean.setPatientModel(getPatientVisit().getPatientModel());
                setKarte(karteBean);
                //-------------------------------------------------------------
                initComponents();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDocumentHistory().showHistory();
                    }
                });
            }

            @Override
            protected void cancelled() {
                ClientContext.getBootLogger().debug("Task cancelled");
            }

            @Override
            protected void failed(java.lang.Throwable cause) {
                ClientContext.getBootLogger().warn("Task failed");
                ClientContext.getBootLogger().warn(cause.getMessage());
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
        Component c = null;
        String message = "カルテオープン";
        StringBuilder sb = new StringBuilder();
        sb.append(getPatientVisit().getPatientModel().getFullName()).append(resource.getString("sama"));
        sb.append("を開いています...");
        String note = sb.toString();
        int maxEstimation = Integer.parseInt(resource.getString("maxEstimation"));
        int dl = Integer.parseInt(resource.getString("timerDelay"));

        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / dl);

        taskTimer = new Timer(dl, new ActionListener() {

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
        
//s.oh^ カルテの画像連携
        Long imageDelay = Long.parseLong(Project.getString("karte.imagelink.watching.delay", "500"));
        sb = new StringBuilder();
        String dir = Project.getString("karte.imagelink.dir");
        if(dir != null && dir.length() > 0) {
            firstImageWatcher = true;
            sb.append(dir).append(File.separator).append(pvt.getPatientId());
            imageWatcher = new ImageWatcher(new File(sb.toString()));
            imageScheduler = Executors.newSingleThreadScheduledExecutor();
            imageHandler = imageScheduler.scheduleWithFixedDelay(imageWatcher, imageDelay, imageDelay, TimeUnit.MILLISECONDS);
            karteEditorList = new ArrayList();
        }
//s.oh$

        worker.execute();
    }

    /**
     * 患者のカルテを検索取得し、GUI を構築する。
     * このメソッドはバックグランドスレッドで実行される。
     */
    public void initComponents() {

        ResourceBundle resource = ClientContext.getBundle(this.getClass());

        //---------------------------------------------
        // このチャート の Frame を生成し初期化する。
        // Frame のタイトルを
        // 患者氏名(カナ):患者ID に設定する
        //---------------------------------------------
        String Inspector = resource.getString("inspector");
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

        // ChartMediator を生成する
        mediator = new ChartMediator(this);

        // 患者インスペクタを生成する
        inspector = new PatientInspector(this);
        inspector.getPanel().setBorder(BorderFactory.createEmptyBorder(7, 7, 5, 2)); // カット&トライ

        // Status パネルを生成する
        statusPanel = new StatusPanel();

        // Status パネルに表示する情報を生成する
        // カルテ登録日 Status パネルの右側に配置する
        String rdFormat = resource.getString("rdFormat");             // yyyy-MM-dd
        String rdPrifix = resource.getString("rdDatePrefix");         // カルテ登録日:
        String patienIdPrefix = resource.getString("patientIdPrefix"); // 患者ID:
        Date date = getKarte().getCreated();
        SimpleDateFormat sdf = new SimpleDateFormat(rdFormat);
        String created = sdf.format(date);
        statusPanel.setRightInfo(rdPrifix + created);           // カルテ登録日:yyyy/mm/dd
        
//masuda^最終受診日をstatus panelに表示する
        // 患者ID Status パネルの左に配置する
        //statusPanel.setLeftInfo(patienIdPrefix + getKarte().getPatientModel().getPatientId()); // 患者ID:xxxxxx
        // 最終受診日を取得する
        final SimpleDateFormat frmt = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        Date lastDocDate = getKarte().getLastDocDate();
        sb = new StringBuilder();
        sb.append(patienIdPrefix);
        sb.append(getKarte().getPatient().getPatientId());
        if (lastDocDate != null) {
            sb.append(" 最終受診日:");
            sb.append(frmt.format(lastDocDate));
        } else {
            sb.append(" 新患");
        }
        statusPanel.setLeftInfo(sb.toString());
//masuda$        

        //-------------------------------------------------------------
        // Menu を生成する
        //-------------------------------------------------------------
        AbstractMenuFactory appMenu = AbstractMenuFactory.getFactory();
        appMenu.setMenuSupports(getContext().getMenuSupport(), mediator);
        appMenu.build(myMenuBar);
        mediator.registerActions(appMenu.getActionMap());
        myToolPanel = appMenu.getToolPanelProduct();
        myToolPanel.add(inspector.getBasicInfoInspector().getPanel(), 0);
        
        // adminとそれ以外
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

        //---------------------------------
        // このクラス固有のToolBarを生成する
        //---------------------------------
        JToolBar toolBar = appMenu.getToolBar();
        //myToolPanel.add(toolBar);
        toolBar.addSeparator();
        
        // テキストツールを生成する
        Action action = mediator.getActions().get(GUIConst.ACTION_INSERT_TEXT);
        final JToggleButton textBtn = new JToggleButton();
        textBtn.setName("textBtn");
        textBtn.setAction(action);
        textBtn.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    if (mediator.getActions().get(GUIConst.ACTION_INSERT_TEXT).isEnabled()) {
                        JPopupMenu menu = new JPopupMenu();
                        mediator.addTextMenu(menu);
                        
                        menu.addPopupMenuListener(new PopupMenuListener() {
                            @Override
                            public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
                            }
                            @Override
                            public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
                                textBtn.setSelected(false);
                            }
                            @Override
                            public void popupMenuCanceled(PopupMenuEvent pme) {
                                textBtn.setSelected(false);
                            }
                        });
                        Component c = (Component)ie.getSource();
                        menu.show(c, 0, c.getHeight());
                    }
                }
            }
        });
        textBtn.setFocusable(false);
        textBtn.setBorderPainted(false);
        textBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(textBtn);

        // シェーマツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_INSERT_SCHEMA);
        final JToggleButton schemaBtn = new JToggleButton();
        schemaBtn.setName("schemaBtn");
        schemaBtn.setAction(action);
        schemaBtn.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    if (mediator.getActions().get(GUIConst.ACTION_INSERT_SCHEMA).isEnabled()) {
                        getContext().showSchemaBox();
                    }
                    schemaBtn.setSelected(false);
                }
            }
        });
        schemaBtn.setFocusable(false);
        schemaBtn.setBorderPainted(false);
        schemaBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(schemaBtn);

        // スタンプツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_INSERT_STAMP);
        final JToggleButton stampBtn = new JToggleButton();
        stampBtn.setName("stampBtn");
        stampBtn.setAction(action);
        stampBtn.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    if (mediator.getActions().get(GUIConst.ACTION_INSERT_STAMP).isEnabled()) {
                        JPopupMenu menu = new JPopupMenu();
                        mediator.addStampMenu(menu);
                        
                        menu.addPopupMenuListener(new PopupMenuListener() {
                            @Override
                            public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
                            }
                            @Override
                            public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
                                stampBtn.setSelected(false);
                            }
                            @Override
                            public void popupMenuCanceled(PopupMenuEvent pme) {
                                stampBtn.setSelected(false);
                            }
                        });
                        
                        Component c = (Component)ie.getSource();
                        menu.show(c, 0, c.getHeight());
                    }
                }
            }
        });
        stampBtn.setFocusable(false);
        stampBtn.setBorderPainted(true);
        stampBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(stampBtn);

//        //-----------------------------------------------------------------
//        // 処方日数を一括変更する
//        //-----------------------------------------------------------------
//        action = mediator.getActions().get(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL);
//        JButton chgBtn = new JButton();
//        chgBtn.setName("chgBtn");
//        chgBtn.setAction(action);
//        toolBar.add(chgBtn);

        //-------------------------------------------------------------
        // 保険選択ツールを生成する
        // 保険の切り替え（変更）で karteEditorの applyInsurance が起動される
        //-------------------------------------------------------------
        action = mediator.getActions().get(GUIConst.ACTION_SELECT_INSURANCE);
        final JToggleButton insBtn = new JToggleButton();
        insBtn.setName("insBtn");
        insBtn.setAction(action);
        insBtn.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    if (mediator.getActions().get(GUIConst.ACTION_SELECT_INSURANCE).isEnabled()) {
                        JPopupMenu menu = new JPopupMenu();
                        PVTHealthInsuranceModel[] insurances = getHealthInsurances();
                        for (PVTHealthInsuranceModel hm : insurances) {
                            ReflectActionListener ra = new ReflectActionListener(mediator,
                                    "applyInsurance",
                                    new Class[]{hm.getClass()},
                                    new Object[]{hm});
                            JMenuItem mi = new JMenuItem(hm.toString());
                            mi.addActionListener(ra);
                            menu.add(mi);
                        }
                        
                        menu.addPopupMenuListener(new PopupMenuListener() {
                            @Override
                            public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
                            }
                            @Override
                            public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
                                insBtn.setSelected(false);
                            }
                            @Override
                            public void popupMenuCanceled(PopupMenuEvent pme) {
                                insBtn.setSelected(false);
                            }
                        });
                        
                        Component c = (Component)ie.getSource();
                        menu.show(c, 0, c.getHeight());
                    }
                }
            }
        });
        insBtn.setFocusable(false);
        insBtn.setBorderPainted(true);
        insBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(insBtn);

        // Document プラグインのタブを生成する
        tabbedPane = loadDocuments();

        // 全体をレイアウトする
        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inspector.getPanel(), tabbedPane);
        //splitPane.setName("splitPane");

        inspector.getPanel().setPreferredSize(new Dimension(280, 620));
        JPanel tmp = new JPanel(new BorderLayout());
        tmp.add(myToolPanel, BorderLayout.NORTH);
        tmp.add(inspector.getPanel(), BorderLayout.WEST);
        tmp.add(tabbedPane, BorderLayout.CENTER);

        JPanel myPanel = new JPanel();
        myPanel.setOpaque(true);
        myPanel.setLayout(new BorderLayout(5, 7));

        myPanel.add(tmp, BorderLayout.CENTER);
        myPanel.add((JPanel) statusPanel, BorderLayout.SOUTH);
        frame.setContentPane(myPanel);

        // Injection
//minagawa^ Icon Server        
        //textBtn.setIcon(ClientContext.getImageIcon(resource.getString("textBtn.icon")));
        textBtn.setIcon(ClientContext.getImageIconArias("icon_text_stap_menu"));
        textBtn.setText(null);
        textBtn.setToolTipText(resource.getString("textBtn.toolTipText"));
        //textBtn.setMargin(new Insets(5,5,5,5));

        //schemaBtn.setIcon(ClientContext.getImageIcon(resource.getString("schemaBtn.icon")));
        schemaBtn.setIcon(ClientContext.getImageIconArias("icon_open_schema_box"));
        schemaBtn.setText(null);
        schemaBtn.setToolTipText(resource.getString("schemaBtn.toolTipText"));
        //schemaBtn.setMargin(new Insets(5,5,5,5));

        //stampBtn.setIcon(ClientContext.getImageIcon(resource.getString("stampBtn.icon")));
        stampBtn.setIcon(ClientContext.getImageIconArias("icon_stamp_menu"));
        stampBtn.setText(null);
        stampBtn.setToolTipText(resource.getString("stampBtn.toolTipText"));
        //stampBtn.setMargin(new Insets(5,5,5,5));

//        chgBtn.setIcon(ClientContext.getImageIcon(resource.getString("chgBtn.icon")));
//        chgBtn.setText(null);
//        chgBtn.setToolTipText(resource.getString("chgBtn.toolTipText"));
//        chgBtn.setMargin(new Insets(5,5,5,5));

        //insBtn.setIcon(ClientContext.getImageIcon(resource.getString("insBtn.icon")));
        insBtn.setIcon(ClientContext.getImageIconArias("icon_health_insurance"));
//minagawa$         
        insBtn.setText(null);
        insBtn.setToolTipText(resource.getString("insBtn.toolTipText"));
        //insBtn.setMargin(new Insets(5,5,5,5));

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
                if (!blockGlass.isVisible()) {
                    processWindowClosing();
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // リストへ追加する
                allCharts.add(ChartImpl.this);
                
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // リストから削除し状態変化を通知する
                if (allCharts.remove(ChartImpl.this)) {
                    ChartEventHandler scl = ChartEventHandler.getInstance();
                    scl.publishKarteClosed(ChartImpl.this.getPatientVisit());
                }
            }

            @Override
            public void windowActivated(WindowEvent e) {
                // 文書履歴へフォーカスする
                getDocumentHistory().requestFocus();
            }
        });

        // Frame の大きさをストレージからロードする
        // デフォルト値を用意して userDefaults から読み込む
        int x = Integer.parseInt(resource.getString("frameX"));
        int y = Integer.parseInt(resource.getString("frameY"));
        int width = Integer.parseInt(resource.getString("frameWidth"));
        int height = Integer.parseInt(resource.getString("frameHeight"));
        Rectangle defRect = new Rectangle(x, y, width, height);
        Rectangle bounds = Project.getRectangle(PROP_FRMAE_BOUNDS, defRect);

        // フレームの表示位置を決める J2SE 5.0
        boolean locByPlatform = Project.getBoolean(Project.LOCATION_BY_PLATFORM);

        if (locByPlatform) {
            frame.setLocationByPlatform(true);
            frame.setSize(bounds.width, bounds.height);

        } else {
            frame.setLocationByPlatform(false);
            frame.setBounds(bounds);
        }
        
        // MML 送信 Queue
        if (Project.getBoolean(Project.SEND_MML)) {
            mmlListener = (MmlMessageListener)getContext().getPlugin("sendMml");
        }

        // CLAIM 送信 Queue
        // 2012-07 claimSenderIsClientかつisSendClaim()=true の時のみ必要
        if (Project.claimSenderIsClient() && isSendClaim()) {
            claimListener = (ClaimMessageListener)getContext().getPlugin("sendClaim");
        }

        getFrame().setVisible(true);
        
        // timer 開始
        statred = System.currentTimeMillis();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        final Runnable beeper = new Runnable() {

            @Override
            public void run() {
                long time = System.currentTimeMillis() - statred;
                time = time / 1000L;
                statusPanel.setTimeInfo(time);
            }
        };
        beeperHandle = scheduler.scheduleAtFixedRate(beeper, delay, delay, TimeUnit.SECONDS);
    }

    /**
     * MML送信リスナを返す。
     * @return MML送信リスナ
     */
    @Override
    public MmlMessageListener getMMLListener() {
        return mmlListener;
    }

    /**
     * CLAIM送信リスナを返す。
     * @return CLAIM送信リスナ
     */
    @Override
    public ClaimMessageListener getCLAIMListener() {
        return claimListener;
    }

    @Override
    public boolean isSendClaim() {
        // Server-ORCA, 評価で Client-ORA 等を考慮^
        boolean send = true;
        send = send && (!isReadOnly());                             // ReadOnlyではない
        send = send && Project.getBoolean(Project.SEND_CLAIM);      // CLAIM送信になっている
        send = send && Project.canAccessToOrca();                   // ORCAにアクセス出来る
        return send;
        // Server-ORCA, 評価で Client-ORA 等を考慮$
    }

    @Override
    public boolean isSendLabtest() {
        boolean send = true;
        send = send && (!isReadOnly());
        send = send && Project.getBoolean(Project.SEND_LABTEST);
        return send;
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
        PluginLoader<ChartDocument> loader = PluginLoader.load(ChartDocument.class);
        Iterator<ChartDocument> iterator = loader.iterator();

        int index = 0;
        providers = new HashMap<String, ChartDocument>();
        JTabbedPane tab = new JTabbedPane();

        while (iterator.hasNext()) {

            try {
                ChartDocument plugin = iterator.next();
                
                if (index == 0) {
                    plugin.setContext(this);
                    plugin.start();
                }

                tab.addTab(plugin.getTitle(), plugin.getIconInfo(this), plugin.getUI());
                providers.put(String.valueOf(index), plugin);

                index += 1;

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        
//s.oh^ ジェネシス連携
        String name = Project.getString("genesys.browser.title");
        if(name != null && name.length() > 0) {
            ChartDocument plugin = new GenesysLinkDocument();
            tab.addTab(plugin.getTitle(), plugin.getIconInfo(this), plugin.getUI());
            providers.put(String.valueOf(index), plugin);
            index += 1;
        }
//s.oh$
        
        if(Project.getString(GenesysLinkDocument.KEY_GENESYSBROWSER, "").toLowerCase().equals(GenesysLinkDocument.VAL_GENESYS)) {
            ChartDocument plugin = new GenesysLinkDocument();
            tab.addTab(plugin.getTitle(), plugin.getIconInfo(this), plugin.getUI());
            providers.put(String.valueOf(index), plugin);
            index += 1;
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
        
        if (plugin==null) {
            return;
        }

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
    
    /**
     * 新規カルテを作成する。
     */    
    public void newKarte() {

        if (DEBUG) {
            ClientContext.getBootLogger().debug("newKarte did enter");
        }
        String deptName = getPatientVisit().getDeptName();
        String deptCode = getPatientVisit().getDeptCode();
        String insuranceUid = getPatientVisit().getInsuranceUid();

        // 新規ドキュメントのタイプ=2号カルテと可能なオプションを設定する
        String docType = IInfoModel.DOCTYPE_KARTE;
        Chart.NewKarteOption option;
        KarteViewer base;

        ChartDocument bridgeOrViewer = (ChartDocument) providers.get("0");

        if (bridgeOrViewer instanceof DocumentBridgeImpl) {
            // Chart画面のタブパネル
            if (DEBUG) {
                ClientContext.getBootLogger().debug("bridgeOrViewer instanceof DocumentBridgeImpl");
            }
            DocumentBridgeImpl bridge = (DocumentBridgeImpl) bridgeOrViewer;
            base = bridge.getBaseKarte();

        } else if (bridgeOrViewer instanceof KarteDocumentViewer) {
            if (DEBUG) {
                ClientContext.getBootLogger().debug("bridgeOrViewer instanceof KarteDocumentViewer");
            }
            KarteDocumentViewer viwer = (KarteDocumentViewer) bridgeOrViewer;
            base = viwer.getBaseKarte();
        } else {
            return;
        }

        if (base != null) {
            if (DEBUG) {
                ClientContext.getBootLogger().debug("base != null");
            }
            if (base.getDocType().equals(IInfoModel.DOCTYPE_KARTE)) {
                if (DEBUG) {
                    ClientContext.getBootLogger().debug("base.getDocType().equals(IInfoModel.DOCTYPE_KARTE");
                }
                option = Chart.NewKarteOption.BROWSER_COPY_NEW;
            } else {
                // ベースがあても２号カルテでない場合
                if (DEBUG) {
                    ClientContext.getBootLogger().debug("base.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE");
                }
                option = Chart.NewKarteOption.BROWSER_NEW;
            }

        } else {
            // ベースのカルテがない場合
            if (DEBUG) {
                ClientContext.getBootLogger().debug("base == null");
            }
            option = Chart.NewKarteOption.BROWSER_NEW;
        }

        //
        // 新規カルテ作成時に確認ダイアログを表示するかどうか
        //
        NewKarteParams params;

        if (Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {

            // 新規カルテダイアログへパラメータを渡し、コピー新規のオプションを制御する
            if (DEBUG) {
                ClientContext.getBootLogger().debug("show newKarteDialog");
            }
            params = getNewKarteParams(docType, option, null, deptName, deptCode, insuranceUid);

        } else {
            // 保険、作成モード、配置方法を手動で設定する
            params = new NewKarteParams(option);
            params.setDocType(docType);
            params.setDepartmentName(deptName);
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
                    int cMode = Project.getInt(Project.KARTE_CREATE_MODE, 0);
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
            params.setOpenFrame(Project.getBoolean(Project.KARTE_PLACE_MODE, true));

        }

        // キャンセルした場合はリターンする
        if (params == null) {
            return;
        }

        if (DEBUG) {
            ClientContext.getBootLogger().debug("returned newKarteDialog");
        }
        DocumentModel editModel;
        KarteEditor editor;

        //--------------------------------------------
        // Baseになるカルテがあるかどうかでモデルの生成が異なる
        //--------------------------------------------
        if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
            if (DEBUG) {
                ClientContext.getBootLogger().debug("empty new is selected");
            }
            editModel = getKarteModelToEdit(params);
        } else {
            if (DEBUG) {
                ClientContext.getBootLogger().debug("copy new is selected");
            }
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
        
//s.oh^ カルテの画像連携
        karteEditorList.add(editor);
//s.oh$
    }

    /**
     * EmptyNew 新規カルテのモデルを生成する。
     * @param params 作成パラメータセット
     * @return 新規カルテのモデル
     */
    @Override
    public DocumentModel getKarteModelToEdit(NewKarteParams params) {

        // カルテモデルを生成する
        DocumentModel model = new DocumentModel();

        //--------------------------
        // DocInfoを設定する
        //--------------------------
        DocInfoModel docInfo = model.getDocInfoModel();

        // docId 文書ID
        docInfo.setDocId(GUIDGenerator.generate(docInfo));

        // 生成目的
        docInfo.setPurpose(PURPOSE_RECORD);

        // DocumentType
        docInfo.setDocType(params.getDocType());

        //-------------------------------------------------------------------
        // 2.0
        // 1. UserModel に ORCAID が設定してあればそれを使用する
        // 2. なければ、受付情報から deptCode,deptName,doctorId,doctorName,JMARI
        //    を取得している。docInfo の departmentDesc にこれらの情報をカンマで連結する。
        // 3.
        //-------------------------------------------------------------------
        StringBuilder sb = new StringBuilder();
        sb.append(getPatientVisit().getDeptName()).append(",");             // 診療科名
        sb.append(getPatientVisit().getDeptCode()).append(",");             // 診療科コード : 受けと不一致、受信？
        sb.append(Project.getUserModel().getCommonName()).append(",");      // 担当医名
        if (Project.getUserModel().getOrcaId()!=null) {
            sb.append(Project.getUserModel().getOrcaId()).append(",");      // 担当医コード: ORCA ID がある場合
        } else if (getPatientVisit().getDoctorId()!=null) {
            sb.append(getPatientVisit().getDoctorId()).append(",");         // 担当医コード: 受付でIDがある場合
        } else {
            sb.append(Project.getUserModel().getUserId()).append(",");      // 担当医コード: ログインユーザーID
        }
        sb.append(getPatientVisit().getJmariNumber());                      // JMARI
        docInfo.setDepartmentDesc(sb.toString());                           // 上記をカンマ区切りで docInfo.departmentDesc へ設定
        docInfo.setDepartment(getPatientVisit().getDeptCode());             // 診療科コード 01 内科等
        
        //-------------------------------------------------------------------
        // 2012-05 クレーム送信をJMS+MDB化するために、新たに施設名と医療資格が必要
        //-------------------------------------------------------------------
        docInfo.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        docInfo.setCreaterLisence(Project.getUserModel().getLicenseModel().getLicense());

        //-----------------------------------------------------------
        // 健康保険を設定する-新規カルテダイアログで選択された保険をセットしている
        //-----------------------------------------------------------
        PVTHealthInsuranceModel insurance = params.getPVTHealthInsurance(); // 選択された保険
        docInfo.setHealthInsurance(insurance.getInsuranceClassCode());      // classCode
        docInfo.setHealthInsuranceDesc(insurance.toString());               // 説明
        // 受付時に選択した保険のUIDはPatientVisitModelの insuranceUidに設定されている
        // これと異なる保険が選択される事もある (i.ie insuranceUid!=selectedInsurance.guid)
        docInfo.setHealthInsuranceGUID(insurance.getGUID());                // UUID

        // Versionを設定する
        VersionModel version = new VersionModel();
        version.initialize();
        docInfo.setVersionNumber(version.getVersionNumber());

        //---------------------------
        // Document の Status を設定する
        // 新規カルテの場合は none
        //---------------------------
        docInfo.setStatus(STATUS_NONE);

        return model;
    }

    /**
     * コピーして新規カルテを生成する場合のカルテモデルを生成する。
     * @param oldModel コピー元のカルテモデル
     * @param params 生成パラメータセット
     * @return 新規カルテのモデル
     */
    @Override
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel, NewKarteParams params) {

        //-------------------------------------------------
        // 新規モデルを作成し、表示されているモデルの内容をコピーする
        //-------------------------------------------------
        DocumentModel newModel = new DocumentModel();
        boolean applyRp = params.getCreateMode() == Chart.NewKarteMode.APPLY_RP ? true : false;
        copyModel(oldModel, newModel, applyRp);

        //-------------------------------------------------
        // 新規カルテの DocInfo を設定する
        //-------------------------------------------------
        DocInfoModel docInfo = newModel.getDocInfoModel();

        // 文書ID
        docInfo.setDocId(GUIDGenerator.generate(docInfo));

        // 生成目的
        docInfo.setPurpose(PURPOSE_RECORD);

        // DocumentType
        docInfo.setDocType(params.getDocType());

        //---------------------------
        // 2.0
        // 受付情報から deptCode,deptName,doctorId,doctorName,JMARI
        // を取得している。docInfo の departmentDesc にこれらの情報を連結する。
        //---------------------------
        StringBuilder sb = new StringBuilder();
        sb.append(getPatientVisit().getDeptName()).append(",");             // 診療科名
        sb.append(getPatientVisit().getDeptCode()).append(",");             // 診療科コード : 受けと不一致、受信？
        sb.append(Project.getUserModel().getCommonName()).append(",");      // 担当医名
        if (Project.getUserModel().getOrcaId()!=null) {
            sb.append(Project.getUserModel().getOrcaId()).append(",");      // 担当医コード: ORCA ID がある場合
        } else if (getPatientVisit().getDoctorId()!=null) {
            sb.append(getPatientVisit().getDoctorId()).append(",");         // 担当医コード: 受付でIDがある場合
        } else {
            sb.append(Project.getUserModel().getUserId()).append(",");      // 担当医コード: ログインユーザーID
        }
        sb.append(getPatientVisit().getJmariNumber());                      // JMARI
        docInfo.setDepartmentDesc(sb.toString());                           // 上記をカンマ区切りで docInfo.departmentDesc へ設定
        docInfo.setDepartment(getPatientVisit().getDeptCode());             // 診療科コード 01 内科等
        
        //-------------------------------------------------------------------
        // 2012-05 クレーム送信をJMS+MDB化するために、新たに施設名と医療資格が必要
        //-------------------------------------------------------------------
        docInfo.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        docInfo.setCreaterLisence(Project.getUserModel().getLicenseModel().getLicense());

        //-----------------------------------------------------------
        // 健康保険を設定する-新規カルテダイアログで選択された保険をセットしている
        //-----------------------------------------------------------
        PVTHealthInsuranceModel insurance = params.getPVTHealthInsurance();
        docInfo.setHealthInsurance(insurance.getInsuranceClassCode());
        docInfo.setHealthInsuranceDesc(insurance.toString());
        docInfo.setHealthInsuranceGUID(insurance.getGUID());

        // Versionを設定する
        VersionModel version = new VersionModel();
        version.initialize();
        docInfo.setVersionNumber(version.getVersionNumber());

        //-------------------------------------
        // Document の Status を設定する
        // 新規カルテの場合は none
        //-------------------------------------
        docInfo.setStatus(STATUS_NONE);

        return newModel;
    }

    /**
     * 修正の場合のカルテモデルを生成する。
     * @param oldModel 修正対象のカルテモデル
     * @return 新しい版のカルテモデル
     */
    @Override
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel) {

        // 修正対象の DocInfo を取得する
        DocInfoModel oldDocInfo = oldModel.getDocInfoModel();

        // 新しい版のモデルにモジュールと画像をコピーする
        DocumentModel newModel = new DocumentModel();
        copyModel(oldModel, newModel, false);

        //-------------------------------------
        // 新しい版の DocInfo を設定する
        //-------------------------------------
        DocInfoModel newInfo = newModel.getDocInfoModel();

        // 文書ID
        newInfo.setDocId(GUIDGenerator.generate(newInfo));

        // 新しい版の firstConfirmDate = 元になる版の firstConfirmDate
        newInfo.setFirstConfirmDate(oldDocInfo.getFirstConfirmDate());
        
//minagawa^ 予定カルテ(予定カルテ対応)
        // FirstとConfirmを比較することで予定カルテかどうか判定
        newInfo.setConfirmDate(oldDocInfo.getConfirmDate());
        newInfo.setClaimDate(oldDocInfo.getClaimDate());
//minagawa$
        
        // docType = old one
        newInfo.setDocType(oldDocInfo.getDocType());

        // purpose = old one
        newInfo.setPurpose(oldDocInfo.getPurpose());

        // タイトルも引き継ぐ
        newInfo.setTitle(oldDocInfo.getTitle());

        // 検体検査オーダー番号
        newInfo.setLabtestOrderNumber(oldDocInfo.getLabtestOrderNumber());

        //-------------------------------------
        // 診療科を設定する 
        // 元になる版の情報を利用する
        //-------------------------------------
        newInfo.setDepartmentDesc(oldDocInfo.getDepartmentDesc());
        newInfo.setDepartment(oldDocInfo.getDepartment());
        
        //-------------------------------------------------------------------
        // 2012-05 クレーム送信をJMS+MDB化するために、新たに施設名と医療資格が必要
        // この情報はpersistされていないため再度設定する
        //-------------------------------------------------------------------
        newInfo.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        newInfo.setCreaterLisence(Project.getUserModel().getLicenseModel().getLicense());

        //-------------------------------------
        // 健康保険を設定する
        // 元になる版の情報を利用する
        //-------------------------------------
        newInfo.setHealthInsuranceDesc(oldDocInfo.getHealthInsuranceDesc());
        newInfo.setHealthInsurance(oldDocInfo.getHealthInsurance());
        newInfo.setHealthInsuranceGUID(oldDocInfo.getHealthInsuranceGUID());
        if (DEBUG) {
            ClientContext.getBootLogger().debug(newInfo.getHealthInsuranceDesc());
            ClientContext.getBootLogger().debug(newInfo.getHealthInsurance());
            ClientContext.getBootLogger().debug(newInfo.getHealthInsuranceGUID());
        }

        //-------------------------------------
        // 親文書IDを設定する
        //-------------------------------------
        newInfo.setParentId(oldDocInfo.getDocId());
        newInfo.setParentIdRelation(PARENT_OLD_EDITION);

        //-------------------------------------
        // old PK を設定する
        //-------------------------------------
        newInfo.setParentPk(oldModel.getId());

        //-------------------------------------
        // Versionを設定する
        // new = old + 1.0
        //-------------------------------------
        VersionModel newVersion = new VersionModel();
        newVersion.setVersionNumber(oldDocInfo.getVersionNumber());
        newVersion.incrementNumber(); // version number ++
        newInfo.setVersionNumber(newVersion.getVersionNumber());

        //-------------------------------------
        // Document Status を設定する
        // 元になる版の status (Final | Temporal | Modified)
        //-------------------------------------
        newInfo.setStatus(oldDocInfo.getStatus());

        return newModel;
    }

    /**
     * カルテエディタを生成する。
     * @return カルテエディタ
     */
    public KarteEditor createEditor() {
        KarteEditor editor;
        try {
            editor = new KarteEditor();
            editor.addMMLListner(mmlListener);
            editor.addCLAIMListner(claimListener);
        } catch (Exception e) {
            ClientContext.getBootLogger().warn(e);
            editor = null;
        }
        return editor;
    }

    //----------------------------------
    // モデルをdeepコピーする
    // DocInfo の設定はない
    //----------------------------------
    private void copyModel(DocumentModel oldModel, DocumentModel newModel, boolean applyRp) {
        
        if (applyRp) {
            List<ModuleModel> modules = oldModel.getModules();
            if (modules!=null) {
                for (ModuleModel bean : modules) {
                    IInfoModel model = bean.getModel();
                    if (model!=null && model instanceof BundleMed) {
                        newModel.addModule(ModelUtils.cloneModule(bean));
                    }
                }
            }
        } else {
            List<ModuleModel> modules = oldModel.getModules();
            if (modules!=null) {
                for (ModuleModel bean : modules) {
                    newModel.addModule(ModelUtils.cloneModule(bean));
                }
            }
            List<SchemaModel> schema = oldModel.getSchema();
            if (schema!=null) {
                for (SchemaModel scm : schema) {
                    newModel.addSchema(ModelUtils.cloneSchema(scm));
                }
            }
            List<AttachmentModel> attachment = oldModel.getAttachment();
            if (attachment!=null) {
                for (AttachmentModel am : attachment) {
                    newModel.addAttachment(ModelUtils.cloneAttachment(am));
                }
            }
        }
    }

    /**
     * カルテ作成時にダアイログをオープンし、保険を選択させる。
     *
     * @return NewKarteParams
     */
    public NewKarteParams getNewKarteParams(String docType, Chart.NewKarteOption option, JFrame f, String deptName, String deptCode, String insuranceUid) {

        //--------------------------------------------
        // 下記は PatientVisit から取得している
        // deptName
        // deptCode
        // insuranceUid 受付なしで患者検索からの場合は null
        //--------------------------------------------
        NewKarteParams params = new NewKarteParams(option);
        params.setDocType(docType);
        params.setDepartmentName(deptName);
        params.setDepartmentCode(deptCode);

        // 患者の健康保険コレクション
        Collection<PVTHealthInsuranceModel> insurances = pvt.getPatientModel().getPvtHealthInsurances();

        // コレクションが null の場合は自費保険を追加する
        if (insurances == null || insurances.isEmpty()) {
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

        // insuranceUidがnullでない場合はそれに一致する保険を探す
        // 見つかった保険をダイアログが表示された時に選択状態にする
        // insuranceUid = null (受付なし）の場合は先頭(index=0)を選択する
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

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String text = option == Chart.NewKarteOption.BROWSER_MODIFY
                ? resource.getString("modifyKarteTitle")
                : resource.getString("newKarteTitle");

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
    @Override
    public PVTHealthInsuranceModel[] getHealthInsurances() {

        // 患者の健康保険
        Collection<PVTHealthInsuranceModel> insurances = pvt.getPatientModel().getPvtHealthInsurances();

        // 保険がない場合 自費保険を生成して追加する
        if (insurances == null || insurances.isEmpty()) {
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
     * 選択された保険を特定する。
     * @param uuid 選択された保険のUUID
     * @return 選択された保険
     */
    @Override
    public PVTHealthInsuranceModel getHealthInsuranceToApply(String uuid) {

        if (DEBUG) {
            ClientContext.getBootLogger().debug("uuid to apply = " + uuid);
        }

        PVTHealthInsuranceModel ret = null;
        PVTHealthInsuranceModel first = null;

        // 患者の健康保険
        Collection<PVTHealthInsuranceModel> insurances = pvt.getPatientModel().getPvtHealthInsurances();

        if (uuid!=null && insurances!=null && insurances.size()>0) {

            for (PVTHealthInsuranceModel hm : insurances) {
                if (first == null) {
                    first = hm;
                }
                if (uuid.equals(hm.getGUID())) {
                    ret = hm;
                    if (DEBUG) {
                        ClientContext.getBootLogger().debug("found uuid to apply = " + uuid);
                    }
                    break;
                }
            }
        }

        if (ret != null) {
            return ret;
        }

        else if (first!=null) {
            return first;
        }

        return null;
    }

    /**
     * タブにドキュメントを追加する。
     * @param doc 追加するドキュメント
     * @param params 追加するドキュメントの情報を保持する NewKarteParams
     */
    public void addChartDocument(ChartDocument doc, NewKarteParams params) {
        String title;
        if (params.getPVTHealthInsurance() != null) {
            title = getTabTitle(params.getDepartmentName(), params.getPVTHealthInsurance().getInsuranceClass());
        } else {
            title = getTabTitle(params.getDepartmentName(), null);
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
    
//minagawa^ LSC Test
    /**
     * タブドキュメントのアイコンを変更する。
     * @param icon タブに設定するアイコン
     * @param c ChartDocumentの Component
     */
    public void setChartDocumentIconAt(ImageIcon icon, Component c) {
        int index = tabbedPane.indexOfComponent(c);
        if (index>=0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setIconAt(index, icon);
        }
    }
//minagawa$    

    /**
     * 新規カルテ用のタブタイトルを作成する
     * @param insurance 保険名
     * @return タブタイトル
     */
    public String getTabTitle(String dept, String insurance) {
        String[] depts = dept.split("\\s*,\\s*");
        StringBuilder buf = new StringBuilder();
        buf.append(ClientContext.getBundle(this.getClass()).getString("newKarteTabTitle"));
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
                    pluginClass).newInstance();
            
            if (doc instanceof KarteEditor) {
                //String dept = getPatientVisit().getDeptNoTokenize();
                //String deptCode = getPatientVisit().getDepartmentCode();
                String dept = getPatientVisit().getDeptName();
                String deptCode = getPatientVisit().getDeptCode();
                String insuranceUid = getPatientVisit().getInsuranceUid();
                Chart.NewKarteOption option = Chart.NewKarteOption.BROWSER_NEW;
                String docType = IInfoModel.DOCTYPE_S_KARTE;
                NewKarteParams params = new NewKarteParams(option);
                params.setDocType(docType);
                params.setDepartmentName(dept);
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

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            ClientContext.getBootLogger().warn(e);
        }
    }
    
    public void checkInteraction() {
        CheckInteractionPanel panel = new CheckInteractionPanel();
        panel.enter(this);
        //panel = null;
    }
    
    /**
     * 新規文書作成で選択されたOffice文書テンプレートを開く。
     * @param templatePath OpenOffice odt 
     */
    private void invokeOffice(final String docName, final String templatePath) {
        
        if (!Desktop.isDesktopSupported() || 
            templatePath==null || 
            (!templatePath.endsWith(EXT_ODT_TEMPLATE))) {
            return;
        }
        
        SwingWorker w = new SwingWorker<String,Void>() {
            
            @Override
            protected String doInBackground() throws Exception {
                DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
//minagawa^ jdk7               
//DocumentTemplate template = documentTemplateFactory.getTemplate(new File(templatePath));
//minagawa$                
                DocumentTemplate template = documentTemplateFactory.getTemplate(Files.newInputStream(Paths.get(templatePath)));
                Map data = new HashMap();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'M'月'd'日'");
                
                // Entry date
                Date entryDate = new Date();
                data.put("entry_date", sdf.format(entryDate));
                data.put("entry_date_era", MMLDate.warekiStringFromDate(entryDate));
                
                // Patient
                data.put("pt_id", getOdtString(getPatient().getPatientId()));
                data.put("pt_kana", getOdtString(getPatient().getKanaName()));
                data.put("pt_name", getPatient().getFullName());
                data.put("g", ModelUtils.getGenderDesc(getPatient().getGender()));

                Date birthday = ModelUtils.getDateAsObject(getPatient().getBirthday());
                if (birthday!=null) {
                    data.put("pt_birth", sdf.format(birthday));
                    data.put("pt_birth_era", getOdtString(MMLDate.warekiStringFromDate(birthday)));
                } else {
                    data.put("pt_birth", "");
                    data.put("pt_birth_era", "");
                }

                String age = AgeCalculater.getAge(getPatient().getBirthday(), 6);
                data.put("pt_age", getOdtString(age));

                data.put("pt_zip", getOdtString(getPatient().contactZipCode()));
                data.put("pt_addr", getOdtString(getPatient().contactAddress()));
                data.put("pt_tel", getOdtString(getPatient().getTelephone()));
                
                // Physician
                UserModel u = Project.getUserModel();
                data.put("phy_hosp", getOdtString(u.getFacilityModel().getFacilityName()));
                data.put("phy_zip", getOdtString(u.getFacilityModel().getZipCode()));
                data.put("phy_addr", getOdtString(u.getFacilityModel().getAddress()));
                data.put("phy_tel", getOdtString(u.getFacilityModel().getTelephone()));
                data.put("phy_fax", getOdtString(u.getFacilityModel().getFacsimile()));
                data.put("phy_name", getOdtString(u.getCommonName()));

                // FileName = 文書名_患者氏名様_YYYY-MM-DD(n).odt
                String pathToOpen = UserDocumentHelper.createPathToDocument(
                        Project.getString(Project.LOCATION_PDF),    // 設定画面で指定されている dir
                        docName,                                    // 文書名
                        EXT_ODT_TEMPLATE,                           // 拡張子
                        getPatient().getFullName(),                 // 患者氏名
                        entryDate);                                 // 日付
//minagawa^ jdk7
                Path pathObj = Paths.get(pathToOpen);
                //template.createDocument(data, new FileOutputStream(pathToOpen));
                template.createDocument(data, Files.newOutputStream(pathObj));
                return pathObj.toAbsolutePath().toString();
//minagawa$                
            }
            
            @Override
            protected void done() {
                try {
                    String pathToOpen = get();
                    if (pathToOpen!=null) {
                        Desktop desktop = Desktop.getDesktop();
//minagawa^ jdk7                       
                        //desktop.open(new File(pathToOpen));
                        desktop.browse(Paths.get(pathToOpen).toUri());
//minagawa$                        
                    }
                } catch (IOException ex) {
                    showOfficeError("OpenOffice文書に関連づけされたアプリケーションを起動できません。");
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                    showOfficeError("OpenOfficeテンプレートから文書を作成できません。");
                }
            }
        };
        
        w.execute();
    }
    
    // 差し込み作成のエラー表示
    private void showOfficeError(String msg) {
        Window parent = SwingUtilities.getWindowAncestor(getFrame());
        JOptionPane.showMessageDialog(parent, msg, ClientContext.getFrameTitle("Office文書作成"), JOptionPane.WARNING_MESSAGE);
    }
    
    // str == null の時 template の ${prop} ="" にする
    private String getOdtString(String str) {
        return str!=null ? str : "";
    }

    /**
     * カルテ以外の文書を作成する。
     */
    public void newDocument() {
        
        // 拡張ポイント新規文書のプラグインをリストアップし、
        // リストで選択させる
        List<NameValuePair> documents = new ArrayList<NameValuePair>(3);
        PluginLister<NChartDocument> lister = PluginLister.list(NChartDocument.class);
        LinkedHashMap<String, String> nproviders = lister.getProviders();
        if (nproviders != null) {
            Iterator<String> iter = nproviders.keySet().iterator();
            while (iter.hasNext()) {
                String cmd = iter.next();
                String clsName = nproviders.get(cmd);
                NameValuePair pair = new NameValuePair(cmd, clsName);
                documents.add(pair);
                if (DEBUG) {
                    ClientContext.getBootLogger().debug(cmd + " = " + clsName);
                }
            }
        }
        
        //---------------------------------------------------------------
        // 訪問看護指示書等のローカルにある OpenOffice Template をリストアップする
        //---------------------------------------------------------------
        boolean hasOOD = false;
//minagawa^ jdk7       
//        File tmpDir = new File(ClientContext.getOdtTemplateDirectory());
//        if (tmpDir.exists() && tmpDir.isDirectory()) {
//            File[] files = tmpDir.listFiles();
//            for (File file : files) {
//                if (file.isFile()) {
//                    String path = file.getPath();
//                    if (path.toLowerCase().endsWith(EXT_ODT_TEMPLATE)) {
//                        String name = file.getName();
//                        int len = name.length()-4;  // .odt
//                        name = name.substring(0, len);
//                        documents.add(new NameValuePair(name, path));
//                        hasOOD = true;
//                    }
//                }
//            }
//        }
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(ClientContext.getOdtTemplateDirectory()));
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    continue;
                }
                String path = p.toAbsolutePath().toString();
                if (path.toLowerCase().endsWith(EXT_ODT_TEMPLATE)) {
                    String name = p.getFileName().toString();
                    int len = name.length()-4;  // .odt
                    name = name.substring(0, len);
                    documents.add(new NameValuePair(name, path));
                    hasOOD = true;
                }
            }
        } catch (Exception e) { 
        }
//minagawa$        
        
        if (documents.isEmpty()) {
            ClientContext.getBootLogger().debug("No plugins");
            return;
        }
        
        final JList docList = new JList(documents.toArray());
        docList.setCellRenderer(new PdfOfficeIconRenderer());

        // 凡例ラベル
        JPanel pdfOffice = new JPanel();
        pdfOffice.setLayout(new BoxLayout(pdfOffice,BoxLayout.Y_AXIS));
        JLabel pdfLabel = new JLabel(": FormからPDF作成");
//minagawa^ Icon Server        
        //pdfLabel.setIcon(ClientContext.getImageIcon("pdf_icon16.png"));
        pdfLabel.setIcon(ClientContext.getImageIconArias("icon_pdf_small"));
//minagawa$        
        pdfOffice.add(pdfLabel);
        
        if (hasOOD) {
            pdfOffice.add(Box.createVerticalStrut(5));
            JLabel officeLabel = new JLabel(": OpenDocumentテンプレートへ差し込み");
//minagawa^ Icon Server            
            //officeLabel.setIcon(ClientContext.getImageIcon("docs_16.png"));
            officeLabel.setIcon(ClientContext.getImageIconArias("icon_plain_document_small"));
//minagawa$            
            pdfOffice.add(officeLabel);
        }
        pdfOffice.setBorder(BorderFactory.createEmptyBorder(6,6,5,5));
        
        // List panel
        JPanel listPanel = new JPanel(new BorderLayout(7, 0));
        listPanel.add(docList, BorderLayout.CENTER);
        listPanel.setBorder(BorderFactory.createEmptyBorder(6,6,5,5));
        JPanel content = new JPanel(new BorderLayout());
        content.add(listPanel, BorderLayout.CENTER);
        content.add(pdfOffice, BorderLayout.SOUTH);
        
        content.setBorder(BorderFactory.createTitledBorder("PDF/差し込み文書作成"));

//minagawa^ mac jdk7        
        //final JButton okButton = new JButton("了解");
        //final JButton cancelButton = new JButton("取消し");
        final JButton okButton = new JButton("選択");
        final JButton cancelButton = new JButton(GUIFactory.getCancelButtonText());
//minagawa$        
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

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
                NameValuePair pair = (NameValuePair) docList.getSelectedValue();
                String test = pair.getValue();
                if (test.endsWith(EXT_ODT_TEMPLATE)) {
                    String docName = pair.getName();
                    invokeOffice(docName, test);
                } else {
                    invokePlugin(test);
                }
            }
        });
        okButton.setEnabled(false);

        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        docList.addListSelectionListener(new ListSelectionListener() {

            @Override
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
//    private void saveAll(java.util.List<UnsavedDocument> dirtyList) {
//
//        if (dirtyList == null || dirtyList.isEmpty()) {
//            return;
//        }
//
//        try {
//            int total = dirtyList.size();
//            int index = 0;
//            for (UnsavedDocument undoc : dirtyList) {
//                if (undoc.isNeedSave()) {
//                    ChartDocument doc = (ChartDocument)providers.get(String.valueOf(undoc.getIndex()));
////minagawa^ 未保存の文書が全て保存されるのを待つ カルテ、病名、紹介状、予約                   
//                    if (doc != null && doc.isDirty()) {
//                        tabbedPane.setSelectedIndex(undoc.getIndex());
//                        DirtySaveController ctl = new DirtySaveController(doc,total,index);
//                        doc.addPropertyChangeListener(ChartDocument.CHART_DOC_DID_SAVE, ctl);
//                        doc.save();
//                    }
////minagawa$                    
//                }
//                index++;
//            }
//
//        } catch (Exception e) {
//            ClientContext.getBootLogger().warn(e);
//        }
//    }
    private void saveAll() {
        // listの最初のドキュメントに保存をコールしbreakする
        // propertychangeを受信して次の保存へ行く
        while(!dirtyList.isEmpty()) {
            UnsavedDocument undoc = dirtyList.remove(0);
            ChartDocument doc = (ChartDocument)providers.get(String.valueOf(undoc.getIndex()));
            if (doc != null && doc.isDirty()) {
                tabbedPane.setSelectedIndex(undoc.getIndex());
                ListeneAndGoController ctl = new ListeneAndGoController(doc);
                doc.addPropertyChangeListener(ChartDocument.CHART_DOC_DID_SAVE, ctl);
                doc.save();
                break;
            }
        }      
    }
    
    // 未保存の文書が全て保存されるのを待って stopを実行するリスナ
    class DirtySaveController implements PropertyChangeListener {
        
        private ChartDocument doc;
        private int total;
        private int index;
        
        public DirtySaveController(ChartDocument doc, int total, int index) {
            this.doc=doc;
            this.total=total;
            this.index=index;
        }

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            if (pce.getPropertyName().equals(AbstractChartDocument.CHART_DOC_DID_SAVE)) {
                doc.removePropertyChangeListener(AbstractChartDocument.CHART_DOC_DID_SAVE, this);
                if (index == (total-1)) {
                    // 最後なら
                    stop();
                }
            }
        }
    }

    class ListeneAndGoController implements PropertyChangeListener {
        
        private ChartDocument doc;
        
        public ListeneAndGoController(ChartDocument doc) {
            this.doc=doc;
        }

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            doc.removePropertyChangeListener(AbstractChartDocument.CHART_DOC_DID_SAVE, this);
            // 保存の正否 キャンセルの場合は false、saveAllがコールされないので止まる
            boolean proceed = ((Boolean)pce.getNewValue()).booleanValue();
            if (proceed) {
                if (!dirtyList.isEmpty()) {
                    saveAll();
                } else {
                    // 空になったので stop
                    stop();
                }
            }
        }
    }
//minagawa$      

    /**
     * ドキュメントのなかにdirtyのものがあるかどうかを返す。
     * @return dirtyの時true
     */
    private java.util.List<UnsavedDocument> dirtyList() {
        java.util.List<UnsavedDocument> ret = null;
        int count = tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            ChartDocument doc = (ChartDocument)providers.get(String.valueOf(i));
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
    @Override
    public void close() {
        
//masuda^ この患者のEditorFrameが開いたままなら、インスペクタを閉じられないようにする
        List<Chart> editorFrames = EditorFrame.getAllEditorFrames();
        if (editorFrames != null && !editorFrames.isEmpty()) {
            long ptId = this.getPatient().getId();
            for (Chart chart : editorFrames) {
                long id = chart.getPatient().getId();
                if (ptId == id) {
                    // よくわからないEditorFrameが残っていて、Frameがぬるぽのときがあるので
                    try {
                        // 最小化してたらFrameを再表示させる
                        chart.getFrame().setExtendedState(Frame.NORMAL);
                        //JOptionPane.showMessageDialog(this.getFrame(),
                        String title = ClientContext.getFrameTitle("インスペクタ");
                        JOptionPane.showMessageDialog(chart.getFrame(),
                                "インスペクタを閉じる前にカルテエディタを閉じてください。",
                                title, JOptionPane.WARNING_MESSAGE);
                        return;
                    } catch (Exception e) {
                    }
                }
            }
        }
//        // 別ウィンドウで開いていたら閉じるように警告する
//        if (inactiveProvidersMap != null && !inactiveProvidersMap.isEmpty()) {
//            for (Iterator itr = inactiveProvidersMap.entrySet().iterator(); itr.hasNext();) {
//                String title = ClientContext.getFrameTitle("インスペクタ");
//                Map.Entry entry = (Map.Entry) itr.next();
//                JFrame frame = (JFrame) entry.getValue();
//                frame.setExtendedState(Frame.NORMAL);
//                JOptionPane.showMessageDialog(frame,
//                        "インスペクタを閉じる前にこのウィンドウを閉じてください。",
//                        title, JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//        }
//masuda$

        //--------------------------------------------
        // 未保存ドキュメントがある場合はダイアログを表示し
        // 保存するかどうかを確認する
        //--------------------------------------------
        List<UnsavedDocument> localDirtyList = dirtyList();

        if (localDirtyList != null && localDirtyList.size() > 0) {

            ResourceBundle resource = ClientContext.getBundle(this.getClass());
            String saveAll = resource.getString("unsavedtask.saveText");     // 保存;
            String discard = resource.getString("unsavedtask.discardText");  // 破棄;
            String question = resource.getString("unsavedtask.question");    // 未保存のドキュメントがあります。保存しますか ?
            String title = resource.getString("unsavedtask.title");          // 未保存処理
//minagawa^ mac jdk7            
//            String cancelText = (String) UIManager.get("OptionPane.cancelButtonText");
            String cancelText = GUIFactory.getCancelButtonText();
//minagawa$
            Object[] message = new Object[localDirtyList.size() + 1];
            message[0] = (Object) question;
            int index = 1;
            for (UnsavedDocument doc : localDirtyList) {
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
//minagawa^ lsctest         
                    //saveAll(dirtyList);
                    dirtyList = new ArrayList<UnsavedDocument>();
                    for (UnsavedDocument doc : localDirtyList) {
                        // 保存がcheckされているもののみ追加
                        if (doc.isNeedSave()) {
                            dirtyList.add(doc);
                        }
                    }
                    if (!dirtyList.isEmpty()) {
                        saveAll();
                    } else {
                        stop();
                    }
//minagawa$                    
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
        if (beeperHandle != null) {
            boolean b = beeperHandle.cancel(true);
            if (DEBUG) {
                ClientContext.getBootLogger().debug("beeperHandle.cancel = " + b);
            }
        }
        if (scheduler != null) {
            scheduler.shutdown();
            if (DEBUG) {
                ClientContext.getBootLogger().debug("scheduler.shutdown");
            }
        }
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
        Project.setRectangle(PROP_FRMAE_BOUNDS, getFrame().getBounds());
        getFrame().setVisible(false);
        getFrame().setJMenuBar(null);
        getFrame().dispose();
        
//s.oh^ カルテの画像連携
        if(imageScheduler != null) {
            imageScheduler.shutdown();
        }
//s.oh$
    }
    
//s.oh^ 不具合修正(一括終了時のステータスクリア)
    public void publishKarteClosed() {
        ChartEventHandler scl = ChartEventHandler.getInstance();
        if(scl != null) {
            scl.publishKarteClosed(ChartImpl.this.getPatientVisit());
        }
    }
//s.oh$

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
        @Override
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

        @Override
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

        @Override
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
    
//s.oh^ カルテの画像連携
    class ImageWatcher implements Runnable {
        
        private File watchDir;
        
        public ImageWatcher(File watchDir) {
            this.watchDir = watchDir;
        }

        @Override
        public void run() {
            
            // 今回のリスト
            File[] files = watchDir.listFiles();
            
            if(files != null) {
                // Iterate
                for (File file : files) {
                    if(firstImageWatcher) {
                        file.delete();
                        continue;
                    }

                    final String name = file.getName();
                    int extIdx = name.lastIndexOf(".");
                    if(extIdx < 0) {
                        continue;
                    }
                    
                    String ext = name.substring(extIdx+1).toLowerCase();
                    if(ext.toLowerCase().equals("jpg") || ext.toLowerCase().equals("JPG") || ext.toLowerCase().equals("jpeg") || ext.toLowerCase().equals("JPEG")) {
                        // 拡張子を除いたファイル名を得る
                        String baseName = name.substring(0, extIdx);
                        String sop = baseName;
                        
                        final ImageEntry entry = new ImageEntry();
                        //entry.setPid(pvt.getPatientId());
                        //entry.setModality("CR");
                        //entry.setSeqNum(Integer.parseInt(seq));
                        entry.setPath(baseName);
                        //entry.setExtension(ext);
                        entry.setFileName(name);
                        try {
                            entry.setUrl(file.toURI().toURL().toString());
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace(System.err);
                        }

                        // 開いているカルテの処理
                        for(Object obj : karteEditorList) {
                            KarteEditor editor = (KarteEditor)obj;
                            if(editor != null && !editor.isClosedFrame() && editor.getSOAPane() != null) {
                                editor.getSOAPane().importImage(entry, sop, ext);
                                break;
                            }
                        }
                        
                        // issue
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }else{
                        file.delete();
                    }
                }
            }
            
            firstImageWatcher = false;
        }
    }
//s.oh$
}
