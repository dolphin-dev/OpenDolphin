/*
 * DiagnosisDocument.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

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
import open.dolphin.infomodel.UserLiteModel;
import open.dolphin.message.DiseaseHelper;
import open.dolphin.message.MessageBuilder;
import open.dolphin.project.*;
import open.dolphin.table.*;
import open.dolphin.util.*;

import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import open.dolphin.dao.SqlOrcaView;
import open.dolphin.message.DiagnosisModuleItem;

/**
 * DiagnosisDocument
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisDocument extends DefaultChartDocument implements PropertyChangeListener {
    
    // 傷病名テーブルのカラム番号定義
    private static final int DIAGNOSIS_COL     = 0;
    private static final int CATEGORY_COL      = 1;
    private static final int OUTCOME_COL       = 2;
    private static final int START_DATE_COL    = 3;
    private static final int END_DATE_COL      = 4;
    
    // 抽出期間コンボボックスデータ
    private NameValuePair[] extractionObjects
            = ClientContext.getNameValuePair("diagnosis.combo.period");
    
    // GUI コンポーネント定義
    private static final String RESOURCE_BASE          = "/open/dolphin/resources/images/";
    private static final String DELETE_BUTTON_IMAGE    = "del_16.gif";
    private static final String ADD_BUTTON_IMAGE       = "add_16.gif";
    private static final String UPDATE_BUTTON_IMAGE    = "save_16.gif";
    private static final String ORCA_VIEW_IMAGE        = "impt_16.gif";
    private static final String TABLE_BORDER_TITLE     = "傷病歴";
    private static final String ORCA_VIEW              = "ORCA View";
    private static final String ORCA_RECORD            = "ORCA";
    
    // GUI Component
    private JButton addButton;                  // 新規病名エディタボタン
    private JButton updateButton;               // 既存傷病名の転帰等の更新ボタン
    private JButton deleteButton;               // 既存傷病名の削除ボタン
    private JButton orcaButton;                 // ORCA View ボタン
    private JTable diagTable;                   // 病歴テーブル
    private ObjectReflectTableModel tableModel; // TableModel
    private JComboBox extractionCombo;          // 抽出期間コンボ
    private JTextField countField;              // 件数フィールド
    
//    // ORCA 病名関係
//    private JTable orcaTable;                   // 病歴テーブル
//    private ObjectReflectTableModel orcaModel;  // TableModel
    
    // 抽出期間内で Dolphin に最初に病名がある日
    // ORCA の病名は抽出期間～dolphinFirstDate
    private String dolphinFirstDate;
    
    // 昇順降順フラグ
    private boolean ascend;
    
    // 新規に追加された傷病名リスト
    List<RegisteredDiagnosisModel> addedDiagnosis;
    
    // 更新された傷病名リスト
    List<RegisteredDiagnosisModel> updatedDiagnosis;
    
    // 病名更新のフラグ
    //private boolean updated;
    
    // 傷病名件数
    private int diagnosisCount;
    
    // TaskTimer
    private javax.swing.Timer taskTimer;
    
    /**
     *  Creates new DiagnosisDocument
     */
    public DiagnosisDocument() {
    }
    
    /**
     * GUI コンポーネントを生成初期化する。
     */
    public void initialize() {
        
        // コマンドボタンパネルを生成する
        JPanel cmdPanel = createButtonPanel2();
        
        // Dolphin 傷病歴パネルを生成する
        JPanel dolphinPanel = createDignosisPanel();
        
//        // ORCA 傷病歴パネルを生成する
//        JPanel orcaPanel = createOrcaViewPanel();
        
        // 抽出期間パネルを生成する
        JPanel filterPanel = createFilterPanel();
        
//        // Dolphin & ORCA パネルを SplitPane に加える
//        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dolphinPanel, orcaPanel);
//        sp.setOneTouchExpandable(true);
//        sp.setDividerLocation(300);
        
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
        ascend = Project.getPreferences().getBoolean(Project.DOC_HISTORY_ASCENDING, false);
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
        //orcaButton.setMargin(new Insets(0,0,0,0));
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
            
            private static final long serialVersionUID = 3528305657868387682L;
            
            // Diagnosisは編集不可
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
                        DiagnosisCategoryModel dcm = (DiagnosisCategoryModel) value;
                        String test = dcm.getDiagnosisCategory();
                        if (test != null && test.equals("") == false) {
                            entry.setCategory(dcm.getDiagnosisCategory());
                            entry.setCategoryDesc(dcm.getDiagnosisCategoryDesc());
                            entry.setCategoryCodeSys(dcm.getDiagnosisCategoryCodeSys());
                        } else {
                            entry.setDiagnosisCategoryModel(null);
                        }
                        fireTableRowsUpdated(row, row);
                        addUpdatedList(entry);
                        break;
                        
                    case OUTCOME_COL:
                        // JComboBox から選択
                        DiagnosisOutcomeModel dom = (DiagnosisOutcomeModel) value;
                        test = dom.getOutcome();
                        if (test != null && test.equals("") == false) {
                            entry.setOutcome(dom.getOutcome());
                            entry.setOutcomeDesc(dom.getOutcomeDesc());
                            entry.setOutcomeCodeSys(dom.getOutcomeCodeSys());
                            
                            // 疾患終了日を入れる
                            String val = entry.getEndDate();
                            if (val == null || val.equals("")) {
                                GregorianCalendar gc = new GregorianCalendar();
                                int offset = Project.getPreferences().getInt(Project.OFFSET_OUTCOME_DATE, -7);
                                gc.add(Calendar.DAY_OF_MONTH, offset);
                                String today = MMLDate.getDate(gc);
                                entry.setEndDate(today);
                            }
                        } else {
                            entry.setDiagnosisOutcomeModel(null);
                        }
                        fireTableRowsUpdated(row, row);
                        addUpdatedList(entry);
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
                        String strVal = (String)value;
                        if ( !strVal.trim().equals("") ) {
                            entry.setStartDate(strVal);
                            fireTableRowsUpdated(row, row);
                            addUpdatedList(entry);
                        }
                        break;
                        // entry.setStartDate((String)value);
                        // break;
                        
                    case END_DATE_COL:
                        strVal = (String)value;
                        if (!strVal.trim().equals("") ) {
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
        
        // 疾患終了日カラムにエディタを設定する
        TableColumn column = diagTable.getColumnModel().getColumn(END_DATE_COL);
        column.setCellEditor(new IMECellEditor(new JTextField(), 1, false));
        
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
        column = diagTable.getColumnModel().getColumn(CATEGORY_COL);
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
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(null);
            }
        });
        new PopupListener(tf);
        tf.setDocument(new RegexConstrainedDocument(datePattern));
        DefaultCellEditor de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        
        column = diagTable.getColumnModel().getColumn(END_DATE_COL);
        tf = new JTextField();
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(null);
            }
        });
        tf.setDocument(new RegexConstrainedDocument(datePattern));
        new PopupListener(tf);
        de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        
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
    
