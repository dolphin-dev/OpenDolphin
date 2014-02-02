package open.dolphin.client;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import open.dolphin.dao.SqlOrcaView;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.*;
import open.dolphin.order.StampEditor;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.util.BeanUtils;
import open.dolphin.util.MMLDate;

/**
 * DiagnosisDocument
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisDocument extends AbstractChartDocument implements PropertyChangeListener {

    private static final String TITLE = "傷病名";

    // 傷病名テーブルのカラム番号定義
    private static final int DIAGNOSIS_COL  = 0;
    private static final int CATEGORY_COL   = 1;
    private static final int OUTCOME_COL    = 2;
    private static final int START_DATE_COL = 3;
    private static final int END_DATE_COL   = 4;

    // GUI コンポーネント定義
    private static final String RESOURCE_BASE = "/open/dolphin/resources/images/";
    private static final String DELETE_BUTTON_IMAGE = "del_16.gif";
    private static final String ADD_BUTTON_IMAGE = "add_16.gif";
    private static final String UPDATE_BUTTON_IMAGE = "save_16.gif";
    private static final String ORCA_VIEW_IMAGE = "impt_16.gif";
    private static final String ORCA_RECORD = "ORCA";
    private static final String DORCA_RECORD = "DORCA";
    private static final String DORCA_UPDATED = "DORCA_UPDATED";
    private static final String[] COLUMN_TOOLTIPS = new String[]{null,
        "クリックするとコンボボックスが立ち上がります。", "クリックするとコンボボックスが立ち上がります。",
        "右クリックでカレンダがポップアップします。", "右クリックでカレンダがポップアップします。"};

    // GUI Component
    /** JTableレンダラ用の奇数カラー */
    private static final Color ODD_COLOR = ClientContext.getColor("color.odd");

    /** JTableレンダラ用の偶数カラー */
    private static final Color EVEN_COLOR = ClientContext.getColor("color.even");
    private static final Color ORCA_BACK = ClientContext.getColor("color.CALENDAR_BACK");
    
    private JTable diagTable;                   // 病歴テーブル
    private ListTableModel<RegisteredDiagnosisModel> tableModel; // TableModel
    private JComboBox extractionCombo;          // 抽出期間コンボ
    
    private JTextField countField;              // 件数フィールド
    private AbstractAction addAction;           // 新規病名エディタ
    private AbstractAction deleteAction;        // 既存傷病名の削除
    private AbstractAction updateAction;        // 既存傷病名の転帰等の更新
    private AbstractAction orcaAction;          // ORCA action
    private AbstractAction activeAction;        // active病名のみ表示

    // 昇順降順フラグ
    private boolean ascend;

    // アクティブ病名のみ表示
    private boolean activeOnly;

    // 検索開始日
    private Date searchFrom;

    // 新規に追加された傷病名リスト
    List<RegisteredDiagnosisModel> addedDiagnosis;

    // 更新された傷病名リスト
    List<RegisteredDiagnosisModel> updatedDiagnosis;

    // このドキュメントがオープンされた時の Dolphinの傷病名件数
    private int diagnosisCount;
    
    private boolean DEBUG;

    /**
     *  Creates new DiagnosisDocument
     */
    public DiagnosisDocument() {
        setTitle(TITLE);
    }

    /**
     * GUI コンポーネントを生成初期化する。
     */
    private void initialize() {

        // Project から昇順降順を設定する
        ascend = Project.getBoolean(Project.DIAGNOSIS_ASCENDING, false);

        // Projectからアクティブ病名のみを表示するかどうか設定する
        activeOnly = Project.getBoolean("diagnosis.activeOnly");

        // コマンドボタンパネルを生成する
        JPanel cmdPanel = createButtonPanel2();

        // Dolphin 傷病歴パネルを生成する
        JPanel dolphinPanel = createDignosisPanel();

        // 抽出期間パネルを生成する
        JPanel filterPanel = createFilterPanel();

        JPanel content = new JPanel(new BorderLayout(0, 7));
        content.add(cmdPanel, BorderLayout.NORTH);
        content.add(dolphinPanel, BorderLayout.CENTER);
        content.add(filterPanel, BorderLayout.SOUTH);

        // 全体をレイアウトする
        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout(0, 7));
        myPanel.add(content);
        myPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    }

    /**
     * コマンドボタンパネルをする。
     */
    private JPanel createButtonPanel2() {

        // 更新ボタン
        updateAction = new AbstractAction("保存", createImageIcon(UPDATE_BUTTON_IMAGE)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                save();
            }
        };
        updateAction.setEnabled(false);
        JButton updateButton = new JButton(updateAction);
        updateButton.setToolTipText("追加変更した傷病名をデータベースに反映します。");

        // 削除ボタン
        deleteAction = new AbstractAction("削除", createImageIcon(DELETE_BUTTON_IMAGE)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                delete();
            }
        };
        deleteAction.setEnabled(false);
        JButton deleteButton = new JButton(deleteAction);
        deleteButton.setToolTipText("選択した傷病名を削除します。");

        // 新規登録ボタン
        addAction = new AbstractAction("追加", createImageIcon(ADD_BUTTON_IMAGE)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        JButton addButton = new JButton(addAction);
        addButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    // ASP StampBox が選択されていて傷病名Treeがない場合がある
                    if (getContext().getChartMediator().hasTree(IInfoModel.ENTITY_DIAGNOSIS)) {
                        JPopupMenu popup = new JPopupMenu();
                        getContext().getChartMediator().addDiseaseMenu(popup);
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        String msg1 = "現在使用中のスタンプボックスには傷病名がありません。";
                        String msg2 = "個人用のスタンプボックス等に切り替えてください。";
                        Object obj = new String[]{msg1, msg2};
                        String title = ClientContext.getFrameTitle("傷病名追加");
                        Component comp = getUI();
                        JOptionPane.showMessageDialog(comp, obj, title, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        // Depends on readOnly prop
        addAction.setEnabled(!isReadOnly());
        addButton.setToolTipText("傷病名を追加します。");

        // ORCA View
        orcaAction = new AbstractAction("ORCA", createImageIcon(ORCA_VIEW_IMAGE)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                viewOrca();
            }
        };
        JButton orcaButton = new JButton(orcaAction);
        orcaButton.setToolTipText("ORCAに登録してある病名を参照または取り込みます。");

        // ボタンパネル
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(5));
        p.add(orcaButton);
        p.add(Box.createHorizontalGlue());
        p.add(deleteButton);
        p.add(Box.createHorizontalStrut(5));
        p.add(addButton);
        p.add(Box.createHorizontalStrut(5));
        p.add(updateButton);
        return p;
    }

    /**
     * 既傷病歴テーブルを生成する。
     */
    private JPanel createDignosisPanel() {

        String[] columnNames = ClientContext.getStringArray("diagnosis.columnNames");
        String[] methodNames = ClientContext.getStringArray("diagnosis.methodNames");
        Class[] columnClasses = new Class[]{String.class, String.class, String.class, String.class, String.class};
        int startNumRows = ClientContext.getInt("diagnosis.startNumRows");

        // Diagnosis テーブルモデルを生成する
        tableModel = new ListTableModel<RegisteredDiagnosisModel>(columnNames, startNumRows, methodNames, columnClasses) {

            // Diagnosisは編集不可
            @Override
            public boolean isCellEditable(int row, int col) {

                // licenseCodeで制御
                if (isReadOnly()) {
                    return false;
                }

                // 病名レコードが存在しない場合は false
                RegisteredDiagnosisModel entry = getObject(row);
                if (entry == null) {
                    return false;
                }

                // ORCA に登録されている病名の場合
                if (isOrcaDisease(entry)) {
                    return false;
                }

                // それ以外はカラムに依存する
                boolean editable = true;
                editable = editable && (col == CATEGORY_COL || col == OUTCOME_COL || col == START_DATE_COL || col == END_DATE_COL);
                return editable;
            }

            // オブジェクトの値を設定する
            @Override
            public void setValueAt(Object value, int row, int col) {

                RegisteredDiagnosisModel entry = getObject(row);

                if (entry == null || value == null) {
                    return;
                }

                switch (col) {

                    case DIAGNOSIS_COL:
                        break;

                    case CATEGORY_COL:
                        // JComboBox から選択
                        String saveCategory = entry.getCategory();
                        DiagnosisCategoryModel dcm = (DiagnosisCategoryModel) value;
                        String test = dcm.getDiagnosisCategory();
                        test = test != null && (!test.equals("")) ? test : null;

                        if (saveCategory == null && test != null) {
                            entry.setCategory(dcm.getDiagnosisCategory());
                            entry.setCategoryDesc(dcm.getDiagnosisCategoryDesc());
                            entry.setCategoryCodeSys(dcm.getDiagnosisCategoryCodeSys());
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        } else if (saveCategory != null && test == null) {
                            entry.setDiagnosisCategoryModel(null);
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        } else if (saveCategory != null && test != null && (!saveCategory.equals(test))) {
                            entry.setCategory(dcm.getDiagnosisCategory());
                            entry.setCategoryDesc(dcm.getDiagnosisCategoryDesc());
                            entry.setCategoryCodeSys(dcm.getDiagnosisCategoryCodeSys());
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        }
                        break;

                    case OUTCOME_COL:
                        // JComboBox から選択
                        String saveOutcome = entry.getOutcome();
                        DiagnosisOutcomeModel dom = (DiagnosisOutcomeModel) value;
                        test = dom.getOutcome();
                        test = test != null && (!test.equals("")) ? test : null;

                        if (saveOutcome == null && test != null) {
                            //System.err.println("saveOutcome == null && test != null");
                            entry.setOutcome(dom.getOutcome());
                            entry.setOutcomeDesc(dom.getOutcomeDesc());
                            entry.setOutcomeCodeSys(dom.getOutcomeCodeSys());
                            // 疾患終了日を入れる
                            if (Project.getBoolean("autoOutcomeInput", false)) {
                                String val = entry.getEndDate();
                                if (val == null || val.equals("")) {
                                    GregorianCalendar gc = new GregorianCalendar();
                                    int offset = Project.getInt(Project.OFFSET_OUTCOME_DATE, -7);
                                    gc.add(Calendar.DAY_OF_MONTH, offset);
                                    String today = MMLDate.getDate(gc);
                                    entry.setEndDate(today);
                                }
                            }
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);

                        } else if (saveOutcome != null && test == null) {
                            //System.err.println("saveOutcome != null && test == null");
                            entry.setDiagnosisOutcomeModel(null);
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);

                        } else if (saveOutcome != null && test != null && (!saveOutcome.equals(test))) {
                            //System.err.println("saveOutcome != null && test != null && (!saveOutcome.equals(test))");
                            entry.setOutcome(dom.getOutcome());
                            entry.setOutcomeDesc(dom.getOutcomeDesc());
                            entry.setOutcomeCodeSys(dom.getOutcomeCodeSys());
                            // 疾患終了日を入れる
                            if (Project.getBoolean("autoOutcomeInput", false)) {
                                String val = entry.getEndDate();
                                if (val == null || val.equals("")) {
                                    GregorianCalendar gc = new GregorianCalendar();
                                    int offset = Project.getInt(Project.OFFSET_OUTCOME_DATE, -7);
                                    gc.add(Calendar.DAY_OF_MONTH, offset);
                                    String today = MMLDate.getDate(gc);
                                    entry.setEndDate(today);
                                }
                            }
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        }
                        break;

                    // case FIRST_ENCOUNTER_COL:
                    // if (value != null && ! ((String)value).trim().equals("") ) {
                    // entry.setFirstEncounterDate((String)value);
                    // entry.setStatus('M');
                    // fireTableRowsUpdated(row, row);
                    // setDirty(true);
                    // }
                    // break;

                    case START_DATE_COL:
                        String strVal = (String) value;
                        if (!strVal.trim().equals("")) {
                            entry.setStartDate(strVal);
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        }
                        break;

                    case END_DATE_COL:
                        //System.err.println("END_DATE_COL");
                        strVal = ((String) value).trim();
                        System.err.println(strVal);
                        strVal = strVal.equals("") ? null : strVal;
                        entry.setEndDate(strVal);
                        fireTableRowsUpdated(row, row);
                        addUpdatedList(entry);
                        break;
                }
            }
        };

        // 傷病歴テーブルを生成する
        diagTable = new JTable(tableModel) {

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    @Override
                    public String getToolTipText(MouseEvent e) {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        return COLUMN_TOOLTIPS[realIndex];
                    }
                };
            }
        };

        // 行高
        diagTable.setRowHeight(ClientContext.getMoreHigherRowHeight());

        // 奇数、偶数行の色分けをする
        diagTable.setDefaultRenderer(Object.class, new DolphinOrcaRenderer());

        // ??
        diagTable.setSurrendersFocusOnKeystroke(true);

        // 行選択が起った時のリスナを設定する
        diagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        diagTable.setRowSelectionAllowed(true);
        ListSelectionModel m = diagTable.getSelectionModel();
        m.addListSelectionListener((ListSelectionListener) EventHandler.create(ListSelectionListener.class, this, "rowSelectionChanged", ""));

        // Category comboBox 入力を設定する
        String[] values = ClientContext.getStringArray("diagnosis.category");
        String[] descs = ClientContext.getStringArray("diagnosis.categoryDesc");
        String[] codeSys = ClientContext.getStringArray("diagnosis.categoryCodeSys");
        DiagnosisCategoryModel[] categoryList = new DiagnosisCategoryModel[values.length + 1];
        DiagnosisCategoryModel dcm = new DiagnosisCategoryModel();
        dcm.setDiagnosisCategory("");
        dcm.setDiagnosisCategoryDesc("");
        dcm.setDiagnosisCategoryCodeSys("");
        categoryList[0] = dcm;
        for (int i = 0; i < values.length; i++) {
            dcm = new DiagnosisCategoryModel();
            dcm.setDiagnosisCategory(values[i]);
            dcm.setDiagnosisCategoryDesc(descs[i]);
            dcm.setDiagnosisCategoryCodeSys(codeSys[i]);
            categoryList[i + 1] = dcm;
        }
        JComboBox categoryCombo = new JComboBox(categoryList);
        TableColumn column = diagTable.getColumnModel().getColumn(CATEGORY_COL);
        column.setCellEditor(new DefaultCellEditor(categoryCombo));

        // Outcome comboBox 入力を設定する
        String[] ovalues = ClientContext.getStringArray("diagnosis.outcome");
        String[] odescs = ClientContext.getStringArray("diagnosis.outcomeDesc");
        String ocodeSys = ClientContext.getString("diagnosis.outcomeCodeSys");
        DiagnosisOutcomeModel[] outcomeList = new DiagnosisOutcomeModel[ovalues.length + 1];
        DiagnosisOutcomeModel dom = new DiagnosisOutcomeModel();
        dom.setOutcome("");
        dom.setOutcomeDesc("");
        dom.setOutcomeCodeSys("");
        outcomeList[0] = dom;
        for (int i = 0; i < ovalues.length; i++) {
            dom = new DiagnosisOutcomeModel();
            dom.setOutcome(ovalues[i]);
            dom.setOutcomeDesc(odescs[i]);
            dom.setOutcomeCodeSys(ocodeSys);
            outcomeList[i + 1] = dom;
        }
        JComboBox outcomeCombo = new JComboBox(outcomeList);
        column = diagTable.getColumnModel().getColumn(OUTCOME_COL);
        column.setCellEditor(new DefaultCellEditor(outcomeCombo));

        //
        // Start Date && EndDate Col にポップアップカレンダーを設定する
        // IME を OFF にする
        //
        String datePattern = ClientContext.getString("common.pattern.mmlDate");
        column = diagTable.getColumnModel().getColumn(START_DATE_COL);
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        PopupListener pl1 = new PopupListener(tf);
        tf.setDocument(new RegexConstrainedDocument(datePattern));
        DefaultCellEditor de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        int clickCountToStart = Project.getInt("diagnosis.table.clickCountToStart", 1);
        de.setClickCountToStart(clickCountToStart);

        column = diagTable.getColumnModel().getColumn(END_DATE_COL);
        tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        tf.setDocument(new RegexConstrainedDocument(datePattern));
        PopupListener pl2 = new PopupListener(tf);
        de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        de.setClickCountToStart(clickCountToStart);

        //-----------------------------------------------
        // TransferHandler を設定する
        //-----------------------------------------------
        diagTable.setTransferHandler(new DiagnosisTransferHandler(this));
        diagTable.setDragEnabled(true);

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        final AbstractAction copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        diagTable.getInputMap().put(copy, "Copy");
        diagTable.getActionMap().put("Copy", copyAction);

        //-------------------------------------------------
        // Copy menu を加える
        //-------------------------------------------------
        diagTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }

            @Override
            public void mousePressed(MouseEvent me) {
                mabeShowPopup(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mabeShowPopup(me);
            }

            public void mabeShowPopup(MouseEvent e) {
                
                if (!e.isPopupTrigger()) {
                    return;
                }

                int row = diagTable.rowAtPoint(e.getPoint());
                RegisteredDiagnosisModel obj = tableModel.getObject(row);
                int selected = diagTable.getSelectedRow();

                if (row < 0 || row != selected || obj == null) {
                    return;
                }

                // ORCA 病名かどうか
                boolean selectedIsOrca = true;
                selectedIsOrca = selectedIsOrca && (obj.getStatus()!=null && obj.getStatus().equals(ORCA_RECORD));

                JPopupMenu contextMenu = new JPopupMenu();
                contextMenu.add(new JMenuItem(copyAction));

                if (!selectedIsOrca) {
                    contextMenu.addSeparator();
                    contextMenu.add(new JMenuItem(new ReflectAction("削除", DiagnosisDocument.this, "delete")));
                }

                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        // Layout
        JScrollPane scroller = new JScrollPane(diagTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel p = new JPanel(new BorderLayout());
        p.add(scroller, BorderLayout.CENTER);
        return p;
    }

    /**
     * 抽出期間パネルを生成する。
     */
    private JPanel createFilterPanel() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(7));

        // 抽出期間コンボボックス
        p.add(new JLabel("抽出期間(過去)"));
        NameValuePair[] extractionObjects = ClientContext.getNameValuePair("diagnosis.combo.period");
        extractionCombo = new JComboBox(extractionObjects);
        int currentDiagnosisPeriod = Project.getInt(Project.DIAGNOSIS_PERIOD, 0);
        int selectIndex = NameValuePair.getIndex(String.valueOf(currentDiagnosisPeriod), extractionObjects);
        extractionCombo.setSelectedIndex(selectIndex);
        extractionCombo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "extPeriodChanged", ""));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        comboPanel.add(extractionCombo);

        // Active 病名のみ表示 ToDo
        activeAction = new AbstractAction("アクティブ病名のみ") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                activeOnly = !activeOnly;
                getDiagnosisHistory();
            }
        };
        JCheckBox activeBox = new JCheckBox();
        activeBox.setSelected(activeOnly);
        activeBox.setAction(activeAction);
        comboPanel.add(activeBox);

        p.add(comboPanel);
        p.add(Box.createHorizontalGlue());

        // 件数フィールド
        countField = new JTextField(2);
        countField.setEditable(false);
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        countPanel.add(new JLabel("件数"));
        countPanel.add(countField);

        p.add(countPanel);
        p.add(Box.createHorizontalStrut(7));
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        return p;
    }

    /**
     * 行選択が起った時のボタン制御を行う。
     */
    public void rowSelectionChanged(ListSelectionEvent e) {

        if (e.getValueIsAdjusting() == false) {

            // CLAIM 送信をdiasabled
            getContext().enabledAction(GUIConst.ACTION_SEND_CLAIM, false);

            // 削除ボタンをコントロールする
            // licenseCode 制御を追加
            if (isReadOnly()) {
                return;
            }

            // 選択された行のオブジェクトを得る
            int row = diagTable.getSelectedRow();
            RegisteredDiagnosisModel rd = tableModel.getObject(row);

            // ヌルの場合
            if (rd == null) {
                deleteAction.setEnabled(false);
                return;
            }

            // ORCA の場合
            if (isOrcaDisease(rd)) {
                deleteAction.setEnabled(false);
                return;
            }

            // Dolphin の場合
            if (!deleteAction.isEnabled()) {
                deleteAction.setEnabled(true);
            }

            // CLAIM 送信を制御
            boolean sendOk = true;
            sendOk = sendOk && (getContext().isSendClaim());
            getContext().enabledAction(GUIConst.ACTION_SEND_CLAIM, sendOk);
        }
    }

    /**
     * 抽出期間を変更した場合に再検索を行う。
     * ORCA 病名ボタンが disable であれば検索後に enable にする。
     */
    public void extPeriodChanged(ItemEvent e) {       
        if (e.getStateChange() == ItemEvent.SELECTED) {
            getDiagnosisHistory();
        }
    }

    public JTable getDiagnosisTable() {
        return diagTable;
    }

    @Override
    public void start() {
        initialize();
        getDiagnosisHistory();
        enter();
    }

    @Override
    public void stop() {
        if (tableModel != null) {
            tableModel.clear();
        }
    }

    @Override
    public void enter() {
        super.enter();
    }

    /**
     * 新規傷病名リストに追加する。
     * @param added 追加されたRegisteredDiagnosisModel
     */
    private void addAddedList(RegisteredDiagnosisModel added) {
        if (addedDiagnosis == null) {
            addedDiagnosis = new ArrayList<RegisteredDiagnosisModel>(5);
        }
        addedDiagnosis.add(added);
        controlUpdateButton();
    }

    private void addAllAddedList(List<RegisteredDiagnosisModel> list) {
        if (addedDiagnosis == null) {
            addedDiagnosis = new ArrayList<RegisteredDiagnosisModel>(5);
        }
        addedDiagnosis.addAll(list);
        controlUpdateButton();
    }

    private boolean isOrcaDisease(RegisteredDiagnosisModel test) {
        return (test!=null && test.getStatus()!=null && test.getStatus().equals(ORCA_RECORD));
    }

    private boolean isDorcaDisease(RegisteredDiagnosisModel test) {
        return (test!=null && test.getStatus()!=null && test.getStatus().equals(DORCA_RECORD));
    }

    private boolean isDorcaUpdatedDisease(RegisteredDiagnosisModel test) {
        return (test!=null && test.getStatus()!=null && test.getStatus().equals(DORCA_UPDATED));
    }

    private boolean isPureDisease(RegisteredDiagnosisModel test) {
        return (test!=null && test.getStatus()!=null && test.getStatus().equals(IInfoModel.STATUS_FINAL));
    }

    /**
     * 更新リストに追加する。
     * @param updated 更新されたRegisteredDiagnosisModel
     */
    private void addUpdatedList(RegisteredDiagnosisModel updated) {

        // ディタッチオブジェクトの時
        if (updated.getId() != 0L) {
            // 更新リストに追加する
            if (updatedDiagnosis == null) {
                updatedDiagnosis = new ArrayList<RegisteredDiagnosisModel>(5);
            }
            // 同じものが再度更新されているケースを除く
            if (!updatedDiagnosis.contains(updated)) {
                updatedDiagnosis.add(updated);
            }
            controlUpdateButton();

        } else if (isDorcaDisease(updated)) {
            // DORCA 病名の場合 -> DORCA_UPDATED -> CLAIM送信
            //System.err.println(updated.getDiagnosis());
            updated.setStatus(DORCA_UPDATED);
        }
    }

    /**
     * 追加及び更新リストをクリアする。
     */
    private void clearDiagnosisList() {

        if (addedDiagnosis != null && addedDiagnosis.size() > 0) {
            while (addedDiagnosis.size() > 0) {
                addedDiagnosis.remove(0);
            }
        }

        if (updatedDiagnosis != null && updatedDiagnosis.size() > 0) {
            while (updatedDiagnosis.size() > 0) {
                updatedDiagnosis.remove(0);
            }
        }

        controlUpdateButton();
    }

    /**
     * 更新ボタンを制御する。
     */
    private void controlUpdateButton() {
        boolean hasAdded = (addedDiagnosis != null && addedDiagnosis.size() > 0);
        boolean hasUpdated = (updatedDiagnosis != null && updatedDiagnosis.size() > 0);
        boolean newDirty = (hasAdded || hasUpdated);
        boolean old = isDirty();
        if (old != newDirty) {
            setDirty(newDirty);
            updateAction.setEnabled(isDirty());
        }
        // ORCA
        boolean orca = (tableModel.getObjectCount()==0);
        if (!orcaAction.isEnabled() && orca) {
            orcaAction.setEnabled(orca);
        }
    }

    /**
     * 傷病名件数を返す。
     * @return 傷病名件数
     */
    public int getDiagnosisCount() {
        return diagnosisCount;
    }

    /**
     * 傷病名件数を設定する。
     * @param cnt 傷病名件数
     */
    public void setDiagnosisCount(int cnt) {
        diagnosisCount = cnt;
        try {
            String val = String.valueOf(diagnosisCount);
            countField.setText(val);
        } catch (RuntimeException e) {
            countField.setText("");
        }
    }

    /**
     * ImageIcon を返す
     */
    private ImageIcon createImageIcon(String name) {
        String res = RESOURCE_BASE + name;
        return new ImageIcon(this.getClass().getResource(res));
    }

    /**
     * 傷病名スタンプを取得する worker を起動する。
     */
    public void importStampList(final List<ModuleInfoBean> stampList, final int insertRow) {

        final StampDelegater sdl = new StampDelegater();

        DBTask task = new DBTask<List<StampModel>, Void>(getContext()) {

            @Override
            protected List<StampModel> doInBackground() throws Exception {
                List<StampModel> result = sdl.getStamp(stampList);
                return result;
            }

            @Override
            protected void succeeded(List<StampModel> list) {
                trace("importStampList succeeded");
                if (list != null) {
                    for (int i = list.size() - 1; i > -1; i--) {
                        insertStamp((StampModel) list.get(i), insertRow);
                    }
                }
            }
        };

        task.execute();
    }

    /**
     * 傷病名スタンプをデータベースから取得しテーブルへ挿入する。
     * Worker Thread で実行される。
     * @param stampInfo
     */
    private void insertStamp(StampModel sm, int row) {

        if (sm != null) {
            RegisteredDiagnosisModel module = (RegisteredDiagnosisModel) BeanUtils.xmlDecode(sm.getStampBytes());

            // 今日の日付を疾患開始日として設定する
            GregorianCalendar gc = new GregorianCalendar();
            String today = MMLDate.getDate(gc);
            module.setStartDate(today);

            row = tableModel.getObjectCount() == 0 ? 0 : row;
            int cnt = tableModel.getObjectCount();
            if (row == 0 && cnt == 0) {
                tableModel.addObject(module);
            } else if (row < cnt) {
                tableModel.addObject(row, module);
            } else {
                tableModel.addObject(module);
            }

            // row を選択する
            diagTable.getSelectionModel().setSelectionInterval(row, row);

            addAddedList(module);
        }
    }

    /**
     * 傷病名エディタを開く。
     */
    public void openEditor2() {
        Window lock = SwingUtilities.getWindowAncestor(this.getUI());
        StampEditor editor = new StampEditor("diagnosis", this, lock);
    }

    /**
     * 傷病名エディタからデータを受け取りテーブルへ追加する。
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {

        ArrayList list = (ArrayList) e.getNewValue();
        if (list == null || list.isEmpty()) {
            return;
        }

        int len = list.size();
        // 今日の日付を疾患開始日として設定する
        GregorianCalendar gc = new GregorianCalendar();
        String today = MMLDate.getDate(gc);

        if (ascend) {
            // 昇順なのでテーブルの最後へ追加する
            for (int i = 0; i < len; i++) {
                RegisteredDiagnosisModel module = (RegisteredDiagnosisModel) list.get(i);
                module.setStartDate(today);
                tableModel.addObject(module);
                addAddedList(module);
            }

        } else {
            // 降順なのでテーブルの先頭へ追加する
            for (int i = len - 1; i > -1; i--) {
                RegisteredDiagnosisModel module = (RegisteredDiagnosisModel) list.get(i);
                module.setStartDate(today);
                tableModel.addObject(0, module);
                addAddedList(module);
            }
        }
    }
    
    private boolean isValidOutcome(RegisteredDiagnosisModel rd) {
        
        if (rd.getOutcome() == null) {
            return true;
        }
        
        String start = rd.getStartDate();
        String end = rd.getEndDate();
        
        if (start == null) {
            JOptionPane.showMessageDialog(
                    getContext().getFrame(),
                    "疾患の開始日がありません。",
                    ClientContext.getFrameTitle("病名チェック"),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (end == null) {
            JOptionPane.showMessageDialog(
                    getContext().getFrame(),
                    "疾患の終了日がありません。",
                    ClientContext.getFrameTitle("病名チェック"),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        Date startDate = null;
        Date endDate = null;
        boolean formatOk = true;
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            start = start.replaceAll("/", "-");
            end = end.replaceAll("/", "-");
            startDate = sdf.parse(start);
            endDate = sdf.parse(end);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("日付のフォーマットが正しくありません。");
            sb.append("\n");
            sb.append("「yyyy-MM-dd」の形式で入力してください。");
            sb.append("\n");
            sb.append("右クリックでカレンダが使用できます。");
            JOptionPane.showMessageDialog(
                    getContext().getFrame(),
                    sb.toString(),
                    ClientContext.getFrameTitle("病名チェック"),
                    JOptionPane.WARNING_MESSAGE);
            formatOk = false;
        }
        
        if (!formatOk) {
            return false;
        }
        
        if (endDate.before(startDate)) {
            StringBuilder sb = new StringBuilder();
            sb.append("疾患の終了日が開始日以前になっています。");
            JOptionPane.showMessageDialog(
                    getContext().getFrame(),
                    sb.toString(),
                    ClientContext.getFrameTitle("病名チェック"),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * 新規及び変更された傷病名を保存する。
     */
    @Override
    public void save() {

        if ((addedDiagnosis == null || addedDiagnosis.isEmpty()) &&
            (updatedDiagnosis == null || updatedDiagnosis.isEmpty())) {
            return;
        }

        final boolean sendDiagnosis = Project.getBoolean(Project.SEND_DIAGNOSIS) && ((ChartImpl) getContext()).getCLAIMListener() != null ? true : false;
        trace("sendDiagnosis = " + sendDiagnosis);

        // continue to save
        Date confirmed = new Date();
        trace("confirmed = " + confirmed);
        
        boolean go = true;

        if (addedDiagnosis != null && addedDiagnosis.size() > 0) {

            for (RegisteredDiagnosisModel rd : addedDiagnosis) {
                
                trace("added rd = " + rd.getDiagnosis());
                trace("id = " + rd.getId());

                // 開始日、終了日はテーブルから取得している
                // TODO confirmed, recorded
                rd.setKarteBean(getContext().getKarte());       // Karte
                rd.setUserModel(Project.getUserModel());        // Creator
                rd.setConfirmed(confirmed);                     // 確定日
                rd.setRecorded(confirmed);                      // 記録日

                // DORCA || DORCA_UPDATE 病名の status は変えない
                if ((!isDorcaDisease(rd)) && (!isDorcaUpdatedDisease(rd))) {
                    // Status Final を設定する
                    rd.setStatus(IInfoModel.STATUS_FINAL);
                }

                // 開始日=適合開始日 not-null
                if (rd.getStarted() == null) {
                    rd.setStarted(confirmed);
                }

                // TODO トラフィック
                rd.setPatientLiteModel(getContext().getPatient().patientAsLiteModel());
                rd.setUserLiteModel(Project.getUserModel().getLiteModel());
                
                // 転帰をチェックする
                if (!isValidOutcome(rd)) {
                    go = false;
                    break;
                }
            }
        }
        
        if (!go) {
            return;
        }

        if (updatedDiagnosis != null && updatedDiagnosis.size() > 0) {

            for (RegisteredDiagnosisModel rd : updatedDiagnosis) {
                
                trace("updated rd = " + rd.getDiagnosis());
                trace("id = " + rd.getId());

                // 現バージョンは上書きしている
                rd.setKarteBean(getContext().getKarte());           // Karte
                rd.setUserModel(Project.getUserModel());            // Creator
                rd.setConfirmed(confirmed);
                rd.setRecorded(confirmed);
                // updatedList へ入っているのは detuched object のみ
                rd.setStatus(IInfoModel.STATUS_FINAL);

                // TODO トラフィック
                rd.setPatientLiteModel(getContext().getPatient().patientAsLiteModel());
                rd.setUserLiteModel(Project.getUserModel().getLiteModel());
                
                // 転帰をチェックする
                if (!isValidOutcome(rd)) {
                    go = false;
                    break;
                }
            }
        }
        
        if (!go) {
            return;
        }

        DocumentDelegater ddl = new DocumentDelegater();
        DiagnosisPutTask task = new DiagnosisPutTask(getContext(), addedDiagnosis, updatedDiagnosis, sendDiagnosis, ddl);
        task.execute();
    }

    /**
     * 指定期間以降の傷病名を検索してテーブルへ表示する。
     * バッググランドスレッドで実行される。
     */
    //public void getDiagnosisHistory(final Date past) {
    public void getDiagnosisHistory() {

        NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
        int past = Integer.parseInt(pair.getValue());
        if (past != 0) {
            GregorianCalendar today = new GregorianCalendar();
            today.add(GregorianCalendar.MONTH, past);
            today.clear(Calendar.HOUR_OF_DAY);
            today.clear(Calendar.MINUTE);
            today.clear(Calendar.SECOND);
            today.clear(Calendar.MILLISECOND);
            searchFrom = today.getTime();
        } else {
            searchFrom = new Date(0L);
        }

        DBTask task = new DBTask<List<RegisteredDiagnosisModel>, Void>(getContext()) {

            @Override
            protected List<RegisteredDiagnosisModel> doInBackground() throws Exception {
                DocumentDelegater ddl = new DocumentDelegater();
                List<RegisteredDiagnosisModel> result = ddl.getDiagnosisList(getContext().getKarte().getId(), searchFrom, activeOnly);
                return result;
            }

            @Override
            protected void succeeded(List<RegisteredDiagnosisModel> list) {
                if (list != null) {
                    if (ascend) {
                        Collections.sort(list);
                    } else {
                        Collections.sort(list, Collections.reverseOrder());
                    }
                    tableModel.setDataProvider(list);
                    setDiagnosisCount(list.size());
                }
            }
        };

        task.execute();
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        StringBuilder sb = new StringBuilder();
        int numRows = diagTable.getSelectedRowCount();
        int[] rowsSelected = diagTable.getSelectedRows();
        int numColumns =   diagTable.getColumnCount();

        for (int i = 0; i < numRows; i++) {
            if (tableModel.getObject(rowsSelected[i]) != null) {
                StringBuilder s = new StringBuilder();
                for (int col = 0; col < numColumns; col++) {
                    Object o = diagTable.getValueAt(rowsSelected[i], col);
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
        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }

    /**
     * 選択された行のデータを削除する。
     */
    public void delete() {

        // 選択された行のオブジェクトを取得する
        final int row = diagTable.getSelectedRow();
        final RegisteredDiagnosisModel model = (RegisteredDiagnosisModel)tableModel.getObject(row);
        if (model==null) {
            return;
        }

        // まだデータベースに登録されていないデータの場合
        // テーブルから削除してリターンする
        if (model.getId()==0L) {
            //----------------------------------------------------
            // ???? addedDiagnosis.remove(model)!=valid
            //----------------------------------------------------
            int indexToremove = -1;
            for (int i = 0; i <addedDiagnosis.size(); i++) {
                RegisteredDiagnosisModel r = addedDiagnosis.get(i);
                if (r==model) {
                    indexToremove = i;
                    break;
                }
            }

            trace("indexToremove=" + indexToremove);

            if (indexToremove>=0 && indexToremove<addedDiagnosis.size()) {
                addedDiagnosis.remove(indexToremove);
            }
            tableModel.deleteAt(row);
            controlUpdateButton();
            return;
        }

        // ディタッチオブジェクトの場合はデータベースから削除する
        // 削除の場合はその場でデータベースの更新を行う 2006-03-25
        final List<Long> list = new ArrayList<Long>(1);
        list.add(new Long(model.getId()));

        DBTask task = new DBTask<Void, Void>(getContext()) {

            @Override
            protected Void doInBackground() throws Exception {
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeDiagnosis(list);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                // 更新リストにある場合
                // 更新リストから取り除く
                if (updatedDiagnosis != null) {
                    //----------------------------------------------------
                    // ???? updatedDiagnosis.remove(model)!=valid
                    //----------------------------------------------------
                    int indexToremove = -1;
                    for (int i = 0; i <updatedDiagnosis.size(); i++) {
                        RegisteredDiagnosisModel r = updatedDiagnosis.get(i);
                        if (r==model) {
                            indexToremove = i;
                            break;
                        }
                    }

                    trace("indexToremove=" + indexToremove);

                    if (indexToremove>=0 && indexToremove<updatedDiagnosis.size()) {
                        updatedDiagnosis.remove(indexToremove);
                    }
                }
                tableModel.deleteAt(row);
                controlUpdateButton();
            }
        };

        task.execute();
    }

//    /**
//     * ORCAに登録されている病名を取り込む。（テーブルへ追加する）
//     * 検索後、ボタンを disabled にする。
//     */
//    public void viewOrca() {
//
//        // 患者IDを取得する
//        final String patientId = getContext().getPatient().getPatientId();
//
//        // 抽出期間から検索範囲の最初の日を取得する
//        NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
//        int past = Integer.parseInt(pair.getValue());
//
//        // 検索開始日
//        Date date = null;
//        if (past != 0) {
//            GregorianCalendar today = new GregorianCalendar();
//            today.add(GregorianCalendar.MONTH, past);
//            today.clear(Calendar.HOUR_OF_DAY);
//            today.clear(Calendar.MINUTE);
//            today.clear(Calendar.SECOND);
//            today.clear(Calendar.MILLISECOND);
//            date = today.getTime();
//        } else {
//            // 増田内科
//            date = new Date(0L);
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        final String from = sdf.format(date);
//
//        // 検索終了日=今日
//        final String to = sdf.format(new Date());
//        ClientContext.getBootLogger().debug("from = " + from);
//        ClientContext.getBootLogger().debug("to = " + to);
//
//        DBTask task = new DBTask<List<RegisteredDiagnosisModel>, Void>(getContext()) {
//
//            @Override
//            protected List<RegisteredDiagnosisModel> doInBackground() throws Exception {
//                SqlOrcaView dao = new SqlOrcaView();
//                List<RegisteredDiagnosisModel> result = dao.getOrcaDisease(patientId, from, to, ascend);
//                if (dao.isNoError()) {
//                    return result;
//                } else {
//                    throw new Exception(dao.getErrorMessage());
//                }
//            }
//
//            @Override
//            protected void succeeded(List<RegisteredDiagnosisModel> result) {
//
//                // 空ならリターーンするしかない
//                if (result==null || result.isEmpty()) {
//                    orcaAction.setEnabled(true);
//                    return;
//                }
//
//                // Dolphinに病名が登録されている場合は単純に参照として追加する
//                if (diagnosisCount!=0) {
//                    refferenceAddOrca(result);
//                    return;
//                }
//
//                //-------------------------------------------
//                // diagnosisCount==0 && !result.isEmpty()
//                // ORCAに登録してある病名を取り込むかどうかを選択させる
//                // 取り込む場合は status をクリアし + または DnD された
//                // 新規病名として扱う
//                // 取り込まない場合はそのままtableに追加する。
//                //-------------------------------------------
//                if (diagnosisCount==0) {
//                    String importYes = "取り込む";
//                    String importNo = "参照のみ";
//                    String title = "病名取り込み";
//                    Object[] cstOptions = new Object[]{importYes, importNo};
//
//                    String msg = "ORCAに登録してある病名を取り込みますか?";
//                    int select = JOptionPane.showOptionDialog(
//                            getContext().getFrame(),
//                            msg,
//                            ClientContext.getFrameTitle(title),
//                            JOptionPane.YES_NO_CANCEL_OPTION,
//                            JOptionPane.QUESTION_MESSAGE,
//                            ClientContext.getImageIcon("impt_32.gif"),
//                            cstOptions,
//                            importYes);
//
//                    if (select == 0) {
//                        // 取り込む -> DORCA 病名にする
//                        for (RegisteredDiagnosisModel rdm : result) {
//                            rdm.setStatus(DORCA_RECORD);
//                        }
//                        // 新規病名リストに追加する
//                        addAllAddedList(result);
//                        // disabledにする
//                        orcaAction.setEnabled(false);
//                        extractionCombo.setEnabled(false);
//                        //--------------------------------------------------------
//                        // もしtableに病名レコードがあれば（+ or DnD diagCount=0 で追加）
//                        // 全体をソートする
//                        //--------------------------------------------------------
//                        List<RegisteredDiagnosisModel> data = tableModel.getDataProvider();
//                        if (data!=null) {
//                            data.addAll(result);
//                            sortDiasease(data);
//                            tableModel.fireTableDataChanged();
//
//                        } else {
//                            sortDiasease(result);
//                            tableModel.setDataProvider(result);
//                        }
//
//                    } else {
//                        // 参照追加する
//                        refferenceAddOrca(result);
//                    }
//                }
//            }
//        };
//
//        task.execute();
//    }


    public void viewOrca() {

        // 患者IDを取得する
        final String patientId = getContext().getPatient().getPatientId();

        DBTask task = new DBTask<List<RegisteredDiagnosisModel>, Void>(getContext()) {

            @Override
            protected List<RegisteredDiagnosisModel> doInBackground() throws Exception {
                SqlOrcaView dao = new SqlOrcaView();
                List<RegisteredDiagnosisModel> result = dao.getActiveOrcaDisease(patientId,ascend);
                if (dao.isNoError()) {
                    return result;
                } else {
                    throw new Exception(dao.getErrorMessage());
                }
            }

            @Override
            protected void succeeded(List<RegisteredDiagnosisModel> result) {

                // 空ならリターーンするしかない
                if (result==null || result.isEmpty()) {
                    orcaAction.setEnabled(true);
                    return;
                }

                // Dolphinに病名が登録されている場合は単純に参照として追加する
                if (diagnosisCount!=0) {
                    refferenceAddOrca(result);
                    return;
                }

                //-------------------------------------------
                // diagnosisCount==0 && !result.isEmpty()
                // ORCAに登録してある病名を取り込むかどうかを選択させる
                // 取り込む場合は status をクリアし + または DnD された
                // 新規病名として扱う
                // 取り込まない場合はそのままtableに追加する。
                //-------------------------------------------
                if (diagnosisCount==0) {
                    String importYes = "取り込む";
                    String importNo = "参照のみ";
                    String title = "病名取り込み";
                    Object[] cstOptions = new Object[]{importYes, importNo};

                    String msg = "ORCAに登録してある病名を取り込みますか?";
                    int select = JOptionPane.showOptionDialog(
                            getContext().getFrame(),
                            msg,
                            ClientContext.getFrameTitle(title),
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            ClientContext.getImageIcon("impt_32.gif"),
                            cstOptions,
                            importYes);

                    if (select == 0) {
                        // 取り込む -> DORCA 病名にする
                        for (RegisteredDiagnosisModel rdm : result) {
                            rdm.setStatus(DORCA_RECORD);
                        }
                        // 新規病名リストに追加する
                        addAllAddedList(result);
                        // disabledにする
                        orcaAction.setEnabled(false);
                        extractionCombo.setEnabled(false);
                        //--------------------------------------------------------
                        // もしtableに病名レコードがあれば（+ or DnD diagCount=0 で追加）
                        // 全体をソートする
                        //--------------------------------------------------------
                        List<RegisteredDiagnosisModel> data = tableModel.getDataProvider();
                        if (data!=null) {
                            data.addAll(result);
                            sortDiasease(data);
                            tableModel.fireTableDataChanged();

                        } else {
                            //sortDiasease(result);
                            tableModel.setDataProvider(result);
                        }

                    } else if (select == 1){
                        // 参照追加する
                        refferenceAddOrca(result);
                    } 
                }
            }
        };

        task.execute();
    }

    // ORCA病名を傷病名テーブルに参照追加する
    private void refferenceAddOrca(List<RegisteredDiagnosisModel> result) {
        //sortDiasease(result);
        tableModel.addAll(result);
        orcaAction.setEnabled(true);
    }

    private void sortDiasease(List<RegisteredDiagnosisModel> data) {
        if (ascend) {
            Collections.sort(data);
        } else {
            Collections.sort(data, Collections.reverseOrder());
        }
    }


    /**
     * 選択された診断を CLAIM 送信する
     * 元町皮ふ科
     */
    public void sendClaim() {
        
        // 選択された診断を CLAIM 送信する
        RegisteredDiagnosisModel rd;
        List diagList = new ArrayList();
        Date confirmed = new Date();
        int rows[] = diagTable.getSelectedRows();
        for (int row : rows) {
            rd = (RegisteredDiagnosisModel) tableModel.getObject(row);
            rd.setKarteBean(getContext().getKarte());           // Karte
            rd.setUserModel(Project.getUserModel());          // Creator
            rd.setConfirmed(confirmed);                     // 確定日
            rd.setRecorded(confirmed);                      // 記録日
            // 開始日=適合開始日 not-null
            if (rd.getStarted() == null) {
              rd.setStarted(confirmed);
            }
            rd.setPatientLiteModel(getContext().getPatient().patientAsLiteModel());
            rd.setUserLiteModel(Project.getUserModel().getLiteModel());

            // 転帰をチェックする
            if (!isValidOutcome(rd)) return;

            diagList.add(rd);
        }

        if (!diagList.isEmpty()) {
            IDiagnosisSender sender = new DiagnosisSender();
            sender.setContext(getContext());
            sender.prepare(diagList);
            sender.send(diagList);
        }
    }
    
    private void trace(String msg) {
        if (DEBUG) {
            System.err.println(msg);
        }
    }

    /**
     * PopupListener
     */
    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        private JPopupMenu popup;
        private JTextField tf;

        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(PopupListener.this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[]{-12, 0});
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                tf.setText(SimpleDate.simpleDateToMmldate(sd));
                popup.setVisible(false);
                popup = null;
            }
        }
    }

    /**
     * DiagnosisPutTask
     */
    class DiagnosisPutTask extends DBTask<List<Long>, Void> {

        //private Chart chart;
        private List<RegisteredDiagnosisModel> added;
        private List<RegisteredDiagnosisModel> updated;
        private boolean sendClaim;
        private DocumentDelegater ddl;

        public DiagnosisPutTask(
                Chart chart,
                List<RegisteredDiagnosisModel> added,
                List<RegisteredDiagnosisModel> updated,
                boolean sendClaim,
                DocumentDelegater ddl) {

            super(chart);
            this.added = added;
            this.updated = updated;
            this.sendClaim = sendClaim;
            this.ddl = ddl;
        }

        @Override
        protected List<Long> doInBackground() throws Exception {

            // 更新する
            if (updated != null && updated.size() > 0) {
                ddl.updateDiagnosis(updated);
            }

            List<Long> result = null;

            // 保存する
            if (added != null && added.size() > 0) {
                result = ddl.putDiagnosis(added);
                for (int i = 0; i < added.size(); i++) {
                    long pk = result.get(i).longValue();
                    RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) added.get(i);
                    rd.setId(pk);
                }
            }

            // CLAIM 送信の sender を作成
            //ClaimSender sender = new ClaimSender(((ChartImpl) getContext()).getCLAIMListener());
            //sender.setPatientVisitModel(getContext().getPatientVisit());
            IDiagnosisSender sender = new DiagnosisSender();
            sender.setContext(getContext());

            // 元町皮ふ科
            // 追加病名を CLAIM 送信する
            // その際、DORCA病名を覗く（これはORCAにあってDolphinにインポートされ、転帰等の変更がないもの）
            if (sendClaim && addedDiagnosis != null && addedDiagnosis.size() > 0) {
                List<RegisteredDiagnosisModel> actualList = new ArrayList<RegisteredDiagnosisModel>();
                for (RegisteredDiagnosisModel rdm : addedDiagnosis) {
                    if (isDorcaUpdatedDisease(rdm) || isPureDisease(rdm)) {
                        actualList.add(rdm);
                    }
                }
                if (!actualList.isEmpty()) {
                    if (DEBUG) {
                        trace("-------- Send Diagnosis List ----------------");
                        for (RegisteredDiagnosisModel r : actualList) {
                            trace(r.getDiagnosis());
                        }
                    }
                    sender.prepare(actualList);
                    sender.send(actualList);
                }
            }

            // 更新された病名を CLAIM 送信する
            // detuched object のみ
            if (sendClaim && updatedDiagnosis != null && updatedDiagnosis.size() > 0) {
                if (DEBUG) {
                    trace("-------- Send Diagnosis List ----------------");
                    for (RegisteredDiagnosisModel r : updatedDiagnosis) {
                        trace(r.getDiagnosis());
                    }
                }
                sender.prepare(updatedDiagnosis);
                sender.send(updatedDiagnosis);
            }

            return result;
        }

        @Override
        protected void succeeded(List<Long> list) {
            clearDiagnosisList();
        }
    }

    /**
     *
     */
    class DolphinOrcaRenderer extends DefaultTableCellRenderer {

        /** Creates new IconRenderer */
        public DolphinOrcaRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {

            RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) tableModel.getObject(row);

            if (isSelected) {
                // 選択されている時はデフォルトの表示を行う
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                // 選択されていない時
                // Foreground をデフォルトにする
                // ORCA データの時は背景を変える
                // それ以外は奇数と偶数で色分けする
                setForeground(table.getForeground());
                if (isOrcaDisease(rd)) {
                    setBackground(ORCA_BACK);

                } else {

                    if ((row & (1)) == 0) {
                        setBackground(EVEN_COLOR);
                    } else {
                        setBackground(ODD_COLOR);
                    }
                }
            }

            if (value != null) {
                if (value instanceof String) {
                    this.setText((String) value);
                } else {
                    this.setText(value.toString());
                }
            } else {
                this.setText("");
            }

            return this;
        }
    }
}
