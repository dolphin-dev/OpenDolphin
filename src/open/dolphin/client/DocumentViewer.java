package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.project.Project;

/**
 * DocumentViewer
 *
 * @author Minagawa,Kazushi
 *
 */
public class DocumentViewer extends DefaultChartDocument {
    
    /** Busy プロパティ名 */
    public static final String BUSY_PROP = "busyProp";
    
    /** 更新を表す文字 */
    private static final String TITLE_UPDATE = "更新";
    
    // このアプリケーションは文書履歴を複数選択することができる
    // このリストはそれに対応した KarteViewer(2号カルテ)を保持している
    // このリストの内容（KarteViewer)が一枚のパネルに並べて表示される
    private List<KarteViewer> karteList;
    
    // 上記パネル内でマウスで選択されているカルテ(karteViewer)
    // 前回処方を適用した新規カルテはこの選択されたカルテが元になる
    private KarteViewer selectedKarte; // 選択されている karteViewer
    
    /** busy プリパティ */
    private boolean busy;
    
    /** 束縛サポート */
    private PropertyChangeSupport boundSupport;
    
    /** 文書履を昇順で表示する場合に true */
    private boolean ascending;
    
    /** 文書の修正履歴を表示する場合に true */
    private boolean showModified;
    
    /** Scroller  */
    private JScrollPane scroller;
    
    /** このクラスの状態マネージャ */
    private StateMgr stateMgr;
    
    /** Timer */
    private Timer taskTimer;
    
    private ArrayList<KarteViewer> removed;
    
   
    /**
     * DocumentViewerオブジェクトを生成する。
     */
    public DocumentViewer() {
    }
    
    /**
     * 束縛リスナを追加する。
     * @param prop 束縛プロパティ名
     * @param l リスナ
     */
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    /**
     * 束縛リスナを削除する。
     * @param prop 束縛プロパティ名
     * @param l リスナ
     */
    public void removePropertyChangeListener(String prop,PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    /**
     * busy かどうかを返す。
     * @return busy の時 true
     */
    public boolean isBusy() {
        return busy;
    }
    
    /**
     * busy 状態を設定する。
     * @param busy busy の時 true
     */
    public void setBusy(boolean busy) {
        boolean old = this.busy;
        this.busy = busy;
        boundSupport.firePropertyChange(BUSY_PROP, old, this.busy);
    }
    
    /**
     * GUIコンポーネントを初期化する。
     */
    public void initialize() {
        // StateMgrを生成し最初に NO_KARTE Stateにする
        stateMgr = new StateMgr();
        initComponent();
        connect();
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        enter();
    }
    
    public void stop() {
        if (karteList != null) {
            for (KarteViewer karte : karteList) {
                karte.stop();
            }
            karteList.clear();
        }
    }
    
    /**
     * タブの切り替わりにコールされ、現在のStateMgrでメニューを制御する。
     */
    public void enter() {
        super.enter();
        stateMgr.controlMenu();
    }
    
    /**
     * 選択されているKarteViwerを返す。
     * @return 選択されているKarteViwer
     */
    public KarteViewer getSelectedKarte() {
        return selectedKarte;
    }
    
    /**
     * マウスクリック(選択)されたKarteViwerをselectedKarteに設定する。
     * 他のカルテが選択されている場合はそれを解除する。
     * StateMgrを Haskarte State にする。
     * @param view 選択されたKarteViwer
     */
    public void setSelectedKarte(KarteViewer view) {
        KarteViewer old = selectedKarte;
        selectedKarte = view;
        //
        // 他のカルテが選択されている場合はそれを解除する
        //
        if (selectedKarte != old) {
            if (selectedKarte != null) {
                for (KarteViewer karte : karteList) {
                    karte.setSelected(false);
                }
                selectedKarte.setSelected(true);
                stateMgr.setHasKarte();
                
            } else {
                // null 
                stateMgr.setNoKarte();
            }
        }
    }
    
    /**
     * 新規カルテ作成の元になるカルテを返す。
     * @return 作成の元になるカルテ
     */
    private KarteViewer getBaseKarte() {
        KarteViewer ret = getSelectedKarte();
        if (ret == null) {
            if (karteList != null && karteList.size() > 0) {
                ret = ascending ? karteList.get(karteList.size() - 1) : karteList.get(0);
            }
        }
        return ret;
    }
    
    /**
     * GUIコンポーネントを初期化する。
     */
    private void initComponent() {
        scroller = new JScrollPane();
        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(scroller, BorderLayout.CENTER);
        karteList = new ArrayList<KarteViewer>(1);
    }
    
    public void historyPeriodChanged() {
        if (karteList != null) {
            karteList.clear();
        }
        scroller.setViewportView(null);
        setSelectedKarte(null);
        enter();
        getContext().showDocument(0);
    }
    
    public void documentSelectionChanged(PropertyChangeEvent e) {
        DocInfoModel[] selectedHistoroes = (DocInfoModel[]) e.getNewValue();
        if (selectedHistoroes != null && selectedHistoroes.length > 0) {
            getContext().showDocument(0);
            createAndShowKarteViewers(selectedHistoroes);
        }
    }
    
    /**
     * GUIコンポーネントにリスナを設定する。
     *
     */
    private void connect() {
        
        //
        // 文書履歴の抽出期間が更新された場合に通知を受ける
        // karteList をclear、選択されているkarteViewerを解除、sateMgrをNoKarte状態に設定する
        //
        getContext().getDocumentHistory().addPropertyChangeListener(DocumentHistory.HITORY_UPDATED, 
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "historyPeriodChanged"));
        
        //
        // 文書履歴テーブルで選択の変化があった場合に通知を受ける
        //
        getContext().getDocumentHistory().addPropertyChangeListener(DocumentHistory.SELECTED_HISTORIES, 
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "documentSelectionChanged", ""));
        
