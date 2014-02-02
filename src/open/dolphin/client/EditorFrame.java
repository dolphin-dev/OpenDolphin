/*
 * Created on 2005/07/11
 *
 */
package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.plugin.helper.ComponentMemory;
import open.dolphin.plugin.helper.WindowSupport;
import open.dolphin.project.Project;

/**
 * EditorFrame
 *
 * @author Kazushi Minagawa
 */
public class EditorFrame extends DefaultMainWindowPlugin implements IChart {
    
    // このクラスの２つのモード（状態）でメニューの制御に使用する
    public enum EditorMode {BROWSER, EDITOR};
    
    // 全インスタンスを保持するリスト
    private static List<IChart> allEditorFrames = new ArrayList<IChart>(3);
    
    // フレームサイズ関連
    private static final int FRAME_X = 25;
    private static final int FRAME_Y = 20;
    private static final int FRAME_WIDTH          = 724;
    private static final int FRAME_HEIGHT         = 740;
    private static final String TITLE_ASSIST = " - カルテ";
    
    /** このフレームの実のコンテキストチャート */
    private IChart realChart;
    
    /** このフレームに表示する KarteView オブジェクト */
    private KarteViewer view;
    
    /** このフレームに表示する KarteEditor オブジェクト */
    private KarteEditor editor;
    
    /** ToolBar パネル */
    private JPanel myToolPanel;
    
    /** スクローラコンポーネント */
    private JScrollPane scroller;
    
    /** Status パネル */
    private IStatusPanel statusPanel;
    
    /** このフレームの動作モード */
    private EditorMode mode;
    
    /** WindowSupport オブジェクト */
    private WindowSupport windowSupport;
    
    /** Mediator オブジェクト */
    private ChartMediator mediator;
    
    /** Block GlassPane */
    private BlockGlass blockGlass;
    
    /** 親チャートの位置 */
    private Point parentLoc;
    
    private JPopupMenu insurancePop;
    
