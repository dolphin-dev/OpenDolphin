package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.im.InputSubset;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.DefaultCellEditor;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.TableColumn;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.project.Project;

/**
 * DocumentHistory
 *
 * @author Minagawa,Kazushi
 */
public class DocumentHistory implements PropertyChangeListener {
    
    // PropertyChange 名
    public static final String SELECTED_HISTORIES = "selectedHistories";
    
    public static final String SELECTED_KARTES = "selectedKartes";
    
    public static final String HITORY_UPDATED = "historyUpdated";
    
    /** 文書履歴テーブル */
    private ObjectListTable docHistoryTable;
    
    /** 抽出期間 */
    private JComboBox extractionCombo;
    
    /** 件数フィールド */
    private JTextField countField;
    
    /** レイアウトパネル */
    private JPanel historyPanel;
    
    /** 束縛サポート */
    private PropertyChangeSupport boundSupport;
    
    /** context */
    private ChartPlugin context;
    
    /** 選択された文書情報(DocInfo)の配列 */
    private DocInfoModel[] selectedHistories;
    
    /** 抽出コンテント */
    private String extractionContent;
    
    /** 抽出開始日 */
    private Date extractionPeriod;
    
    /** 自動的に取得する文書数 */
    private int autoFetchCount;
    
    /** 昇順降順のフラグ */
    private boolean ascending;
    
    /** 修正版も表示するかどうかのフラグ */
    private boolean showModified;
    
    /** */
    private boolean start;
    
    /** タイマータスク関連 */
    private Timer taskTimer;
    
    /** Key入力をブロックするリスナ */
    private BlockKeyListener blockKeyListener;
    
    
    /**
     * 文書履歴オブジェクトを生成する。
     * @param owner コンテキシト
     */
    public DocumentHistory(ChartPlugin context) {
        this.context = context;
        initComponent();
        connect();
        start = true;
    }
    
    /**
     * 履歴テーブルのコレクションを clear する。
     */
    public void clear() {
        if (docHistoryTable != null) {
            docHistoryTable.clear();
        }
    }
    
