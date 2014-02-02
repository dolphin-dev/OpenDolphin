/*
 * ChartPlugin.java
 * Copyright 2001,2002 Dolphin project. All Rights Reserved.
 * Copyright 2004-2005 Digital Globe, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.*;

import open.dolphin.delegater.DocumentDelegater;
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
import open.dolphin.order.MMLTable;
import open.dolphin.plugin.*;
import open.dolphin.plugin.helper.*;
import open.dolphin.project.*;
import open.dolphin.util.GUIDGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 2号カルテ、傷病名、検査結果履歴等、患者の総合的データを提供するクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ChartPlugin extends DefaultMainWindowPlugin implements IChart,IInfoModel {
    
    private static final long serialVersionUID = 3074544825882680694L;
    
    /** カルテ状態の束縛プロパティ名 */
    public static final String CHART_STATE = "chartStateProp";
    
    /** 診察未終了で閉じている状態 */
    public static final int CLOSE_NONE      = 0;
    
    /** 診察が終了し閉じている状態  */
    public static final int CLOSE_SAVE      = 1;
    
    /** 診察未終了でオープンしている状態 */
    public static final int OPEN_NONE       = 2;
    
    /** 診察が終了しオープンしている状態 */
    public static final int OPEN_SAVE       = 3;
    
    /** 受付キャンセル */
    public static final int CANCEL_PVT      = -1;
    
    /**  Chart インスタンスを管理するstatic 変数 */
    private static ArrayList<ChartPlugin> allCharts = new ArrayList<ChartPlugin>(3);
    
    /** Chart 状態の通知を行うための static 束縛サポート */
    private static PropertyChangeSupport boundSupport = new PropertyChangeSupport(new Object());
    
    // フレームサイズと位置
    private static final int FRAME_X = 25;
    
    private static final int FRAME_Y = 20;
    
    private static final int DOCUMENT_WIDTH     = 710; // 345+345+2+scrollW = 692+17 = 710? -> 724
    
    private static final int DOC_HISTORY_WIDTH  = 280;
    
    private static final int FRAME_WIDTH = DOCUMENT_WIDTH + DOC_HISTORY_WIDTH;
    
    private static final int FRAME_HEIGHT   = 740;
    
    private static final int HISTORY_PALCE  = 0; // 0 = LEFT, 1 = RIGHT
    
    private static final int DIVIDER_SIZE   = 1;
    
    private static final String TITLE_ASSIST = " - インスペクタ";
    
    private static final String SAMA = "様";
    
    // このクラスから起動するプラグインの JNDI 名
    private static final String KARTE_EDITOR_JNDI = "chart/karteEditor";
    
    // このクラスのプラグイン
    private static final String DOCUMENT_PLUG_POINT = "chart/comp"; // PlugPoint
    
    //
    // インスタンス変数
    //
    
    /** Document Plugin を格納する TabbedPane */
    private JTabbedPane tabbedPane;
    
    /** Document Plugin のリスト */
    private ArrayList<PluginReference> documents; 
    
    /** Active になっているDocument Plugin */
    private Hashtable<String, IChartDocument> activeChildren;
    
    /** 患者インスペクタ */
    private PatientInspector inspector; 
    
    /** ドキュメントとヒストリを分割する SpltPane */
    private int historyPlace = HISTORY_PALCE; // ヒストリーを左側に位置させる
    
    /** ディバイダのサイズ */
    private int dividerSize = DIVIDER_SIZE;
    
    /** ディバイダの位置 */
    private int dividerLocation
            = HISTORY_PALCE == 0 ? DOC_HISTORY_WIDTH : DOCUMENT_WIDTH;
    
    /** Window Menu をサポートする委譲クラス */
    private WindowSupport windowSupport;
    
    /** Toolbar */
    private JPanel myToolPanel;
    
    /** 検索状況等を表示する共通のパネル */
    private IStatusPanel statusPanel;
    
    /** 患者来院情報 */
    private PatientVisitModel pvt;
    
    /** Read Only の時 true */
    private boolean readOnly;
    