    /**
     * 全インスタンスを保持するリストを返す。
     * @return 全インスタンスを保持するリスト
     */
    public static List<IChart> getAllEditorFrames() {
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
    public void setChart(IChart chartCtx) {
        this.realChart = chartCtx;
        parentLoc = realChart.getFrame().getLocation();
        super.setContext(chartCtx.getContext());
    }
    
    public IChart getChart() {
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
    
//    public void setClaimSent(boolean b) {
//        realChart.setClaimSent(b);
//    }
//
//    public boolean isClaimSent() {
//        return realChart.isClaimSent();
//    }
    
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
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        
        //
        // コンポーネントの初期化を別スレッドで行い制御を呼び出し側に返す
        //
        Runnable r = new Runnable() {
            public void run() {
                initialize();
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * 初期化する。
     */
    @SuppressWarnings("serial")
    private void initialize() {
        
        //
        // Frame を生成する
        // Frame のタイトルを
        // 患者氏名(カナ):性別:患者ID に設定する
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
        JMenuBar myMenuBar = windowSupport.getMenuBar();
        final JFrame frame = windowSupport.getFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                processWindowClosing();
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
            int x = parentLoc.x + FRAME_X;
            int y = parentLoc.y + FRAME_Y;
            Point loc = new Point(x, y);
            Dimension size = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
            ComponentMemory cm = new ComponentMemory(frame, loc, size, this);
            cm.setToPreferenceBounds();
        }
        
        blockGlass = new BlockGlass();
        frame.setGlassPane(blockGlass);
        
        //
        // Mediator が変更になる
        //
        mediator = new ChartMediator(this);
        
        //
        //  MenuBar を生成する
        //
        Object[] menuStaff = realChart.getContext().createMenuBar(myMenuBar,mediator);
        myToolPanel = (JPanel) menuStaff[1];
        frame.getContentPane().add(myToolPanel, BorderLayout.NORTH);
        
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
        insurancePop = new JPopupMenu();
        PVTHealthInsuranceModel[] insurances = ((ChartPlugin)realChart).getHealthInsurances();
        for (PVTHealthInsuranceModel hm : insurances) {
            ReflectActionListener ra = new ReflectActionListener(mediator,
                    "applyInsurance",
                    new Class[]{hm.getClass()},
                    new Object[]{hm});
            JMenuItem mi = new JMenuItem(hm.toString());
            mi.addActionListener(ra);
            insurancePop.add(mi);
        }
        
        stampBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                insurancePop.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        statusPanel = new StatusPanel();
        
        //
        // 何故か Event Dispatch スレッドで GUI の組み立てをしている
        //
        Runnable awt = new Runnable() {
            
            public void run() {
                
                if (view != null) {
                    mode = EditorMode.BROWSER;
                    view.setContext(EditorFrame.this);
                    view.initialize();
                    view.start();
                    scroller = new JScrollPane(view.getUI());
                    
                } else if (editor != null) {
                    mode = EditorMode.EDITOR;
                    editor.setContext(EditorFrame.this);
                    editor.initialize();
                    editor.start();
                    scroller = new JScrollPane(editor.getUI());
                }
                
                frame.getContentPane().add(scroller, BorderLayout.CENTER);
                frame.getContentPane().add((JPanel) statusPanel, BorderLayout.SOUTH);
                frame.setVisible(true);
            }
        };
        
        SwingUtilities.invokeLater(awt);
        
    }
    
    /**
     * プログラムを終了する。
     */
    public void stop() {
        mediator.dispose();
        allEditorFrames.remove(this);
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
            mode = EditorMode.EDITOR;
            getFrame().getContentPane().remove(scroller);
            scroller = new JScrollPane(editor.getUI());
            getFrame().getContentPane().add(scroller, BorderLayout.CENTER);
            getFrame().validate();
        }
    }
    
    /**
     * 新規カルテを作成する。
     */
    public void newKarte() {
        
        //
        // 新規カルテ作成ダイアログを表示しパラメータを得る
        //
        final ChartPlugin chart = (ChartPlugin) realChart;
        String dept = chart.getPatientVisit().getDepartment();
        String deptCode = chart.getPatientVisit().getDepartmentCode();
        String insuranceUid = chart.getPatientVisit().getInsuranceUid();
        
        NewKarteParams params = null;
        Preferences prefs = Project.getPreferences();
        
        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
            
            params = chart.getNewKarteParams(IChart.NewKarteOption.EDITOR_COPY_NEW, getFrame(), dept, deptCode, insuranceUid);
            
        } else {
            //
            // 手動でパラメータを設定する
            //
            params = new NewKarteParams(IChart.NewKarteOption.EDITOR_COPY_NEW);
            params.setDepartment(dept);
            params.setDepartmentCode(deptCode);
            
            PVTHealthInsuranceModel[] ins = chart.getHealthInsurances();
            params.setPVTHealthInsurance(ins[0]);
            
            int cMode = prefs.getInt(Project.KARTE_CREATE_MODE, 0);
            if (cMode == 0) {
                params.setCreateMode(IChart.NewKarteMode.EMPTY_NEW);
            } else if (cMode == 1) {
                params.setCreateMode(IChart.NewKarteMode.APPLY_RP);
            } else if (cMode == 2) {
                params.setCreateMode(IChart.NewKarteMode.ALL_COPY);
            }
        }
        
        if (params == null) {
            return;
        }
        
        // 編集用のモデルを得る
        DocumentModel editModel = null;
        if (params.getCreateMode() == IChart.NewKarteMode.EMPTY_NEW) {
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
                
                Runnable awt = new Runnable() {
                    public void run() {
                        editor.initialize();
                        editor.start();
                        replaceView();
                    }
                };
                
                SwingUtilities.invokeLater(awt);
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
                
                ChartPlugin chart = (ChartPlugin)realChart;
                DocumentModel editModel = chart.getKarteModelToEdit(view.getModel());
                editor = chart.createEditor();
                editor.setModel(editModel);
                editor.setEditable(true);
                editor.setContext(EditorFrame.this);
                editor.setModify(true);
                
                Runnable awt = new Runnable() {
                    public void run() {
                        editor.initialize();
                        editor.start();
                        replaceView();
                    }
                };
                
                SwingUtilities.invokeLater(awt);
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
                
                String save = ClientContext.getString("chart.unsavedtask.saveText"); //"保存";
                String discard = ClientContext.getString("chart.unsavedtask.discardText"); //"破棄";
                String question = ClientContext.getString("editoFrame.unsavedtask.question"); // 未保存のドキュメントがあります。保存しますか ?
                String title = ClientContext.getString("chart.unsavedtask.title"); // 未保存処理
                String cancelText =  (String)UIManager.get("OptionPane.cancelButtonText");
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
