package open.dolphin.client;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
    private static final List<Chart> allEditorFrames = new CopyOnWriteArrayList<>();
    
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
    
    // Content panel
    private JPanel content;
    
    /**
     * 全インスタンスを保持するリストを返す。
     * @return 全インスタンスを保持するリスト
     */
    public static List<Chart> getAllEditorFrames() {
        return allEditorFrames;
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
     * Ppane にDropされた病名スタンプをリストに保存する。
     * @param dropped Ppane にDropされた病名スタンプ
     */
    @Override
    public void addDroppedDiagnosis(ModuleInfoBean dropped) {
        realChart.addDroppedDiagnosis(dropped);
    }
    
    /**
     * Ppane にDropされた病名スタンプをリストを返す。
     * @return 病名スタンプリスト
     */
    @Override
    public List<ModuleInfoBean> getDroppedDiagnosisList() {
        return realChart.getDroppedDiagnosisList();
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
     * @param b
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
     * @param name
     * @param enabled
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
    
        // Frame を生成する
        // Frame のタイトルを
        // 患者氏名(カナ):性別:患者ID に設定する
        String patientName = getPatient().getFullName();
        String kana = getPatient().getKanaName().replace("　", " ");
        String patientId = getPatient().getPatientId();
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(EditorFrame.class);
        
        String frameFormat = bundle.getString("messageFormat.frame.title");
        MessageFormat msf0 = new MessageFormat(frameFormat);
        String frameTitle = msf0.format(new Object[]{patientName,kana,patientId});
        
        windowSupport = WindowSupport.create(frameTitle);
        
        JMenuBar myMenuBar = windowSupport.getMenuBar();
        
        JFrame frame = windowSupport.getFrame();
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
        
//s.oh^ 2014/04/16 メニュー制御
        mediator.getAction(GUIConst.ACTION_EDIT_FACILITY_INFO).setEnabled(admin);
//s.oh$
        // このクラス固有のToolBarを生成する
        JToolBar toolBar = appMenu.getToolBar();
        toolBar.addSeparator();
        
        // テキストツールを生成する
        Action action = mediator.getActions().get(GUIConst.ACTION_INSERT_TEXT);
        final JToggleButton textBtn = new JToggleButton();
        textBtn.setAction(action);
        textBtn.addItemListener((ItemEvent ie) -> {
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
        });
        textBtn.setFocusable(false);
        textBtn.setBorderPainted(false);
        textBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(textBtn);
        
        // シェーマツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_INSERT_SCHEMA);
        final JToggleButton schemaBtn = new JToggleButton();
        schemaBtn.setAction(action);
        schemaBtn.addItemListener((ItemEvent ie) -> {
            if (ie.getStateChange()==ItemEvent.SELECTED) {
                if (mediator.getActions().get(GUIConst.ACTION_INSERT_SCHEMA).isEnabled()) {
                    getContext().showSchemaBox();
                }
                schemaBtn.setSelected(false);
            }
        });
        schemaBtn.setFocusable(false);
        schemaBtn.setBorderPainted(false);
        schemaBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(schemaBtn);
        
        // スタンプツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_INSERT_STAMP);
        final JToggleButton stampBtn = new JToggleButton();
        stampBtn.setAction(action);
        stampBtn.addItemListener((ItemEvent ie) -> {
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
        });
        stampBtn.setFocusable(false);
        stampBtn.setBorderPainted(false);
        stampBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(stampBtn);
        
        // 保険選択ツールを生成する
        // 保険選択ツールを生成する
        action = mediator.getActions().get(GUIConst.ACTION_SELECT_INSURANCE);
        final JToggleButton insBtn = new JToggleButton();
        insBtn.setAction(action);
        insBtn.addItemListener((ItemEvent ie) -> {
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
        });
        insBtn.setFocusable(false);
        insBtn.setBorderPainted(false);
        insBtn.setMargin(new Insets(3,3,3,3));
        toolBar.add(insBtn);
        
//s.oh^ テキストの挿入 2013/08/12
        if(Project.getString(GUIConst.ACTION_SOAPANE_INSERTTEXT_DIR, "").length() > 0) {
            toolBar.addSeparator();
            JButton insertSOATextBtn = new JButton();
            insertSOATextBtn.setAction(mediator.getActions().get("insertSOAText"));
            insertSOATextBtn.setText(null);
            String toolTipText = bundle.getString("toolTipText.insertSOABtn");
            insertSOATextBtn.setToolTipText(toolTipText);
            insertSOATextBtn.setMargin(new Insets(3,3,3,3));
            insertSOATextBtn.setFocusable(false);
            insertSOATextBtn.setBorderPainted(true);
            toolBar.add(insertSOATextBtn);
        }
        
        if(Project.getString(GUIConst.ACTION_PPANE_INSERTTEXT_DIR, "").length() > 0) {
            toolBar.addSeparator();
            JButton insertPTextBtn = new JButton();
            insertPTextBtn.setAction(mediator.getActions().get("insertPText"));
            insertPTextBtn.setText(null);
            String toolTipText = bundle.getString("toolTiptext.insertPBtn");
            insertPTextBtn.setToolTipText(toolTipText);
            insertPTextBtn.setMargin(new Insets(3,3,3,3));
            insertPTextBtn.setFocusable(false);
            insertPTextBtn.setBorderPainted(true);
            toolBar.add(insertPTextBtn);
        }
//s.oh$
        // Status 情報
        setStatusPanel(new StatusPanel(false));
        getStatusPanel().setRightInfo(getPatient().getPatientId());
        getStatusPanel().setLeftInfo(getPatient().getFullName());
        
        if (view != null) {
            mode = EditorMode.BROWSER;
            view.setContext(EditorFrame.this); // context
            view.start();
            scroller = new JScrollPane(view.getUI());
            scroller.getVerticalScrollBar().setUnitIncrement(16);
            mediator.enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);

        } else if (editor != null) {
            mode = EditorMode.EDITOR;
            editor.setContext(EditorFrame.this); // context
            editor.initialize();
            editor.start();
            scroller = editor.getScroller();
            mediator.enabledAction(GUIConst.ACTION_NEW_KARTE, false);
            mediator.enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);
        }

        content.add(scroller, BorderLayout.CENTER);
        frame.getContentPane().setLayout(new BorderLayout(0, 7));
        frame.getContentPane().add(content, BorderLayout.CENTER);
        frame.getContentPane().add((JPanel) statusPanel, BorderLayout.SOUTH);
        
        // Injection
        textBtn.setIcon(ClientContext.getImageIconArias("icon_text_stap_menu"));
        textBtn.setText(null);
        String toolTipText = bundle.getString("toolTipText.textBtn");
        textBtn.setToolTipText(toolTipText);

        schemaBtn.setIcon(ClientContext.getImageIconArias("icon_open_schema_box"));
        schemaBtn.setText(null);
        toolTipText = bundle.getString("toolTipText.schemaBtn");
        schemaBtn.setToolTipText(toolTipText);
        
        stampBtn.setIcon(ClientContext.getImageIconArias("icon_stamp_menu"));
        stampBtn.setText(null);
        toolTipText = bundle.getString("toolTipText.stampBtn");
        stampBtn.setToolTipText(toolTipText);
        
        insBtn.setIcon(ClientContext.getImageIconArias("icon_health_insurance"));        
        insBtn.setText(null);
        toolTipText = bundle.getString("toolTipText.insBtn");
        insBtn.setToolTipText(toolTipText);

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
        String frameX = bundle.getString("frame.x");
        String frameY = bundle.getString("frame.y");
        String frameWidth = bundle.getString("frame.width");
        String frameHeight = bundle.getString("frame.height");
        int x = Integer.parseInt(frameX);
        int y = Integer.parseInt(frameY);
        int width = Integer.parseInt(frameWidth);
        int height = Integer.parseInt(frameHeight);
        
        Rectangle defRect = new Rectangle(x, y, width, height);
        Rectangle bounds = Project.getRectangle("editorFrame.bounds", defRect);

        frame.setBounds(bounds);
        windowSupport.getFrame().setVisible(true);

        Runnable awt = () -> {
            if (view != null) {
                view.getUI().scrollRectToVisible(new Rectangle(0,0,view.getUI().getWidth(), 50));
            } else if (editor != null) {
                editor.getUI().scrollRectToVisible(new Rectangle(0,0,editor.getUI().getWidth(), 50));
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
        if (editor!=null) {
            editor.stop();
        }
        Project.setRectangle("editorFrame.bounds", getFrame().getBounds());
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
            //scroller = new JScrollPane(editor.getUI());
            //scroller.getVerticalScrollBar().setUnitIncrement(16);
            scroller = editor.getScroller();
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
        
        Runnable r = () -> {
            editor = chart.createEditor();
            editor.setModel(theModel);
            editor.setEditable(true);
            editor.setContext(EditorFrame.this);
            editor.setMode(KarteEditor.DOUBLE_MODE);
            
            Runnable awt = () -> {
                editor.initialize();
                editor.start();
                replaceView();
            };
            
            EventQueue.invokeLater(awt);
        };
        
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
        
    /**
     * カルテを修正する。
     */
    public void modifyKarte() {
//s.oh^ 2014/06/17 複数カルテ修正制御
        for (KarteEditor karte : KarteEditor.getAllKarte()) {
            if(karte.getContext().getPatient().getId() == realChart.getPatient().getId()) {
                if(!karte.checkModify()) {
                    return;
                }
            }
        }
//s.oh$
        
//s.oh^ 2014/08/21 修正時にアラート表示
        if(Project.getBoolean(Project.KARTE_SHOW_MODIFY_MSG)) {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            Calendar c2 = Calendar.getInstance();
            c2.setTime(view.getModel().getStarted());
            if(c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR) || c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH) || c1.get(Calendar.DATE) != c2.get(Calendar.DATE)) {

                java.util.ResourceBundle bundle = ClientContext.getMyBundle(EditorFrame.class);
                
                String cDateFmt = bundle.getString("dateFormat.started.modifyKarte");
                String question = bundle.getString("messageFormat.question.modifyKarte");
                String optionModify = bundle.getString("optionText.modify");
                String title = bundle.getString("title.optionPane.modifyKarte");
                
                SimpleDateFormat sdf = new SimpleDateFormat(cDateFmt);
                MessageFormat msft = new MessageFormat(question);
                
                String msg = msft.format(new Object[]{sdf.format(c2.getTime())});
                String[] btn = new String[]{optionModify, GUIFactory.getCancelButtonText()};
                
                int option = JOptionPane.showOptionDialog(
                        getFrame(),
                        msg,
                        ClientContext.getFrameTitle(title),
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        btn,
                        btn[1]);
                if(option != 0) {
                    return;
                }
            }
        }
//s.oh$
        
        Runnable r = () -> {
            ChartImpl chart = (ChartImpl)realChart;
            DocumentModel editModel = getKarteModelToEdit(view.getModel());
            editor = chart.createEditor();
            editor.setModel(editModel);
            editor.setEditable(true);
            editor.setContext(EditorFrame.this);
            //s.oh^ 2014/06/17 複数カルテ修正制御
            editor.setEditorFrame(EditorFrame.this);
            //s.oh$
            editor.setModify(true);
            String docType = editModel.getDocInfoModel().getDocType();
            int mode1 = docType.equals(IInfoModel.DOCTYPE_KARTE) ? KarteEditor.DOUBLE_MODE : KarteEditor.SINGLE_MODE;
            editor.setMode(mode1);
            Runnable awt = () -> {
                editor.initialize();
                editor.start();
                replaceView();
            };
            EventQueue.invokeLater(awt);
        };
        
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    private PageFormat getPageFormat() {
        return realChart.getContext().getPageFormat();
    }
    
    /**
     * Prints
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
     * Close
     */
    @Override
    public void close() {
        
        if (mode == EditorMode.EDITOR) {
            
            if (editor.isDirty()) {
                java.util.ResourceBundle bundle = ClientContext.getMyBundle(EditorFrame.class);
                String save = bundle.getString("optionText.save.unsaved");
                String discard = bundle.getString("optionText.discard.unsaved");
                String question = bundle.getString("question.unsaved");
                String title = bundle.getString("title.optionPane.unsaved");
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
                        // 破棄の場合、もし病名をDropしていればクリアする
                        if (realChart.getDroppedDiagnosisList()!=null) {
                            realChart.getDroppedDiagnosisList().clear();
                        }
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