        // DocHistory を busy prop リスナにする
        // この設定は文書取得中に履歴選択をさせないために行う
        addPropertyChangeListener(BUSY_PROP, getContext().getDocumentHistory());
        
        // 文書履歴に昇順／降順、修正履歴表示の設定をする
        // この値の初期値はデフォル値であり、個々のドキュメント（画面）単位にメニューで変更できる。（適用されるのは個々のドキュメントのみ）
        // デフォルト値の設定は環境設定で行う。
        ascending = getContext().getDocumentHistory().isAscending();
        showModified = getContext().getDocumentHistory().isShowModified();
    }
    
    /**
     * KarteViewerを生成し表示する。
     *
     * @param selectedHistories 選択された文書情報 DocInfo 配列
     */
    private void createAndShowKarteViewers(DocInfoModel[] selectedHistories) {
        
        if (selectedHistories == null || selectedHistories.length == 0) {
            return;
        }
        
        // 現在のリストと比較し、新たに追加されたもの、削除されたものに分ける
        final ArrayList<DocInfoModel> added = new ArrayList<DocInfoModel>(1); // 追加されたもの
        if (removed == null) {
            removed = new ArrayList<KarteViewer>(1); // 選択が解除されているもの
        } else {
            removed.clear();
        }
        
        // 追加されたものと選択を解除されたものに分ける
        
        // 1. 選択リストにあって 現在の karteList にないものは追加する
        for (DocInfoModel selectedDocInfo : selectedHistories) {
            boolean found = false;
            for (KarteViewer viewer : karteList) {
                if (viewer.getModel().getDocInfo().equals(selectedDocInfo)) {
                    found = true;
                    break;
                }
            }
            if (found == false) {
                added.add(selectedDocInfo);
            }
        }
        
        // 2 karteList にあって選択リストにないものはkarteListから削除する
        for (KarteViewer viewer : karteList) {
            boolean found = false;
            for (DocInfoModel selectedDocInfo : selectedHistories) {
                if (viewer.getModel().getDocInfo().equals(selectedDocInfo)) {
                    found = true;
                    break;
                }
            }
            if (found == false) {
                removed.add(viewer);
            }
        }
        
        // 解除されたものがあればそれをリストから取り除く
        if (removed != null && removed.size() > 0) {
            for (KarteViewer karte : removed) {
                karteList.remove(karte);
                //karte.stop();
                //karte = null;
            }
        }
        
        // 追加されたものをデータベースから検索する
        if (added == null || added.size() == 0) {
            
            Preferences prefs = Project.getPreferences();
            boolean vsc = prefs.getBoolean(Project.KARTE_SCROLL_DIRECTION, true);
            JPanel panel = new JPanel();
            if (vsc) {
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            } else {
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            }
            //
            for (KarteViewer view : karteList) {
                if (!vsc) {
                    view.getUI().setPreferredSize(new Dimension(692, 2100));
                }
                panel.add(view.getUI());     
            }
            
            showKartePanel(panel);
            
            return;
        }
        
        //final DocInfoModel[] docInfos = new DocInfoModel[added.size()];
        //added.toArray(docInfos);
        
        // 取得する文書ID(PK)を生成し
        List<Long> docId = new ArrayList<Long>(added.size());
        for (DocInfoModel bean : added) {
            docId.add(new Long(bean.getDocPk()));
        }
        
        // データベースを検索し、karteListへ加える
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        final DocumentDelegater ddl = new DocumentDelegater();
        
        final KarteTask worker = new KarteTask(docId, ddl, maxEstimation/delay);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                statusPanel.setMessage(worker.getMessage());
                
                if (worker.isDone()) {
                    
                    statusPanel.stop();
                    taskTimer.stop();
                    
                    if (ddl.isNoError()) {
                        // 検索では KarteModel を取得する
                        List<DocumentModel> models = worker.getKarteModel();
                        // 次のメソッドで表示する
                        addKarteViewer(models, added);
                        
                    } else {
                        warning(ClientContext.getString("docHistory.title"), ddl.getErrorMessage());
                    }
                    setBusy(false);
                    
                } else if (worker.isTimeOver()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("docHistory.title");
                    new TimeoutWarning(parent, title, null).start();
                    setBusy(false);
                }
            }
        });
        setBusy(true);
        worker.start();
        statusPanel.start("");
        taskTimer.start();
    }
    
    /**
     * データベースで検索した KarteModelを Viewer で表示する。
     *
     * @param models KarteModel
     * @param docInfos DocInfo
     */
    @SuppressWarnings("unchecked")
    private void addKarteViewer(List<DocumentModel> models, List<DocInfoModel> docInfos) {
        
        if (models != null) {
            
            int index = 0;
            for (DocumentModel karteModel : models) {
                
                //System.out.println("Karte PK = " + karteModel.getId());
                karteModel.setDocInfo(docInfos.get(index++)); // ?
                
                // KarteViewer(2号カルテ)を生成する
                final KarteViewer karteViewer = new KarteViewer();
                karteViewer.setContext(getContext());
                karteViewer.setModel(karteModel);
                karteViewer.setAvoidEnter(true); // ?
                
                // このコールでモデルのレンダリングが開始される
                karteViewer.start();
                //System.out.println("Karte viwer statred");
                
                // MouseListener を生成して KarteViewer の Pane にアタッチする
                // これでダブルクリックされたカルテを別画面で表示する
                final MouseListener ml = new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        int cnt = e.getClickCount();
                        if (cnt == 2) {
                            // 選択した Karte を EditoFrame で開く
                            setSelectedKarte(karteViewer);
                            openKarte();
                        } else if (cnt == 1) {
                            setSelectedKarte(karteViewer);
                        }
                    }
                };
                // MouseListener は JTextPane へ登録する
                karteViewer.getSOAPane().getTextPane().addMouseListener(ml);
                karteViewer.getPPane().getTextPane().addMouseListener(ml);
                
                karteList.add(karteViewer);
            }
            // 時間軸でソート、viewへ通知、選択処理をする
            if (ascending) {
                Collections.sort(karteList);
            } else {
                Collections.sort(karteList, Collections.reverseOrder());
            }
            
            //
            // 選択する
            //
            if (karteList.size() > 0) {
                if (ascending) {
                    setSelectedKarte(karteList.get(karteList.size() - 1));
                } else {
                    setSelectedKarte(karteList.get(0));
                }
            }
        }

        Preferences prefs = Project.getPreferences();
        boolean vsc = prefs.getBoolean(Project.KARTE_SCROLL_DIRECTION, true);
        JPanel panel = new JPanel();
        
        if (vsc) {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        } else {
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        }
        //
        for (KarteViewer view : karteList) {
            if (!vsc) {
                view.getUI().setPreferredSize(new Dimension(692, 2100));
            }
            panel.add(view.getUI());     
        }
        
        showKartePanel(panel);
    }
    
    
    private void showKartePanel(final JPanel panel) {
        
        Runnable awt = new Runnable() {
                    
            public void run() {
                //
                // コンポーネントを実体化する
                //
                scroller.setViewportView(panel);
                
                if (removed != null) {
                    for (KarteViewer karte : removed) {
                        karte.stop();
                    }
                    removed.clear();
                }
            }
        };
        SwingUtilities.invokeLater(awt);
    }
    
    /*public boolean copyStamp() {
            return selectedKarte != null ? selectedKarte.copyStamp() : false;
    }*/
    
    //////////////// メニューメソッド /////////////
    
    /**
     * 新規カルテを作成する。
     */
    public void newKarte() {
        
        ChartPlugin chart = (ChartPlugin) getContext();
        String dept = getContext().getPatientVisit().getDepartment();
        String deptCode = getContext().getPatientVisit().getDepartmentCode();
        String insuranceUid = getContext().getPatientVisit().getInsuranceUid();
        
        
        // 新規カルテ作成時のベースになるカルテがあるか
        KarteViewer base = getBaseKarte();
        IChart.NewKarteOption option = base != null
                ? IChart.NewKarteOption.BROWSER_COPY_NEW
                : IChart.NewKarteOption.BROWSER_NEW;
        
        //
        // 新規カルテ作成時に確認ダイアログを表示するかどうか
        //
        NewKarteParams params = null;
        Preferences prefs = Project.getPreferences();
        
        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
        
            // 新規カルテダイアログへパラメータを渡し、コピー新規のオプションを制御する
            params = chart.getNewKarteParams(option, getContext().getFrame(), dept, deptCode, insuranceUid);
            
        } else {
            // 保険、作成モード、配置方法を手動で設定する
            params = new NewKarteParams(option);
            params.setDepartment(dept);
            params.setDepartmentCode(deptCode);
            
            //
            // 保険
            //
            PVTHealthInsuranceModel[] ins = chart.getHealthInsurances();
            params.setPVTHealthInsurance(ins[0]);
            if (insuranceUid != null) {
                int index = 0;
                for (int i = 0; i < ins.length; i++) {
                    if (ins[i].getGUID() != null) {
                        if (insuranceUid.equals(ins[i].getGUID())) {
                            params.setPVTHealthInsurance(ins[i]);
                            break;
                        }
                    }
                }
            }
            
            //
            // 作成モード
            //
            switch (option) {
                
                case BROWSER_NEW:
                    params.setCreateMode(IChart.NewKarteMode.EMPTY_NEW);
                    break;
                    
                case BROWSER_COPY_NEW:
                    int cMode = prefs.getInt(Project.KARTE_CREATE_MODE, 0);
                    if (cMode == 0) {
                        params.setCreateMode(IChart.NewKarteMode.EMPTY_NEW);
                    } else if (cMode == 1) {
                        params.setCreateMode(IChart.NewKarteMode.APPLY_RP);
                    } else if (cMode == 2) {
                        params.setCreateMode(IChart.NewKarteMode.ALL_COPY);
                    }
                    break;
            }
            
            //
            // 配置方法
            //
            params.setOpenFrame(prefs.getBoolean(Project.KARTE_PLACE_MODE, true));
            
        }
        
        // キャンセルした場合はリターンする
        if (params == null) {
            return;
        }
        
        // Baseになるカルテがあるかどうかでモデルの生成が異なる
        DocumentModel editModel = null;
        if (params.getCreateMode() == IChart.NewKarteMode.EMPTY_NEW) {
            editModel = chart.getKarteModelToEdit(params);
        } else {
            editModel = chart.getKarteModelToEdit(base.getModel(), params);
        }
        final KarteEditor editor = chart.createEditor();
        editor.setModel(editModel);
        editor.setEditable(true);
        
        if (params.isOpenFrame()) {
            startEditorFrame(editor);
        } else {
            editor.setContext(chart);
            editor.initialize();
            editor.start();
            chart.addChartDocument(editor, params);
        }
    }
    
    /**
     * カルテを修正する。
     */
    public void modifyKarte() {
        
        if (getBaseKarte() == null) {
            return;
        }
        final ChartPlugin chart = (ChartPlugin) getContext();
        String dept = getContext().getPatientVisit().getDepartment();
        String deptCode = getContext().getPatientVisit().getDepartmentCode();
        String insuranceUid = getContext().getPatientVisit().getInsuranceUid();
        
        NewKarteParams params = null;
        Preferences prefs = Project.getPreferences();
        
        if (prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true)) {
            params = chart.getNewKarteParams(IChart.NewKarteOption.BROWSER_MODIFY, getContext().getFrame(), dept, deptCode, insuranceUid);
            
        } else {
            params = new NewKarteParams(IChart.NewKarteOption.BROWSER_MODIFY);
            params.setDepartment(dept); 
            params.setDepartmentCode(deptCode); 
            //
            // 配置方法
            //
            params.setOpenFrame(prefs.getBoolean(Project.KARTE_PLACE_MODE, true));
        }
        
        if (params == null) {
            return;
        }
        
        DocumentModel editModel = chart.getKarteModelToEdit(getBaseKarte().getModel());
        KarteEditor editor = chart.createEditor();
        editor.setModel(editModel);
        editor.setEditable(true);
        editor.setModify(true);
        
        if (params.isOpenFrame()) {
            startEditorFrame(editor);
        } else {
            editor.setContext(chart);
            editor.initialize();
            editor.start();
            chart.addChartDocument(editor, TITLE_UPDATE);
        }
    }
    
    /**
     * カルテを印刷する。
     */
    public void print() {
        KarteViewer view = getSelectedKarte();
        if (view != null) {
            view.print();
        }
    }
    
    private void startEditorFrame(KarteEditor editor) {
        EditorFrame editorFrame = new EditorFrame();
        editorFrame.setChart(getContext());
        editorFrame.setKarteEditor(editor);
        editorFrame.start();
    }
    
    /**
     * 昇順表示にする。
     */
    public void ascending() {
        ascending = true;
        getContext().getDocumentHistory().setAscending(ascending);
    }
    
    /**
     * 降順表示にする。
     */
    public void descending() {
        ascending = false;
        getContext().getDocumentHistory().setAscending(ascending);
    }
    
    /**
     * 修正履歴の表示モードにする。
     */
    public void showModified() {
        showModified = !showModified;
        getContext().getDocumentHistory().setShowModified(showModified);
    }
    
    /**
     * karteList 内でダブルクリックされたカルテ（文書）を EditorFrame で開く。
     */
    public void openKarte() {
        if (getSelectedKarte() != null) {
            EditorFrame editorFrame = new EditorFrame();
            editorFrame.setChart(getContext());
            KarteViewer view = new KarteViewer();
            view.setModel(getSelectedKarte().getModel());
            editorFrame.setKarteViewer(view);
            editorFrame.start();
        }
    }
    
    /**
     * 表示選択されているカルテを論理削除する。
     * 患者を間違えた場合等に履歴に表示されないようにするため。
     */
    public void delete() {
        
        // 対象のカルテを得る
        KarteViewer delete = getBaseKarte();
        if (delete == null) {
            return;
        }
        
        // Dialog を表示し理由を求める
        String message = "このドキュメントを削除しますか ?   ";
        final JCheckBox box1 = new JCheckBox("作成ミス");
        final JCheckBox box2 = new JCheckBox("診察キャンセル");
        final JCheckBox box3 = new JCheckBox("その他");
        box1.setSelected(true);
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (box1.isSelected() || box2.isSelected()) {
                    return;
                } else if (!box3.isSelected()){
                    box3.setSelected(true);
                }
            }
        };
        
        box1.addActionListener(al);
        box2.addActionListener(al);
        box3.addActionListener(al);
        
        Object[] msg = new Object[5];
        msg[0] = message;
        msg[1] = box1;
        msg[2] = box2;
        msg[3] = box3;
        msg[4] = new JLabel(" ");
        String deleteText = "削除する";
        String cancelText = (String) UIManager.get("OptionPane.cancelButtonText");
        
        int option = JOptionPane.showOptionDialog(
                    this.getUI(),
                    msg,
                    ClientContext.getFrameTitle("ドキュメント削除"),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[] { deleteText, cancelText },
                    cancelText);
        
        System.out.println(option);
        
        // キャンセルの場合はリターンする
        if (option != 0) {
            return;
        }
        
        //
        // 削除する status = 'D'
        //
        final long deletePk = delete.getModel().getId();
        final DocumentDelegater ddl = new DocumentDelegater();
        
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        
        final DeleteTask worker = new DeleteTask(deletePk, ddl, maxEstimation/delay);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                statusPanel.setMessage(worker.getMessage());
                
                if (worker.isDone()) {
                    
                    statusPanel.stop();
                    taskTimer.stop();
                    
                    if (ddl.isNoError()) {
                        //
                        // 文書履歴の更新を通知する
                        //
                        getContext().getDocumentHistory().getDocumentHistory();
                        
                    } else {
                        //
                        // エラーが生じている場合は警告する
                        //
                        warning(ClientContext.getString("ドキュメント削除"), ddl.getErrorMessage());
                    }
                    setBusy(false);
                    
                } else if (worker.isTimeOver()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("ドキュメント削除");
                    new TimeoutWarning(parent, title, null).start();
                    setBusy(false);
                }
            }
        });
        setBusy(true);
        worker.start();
        statusPanel.start("");
        taskTimer.start();
 
        // 検索の where 節を変更する必要がある
        // where and (status='F' or status='T')
        // where and status!='D'
    }
    
    /////////////////////////////////////////////////////////
    
    class KarteTask extends AbstractInfiniteTask {
        
        private List<DocumentModel> model;
        private DocumentDelegater ddl;
        private List<Long> docId;
        
        public KarteTask(List<Long> docId, DocumentDelegater ddl, int taskLength) {
            this.docId = docId;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        protected List<DocumentModel> getKarteModel() {
            return model;
        }
        
        protected void doTask() {
            model = ddl.getDocuments(docId);
            setDone(true);
        }
    }
    
        
    class DeleteTask extends AbstractInfiniteTask {
        
        private DocumentDelegater ddl;
        private long docPk;
        
        public DeleteTask(long docPk, DocumentDelegater ddl, int taskLength) {
            this.docPk = docPk;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        protected void doTask() {
            ddl.deleteDocument(docPk);
            setDone(true);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected abstract class BrowserState {
        
        public BrowserState() {
        }
        
        public abstract void controlMenu();
    }
    
    /**
     * 表示するカルテがない状態を表す。
     */
    protected final class NoKarteState extends BrowserState {
        
        public NoKarteState() {
        }
        
        public void controlMenu() {
            // スーパークラスとの差分のみをのみを制御する
            ChartMediator mediator = getContext().getChartMediator();
            boolean canEdit = isReadOnly() ? false : true;
            mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(canEdit); // 新規カルテ
            mediator.getAction(GUIConst.ACTION_ASCENDING).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_DESCENDING).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_SHOW_MODIFIED).setEnabled(true);
        }
    }
    
    /**
     * カルテが表示されている状態を表す。
     */
    protected final class HasKarteState extends BrowserState {
        
        public HasKarteState() {
        }
        
        public void controlMenu() {
            // スーパークラスとの差分のみをのみを制御する
            ChartMediator mediator = getContext().getChartMediator();
            
            //
            // 新規カルテが可能なケース 仮保存でないことを追加
            //
            boolean canEdit = isReadOnly() ? false : true;
            boolean tmpKarte = false;
            KarteViewer base = getBaseKarte();
            if (base != null) {
                String state = base.getModel().getDocInfo().getStatus();
                if (state.equals(IInfoModel.STATUS_TMP)) {
                    tmpKarte = true;
                }
            }
            boolean newOk = canEdit && (!tmpKarte) ? true : false;
            mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(newOk);  // 新規カルテ
            mediator.getAction(GUIConst.ACTION_MODIFY_KARTE).setEnabled(canEdit); // 新規カルテ
            mediator.getAction(GUIConst.ACTION_DELETE_KARTE).setEnabled(canEdit); // 削除
            mediator.getAction(GUIConst.ACTION_PRINT).setEnabled(true); // 印刷
            mediator.getAction(GUIConst.ACTION_ASCENDING).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_DESCENDING).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_SHOW_MODIFIED).setEnabled(true);
        }
    }
    
    protected final class StateMgr {
        
        private BrowserState noKarteState = new NoKarteState();
        private BrowserState hasKarteState = new HasKarteState();
        private BrowserState currentState;
        
        public StateMgr() {
            currentState = noKarteState;
        }
        
        public void setNoKarte() {
            currentState = noKarteState;
            currentState.controlMenu();
        }
        
        public void setHasKarte() {
            if (currentState != hasKarteState) {
                currentState = hasKarteState;
            }
            currentState.controlMenu();
        }
        
        public void controlMenu() {
            currentState.controlMenu();
        }
    }
}
