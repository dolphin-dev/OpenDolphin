/*
 * ItemTable.java
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

import open.dolphin.client.*;
import open.dolphin.infomodel.*;
import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.im.InputSubset;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ItemTablePanel extends JPanel 
implements PropertyChangeListener, DropTargetListener, DragSourceListener, DragGestureListener {
        
    // セットテーブルのカラム名
    private static final String[] COLUMN_NAMES = {
       "名 称", "一回量", "単 位"
    };
    
    // セットテーブルのカラム幅
    private static final int[] COLUMN_WIDTH = {
        300, 50, 50
    };
    
    // セットテーブルの開始行数
    private static final int NUM_ROWS = 9;
    
    // 数量コンボ用のデータ
    private static String[] NUMBER_LIST = null;
    static {
        NUMBER_LIST = new String[31];
        for (int i = 0; i < 31; i++) {
            NUMBER_LIST[i] = String.valueOf(i+ 1);
        }
    }
    
    // 数量カラムのインデックス
    private static int NUMBER_COLUMN = 1;
    
    // CLAIM 関係
    private boolean findClaimClassCode;     // 診療行為区分を診療行為アイテムから取得するとき true
    private String orderName;               // ドルフィンのオーダ履歴用の名前
    private String classCode;               // 診療行為区分
    private String classCodeId;             // 診療行為区分定義のテーブルID == Claom007
    private String classCodeName;           // Claim007 テーブルの診療行為名称
    private String subclassCodeId;          // == Claim003
    private String entityName;              // 実体名
    
    private Module savedStamp;
    
    // GUI コンポーネント
    private JTable setTable;
    private ItemTableModel tableModel;
    private JTextField stampNameField;
    private JRadioButton expand;
    private JRadioButton turnInRadio;
    private JTextField commentField;
    private JComboBox numberCombo;
    private JButton removeButton;
    private JButton clearButton;
    private StampModelEditor parent;
    //private static final String RESOURCE_BASE       = "/open/dolphin/resources/images/";
    private static final String REMOVE_BUTTON_IMAGE = "Delete24.gif";
    private static final String CLEAR_BUTTON_IMAGE  = "New24.gif";
    
    // DnD サポート
    private DragSource dragSource;
    private DropTarget dropTarget;
        
    /** Creates new ItemTable */
    public ItemTablePanel() {
        
        super(new BorderLayout());
                
        // セットテーブルを生成する
        tableModel = new ItemTableModel(COLUMN_NAMES, NUM_ROWS, NUMBER_COLUMN) {
            
            public void setValueAt(Object o, int row, int col) {
                super.setValueAt(o, row, col);
                
                if (col == 1) {
                    checkValidModel();
                }
            }
        };
                    
        setTable = new JTable(tableModel);        
        setTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);     // 選択モード
        setTable.setRowSelectionAllowed(true);                              // 行選択
        setTable.setSurrendersFocusOnKeystroke(true);

        // 行が選択された場合の処理を登録する
        ListSelectionModel m = setTable.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    notifySelectedRow();
                }
            }
        });
        
        // テーブルに DnD 機能を追加する
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(setTable, DnDConstants.ACTION_COPY_OR_MOVE, this);
        dropTarget = new DropTarget(setTable, this);

        // カラム幅を設定する
        TableColumn column = null;
        int widthSize = 400;
        if (COLUMN_WIDTH != null) {
            int len = COLUMN_WIDTH.length;
            widthSize = 0;
            for (int i = 0; i < len; i++) {
                column = setTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(COLUMN_WIDTH[i]);
                widthSize += COLUMN_WIDTH[i];
            }
        }

        // 数量カラムに DocumentListener を設定する
        //DocumentListener dl = new DocumentListener() {

            //public void changedUpdate(DocumentEvent e) {
            //}

            //public void insertUpdate(DocumentEvent e) {
                //checkValidModel();
            //}

            //public void removeUpdate(DocumentEvent e) {
                //checkValidModel();
            //}
        //};
        
        // 数量カラムにセルエディタを設定する
        //JFormattedTextField tf = new JFormattedTextField(new DecimalFormat("####"));
        JTextField tf = new JTextField();
        column = setTable.getColumnModel().getColumn(NUMBER_COLUMN);
        column.setCellEditor (new NumberCellEditor(tf));
        //tf.getDocument().addDocumentListener(dl);

        // テーブル下のコマンドボタン等
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
        
        // コメントフィールド
        p.add(new JLabel("検査コメント"));
        p.add(Box.createRigidArea(new Dimension(5,0)));
        commentField = new JTextField();
        commentField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
               commentField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
            public void focusLosted(FocusEvent event) {
               commentField.getInputContext().setCharacterSubsets(null);
            }
        });
        dim = new Dimension(150,21);
        commentField.setPreferredSize(dim);
        commentField.setMaximumSize(dim);
        p.add(commentField);

        // 数量コンボボックス
        p.add(Box.createRigidArea(new Dimension(7,0)));
        p.add(new JLabel("数 量"));
        p.add(Box.createRigidArea(new Dimension(5,0)));
        numberCombo = new JComboBox(NUMBER_LIST);
        dim = new Dimension(50,21);
        numberCombo.setPreferredSize(dim);
        numberCombo.setMaximumSize(dim);
        p.add(numberCombo);
        
        // Add horizontal glue
        p.add(Box.createHorizontalGlue()); 
        
        // 削除ボタン
        removeButton = new JButton(createImageIcon(REMOVE_BUTTON_IMAGE));
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });
        p.add(removeButton);

        p.add(Box.createRigidArea(new Dimension(5, 0)));

        // クリアボタン
        clearButton = new JButton(createImageIcon(CLEAR_BUTTON_IMAGE));
        clearButton.setEnabled(false);
        clearButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        p.add(clearButton);    
        p.add(Box.createHorizontalStrut(5));
        
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // スクローラ
        JScrollPane scroller = new JScrollPane(setTable);
        scroller.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(new Dimension(widthSize, NUM_ROWS*18));
        
        this.add(scroller, BorderLayout.CENTER);
        this.add(p, BorderLayout.SOUTH);
    }
    
    public void setOrderName(String val) {
        orderName = val;
    }
    
    /*public void setTurnIn(boolean b) {
        turnIn = b;
    }*/
    
    public void setClassCode(String val) {
        classCode = val;
    }
    
    public void setClassCodeId(String val) {
        classCodeId = val;
    }
    
    public void setClassCodeName(String val) {
        classCodeName = val;
    }
    
    public void setSubClassCodeId(String val) {
        subclassCodeId = val;
    }
    
	public void setEntityName(String val) {
		entityName = val;
	}    
    
    public void setParent(StampModelEditor parent) {
        this.parent = parent;
    }
    
    public void setBundleNumber(String val) {
        numberCombo.setSelectedItem(val);
    }
    
    public void setFindClaimClassCode(boolean b) {
        findClaimClassCode = true;
    }
    
    public Object getValue() {
        
        // Return stamp
        String text = stampNameField.getText().trim();
        if (! text.equals("")) {
            savedStamp.getModuleInfo().setName(text);
        }
        //savedStamp.getStampInfo().setTurnIn(turnInRadio.isSelected());

        // Creates a new BundleDolphin
        BundleDolphin bundle = new BundleDolphin();
        
        bundle.setOrderName(orderName);		// StampHolder で使用
        //bundle.setEntityName(entityName);  	// この名前がオーダ履歴のキーになる
        
        Object[] items = tableModel.getItems();
        
        if (items != null) {
            
            int len = items.length;
            boolean found = false;
            
            for(int i = 0; i < len; i++) {
                
                MasterItem mItem = (MasterItem)items[i];
                ClaimItem item = new ClaimItem();
                
                // Set name, code, masterTableId
                item.setName(mItem.name);                    // 名称
                item.setCode(mItem.code);                    // コード
                //item.setCodeSystem(mItem.masterTableId);     // テーブルID                

                // 診療種別区分(手技/材料・薬剤の別) mItem が保持
                String subclassCode = String.valueOf(mItem.classCode);
                item.setClassCode(subclassCode); 
                item.setClassCodeSystem(subclassCodeId); // == Claom003
                
                // 診療行為コードを取得する
                // 最初に見つかった手技の診療行為コードをCLAIMに設定する
                // Dolphin Project の決定事項
                if (findClaimClassCode && (mItem.classCode == 0) && (! found) ) {
                    if (mItem.claimClassCode != null) {
                        
                        // 注射の場合、点数集計先コードから新たに診療行為コードを生成する
                        // Kirishima ver. より
                        if (mItem.claimClassCode.equals("311")) {
                            classCode = "310";
                        }
                        else if (mItem.claimClassCode.equals("321")) {
                            classCode = "320";
                        }
                        else if (mItem.claimClassCode.equals("331")) {
                            classCode = "330";
                        }
                        else {  
                            // 注射以外のケース
                            classCode = mItem.claimClassCode;
                        }
                        found = true;
                    }
                }
                
                // 材料もしくは薬剤の時、数量と単位を取得する
                if (mItem.classCode != 0) {
                    
                    String number = mItem.number;
                    if (number != null) {
                        number = number.trim();
                        if (! number.equals("")) {
                            
                            item.setNumber(number);
                            item.setUnit(mItem.unit);
                            item.setNumberCode(getNumberCode(mItem.classCode));
                            item.setNumberCodeSystem("Claim004");
                        }
                    }
                }
                bundle.addClaimItem(item);
            }
        }
        
        // バンドルメモ
        String memo = commentField.getText();
        if (! memo.equals("")) {
            bundle.setMemo(memo);
        }

        // バンドル数
        bundle.setBundleNumber((String)numberCombo.getSelectedItem());
        
        // ClaimClassCode found or saved value
        bundle.setClassCode(classCode);
        bundle.setClassCodeSystem(classCodeId);
        bundle.setClassName(MMLTable.getClaimClassCodeName(classCode));

        //return (Object)bundle;
        savedStamp.setModel((InfoModel)bundle);
        return (Object)savedStamp;
    }
    
    public void setValue(Object theStamp) {

        if (theStamp == null) {
            return;
        }
        
        // 引数で渡された Stamp を保存する
        savedStamp = (Module)theStamp;
        
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
        open.dolphin.infomodel.BundleDolphin bundle = (open.dolphin.infomodel.BundleDolphin)savedStamp.getModel();
        if (bundle == null) {
            return;
        }

        // 診療行為区分を保存
        classCode = bundle.getClassCode();
        
        ClaimItem[] items = bundle.getClaimItem();
        int count = items.length;
        
        for (int i = 0; i < count; i++) {
            
            ClaimItem item = items[i];
            MasterItem mItem = new MasterItem();
            
            // 手技・材料・薬品のフラグ
            String val = item.getClassCode();
            mItem.classCode = Integer.parseInt(val);
            ////System.out.println("subclassCode = " + mItem.classCode);
            
            // Name Code TableId
            mItem.name = item.getName();
            mItem.code = item.getCode();
            ////mItem.masterTableId = item.getTableId();

            // 材料もしくは薬剤の場合
            // 数量と単位を取得する
            if (mItem.classCode != 0) {
                val = item.getNumber();
                if (val != null) {
                    mItem.number = val;
                    val = item.getUnit();
                    if (val != null) {
                        mItem.unit = val;
                    }
                }
            }
            
            // Show item
            tableModel.addItem(mItem);
        }

        // Bundle Memo
        String memo = bundle.getMemo();
        if (memo != null) {
            commentField.setText(memo);
        }

        String number = bundle.getBundleNumber();
        numberCombo.setSelectedItem(number);
        
        // Notify
        notifyCount();           
    }
    
    public void propertyChange(PropertyChangeEvent e) {

        String prop = e.getPropertyName();

        if (prop.equals("selectedItemProp")) {           
            MasterItem item = (MasterItem)e.getNewValue();
            if (item.classCode != 0) {
                item.number = "1";
            }
            tableModel.addItem(item);
            notifyCount();
            notifySelectedRow();
        }
        else if (prop.equals(RadiologyMethod.RADIOLOGY_MEYTHOD_PROP)) {                
            String text = (String)e.getNewValue();
            commentField.setText(text);
        }
    }

    private void notifyCount() {
        boolean b = (tableModel.getDataSize() > 0) ? true : false;
        clearButton.setEnabled(b);
        checkValidModel();
    }

    private void notifySelectedRow() {
        int index = setTable.getSelectedRow();
        boolean b = isValidRow(index);
        removeButton.setEnabled(b);
    }    

    /**
     *  Clear all items.
     */
    private void clear() {
        tableModel.clear();
        notifyCount();
        notifySelectedRow();
    }

    /**
     * Clear selected item row.
     */
    private void removeSelectedItem() {
        int row = setTable.getSelectedRow();
        
        // No need to check the range
        tableModel.deleteRow(row);
        notifyCount();
        notifySelectedRow();
    } 
        
    private void checkValidModel() {
        boolean mmlOk = (isCountOk() && isNumberOk()) ? true : false;
        parent.setValidModel(mmlOk);
    }

    private boolean isCountOk() {
        return tableModel.getDataSize() > 0 ? true : false;
    }

    private boolean isNumberOk() {
        return tableModel.isNumberOk();
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
        //String res = RESOURCE_BASE + name;
        //return new ImageIcon(this.getClass().getResource(res));
        return ClientContext.getImageIcon(name);
    }   
    
    ///////////////////////////////////////////////////////////////////////////
    
    public void dragGestureRecognized(DragGestureEvent event) {
        
        try {
            int row = setTable.getSelectedRow();
            if ( isValidRow(row) ) {                
                Transferable t = new IntegerTransferable(new Integer(row));
                dragSource.startDrag(event, DragSource.DefaultCopyDrop, t, this);
            }
        }
        catch (Exception e) {
            System.out.println("Exception at dragGestureRecognized: " + e.toString());
        }
    }

    public void dragDropEnd(DragSourceDropEvent event) { 
    }

    public void dragEnter(DragSourceDragEvent event) {
    }

    public void dragExit(DragSourceEvent event) {
    }

    public void dragOver(DragSourceDragEvent event) {
    }

    public void dropActionChanged ( DragSourceDragEvent event) {
    }
   
    ///////////////////////////////////////////////////////////////////////////
    private void setDropTargetBorder(final boolean b) {
        Color c = b ? DesignFactory.getDropOkColor() : this.getBackground();
        setTable.setBorder(BorderFactory.createLineBorder(c));
    }
    
    public boolean isDragAcceptable(DropTargetDragEvent evt) {
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }
    
    public boolean isDropAcceptable(DropTargetDropEvent evt) {
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }    
    
    public void dragEnter(DropTargetDragEvent evt) {
        //evt.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        if (! isDragAcceptable(evt)) {
            evt.rejectDrag();
        }
    }
   
    public void dragOver(DropTargetDragEvent evt) {
        if (isDragAcceptable(evt)) {
            setDropTargetBorder(true);
        }
    }
   
    public void dragExit(DropTargetEvent evt) {
        setDropTargetBorder(true);
    }
   
    public void dropActionChanged(DropTargetDragEvent evt) {
        if (! isDragAcceptable(evt)) {
            evt.rejectDrag();
        }
    }
   
    public void drop(DropTargetDropEvent event) {
        
        if (! isDropAcceptable(event)) {
            event.rejectDrop();
            setDropTargetBorder(false);
        }
        
        event.acceptDrop(DnDConstants.ACTION_MOVE);

        Transferable tr = event.getTransferable();

        // Drop 位置を得る
        Point loc = event.getLocation();

        // CopyAction かどうか
        //int action = event.getDropAction();
        //boolean copyAction = (action == DnDConstants.ACTION_COPY);

        // Table へ Drop する
        boolean ok = doDrop(tr, loc);

        /*if (ok) {
            // 成功した場合
            if (copyAction) 
                event.acceptDrop(DnDConstants.ACTION_COPY);
            else 
                event.acceptDrop(DnDConstants.ACTION_MOVE);
        }
        else {
            // 失敗した場合
            event.rejectDrop();
        }*/
        // Drop 完了
        event.getDropTargetContext().dropComplete(true);
        setDropTargetBorder(false);
    }   
    
    /**
     * Handle drop.
     */ 
    private boolean doDrop(Transferable tr, Point loc) {
             
        // サポートしている　Data Flavor か
        if (! tr.isDataFlavorSupported(IntegerTransferable.intFlavor)) {
            return false;
        }
        
        // PointToCell
        int toRow = setTable.rowAtPoint(loc);
        //int toCol = setTable.columnAtPoint(loc);
        toRow = getValidRow(toRow);
        boolean ret = false;
        
        try {
            Integer integer = (Integer)tr.getTransferData(IntegerTransferable.intFlavor);
            tableModel.moveRow(integer.intValue(), toRow);
            setTable.getSelectionModel().addSelectionInterval(toRow, toRow);
            ret = true;
        }        
        catch (IOException io) { 
            System.out.println (io);
        } 
        catch (UnsupportedFlavorException ufe) {
            System.out.println (ufe);
        }
        return ret;
    }   
    
    private boolean isValidRow(int row) {
        return (row > -1 && row < tableModel.getDataSize()) ? true : false;
    }
    
    private int getValidRow(int row) {
        int start = 0;
        int end = tableModel.getDataSize() - 1;
        if (row < start) {
            row = start;
        }
        else if ( row > end ) {
            row = end;
        }
        return row;
    }
}