    public void requestFocus() {
        docHistoryTable.getTable().requestFocusInWindow();
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
     * 履歴選択を不可にするための通知を受け、履歴テーブルを dsiabled にする。
     * これは文書本体の取得中等に、履歴の選択ができないようにするためのメソッドである。
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(DocumentViewer.BUSY_PROP)) {
            boolean busy = ((Boolean) e.getNewValue()).booleanValue();
            if (busy) {
                docHistoryTable.getTable().addKeyListener(blockKeyListener);
            } else {
                docHistoryTable.getTable().removeKeyListener(blockKeyListener);
            }
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
        
        if (start && extractionPeriod != null
                && extractionContent != null) {
            
            // 検索パラメータセットのDTOを生成する
            DocumentSearchSpec spec = new DocumentSearchSpec();
            spec.setKarteId(context.getKarte().getId());		// カルテID
            spec.setDocType(extractionContent);					// 文書タイプ
            spec.setFromDate(extractionPeriod);					// 抽出期間開始
            spec.setIncludeModifid(showModified);				// 修正履歴
            spec.setCode(DocumentSearchSpec.DOCTYPE_SEARCH);	// 検索タイプ
            spec.setAscending(ascending);
            
            // ProgressBar
            final IStatusPanel statusPanel = context.getStatusPanel();
            
            // 検索タスクを生成する
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            final DocumentDelegater ddl = new DocumentDelegater();
            final DocInfoTask worker = new DocInfoTask(spec, ddl, maxEstimation/delay);
            
            // タイマーを生成する
            taskTimer = new javax.swing.Timer(delay,
                    new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    
                    worker.getCurrent();
                    statusPanel.setMessage(worker.getMessage());
                    
                    if (worker.isDone()) {
                        taskTimer.stop();
                        statusPanel.stop();
                        
                        // エラーをチェックする
                        if (ddl.isNoError()) {
                            updateHistory(worker.getDocumentList());
                            
                        } else {
                            JFrame parent = context.getFrame();
                            String title = ClientContext.getString("docHistory.title");
                            JOptionPane.showMessageDialog(
                                    parent,
                                    ddl.getErrorMessage(),
                                    ClientContext.getFrameTitle(title),
                                    JOptionPane.WARNING_MESSAGE);
                        }
                        
                    } else if (worker.isTimeOver()) {
                        // タイムアウト表示を行う
                        taskTimer.stop();
                        statusPanel.stop();
                        JFrame parent = context.getFrame();
                        String title = ClientContext.getString("docHistory.title");
                        new TimeoutWarning(parent, title, null).start();
                    }
                }
            });
            // 検索を開始する
            statusPanel.start("");
            countField.setText("");
            worker.start();
            taskTimer.start();
        }
    }
    
    /**
     * 抽出期間等が変化し、履歴を再取得した場合等の処理で、履歴テーブルの更新、 最初の行の自動選択、束縛プロパティの変化通知を行う。
     */
    @SuppressWarnings("unchecked")
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
        docHistoryTable.setObjectList(mewHistory);
        
        // 束縛プロパティの通知を行う
        boundSupport.firePropertyChange(HITORY_UPDATED, false, true);
        
        if (mewHistory != null && mewHistory.size() > 0) {
            
            int cnt = mewHistory.size();
            countField.setText(String.valueOf(cnt));
            int fetchCount = cnt > autoFetchCount ? autoFetchCount : cnt;
            
            // テーブルの最初の行の自動選択を行う
            JTable table = docHistoryTable.getTable();
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
            countField.setText("0");
        }
    }
    
    public void documentSelectionChanged(PropertyChangeEvent e) {
        
        Object[] obj = (Object[]) e.getNewValue();
        
        if (obj != null && obj.length > 0) {
            DocInfoModel[] selectedHistories = new DocInfoModel[obj.length];
            for (int i = 0; i < obj.length; i++) {
                selectedHistories[i] = (DocInfoModel) obj[i];
            }
            setSelectedHistories(selectedHistories);

        } else {
            setSelectedHistories((DocInfoModel[]) null);
        }
    }
       
    /**
     * 文書履歴のタイトルを変更する。
     */
    public void titleChanged(PropertyChangeEvent e) {
        
        DocInfoModel docInfo = (DocInfoModel) e.getNewValue();
        
        if (docInfo != null && docInfo.getTitle() != null) {
            
            // ProgressBar
            final IStatusPanel statusPanel = context.getStatusPanel();
            
            // 検索タスクを生成する
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            final DocumentDelegater ddl = new DocumentDelegater();
            final ChangeTitleTask worker = new ChangeTitleTask(docInfo, ddl, maxEstimation/delay);
            
            // タイマーを生成する
            taskTimer = new javax.swing.Timer(delay,
                    new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    
                    worker.getCurrent();
                    statusPanel.setMessage(worker.getMessage());
                    
                    if (worker.isDone()) {
                        taskTimer.stop();
                        statusPanel.stop();
                        
                        // エラーをチェックする
                        if (ddl.isNoError()) {
                            
                        } else {
                            JFrame parent = context.getFrame();
                            String title = ClientContext.getString("docHistory.title");
                            JOptionPane.showMessageDialog(
                                    parent,
                                    ddl.getErrorMessage(),
                                    ClientContext.getFrameTitle(title),
                                    JOptionPane.WARNING_MESSAGE);
                        }
                        
                    } else if (worker.isTimeOver()) {
                        // タイムアウト表示を行う
                        taskTimer.stop();
                        statusPanel.stop();
                        JFrame parent = context.getFrame();
                        String title = ClientContext.getString("docHistory.title");
                        new TimeoutWarning(parent, title, null).start();
                    }
                }
            });
            // 検索を開始する
            statusPanel.start("");
            countField.setText("");
            worker.start();
            taskTimer.start();
        }
    }
    
    public void periodChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
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
     * GUI コンポーネントを生成する。
     */
    private void initComponent() {
        
        //
        // 履歴テーブルのパラメータを取得する
        //
        String[] columnNames = ClientContext.getStringArray("docHistory.columnNames"); // {"確定日", "内容"};
        String[] methodNames = ClientContext.getStringArray("docHistory.methodNames"); // {"getFirstConfirmDateTrimTime",// "getTitle"};
        Class[] columnClasses = { String.class, String.class };
        int[] columnWidth = ClientContext.getIntArray("docHistory.columnWidth"); // {80,200};
        int startNumRows = ClientContext.getInt("docHistory.startNumRows");
        int[] cellSpacing = ClientContext.getIntArray("docHistory.cellSpacing"); // 7,2
        String extractionText = ClientContext.getString("docHistory.combo.text");
        String countText = ClientContext.getString("docHistory.countField.text");
        NameValuePair[] extractionObjects = ClientContext.getNameValuePair("docHistory.combo.period");
        
        //
        // 文書履歴テーブルを生成する
        //
        docHistoryTable = new ObjectListTable(columnNames, startNumRows, methodNames, columnClasses, new int[]{1});
        docHistoryTable.setColumnWidth(columnWidth);
        docHistoryTable.getTable().setIntercellSpacing(new Dimension(cellSpacing[0], cellSpacing[1]));
        JTextField tf = new JTextField();
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
        });
        TableColumn column = docHistoryTable.getTable().getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(tf));
        
        // 抽出機関 ComboBox を生成する
        extractionCombo = new JComboBox(extractionObjects);
        
        // 件数フィールドを生成する
        countField = new JTextField(2);
        countField.setEditable(false);
        
        // フィルターパネル
        JLabel extractionLabel = new JLabel(extractionText);
        JLabel countLabel = new JLabel(countText);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filter.add(extractionLabel);
        filter.add(extractionCombo);
        filter.add(countLabel);
        filter.add(countField);
        
        historyPanel = new JPanel(new BorderLayout(0, 7));
        historyPanel.add(docHistoryTable.getScroller());
        historyPanel.add(filter, BorderLayout.SOUTH);
    }
    
    /**
     * レイアウトパネルを返す。
     * @return
     */
    public JPanel getPanel() {
        return historyPanel;
    }
    
    /**
     * Event 接続を行う
     */
    private void connect() {
        
        // 文書履歴の選択をリダイレクトする
        docHistoryTable.addPropertyChangeListener(ObjectListTable.SELECTED_OBJECT,
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "documentSelectionChanged", ""));
        
        //
        // 文書タイトルを変更する
        //
        docHistoryTable.addPropertyChangeListener(ObjectListTable.OBJECT_VALUE,
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "titleChanged", ""));
        
        // 抽出期間コンボボックスの選択を処理する
        extractionCombo.addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "periodChanged", "stateChange"));
        
        // Preference から文書種別を設定する
        setExtractionContent("karte");
        
        // Preference から抽出期間を設定する
        NameValuePair[] extractionObjects = ClientContext.getNameValuePair("docHistory.combo.period");
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
        
        //
        // 文書履歴テーブルのキーボード入力をブロックするリスナ
        //
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
        this.extractionContent = extractionContent;
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
    class DocInfoTask extends AbstractInfiniteTask {
        
        // 結果を格納するリスト
        private List result;
        
        // Delegator
        private DocumentDelegater ddl;
        
        // 検索パラメータを保持するオブジェクト
        private DocumentSearchSpec spec;
        
        /**
         * タスクを生成する。
         * @param spec 検索パラメータを保持するオブジェクト
         * @param ddl Delegator
         */
        public DocInfoTask(DocumentSearchSpec spec, DocumentDelegater ddl, int taskLength) {
            this.spec = spec;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        /**
         * 検索結果の文書履歴リストを返す。
         * @return 書履歴リスト
         */
        protected List getDocumentList() {
            return result;
        }
        
        /**
         * 実タスク実行クラス。
         */
        protected void doTask() {
            result = ddl.getDocumentList(spec);
            setDone(true);
        }
    }
    
        
    /**
     * タイトル変更タスククラス。
     */
    class ChangeTitleTask extends AbstractInfiniteTask {
        
        // DocInfo
        private DocInfoModel docInfo;
        
        // Delegator
        private DocumentDelegater ddl;
        
        
        /**
         * タスクを生成する。
         * @param spec 検索パラメータを保持するオブジェクト
         * @param ddl Delegator
         */
        public ChangeTitleTask(DocInfoModel docInfo, DocumentDelegater ddl, int taskLength) {
            this.docInfo = docInfo;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        /**
         * 実タスク実行クラス。
         */
        protected void doTask() {
            ddl.updateTitle(docInfo);
            setDone(true);
        }
    }
}
