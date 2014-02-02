package open.dolphin.client;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.DBTask;
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
public class KarteDocumentViewer extends AbstractChartDocument implements DocumentViewer {

    // Busy プロパティ名
    public static final String BUSY_PROP = "busyProp";

    // 更新を表す文字
    private static final String TITLE_UPDATE = "更新";
    private static final String TITLE = "参 照";

    // このアプリケーションは文書履歴を複数選択することができる
    // このリストはそれに対応した KarteViewer(2号カルテ)を保持している
    // このリストの内容（KarteViewer)が一枚のパネルに並べて表示される
    private List<KarteViewer> karteList;

    // 上記パネル内でマウスで選択されているカルテ(karteViewer)
    // 前回処方を適用した新規カルテはこの選択されたカルテが元になる
    private KarteViewer selectedKarte; // 選択されている karteViewer

    // busy プリパティ
    private boolean busy;

    // 文書履を昇順で表示する場合に true
    private boolean ascending;

    // 文書の修正履歴を表示する場合に true
    private boolean showModified;

    // このクラスの状態マネージャ
    private StateMgr stateMgr;

    // 選択を解除されたカルテのリスト
    private ArrayList<KarteViewer> removed;
    private JPanel scrollerPanel;

    /**
     * DocumentViewerオブジェクトを生成する。
     */
    public KarteDocumentViewer() {
        super();
        setTitle(TITLE);
    }

    /**
     * busy かどうかを返す。
     * @return busy の時 true
     */
    public boolean isBusy() {
        return busy;
    }

    @Override
    public void start() {
        karteList = new ArrayList<KarteViewer>(1);
        connect();
        stateMgr = new StateMgr();
        enter();
    }

    @Override
    public void stop() {
        if (karteList != null) {
            for (KarteViewer karte : karteList) {
                karte.stop();
            }
            karteList.clear();
        }
    }

