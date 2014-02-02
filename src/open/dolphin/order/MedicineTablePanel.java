package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import open.dolphin.table.OddEvenRowRenderer;

import open.dolphin.table.ObjectReflectTableModel;


import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.StampModelEditor;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.project.Project;
import open.dolphin.util.ZenkakuUtils;


/**
 * Medicine Set Table.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class MedicineTablePanel extends JPanel implements PropertyChangeListener {
    
    private static final long serialVersionUID = 8361225970230987080L;
    
    protected static final String DEFAULT_STAMP_NAME = "新規スタンプ";
    private static final String FROM_EDITOR_STAMP_NAME = "エディタから";
    
    private static final String[] COLUMN_NAMES = {"コード", "診療内容", "数量","単位", " ", "回数"};
    private static final String[] METHOD_NAMES = {"getCode", "getName", "getNumber", "getUnit", "getDummy", "getBundleNumber"};
    private static final int[] COLUMN_WIDTH = {50, 210, 20, 20, 10, 20};
    private static final String TOOLTIP_DELETE = "選択したアイテムを削除します。";
    private static final String TOOLTIP_CLEAR = "セット内容をクリアします。";
    private static final String TOOLTIP_DND = "ドラッグ & ドロップで順番を入れ替えることができます。";
    private static final String RESOURCE_BASE       = "/open/dolphin/resources/images/";
    private static final String REMOVE_BUTTON_IMAGE = "del_16.gif";
    private static final String CLEAR_BUTTON_IMAGE  = "remov_16.gif";
    private static final String INFO_BUTTON_IMAGE   = "about_16.gif";
    private static final String LABEL_TEXT_IN_MED = "院内";
    private static final String LABEL_TEXT_OUT_MED = "院外";
    private static final String IN_MEDICINE     = "院内処方";
    private static final String EXT_MEDICINE    = "院外処方";
    private static final String LABEL_TEXT_STAMP_NAME = "スタンプ名";
    private static final String ADMIN_MARK = "[用法] ";
    private static final String REG_ADMIN_MARK = "\\[用法\\] ";
    
    // Table の行数
    private static final int ROWS               = 9;
    
    // 数量カラム番号 
    private static final int ONEDAY_COLUMN      = 2;
    
    // 回数絡む
    private static final int BUNDLE_COLUMN      = 5;
    
    // セットテーブルの TableModel
    private ObjectReflectTableModel medTableModel;
    
    // セットテーブルの JTable
    private JTable medTable;
    
    // スタンプ名フィールド
    private JTextField stampNameField;
    
    // 院内処方 
    private JRadioButton inMed;
    
    // 院外処方 
    private JRadioButton extMed;
    
    // State Label
    private JLabel stateLabel;
    
    // 削除ボタン 
    private JButton removeButton;
    
    // クリアボタン 
    private JButton clearButton;
    
    // StampModelEditor 
    private StampModelEditor parent;
    
    // 有効モデルフラグ 
    private boolean validModel;
    
    // State Manager 
    private MedTableStateMgr stateMgr;
    
    // このエディタの Entity 
    private String entity;
    
    // 再編集の場合に保存しておくレセ電算コード 
    private String saveReceiptCode;
    
    // デフォルト数
    private String defaultBundleNumber = "1";
    private String defaultNumber = "1";
   
    /**
     * Creates new MedicineTable
     */
    public MedicineTablePanel(StampModelEditor parent) {
        
        super(new BorderLayout());
        
        setParent(parent);
        
        // 薬剤セットテーブルを生成する
        medTableModel = new ObjectReflectTableModel(COLUMN_NAMES, ROWS, METHOD_NAMES, null) {
            
            // 数量と回数のみ編集可能
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col == ONEDAY_COLUMN || col == BUNDLE_COLUMN) ? true : false;
            }
            
            @Override
            public void setValueAt(Object o, int row, int col) {
                
                if (o == null || ((String)o).trim().equals("")) {
                    return;
                }
                
                MasterItem mItem = (MasterItem) getObject(row);
                
                if ( col == ONEDAY_COLUMN && mItem != null) {
                    mItem.setNumber((String) o);
                    stateMgr.checkState();
                } else if ( col == BUNDLE_COLUMN && mItem != null) {
                    mItem.setBundleNumber((String) o);
                    stateMgr.checkState();
                }
            }
        };
        
        // Tableを生成する
        medTable = new JTable(medTableModel);
        medTable.setTransferHandler(new MasterItemTransferHandler());
        medTable.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
                    TransferHandler.COPY : TransferHandler.MOVE;
                JComponent c = (JComponent)e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, action);
            }
            public void mouseMoved(MouseEvent e) {}
        });
        medTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medTable.setRowSelectionAllowed(true);
        ListSelectionModel m = medTable.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    notifySelectedRow();
                }
            }
        });
        medTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        medTable.setToolTipText(TOOLTIP_DND);
        
        // 数量カラム
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        TableColumn column = medTable.getColumnModel().getColumn(ONEDAY_COLUMN);
        DefaultCellEditor dce = new DefaultCellEditor(tf);
        int ccts = Project.getPreferences().getInt("order.table.clickCountToStart", 1);
        dce.setClickCountToStart(ccts);
        column.setCellEditor(dce);
        
        // 回数カラム
        JTextField tf2 = new JTextField();
        tf2.addFocusListener(AutoRomanListener.getInstance());
        TableColumn column2 = medTable.getColumnModel().getColumn(BUNDLE_COLUMN);
        DefaultCellEditor dce2 = new DefaultCellEditor(tf2);
        dce2.setClickCountToStart(ccts);
        column2.setCellEditor(dce2);
        
        // 列幅を設定する
        int len = COLUMN_NAMES.length;
        for (int i = 0; i < len; i++) {
            column = medTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(COLUMN_WIDTH[i]);
        }
        
        // StampNameフィールドを生成する
        stampNameField = new JTextField(12);
        stampNameField.setOpaque(true);
        stampNameField.setBackground(new Color(251,239,128));
        stampNameField.addFocusListener(AutoKanjiListener.getInstance());
        
        // 院内・院外処方ラジオボタンを生成する
        inMed = new JRadioButton(LABEL_TEXT_IN_MED);
        extMed = new JRadioButton(LABEL_TEXT_OUT_MED);
        ButtonGroup g = new ButtonGroup();
        g.add(inMed);
        g.add(extMed);
        boolean bOut = Project.getPreferences().getBoolean(Project.RP_OUT, true);
        if (bOut) {
            extMed.setSelected(true);
            
        } else {
            inMed.setSelected(true);
        }
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = extMed.isSelected();
                Project.getPreferences().putBoolean(Project.RP_OUT, b);
            }
        };
        inMed.addActionListener(al);
        extMed.addActionListener(al);
        
        // Info Icon
        JLabel infoIcon = new JLabel(ClientContext.getImageIcon(INFO_BUTTON_IMAGE));
        stateLabel = new JLabel();
        
        // Remove buttonを生成する
        removeButton = new JButton(createImageIcon(REMOVE_BUTTON_IMAGE));
        removeButton.setEnabled(false);
        removeButton.setToolTipText(TOOLTIP_DELETE);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });
        
        // Clear buttonを生成する
        clearButton = new JButton(createImageIcon(CLEAR_BUTTON_IMAGE));
        clearButton.setEnabled(false);
        clearButton.setToolTipText(TOOLTIP_CLEAR);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        
        // スタンプ名、数量、用法、メモ、院内院外パネルを生成する
        JPanel infoP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoP.add(new JLabel(LABEL_TEXT_STAMP_NAME));
        infoP.add(stampNameField);
        infoP.add(inMed);
        infoP.add(extMed);
        infoP.add(infoIcon);
        infoP.add(stateLabel);
        
        // ボタンパネルを生成する
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(removeButton);
        bp.add(clearButton);
        if (parent.getContext().getOkButton() != null) {
            bp.add(parent.getContext().getOkButton());
        }
        
        // 南パネルを生成する
        JPanel southP = new JPanel();
        southP.setLayout(new BoxLayout(southP, BoxLayout.X_AXIS));
        southP.add(infoP);
        southP.add(bp);
        
        // 全体をレイアウトする
        JScrollPane scroller = new JScrollPane(medTable);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller, BorderLayout.CENTER);
        this.add(southP, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(GUIConst.DEFAULT_EDITOR_WIDTH, GUIConst.DEFAULT_EDITOR_HEIGHT+50));
        
        // StateMgrを生成する
        stateMgr = new MedTableStateMgr(this, medTable, removeButton, clearButton, stampNameField, stateLabel);
    }
    
    public StampModelEditor getMyParent() {
        return this.parent;
    }
    
    public void setParent(StampModelEditor parent) {
        this.parent = parent;
    }
    
    public String getEntity() {
        return entity;
    }
    
    public void setEntity(String val) {
        entity = val;
    }
    
    public boolean isValidModel() {
        return validModel;
    }
    
    public void setValidModel(boolean valid) {
        validModel = valid;
        getMyParent().setValidModel(validModel);
    }
    
    /**
     * 医薬品及び用法の通知を受け、データをセットする。
     * @param e PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent e) {
            
        Object newValue = e.getNewValue();

        if (newValue != null && (newValue instanceof MasterItem)) {

            MasterItem item = (MasterItem) newValue;
            
            if (item.getClassCode() == ClaimConst.YAKUZAI) {
                String inputNum = defaultNumber;
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
                medTableModel.addRow(item);
                String name = stampNameField.getText().trim();
                if (name.equals("") || name.equals(DEFAULT_STAMP_NAME)) {
                    stampNameField.setText(item.getName());
                }
                
            } else if (item.getClassCode() == ClaimConst.ZAIRYO) {
                item.setNumber(defaultNumber);
                medTableModel.addRow(item);
                String name = stampNameField.getText().trim();
                if (name.equals("") || name.equals(DEFAULT_STAMP_NAME)) {
                    stampNameField.setText(item.getName());
                }
                
            } else if (item.getClassCode() == ClaimConst.SYUGI) {
                medTableModel.addRow(item);
                String name = stampNameField.getText().trim();
                if (name.equals("") || name.equals(DEFAULT_STAMP_NAME)) {
                    stampNameField.setText(item.getName());
                }
                
            } else if (item.getClassCode() == ClaimConst.ADMIN) {
                item.setName(ADMIN_MARK + item.getName());
                item.setDummy("X");
                item.setBundleNumber(Project.getPreferences().get("defaultRpNum", defaultNumber));
                medTableModel.addRow(item);
            }
            
            stateMgr.checkState();
        }
    }
    
    private void notifySelectedRow() {
        int index = medTable.getSelectedRow();
        boolean b = medTableModel.getObject(index) != null ? true : false;
        removeButton.setEnabled(b);
        //stateMgr.checkState();
    }
    
    private void clear() {
        medTableModel.clear();
        stateMgr.checkState();
    }
    
    private void removeSelectedItem() {
        
        int index = medTable.getSelectedRow();
        if (index < 0) {
            notifySelectedRow();
            return;
        }
        Object o = medTableModel.getObject(index);
        if ( o == null ) {
            notifySelectedRow();
            return;
        }
        
        medTableModel.deleteRow(index);
        stateMgr.checkState();
    }
    
    private ModuleModel createModuleModel() {
        
        ModuleModel retModel = new ModuleModel();
        BundleMed med = new BundleMed();
        retModel.setModel(med);
        
        // StampInfoを設定する
        ModuleInfoBean moduleInfo = retModel.getModuleInfo();
        moduleInfo.setEntity(getEntity());
        moduleInfo.setStampRole(IInfoModel.ROLE_P);
        
        //　スタンプ名を設定する
        String stampName = stampNameField.getText().trim();
        if (!stampName.equals("")) {
            moduleInfo.setStampName(stampName);
        } else {
            moduleInfo.setStampName(DEFAULT_STAMP_NAME);
        }
        
        return retModel;
    }
    
    private ClaimItem createClaimItem(MasterItem mItem) {
        ClaimItem item = new ClaimItem();
        item.setClassCode(String.valueOf(mItem.getClassCode()));
        item.setClassCodeSystem(ClaimConst.SUBCLASS_CODE_ID);
        item.setCode(mItem.getCode());
        item.setName(mItem.getName());
        return item;
    }
    
    private List<ModuleModel> getBundles() {
        
        List<ModuleModel> retList = new ArrayList<ModuleModel>();
        
        List items = medTableModel.getObjectList();
        ModuleModel module = createModuleModel();
        BundleMed bundle = (BundleMed) module.getModel();
        
        for (Iterator iter = items.iterator(); iter.hasNext(); ) {
            
            MasterItem mItem = (MasterItem) iter.next();
            
            if (mItem == null) {
                break;
            }
            
            String number = mItem.getNumber();
            if (number != null && (!number.trim().equals(""))) {
                number = ZenkakuUtils.toHankuNumber(number.trim());
                mItem.setNumber(number);
            } else {
                number = null;
            }
            
            switch(mItem.getClassCode()) {
                
                case ClaimConst.SYUGI:
                    ClaimItem sItem = createClaimItem(mItem);
                    bundle.addClaimItem(sItem);
                    break;
                    
                case ClaimConst.YAKUZAI:
                    ClaimItem yItem = createClaimItem(mItem);
                    bundle.addClaimItem(yItem);
                    if (number != null) {
                        yItem.setNumber(number);
                        yItem.setUnit(mItem.getUnit());
                        //
                        // 数量コード 10/11/12 2007-05 現在のORCAの実装では採用していない
                        //
                        yItem.setNumberCode(ClaimConst.YAKUZAI_TOYORYO);
                        yItem.setNumberCodeSystem(ClaimConst.NUMBER_CODE_ID);
                    }
                    //
                    if (bundle.getClassCode() == null) {
                        String rCode = null;
                        if (mItem.getYkzKbn() != null) {
                            rCode = mItem.getYkzKbn().equals(ClaimConst.YKZ_KBN_NAIYO)
                                        ? ClaimConst.RECEIPT_CODE_NAIYO 
                                        : ClaimConst.RECEIPT_CODE_GAIYO;
                            
                        } else if (saveReceiptCode != null) {
                            rCode = saveReceiptCode;
                            
                        } else {
                            rCode = ClaimConst.RECEIPT_CODE_NAIYO;
                        }
                        bundle.setClassCode(rCode);
                        bundle.setClassCodeSystem(ClaimConst.CLASS_CODE_ID);
                        bundle.setClassName(MMLTable.getClaimClassCodeName(rCode));
                    }
                    break;
                    
                case ClaimConst.ZAIRYO:
                    ClaimItem zItem = createClaimItem(mItem);
                    bundle.addClaimItem(zItem);
                    if (number != null) {
                        zItem.setNumber(number);
                        zItem.setUnit(mItem.getUnit());
                        zItem.setNumberCode(ClaimConst.ZAIRYO_KOSU);
                        zItem.setNumberCodeSystem(ClaimConst.NUMBER_CODE_ID);
                    }
                    break;
                    
                case ClaimConst.ADMIN:
                    String ommit = mItem.getName().replaceAll(REG_ADMIN_MARK, "");
                    bundle.setAdmin(ommit);
                    bundle.setAdminCode(mItem.getCode());
                    String bNum = mItem.getBundleNumber();
                    if (bNum != null && (!bNum.trim().equals(""))) {
                        bNum = ZenkakuUtils.toHankuNumber(bNum.trim());
                        bundle.setBundleNumber(mItem.getBundleNumber());
                    }
                    String memo = inMed.isSelected() ? IN_MEDICINE : EXT_MEDICINE;
                    bundle.setMemo(memo);
                    
                    retList.add(module);
                    module = createModuleModel();
                    bundle =(BundleMed) module.getModel();
                    break;  
            }
        }
        
        return retList;
    }
    
    /**
     * エディタで編集したスタンプを返す。
     * @return エディタで編集したスタンプ
     */
    public Object getValue() {
        List<ModuleModel> list = getBundles();
        return (Object) list.get(0);
    }
    
    /**
     * 編集するスタンプを表示する。
     * @param theModel 編集するスタンプ
     */
    public void setValue(Object theStamp) {
        
        // 連続して編集される場合があるのでテーブル内容等をクリアする
        clear();
        
        if (theStamp == null) {
            // Stateを変更する
            stateMgr.checkState();
            return;
        }
        // 引数で渡された Stamp をキャストする
        ModuleModel target  = (ModuleModel) theStamp;
        
        // Entityを保存する
        setEntity(target.getModuleInfo().getEntity());
        
        // スタンプ名を表示する
        String stampName = target.getModuleInfo().getStampName();
        boolean serialized = target.getModuleInfo().isSerialized();
        
        if (!serialized && stampName.startsWith(FROM_EDITOR_STAMP_NAME)) {
            stampName = DEFAULT_STAMP_NAME;
        } else if (stampName.equals("")) {
            stampName = DEFAULT_STAMP_NAME;
        }
        stampNameField.setText(stampName);
        
        BundleMed med = (BundleMed) target.getModel();
        if (med == null) {
            return;
        }
        
        //
        // レセ電算コードを保存する
        //
        if (med.getClassCode() != null) {
            saveReceiptCode = med.getClassCode();
        }
        
        ClaimItem[] items = med.getClaimItem();

        for (ClaimItem item : items) {
            
            MasterItem mItem = new MasterItem();
            mItem.setClassCode(Integer.parseInt(item.getClassCode()));
            
            // Code Name TableId
            mItem.setName(item.getName());
            mItem.setCode(item.getCode());
            
            String number = item.getNumber();
            if (number != null && (!number.equals(""))) {
                number = ZenkakuUtils.toHankuNumber(number.trim());
                mItem.setNumber(number);
                mItem.setUnit(item.getUnit());
            }
                
            medTableModel.addRow(mItem);
        }
        
        // Save Administration
        if (med.getAdmin() != null) {
            MasterItem item = new MasterItem();
            item.setClassCode(3);
            item.setCode(med.getAdminCode());
            item.setName(ADMIN_MARK + med.getAdmin());
            item.setDummy("X");
            String bNumber = med.getBundleNumber();
            bNumber = ZenkakuUtils.toHankuNumber(bNumber);
            item.setBundleNumber(bNumber);
            medTableModel.addRow(item);
        }
        
        // Memo
        String memo = med.getMemo();
        if (memo != null && memo.equals(IN_MEDICINE)) {
            inMed.setSelected(true);
        } else {
            extMed.setSelected(true);
        }
        
        // Notify
        stateMgr.checkState();
    }
    
    private ImageIcon createImageIcon(String name) {
        String res = RESOURCE_BASE + name;
        return new ImageIcon(this.getClass().getResource(res));
    }
}