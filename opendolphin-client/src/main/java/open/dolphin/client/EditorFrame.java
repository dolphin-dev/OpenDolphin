package open.dolphin.client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;
import open.dolphin.helper.WindowSupport;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;

/**
 * EditorFrame
 *
 * @author Kazushi Minagawa
 */
public class EditorFrame extends AbstractMainTool implements Chart {
    
    // このクラスの２つのモード（状態）でメニューの制御に使用する
    public enum EditorMode {BROWSER, EDITOR};
    
    // 全インスタンスを保持するリスト
    private static List<Chart> allEditorFrames = new CopyOnWriteArrayList<Chart>();

    private static final String PROP_FRMAE_BOUNDS = "editorFrame.bounds";
    
    // このフレームの実のコンテキストチャート
    private Chart realChart;
    
    // このフレームに表示する KarteView オブジェクト
    private KarteViewer view;
    
    // このフレームに表示する KarteEditor オブジェクト
    private KarteEditor editor;
    
    // ToolBar パネル
    private JPanel myToolPanel;
    
    // スクローラコンポーネント
    private JScrollPane scroller;
    
    // Status パネル
    private IStatusPanel statusPanel;
    
    // このフレームの動作モード
    private EditorMode mode;
    
    // WindowSupport オブジェクト
    private WindowSupport windowSupport;
    
    // Mediator オブジェクト
    private ChartMediator mediator;
    
    // Block GlassPane 
    private BlockGlass blockGlass;
    
    // 親チャートの位置 
    private Point parentLoc;
    
    private JPanel content;
    
    
    /**
     * 全インスタンスを保持するリストを返す。
     * @return 全インスタンスを保持するリスト
     */
    public static List<Chart> getAllEditorFrames() {
        return allEditorFrames;
    }
    
    private static PageFormat pageFormat = null;
    static {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        pageFormat = printJob.defaultPage();
    }
    
    /**
     * EditorFrame オブジェクトを生成する。
     */
    public EditorFrame() {
        allEditorFrames.add(EditorFrame.this);
    }
    
    /**
     * IChart コンテキストを設定する。
     * @param chartCtx IChart コンテキスト
     */
    public void setChart(Chart chartCtx) {
        this.realChart = chartCtx;
        parentLoc = realChart.getFrame().getLocation();
        super.setContext(chartCtx.getContext());
    }
    
    public Chart getChart() {
        return realChart;
    }
    
    /**
     * 表示する KarteViewer オブジェクトを設定する。
     * @param view 表示する KarteView
     */
    public void setKarteViewer(KarteViewer view) {
        this.view = view;
    }
    
    /**
     * 編集する KarteEditor オブジェクトを設定する。
     * @param editor 編集する KarteEditor
     */
    public void setKarteEditor(KarteEditor editor) {
        this.editor = editor;
    }
    
    /**
     * 患者モデルを返す。
     * @return 患者モデル
     */
    @Override
    public PatientModel getPatient() {
        return realChart.getPatient();
    }
    
    /**
     * 対象としている KarteBean オブジェクトを返す。
     * @return KarteBean オブジェクト
     */
    @Override
    public KarteBean getKarte() {
        return realChart.getKarte();
    }
    
    /**
     * 対象となる KarteBean オブジェクトを設定する。
     * @param karte KarteBean オブジェクト
     */
    @Override
    public void setKarte(KarteBean karte) {
        realChart.setKarte(karte);
    }
    
    /**
     * 来院情報を返す。
     * @return 来院情報
     */
    @Override
    public PatientVisitModel getPatientVisit() {
        return realChart.getPatientVisit();
    }
    
    /**
     * 来院情報を設定する。
     * @param model 来院情報モデル
     */
    @Override
    public void setPatientVisit(PatientVisitModel model) {
        realChart.setPatientVisit(model);
    }
    