    @Override
    public void enter() {
        super.enter();
        stateMgr.enter();
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
                stateMgr.processCleanEvent();

            } else {
                // selectedKarte == null
                stateMgr.processEmptyEvent();
            }
        }
    }

    /**
     * 新規カルテ作成の元になるカルテを返す。
     * @return 作成の元になるカルテ
     */
    public KarteViewer getBaseKarte() {
        KarteViewer ret = getSelectedKarte();
        if (ret == null) {
            if (karteList != null && karteList.size() > 0) {
                ret = ascending ? karteList.get(karteList.size() - 1) : karteList.get(0);
            }
        }
        return ret;
    }

    /**
     * 文書履歴の抽出期間が変更された場合、
     * karteList をclear、選択されているkarteViewerを解除、sateMgrをNoKarte状態に設定する。
     */
    @Override
    public void historyPeriodChanged() {
        if (karteList != null) {
            karteList.clear();
        }
        setSelectedKarte(null);
        getContext().showDocument(0);
    }

    /**
     * GUIコンポーネントにリスナを設定する。
     *
     */
    private void connect() {

        // 文書履歴に昇順／降順、修正履歴表示の設定をする
        // この値の初期値はデフォル値であり、個々のドキュメント（画面）単位にメニューで変更できる。（適用されるのは個々のドキュメントのみ）
        // デフォルト値の設定は環境設定で行う。
        ascending = getContext().getDocumentHistory().isAscending();
        showModified = getContext().getDocumentHistory().isShowModified();
    }

    /**
     * KarteViewerを生成し表示する。
     *
     * @param selectedHistories 文書履歴テーブルで選択された文書情報 DocInfo 配列
     */
    @Override
    public void showDocuments(DocInfoModel[] selectedHistories, final JScrollPane scroller) {

        if (selectedHistories == null || selectedHistories.length == 0) {
            return;
        }

        // 現在のリストと比較し、新たに追加されたもの、削除されたものに分ける
        ArrayList<DocInfoModel> added = new ArrayList<DocInfoModel>(1); // 追加されたもの
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
                if (viewer.getModel().getDocInfoModel().equals(selectedDocInfo)) {
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
                if (viewer.getModel().getDocInfoModel().equals(selectedDocInfo)) {
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
            }
        }

        // 追加されたものがない場合
        if (added == null || added.isEmpty()) {

            boolean vsc = Project.getBoolean(Project.KARTE_SCROLL_DIRECTION, true);

            if (scrollerPanel != null) {
                scrollerPanel.removeAll();
            }

            scrollerPanel = new JPanel();

            if (vsc) {
                scrollerPanel.setLayout(new BoxLayout(scrollerPanel, BoxLayout.Y_AXIS));
            } else {
                scrollerPanel.setLayout(new BoxLayout(scrollerPanel, BoxLayout.X_AXIS));
            }

            for (KarteViewer view : karteList) {
                scrollerPanel.add(view.getUI());
            }

            scroller.setViewportView(scrollerPanel);

            if (vsc) {
                showKarteListV();
            } else {
                showKarteListH();
            }

            return;
        }

        // 取得する文書のID(PK)をリストを生成する
        List<Long> docId = new ArrayList<Long>(added.size());
        for (DocInfoModel bean : added) {
            docId.add(new Long(bean.getDocPk()));
        }

        // データベースから取得する
        DocumentDelegater ddl = new DocumentDelegater();
        KarteTask task = new KarteTask(getContext(), docId, added, ddl, scroller);
        task.execute();
    }

    private KarteViewer createKarteViewer(DocInfoModel docInfo) {
        if (docInfo != null && docInfo.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
            return new KarteViewer();
        }
        return new KarteViewer2();
    }

    /**
     * データベースで検索した KarteModelを Viewer で表示する。
     *
     * @param models KarteModel
     * @param docInfos DocInfo
     */
    private void addKarteViewer(List<DocumentModel> models, List<DocInfoModel> docInfos, final JScrollPane scroller) {

        if (models != null) {

            int index = 0;
            for (DocumentModel karteModel : models) {

                karteModel.setDocInfoModel(docInfos.get(index++)); // ?

                // シングル及び２号用紙の判定を行い、KarteViewer を生成する
                final KarteViewer karteViewer = createKarteViewer(karteModel.getDocInfoModel());
                karteViewer.setContext(getContext());
                karteViewer.setModel(karteModel);
                karteViewer.setAvoidEnter(true);

                // このコールでモデルのレンダリングが開始される
                karteViewer.start();

                // 2号カルテの場合ダブルクリックされたカルテを別画面で表示する
                // MouseListener を生成して KarteViewer の Pane にアタッチする
                if (karteModel.getDocInfoModel().getDocType().equals(IInfoModel.DOCTYPE_KARTE)) {
                    final MouseListener ml = new MouseAdapter() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int cnt = e.getClickCount();
                            if (cnt == 2) {
                                //-----------------------------------
                                // 選択した Karte を EditoFrame で開く
                                //-----------------------------------
                                setSelectedKarte(karteViewer);
                                openKarte();
                            } else if (cnt == 1) {
                                setSelectedKarte(karteViewer);
                            }
                        }
                    };
                    karteViewer.addMouseListener(ml);
                }

                karteList.add(karteViewer);

            }
            // 時間軸でソート、viewへ通知、選択処理をする
            if (ascending) {
                Collections.sort(karteList);
            } else {
                Collections.sort(karteList, Collections.reverseOrder());
            }

            // 選択する
            if (karteList.size() > 0) {
                if (ascending) {
                    setSelectedKarte(karteList.get(karteList.size() - 1));
                } else {
                    setSelectedKarte(karteList.get(0));
                }
            }
        }

        boolean vsc = Project.getBoolean(Project.KARTE_SCROLL_DIRECTION, true);

        if (scrollerPanel != null) {
            scrollerPanel.removeAll();
        }

        scrollerPanel = new JPanel();
        //scrollerPanel.setVisible(false);
        
        if (vsc) {
            scrollerPanel.setLayout(new BoxLayout(scrollerPanel, BoxLayout.Y_AXIS));
        } else {
            scrollerPanel.setLayout(new BoxLayout(scrollerPanel, BoxLayout.X_AXIS));
        }

        for (KarteViewer view : karteList) {
            scrollerPanel.add(view.getUI());
        }
        
        scroller.setViewportView(scrollerPanel);

        if (vsc) {
            showKarteListV();
        } else {
            showKarteListH();
        }
    }

    private void showKarteListV() {

        Runnable awt = new Runnable() {

            @Override
            public void run() {

                if (karteList.size() > 1) {
                    int totalHeight = 0;
                    for (KarteViewer view : karteList) {
                        int w = view.panel2.getPreferredSize().width;
                        int h = view.getActualHeight() + 30;
                        totalHeight += h;
                        view.panel2.setPreferredSize(new Dimension(w, h));
                    }
                    int spWidth = scrollerPanel.getPreferredSize().width;
                    scrollerPanel.setPreferredSize(new Dimension(spWidth, totalHeight));
                }

                scrollerPanel.scrollRectToVisible(new Rectangle(0, 0, scrollerPanel.getWidth(), 100));
                //scrollerPanel.setVisible(true);
                getContext().showDocument(0);
                if (removed != null) {
                    for (KarteViewer karte : removed) {
                        karte.stop();
                    }
                    removed.clear();
                }
            }
        };
        EventQueue.invokeLater(awt);
    }

    private void showKarteListH() {

        Runnable awt = new Runnable() {

            @Override
            public void run() {

                if (karteList.size() > 1) {
                    int maxHeight = 0;
                    for (KarteViewer view : karteList) {
                        int w = view.panel2.getPreferredSize().width;
                        int h = view.getActualHeight() + 20;
                        maxHeight = maxHeight >= h ? maxHeight : h;
                        view.panel2.setPreferredSize(new Dimension(w, h));
                    }
                    int spWidth = scrollerPanel.getPreferredSize().width;
                    scrollerPanel.setPreferredSize(new Dimension(spWidth, maxHeight));
                }

                scrollerPanel.scrollRectToVisible(new Rectangle(0, 0, scrollerPanel.getWidth(), 100));
                getContext().showDocument(0);
                if (removed != null) {
                    for (KarteViewer karte : removed) {
                        karte.stop();
                    }
                    removed.clear();
                }
            }
        };
        EventQueue.invokeLater(awt);
    }

    /**
     * 表示されているカルテを CLAIM 送信する
     * 元町皮ふ科
     */
    public void sendClaim() {

        // claim を送るのはカルテだけ
        String docType = getBaseKarte().getModel().getDocInfoModel().getDocType();
        if (!IInfoModel.DOCTYPE_KARTE.equals(docType)) {
            return;
        }

        DocumentModel model = getContext().getKarteModelToEdit(getBaseKarte().getModel());
        model.setKarteBean(getContext().getKarte());
        model.getDocInfoModel().setConfirmDate(new Date());

        ClaimSender claimSender = new ClaimSender(getContext().getCLAIMListener());

        // DG  DocInfoに設定されているGUIDに一致する保険情報モジュールを設定する
        PVTHealthInsuranceModel applyIns = getContext().getHealthInsuranceToApply(model.getDocInfoModel().getHealthInsuranceGUID());
        claimSender.setInsuranceToApply(applyIns);
        claimSender.send(model);
    }


    /**
     * カルテを修正する。
     */
    public void modifyKarte() {

        if (getBaseKarte() == null) {
            return;
        }

        String docType = getBaseKarte().getModel().getDocInfoModel().getDocType();

        ChartImpl chart = (ChartImpl) getContext();
        //String dept = getContext().getPatientVisit().getDepartment();
        //String deptCode = getContext().getPatientVisit().getDepartmentCode();
        String deptName = getContext().getPatientVisit().getDeptName();
        String deptCode = getContext().getPatientVisit().getDeptCode();

        NewKarteParams params = new NewKarteParams(Chart.NewKarteOption.BROWSER_MODIFY);
        params.setDocType(docType);
        params.setDepartmentName(deptName);
        params.setDepartmentCode(deptCode);
        // このフラグはカルテを別ウインドウで編集するかどうか
        params.setOpenFrame(Project.getBoolean(Project.KARTE_PLACE_MODE, true));

        DocumentModel editModel = chart.getKarteModelToEdit(getBaseKarte().getModel());
        KarteEditor editor = chart.createEditor();
        editor.setModel(editModel);
        editor.setEditable(true);
        editor.setModify(true);
        int mode = docType.equals(IInfoModel.DOCTYPE_KARTE) ? KarteEditor.DOUBLE_MODE : KarteEditor.SINGLE_MODE;
        editor.setMode(mode);

        // Single Karte の場合 EF させない
        if (mode == 1) {
            params.setOpenFrame(false);
        }

        if (params.isOpenFrame()) {
            EditorFrame editorFrame = new EditorFrame();
            editorFrame.setChart(getContext());
            editorFrame.setKarteEditor(editor);
            editorFrame.start();
        } else {
            editor.setContext(chart);
            editor.initialize();
            editor.start();
            chart.addChartDocument(editor, TITLE_UPDATE);
        }
    }

    @Override
    public void print() {
        KarteViewer view = getSelectedKarte();
        if (view != null) {
            view.print();
        }
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
     *
     */
    public void openKarte() {

        if (getSelectedKarte() != null) {

            // EditorFrameを生成する
            EditorFrame editorFrame = new EditorFrame();
            editorFrame.setChart(getContext());

            // 表示している文書タイプに応じて Viewer を作成する
            DocumentModel model = getSelectedKarte().getModel();
            String docType = model.getDocInfoModel().getDocType();

            if (docType.equals(IInfoModel.DOCTYPE_S_KARTE)) {
                // plain文書をEditorFrameに設定する
                KarteViewer view = new KarteViewer();
                view.setModel(model);
                editorFrame.setKarteViewer(view);
                editorFrame.start();
            } else if (docType.equals(IInfoModel.DOCTYPE_KARTE)) {
                // 2号カルテをEditorFrameに設定する
                KarteViewer2 view = new KarteViewer2();
                view.setModel(model);
                editorFrame.setKarteViewer(view);
                editorFrame.start();
            }
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

            @Override
            public void actionPerformed(ActionEvent e) {
                if (box1.isSelected() || box2.isSelected()) {
                    return;
                } else if (!box3.isSelected()) {
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
                new String[]{deleteText, cancelText},
                cancelText);

        System.out.println(option);

        // キャンセルの場合はリターンする
        if (option != 0) {
            return;
        }

        //
        // 削除する status = 'D'
        //
        long deletePk = delete.getModel().getId();
        DocumentDelegater ddl = new DocumentDelegater();
        DeleteTask task = new DeleteTask(getContext(), deletePk, ddl);
        task.execute();
    }

    /**
     * 文書をデータベースから取得するタスククラス。
     */
    class KarteTask extends DBTask<List<DocumentModel>, Void> {

        private DocumentDelegater ddl;
        private List<Long> docId;
        private List<DocInfoModel> docInfos;
        private JScrollPane scroller;

        public KarteTask(Chart ctx, List<Long> docId, List<DocInfoModel> docInfos, DocumentDelegater ddl, JScrollPane scroller) {
            super(ctx);
            this.docId = docId;
            this.ddl = ddl;
            this.docInfos = docInfos;
            this.scroller = scroller;
        }

        @Override
        protected List<DocumentModel> doInBackground() throws Exception {
            logger.debug("カルテタスク doInBackground");
            List<DocumentModel> result = ddl.getDocuments(docId);
            logger.debug("doInBackground noErr, return result");
            return result;
        }

        @Override
        protected void succeeded(List<DocumentModel> list) {
            logger.debug("KarteTask succeeded");
            if (list != null) {
                addKarteViewer(list, docInfos, scroller);
            }
        }
    }

    /**
     * カルテの削除タスククラス。
     */
    class DeleteTask extends DBTask<Boolean, Void> {

        private DocumentDelegater ddl;
        private long docPk;

        public DeleteTask(Chart ctx, long docPk, DocumentDelegater ddl) {
            super(ctx);
            this.docPk = docPk;
            this.ddl = ddl;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            logger.debug("DeleteTask started");
            ddl.deleteDocument(docPk);
            return true;
        }

        @Override
        protected void succeeded(Boolean result) {
            logger.debug("DeleteTask succeeded");
            Chart chart = (KarteDocumentViewer.this).getContext();
            chart.getDocumentHistory().getDocumentHistory();
        }
    }

    /**
     * 抽象状態クラス。
     */
    protected abstract class BrowserState {

        public BrowserState() {
        }

        public abstract void enter();
    }

    /**
     * 表示するカルテがない状態を表す。
     */
    protected final class EmptyState extends BrowserState {

        public EmptyState() {
        }

        @Override
        public void enter() {
            boolean canEdit = isReadOnly() ? false : true;
            getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);     // 新規カルテ
            getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);  // 新規文書
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
            getContext().enabledAction(GUIConst.ACTION_DELETE, false);          // 削除
            getContext().enabledAction(GUIConst.ACTION_PRINT, false);           // 印刷   
            getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);       // 昇順
            getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);      // 降順
            getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);   // 修正履歴表示
            getContext().enabledAction(GUIConst.ACTION_SEND_CLAIM, false);      // CLAIM送信
        }
    }

    /**
     * カルテが表示されている状態を表す。
     */
    protected final class ClaenState extends BrowserState {

        public ClaenState() {
        }

        @Override
        public void enter() {

            //-----------------------------------------
            // 新規カルテが可能なケース 仮保存でないことを追加
            //-----------------------------------------
            boolean canEdit = isReadOnly() ? false : true;
            boolean tmpKarte = false;
            KarteViewer base = getBaseKarte();
            if (base != null) {
                String state = base.getModel().getDocInfoModel().getStatus();
                if (state.equals(IInfoModel.STATUS_TMP)) {
                    tmpKarte = true;
                }
            }
            boolean newOk = canEdit && (!tmpKarte) ? true : false;
            getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, newOk);        // 新規カルテ
            getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);   // 新規文書
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, canEdit);   // 修正
            getContext().enabledAction(GUIConst.ACTION_DELETE, canEdit);         // 削除
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);             // 印刷
            getContext().enabledAction(GUIConst.ACTION_ASCENDING, true);         // 昇順
            getContext().enabledAction(GUIConst.ACTION_DESCENDING, true);        // 降順
            getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, true);     // 修正履歴表示

            //-----------------------------------------
            // CLAIM 送信が可能なケース
            //-----------------------------------------
            boolean sendOk = getContext().isSendClaim();
            sendOk = sendOk && (!tmpKarte);
            getContext().enabledAction(GUIConst.ACTION_SEND_CLAIM, sendOk);       // CLAIM送信
        }
    }

    /**
     * StateContext クラス。
     */
    protected final class StateMgr {

        private BrowserState emptyState = new EmptyState();
        private BrowserState cleanState = new ClaenState();
        private BrowserState currentState;

        public StateMgr() {
            currentState = emptyState;
        }

        public void processEmptyEvent() {
            currentState = emptyState;
            this.enter();
        }

        public void processCleanEvent() {
            currentState = cleanState;
            this.enter();
        }

        public void enter() {
            currentState.enter();
        }
    }
}