//    // CLAIMデータを送信した場合に true
//    private boolean claimSent;
    
    /** Chart のステート */
    private int chartState;
    
    /** Chart内のドキュメントに共通の MEDIATOR */
    private ChartMediator mediator;
    
    /** State Mgr */
    private StateMgr stateMgr;
    
    /** MML送信 listener */
    private MmlMessageListener mmlListener;
    
    /** CLAIM 送信 listener */
    private ClaimMessageListener claimListener;
       
    /** このチャートの KarteBean */
    private KarteBean karte;
    
    //
    // タスク関連の定数
    //
    /** Task Timer */
    private javax.swing.Timer taskTimer;
    
    /** 割り込み時間 msec */
    private static final int TIMER_DELAY = 200;
    
    /** 全体の見積り時間 */
    private static final int MAX_ESTIMATION = 3000;
    
    /** 300 msec 後にポップアップの判断をする */
    private static final int DECIDE_TO_POPUP = 300;
    
    /** その時 Taskが 500msec以上かかるようであればポップアップする */
    private static final int MILIS_TO_POPUP = 500;
    
    /** Progress monitor  */
    private static final String PROGRESS_NOTE = "カルテを開いています...";
    
    /** GlassPane */
    private BlockGlass blockGlass;
    
    /**
     * Creates new ChartService
     */
    public ChartPlugin() {
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
    
//    public void setClaimSent(boolean b) {
//        claimSent = b;
//    }
//    
//    public boolean isClaimSent() {
//        return claimSent;
//    }
    
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
        ChartPlugin.fireChanged(this);
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
        if (index >= 0 && index <= cnt -1 && index != tabbedPane.getSelectedIndex()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
    
    /**
     * チャート内に未保存ドキュメントがあるかどうかを返す。
     * @return 未保存ドキュメントがある時 true
     */
    public boolean isDirty() {
        
        boolean dirty = false;
        
        if (activeChildren != null && activeChildren.size() > 0) {
            
            Collection<IChartDocument> docs = activeChildren.values();
            for (IChartDocument doc : docs) {
                if (doc.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }
        return dirty;
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        
        // 初期化 Worker を生成する
        final CallBacksWorker worker = new CallBacksWorker(this, "initComponent", null, null);
        
        // 初期化 Worker 用の ProgressMonitor を生成する
        Object[] messages = new Object[1];
        String patientName = getPatientVisit().getPatient().getFullName() + SAMA;
        messages[0] = new JLabel(patientName, ClientContext.getImageIcon("open_32.gif"), SwingConstants.CENTER);
        final ProgressMonitor monitor = new ProgressMonitor(null, messages, PROGRESS_NOTE, 0, MAX_ESTIMATION / TIMER_DELAY);
        
        // Worker チェックの割り込みタイマーを生成する
        taskTimer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                if (worker.isDone()) {
                    taskTimer.stop();
                    monitor.close();
                    //
                    // このコールで文書履歴を表示する
                    //
                    getDocumentHistory().showHistory();
                } else {
                    monitor.setProgress(worker.getCurrent());
                }
            }
        });
        // 初期化を開始する
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(DECIDE_TO_POPUP);
        monitor.setMillisToPopup(MILIS_TO_POPUP);
        worker.start();
        taskTimer.start();
    }
    
    /**
     * 患者のカルテを検索取得し、GUI を構築する。
     * このメソッドはバックグランドスレッドで実行される。
     */
    @SuppressWarnings("serial")
    public void initComponent() {
        
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
        karteBean.setPatient(null);
        karteBean.setPatient(this.getPatientVisit().getPatient());
        setKarte(karteBean);
        
        //
        // このチャート の Frame を生成し初期化する。
        // Frame のタイトルを
        // 患者氏名(カナ):患者ID に設定する
        //
        StringBuilder sb = new StringBuilder();
        sb.append(getPatient().getFullName());
        sb.append("(");
        String kana = getPatient().getKanaName();
        kana = kana.replace("　", " ");
        sb.append(kana);
        sb.append(")");
        //sb.append(" : ");
        //sb.append(getPatient().getGenderDesc());
        sb.append(" : ");
        sb.append(getPatient().getPatientId());
        sb.append(TITLE_ASSIST);
        windowSupport = WindowSupport.create(sb.toString());
        
        // チャート用のメニューバーを得る
        JMenuBar myMenuBar = windowSupport.getMenuBar();
        
        // チャートの JFrame オブジェクトを得る
        JFrame frame = windowSupport.getFrame();
        
        // BlockGlass を設定する
        blockGlass = new BlockGlass();
        frame.setGlassPane(blockGlass);
        
        // このチャートの Window にリスナを設定する
        frame.addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent e) {
                // CloseBox の処理を行う
                processWindowClosing();
            }
            
            public void windowOpened(WindowEvent e) {
                // Window がオープンされた時の処理を行う
                ChartPlugin.windowOpened(ChartPlugin.this);
            }
            
            public void windowClosed(WindowEvent e) {
                // Window がクローズされた時の処理を行う
                ChartPlugin.windowClosed(ChartPlugin.this);
            }
            
            public void windowActivated(WindowEvent e) {
                //
                // 文書履歴へフォーカスする
                //
                getDocumentHistory().requestFocus();
            }
        });
        
        //
        // フレームの表示位置を決める J2SE 5.0
        //
        boolean locByPlatform = Project.getPreferences().getBoolean(Project.LOCATION_BY_PLATFORM, true);
        
        if (locByPlatform) {
            
            frame.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            frame.setLocationByPlatform(true);
            
        } else {
            frame.setLocationByPlatform(false);
            Point loc = new Point(FRAME_X, FRAME_Y);
            Dimension size = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
            ComponentMemory cm = new ComponentMemory(frame, loc, size, this);
            cm.setToPreferenceBounds();
        }
        
        // 患者インスペクタを生成する
        inspector = new PatientInspector(this);
        inspector.getPanel().setBorder(BorderFactory.createEmptyBorder(7, 7, 5, 2)); // カット&トライ
        
        // Status パネルを生成する
        statusPanel = new StatusPanel();
        
        // Status パネルに表示する情報を生成する
        // カルテ登録日 Status パネルの右側に配置する
        Date date = getKarte().getCreated();
        String dateF = ClientContext.getString("statusPanel.karte.rdFormat");
        String rdTitle = ClientContext.getString("statusPanel.karte.title");
        SimpleDateFormat sdf = new SimpleDateFormat(dateF);
        String created = sdf.format(date);
        statusPanel.setRightInfo(rdTitle + created); // カルテ登録日:yyyy/mm/dd
        // 患者ID Status パネルの左に配置する
        String pidTitle = ClientContext.getString("statusPanel.patient.idTitle");
        statusPanel.setLeftInfo(pidTitle + getKarte().getPatient().getPatientId()); // 患者ID:xxxxxx
        
        // ChartMediator を生成する
        mediator = new ChartMediator(this);
        
        // MenuBar を生成する
        Object[] menuStaff = getContext().createMenuBar(myMenuBar, mediator);
        myToolPanel = (JPanel) menuStaff[1];
        myToolPanel.add(inspector.getBasicInfoInspector().getPanel(), 0);
        
        //
        // このクラス固有のToolBarを生成する
        //
        JToolBar toolBar = new JToolBar();
        myToolPanel.add(toolBar);
        
        // テキストツールを生成する
        AbstractAction action = new AbstractAction(GUIConst.MENU_TEXT) {
            public void actionPerformed(ActionEvent e) {
            }
        };
        mediator.getActions().put(GUIConst.ACTION_INSERT_TEXT, action);
        JButton stampBtn = toolBar.add(action);
        stampBtn.setText("");
        stampBtn.setIcon(ClientContext.getImageIcon("notep_24.gif"));
        stampBtn.setToolTipText(GUIConst.TOOLTIPS_INSERT_TEXT);
        stampBtn.setFocusable(false);
        stampBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                mediator.addTextMenu(popup);
                if (!e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // シェーマツールを生成する
        action = new AbstractAction(GUIConst.MENU_SCHEMA) {
            public void actionPerformed(ActionEvent e) {
            }
        };
        mediator.getActions().put(GUIConst.ACTION_INSERT_SCHEMA, action);
        stampBtn = toolBar.add(action);
        stampBtn.setText("");
        stampBtn.setIcon(ClientContext.getImageIcon("picts_24.gif"));
        stampBtn.setToolTipText(GUIConst.TOOLTIPS_INSERT_SCHEMA);
        stampBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                getContext().showSchemaBox();
            }
        });
        
        // スタンプツールを生成する
        action = new AbstractAction(GUIConst.MENU_STAMP) {
            public void actionPerformed(ActionEvent e) {
            }
        };
        mediator.getActions().put(GUIConst.ACTION_INSERT_STAMP, action);
        stampBtn = toolBar.add(action);
        stampBtn.setText("");
        stampBtn.setIcon(ClientContext.getImageIcon("lgicn_24.gif"));
        stampBtn.setToolTipText(GUIConst.TOOLTIPS_INSERT_STAMP);
        stampBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                mediator.addStampMenu(popup);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        // 保険選択ツールを生成する
        action = new AbstractAction(GUIConst.MENU_INSURANCE) {
            public void actionPerformed(ActionEvent e) {
            }
        };
        mediator.getActions().put(GUIConst.ACTION_SELECT_INSURANCE, action);
        stampBtn = toolBar.add(action);
        stampBtn.setText("");
        stampBtn.setIcon(ClientContext.getImageIcon("addbk_24.gif"));
        stampBtn.setToolTipText(GUIConst.TOOLTIPS_SELECT_INSURANCE);
        stampBtn.addMouseListener(new MouseAdapter() {
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
        
        // StateMgr を生成する
        stateMgr = new StateMgr();
        
        // Document プラグインのタブを生成する
        tabbedPane = loadDocuments();
        
        // 全体をレイアウトする
        JSplitPane splitPane = null;
        switch (historyPlace) {
            case 0:
                splitPane = new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT, inspector.getPanel(), tabbedPane);
                splitPane.setDividerLocation(dividerLocation);
                splitPane.setDividerSize(dividerSize);
                break;
                
            case 1:
                splitPane = new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT, tabbedPane,inspector.getPanel());
                splitPane.setDividerLocation(dividerLocation);
                splitPane.setDividerSize(dividerSize);
                break;
        }
        
        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(myToolPanel, BorderLayout.NORTH);
        myPanel.add(splitPane, BorderLayout.CENTER);
        myPanel.add((JPanel) statusPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(myPanel);
        
        // MML 送信 Queue
        if (Project.getSendMML()) {
            mmlListener = (MmlMessageListener) getContext().getPlugin(GUIConst.JNDI_SEND_MML);
        }
        
        // CLAIM 送信 Queue
        if (Project.getSendClaim()) {
            claimListener = (ClaimMessageListener) getContext().getPlugin(GUIConst.JNDI_SEND_CLAIM);
        }
        
        // 最後に実体化の宣言をする
        // これ以降、このスレッドでコンポーネントにアクセスできない。
        getFrame().setVisible(true);
    }
    
    /**
     * このチャートのプラグインコレクションを返す。
     * @return プラグインのコレクション
     */
    public Collection listChildren() {
        return null;
    }
    
    /**
     * このチャートのプラグイン名コレクションを返す。
     * @return プラグイン名のコレクション
     */
    public Collection listChildrenNames() {
        return null;
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
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * ドキュメントタブを生成する。
     */
    private JTabbedPane loadDocuments() {
        
        // プラグインコンテキストを得る
        IPluginContext plctx = ClientContext.getPluginContext();
        
        try {
            // プラグポイントのプラグインコレクションを得る
            Collection<PluginReference> c = plctx.listPluginReferences(DOCUMENT_PLUG_POINT);
            documents = new ArrayList<PluginReference>(c);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        //
        // 実際に生成（アクティブ）されている Document plugin を保持するテーブル
        //
        activeChildren = new Hashtable<String, IChartDocument>();
        
        //
        // Document を格納するタブペイン
        //
        JTabbedPane tab = new JTabbedPane();
        
        //
        // index = 0 のプラグインを生成しタブに加える
        // 
        try {
            PluginReference plf = documents.get(0);
            IChartDocument plugin = (IChartDocument) plctx.lookup(plf.getJndiName());
            String title = (String) plf.getAddrContent(PluginReference.TITLE);
            plugin.setContext(this);
            plugin.setTitle(title);
            plugin.initialize();
            plugin.start();
            tab.addTab(title, plugin.getUI());
            activeChildren.put(String.valueOf(0), plugin);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // ２番目以降のプラグインは遅延生成するためタイトルのみを設定する
        int index = 0;
        for (PluginReference plref : documents) {
            if (index++ > 0) {
                tab.addTab((String) plref.getAddrContent(PluginReference.TITLE), null);
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
        final JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        final int index = tabbedPane.getSelectedIndex();
        String key = String.valueOf(index);
        IChartDocument docPlugin = (IChartDocument) activeChildren.get(key);

        if (docPlugin == null) {
            //
            // まだ生成されていないプラグインを生成する
            //
            try {
                PluginReference plf = documents.get(index);
                IPluginContext plctx = ClientContext.getPluginContext();
                final IChartDocument plugin = (IChartDocument) plctx.lookup(plf.getJndiName());
                plugin.setContext(ChartPlugin.this);
                plugin.setTitle((String) plf.getAddrContent(PluginReference.TITLE));

                // 2005-09-21 plugin の initialize と start を分離した
                // initialize でコンポーネントを初期化しそれが表示された後にstartでデータを取得する
                // このパターンをとるもの 病名、ラボテスト、ケアマップ
                Runnable r = new Runnable() {
                    public void run() {
                        plugin.initialize();
                        Runnable awt = new Runnable() {
                            public void run() {
                                tabbedPane.setComponentAt(index, plugin.getUI());
                                plugin.start();
                                activeChildren.put(String.valueOf(index), plugin);
                            }
                        };
                        EventQueue.invokeLater(awt);
                    }
                };
                Thread t = new Thread(r);
                t.setPriority(Thread.NORM_PRIORITY);
                t.start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            //
            // 既に生成済みプラグインの場合は enter() をコールする
            //
            docPlugin.enter();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 新規カルテのモデルを生成する。
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
        
        //
        // 診療科を設定する
        // 受付情報から得ている
        //
        String dept = params.getDepartment();
        docInfo.setDepartmentDesc(dept); // department
        
        // 診療科コード
        // 受付からとっていない場合....
        String deptCode = params.getDepartmentCode();
        if (deptCode == null) {
            docInfo.setDepartment(MMLTable.getDepartmentCode(dept)); // dept.code
        }
        
        // 健康保険を設定する
        PVTHealthInsuranceModel insurance = params.getPVTHealthInsurance();
        docInfo.setHealthInsurance(insurance.getInsuranceClassCode());
        docInfo.setHealthInsuranceDesc(insurance.toString());
        //docInfo.setHealthInsuranceDesc(insurance.getInsuranceClass());
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
        boolean applyRp = params.getCreateMode() == IChart.NewKarteMode.APPLY_RP ? true : false;
        copyModel(oldModel, newModel, applyRp);
        
        //
        // 新規カルテの DocInfo を設定する
        //
        DocInfoModel docInfo = newModel.getDocInfo();
        
        // 文書ID
        docInfo.setDocId(GUIDGenerator.generate(docInfo));
        
        // 生成目的
        docInfo.setPurpose(PURPOSE_RECORD);
        
        //
        // 診療科を設定する 受付情報から設定する
        //
        String dept = params.getDepartment();
        docInfo.setDepartmentDesc(dept);
        
        // 診療科コード
        // 受付からとっていない場合....
        String deptCode = params.getDepartmentCode();
        if (deptCode == null) {
            docInfo.setDepartment(MMLTable.getDepartmentCode(dept)); // dept.code
        }
        
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
        try {
            IPluginContext pluginCtx = ClientContext.getPluginContext();
            KarteEditor editor = (KarteEditor) pluginCtx.lookup(KARTE_EDITOR_JNDI);
            editor.addMMLListner(mmlListener); // Listeners to send XML
            editor.addCLAIMListner(claimListener);
            return editor;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                        if (((ClaimBundle) model).getClassCode().equals(CLAIM_210)) {
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
    public NewKarteParams getNewKarteParams(IChart.NewKarteOption option, JFrame frame, String dept, String deptCode, String insuranceUid) {
        
        NewKarteParams params = new NewKarteParams(option);
        params.setDepartment(dept);
        params.setDepartmentCode(deptCode);
        
        // 自費保険を追加 2006-05-01 廃止
        // PVTHealthInsuranceModel self = new PVTHealthInsuranceModel();
        // self.setInsuranceClass("自費");
        // self.setInsuranceClassCode("Z1");
        // self.setInsuranceClassCodeSys("MML031");
        
        //
        // 患者の健康保険コレクション
        // 
        Collection<PVTHealthInsuranceModel> insurances
                = pvt.getPatient().getPvtHealthInsurances();
        
        //
        // コレクションが null の場合は自費保険を追加する
        //
        if (insurances == null) {
            insurances = new ArrayList<PVTHealthInsuranceModel>(1);
            PVTHealthInsuranceModel model = new PVTHealthInsuranceModel();
            model.setInsuranceClass(INSURANCE_SELF);
            model.setInsuranceClassCode(INSURANCE_SELF_CODE);
            model.setInsuranceClassCodeSys(INSURANCE_SYS);
        }
        
        //
        // 保険コレクションを配列に変換し、パラメータにセットする
        // ユーザがこの中の保険を選択する
        //
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
        
        
        String text = option == IChart.NewKarteOption.BROWSER_MODIFY 
                    ? "カルテ修正" 
                    : "新規カルテ";
        
        text = ClientContext.getFrameTitle(text);
        
        JFrame parent = frame != null ? frame : getFrame();
        
        // モーダルダイアログを表示する
        NewKarteDialog od = new NewKarteDialog(parent, text);
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
        // 自費保険を追加 2006-05-01 廃止
        // PVTHealthInsuranceModel self = new PVTHealthInsuranceModel();
        // self.setInsuranceClass("自費");
        // self.setInsuranceClassCode("Z1");
        // self.setInsuranceClassCodeSys("MML031");
        
        // 患者の健康保険
        Collection<PVTHealthInsuranceModel> insurances
                = pvt.getPatient().getPvtHealthInsurances();
        
        if (insurances == null) {
            insurances = new ArrayList<PVTHealthInsuranceModel>(1);
            PVTHealthInsuranceModel model = new PVTHealthInsuranceModel();
            model.setInsuranceClass(INSURANCE_SELF);
            model.setInsuranceClassCode(INSURANCE_SELF_CODE);
            model.setInsuranceClassCodeSys(INSURANCE_SYS);
        }
        
        return (PVTHealthInsuranceModel[])insurances.toArray(new PVTHealthInsuranceModel[insurances.size()]);   
    }
    
    /**
     * タブにドキュメントを追加する。
     * @param doc 追加するドキュメント
     * @param params 追加するドキュメントの情報を保持する NewKarteParams
     */
    public void addChartDocument(IChartDocument doc, NewKarteParams params) {
        String title = getTabTitle(params.getDepartment(), params.getPVTHealthInsurance().getInsuranceClass());
        tabbedPane.addTab(title, doc.getUI());
        int index = tabbedPane.getTabCount() - 1;
        activeChildren.put(String.valueOf(index), doc);
        tabbedPane.setSelectedIndex(index);
    }
    
    /**
     * タブにドキュメントを追加する。
     * @param title タブタイトル
     */
    public void addChartDocument(IChartDocument doc, String title) {
        tabbedPane.addTab(title, doc.getUI());
        int index = tabbedPane.getTabCount() - 1;
        activeChildren.put(String.valueOf(index), doc);
        tabbedPane.setSelectedIndex(index);
    }

   /**
     * 新規カルテ用のタブタイトルを作成する
     * @param insurance 保険名
     * @return タブタイトル
     */
    public String getTabTitle(String dept, String insurance) {
        StringBuilder buf = new StringBuilder();
        buf.append("記入(");
        buf.append(dept);
        buf.append("・");
        buf.append(insurance);
        buf.append(")");
        return buf.toString();
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
                    IChartDocument doc = (IChartDocument) activeChildren.get(String.valueOf(undoc.getIndex()));
                    if (doc != null && doc.isDirty()) {
                        tabbedPane.setSelectedIndex(undoc.getIndex());
                        doc.save();
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
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
            IChartDocument doc = (IChartDocument) activeChildren.get(String.valueOf(i));
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
            
            String saveAll = ClientContext.getString("chart.unsavedtask.saveText");     //"保存";
            String discard = ClientContext.getString("chart.unsavedtask.discardText");  //"破棄";
            String question = ClientContext.getString("chart.unsavedtask.question");    // 未保存のドキュメントがあります。保存しますか ?
            String title = ClientContext.getString("chart.unsavedtask.title");          // 未保存処理
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
                    new String[] { saveAll, discard, cancelText },
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
    
    /**
     * このチャートを終了する。Frame の後始末をする。
     */
    public void stop() {
        if (activeChildren != null) {
            for (Iterator<String> iter = activeChildren.keySet().iterator(); iter.hasNext(); ) {
                IChartDocument doc = activeChildren.get(iter.next());
                if (doc != null) {
                    doc.stop();
                }
            }
            activeChildren.clear();
        }
        documents.clear();
        mediator.dispose();
        inspector.dispose();
        getFrame().setVisible(false);
        getFrame().setJMenuBar(null);
        getFrame().dispose();
    }
    
    //////////////////// State Mgr /////////////////////////////////////////////
    
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
    public static ArrayList<ChartPlugin> getAllChart() {
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
    public static void windowOpened(ChartPlugin opened) {
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
        boundSupport.firePropertyChange(ChartPlugin.CHART_STATE, null, model);
    }
    
    /**
     * チャートウインドウのクローズを通知する。
     * @param closed クローズした ChartPlugin
     */
    public static void windowClosed(ChartPlugin closed) {
        
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
            boundSupport.firePropertyChange(ChartPlugin.CHART_STATE, null, model);
            closed = null;
        }
    }
    
    /**
     * チャート状態の変化を通知する。
     * @param 変化のあった ChartPlugin
     */
    public static void fireChanged(ChartPlugin changed) {
        PatientVisitModel model = changed.getPatientVisit();
        model.setState(changed.getChartState());
    }
}