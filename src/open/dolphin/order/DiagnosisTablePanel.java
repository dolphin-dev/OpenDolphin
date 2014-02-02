/*
 * DiagnosisEditor.java
 * Copyright (C) 2007 Dolphin Project. All rights reserved.
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
package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.ClientContext;
import open.dolphin.client.IStampModelEditor;
import open.dolphin.client.OddEvenRowRenderer;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.table.IMECellEditor;
import open.dolphin.table.ObjectTableModel;
import open.dolphin.util.MMLDate;

/**
 * 傷病名編集テーブルクラス。
 *
 * @author Kazushi Minagawa
 */
public class DiagnosisTablePanel extends JPanel implements PropertyChangeListener {
    
    /** 傷病名の修飾語コード */
    private static final String MODIFIER_CODE = "ZZZ";
    
    /** 傷病名手入力時につけるコード */
    private static final String HAND_CODE = "0000999";
    
    //
    // Diagnosis table のパラメータ
    //
    private static final int NAME_COL            = 0;
    private static final int CATEGORY_COL        = 1;
    private static final int OUTCOME_COL         = 2;
    private static final int START_DATE_COL      = 3;
    private static final int END_DATE_COL        = 4;
    private static final int[] DIAGNOSIS_TABLE_COLUMN_WIDTHS = {
        300, 90, 90, 90, 90
    };
    private static final int START_NUM_ROWS = 10;
    private static final int ROW_HEIGHT = 18;
    private static final String REMOVE_BUTTON_IMAGE = "del_16.gif";
    private static final String CLEAR_BUTTON_IMAGE  = "remov_16.gif";
    private static final String INFO_BUTTON_IMAGE   = "about_16.gif";
    private static final int TABLE_WIDTH = 890;
    private static final int TABLE_HEIGHT = 90;
    
    private static final String TOOLTIP_REMOVE = "選択した傷病名を削除します";
    private static final String TOOLTIP_CLEAR  = "テーブルをクリアします";
    private static final String TOOLTIP_TABLE  = "Drag & Drop で順番を入れ替えることができます";
    private static final String TOOLTIP_COMBINE  = "テーブルの行を連結して修飾語付きの傷病名にします";
    
    /** 修飾語付き傷病名 表示レベル */
    private static final String LABEL_COMBINED_DIAGNOSIS = "連結した傷病名:";
    
    /** マスタ検索の選択アイテムプロパティ */
    private static final String SELECTED_ITEM_PROP = "selectedItemProp";
    
    /** 複合病名表示フィールドの長さ */
    private static final int COMBINED_FIELD_LENGTH = 20;
    
    /** Table model */
    private ObjectTableModel tableModel;
    
    /** 傷病名編集テーブル */
    private JTable table;
    
    /** カテゴリ ComboBox*/
    private JComboBox categoryCombo;
    
    /** 転帰ComboBox */
    private JComboBox outcomeCombo;
    
    /** デフォルトのカテゴリ */
    private DiagnosisCategoryModel defaultCategory;
    
    /** 削除ボタン */
    private JButton removeButton;
    
    /** クリアボタン */
    private JButton clearButton;
    
    /** 複合病名を表示するフィールド */
    private JTextField combinedDiagnosis;
    
    /** State を表示するラベル */
    private JLabel stateLabel;
    
    /** Stamp Editor */
    private IStampModelEditor context;
    
    /** カレンダーカラーテーブル */
    private HashMap<String, Color> cTable;
    
