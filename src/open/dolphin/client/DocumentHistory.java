package open.dolphin.client;

import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.DefaultCellEditor;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.project.Project;
import open.dolphin.table.ObjectReflectTableModel;
import open.dolphin.table.OddEvenRowRenderer;

/**
 * 文書履歴を取得し、表示するクラス。
 *
 * @author Minagawa,Kazushi
 */
public class DocumentHistory {

    // PropertyChange 名
    public static final String DOCUMENT_TYPE = "documentTypeProp";
    public static final String SELECTED_HISTORIES = "selectedHistories";
    public static final String SELECTED_KARTES = "selectedKartes";
    public static final String HITORY_UPDATED = "historyUpdated";
    
    // 文書履歴テーブル
    private ObjectReflectTableModel tableModel;
    
    private DocumentHistoryView view;
    
    // 抽出期間コンボボックス
    private JComboBox extractionCombo;
    
    // 文書種別コンボボックス
    private JComboBox contentCombo;
    
    // 件数フィールド 
    private JLabel countField;
    
    // 束縛サポート
    private PropertyChangeSupport boundSupport;
    
    // context 
    private ChartImpl context;
    
    // 選択された文書情報(DocInfo)の配列
    private DocInfoModel[] selectedHistories;
    
    // 抽出コンテント(文書種別)
    private String extractionContent;
    
    // 抽出開始日 
    private Date extractionPeriod;
    
    // 自動的に取得する文書数
    private int autoFetchCount;
    
    // 昇順降順のフラグ 
    private boolean ascending;
    
    // 修正版も表示するかどうかのフラグ
    private boolean showModified;
    
    // フラグ
    private boolean start;
    private NameValuePair[] contentObject;
    private NameValuePair[] extractionObjects;
    
    // Key入力をブロックするリスナ
    private BlockKeyListener blockKeyListener;

    /**
     * 文書履歴オブジェクトを生成する。
     * @param owner コンテキシト
     */
    public DocumentHistory(ChartImpl context) {
        this.context = context;
        initComponent();
        connect();
        start = true;
    }

    /**
     * 履歴テーブルのコレクションを clear する。
     */
    public void clear() {
        tableModel.clear();
    }

    public void requestFocus() {
        view.getTable().requestFocusInWindow();
    }

    /**
     * 束縛プロパティリスナを登録する。
     * @param propName プロパティ名
     * @param listener リスナ
     */
    public void addPropertyChangeListener(String propName, PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(propName, listener);
    }

    /**
     * 束縛プロパティを削除する。
     * @param propName プロパティ名
     * @param listener リスナ
     */
    public void removePropertyChangeListener(String propName, PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(propName, listener);
    }

    /**
     * 選択された文書履歴(複数)を返す。
     * @return 選択された文書履歴(複数)
     */
    public DocInfoModel[] getSelectedHistories() {
        return selectedHistories;
    }

    /**
     * 束縛プロパティの選択された文書履歴(複数)を設定する。通知を行う。
     * @param newSelected 選択された文書履歴(複数)
     */
    public void setSelectedHistories(DocInfoModel[] newSelected) {

        DocInfoModel[] old = selectedHistories;
        selectedHistories = newSelected;
        //
        // リスナへ通知を行う
        //
        if (selectedHistories != null) {
            boundSupport.firePropertyChange(SELECTED_HISTORIES, old, selectedHistories);
        }
    }

    /**
     * 履歴の検索時にテーブルのキー入力をブロックする。
     * @param busy true の時検索中
     */
    public void blockHistoryTable(boolean busy) {
        if (busy) {
            view.getTable().addKeyListener(blockKeyListener);
        } else {
            view.getTable().removeKeyListener(blockKeyListener);
        }
    }

    /**
     * 文書履歴を Karte から取得し表示する。
     */
    public void showHistory() {
        List list = context.getKarte().getEntryCollection("docInfo");
        updateHistory(list);
    }