    /**
     * Chart state を返す。
     * @return Chart の state 属性
     */
    @Override
    public int getChartState() {
        return realChart.getChartState();
    }
    
    /**
     * Chart state を設定する。
     * @param state Chart の state
     */
    @Override
    public void setChartState(int state) {
        realChart.setChartState(state);
    }
    
    /**
     * ReadOnly かどうかを返す。
     * @return readOnly の時 true
     */
    @Override
    public boolean isReadOnly() {
        return realChart.isReadOnly();
    }
    
    /**
     * ReadOnly 属性を設定する。
     * @param readOnly の時 true
     */
    @Override
    public void setReadOnly(boolean b) {
        realChart.setReadOnly(b);
    }
    
    /**
     * このオブジェクトの JFrame を返す。
     * @return JFrame オブジェクト
     */
    @Override
    public JFrame getFrame() {
        return windowSupport.getFrame();
    }
    
    /**
     * StatusPanel を返す。
     * @return StatusPanel
     */
    @Override
    public IStatusPanel getStatusPanel() {
        return this.statusPanel;
    }
    
    /**
     * StatusPanel を設定する。
     * @param statusPanel StatusPanel オブジェクト
     */
    @Override
    public void setStatusPanel(IStatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }
    
    /**
     * ChartMediator を返す。
     * @return ChartMediator
     */
    @Override
    public ChartMediator getChartMediator() {
        return mediator;
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
     * DocumentHistory を返す。
     * @return DocumentHistory
     */
    @Override
    public DocumentHistory getDocumentHistory() {
        return realChart.getDocumentHistory();
    }
    
    /**
     * 引数のタブ番号にあるドキュメントを表示する。
     * @param index 表示するドキュメントのタブ番号
     */
    @Override
    public void showDocument(int index) {
        realChart.showDocument(index);
    }
    
    /**
     * dirty かどうかを返す。
     * @return dirty の時 true
     */
    @Override
    public boolean isDirty() {
        return (mode == EditorMode.EDITOR) ? editor.isDirty() : false;
    }
    
    @Override
    public PVTHealthInsuranceModel[] getHealthInsurances() {
        return realChart.getHealthInsurances();
    }

    @Override
    public PVTHealthInsuranceModel getHealthInsuranceToApply(String uuid) {
        return realChart.getHealthInsuranceToApply(uuid);
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        initialize();
    }
    
    /**
     * 初期化する。
     */
    private void initialize() {

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        
        // Frame を生成する
        // Frame のタイトルを
        // 患者氏名(カナ):性別:患者ID に設定する
        String karteStr = resource.getString("karteStr");
        StringBuilder sb = new StringBuilder();
        sb.append(getPatient().getFullName());
        sb.append("(");
        String kana = getPatient().getKanaName();
        kana = kana.replace("　", " ");
        sb.append(kana);
        sb.append(")");
        sb.append(" : ");
        sb.append(getPatient().getPatientId());
        sb.append(karteStr);
        
        windowSupport = WindowSupport.create(sb.toString());
        
        JMenuBar myMenuBar = windowSupport.getMenuBar();
        
        JFrame frame = windowSupport.getFrame();
        frame.setName("editorFrame");
        content = new JPanel(new BorderLayout());
        
        // Mediator が変更になる
        mediator = new ChartMediator(this);
        
        //  MenuBar を生成する
        AbstractMenuFactory appMenu = AbstractMenuFactory.getFactory();
        appMenu.setMenuSupports(realChart.getContext().getMenuSupport(), mediator);
        appMenu.build(myMenuBar);
        mediator.registerActions(appMenu.getActionMap());
        myToolPanel = appMenu.getToolPanelProduct();
        content.add(myToolPanel, BorderLayout.NORTH);
        
        // このクラス固有のToolBarを生成する
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
                if (mediator.getActions().get(GUIConst.ACTION_INSERT_TEXT).isEnabled()) {
                    JPopupMenu popup = new JPopupMenu();
                    mediator.addTextMenu(popup);
                    if (!e.isPopupTrigger()) {
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
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
                if (mediator.getActions().get(GUIConst.ACTION_INSERT_SCHEMA).isEnabled()) {
                    getContext().showSchemaBox();
                }
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
                if (mediator.getActions().get(GUIConst.ACTION_INSERT_STAMP).isEnabled()) {
                    JPopupMenu popup = new JPopupMenu();
                    mediator.addStampMenu(popup);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        toolBar.add(stampBtn);
        
        // 保険選択ツールを生成する
        // 保険選択ツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_SELECT_INSURANCE);
        JButton insBtn = new JButton();
        insBtn.setName("insBtn");
        insBtn.setAction(action);
        insBtn.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (mediator.getActions().get(GUIConst.ACTION_SELECT_INSURANCE).isEnabled()) {
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
            }
        });
        toolBar.add(insBtn);

        // Status 情報
        setStatusPanel(new StatusPanel(false));
        getStatusPanel().setRightInfo(getPatient().getPatientId());
        getStatusPanel().setLeftInfo(getPatient().getFullName());
        
        if (view != null) {
            mode = EditorMode.BROWSER;
            view.setContext(EditorFrame.this); // context
            view.start();
            scroller = new JScrollPane(view.getUI());
            mediator.enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);

        } else if (editor != null) {
            mode = EditorMode.EDITOR;
            editor.setContext(EditorFrame.this); // context
            editor.initialize();
            editor.start();
            scroller = new JScrollPane(editor.getUI());
            mediator.enabledAction(GUIConst.ACTION_NEW_KARTE, false);
            mediator.enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);
        }

        content.add(scroller, BorderLayout.CENTER);
        frame.getContentPane().setLayout(new BorderLayout(0, 7));
        frame.getContentPane().add(content, BorderLayout.CENTER);
        frame.getContentPane().add((JPanel) statusPanel, BorderLayout.SOUTH);
        // Injection
        textBtn.setIcon(ClientContext.getImageIcon(resource.getString("textBtn.icon")));
        textBtn.setText(null);
        textBtn.setToolTipText(resource.getString("textBtn.toolTipText"));
        textBtn.setMargin(new Insets(5,5,5,5));

        schemaBtn.setIcon(ClientContext.getImageIcon(resource.getString("schemaBtn.icon")));
        schemaBtn.setText(null);
        schemaBtn.setToolTipText(resource.getString("schemaBtn.toolTipText"));
        schemaBtn.setMargin(new Insets(5,5,5,5));

        stampBtn.setIcon(ClientContext.getImageIcon(resource.getString("stampBtn.icon")));
        stampBtn.setText(null);
        stampBtn.setToolTipText(resource.getString("stampBtn.toolTipText"));
        stampBtn.setMargin(new Insets(5,5,5,5));

//        chgBtn.setIcon(ClientContext.getImageIcon(resource.getString("chgBtn.icon")));
//        chgBtn.setText(null);
//        chgBtn.setToolTipText(resource.getString("chgBtn.toolTipText"));
//        chgBtn.setMargin(new Insets(5,5,5,5));

        insBtn.setIcon(ClientContext.getImageIcon(resource.getString("insBtn.icon")));
        insBtn.setText(null);
        insBtn.setToolTipText(resource.getString("insBtn.toolTipText"));
        insBtn.setMargin(new Insets(5,5,5,5));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!blockGlass.isVisible()) {
                    processWindowClosing();
                }
            }
        });
        
        blockGlass = new BlockGlass();
        frame.setGlassPane(blockGlass);

        // デフォルト値を用意して userDefaults から読み込む
        int x = Integer.parseInt(resource.getString("frameX"));
        int y = Integer.parseInt(resource.getString("frameY"));
        int width = Integer.parseInt(resource.getString("frameWidth"));
        int height = Integer.parseInt(resource.getString("frameHeight"));
        Rectangle defRect = new Rectangle(x, y, width, height);
        Rectangle bounds = Project.getRectangle(PROP_FRMAE_BOUNDS, defRect);

        frame.setBounds(bounds);
        windowSupport.getFrame().setVisible(true);

        Runnable awt = new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.getUI().scrollRectToVisible(new Rectangle(0,0,view.getUI().getWidth(), 50));
                } else if (editor != null) {
                    editor.getUI().scrollRectToVisible(new Rectangle(0,0,editor.getUI().getWidth(), 50));
                }
            }
        };
        EventQueue.invokeLater(awt);

    }
    
    /**
     * プログラムを終了する。
     */
    @Override
    public void stop() {
        mediator.dispose();
        allEditorFrames.remove(this);
        Project.setRectangle(PROP_FRMAE_BOUNDS, getFrame().getBounds());
        getFrame().setVisible(false);
        getFrame().dispose();
    }
    
    /**
     * ウインドウの close box が押された時の処理を実行する。
     */
    public void processWindowClosing() {
        close();
    }
    
    /**
     * ウインドウオープン時の処理を行う。
     */
    public void processWindowOpened() {
    }
    
    /**
     * Focus ゲインを得た時の処理を行う。
     */
    public void processGainedFocus() {
        
        switch (mode) {
            case BROWSER:
                if (view != null) {
                    view.enter();
                }
                break;
                
            case EDITOR:
                if (editor != null) {
                    editor.enter();
                }
                break;
        }
    }
    
    /**
     * コンテンツを KarteView から KarteEditor に切り替える。
     * Viewerの状態からエディターの状態へ切り替える。
     */
    private void replaceView() {
        if (editor != null) {
            // Editor Frame の時、
            // 新規カルテとドキュメントは不可とする
            mediator.enabledAction(GUIConst.ACTION_NEW_KARTE, false);
            mediator.enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);
            mode = EditorMode.EDITOR;
            content.remove(scroller);
            scroller = new JScrollPane(editor.getUI());
            content.add(scroller, BorderLayout.CENTER);
            getFrame().validate();
        }
    }

    @Override
    public DocumentModel getKarteModelToEdit(NewKarteParams params) {
        return realChart.getKarteModelToEdit(params);
    }

    @Override
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel, NewKarteParams params) {
        return realChart.getKarteModelToEdit(oldModel, params);
    }

    @Override
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel) {
        return realChart.getKarteModelToEdit(oldModel);
    }

    @Override
    public MmlMessageListener getMMLListener() {
        return realChart.getMMLListener();
    }

    @Override
    public ClaimMessageListener getCLAIMListener() {
        return realChart.getCLAIMListener();
    }

    @Override
    public boolean isSendClaim() {
        return realChart.isSendClaim();
    }

    @Override
    public boolean isSendLabtest() {
        return realChart.isSendLabtest();
    }

    /**
     * 新規カルテを作成する。
     */    
    public void newKarte() {
        
        // 新規カルテ作成ダイアログを表示しパラメータを得る
        String docType = view.getModel().getDocInfoModel().getDocType();
        
        final ChartImpl chart = (ChartImpl) realChart;

        String insuranceUid = chart.getPatientVisit().getInsuranceUid();
        String dept = chart.getPatientVisit().getDeptName();
        String deptCode = chart.getPatientVisit().getDeptCode();
        
        NewKarteParams params;
        
        if (Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
            
            params = chart.getNewKarteParams(docType,Chart.NewKarteOption.EDITOR_COPY_NEW, getFrame(), dept, deptCode, insuranceUid);
            
        } else {
            //
            // 手動でパラメータを設定する
            //
            params = new NewKarteParams(Chart.NewKarteOption.EDITOR_COPY_NEW);
            params.setDocType(docType);
            params.setDepartmentName(dept);
            params.setDepartmentCode(deptCode);
            
            PVTHealthInsuranceModel[] ins = chart.getHealthInsurances();
            params.setPVTHealthInsurance(ins[0]);
            
            int cMode = Project.getInt(Project.KARTE_CREATE_MODE, 0);
            if (cMode == 0) {
                params.setCreateMode(Chart.NewKarteMode.EMPTY_NEW);
            } else if (cMode == 1) {
                params.setCreateMode(Chart.NewKarteMode.APPLY_RP);
            } else if (cMode == 2) {
                params.setCreateMode(Chart.NewKarteMode.ALL_COPY);
            }
        }
        
        if (params == null) {
            return;
        }
        
        // 編集用のモデルを得る
        DocumentModel editModel;
        
        if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
            editModel = getKarteModelToEdit(params);
        } else {
            editModel = getKarteModelToEdit(view.getModel(), params);
        }
        
        final DocumentModel theModel = editModel;
        
        Runnable r = new Runnable() {
            
            @Override
            public void run() {
                
                editor = chart.createEditor();
                editor.setModel(theModel);
                editor.setEditable(true);
                editor.setContext(EditorFrame.this);
                editor.setMode(KarteEditor.DOUBLE_MODE);
                
                Runnable awt = new Runnable() {
                    @Override
                    public void run() {
                        editor.initialize();
                        editor.start();
                        replaceView();
                    }
                };
                
                EventQueue.invokeLater(awt);
            }
        };
        
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
        
    /**
     * カルテを修正する。
     */
    public void modifyKarte() {
        
        Runnable r = new Runnable() {
            
            @Override
            public void run() {
                
                ChartImpl chart = (ChartImpl)realChart;
                DocumentModel editModel = getKarteModelToEdit(view.getModel());
                editor = chart.createEditor();
                editor.setModel(editModel);
                editor.setEditable(true);
                editor.setContext(EditorFrame.this);
                editor.setModify(true);
                String docType = editModel.getDocInfoModel().getDocType();
                int mode = docType.equals(IInfoModel.DOCTYPE_KARTE) ? KarteEditor.DOUBLE_MODE : KarteEditor.SINGLE_MODE;
                editor.setMode(mode);
                
                Runnable awt = new Runnable() {
                    @Override
                    public void run() {
                        editor.initialize();
                        editor.start();
                        replaceView();
                    }
                };
                
                EventQueue.invokeLater(awt);
            }
        };
        
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    private PageFormat getPageFormat() {
        return realChart.getContext().getPageFormat();
    }
    
    /**
     * 印刷する。
     */
    public void print() {
        
        switch (mode) {
            
            case BROWSER:
                if (view != null) {
                    view.printPanel2(getPageFormat());
                }
                break;
                
            case EDITOR:
                if (editor != null) {
                    editor.printPanel2(getPageFormat());
                }
                break;
        }
    }
    
    /**
     * クローズする。
     */
    @Override
    public void close() {
        
        if (mode == EditorMode.EDITOR) {
            
            if (editor.isDirty()) {
                ResourceBundle resource = ClientContext.getBundle(this.getClass());
                String save = resource.getString("unsavedtask.saveText"); //"保存";
                String discard = resource.getString("unsavedtask.discardText"); //"破棄";
                String question = resource.getString("unsavedtask.question"); // 未保存のドキュメントがあります。保存しますか ?
                String title = resource.getString("unsavedtask.title"); // 未保存処理
                String cancelText =  (String) UIManager.get("OptionPane.cancelButtonText");
                int option = JOptionPane.showOptionDialog(
                        getFrame(),
                        question,
                        ClientContext.getFrameTitle(title),
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{save, discard, cancelText},
                        save
                        );
                
                switch (option) {
                    
                    case 0:
                        editor.save();
                        break;
                        
                    case 1:
                        stop();
                        break;
                        
                    case 2:
                        break;
                }
                
            } else {
                stop();
            }
            
        } else {
            stop();
        }
    }
}
