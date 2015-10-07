package open.dolphin.client;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.StripeTableCellRenderer;

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
    private ListTableModel<DocInfoModel> tableModel;
    
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
    private final ChartImpl context;
    
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
    private final boolean start;
    private NameValuePair[] contentObject;
    private NameValuePair[] extractionObjects;
    
    // Key入力をブロックするリスナ
    private BlockKeyListener blockKeyListener;

    /**
     * Creates new DocumentHistory
     * @param context ChartImpl
     */
    public DocumentHistory(ChartImpl context) {
        this.context = context;
        initComponent();
        connect();
        start = true;
    }

    // (予定カルテ対応)    
    public int getDocumentCount() {
        if (tableModel==null) {
            return 0;
        }
        List<DocInfoModel> list = tableModel.getDataProvider();
        return list!=null ? list.size() : 0;
    }

    /**
     * 履歴テーブルのコレクションを clear する。
     */
    public void clear() {
        if (tableModel != null && tableModel.getDataProvider() != null) {
            tableModel.getDataProvider().clear();
        }
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
        List<DocInfoModel> list = context.getKarte().getDocInfoList();
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
    
    public void getLetterHistory() {
        if (contentCombo.getSelectedIndex()==0) {
            contentCombo.setSelectedIndex(1);
        } else {
            getDocumentHistory();
        }
    }

    /**
     * 抽出期間等が変化し、履歴を再取得した場合等の処理で、履歴テーブルの更新、 最初の行の自動選択、束縛プロパティの変化通知を行う。
     */
    private void updateHistory(List<DocInfoModel> mewHistory) {

        // ソーティングする
        if (mewHistory != null && mewHistory.size() > 0) {
            if (ascending) {
                Collections.sort(mewHistory);
            } else {
                Collections.sort(mewHistory, Collections.reverseOrder());
            }
        }

        // 文書履歴テーブルにデータの Arraylist を設定する
        tableModel.setDataProvider(mewHistory);

        // 束縛プロパティの通知を行う
        boundSupport.firePropertyChange(HITORY_UPDATED, false, true);

        String countInfo = ClientContext.getMyBundle(DocumentHistory.class).getString("messageFormat.numRecords");
        MessageFormat msf = new MessageFormat(countInfo);
        
        if (mewHistory != null && mewHistory.size() > 0) {

            int cnt = mewHistory.size();
            countField.setText(msf.format(new Object[]{String.valueOf(cnt)}));
            int fetchCount = cnt > autoFetchCount ? autoFetchCount : cnt;

            // テーブルの最初の行の自動選択を行う
            JTable table = view.getTable();
            int first;
            int last;

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
            //countField.setText("0 件");
            countField.setText(msf.format(new Object[]{"0"}));
        }
    }

    /**
     * 文書履歴のタイトルを変更する。
     * @param docInfo
     */
    public void titleChanged(DocInfoModel docInfo) {

        if (docInfo != null && docInfo.getTitle() != null) {
            ChangeTitleTask task = new ChangeTitleTask(context, docInfo, new DocumentDelegater());
            task.execute();
        }
    }

    /**
     * 抽出期間を変更し再検索する。
     * @param state
     */
    public void periodChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            int index = extractionCombo.getSelectedIndex();
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
     * @param state
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

        // View
        view = new DocumentHistoryView();

        // 履歴テーブルのパラメータを取得する
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(DocumentHistory.class);
        String columnLine = bundle.getString("columnNames.table");
        String methodLine = bundle.getString("methods.table");
        String[] columnNames = columnLine.split(",");
        String[] methodNames = methodLine.split(",");
        Class[] columnClasses = {String.class, String.class};
        
        String extNameLine = bundle.getString("names.extractionPeriod");
        String extValueLine = bundle.getString("values.extractionPeriod");
        String[] extNames = extNameLine.split(",");
        String[] extValues = extValueLine.split(",");
        extractionObjects = new NameValuePair[extNames.length];
        for (int i=0; i<extNames.length;i++) {
             extractionObjects[i] = new NameValuePair(extNames[i], extValues[i]);
        }

        // 文書履歴テーブルを生成する
        tableModel = new ListTableModel<DocInfoModel>(columnNames, 0, methodNames, columnClasses) {

            @Override
            public boolean isCellEditable(int row, int col) {
                
                // readOnlyを拒否
                if (context.isReadOnly()) {
                    return false;
                }

                if (col == 1 && getObject(row) != null) {
                    DocInfoModel docInfo = getObject(row);
                    return (docInfo.getDocType().equals(IInfoModel.DOCTYPE_KARTE) ||
                            docInfo.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE));
                }
                return false;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {

                if (col != 1 || value == null || value.equals("")) {
                    return;
                }

                DocInfoModel docInfo = getObject(row);
                if (docInfo == null) {
                    return;
                }

                if (docInfo.getDocType().equals(IInfoModel.DOCTYPE_KARTE) ||
                    docInfo.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
                    // 文書タイトルを変更し通知する
                    docInfo.setTitle((String) value);
                    titleChanged(docInfo);
                }
            }
        };
        view.getTable().setModel(tableModel);
        
        // カラム幅を調整する
        view.getTable().getColumnModel().getColumn(0).setPreferredWidth(90);
        view.getTable().getColumnModel().getColumn(1).setPreferredWidth(190);

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        String copyText = bundle.getString("actionText.copy");
        String deleteText = bundle.getString("actionText.delete");
        String duplicateText = bundle.getString("actionText.dupricate");
        
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        final AbstractAction copyAction = new AbstractAction(copyText) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        view.getTable().getInputMap().put(copy, "Copy");
        view.getTable().getActionMap().put("Copy", copyAction);

        // Delete ACtion
        final AbstractAction deleteAction = new AbstractAction(deleteText) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                deleteRow();
            }
        };
        
