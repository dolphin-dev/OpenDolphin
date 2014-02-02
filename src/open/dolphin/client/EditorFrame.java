package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.helper.WindowSupport;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.project.Project;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 * EditorFrame
 *
 * @author Kazushi Minagawa
 */
public class EditorFrame extends AbstractMainTool implements Chart {
    
    // このクラスの２つのモード（状態）でメニューの制御に使用する
    public enum EditorMode {BROWSER, EDITOR};
    
    // 全インスタンスを保持するリスト
    private static List<Chart> allEditorFrames = new ArrayList<Chart>(3);
    
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
    
    //private JPopupMenu insurancePop;
    
    private ResourceMap resMap;
    
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
        if (printJob != null && pageFormat == null) {
            // set default format
            pageFormat = printJob.defaultPage();
        }
    }
    
    /**
     * EditorFrame オブジェクトを生成する。
     */
    public EditorFrame() {
        allEditorFrames.add(this);
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
    public PatientModel getPatient() {
        return realChart.getPatient();
    }
    
    /**
     * 対象としている KarteBean オブジェクトを返す。
     * @return KarteBean オブジェクト
     */
    public KarteBean getKarte() {
        return realChart.getKarte();
    }
    
    /**
     * 対象となる KarteBean オブジェクトを設定する。
     * @param karte KarteBean オブジェクト
     */
    public void setKarte(KarteBean karte) {
        realChart.setKarte(karte);
    }
    
    /**
     * 来院情報を返す。
     * @return 来院情報
     */
    public PatientVisitModel getPatientVisit() {
        return realChart.getPatientVisit();
    }
    
    /**
     * 来院情報を設定する。
     * @param model 来院情報モデル
     */
    public void setPatientVisit(PatientVisitModel model) {
        realChart.setPatientVisit(model);
    }
    
    /**
     * Chart state を返す。
     * @return Chart の state 属性
     */
    public int getChartState() {
        return realChart.getChartState();
    }
    
    /**
     * Chart state を設定する。
     * @param state Chart の state
     */
    public void setChartState(int state) {
        realChart.setChartState(state);
    }
    
    /**
     * ReadOnly かどうかを返す。
     * @return readOnly の時 true
     */
    public boolean isReadOnly() {
        return realChart.isReadOnly();
    }
    
    /**
     * ReadOnly 属性を設定する。
     * @param readOnly の時 true
     */
    public void setReadOnly(boolean b) {
        realChart.setReadOnly(b);
    }
    
    /**
     * このオブジェクトの JFrame を返す。
     * @return JFrame オブジェクト
     */
    public JFrame getFrame() {
        return windowSupport.getFrame();
    }
    
    /**
     * StatusPanel を返す。
     * @return StatusPanel
     */
    public IStatusPanel getStatusPanel() {
        return this.statusPanel;
    }
    
    /**
     * StatusPanel を設定する。
     * @param statusPanel StatusPanel オブジェクト
     */
    public void setStatusPanel(IStatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }
    
    /**
     * ChartMediator を返す。
     * @return ChartMediator
     */
    public ChartMediator getChartMediator() {
        return mediator;
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
     * DocumentHistory を返す。
     * @return DocumentHistory
     */
    public DocumentHistory getDocumentHistory() {
        return realChart.getDocumentHistory();
    }
    
    /**
     * 引数のタブ番号にあるドキュメントを表示する。
     * @param index 表示するドキュメントのタブ番号
     */
    public void showDocument(int index) {
        realChart.showDocument(index);
    }
    
    /**
     * dirty かどうかを返す。
     * @return dirty の時 true
     */
    public boolean isDirty() {
        return (mode == EditorMode.EDITOR) ? editor.isDirty() : false;
    }
    
    public PVTHealthInsuranceModel[] getHealthInsurances() {
        return realChart.getHealthInsurances();
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        initialize();
    }
    
    /**
     * 初期化する。
     */
    private void initialize() {
        
        // ResourceMap を保存する
        resMap = ClientContext.getResourceMap(EditorFrame.class);
        
        //
        // Frame を生成する
        // Frame のタイトルを
        // 患者氏名(カナ):性別:患者ID に設定する
        String karteStr = resMap.getString("karteStr");
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
        
        //
        // Mediator が変更になる
        //
        mediator = new ChartMediator(this);
        
        //
        //  MenuBar を生成する
        //
        AbstractMenuFactory appMenu = AbstractMenuFactory.getFactory();
        appMenu.setMenuSupports(realChart.getContext().getMenuSupport(), mediator);
        appMenu.build(myMenuBar);
        mediator.registerActions(appMenu.getActionMap());
        myToolPanel = appMenu.getToolPanelProduct();
        content.add(myToolPanel, BorderLayout.NORTH);
        
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
        
        statusPanel = new StatusPanel();
        
        if (view != null) {
            mode = EditorMode.BROWSER;
            view.setContext(EditorFrame.this);
            view.start();
            scroller = new JScrollPane(view.getUI());
            mediator.enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);

        } else if (editor != null) {
            mode = EditorMode.EDITOR;
            editor.setContext(EditorFrame.this);
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
        resMap.injectComponents(frame);
        
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                processWindowClosing();
            }
        });
        
        blockGlass = new BlockGlass();
        frame.setGlassPane(blockGlass);
        
        // Frame の大きさをストレージからロードする
        Rectangle bounds = null;
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        try {
            bounds = (Rectangle) appCtx.getLocalStorage().load("editorFrameBounds.xml");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (bounds == null) {
            int x = resMap.getInteger("frameX").intValue();
            int y = resMap.getInteger("frameY").intValue();
            int width = resMap.getInteger("frameWidth").intValue();
            int height = resMap.getInteger("frameHeight").intValue();
            bounds = new Rectangle(x, y, width, height);
        }
        frame.setBounds(bounds);
        windowSupport.getFrame().setVisible(true);
        
        Runnable awt = new Runnable() {      
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
    public void stop() {
        mediator.dispose();
        allEditorFrames.remove(this);
        try {
            ClientContext.getLocalStorage().save(getFrame().getBounds(), "editorFrameBounds.xml");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
    
    /**
     * 新規カルテを作成する。
     */
//    public void newKarte() {
//        
//        // 新規カルテ作成ダイアログを表示しパラメータを得る
//        String docType = view.getModel().getDocInfo().getDocType();
//        
//        final ChartImpl chart = (ChartImpl) realChart;
//        String dept = chart.getPatientVisit().getDepartment();
//        String deptCode = chart.getPatientVisit().getDepartmentCode();
//        String insuranceUid = chart.getPatientVisit().getInsuranceUid();
//        
//        NewKarteParams params = null;
//        Preferences prefs = Project.getPreferences();
//        
//        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
//            
//            params = chart.getNewKarteParams(docType,Chart.NewKarteOption.EDITOR_COPY_NEW, getFrame(), dept, deptCode, insuranceUid);
//            
//        } else {
//            //
//            // 手動でパラメータを設定する
//            //
//            params = new NewKarteParams(Chart.NewKarteOption.EDITOR_COPY_NEW);
//            params.setDocType(docType);
//            params.setDepartment(dept);
//            params.setDepartmentCode(deptCode);
//            
//            PVTHealthInsuranceModel[] ins = chart.getHealthInsurances();
//            params.setPVTHealthInsurance(ins[0]);
//            
//            int cMode = prefs.getInt(Project.KARTE_CREATE_MODE, 0);
//            if (cMode == 0) {
//                params.setCreateMode(Chart.NewKarteMode.EMPTY_NEW);
//            } else if (cMode == 1) {
//                params.setCreateMode(Chart.NewKarteMode.APPLY_RP);
//            } else if (cMode == 2) {
//                params.setCreateMode(Chart.NewKarteMode.ALL_COPY);
//            }
//        }
//        
//        if (params == null) {
//            return;
//        }
//        
//        
//        // 編集用のモデルを得る
//        DocumentModel editModel = null;
//        
//        // シングルドキュメントを生成する場合
//        if (params.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
//            //logger.debug("シングルドキュメントを生成する");
//            // Baseになるカルテがあるかどうかでモデルの生成が異なる
//            if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
//                //logger.debug("empty new is selected");
//                editModel = chart.getKarteModelToEdit(params);
//            } else {
//                //logger.debug("copy new is selected");
//                editModel = chart.getKarteModelToEdit(view.getModel(), params);
//            }
//            
//        } else {
//            //logger.debug("2号カルテを生成する");
//            // Baseになるカルテがあるかどうかでモデルの生成が異なる
//            if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
//                //logger.debug("empty new is selected");
//                editModel = chart.getKarteModelToEdit(params);
//            } else {
//                //logger.debug("copy new is selected");
//                editModel = chart.getKarteModelToEdit(view.getModel(), params);
//            }
//        }
//        
//        final DocumentModel theModel = editModel;
//        
//        Runnable r = new Runnable() {
//            
//            public void run() {
//                
//                editor = chart.createEditor();
//                editor.setModel(theModel);
//                editor.setEditable(true);
//                editor.setContext(EditorFrame.this);
//                if (theModel.getDocInfo().getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
//                    editor.setMode(KarteEditor.SINGLE_MODE);
//                } else {
//                    editor.setMode(KarteEditor.DOUBLE_MODE);
//                }
//                
//                Runnable awt = new Runnable() {
//                    public void run() {
//                        editor.initialize();
//                        editor.start();
//                        replaceView();
//                    }
//                };
//                
//                EventQueue.invokeLater(awt);
//            }
//        };
//        
//        Thread t = new Thread(r);
//        t.setPriority(Thread.NORM_PRIORITY);
//        t.start();
//    }
    
    /**
     * 新規カルテを作成する。
     */    
    public void newKarte() {
        
        // 新規カルテ作成ダイアログを表示しパラメータを得る
        String docType = view.getModel().getDocInfo().getDocType();
        
        final ChartImpl chart = (ChartImpl) realChart;
        String dept = chart.getPatientVisit().getDepartment();
        String deptCode = chart.getPatientVisit().getDepartmentCode();
        String insuranceUid = chart.getPatientVisit().getInsuranceUid();
        
        NewKarteParams params = null;
        Preferences prefs = Project.getPreferences();
        
        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
            
            params = chart.getNewKarteParams(docType,Chart.NewKarteOption.EDITOR_COPY_NEW, getFrame(), dept, deptCode, insuranceUid);
            
        } else {
            //
            // 手動でパラメータを設定する
            //
            params = new NewKarteParams(Chart.NewKarteOption.EDITOR_COPY_NEW);
            params.setDocType(docType);
            params.setDepartment(dept);
            params.setDepartmentCode(deptCode);
            
            PVTHealthInsuranceModel[] ins = chart.getHealthInsurances();
            params.setPVTHealthInsurance(ins[0]);
            
            int cMode = prefs.getInt(Project.KARTE_CREATE_MODE, 0);
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
        DocumentModel editModel = null;
        
        if (params.getCreateMode() == Chart.NewKarteMode.EMPTY_NEW) {
            editModel = chart.getKarteModelToEdit(params);
        } else {
            editModel = chart.getKarteModelToEdit(view.getModel(), params);
        }
        
        final DocumentModel theModel = editModel;
        
        Runnable r = new Runnable() {
            
            public void run() {
                
                editor = chart.createEditor();
                editor.setModel(theModel);
                editor.setEditable(true);
                editor.setContext(EditorFrame.this);
                editor.setMode(KarteEditor.DOUBLE_MODE);
                
                Runnable awt = new Runnable() {
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
            
            public void run() {
                
                ChartImpl chart = (ChartImpl)realChart;
                DocumentModel editModel = chart.getKarteModelToEdit(view.getModel());
                editor = chart.createEditor();
                editor.setModel(editModel);
                editor.setEditable(true);
                editor.setContext(EditorFrame.this);
                editor.setModify(true);
                String docType = editModel.getDocInfo().getDocType();
                int mode = docType.equals(IInfoModel.DOCTYPE_KARTE) ? KarteEditor.DOUBLE_MODE : KarteEditor.SINGLE_MODE;
                editor.setMode(mode);
                
                Runnable awt = new Runnable() {
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
    public void close() {
        
        if (mode == EditorMode.EDITOR) {
            
            if (editor.isDirty()) {
                
                String save = resMap.getString("unsavedtask.saveText"); //"保存";
                String discard = resMap.getString("unsavedtask.discardText"); //"破棄";
                String question = resMap.getString("unsavedtask.question"); // 未保存のドキュメントがあります。保存しますか ?
                String title = resMap.getString("unsavedtask.title"); // 未保存処理
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
