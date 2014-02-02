package open.dolphin.order;

import open.dolphin.table.OddEvenRowRenderer;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import open.dolphin.client.*;
import open.dolphin.client.GUIConst;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.table.ObjectReflectTableModel;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Iterator;
import open.dolphin.project.Project;
import open.dolphin.util.ZenkakuUtils;

/**
 * ItemTablePanel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ItemTablePanel extends JPanel implements PropertyChangeListener {
    
    protected static final String DEFAULT_STAMP_NAME = "新規スタンプ";
    private static final String FROM_EDITOR_STAMP_NAME = "エディタから";
    private static final String DEFAULT_NUMBER = "1";
    private static final String[] COLUMN_NAMES = { "コード", "診療内容", "数 量", "単 位" };
    private static final String[] METHOD_NAMES = { "getCode", "getName", "getNumber", "getUnit" };
    private static final int[] COLUMN_WIDTH = {50, 200, 10, 10};
    private static final int NUM_ROWS = 14;
    private static final String REMOVE_BUTTON_IMAGE = "del_16.gif";
    private static final String CLEAR_BUTTON_IMAGE = "remov_16.gif";
    private static final String NUMBER_LABEL_TEXT = "回 数";
    private static final String SET_NAME_LABEL_TEXT = "セット名";
    private static final String MEMO_LABEL_TEXT = "メ モ";
    private static final String TOOLTIP_DELETE = "選択したアイテムを削除します。";
    private static final String TOOLTIP_CLEAR = "セット内容をクリアします。";
    private static final String TOOLTIP_DND = "ドラッグ & ドロップで順番を入れ替えることができます。";
    
    // 数量コンボ用のデータを生成する
    private static String[] NUMBER_LIST = null;
    static {
        NUMBER_LIST = new String[31];
        for (int i = 0; i < 31; i++) {
            NUMBER_LIST[i] = String.valueOf(i + 1);
        }
    }
    
    // 数量カラムのインデックス
    private static final int NUMBER_COLUMN = 2;
    
    // CLAIM 関係
    private boolean findClaimClassCode; // 診療行為区分を診療行為アイテムから取得するとき true
    private String orderName;           // ドルフィンのオーダ履歴用の名前
    private String classCode;           // 診療行為区分 400,500,600 .. etc
    private String classCodeId;         // 診療行為区分定義のテーブルID == Claim007
    private String subclassCodeId;      // == Claim003
    private String entity;              // 
    
    // GUI コンポーネント
    private JTable setTable;
    private ObjectReflectTableModel tableModel;
    private JTextField stampNameField;
    private JTextField commentField;
    private JComboBox numberCombo;
    private JButton removeButton;
    private JButton clearButton;
    
    private IStampModelEditor parent;
    private boolean validModel;
    private SetTableStateMgr stateMgr;
    
    
    /**
     * Creates new ItemTable
     */
    public ItemTablePanel(IStampModelEditor parent) {
        
        super(new BorderLayout(0, 5));
        
        setMyParent(parent);
        
        // セットテーブルのモデルを生成する
        tableModel = new ObjectReflectTableModel(COLUMN_NAMES, NUM_ROWS, METHOD_NAMES, null) {
            
            // NUMBER_COLUMN を編集可能にする
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == NUMBER_COLUMN ? true : false;
            }
            
            // NUMBER_COLUMN に値を設定する
            @Override
            public void setValueAt(Object o, int row, int col) {
                
                if (o == null || ((String) o).trim().equals("")) {
                    return;
                }
                
                // MasterItem に数量を設定する
                MasterItem mItem = (MasterItem) getObject(row);
                
                if (col == NUMBER_COLUMN && mItem != null) {
                    mItem.setNumber((String) o);
                    stateMgr.checkState();
                }
            }
        };
        
        // セットテーブルを生成する
        setTable = new JTable(tableModel);
        setTable.setTransferHandler(new MasterItemTransferHandler()); // TransferHandler
        setTable.addMouseMotionListener(new MouseMotionListener() {
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
        setTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 選択モード
        setTable.setRowSelectionAllowed(true);
        ListSelectionModel m = setTable.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    notifySelectedRow();
                }
            }
        });
        setTable.setToolTipText(TOOLTIP_DND);
        setTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        
        // カラム幅を設定する
        TableColumn column = null;
        if (COLUMN_WIDTH != null) {
            int len = COLUMN_WIDTH.length;
            for (int i = 0; i < len; i++) {
                column = setTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(COLUMN_WIDTH[i]);
            }
        }
        
        // 数量カラムにセルエディタを設定する
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        column = setTable.getColumnModel().getColumn(NUMBER_COLUMN);
        DefaultCellEditor de = new DefaultCellEditor(tf);
        int ccts = Project.getPreferences().getInt("order.table.clickCountToStart", 1);
        de.setClickCountToStart(ccts);
        column.setCellEditor(de);
        
        // 数量コンボを設定する
        numberCombo = new JComboBox(NUMBER_LIST);
        
        // コメントフィールドを生成する
        commentField = new JTextField(15);
        commentField.addFocusListener(AutoKanjiListener.getInstance());
        
        // スタンプ名フィールドを生成する
        stampNameField = new JTextField(15);
        stampNameField.setOpaque(true);
        stampNameField.setBackground(new Color(251, 239, 128));  // TODO
        stampNameField.addFocusListener(AutoKanjiListener.getInstance());
        
        // 削除ボタンを生成する
        removeButton = new JButton(createImageIcon(REMOVE_BUTTON_IMAGE));
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });
        removeButton.setToolTipText(TOOLTIP_DELETE);
        
        // クリアボタンを生成する
        clearButton = new JButton(createImageIcon(CLEAR_BUTTON_IMAGE));
        clearButton.setEnabled(false);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        clearButton.setToolTipText(TOOLTIP_CLEAR);
        
        // セット名、数量、コメント
        JPanel infoP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        infoP.add(new JLabel(SET_NAME_LABEL_TEXT));
        infoP.add(stampNameField);
        
        infoP.add(new JLabel(NUMBER_LABEL_TEXT));
        infoP.add(numberCombo);
        
        infoP.add(new JLabel(MEMO_LABEL_TEXT));
        infoP.add(commentField);
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(removeButton);
        bp.add(clearButton);
        if (parent.getContext().getOkButton() != null) {
            bp.add(parent.getContext().getOkButton());
        }
        
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        south.add(infoP);
        south.add(Box.createHorizontalGlue());
        south.add(bp);
        
        // スクローラ
        JScrollPane scroller = new JScrollPane(setTable);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scroller.setPreferredSize(new Dimension(GUIConst.DEFAULT_EDITOR_WIDTH, GUIConst.DEFAULT_EDITOR_HEIGHT));
        
        // 全体を配置する
        this.add(scroller, BorderLayout.CENTER);
        this.add(south, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(GUIConst.DEFAULT_EDITOR_WIDTH, GUIConst.DEFAULT_EDITOR_HEIGHT));
        
        // StateMgrを生成する
        stateMgr = new SetTableStateMgr(this, setTable, removeButton, clearButton, stampNameField);
    }
    
    public boolean isValidModel() {
        return validModel;
    }
    
    public void setValidModel(boolean valid) {
        validModel = valid;
        getMyParent().setValidModel(validModel);
    }
    
    public String getOrderName() {
        return orderName;
    }
    
    public void setOrderName(String val) {
        orderName = val;
    }
    
    public String getEntity() {
        return entity;
    }
    
    public void setEntity(String val) {
        entity = val;
    }
    
    public String getClassCode() {
        return classCode;
    }
    
    public void setClassCode(String val) {
        classCode = val;
    }
    
    public String getClassCodeId() {
        return classCodeId;
    }
    
    public void setClassCodeId(String val) {
        classCodeId = val;
    }
    
    public String getSubClassCodeId() {
        return subclassCodeId;
    }
    
    public void setSubClassCodeId(String val) {
        subclassCodeId = val;
    }
    
    public IStampModelEditor getMyParent() {
        return parent;
    }
    
    public void setMyParent(IStampModelEditor parent) {
        this.parent = parent;
    }
    
    public String getBundleNumber() {
        return (String)numberCombo.getSelectedItem();
    }
    
    public void setBundleNumber(String val) {
        numberCombo.setSelectedItem(val);
    }
    
    public boolean isFindClaimClassCode() {
        return findClaimClassCode;
    }
    
    public void setFindClaimClassCode(boolean b) {
        findClaimClassCode = true;
    }
    
    /**
     * エディタで編集したスタンプの値を返す。
     * @return スタンプ(ModuleMode = ModuleInfo + InfoModel)
     */
    public Object getValue() {
        
        // 常に新規のモデルとして返す
        ModuleModel retModel = new ModuleModel();
        ModuleInfoBean moduleInfo = retModel.getModuleInfo();
        moduleInfo.setEntity(getEntity());
        moduleInfo.setStampRole(IInfoModel.ROLE_P);
        
        // スタンプ名を設定する
        String text = stampNameField.getText().trim();
        if (!text.equals("")) {
            moduleInfo.setStampName(text);
        } else {
            moduleInfo.setStampName(DEFAULT_STAMP_NAME);
        }
        
        // BundleDolphin を生成する
        BundleDolphin bundle = new BundleDolphin();
        
        // Dolphin Appli で使用するオーダ名称を設定する
        // StampHolder で使用される（タブ名に相当）
        bundle.setOrderName(getOrderName());
        
        // セットテーブルのマスターアイテムを取得する
        java.util.List itemList = tableModel.getObjectList();
        
        if (itemList != null) {
            
            // 診療行為があるかどうかのフラグ
            boolean found = false;
            
            for (Iterator iter = itemList.iterator(); iter.hasNext(); ) {
                
                MasterItem mItem = (MasterItem) iter.next();
                ClaimItem item = new ClaimItem();
                
                // 名称、コードを設定する
                item.setName(mItem.getName()); // 名称
                item.setCode(mItem.getCode()); // コード
                
                // 診療種別区分(手技/材料・薬剤の別) mItem が保持を設定する
                String subclassCode = String.valueOf(mItem.getClassCode());
                item.setClassCode(subclassCode);
                item.setClassCodeSystem(subclassCodeId); // == Claom003
                
                // 診療行為コードを取得する
                // 最初に見つかった手技の診療行為コードをCLAIMに設定する
                // Dolphin Project の決定事項
                if (isFindClaimClassCode() && (mItem.getClassCode() == ClaimConst.SYUGI) && (!found)) {
                    
                    if (mItem.getClaimClassCode() != null) {
                        
                        // 注射の場合、点数集計先コードから新たに診療行為コードを生成する
                        // Kirishima ver. より
                        if (mItem.getClaimClassCode().equals(ClaimConst.INJECTION_311)) {
                            classCode = ClaimConst.INJECTION_310;
                        } else if (mItem.getClaimClassCode().equals(ClaimConst.INJECTION_321)) {
                            classCode = ClaimConst.INJECTION_320;
                        } else if (mItem.getClaimClassCode().equals(ClaimConst.INJECTION_331)) {
                            classCode = ClaimConst.INJECTION_330;
                        } else {
                            // 注射以外のケース
                            classCode = mItem.getClaimClassCode();
                        }
                        found = true;
                    }
                }
                                    
                String number = mItem.getNumber();
                if (number != null) {
                    number = number.trim();
                    if (!number.equals("")) {
                        number = ZenkakuUtils.toHankuNumber(number);
                        item.setNumber(number);
                        item.setUnit(mItem.getUnit());
                        item.setNumberCode(getNumberCode(mItem.getClassCode()));
                        item.setNumberCodeSystem(ClaimConst.NUMBER_CODE_ID);
                    }
                }
                bundle.addClaimItem(item);
            }
        }
        
        // バンドルメモ
        String memo = commentField.getText();
        if (!memo.equals("")) {
            bundle.setMemo(memo);
        }
        
        // バンドル数
        bundle.setBundleNumber((String) numberCombo.getSelectedItem());
        
        // 診療行為コード
        bundle.setClassCode(classCode);
        bundle.setClassCodeSystem(classCodeId); // Claim007 固定の値
        bundle.setClassName(MMLTable.getClaimClassCodeName(classCode)); // 上記テーブルで定義されている診療行為の名称
        
        // 
        retModel.setModel((InfoModel) bundle);
        
        return (Object) retModel;
    }
    
    /**
     * 編集するスタンプの内容を表示する。
     * @param theStamp 編集するスタンプ、戻り値は常に新規スタンプである。
     */
    public void setValue(Object theStamp) {
        
        // 連続して編集される場合があるのでテーブル内容等をクリアする
        clear();
        
        // null であればリターンする
        if (theStamp == null) {
            // Stateを変更する
            stateMgr.checkState();
            return;
        }
        
        // 引数で渡された Stamp をキャストする
        ModuleModel target  = (ModuleModel) theStamp;
        
        // Entityを保存する
        setEntity(target.getModuleInfo().getEntity());
        
        // Stamp 名と表示形式を設定する
        String stampName = target.getModuleInfo().getStampName();
        boolean serialized = target.getModuleInfo().isSerialized();
        
        // スタンプ名がエディタから発行の場合はデフォルトの名称にする
        if (!serialized && stampName.startsWith(FROM_EDITOR_STAMP_NAME)) {
            stampName = DEFAULT_STAMP_NAME;
        } else if (stampName.equals("")) {
            stampName = DEFAULT_STAMP_NAME;
        }
        stampNameField.setText(stampName);
        
        // Model を表示する
        BundleDolphin bundle = (BundleDolphin) target.getModel();
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
            mItem.setClassCode(Integer.parseInt(val));
            
            // Name Code TableId
            mItem.setName(item.getName());
            mItem.setCode(item.getCode());
            
            val = item.getNumber();
            if (val != null && (!val.equals(""))) {
                val = ZenkakuUtils.toHankuNumber(val.trim());
                mItem.setNumber(val);
                val = item.getUnit();
                if (val != null) {
                    mItem.setUnit(val);
                }
            }
            
            // Show item
            tableModel.addRow(mItem);
        }
        
        // Bundle Memo
        String memo = bundle.getMemo();
        if (memo != null) {
            commentField.setText(memo);
        }
        
        String number = bundle.getBundleNumber();
        if (number != null && (!number.equals(""))) {
            number = ZenkakuUtils.toHankuNumber(number);
            numberCombo.setSelectedItem(number);
        }
        
        // Stateを変更する
        stateMgr.checkState();
    }
    
    /**
     * マスターテーブルで選択されたアイテムの通知を受け、
     * セットテーブルへ追加する。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals("selectedItemProp")) {
            
            MasterItem item = (MasterItem) e.getNewValue();
            String textVal = stampNameField.getText().trim();
            
            // マスターアイテムを判別して自動設定を行う
            if (item.getClassCode() == ClaimConst.SYUGI) {
                // 材料及び薬剤の場合は数量1を設定する
                //item.setNumber(DEFAULT_NUMBER);
                if (textVal.equals("") || textVal.equals(DEFAULT_STAMP_NAME)) {
                    // 手技の場合はスタンプ名フィールドに名前を設定する
                    stampNameField.setText(item.getName());
                }
                
            } else if (item.getClassCode() == ClaimConst.YAKUZAI) {
                String inputNum = "1";
                if (item.getUnit()!= null) {
                    String unit = item.getUnit();
                    if (unit.equals("錠")) {
                        inputNum = Project.getPreferences().get("defaultZyozaiNum", "3");
                    } else if (unit.equals("ｇ")) {
                        inputNum = Project.getPreferences().get("defaultSanyakuNum", "1.0");
                    } else if (unit.equals("ｍＬ")) {
                        inputNum = Project.getPreferences().get("defaultMizuyakuNum", "1");
                    }
                } 
                item.setNumber(inputNum);
                
            } else if (item.getClassCode() == ClaimConst.ZAIRYO) {
                item.setNumber(DEFAULT_NUMBER);
            }
            
            tableModel.addRow(item);
            stateMgr.checkState();
            
        } else if (prop.equals(RadiologyMethod.RADIOLOGY_MEYTHOD_PROP)) {
            String text = (String) e.getNewValue();
            commentField.setText(text);
        }
    }
    
    private void notifySelectedRow() {
        int index = setTable.getSelectedRow();
        boolean b = tableModel.getObject(index) != null ? true : false;
        removeButton.setEnabled(b);
    }
    
    /**
     * Clear all items.
     */
    public void clear() {
        tableModel.clear();
        stateMgr.checkState();
    }
    
    /**
     * Clear selected item row.
     */
    private void removeSelectedItem() {
        int row = setTable.getSelectedRow();
        if (tableModel.getObject(row) != null) {
            tableModel.deleteRow(row);
            stateMgr.checkState();
        }
    }
    
    /**
     * Returns Claim004 Number Code 21 材料個数 when subclassCode = 1 11
     * 薬剤投与量（１回）when subclassCode = 2
     */
    private String getNumberCode(int subclassCode) {
        return (subclassCode == 1) ? ClaimConst.ZAIRYO_KOSU : ClaimConst.YAKUZAI_TOYORYO_1KAI; // 材料個数 : 薬剤投与量１回
    }
    
    private ImageIcon createImageIcon(String name) {
        return ClientContext.getImageIcon(name);
    }
}