//s.oh^ 2014/04/03 文書の複製
        final AbstractAction copyDocAction = new AbstractAction(duplicateText) {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyDoc();
            }
        };
//s.oh$

        // 右クリックコピー
        view.getTable().addMouseListener(new MouseAdapter() {

            private void mabeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = view.getTable().rowAtPoint(e.getPoint());
                    DocInfoModel m = tableModel.getObject(row);
                    if (m == null) {
                        return;
                    }
                    JPopupMenu pop = new JPopupMenu();
                    JMenuItem item2 = new JMenuItem(copyAction);
                    pop.add(item2);
//s.oh^ 2014/04/02 閲覧権限の制御
                    if(context.isReadOnly()) {
                        copyAction.setEnabled(false);
                    }
//s.oh$
                    
//s.oh^ 2014/04/03 文書の複製
                    if(extractionContent.equals(IInfoModel.DOCTYPE_LETTER)) {
                        JMenuItem itemCopyDoc = new JMenuItem(copyDocAction);
                        copyDocAction.setEnabled(context.isShowDocument(0));
                        pop.add(itemCopyDoc);
//s.oh^ 2014/04/02 閲覧権限の制御
                        if(context.isReadOnly()) {
                            copyDocAction.setEnabled(false);
                        }
//s.oh$
                    }
//s.oh$

                    if (Project.getBoolean("allow.delete.letter", false) &&
                            extractionContent.equals(IInfoModel.DOCTYPE_LETTER)) {
                        pop.addSeparator();
                        JMenuItem item3 = new JMenuItem(deleteAction);
                        pop.add(item3);
//s.oh^ 2014/04/02 閲覧権限の制御
                        if(context.isReadOnly()) {
                            deleteAction.setEnabled(false);
                        }
//s.oh$
                    }

                    pop.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mabeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mabeShowPopup(e);
            }
        });
        
        // タイトルカラムに IME ON を設定する
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoKanjiListener.getInstance());
        TableColumn column = view.getTable().getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(tf));
        
        // 奇数偶数レンダラを設定する
        DocInfoRenderer rederer = new DocInfoRenderer();
        rederer.setTable(view.getTable());
        rederer.setDefaultRenderer();
        // 行の高さ
        view.getTable().setRowHeight(ClientContext.getMoreHigherRowHeight());

        // 文書種別(コンテントタイプ) ComboBox を生成する
        contentObject = new NameValuePair[2];
        String karteText = bundle.getString("contentText.karte");
        String letterText = bundle.getString("contentText.letter");
        contentObject[0] = new NameValuePair(karteText, IInfoModel.DOCTYPE_KARTE);
        contentObject[1] = new NameValuePair(letterText, IInfoModel.DOCTYPE_LETTER);
        
        // add in18
        contentCombo = view.getDocTypeCombo();
        DefaultComboBoxModel contentModel = new DefaultComboBoxModel(contentObject);
        contentCombo.setModel(contentModel);

        // 抽出機関 ComboBox を生成する add in18
        extractionCombo = view.getExtractCombo();
        DefaultComboBoxModel extractionModel = new DefaultComboBoxModel(extractionObjects);
        extractionCombo.setModel(extractionModel);
        