    /**
     * 文書履歴を取得する。
     * 取得するパラメータ(患者ID、文書タイプ、抽出期間)はこのクラスの属性として
     * 定義されている。これらのパラメータは comboBox等で選択される。値が変化する度に
     * このメソッドがコールされる。
     */
    public void getDocumentHistory() {

        if (start && extractionPeriod != null && extractionContent != null) {

            // 検索パラメータセットのDTOを生成する
            DocumentSearchSpec spec = new DocumentSearchSpec();
            spec.setKarteId(context.getKarte().getId());	// カルテID
            spec.setDocType(extractionContent);			// 文書タイプ
            spec.setFromDate(extractionPeriod);			// 抽出期間開始
            spec.setIncludeModifid(showModified);		// 修正履歴
            spec.setCode(DocumentSearchSpec.DOCTYPE_SEARCH);	// 検索タイプ
            spec.setAscending(ascending);

            DocInfoTask task = new DocInfoTask(context, spec, new DocumentDelegater());
            task.execute();
        }
    }

    /**
     * 抽出期間等が変化し、履歴を再取得した場合等の処理で、履歴テーブルの更新、 最初の行の自動選択、束縛プロパティの変化通知を行う。
     */
    private void updateHistory(List mewHistory) {

        // ソーティングする
        if (mewHistory != null && mewHistory.size() > 0) {
            if (ascending) {
                Collections.sort(mewHistory);
            } else {
                Collections.sort(mewHistory, Collections.reverseOrder());
            }
        }

        // 文書履歴テーブルにデータの Arraylist を設定する
        tableModel.setObjectList(mewHistory);

        // 束縛プロパティの通知を行う
        boundSupport.firePropertyChange(HITORY_UPDATED, false, true);

        if (mewHistory != null && mewHistory.size() > 0) {

            int cnt = mewHistory.size();
            countField.setText(String.valueOf(cnt) + " 件");
            int fetchCount = cnt > autoFetchCount ? autoFetchCount : cnt;

            // テーブルの最初の行の自動選択を行う
            JTable table = view.getTable();
            int first = 0;
            int last = 0;

            if (ascending) {
                last = cnt - 1;
                first = cnt - fetchCount;
            } else {
                first = 0;
                last = fetchCount - 1;
            }

            // 自動選択
            table.getSelectionModel().addSelectionInterval(first, last);

            // 選択した行が表示されるようにスクロールする
            Rectangle r = table.getCellRect(first, last, true);
            table.scrollRectToVisible(r);

        } else {
            countField.setText("0 件");
        }
    }

    /**
     * 文書履歴のタイトルを変更する。
     */
    public void titleChanged(DocInfoModel docInfo) {

        if (docInfo != null && docInfo.getTitle() != null) {
            ChangeTitleTask task = new ChangeTitleTask(context, docInfo, new DocumentDelegater());
            task.execute();
        }
    }