//    /**
//     * ORCA 病名パネルを生成する。
//     */
//    private JPanel createOrcaViewPanel() {
//        
//        String[] columnNames = ClientContext.getStringArray("diagnosis.columnNames");
//        String[] methodNames = ClientContext.getStringArray("diagnosis.methodNames");
//        Class[] columnClasses = new Class[]{String.class, String.class, String.class, String.class, String.class};
//        int startNumRows = ClientContext.getInt("diagnosis.startNumRows");
//        
//        // Diagnosis テーブルモデルを生成する
//        orcaModel = new ObjectReflectTableModel(columnNames, startNumRows, methodNames, columnClasses);
//        
//        // ORCA 傷病歴テーブルを生成する
//        orcaTable = new JTable(orcaModel);
//        
//        // 奇数、偶数行の色分けをする
//        orcaTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
//        
//        // Layout
//        JScrollPane scroller = new JScrollPane(orcaTable,
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        
//        JPanel p = new JPanel(new BorderLayout());
//        p.add(scroller, BorderLayout.CENTER);
//        return p;
//    }
    
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
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        
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
    
    public void stop() {
        if (tableModel != null) {
            tableModel.clear();
        }
    }
    
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
    public void importStampList(List<ModuleInfoBean> stampList, final int insertRow) {
        
        // ProgressBar を取得する
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        
        // Worker を生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        final StampDelegater sdl = new StampDelegater();
        final StampGetTask worker = new StampGetTask(stampList, sdl, taskLength);
        
        // タスクタイマーを生成する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                
                if (worker.isDone()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    
                    if (sdl.isNoError()) {
                        List<StampModel> modelList = worker.getModelList();
                        if (modelList != null) {
                            for (int i = modelList.size() -1; i > -1; i--) {
                                insertStamp((StampModel)modelList.get(i), insertRow);
                            }
                        }
                        
                    } else {
                        warning(ClientContext.getString("diagnosis.title"), sdl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("diagnosis.title");
                    new TimeoutWarning(parent, title, null).start();
                }
            }
        });
        worker.start();
        statusPanel.start("");
        taskTimer.start();
    }
    
    /**
     * 傷病名スタンプをデータベースから取得しテーブルへ挿入する。
     * Worker Thread で実行される。
     * @param stampInfo
     */
    private void insertStamp(StampModel sm, int row) {
        
        if (sm != null) {
            RegisteredDiagnosisModel module
                    = (RegisteredDiagnosisModel) BeanUtils.xmlDecode(sm.getStampBytes());
            
            // 今日の日付を疾患開始日として設定する
            GregorianCalendar gc = new GregorianCalendar();
            String today = MMLDate.getDate(gc);
            module.setStartDate(today);
            
//            // デフォルトのCategory 値をセットする
//            DiagnosisCategoryModel dc = new DiagnosisCategoryModel();
//            dc.setDiagnosisCategory(IInfoModel.DEFAULT_DIAGNOSIS_CATEGORY);
//            dc.setDiagnosisCategoryDesc(IInfoModel.DEFAULT_DIAGNOSIS_CATEGORY_DESC);
//            dc.setDiagnosisCategoryCodeSys(IInfoModel.DEFAULT_DIAGNOSIS_CATEGORY_CODESYS);
//            module.setDiagnosisCategoryModel(dc);
            
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
        if (list == null) {
            return;
        }
        
        int len = list.size();
        
        if (ascend) {
            // 昇順なのでテーブルの最後へ追加する
            for (int i = 0; i < len; i++) {
                RegisteredDiagnosisModel module
                        = (RegisteredDiagnosisModel) list.get(i);
                tableModel.addRow(module);
                addAddedList(module);
            }
            
        } else {
            // 降順なのでテーブルの先頭へ追加する
            for (int i = len - 1; i > -1; i--) {
                RegisteredDiagnosisModel module
                        = (RegisteredDiagnosisModel) list.get(i);
                tableModel.insertRow(0, module);
                addAddedList(module);
            }
        }
    }
    
    /**
     * 新規及び変更された傷病名を保存する。
     */
    public void save() {
        
        if ( (addedDiagnosis == null || addedDiagnosis.size() == 0) &&
                (updatedDiagnosis == null || updatedDiagnosis.size() == 0) ) {
            return;
        }
        
        final boolean sendDiagnosis = Project.getSendDiagnosis()
        && ((ChartPlugin) getContext()).getCLAIMListener() != null ? true : false;
        
        
        // continue to save
        Date confirmed = new Date();
        
        if (addedDiagnosis != null && addedDiagnosis.size() > 0) {
            
            for (RegisteredDiagnosisModel rd : addedDiagnosis) {
                
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
            }
        }
        
        if (updatedDiagnosis != null && updatedDiagnosis.size() > 0) {
            
            for (RegisteredDiagnosisModel rd : updatedDiagnosis) {
                
                // 現バージョンは上書きしている
                rd.setCreator(Project.getUserModel());
                rd.setConfirmed(confirmed);
                rd.setRecorded(confirmed);
                rd.setStatus(IInfoModel.STATUS_FINAL);
                
                // TODO トラフィック
                rd.setPatientLiteModel(getContext().getPatient().patientAsLiteModel());
                rd.setUserLiteModel(Project.getUserModel().getLiteModel());
            }
        }
        
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        
        // Worker を生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        final DocumentDelegater ddl = new DocumentDelegater();
        final DiagnosisPutTask worker = new DiagnosisPutTask(addedDiagnosis, updatedDiagnosis, sendDiagnosis, ddl, taskLength);
        
        // タイマーを起動する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                
                if (worker.isDone()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    
                    // 保存及び更新された数
                    if (ddl.isNoError()) {
                        List<Long> addedIds = worker.getAddedIds();
                        if (addedDiagnosis != null && addedIds != null && addedDiagnosis.size() == addedIds.size()) {
                            for (int i = 0; i < addedDiagnosis.size(); i++) {
                                RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) addedDiagnosis.get(i);
                                rd.setId(addedIds.get(i));
                            }
                        }
                        clearDiagnosisList();
                    } else {
                        //エラー内容を表示する
                        warning(ClientContext.getString("diagnosis.title"), ddl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("diagnosis.title");
                    new TimeoutWarning(parent, title, null).start();
                }
            }
        });
        worker.start();
        statusPanel.start("");
        taskTimer.start();
    }
    
    /**
     * 指定期間以降の傷病名を検索してテーブルへ表示する。
     * バッググランドスレッドで実行される。
     */
    @SuppressWarnings("unchecked")
    public void getDiagnosisHistory(Date past) {
        
        DiagnosisSearchSpec spec = new DiagnosisSearchSpec();
        spec.setCode(DiagnosisSearchSpec.PATIENT_SEARCH);
        spec.setKarteId(getContext().getKarte().getId());
        if (past != null) {
            spec.setFromDate(past);
        }
        
        final DocumentDelegater ddl = new DocumentDelegater();
        
        // ProgressBar を取得する
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        
        // Worker を生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        final DiagnosisGetTask worker = new DiagnosisGetTask(spec, ddl, taskLength);
        
        // タスクタイマーを生成する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                statusPanel.setMessage(worker.getMessage());
                
                if (worker.isDone()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    orcaButton.setEnabled(true);    // reset
                    
                    // エラーをチェックする
                    if (ddl.isNoError()) {
                        // noError
                        List list = worker.getResult();
                        if (list != null && list.size() > 0) {
                            if (ascend) {
                                Collections.sort(list);
                                RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) list.get(0);
                                dolphinFirstDate = rd.getStartDate();
                            } else {
                                Collections.sort(list, Collections.reverseOrder());
                                int index = list.size() -1;
                                RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) list.get(index);
                                dolphinFirstDate = rd.getStartDate();
                            }
                            tableModel.setObjectList(list);
                            setDiagnosisCount(list.size());
                        }
                        
                    } else {
                        // エラー内容を表示する
                        warning(ClientContext.getString("diagnosis.title"), ddl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    // タイムアウト表示をする
                    statusPanel.stop();
                    taskTimer.stop();
                    orcaButton.setEnabled(true);
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("diagnosis.title");
                    new TimeoutWarning(parent, title, null).start();
                }
            }
        });
        worker.start();
        statusPanel.start("");
        taskTimer.start();
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
        
        // Worker を生成する
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        final DocumentDelegater ddl = new DocumentDelegater();
        final DiagnosisDeleteTask worker = new DiagnosisDeleteTask(list, ddl, taskLength);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                worker.getCurrent();
                if (worker.isDone()) {
                    taskTimer.stop();
                    statusPanel.stop("");
                    if (ddl.isNoError()) {
                        tableModel.deleteRow(row);
                        setDiagnosisCount(tableModel.getObjectCount());
                        // 更新リストにある場合
                        // 更新リストから取り除く
                        if (updatedDiagnosis != null) {
                            updatedDiagnosis.remove(model);
                            controlUpdateButton();
                        }
                    } else {
                        warning(ClientContext.getString("diagnosis.title"), ddl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    statusPanel.stop("");
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("diagnosis.title");
                    new TimeoutWarning(parent, title, null).start();
                }
            }
        });
        worker.start();
        statusPanel.start("");
        taskTimer.start();
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
        System.out.println("from = " + from);
        System.out.println("to = " + to);
        
        final Component comp = (Component) this.getUI();
        
        // DAOを生成する
        final SqlOrcaView dao = new SqlOrcaView();
        
        // ReflectMonitor を生成する
        final ReflectMonitor rm = new ReflectMonitor();
        rm.setReflection(dao, 
                         "getOrcaDisease", 
                         new Class[]{String.class, String.class, String.class, Boolean.class}, 
                         new Object[]{patientId, from, to, new Boolean(ascend)});
        rm.setMonitor(SwingUtilities.getWindowAncestor(comp), ORCA_VIEW, "病名を検索しています...  ", 200, 60*1000);
        
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
                            if (ascend) {
                                Collections.sort(list);
                            } else {
                                Collections.sort(list, Collections.reverseOrder());
                            }
                            tableModel.addRows(list);
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
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        private void maybeShowPopup(MouseEvent e) {
            
            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(getContext().getContext().getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[] { -12, 0 });
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
     * StampTask
     */
    protected class StampTask extends AbstractInfiniteTask {
        
        private StampModel stampModel;
        private String stampId;
        private StampDelegater sdl;
        
        public StampTask(String stampId, StampDelegater sdl, int taskLength) {
            this.stampId = stampId;
            this.sdl = sdl;
            setTaskLength(taskLength);
        }
        
        protected StampModel getStamp() {
            return stampModel;
        }
        
        protected void doTask() {
            stampModel = sdl.getStamp(stampId);
            setDone(true);
        }
    }
    
    /**
     * DiagnosisGetTask
     */
    protected class DiagnosisGetTask extends AbstractInfiniteTask {
        
        private List list;
        private DocumentDelegater ddl;
        private DiagnosisSearchSpec spec;
        
        public DiagnosisGetTask(DiagnosisSearchSpec spec, DocumentDelegater ddl, int taskLength) {
            this.spec = spec;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        protected List getResult() {
            return list;
        }
        
        protected void doTask() {
            list = ddl.getDiagnosisList(spec);
            setDone(true);
        }
    }
    
    /**
     * DiagnosisDeleteTask
     */
    protected class DiagnosisDeleteTask extends AbstractInfiniteTask {
        
        private List<Long> list;
        private DocumentDelegater ddl;
        private int putCode;
        
        public DiagnosisDeleteTask(List<Long> list, DocumentDelegater ddl, int taskLength) {
            this.list = list;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        protected int getResult() {
            return putCode;
        }
        
        protected void doTask() {
            putCode = ddl.removeDiagnosis(list);
            setDone(true);
        }
    }
    
    /**
     * DiagnosisPutTask
     */
    protected class DiagnosisPutTask extends AbstractInfiniteTask {
        
        private List<RegisteredDiagnosisModel> added;
        private List<RegisteredDiagnosisModel> updated;
        private boolean sendClaim;
        private DocumentDelegater ddl;
        private List<Long> ids;
        
        public DiagnosisPutTask(List<RegisteredDiagnosisModel> added,
                List<RegisteredDiagnosisModel> updated,
                boolean sendClaim,
                DocumentDelegater ddl,
                int taskLength) {
            
            this.added = added;
            this.updated = updated;
            this.sendClaim = sendClaim;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        protected List<Long> getAddedIds() {
            return ids;
        }
        
        protected void doTask() {
            
            // 更新する
            if (updated != null && updated.size() > 0) {
                ddl.updateDiagnosis(updated);
            }
            
            // 保存する
            if (added != null && added.size() > 0) {
                ids = ddl.putDiagnosis(added);
            }
            
            //
            // 追加病名を CLAIM 送信する
            //
            if (sendClaim && added != null && added.size() > 0) {
                
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
                UserLiteModel creator = added.get(0).getUserLiteModel();
                PatientLiteModel patient = added.get(0).getPatientLiteModel();
                
                // ヘルパークラスを生成する
                DiseaseHelper dhl = new DiseaseHelper();
                dhl.setPatientId(patient.getPatientId());
                dhl.setConfirmDate(confirmDate);
                dhl.setCreator(creator);
                dhl.setDiagnosisModuleItems(moduleItems);
                dhl.setGroupId(GUIDGenerator.generate(dhl));
                dhl.setDepartment(getContext().getPatientVisit().getDepartmentCode());
                dhl.setDepartmentDesc(getContext().getPatientVisit().getDepartment());
                //dhl.setDepartment(Project.getUserModel().getDepartmentModel().getDepartment());
                //dhl.setDepartmentDesc(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
                
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
                ClaimMessageListener claimListener = ((ChartPlugin) getContext()).getCLAIMListener();
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
                UserLiteModel creator = updated.get(0).getUserLiteModel();
                PatientLiteModel patient = updated.get(0).getPatientLiteModel();
                
                // ヘルパークラスを生成する
                DiseaseHelper dhl = new DiseaseHelper();
                dhl.setPatientId(patient.getPatientId());
                dhl.setConfirmDate(confirmDate);
                dhl.setCreator(creator);
                dhl.setDiagnosisModuleItems(moduleItems);
                dhl.setGroupId(GUIDGenerator.generate(dhl));
                dhl.setDepartment(Project.getUserModel().getDepartmentModel().getDepartment());
                dhl.setDepartmentDesc(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
                
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
                ClaimMessageListener claimListener = ((ChartPlugin) getContext()).getCLAIMListener();
                if (claimListener != null) {
                    claimListener.claimMessageEvent(event);
                }
            }
            
            setDone(true);
        }
    }
    
    /**
     *
     */    
    class DolphinOrcaRenderer extends DefaultTableCellRenderer {
        
        /** JTableレンダラ用の奇数カラー */
        private Color ODD_COLOR = ClientContext.getColor("color.odd");
    
        /** JTableレンダラ用の偶数カラー */
        private Color EVEN_COLOR = ClientContext.getColor("color.even");
        
        private Color ORCA_BACK = ClientContext.getColor("color.CALENDAR_BACK");
        
        /** Creates new IconRenderer */
        public DolphinOrcaRenderer() {
            super();
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused, row, col);
            
            RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) tableModel.getObject(row);
            
            // ORCA レコードかどうかを判定する
            boolean orca = (rd != null && rd.getStatus() != null && rd.getStatus().equals(ORCA_RECORD)) ? true : false;
            
            if (orca) {
                setBackground(ORCA_BACK);
                
            } else {
                
                if (row % 2 == 0) {
                    setBackground(EVEN_COLOR);
                } else {
                    setBackground(ODD_COLOR);
                }
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            if (value != null && value instanceof String) {
                ((JLabel) c).setText((String) value);
            } else {
                ((JLabel) c).setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
}