//s.oh^ 2014/08/19 ID権限
        if(Project.isOtherCare()) {
            contentCombo.setEnabled(false);
            extractionCombo.setEnabled(false);
        }
//s.oh$

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
        slm.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting() == false) {
                JTable table = view.getTable();
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    List<DocInfoModel> list = new ArrayList<>(1);
                    for (int i = 0; i < selectedRows.length; i++) {
                        DocInfoModel obj = tableModel.getObject(selectedRows[i]);
                        if (obj != null) {
                            list.add(obj);
                        }
                    }
                    //Collections.sort(list);
                    DocInfoModel[] selected = (DocInfoModel[]) list.toArray(new DocInfoModel[list.size()]);
                    if (selected != null && selected.length > 0) {
                        setSelectedHistories(selected);
                    } else {
                        setSelectedHistories((DocInfoModel[]) null);
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
        int past = Project.getInt(Project.DOC_HISTORY_PERIOD, -12);
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
        autoFetchCount = Project.getInt(Project.DOC_HISTORY_FETCHCOUNT, 1);

        // Preference から昇順降順を設定する
        ascending = Project.getBoolean(Project.DOC_HISTORY_ASCENDING);

        // 文書履歴テーブルのキーボード入力をブロックするリスナ
        blockKeyListener = new BlockKeyListener();
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        StringBuilder sb = new StringBuilder();
        int numRows = view.getTable().getSelectedRowCount();
        int[] rowsSelected = view.getTable().getSelectedRows();
        int numColumns = view.getTable().getColumnCount();

        for (int i = 0; i < numRows; i++) {

            StringBuilder s = new StringBuilder();
            for (int col = 0; col < numColumns; col++) {
                Object o = view.getTable().getValueAt(rowsSelected[i], col);
                if (o!=null) {
                    s.append(o.toString());
                }
                s.append(",");
            }
            if (s.length()>0) {
                s.setLength(s.length()-1);
            }
            sb.append(s.toString()).append("\n");

        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }

    /**
     * 削除、非公開
     */
    public void deleteRow() {
        int row = view.getTable().getSelectedRow();
        DocInfoModel m = tableModel.getObject(row);
        if (m==null) {
            return;
        }
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(DocumentHistory.class);
        String question = bundle.getString("messageFormat.delete");
        String optionDelete = bundle.getString("optionText.delete");
        String title = bundle.getString("title.optionPane.delete");
        
        MessageFormat msf = new MessageFormat(question);
        String msg = msf.format(new Object[]{m.getTitle()});
        Object[] cstOptions = new Object[]{optionDelete, GUIFactory.getCancelButtonText()};
        
        int select = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(view.getTable()),
                msg,
                ClientContext.getFrameTitle(title),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,              
                ClientContext.getImageIconArias("icon_caution"),             
                cstOptions, optionDelete);

        if (select != 0) {
            return;
        }
        
        DeleteTask task = new DeleteTask(context, m.getDocPk());
        task.execute();
    }
    
//s.oh^ 2014/04/03 文書の複製
    public void copyDoc() {
        context.getChartMediator().sendToChain("copyDocument");
    }
//s.oh$

    /**
     * キーボード入力をブロックするリスナクラス。
     */
    class BlockKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            e.consume();
        }

        /** Handle the key-pressed event from the text field. */
        @Override
        public void keyPressed(KeyEvent e) {
            e.consume();
        }

        /** Handle the key-released event from the text field. */
        @Override
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
//minagawa^ 紹介状の場合は singleSelection
        if (this.extractionContent.equals(IInfoModel.DOCTYPE_LETTER)) {
            view.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            view.getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
//minagawa$        
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
    class DocInfoTask extends DBTask<List<DocInfoModel>, Void> {

        // Delegator
        private final DocumentDelegater ddl;
        // 検索パラメータを保持するオブジェクト
        private final DocumentSearchSpec spec;

        public DocInfoTask(Chart ctx, DocumentSearchSpec spec, DocumentDelegater ddl) {
            super(ctx);
            this.spec = spec;
            this.ddl = ddl;
        }

        @Override
        protected List<DocInfoModel> doInBackground() throws Exception {
            List<DocInfoModel> result = (List<DocInfoModel>) ddl.getDocumentList(spec);
            return result;
        }

        @Override
        protected void succeeded(List<DocInfoModel> result) {           
            updateHistory(result);       
        }
    }

    class DeleteTask extends DBTask<Void, Void> {

        // 検索パラメータを保持するオブジェクト
        private final long spec;

        public DeleteTask(Chart ctx, long spec) {
            super(ctx);
            this.spec = spec;
        }

        @Override
        protected Void doInBackground() throws Exception {
            LetterDelegater ldl = new LetterDelegater();
            ldl.delete(spec);
            return null;
        }

        @Override
        protected void succeeded(Void result) {
            getDocumentHistory();
        }
    }

    /**
     * タイトル変更タスククラス。
     */
    class ChangeTitleTask extends DBTask<Boolean, Void> {

        // DocInfo
        private final DocInfoModel docInfo;
        // Delegator
        private final DocumentDelegater ddl;

        public ChangeTitleTask(Chart ctx, DocInfoModel docInfo, DocumentDelegater ddl) {
            super(ctx);
            this.docInfo = docInfo;
            this.ddl = ddl;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            ddl.updateTitle(docInfo);
            return true;
        }

        @Override
        protected void succeeded(Boolean result) {
        }
    }

    /**
     * 文書履歴テーブル用のセルレンダラ
     * 自賠責、労災、自費のカラーリング
     */
    class DocInfoRenderer extends StripeTableCellRenderer {

        public DocInfoRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column ) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            DocInfoModel info = tableModel.getObject(row);
            ImageIcon icon = null;
            // Attachmentの判定 まずい...ICON_TEMP_SAVE_SMALL
            if (column==1 && info!=null && info.isHasMark()) {
                icon = ClientContext.getImageIconArias("icon_attachment_small");  
                
            } else if (column==1 && info!=null && IInfoModel.STATUS_TMP.equals(info.getStatus())) {
                // 仮保存
                icon = ClientContext.getImageIconArias("icon_temp_save_small");            
            }
    
            if ((!isSelected) && info!= null && 
                info.getDocType().equals(IInfoModel.DOCTYPE_KARTE) &&
                info.getHealthInsurance()!=null) {

                java.util.ResourceBundle clBundle = ClientContext.getClaimBundle();

                if (Project.getBoolean("docHistory.coloring.rosai") && info.getHealthInsurance().startsWith(clBundle.getString("INSURANCE_ROSAI_PREFIX"))) {
                    // 労災
                    setBackground(GUIConst.ROSAI_INSURANCE_COLOR);

                } else if (Project.getBoolean("docHistory.coloring.jibaiseki") && info.getHealthInsurance().startsWith(clBundle.getString("INSURANCE_JIBAISEKI_PREFIX"))) {
                    // 自賠責
                    setBackground(GUIConst.JIBAISEKI_INSURANCE_COLOR);
                } 
//minagawa^ Kuroiwa specific  下記をコメントアウト                  
                else if (Project.getBoolean("docHistory.coloring.jihi") && info.getHealthInsurance().startsWith(clBundle.getString("INSURANCE_SELF_PREFIX"))) {
                    // 自費
                    setBackground(GUIConst.SELF_INSURANCE_COLOR);   
                } 
//minagawa$                    
            } 
            //-------------------------------------------------------
            if (value != null) {

                if (value instanceof java.lang.String) {
                    this.setText((String) value);
                } else {
                    this.setText(value.toString());
                }

            } else {
                this.setText("");
            }
            setIcon(icon);
            //-------------------------------------------------------

            return this;
        }
    }
}