    /**
     * 抽出期間を変更し再検索する。
     */
    public void periodChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            int index = contentCombo.getSelectedIndex();
            NameValuePair pair = extractionObjects[index];
            String value = pair.getValue();
            int addValue = Integer.parseInt(value);
            GregorianCalendar today = new GregorianCalendar();
            today.add(GregorianCalendar.MONTH, addValue);
            today.clear(Calendar.HOUR_OF_DAY);
            today.clear(Calendar.MINUTE);
            today.clear(Calendar.SECOND);
            today.clear(Calendar.MILLISECOND);
            setExtractionPeriod(today.getTime());
        }
    }

    /**
     * 文書種別を変更し再検索する。
     */
    public void contentChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            int index = contentCombo.getSelectedIndex();
            NameValuePair pair = contentObject[index];
            setExtractionContent(pair.getValue());
        }
    }

    /**
     * GUI コンポーネントを生成する。
     */
    private void initComponent() {

        view = new DocumentHistoryView();

        // 履歴テーブルのパラメータを取得する
        String[] columnNames = ClientContext.getStringArray("docHistory.columnNames"); // {"確定日", "内容"};
        String[] methodNames = ClientContext.getStringArray("docHistory.methodNames"); // {"getFirstConfirmDateTrimTime",// "getTitle"};
        Class[] columnClasses = {String.class, String.class};
        int[] columnWidth = ClientContext.getIntArray("docHistory.columnWidth"); // {80,200};
        int startNumRows = ClientContext.getInt("docHistory.startNumRows");
        
        // ToDO
        extractionObjects = new NameValuePair[7];
        extractionObjects[0] = new NameValuePair("1年", "-12");
        extractionObjects[1] = new NameValuePair("1ヶ月", "-1");
        extractionObjects[2] = new NameValuePair("3ヶ月", "-3");
        extractionObjects[3] = new NameValuePair("半年", "-6");
        extractionObjects[4] = new NameValuePair("2年", "-24");
        extractionObjects[5] = new NameValuePair("3年", "-36");
        extractionObjects[6] = new NameValuePair("全て", "-60");

        // 文書履歴テーブルを生成する
        tableModel = new ObjectReflectTableModel(columnNames, startNumRows, methodNames, columnClasses) {

            @Override
            public boolean isCellEditable(int row, int col) {

                if (col == 1 && getObject(row) != null) {
                    return true;
                }
                return false;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {

                if (col != 1 || value == null || value.equals("")) {
                    return;
                }

                Object o = getObject(row);
                if (o == null) {
                    return;
                }

                // 文書タイトルを変更し通知する
                DocInfoModel docInfo = (DocInfoModel) o;
                docInfo.setTitle((String) value);
                titleChanged(docInfo);
            }
        };
        view.getTable().setModel(tableModel);

        // カラム幅を調整する
        for (int i = 0; i < columnWidth.length; i++) {
            view.getTable().getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        }
        
        // タイトルカラムに IME ON を設定する
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoKanjiListener.getInstance());
        TableColumn column = view.getTable().getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(tf));
        
        // 奇数偶数レンダラを設定する
        view.getTable().setDefaultRenderer(Object.class, new OddEvenRowRenderer());

        // 文書種別(コンテントタイプ) ComboBox を生成する
        contentObject = new NameValuePair[2];
        contentObject[0] = new NameValuePair("カルテ", "karte");
        contentObject[1] = new NameValuePair("紹介状", "letter");
        contentCombo = view.getDocTypeCombo();

        // 抽出機関 ComboBox を生成する
        extractionCombo = view.getExtractCombo();

        // 件数フィールドを生成する
        countField = view.getCntLbl();
    }

    /**
     * レイアウトパネルを返す。
     * @return
     */
    public JPanel getPanel() {
        return (JPanel) view;
    }

    /**
     * Event 接続を行う
     */
    private void connect() {

        // 履歴テーブルで選択された行の文書を表示する
        ListSelectionModel slm = view.getTable().getSelectionModel();
        slm.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    JTable table = view.getTable();
                    int[] selectedRows = table.getSelectedRows();
                    if (selectedRows.length > 0) {
                        ArrayList<DocInfoModel> list = new ArrayList<DocInfoModel>(1);
                        for (int i = 0; i < selectedRows.length; i++) {
                            DocInfoModel obj = (DocInfoModel) tableModel.getObject(selectedRows[i]);
                            if (obj != null) {
                                list.add(obj);
                            }
                        }
                        DocInfoModel[] selected = (DocInfoModel[]) list.toArray(new DocInfoModel[list.size()]);
                        if (selected != null && selected.length > 0) {
                            setSelectedHistories(selected);
                        } else {
                            setSelectedHistories((DocInfoModel[]) null);
                        }
                    }
                }
            }
        });

        // 文書種別変更
        contentCombo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "contentChanged", "stateChange"));

        // 抽出期間コンボボックスの選択を処理する
        extractionCombo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "periodChanged", "stateChange"));

        // Preference から文書種別を設定する
        extractionContent = IInfoModel.DOCTYPE_KARTE;

        // Preference から抽出期間を設定する
        int past = Project.getPreferences().getInt(Project.DOC_HISTORY_PERIOD, -12);
        int index = NameValuePair.getIndex(String.valueOf(past), extractionObjects);
        extractionCombo.setSelectedIndex(index);
        GregorianCalendar today = new GregorianCalendar();
        today.add(GregorianCalendar.MONTH, past);
        today.clear(Calendar.HOUR_OF_DAY);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);
        setExtractionPeriod(today.getTime());

        // Preference から自動文書取得数を設定する
        autoFetchCount = Project.getPreferences().getInt(Project.DOC_HISTORY_FETCHCOUNT, 1);

        // Preference から昇順降順を設定する
        ascending = Project.getPreferences().getBoolean(Project.DIAGNOSIS_ASCENDING, false);

        // Preference から修正履歴表示を設定する
        showModified = Project.getPreferences().getBoolean(Project.DOC_HISTORY_SHOWMODIFIED, false);

        // 文書履歴テーブルのキーボード入力をブロックするリスナ
        blockKeyListener = new BlockKeyListener();
    }

    /**
     * キーボード入力をブロックするリスナクラス。
     */
    class BlockKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
            e.consume();
        }

        /** Handle the key-pressed event from the text field. */
        public void keyPressed(KeyEvent e) {
            e.consume();
        }

        /** Handle the key-released event from the text field. */
        public void keyReleased(KeyEvent e) {
            e.consume();
        }
    }

    /**
     * 検索パラメータの文書タイプを設定する。。
     * @param extractionContent 文書タイプ
     */
    public void setExtractionContent(String extractionContent) {
        String old = this.extractionContent;
        this.extractionContent = extractionContent;
        boundSupport.firePropertyChange(DOCUMENT_TYPE, old, this.extractionContent);
        getDocumentHistory();
    }

    /**
     * 検索パラメータの文書タイプを返す。
     * @return 文書タイプ
     */
    public String getExtractionContent() {
        return extractionContent;
    }

    /**
     * 検索パラメータの抽出期間を設定する。
     * @param extractionPeriod 抽出期間
     */
    public void setExtractionPeriod(Date extractionPeriod) {
        this.extractionPeriod = extractionPeriod;
        getDocumentHistory();
    }

    /**
     * 検索パラメータの抽出期間を返す。
     * @return 抽出期間
     */
    public Date getExtractionPeriod() {
        return extractionPeriod;
    }

    /**
     * 文書履歴表示の昇順/降順を設定する。
     * @param ascending 昇順の時 true
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
        getDocumentHistory();
    }

    /**
     * 文書履歴表示の昇順/降順を返す。
     * @return 昇順の時 true
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * 修正版を表示するかどうかを設定する。
     * @param showModifyed 表示する時 true
     */
    public void setShowModified(boolean showModifyed) {
        this.showModified = showModifyed;
        getDocumentHistory();
    }

    /**
     * 修正版を表示するかどうかを返す。
     * @return 表示する時 true
     */
    public boolean isShowModified() {
        return showModified;
    }

    /**
     * 検索タスク。
     */
    class DocInfoTask extends DBTask<List<DocInfoModel>> {

        // Delegator
        private DocumentDelegater ddl;
        // 検索パラメータを保持するオブジェクト
        private DocumentSearchSpec spec;

        public DocInfoTask(Chart ctx, DocumentSearchSpec spec, DocumentDelegater ddl) {
            super(ctx);
            this.spec = spec;
            this.ddl = ddl;
        }

        @Override
        protected List<DocInfoModel> doInBackground() {
            logger.debug("DocInfoTask started");
            List<DocInfoModel> result = (List<DocInfoModel>) ddl.getDocumentList(spec);
            if (ddl.isNoError()) {
                return result;
            } else {
                return null;
            }
        }

        @Override
        protected void succeeded(List<DocInfoModel> result) {
            logger.debug("DocInfoTask succeeded");
            if (result != null) {
                updateHistory(result);
            }
        }
    }

    /**
     * タイトル変更タスククラス。
     */
    class ChangeTitleTask extends DBTask<Boolean> {

        // DocInfo
        private DocInfoModel docInfo;
        // Delegator
        private DocumentDelegater ddl;

        public ChangeTitleTask(Chart ctx, DocInfoModel docInfo, DocumentDelegater ddl) {
            super(ctx);
            this.docInfo = docInfo;
            this.ddl = ddl;
        }

        @Override
        protected Boolean doInBackground() {
            logger.debug("ChangeTitleTask started");
            ddl.updateTitle(docInfo);
            return new Boolean(ddl.isNoError());
        }

        @Override
        protected void succeeded(Boolean result) {
            logger.debug("ChangeTitleTask succeeded");
        }
    }
}