    /** 状態マシン */
    private DiagnosisStateMgr curState;
    
    
    /**
     * DiagnosisTablePanelを生成する。
     */
    public DiagnosisTablePanel(IStampModelEditor context) {
        
        super(new BorderLayout());
        
        setContext(context);
        
        // Popup カレンダー用のカラーテーブルを生成する
        cTable = new HashMap<String, Color>(10, 0.75f);
        cTable.put("TODAY", Color.PINK);
        cTable.put("BIRTHDAY", Color.CYAN);
        cTable.put("PVT", Color.YELLOW);
        cTable.put("DOC_HISTORY", Color.YELLOW);
        
        // テーブルのカラム名を取得する
        String[] diganosisColumns = ClientContext.getStringArray("diagnosis.columnNames");
        
        // テーブルモデルを生成する
        tableModel = new ObjectTableModel(diganosisColumns, START_NUM_ROWS) {
            
            // 病名カラムも修飾語の編集が可能
            public boolean isCellEditable(int row, int col) {
                return true;
            }
            
            public Object getValueAt(int row, int col) {
                
                RegisteredDiagnosisModel model = (RegisteredDiagnosisModel) getObject(row);
                
                if (model == null) {
                    return null;
                }
                
                String ret = null;
                
                switch (col) {
                    
                    case NAME_COL:
                        ret = model.getDiagnosis();
                        break;
                        
                    case CATEGORY_COL:
                        if (model.getDiagnosisCategoryModel() != null) {
                            ret = model.getDiagnosisCategoryModel().getDiagnosisCategoryDesc();
                        }
                        break;
                        
                    case OUTCOME_COL:
                        if (model.getDiagnosisOutcomeModel() != null) {
                            ret = model.getDiagnosisOutcomeModel().getOutcomeDesc();
                        }
                        break;
                        
                    case START_DATE_COL:
                        ret = model.getStartDate();
                        break;
                        
                    case END_DATE_COL:
                        ret = model.getEndDate();
                        break;
                }
                
                return ret;
            }
            
            public void setValueAt(Object o, int row, int col) {
                
                if (o == null) {
                    return;
                }
                
                String value = (String) o;
                value = value.trim();
                
                if (value.equals("")){
                    return;
                }
                
                RegisteredDiagnosisModel model = (RegisteredDiagnosisModel) getObject(row);
                
                switch (col) {
                    
                    case NAME_COL:
                        //
                        // 病名が手入力された場合は、コードに 0000999 を設定する
                        //
                        if (model != null) {
                            model.setDiagnosis(value);
                            model.setDiagnosisCode(HAND_CODE);
                            fireTableCellUpdated(row, col);
                            
                        } else {
                            model = new RegisteredDiagnosisModel();
                            model.setDiagnosis(value);
                            model.setDiagnosisCode(HAND_CODE);
                            // 分類名 日付自動入力
                            //
                            // 主病名のセットはしない
                            //model.setDiagnosisCategoryModel(defaultCategory);
                            GregorianCalendar gc = new GregorianCalendar();
                            String today = MMLDate.getDate(gc);
                            model.setStartDate(today);
                            addRow(model);
                            curState.processEvent(DiagnosisStateMgr.Event.ADDED);
                        }
                        break;
                        
                    case CATEGORY_COL:
                        if (model != null) {
                            defaultCategory = (DiagnosisCategoryModel) o;
                            model.setDiagnosisCategoryModel(defaultCategory);
                            fireTableCellUpdated(row, col);
                        } else {
                            o = null;
                        }
                        break;
                        
                    case OUTCOME_COL:
                        if (model != null) {
                            model.setDiagnosisOutcomeModel((DiagnosisOutcomeModel) o);
                            fireTableCellUpdated(row, col);
                        } else {
                            o = null;
                        }
                        break;
                        
                    case START_DATE_COL:
                        if (model != null) {
                            model.setStartDate((String)o);
                            fireTableCellUpdated(row, col);
                        } else {
                            o = null;
                        }
                        break;
                        
                    case END_DATE_COL:
                        if (model != null) {
                            model.setEndDate((String)o);
                            fireTableCellUpdated(row, col);
                        } else {
                            o = null;
                        }
                        break;
                }
            }
        };
        
        //
        // Table を生成し transferHandler を生成する
        //
        table = new JTable(tableModel);;
        table.setToolTipText(TOOLTIP_TABLE);
        table.setTransferHandler(new RegisteredDiagnosisTransferHandler(DiagnosisTablePanel.this)); // TransferHandler
        table.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask)
                            ? TransferHandler.COPY
                            : TransferHandler.MOVE;
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, action);
            }
            
            public void mouseMoved(MouseEvent e) {
            }
        });
        
        table.setRowHeight(ROW_HEIGHT);
        table.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        table.setPreferredSize(new Dimension(TABLE_WIDTH,TABLE_HEIGHT));
        //table.setRowMargin(5);
        //table.setIntercellSpacing(new Dimension(-5,-5));
        table.setSurrendersFocusOnKeystroke(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        ListSelectionModel m = table.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    //notifySelectedRow();
                    curState.processEvent(DiagnosisStateMgr.Event.SELECTED);
                }
            }
        });
        
        // CellEditor を設定する
        // NAME_COL clickCountToStart=2, IME=ON
        TableColumn column = table.getColumnModel().getColumn(NAME_COL);
        column.setCellEditor(new IMECellEditor(new JTextField(), 2, true));
        
        // Category comboBox 入力を設定する
        String[] values = ClientContext.getStringArray("diagnosis.category");
        String[] descs = ClientContext.getStringArray("diagnosis.categoryDesc");
        String[] codeSys = ClientContext.getStringArray("diagnosis.categoryCodeSys");
        DiagnosisCategoryModel[] categoryList = new DiagnosisCategoryModel[values.length + 1];
        DiagnosisCategoryModel dcm = new DiagnosisCategoryModel();
        dcm.setDiagnosisCategory("");
        dcm.setDiagnosisCategoryDesc("");
        categoryList[0] = null;
        for (int i = 0; i < values.length; i++) {
            dcm = new DiagnosisCategoryModel();
            dcm.setDiagnosisCategory(values[i]);
            dcm.setDiagnosisCategoryDesc(descs[i]);
            dcm.setDiagnosisCategoryCodeSys(codeSys[i]);
            categoryList[i+1] = dcm;
        }
        categoryCombo = new JComboBox(categoryList);
        column = table.getColumnModel().getColumn(CATEGORY_COL);
        column.setCellEditor(new DefaultCellEditor(categoryCombo));
        defaultCategory = categoryList[1];
        
        // Outcome comboBox 入力を設定する
        String[] ovalues = ClientContext.getStringArray("diagnosis.outcome");
        String[] odescs = ClientContext.getStringArray("diagnosis.outcomeDesc");
        String ocodeSys = ClientContext.getString("diagnosis.outcomeCodeSys");
        DiagnosisOutcomeModel[] outcomeList = new DiagnosisOutcomeModel[ovalues.length + 1];
        DiagnosisOutcomeModel dom = new DiagnosisOutcomeModel();
        dom.setOutcome("");
        dom.setOutcomeDesc("");
        //outcomeList[0] = dom;
        //
        // 主病名は使用しないらしい
        //
        outcomeList[0] = null;
        for (int i = 0; i < ovalues.length; i++) {
            dom = new DiagnosisOutcomeModel();
            dom.setOutcome(ovalues[i]);
            dom.setOutcomeDesc(odescs[i]);
            dom.setOutcomeCodeSys(ocodeSys);
            outcomeList[i+1] = dom;
        }
        outcomeCombo = new JComboBox(outcomeList);
        column = table.getColumnModel().getColumn(OUTCOME_COL);
        column.setCellEditor(new DefaultCellEditor(outcomeCombo));
        
        // Start Date && EndDate Col
        column = table.getColumnModel().getColumn(START_DATE_COL);
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        new PopupListener(tf);
        DefaultCellEditor de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        
        column = table.getColumnModel().getColumn(END_DATE_COL);
        tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        new PopupListener(tf);
        de = new DefaultCellEditor(tf);
        column.setCellEditor(de);
        
        // 列幅設定
        int len = DIAGNOSIS_TABLE_COLUMN_WIDTHS.length;
        for (int i = 0; i < len; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(DIAGNOSIS_TABLE_COLUMN_WIDTHS[i]);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        //
        // 複合病名と Command button
        //
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        
        // 複合病名フィールドを生成する
        btnPanel.add(new JLabel(LABEL_COMBINED_DIAGNOSIS));
        btnPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        combinedDiagnosis = new JTextField(COMBINED_FIELD_LENGTH);
        combinedDiagnosis.setEditable(false);
        combinedDiagnosis.setToolTipText(TOOLTIP_COMBINE);
        // State を表示するラベル
        stateLabel = new JLabel("");
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(combinedDiagnosis);
        p.add(new JLabel(ClientContext.getImageIcon(INFO_BUTTON_IMAGE)));
        p.add(stateLabel);
        btnPanel.add(p);
        
        btnPanel.add(Box.createHorizontalGlue());
        
        // 削除ボタンを生成する
        removeButton = new JButton(ClientContext.getImageIcon(REMOVE_BUTTON_IMAGE));
        removeButton.setToolTipText(TOOLTIP_REMOVE);
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                // TableModel でレンジチェックしているので安全
                tableModel.removeRow(row);
                reconstractDiagnosis();
                curState.processEvent(DiagnosisStateMgr.Event.DELETED);
            }
        });
        btnPanel.add(removeButton);
        
        btnPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        
        // クリアボタンを生成する
        clearButton = new JButton(ClientContext.getImageIcon(CLEAR_BUTTON_IMAGE));
        clearButton.setToolTipText(TOOLTIP_CLEAR);
        clearButton.setEnabled(false);
        clearButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                tableModel.clear();
                combinedDiagnosis.setText("");
                curState.processEvent(DiagnosisStateMgr.Event.CLEARED);
            }
        });
        btnPanel.add(clearButton);
        
        btnPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        
        // 親ダイアログのOKボタンを追加する
        if (getContext().getContext().getOkButton() != null) {
            btnPanel.add(getContext().getContext().getOkButton());
        }
        //btnPanel.add(getContext().getOkButton());
        
        //
        // 状態マシンを開始する
        //
        curState = new DiagnosisStateMgr(removeButton, clearButton, stateLabel,
                                        tableModel, table, getContext());
        curState.enter();
        
        // カラムヘッダーを表示してレイアウトする
        //add(table.getTableHeader(), BorderLayout.NORTH);
        //JScrollPane scroller = new JScrollPane(table);
        //scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //add(scroller, BorderLayout.CENTER);
        add(table.getTableHeader(), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    /**
     * StampEditor を返す。
     * @return この編集テーブルの StampEditor
     */
    public IStampModelEditor getContext() {
        return context;
    }
    
    /**
     * StampEditor を設定する。
     * @param context この編集テーブルの StampEditor
     */
    public void setContext(IStampModelEditor context) {
        this.context = context;
    }
    
    /**
     * マスタ検索テーブルで選択されたアイテムを編集テーブルへ取り込む。
     * @param e PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals(SELECTED_ITEM_PROP)) {
            
            //
            // 通知された MasterItem から RegisteredDiagnosisModel 
            // を生成し、編集テーブルへ加える。
            //
            MasterItem item = (MasterItem) e.getNewValue();
            
            if (item != null) {
                
                RegisteredDiagnosisModel model = new RegisteredDiagnosisModel();
                model.setDiagnosis(item.getName());
                model.setDiagnosisCode(item.getCode());
                model.setDiagnosisCodeSystem(item.getMasterTableId());
                
                if (item.getCode().startsWith(MODIFIER_CODE)) {
                    
                    //
                    // 接頭語及び接尾語のケース
                    //
                    
                } else {
                    
                    //
                    // 分類名 日付自動入力する
                    // 主病名は使用しない
                    //
                    //model.setDiagnosisCategoryModel(defaultCategory);
                    GregorianCalendar gc = new GregorianCalendar();
                    String today = MMLDate.getDate(gc);
                    model.setStartDate(today);
                }
                
                tableModel.addRow(model);
                
                reconstractDiagnosis();
                
//                //
//                // コンポジット病名を表示する
//                //
//                StringBuilder sb = new StringBuilder();
//                String value = combinedDiagnosis.getText().trim();
//                if (!value.equals("")) {
//                    sb.append(value);
//                }
//                sb.append(item.getName());
//                combinedDiagnosis.setText(sb.toString());
                
                //
                // 状態マシンへイベントを送信する
                //
                curState.processEvent(DiagnosisStateMgr.Event.ADDED);
            }
        }
    }
    
    /**
     * テーブルをスキャンし、傷病名コンポジットする。
     */
    public void reconstractDiagnosis() {
        
        if (hasModifier()) {
            StringBuilder sb = new StringBuilder();
            int count = tableModel.getDataSize();
            for (int i = 0; i < count; i++) {
                RegisteredDiagnosisModel diag = (RegisteredDiagnosisModel) tableModel.getObject(i);
                sb.append(diag.getDiagnosis());
            }
            combinedDiagnosis.setText(sb.toString());
        } else {
            combinedDiagnosis.setText("");
        }
    }
    
    /**
     * 修飾語をふくんでいるかどうかを返す。
     */
    private boolean hasModifier() {
        boolean hasModifier = false;
        int count = tableModel.getDataSize();
        for (int i = 0; i < count; i++) {
            RegisteredDiagnosisModel diag = (RegisteredDiagnosisModel) tableModel.getObject(i);
            if (diag.getDiagnosisCode().startsWith(MODIFIER_CODE)) {
                hasModifier = true;
                break;
            }
        }
        return hasModifier;
    }
    
    /**
     * 傷病名テーブルをスキャンし修飾語つきの傷病にして返す。
     */
    public Object getValue() {
        
        if (hasModifier()) {
            return getValue1();
        } else {
            return getValue2();
        }
    }
    
    
    /**
     * 傷病名テーブルをスキャンし修飾語つきの傷病にして返す。
     */
    private Object getValue1() {
        
        RegisteredDiagnosisModel diagnosis = null;
        
        StringBuilder name = new StringBuilder();
        StringBuilder code = new StringBuilder();
        
        // テーブルをスキャンする
        int count = tableModel.getDataSize();
        for (int i = 0; i < count; i++) {
            
            RegisteredDiagnosisModel diag = (RegisteredDiagnosisModel) tableModel.getObject(i);
            String diagCode = diag.getDiagnosisCode();
            
            if (!diagCode.startsWith(MODIFIER_CODE)) {
                //
                // 修飾語でない場合は基本病名と見なし、パラメータを設定する
                //
                diagnosis = new RegisteredDiagnosisModel();
                diagnosis.setDiagnosisCodeSystem(diag.getDiagnosisCodeSystem());
                diagnosis.setDiagnosisCategoryModel(diag.getDiagnosisCategoryModel());
                diagnosis.setDiagnosisOutcomeModel(diag.getDiagnosisOutcomeModel());
                diagnosis.setStartDate(diag.getStartDate());
                diagnosis.setEndDate(diag.getEndDate());
            
            } else {
                //
                // ZZZ をトリムする ORCA 実装
                //
                diagCode = diagCode.substring(MODIFIER_CODE.length());
            }
            
            //
            // コードを . で連結する
            //
            if (code.length() > 0) {
                code.append(".");
            }
            code.append(diagCode);
            
            //
            // 名前を連結する
            //
            name.append(diag.getDiagnosis());
            
        }
        
        if (diagnosis != null && name.length() > 0 && code.length() > 0) {
            
            //
            // 名前とコードを設定する
            //
            diagnosis.setDiagnosis(name.toString());
            diagnosis.setDiagnosisCode(code.toString());
            ArrayList ret = new ArrayList(1);
            ret.add(diagnosis);
            
            return ret;
            
        } else {
            return null;
        }
    }
    
        
    /**
     * 傷病名テーブルをスキャンし修飾語つきの傷病にして返す。
     */
    private Object getValue2() {
        
        return tableModel.getObjectList();
    }
    
    public void setValue(Object[] o) {
    }
        

    /**
     * Popup Calendar クラス。
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
                CalendarCardPanel cc = new CalendarCardPanel(cTable);
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[]{-12, 0});
                popup.insert(cc,0);
                popup.show(e.getComponent(),e.getX(), e.getY());
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
}

