package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.dto.DiagnosisSearchSpec;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.PatientLiteModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.infomodel.StampModel;
import open.dolphin.message.DiseaseHelper;
import open.dolphin.message.MessageBuilder;
import open.dolphin.project.*;
import open.dolphin.table.*;
import open.dolphin.util.*;

import open.dolphin.dao.SqlOrcaView;
import open.dolphin.helper.DBTask;
import open.dolphin.message.DiagnosisModuleItem;

/**
 * DiagnosisDocument
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisDocument extends AbstractChartDocument implements PropertyChangeListener {

    private static final String TITLE = "傷病名";
    // 傷病名テーブルのカラム番号定義
    private static final int DIAGNOSIS_COL = 0;
    private static final int CATEGORY_COL = 1;
    private static final int OUTCOME_COL = 2;
    private static final int START_DATE_COL = 3;
    private static final int END_DATE_COL = 4;
    // 抽出期間コンボボックスデータ
    private NameValuePair[] extractionObjects = ClientContext.getNameValuePair("diagnosis.combo.period");
    // GUI コンポーネント定義
    private static final String RESOURCE_BASE = "/open/dolphin/resources/images/";
    private static final String DELETE_BUTTON_IMAGE = "del_16.gif";
    private static final String ADD_BUTTON_IMAGE = "add_16.gif";
    private static final String UPDATE_BUTTON_IMAGE = "save_16.gif";
    private static final String ORCA_VIEW_IMAGE = "impt_16.gif";
    private static final String TABLE_BORDER_TITLE = "傷病歴";
    private static final String ORCA_VIEW = "ORCA View";
    private static final String ORCA_RECORD = "ORCA";
    // GUI Component
    /** JTableレンダラ用の奇数カラー */
    private static final Color ODD_COLOR = ClientContext.getColor("color.odd");
    /** JTableレンダラ用の偶数カラー */
    private static final Color EVEN_COLOR = ClientContext.getColor("color.even");
    private static final Color ORCA_BACK = ClientContext.getColor("color.CALENDAR_BACK");
    private JButton addButton;                  // 新規病名エディタボタン
    private JButton updateButton;               // 既存傷病名の転帰等の更新ボタン
    private JButton deleteButton;               // 既存傷病名の削除ボタン
    private JButton orcaButton;                 // ORCA View ボタン
    private JTable diagTable;                   // 病歴テーブル
    private ObjectReflectTableModel tableModel; // TableModel
    private JComboBox extractionCombo;          // 抽出期間コンボ
    private JTextField countField;              // 件数フィールド

    // 抽出期間内で Dolphin に最初に病名がある日
    // ORCA の病名は抽出期間～dolphinFirstDate
    private String dolphinFirstDate;
    // 昇順降順フラグ
    private boolean ascend;
    // 新規に追加された傷病名リスト
    List<RegisteredDiagnosisModel> addedDiagnosis;
    // 更新された傷病名リスト
    List<RegisteredDiagnosisModel> updatedDiagnosis;

    // 傷病名件数
    private int diagnosisCount;

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
        content.setBorder(BorderFactory.createTitledBorder(TABLE_BORDER_TITLE));

        // 全体をレイアウトする
        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout(0, 7));
        myPanel.add(content);
        myPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

        // Preference から昇順降順を設定する
        ascend = Project.getPreferences().getBoolean(Project.DIAGNOSIS_ASCENDING, false);
    }

    /**
     * コマンドボタンパネルをする。
     */
    private JPanel createButtonPanel2() {

        // 更新ボタン (ActionListener) EventHandler.create(ActionListener.class, this, "save")
        updateButton = new JButton(createImageIcon(UPDATE_BUTTON_IMAGE));
        updateButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "save"));
        updateButton.setEnabled(false);
        updateButton.setToolTipText("追加変更した傷病名をデータベースに反映します。");

        // 削除ボタン
        deleteButton = new JButton(createImageIcon(DELETE_BUTTON_IMAGE));
        deleteButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "delete"));
        deleteButton.setEnabled(false);
        deleteButton.setToolTipText("選択した傷病名を削除します。");

        // 新規登録ボタン
        addButton = new JButton(createImageIcon(ADD_BUTTON_IMAGE));
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
        addButton.setEnabled(!isReadOnly());
        addButton.setToolTipText("傷病名を追加します。");

        // ORCA View
        orcaButton = new JButton(createImageIcon(ORCA_VIEW_IMAGE));
        orcaButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "viewOrca"));
        orcaButton.setToolTipText("ORCAに登録してある病名を取り込みます。");

        // ボタンパネル
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        p.add(orcaButton);
        p.add(deleteButton);
        p.add(addButton);
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
        tableModel = new ObjectReflectTableModel(columnNames, startNumRows, methodNames, columnClasses) {

            // Diagnosisは編集不可
            @Override
            public boolean isCellEditable(int row, int col) {

                // licenseCodeで制御
                if (isReadOnly()) {
                    return false;
                }

                // 病名レコードが存在しない場合は false
                RegisteredDiagnosisModel entry = (RegisteredDiagnosisModel) getObject(row);
                if (entry == null) {
                    return false;
                }

                // ORCA に登録されている病名の場合
                if (entry.getStatus() != null && entry.getStatus().equals(ORCA_RECORD)) {
                    return false;
                }

                // それ以外はカラムに依存する
                return ((col == CATEGORY_COL || col == OUTCOME_COL || col == START_DATE_COL || col == END_DATE_COL))
                        ? true
                        : false;
            }

            // オブジェクトの値を設定する
            @Override
            public void setValueAt(Object value, int row, int col) {

                RegisteredDiagnosisModel entry = (RegisteredDiagnosisModel) getObject(row);

                if (value == null || entry == null) {
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
                        if (saveCategory != null) {
                            if (test != null) {
                                if (!test.equals(saveCategory)) {
                                    entry.setCategory(dcm.getDiagnosisCategory());
                                    entry.setCategoryDesc(dcm.getDiagnosisCategoryDesc());
                                    entry.setCategoryCodeSys(dcm.getDiagnosisCategoryCodeSys());
                                    fireTableRowsUpdated(row, row);
                                    addUpdatedList(entry);
                                }
                            } else {
                                entry.setDiagnosisCategoryModel(null);
                                fireTableRowsUpdated(row, row);
                                addUpdatedList(entry);
                            }
                            
                        } else {
                            if (test != null) {
                                entry.setCategory(dcm.getDiagnosisCategory());
                                entry.setCategoryDesc(dcm.getDiagnosisCategoryDesc());
                                entry.setCategoryCodeSys(dcm.getDiagnosisCategoryCodeSys());
                                fireTableRowsUpdated(row, row);
                                addUpdatedList(entry);
                            }
                        }
                        break;

                    case OUTCOME_COL:
                        // JComboBox から選択
                        String saveOutcome = entry.getOutcome();
                        DiagnosisOutcomeModel dom = (DiagnosisOutcomeModel) value;
                        test = dom.getOutcome();
                        test = test != null && (!test.equals("")) ? test : null;
                        if (saveOutcome != null) {
                            if (test != null) {
                                if (!saveOutcome.equals(test)) {
                                    entry.setOutcome(dom.getOutcome());
                                    entry.setOutcomeDesc(dom.getOutcomeDesc());
                                    entry.setOutcomeCodeSys(dom.getOutcomeCodeSys());
                                    // 疾患終了日を入れる
                                    if (Project.getPreferences().getBoolean("autoOutcomeInput", false)) {
                                        String val = entry.getEndDate();
                                        if (val == null || val.equals("")) {
                                            GregorianCalendar gc = new GregorianCalendar();
                                            int offset = Project.getPreferences().getInt(Project.OFFSET_OUTCOME_DATE, -7);
                                            gc.add(Calendar.DAY_OF_MONTH, offset);
                                            String today = MMLDate.getDate(gc);
                                            entry.setEndDate(today);
                                        }
                                    }
                                    fireTableRowsUpdated(row, row);
                                    addUpdatedList(entry);
                                }
                            } else {
                                entry.setDiagnosisOutcomeModel(null);
                                fireTableRowsUpdated(row, row);
                                addUpdatedList(entry);
                            }
                        } else {
                            if (test != null) {
                                entry.setOutcome(dom.getOutcome());
                                entry.setOutcomeDesc(dom.getOutcomeDesc());
                                entry.setOutcomeCodeSys(dom.getOutcomeCodeSys());
                                // 疾患終了日を入れる
                                if (Project.getPreferences().getBoolean("autoOutcomeInput", false)) {
                                    String val = entry.getEndDate();
                                    if (val == null || val.equals("")) {
                                        GregorianCalendar gc = new GregorianCalendar();
                                        int offset = Project.getPreferences().getInt(Project.OFFSET_OUTCOME_DATE, -7);
                                        gc.add(Calendar.DAY_OF_MONTH, offset);
                                        String today = MMLDate.getDate(gc);
                                        entry.setEndDate(today);
                                    }
                                }
                                fireTableRowsUpdated(row, row);
                                addUpdatedList(entry);
                            }
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
                        strVal = (String) value;
                        if (!strVal.trim().equals("")) {
                            entry.setEndDate((String) value);
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        }
                        break;
                }
            }
        };

        // 傷病歴テーブルを生成する
        diagTable = new JTable(tableModel);

        // 奇数、偶数行の色分けをする
        diagTable.setDefaultRenderer(Object.class, new DolphinOrcaRenderer());

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
        //outcomeList[0] = dom;
        //
        // 主病名は使用しないらいしい
        //
        outcomeList[0] = null;
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
        new PopupListener(tf);
        tf.setDocument(new RegexConstrainedDocument(datePattern));
        DefaultCellEditor de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        int clickCountToStart = Project.getPreferences().getInt("diagnosis.table.clickCountToStart", 1);
        de.setClickCountToStart(clickCountToStart);

        column = diagTable.getColumnModel().getColumn(END_DATE_COL);
        tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        tf.setDocument(new RegexConstrainedDocument(datePattern));
        new PopupListener(tf);
        de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        de.setClickCountToStart(clickCountToStart);

        //
        // TransferHandler を設定する
        //
        diagTable.setTransferHandler(new DiagnosisTransferHandler(this));
        diagTable.setDragEnabled(true);

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
        p.add(Box.createRigidArea(new Dimension(5, 0)));
        extractionCombo = new JComboBox(extractionObjects);
        Preferences prefs = Project.getPreferences();
        int currentDiagnosisPeriod = prefs.getInt(Project.DIAGNOSIS_PERIOD, 0);
        int selectIndex = NameValuePair.getIndex(String.valueOf(currentDiagnosisPeriod), extractionObjects);
        extractionCombo.setSelectedIndex(selectIndex);
        extractionCombo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "extPeriodChanged", ""));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboPanel.add(extractionCombo);
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
            // 削除ボタンをコントロールする
            // licenseCode 制御を追加
            if (isReadOnly()) {
                return;
            }

            // 選択された行のオブジェクトを得る
            int row = diagTable.getSelectedRow();
            RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) tableModel.getObject(row);

            // ヌルの場合
            if (rd == null) {
                if (deleteButton.isEnabled()) {
                    deleteButton.setEnabled(false);
                }
                return;
            }

            // ORCA の場合
            if (rd.getStatus() != null && rd.getStatus().equals(ORCA_RECORD)) {
                if (deleteButton.isEnabled()) {
                    deleteButton.setEnabled(false);
                }
                return;
            }

            // Dolphin の場合
            if (!deleteButton.isEnabled()) {
                deleteButton.setEnabled(true);
            }
        }
    }

    /**
     * 抽出期間を変更した場合に再検索を行う。
     * ORCA 病名ボタンが disable であれば検索後に enable にする。
     */
    public void extPeriodChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
            int past = Integer.parseInt(pair.getValue());
            if (past != 0) {
                GregorianCalendar today = new GregorianCalendar();
                today.add(GregorianCalendar.MONTH, past);
                today.clear(Calendar.HOUR_OF_DAY);
                today.clear(Calendar.MINUTE);
                today.clear(Calendar.SECOND);
                today.clear(Calendar.MILLISECOND);
                getDiagnosisHistory(today.getTime());
            } else {
                getDiagnosisHistory(new Date(0L));
            }
        }
    }

    public JTable getDiagnosisTable() {
        return diagTable;
    }

    @Override
    public void start() {

        initialize();

        NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
        int past = Integer.parseInt(pair.getValue());

        Date date = null;
        if (past != 0) {
            GregorianCalendar today = new GregorianCalendar();
            today.add(GregorianCalendar.MONTH, past);
            today.clear(Calendar.HOUR_OF_DAY);
            today.clear(Calendar.MINUTE);
            today.clear(Calendar.SECOND);
            today.clear(Calendar.MILLISECOND);
            date = today.getTime();
        } else {
            date = new Date(0l);
        }

        getDiagnosisHistory(date);
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
        }
    }

    /**
     * 追加及び更新リストをクリアする。
     */
    private void clearDiagnosisList() {

        if (addedDiagnosis != null && addedDiagnosis.size() > 0) {
            int index = 0;
            while (addedDiagnosis.size() > 0) {
                addedDiagnosis.remove(index);
            }
        }

        if (updatedDiagnosis != null && updatedDiagnosis.size() > 0) {
            int index = 0;
            while (updatedDiagnosis.size() > 0) {
                updatedDiagnosis.remove(index);
            }
        }

        controlUpdateButton();
    }

    /**
     * 更新ボタンを制御する。
     */
    private void controlUpdateButton() {
        boolean hasAdded = (addedDiagnosis != null && addedDiagnosis.size() > 0) ? true : false;
        boolean hasUpdated = (updatedDiagnosis != null && updatedDiagnosis.size() > 0) ? true : false;
        boolean newDirty = (hasAdded || hasUpdated) ? true : false;
        boolean old = isDirty();
        if (old != newDirty) {
            setDirty(newDirty);
            updateButton.setEnabled(isDirty());
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

        DBTask task = new DBTask<List<StampModel>>(getContext()) {

            @Override
            protected List<StampModel> doInBackground() throws Exception {
                List<StampModel> result = sdl.getStamp(stampList);
                return result;
            }

            @Override
            protected void succeeded(List<StampModel> list) {
                logger.debug("importStampList succeeded");
                if (sdl.isNoError() && list != null) {
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
                tableModel.addRow(module);
            } else if (row < cnt) {
                tableModel.insertRow(row, module);
            } else {
                tableModel.addRow(module);
            }

            //
            // row を選択する
            //
            diagTable.getSelectionModel().setSelectionInterval(row, row);

            addAddedList(module);
        }
    }

    /**
     * 傷病名エディタを開く。
     */
    public void openEditor2() {
        StampEditorDialog stampEditor = new StampEditorDialog("diagnosis", null);

        // 編集終了、値の受け取りにこのオブジェクトを設定する
        stampEditor.addPropertyChangeListener(StampEditorDialog.VALUE_PROP, this);
        stampEditor.start();
    }

    /**
     * 傷病名エディタからデータを受け取りテーブルへ追加する。
     */
    public void propertyChange(PropertyChangeEvent e) {

        ArrayList list = (ArrayList) e.getNewValue();
        if (list == null || list.size() == 0) {
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
                tableModel.addRow(module);
                addAddedList(module);
            }

        } else {
            // 降順なのでテーブルの先頭へ追加する
            for (int i = len - 1; i > -1; i--) {
                RegisteredDiagnosisModel module = (RegisteredDiagnosisModel) list.get(i);
                module.setStartDate(today);
                tableModel.insertRow(0, module);
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

        if ((addedDiagnosis == null || addedDiagnosis.size() == 0) &&
                (updatedDiagnosis == null || updatedDiagnosis.size() == 0)) {
            return;
        }

        final boolean sendDiagnosis = Project.getSendDiagnosis() && ((ChartImpl) getContext()).getCLAIMListener() != null ? true : false;
        logger.debug("sendDiagnosis = " + sendDiagnosis);

        // continue to save
        Date confirmed = new Date();
        logger.debug("confirmed = " + confirmed);
        
        boolean go = true;

        if (addedDiagnosis != null && addedDiagnosis.size() > 0) {

            for (RegisteredDiagnosisModel rd : addedDiagnosis) {
                
                logger.debug("added rd = " + rd.getDiagnosis());
                logger.debug("id = " + rd.getId());

                // 開始日、終了日はテーブルから取得している
                // TODO confirmed, recorded
                rd.setKarte(getContext().getKarte());           // Karte
                rd.setCreator(Project.getUserModel());          // Creator
                rd.setConfirmed(confirmed);                     // 確定日
                rd.setRecorded(confirmed);                      // 記録日
                rd.setStatus(IInfoModel.STATUS_FINAL);

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
                
                logger.debug("updated rd = " + rd.getDiagnosis());
                logger.debug("id = " + rd.getId());

                // 現バージョンは上書きしている
                rd.setCreator(Project.getUserModel());
                rd.setConfirmed(confirmed);
                rd.setRecorded(confirmed);
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
    public void getDiagnosisHistory(Date past) {

        final DiagnosisSearchSpec spec = new DiagnosisSearchSpec();
        spec.setCode(DiagnosisSearchSpec.PATIENT_SEARCH);
        spec.setKarteId(getContext().getKarte().getId());
        if (past != null) {
            spec.setFromDate(past);
        }

        final DocumentDelegater ddl = new DocumentDelegater();

        DBTask task = new DBTask<List>(getContext()) {

            @Override
            protected List doInBackground() throws Exception {
                logger.debug("getDiagnosisHistory doInBackground");
                List result = ddl.getDiagnosisList(spec);
                return result;
            }

            @Override
            protected void succeeded(List list) {
                logger.debug("getDiagnosisHistory succeeded");
                if (ddl.isNoError() && list != null && list.size() > 0) {
                    if (ascend) {
                        Collections.sort(list);
                        RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) list.get(0);
                        dolphinFirstDate = rd.getStartDate();
                    } else {
                        Collections.sort(list, Collections.reverseOrder());
                        int index = list.size() - 1;
                        RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) list.get(index);
                        dolphinFirstDate = rd.getStartDate();
                    }
                    tableModel.setObjectList(list);
                    setDiagnosisCount(list.size());
                }
            }
        };

        task.execute();
    }

    /**
     * 選択された行のデータを削除する。
     */
    public void delete() {

        // 選択された行のオブジェクトを取得する
        final int row = diagTable.getSelectedRow();
        final RegisteredDiagnosisModel model = (RegisteredDiagnosisModel) tableModel.getObject(row);
        if (model == null) {
            return;
        }

        // まだデータベースに登録されていないデータの場合
        // テーブルから削除してリターンする
        if (model.getId() == 0L) {
            if (addedDiagnosis != null && addedDiagnosis.contains(model)) {
                tableModel.deleteRow(row);
                setDiagnosisCount(tableModel.getObjectCount());
                addedDiagnosis.remove(model);
                controlUpdateButton();
                return;
            }
        }

        // ディタッチオブジェクトの場合はデータベースから削除する
        // 削除の場合はその場でデータベースの更新を行う 2006-03-25
        final List<Long> list = new ArrayList<Long>(1);
        list.add(new Long(model.getId()));

        final DocumentDelegater ddl = new DocumentDelegater();

        DBTask task = new DBTask<Void>(getContext()) {

            @Override
            protected Void doInBackground() throws Exception {
                logger.debug("delete doInBackground");
                ddl.removeDiagnosis(list);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                logger.debug("delete succeeded");
                if (ddl.isNoError()) {
                    tableModel.deleteRow(row);
                    setDiagnosisCount(tableModel.getObjectCount());
                    // 更新リストにある場合
                    // 更新リストから取り除く
                    if (updatedDiagnosis != null) {
                        updatedDiagnosis.remove(model);
                        controlUpdateButton();
                    }
                }
            }
        };

        task.execute();
    }

    /**
     * ORCAに登録されている病名を取り込む。（テーブルへ追加する） 
     * 検索後、ボタンを disabled にする。
     */
    public void viewOrca() {

        // 患者IDを取得する
        String patientId = getContext().getPatient().getPatientId();

        // 抽出期間から検索範囲の最初の日を取得する
        NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
        int past = Integer.parseInt(pair.getValue());

        Date date = null;
        if (past != 0) {
            GregorianCalendar today = new GregorianCalendar();
            today.add(GregorianCalendar.MONTH, past);
            today.clear(Calendar.HOUR_OF_DAY);
            today.clear(Calendar.MINUTE);
            today.clear(Calendar.SECOND);
            today.clear(Calendar.MILLISECOND);
            date = today.getTime();
        } else {
            date = new Date(0l);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String from = sdf.format(date);

        // 検索範囲の最後の日は Dolphin レコードの最初の日
        // ヌルの場合は今日までを検索する
//        String to = null;
//        if (dolphinFirstDate == null) {
//            to = sdf.format(new Date());
//        } else {
//            to = dolphinFirstDate.replaceAll("-", "");
//        }
        String to = sdf.format(new Date());
        logger.debug("from = " + from);
        logger.debug("to = " + to);

        final Component comp = (Component) this.getUI();

        // DAOを生成する
        final SqlOrcaView dao = new SqlOrcaView();

        // ReflectMonitor を生成する
        final ReflectMonitor rm = new ReflectMonitor();
        rm.setReflection(dao,
                "getOrcaDisease",
                new Class[]{String.class, String.class, String.class, Boolean.class},
                new Object[]{patientId, from, to, new Boolean(ascend)});
        rm.setMonitor(SwingUtilities.getWindowAncestor(comp), ORCA_VIEW, "病名を検索しています...  ", 200, 60 * 1000);

        //
        // ReflectMonitor の結果State property の束縛リスナを生成する
        //
        PropertyChangeListener pl = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {

                int state = ((Integer) e.getNewValue()).intValue();

                switch (state) {

                    case ReflectMonitor.DONE:
                        if (dao.isNoError()) {
                            List list = (List) rm.getResult();
                            if (list != null && list.size() > 0) {
                                if (ascend) {
                                    Collections.sort(list);
                                } else {
                                    Collections.sort(list, Collections.reverseOrder());
                                }
                                tableModel.addRows(list);
                            }
                        } else {
                            String errMsg = dao.getErrorMessage();
                            String title = ClientContext.getFrameTitle(ORCA_VIEW);
                            JOptionPane.showMessageDialog(comp, errMsg, title, JOptionPane.WARNING_MESSAGE);
                        }

                        break;

                    case ReflectMonitor.TIME_OVER:
                        orcaButton.setEnabled(true);
                        break;

                    case ReflectMonitor.CANCELED:
                        orcaButton.setEnabled(true);
                        break;
                }

            //
            // Block を解除する
            //
            //setBusy(false);
            }
        };
        rm.addPropertyChangeListener(pl);

        //
        // Block し、メソッドの実行を開始する
        //
        //setBusy(true);
        orcaButton.setEnabled(false);
        rm.start();
    }

    /**
     * PopupListener
     */
    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        private JPopupMenu popup;
        private JTextField tf;

        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(this);
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
    class DiagnosisPutTask extends DBTask<List<Long>> {

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
            
            logger.debug("doInBackground");

            // 更新する
            if (updated != null && updated.size() > 0) {
                logger.debug("ddl.updateDiagnosis");
                ddl.updateDiagnosis(updated);
            }

            List<Long> result = null;

            // 保存する
            if (added != null && added.size() > 0) {
                logger.debug("ddl.putDiagnosis");
                result = ddl.putDiagnosis(added);
                if (ddl.isNoError()) {
                    logger.debug("ddl.putDiagnosis() is NoErr");
                    for (int i = 0; i < added.size(); i++) {
                        long pk = result.get(i).longValue();
                        logger.debug("persist id = " + pk);
                        RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) added.get(i);
                        rd.setId(pk);
                    }
                }
            }

            //
            // 追加病名を CLAIM 送信する
            //
            if (sendClaim && added != null && added.size() > 0) {
                
                logger.debug("sendClaim Diagnosis");

                // DocInfo & RD をカプセル化したアイテムを生成する
                ArrayList<DiagnosisModuleItem> moduleItems = new ArrayList<DiagnosisModuleItem>();

                for (RegisteredDiagnosisModel rd : added) {
                    DocInfoModel docInfo = new DocInfoModel();
                    docInfo.setDocId(GUIDGenerator.generate(docInfo));
                    docInfo.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
                    docInfo.setPurpose(IInfoModel.PURPOSE_RECORD);
                    docInfo.setFirstConfirmDate(ModelUtils.getDateTimeAsObject(rd.getConfirmDate()));
                    docInfo.setConfirmDate(ModelUtils.getDateTimeAsObject(rd.getFirstConfirmDate()));

                    DiagnosisModuleItem mItem = new DiagnosisModuleItem();
                    mItem.setDocInfo(docInfo);
                    mItem.setRegisteredDiagnosisModule(rd);
                    moduleItems.add(mItem);
                }

                // ヘルパー用の値を生成する
                String confirmDate = added.get(0).getConfirmDate();
                //UserLiteModel creator = added.get(0).getUserLiteModel();
                PatientLiteModel patient = added.get(0).getPatientLiteModel();

                // ヘルパークラスを生成する
                logger.debug("create DiseaseHelper");
                DiseaseHelper dhl = new DiseaseHelper();
                
                dhl.setPatientId(patient.getPatientId());
                logger.debug("setPatientId = " + dhl.getPatientId());
                
                dhl.setConfirmDate(confirmDate);
                logger.debug("setConfirmDate = " + dhl.getConfirmDate());
                
                dhl.setDiagnosisModuleItems(moduleItems);
                
                dhl.setGroupId(GUIDGenerator.generate(dhl));
                logger.debug("setGroupId = " + dhl.getGroupId());
                
                dhl.setDepartment(context.getPatientVisit().getDepartmentCode());
                logger.debug("dhl.setDepartment = " + dhl.getDepartment());
                
                dhl.setDepartmentDesc(context.getPatientVisit().getDepartment());
                logger.debug("dhl.setDepartmentDesc = " + dhl.getDepartmentDesc());
                
                dhl.setCreatorName(context.getPatientVisit().getAssignedDoctorName());
                logger.debug("dhl.setCreatorName = " + dhl.getCreatorName());
                
                dhl.setCreatorId(context.getPatientVisit().getAssignedDoctorId());
                logger.debug("dhl.setCreatorId = " + dhl.getCreatorId());
                
                dhl.setCreatorLicense(Project.getUserModel().getLicenseModel().getLicense());
                logger.debug("dhl.setCreatorLicense = " + dhl.getCreatorLicense());
                
                dhl.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
                logger.debug("dhl.setFacilityName = " + dhl.getFacilityName());
                
                dhl.setJmariCode(context.getPatientVisit().getJmariCode());
                logger.debug("dhl.setJmariCode = " + dhl.getJmariCode());

                MessageBuilder mb = new MessageBuilder();
                logger.debug("MessageBuilder created");
                String claimMessage = mb.build(dhl);
                logger.debug("claimMessage builded" + claimMessage);
                
                if (ClientContext.getClaimLogger() != null) {
                    ClientContext.getClaimLogger().debug(claimMessage);
                }
                ClaimMessageEvent event = new ClaimMessageEvent(this);
                event.setPatientId(patient.getPatientId());
                event.setPatientName(patient.getName());
                event.setPatientSex(patient.getGender());
                event.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
                event.setClaimInstance(claimMessage);
                event.setConfirmDate(confirmDate);
                ClaimMessageListener claimListener = ((ChartImpl) context).getCLAIMListener();
                if (claimListener != null) {
                    claimListener.claimMessageEvent(event);
                }
            }

            //          
            // 更新された病名を CLAIM 送信する
            //
            if (sendClaim && updated != null && updated.size() > 0) {

                // RegisteredDiagnosisModel数分の DocInfo を生成する
                ArrayList<DiagnosisModuleItem> moduleItems = new ArrayList<DiagnosisModuleItem>();

                for (RegisteredDiagnosisModel rd : updated) {
                    DocInfoModel docInfo = new DocInfoModel();
                    docInfo.setDocId(GUIDGenerator.generate(docInfo));
                    docInfo.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
                    docInfo.setPurpose(IInfoModel.PURPOSE_RECORD);
                    docInfo.setFirstConfirmDate(ModelUtils.getDateTimeAsObject(rd.getConfirmDate()));
                    docInfo.setConfirmDate(ModelUtils.getDateTimeAsObject(rd.getFirstConfirmDate()));

                    DiagnosisModuleItem mItem = new DiagnosisModuleItem();
                    mItem.setDocInfo(docInfo);
                    mItem.setRegisteredDiagnosisModule(rd);
                    moduleItems.add(mItem);
                }

                // ヘルパー用の値を生成する
                String confirmDate = updated.get(0).getConfirmDate();
                //UserLiteModel creator = updated.get(0).getUserLiteModel();
                PatientLiteModel patient = updated.get(0).getPatientLiteModel();

                // ヘルパークラスを生成する
                DiseaseHelper dhl = new DiseaseHelper();
                dhl.setPatientId(patient.getPatientId());
                dhl.setConfirmDate(confirmDate);
                dhl.setDiagnosisModuleItems(moduleItems);
                dhl.setGroupId(GUIDGenerator.generate(dhl));
                dhl.setDepartment(context.getPatientVisit().getDepartmentCode());
                dhl.setDepartmentDesc(context.getPatientVisit().getDepartment());
                dhl.setCreatorName(context.getPatientVisit().getAssignedDoctorName());
                dhl.setCreatorId(context.getPatientVisit().getAssignedDoctorId());
                dhl.setCreatorLicense(Project.getUserModel().getLicenseModel().getLicense());
                dhl.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
                dhl.setJmariCode(context.getPatientVisit().getJmariCode());

                MessageBuilder mb = new MessageBuilder();
                String claimMessage = mb.build(dhl);
                // debug
                if (ClientContext.getLogger("claim") != null) {
                    ClientContext.getLogger("claim").debug(claimMessage);
                }
                ClaimMessageEvent event = new ClaimMessageEvent(this);
                event.setPatientId(patient.getPatientId());
                event.setPatientName(patient.getName());
                event.setPatientSex(patient.getGender());
                event.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
                event.setClaimInstance(claimMessage);
                event.setConfirmDate(confirmDate);
                ClaimMessageListener claimListener = ((ChartImpl) context).getCLAIMListener();
                if (claimListener != null) {
                    claimListener.claimMessageEvent(event);
                }
            }

            return result;
        }

        @Override
        protected void succeeded(List<Long> list) {
            logger.debug("DiagnosisPutTask succeeded");
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

            // ORCA レコードかどうかを判定する
            boolean orca = (rd != null && rd.getStatus() != null && rd.getStatus().equals(ORCA_RECORD)) ? true : false;

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
                if (orca) {
                    setBackground(ORCA_BACK);

                } else {

                    if (row % 2 == 0) {
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
