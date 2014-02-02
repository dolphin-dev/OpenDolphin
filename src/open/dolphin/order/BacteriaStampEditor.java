/*
 * BacteriaStampEditor.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Module;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.awt.im.InputSubset;
import flextable.*;


/**
 * Bacteria test editor.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class BacteriaStampEditor extends StampModelEditor  {
    
    // Bacteria test table related
    private static final String[] COLUMN_NAMES = {
       "名　称", "対 象 薬 品", "数　量"
    };
    private static final int[] COLUMN_WIDTH = {
        300, 200, 50
    };
    private static String[] NUMBER_LIST = null;
    static {
        NUMBER_LIST = new String[31];
        for (int i = 0; i < 31; i++) {
            NUMBER_LIST[i] = String.valueOf(i+ 1);
        }
    }
    private static final String TREATMENT_CRITERIA = "600";  //"62";
    private static final int NUM_ROWS           = 9;
    private static final int NAME_COLUMN        = 0;
    private static final int MEDICINE_COLUMN    = 1;
    private static final int NUMBER_COLUMN      = 2;
    private static final int NUMBER_ROW         = 0;
    
    private static final String ORDER_NAME      = "細菌検査";
    
    // Claim 診療行為区分
    private static final String classCode        = "600";
    private static final String classCodeId      = "Claim007";
    private static final String classCodeName    = "検査";
	private static final String entityName       = "bacteria";
        
    // 診療種別区分(手技・材料・薬剤)
    private String subclassCode;
    private static final String subclassCodeId  = "Claim003";
    
    private static final String DEFAULT_BUNDLE_NUMBER = "1";
        
    private TestTablePanel testTable;
    private MasterTabPanel masterPanel;
    
    private static final String RESOURCE_BASE       = "/open/dolphin/resources/images/";
    private static final String REMOVE_BUTTON_IMAGE = "Delete24.gif";
    private static final String CLEAR_BUTTON_IMAGE  = "New24.gif";
    private static final String TABLE_TITLE         = "細菌検査セット";
    private static final String WINDOW_TITLE        = "細菌検査エディタ - " + ClientContext.getString("app.title");
    
    /** 
     * Creates new InjectionStampEditor 
     */
    public BacteriaStampEditor() {
        
        //this.title = WINDOW_TITLE;
                
        // Creates table
        testTable = new TestTablePanel();
        Border b = BorderFactory.createEtchedBorder();
        testTable.setBorder(BorderFactory.createTitledBorder(b, TABLE_TITLE));
        
        // Start master
        masterPanel = new MasterTabPanel();
        masterPanel.setSearchClass(TREATMENT_CRITERIA);
        masterPanel.startCharge(testTable);
        
        testTable.setParent(this);
        
        setLayout(new BorderLayout());
        add(testTable, BorderLayout.NORTH);
        add(masterPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(920, 610));
    }
    
    public Object getValue() {
        return testTable.getValue();
    }
    
    public void setValue(Object val) {
        testTable.setValue(val);
    }
    
    public void dispose() {
        masterPanel.stopCharge(testTable);
    }
    
    /**
     * Test order table.
     *
     * @author  Kazushi Minagawa, Digital Globe, Inc.
     */
    protected final class TestTablePanel extends JPanel implements PropertyChangeListener {

        private FlexibleTable medTable;
        private JTextField   adminMemo;
        private int itemCount;
        private final JComboBox numberCombo;

        private JButton removeButton;
        private JButton clearButton;
        private StampModelEditor parent;
        
        private Module savedStamp;
        private JTextField stampNameField;
        private JRadioButton expand;
        private JRadioButton turnInRadio;

        /** 
         * Creates new MedicineTable
         */
        public TestTablePanel() {

            super(new BorderLayout());

            medTable = new FlexibleTable(new FlexibleTableModel(COLUMN_NAMES, NUM_ROWS)) {

                public boolean isCellEditable(int row, int col) {
                    return ( (col == MEDICINE_COLUMN) || 
                             (col == NUMBER_COLUMN) )  ? true : false;
                }
            };
            medTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            medTable.setRowSelectionAllowed(true);
            //medTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            ListSelectionModel m = medTable.getSelectionModel();
            m.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        notifySelectedRow();
                    }
                }
            });

            // 列幅を設定する
            TableColumn column = null;
            int len = COLUMN_NAMES.length;
            for (int i = 0; i < len; i++) {
                column = medTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(COLUMN_WIDTH[i]);
            }
            
            // Set the combo editor to the bundleNumber column
            numberCombo = new JComboBox(NUMBER_LIST);
            TableColumn numberColumn = medTable.getColumnModel().getColumn(NUMBER_COLUMN);
            numberColumn.setCellEditor (new DefaultCellEditor(numberCombo));

            // Combine rows
            combineRows(NUMBER_ROW, NUMBER_COLUMN, NUM_ROWS);

            // Stamp, Admin comment & memo
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
            
            // スタンプ名編集フィールド
            p.add(new JLabel("スタンプ名"));
            p.add(Box.createRigidArea(new Dimension(5,0)));
            stampNameField = new JTextField();
            stampNameField.setOpaque(true);
            stampNameField.setBackground(new Color(251,239,128));
            stampNameField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                   stampNameField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                }
                public void focusLosted(FocusEvent event) {
                   stampNameField.getInputContext().setCharacterSubsets(null);
                }
            });
            Dimension dim = new Dimension(150,21);
            stampNameField.setPreferredSize(dim);
            stampNameField.setMaximumSize(dim);
            p.add(stampNameField);

            // 表示形態
            p.add(Box.createRigidArea(new Dimension(7,0)));
            p.add(new JLabel("表示"));
            expand = new JRadioButton("縦並び"); 
            turnInRadio = new JRadioButton("横並び");
            ButtonGroup bg = new ButtonGroup();
            bg.add(expand);
            bg.add(turnInRadio);
            turnInRadio.setSelected(true);
            p.add(expand);
            p.add(turnInRadio);
            
            p.add(Box.createHorizontalGlue());
            
            p.add(new JLabel("検体材料"));
            p.add(Box.createRigidArea(new Dimension(5,0)));
            adminMemo = new JTextField();
            dim = new Dimension(200,21);
            adminMemo.setPreferredSize(dim);
            adminMemo.setMaximumSize(dim);
            p.add(adminMemo);

            p.add(Box.createHorizontalGlue());

            // Command buttons
            removeButton = new JButton(createImageIcon(REMOVE_BUTTON_IMAGE));
            removeButton.setEnabled(false);
            removeButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    removeSelectedItem();
                }
            });
            p.add(removeButton);

            p.add(Box.createRigidArea(new Dimension(5, 0)));

            clearButton = new JButton(createImageIcon(CLEAR_BUTTON_IMAGE));
            clearButton.setEnabled(false);
            clearButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    clear();
                }
            });
            p.add(clearButton);     
            p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

            this.add(medTable.getTableHeader(), BorderLayout.NORTH);
            this.add(medTable, BorderLayout.CENTER);
            this.add(p, BorderLayout.SOUTH);

            // Set the default bundle number
            setDefaultBundleNumber();
        }

        private void setDefaultBundleNumber() {
            FlexibleTableModel model = (FlexibleTableModel)medTable.getModel();
            model.setValueAt(DEFAULT_BUNDLE_NUMBER, NUMBER_ROW, NUMBER_COLUMN); 
        }

        public void setParent(StampModelEditor parent) {
            this.parent = parent;
        }

        public void propertyChange(PropertyChangeEvent e) {

            String prop = e.getPropertyName();

            if (prop.equals("selectedItemProp")) {

                if (itemCount < NUM_ROWS) {

                    MasterItem item = (MasterItem)e.getNewValue();
                    medTable.setValueAt(item, itemCount, NAME_COLUMN);

                    // 診療行為のみ
                    FlexibleTableModel model = (FlexibleTableModel)medTable.getModel();
                    model.fireTableRowsUpdated(itemCount, itemCount);
                    itemCount++;
                    notifyCount();
                    notifySelectedRow();
                }
            }
        }

        private void notifyCount() {
            boolean b = (itemCount > 0) ? true : false;
            clearButton.setEnabled(b);
            checkValidModel();
        }

        private void notifySelectedRow() {
            int index = medTable.getSelectedRow();
            int val = -1;
            if ( index > val ) {
                Object o = medTable.getValueAt(index, 0);
                if (o == null) {
                    index = val;
                }
            }
            boolean b = (index > -1) ? true : false;
            removeButton.setEnabled(b);
        }    

        private void clear() {

            for (int i = 0; i < NUM_ROWS; i++) {
                for ( int j = 0; j < COLUMN_NAMES.length -1; j++) {
                    medTable.setValueAt(null, i, j);
                }
            }        
            FlexibleTableModel model = (FlexibleTableModel)medTable.getModel();
            model.fireTableRowsUpdated(0, NUM_ROWS);
            itemCount= 0;
            notifyCount();
            notifySelectedRow();
        }

        private void removeSelectedItem() {

            int index = medTable.getSelectedRow();
            if (index < 0) {
                notifySelectedRow();
                return;
            }
            Object o = medTable.getValueAt(index, 0);
            if ( o == null ) {
                notifySelectedRow();
                return;
            }

            for (int i = index; i < itemCount; i++) {
                for ( int j = 0; j < COLUMN_NAMES.length -1; j++) {
                    o = medTable.getValueAt(i + 1, j);
                    medTable.setValueAt(o, i, j);    
                }
            }
            for ( int j = 0; j < COLUMN_NAMES.length -1; j++) {
                medTable.setValueAt(null, itemCount - 1, j);    
            }
            itemCount--;
            notifyCount();
            notifySelectedRow();
        }

        private void combineRows(int row, int col, int nRows) {

            FlexibleTableModel model = (FlexibleTableModel)medTable.getModel();
            CellAttribute cellAtt = (CellAttribute)model.getCellAttribute();
            int[] r = new int[nRows];
            for (int i = 0; i < nRows; i++) {
                r[i] = row + i;
            }
            int[] c = {col};
            cellAtt.combine(r,c);       
        }

        public Object getValue() {

            // Stamp　名と表示形式をセット
            String text = stampNameField.getText().trim();
            if (! text.equals("")) {
                savedStamp.getModuleInfo().setName(text);
            }
            //savedStamp.getStampInfo().setTurnIn(turnInRadio.isSelected());
        
            BundleDolphin test = new BundleDolphin();
            test.setOrderName(ORDER_NAME);
            
            test.setClassCode(classCode);
            test.setClassCodeSystem(classCodeId);
            test.setClassName(classCodeName);
            //test.setEntityName(entityName);

            FlexibleTableModel tableModel = (FlexibleTableModel)medTable.getModel();
            int rows = tableModel.getRowCount();
            String name;
            String memo;

			ClaimItem item;
            MasterItem mItem;

            for (int i = 0; i < itemCount; i++) {

                mItem = (MasterItem)tableModel.getValueAt(i, NAME_COLUMN);

                if (mItem != null) {

                    item = new ClaimItem();
                    // Code Name TableId
                    item.setName(mItem.name);
                    item.setCode(mItem.code);
                    //item.setTableId(mItem.masterTableId);
                    
                    // 診療種別
                    subclassCode = String.valueOf(mItem.classCode);
                    item.setClassCode(subclassCode);
                    item.setClassCodeSystem(subclassCodeId);

                    // item memo に薬品名
                    memo = (String)tableModel.getValueAt(i, MEDICINE_COLUMN);
                    if ( (memo != null) && (! memo.equals("")) ){
                        item.setMemo(memo);
                    }
                    test.addClaimItem(item);
                }
            }

            // Admin Memo
            memo = adminMemo.getText();
            if (! memo.equals("")) {
                test.setAdminMemo(memo);
            }

            // BundleNumber
            test.setBundleNumber((String)tableModel.getValueAt(NUMBER_ROW, NUMBER_COLUMN));

            //return (Object)test;
            savedStamp.setModel((IInfoModel)test);
            return (Object)savedStamp;
        }

        public void setValue(Object theModel) {

            if (theModel == null) {
                return;
            }
            
            savedStamp = (Module)theModel;
            
            // Stamp 名と表示形式を設定する
            String stampName = savedStamp.getModuleInfo().getName();
            boolean serialized = savedStamp.getModuleInfo().isSerialized();
            if (!serialized && stampName.startsWith("エディタから")) {
                stampName = "新規スタンプ";
            }
            stampNameField.setText(stampName);
            
            //boolean b = savedStamp.getStampInfo().getTurnIn();
			boolean b = false;
            if (b) {
                turnInRadio.setSelected(true);

            } else {
                expand.setSelected(true);
            }
            
            // Model 表示
            BundleDolphin test = (BundleDolphin)savedStamp.getModel();
            if (test == null) {
                return;
            }

            ClaimItem[] items = test.getClaimItem();
            int count = items.length;
            ClaimItem item;
            MasterItem mItem;
            FlexibleTableModel tableModel = (FlexibleTableModel)medTable.getModel();

            for (int i = 0; i < count; i++) {
                item = items[i];
                mItem = new MasterItem();
                subclassCode = item.getClassCode();
                mItem.classCode = Integer.parseInt(subclassCode);
                
                // Code, Name, TableId
                mItem.name = item.getName();
                mItem.code = item.getCode();
                //mItem.masterTableId = item.getTableId();
                tableModel.setValueAt(mItem, i, NAME_COLUMN);

                // item memo = 薬品
                if (item.getMemo() != null) {
                    tableModel.setValueAt(item.getMemo(), i, MEDICINE_COLUMN);  
                }
            }

            // AdminMemo
            String memo = test.getAdminMemo();
            if (memo != null) {
                adminMemo.setText(memo);
            }

            // Bundle number
            tableModel.setValueAt(test.getBundleNumber(), NUMBER_ROW, NUMBER_COLUMN);      // ComboBox !

            // Notify
            itemCount = count;
            notifyCount();           
        } 

        private void checkValidModel() {
            boolean mmlOk = itemCount > 0 ? true : false;
            parent.setValidModel(mmlOk);
        }

       /**
         * Returns Claim004 Number Code
         * 21 材料個数　　when subclassCode = 1
         * 11 薬剤投与量（１回）when subclassCode = 2
         */
        private String getNumberCode(int subclassCode) {
            return (subclassCode == 1) ? "21" : "11";   // 材料個数 : 薬剤投与量１回
        }

        private ImageIcon createImageIcon(String name) {
            String res = RESOURCE_BASE + name;
            return new ImageIcon(this.getClass().getResource(res));
        }
    }    
}