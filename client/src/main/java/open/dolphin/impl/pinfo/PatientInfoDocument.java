package open.dolphin.impl.pinfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.PatientDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.table.StripeTableCellRenderer;
import open.dolphin.util.AgeCalculater;

/**
 * Documet to show Patient and Health Insurance info.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PatientInfoDocument extends AbstractChartDocument {
    
    // 患者属性名
//s.oh^ 2014/08/29 患者情報の追加
    //private static final String[] PATIENT_ATTRS = { 
    //    "患者 ID", "氏  名", "カナ", "ローマ字 *","性  別", "生年月日", "国  籍 *", "婚姻状況 *", "郵便番号", "住  所", "電  話", "携帯電話 *", "電子メール *"
    //};
    //private static final String[] PATIENT_ATTRS_TEMP = { 
    //    "患者 ID", "氏  名", "カナ", "ローマ字","性  別", "生年月日", "国  籍", "婚姻状況", "郵便番号", "住  所", "電  話", "携帯電話", "電子メール"
    //};
    private final String[] PATIENT_ATTRS;
    private final String[] PATIENT_ATTRS_TEMP;
    
//s.oh$   
    
    private final String INFO;
    
    // カラム名
    private final String[] COLUMN_NAMES;
    
    // 編集可能な行
//s.oh^ 2014/08/29 患者情報の追加
    //private static final int[] EDITABLE_ROWS = {3, 6, 7, 11, 12};
    private static final int[] EDITABLE_ROWS = {3, 6, 7, 11, 12, 13, 14, 15, 16, 17, 18};
//s.oh$     
    
    // 保存ボタン
    private JButton saveBtn;
    
    // テーブルモデル
    private PatientInfoTableModel pModel;
    
    // 属性表示テーブル
    private JTable pTable;
    
    // State Context
    private StateContext stateMgr;
    
    /** 
     * Creates new PatientInfoDocument 
     */
    public PatientInfoDocument() {
        
        // Resource Injection 
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(PatientInfoDocument.class);
        setTitle(bundle.getString("title.document"));
        
        PATIENT_ATTRS = bundle.getString("attributes.patient").split(",");
        PATIENT_ATTRS_TEMP = bundle.getString("attributes.patient.tmp").split(",");
        
        INFO = bundle.getString("labelText.editing");
        
        COLUMN_NAMES = bundle.getString("columnHeader.table").split(",");
    }
    
    private void initialize() {
       
        JComponent compo = createComponent();
       
        // 保存ボタンを生成する
        JPanel cmdPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cmdPanel.add(new JLabel(ClientContext.getImageIconArias("icon_info_small")));
        cmdPanel.add(new JLabel(INFO));
        
        saveBtn = new JButton(ClientContext.getImageIconArias("icon_save_small"));       
        saveBtn.setEnabled(false);
        saveBtn.addActionListener((ActionEvent e) -> {
            save();
        });
        cmdPanel.add(saveBtn);

        JPanel content = new JPanel(new BorderLayout());
        content.add(cmdPanel, BorderLayout.NORTH);
        content.add(compo, BorderLayout.CENTER);

        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(content, BorderLayout.CENTER);
        myPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        stateMgr = new StateContext();
        enter();
    }
    
    @Override
    public void start() {
        initialize();
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    public void enter() {
        super.enter();
        if (stateMgr != null) {
            stateMgr.enter();
        }
    }
    
    @Override
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }
    
    /**
     * 患者情報を更新する。
     */
    @Override
    public void save() {
        
        final PatientModel update = getContext().getPatient();
        final PatientDelegater pdl = new PatientDelegater();
        
        DBTask task = new DBTask<Void, Void>(getContext()) {
            
            @Override
            public Void doInBackground() throws Exception {
                pdl.updatePatient(update);
                return null;
            }
            
            @Override
            public void succeeded(Void result) {
                if (boundSupport!=null) {
                    setChartDocDidSave(true);
                    return;
                }               
                stateMgr.processSavedEvent();
            }
        };
        
        task.execute();
    }
    
    private JComponent createComponent() {
        
        // 患者モデルを取得する
        PatientModel patient = getContext().getPatient();
        Collection<PVTHealthInsuranceModel> insList = patient.getPvtHealthInsurances();
        
        // 患者情報テーブルを生成する
        pModel = new PatientInfoTableModel(patient, PATIENT_ATTRS, COLUMN_NAMES);
        pTable = new JTable(pModel);
        
        // レンダラ
        StripeTableCellRenderer rederer = new StripeTableCellRenderer();
        rederer.setTable(pTable);
        rederer.setDefaultRenderer();

        // 行の高さ
        pTable.setRowHeight(ClientContext.getMoreHigherRowHeight());
        
        // spacing
        pTable.setIntercellSpacing(new Dimension(2,2));

        // エディタ
        TableColumn column = pTable.getColumnModel().getColumn(1);
        DefaultCellEditor de = new DefaultCellEditor(new JTextField());
        de.setClickCountToStart(2);
        column.setCellEditor(de);

        // 幅
        pTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        pTable.getColumnModel().getColumn(1).setPreferredWidth(500);
        
        //-----------------------------------------------
        // 家族カルテ機能を実装する^
        //-----------------------------------------------
//        pTable.setTransferHandler(new PatientInfoTableTransferHandler());

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        String actionText = ClientContext.getMyBundle(PatientInfoDocument.class).getString("actionText.copy");
        final AbstractAction copyAction = new AbstractAction(actionText) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        pTable.getInputMap().put(copy, "Copy");
        pTable.getActionMap().put("Copy", copyAction);

        //-------------------------------------------------
        // Copy menu を加える
        //-------------------------------------------------
        pTable.addMouseListener(new MouseAdapter() {

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

                int row = pTable.rowAtPoint(e.getPoint());

                if (row < 0) {
                    return;
                }

                JPopupMenu contextMenu = new JPopupMenu();
                contextMenu.add(new JMenuItem(copyAction));
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        // 配置する
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pTable);
        
        //
        // 健康保険情報テーブルを生成する
        //
        if (insList != null) {
            
            for (PVTHealthInsuranceModel insurance : insList) {
                HealthInsuranceTableModel hModel = new HealthInsuranceTableModel(
                        insurance, COLUMN_NAMES);
                JTable hTable = new JTable(hModel);
                
                // spacing
                hTable.setIntercellSpacing(new Dimension(2,2));

                // レンダラ
                StripeTableCellRenderer sr = new StripeTableCellRenderer();
                sr.setTable(hTable);
                sr.setDefaultRenderer();
                
                // 行の高さ
                hTable.setRowHeight(ClientContext.getMoreHigherRowHeight());
                
                // 幅
                hTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                hTable.getColumnModel().getColumn(1).setPreferredWidth(500);

                // 配置する
                panel.add(Box.createVerticalStrut(7));
                panel.add(hTable);
            }
        }
        
        JScrollPane scroller = new JScrollPane(panel);
        
        return scroller;
    }
    
//    // Dropされた患者を家族カルテに登録する
//    private void importFamily(PatientModel model) {
//        
//        final JButton save = new JButton("登 録");
//        final JButton cancel = new JButton((String)UIManager.get("OptionPane.cancelButtonText"));
//        save.setEnabled(false);
//        
//        StringBuilder sb = new StringBuilder();
//        sb.append(model.getFullName());
//        sb.append(" さんを家族として登録します。");
//        String msg = sb.toString();
//        final String patientId = model.getPatientId();
//        
//        JLabel lbl = new JLabel("続柄:");
////        final String[] relations = new String[]{
////          "", "父", "母", "----","兄", "弟", "姉", "妹","----", "子供", "----", "祖父", "祖母","----","孫", "----","夫", "妻", "----","その他"
////        };
//        final String[] relations = new String[]{
//          "", "兄弟姉妹", "------","親", "子供","------","祖父母","孫","------","夫婦", "------","その他"
//        };
//        final JComboBox cmb = new JComboBox(relations);
//        cmb.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    save.setEnabled(cmb.getSelectedIndex()!=0);
//                }
//            }
//        });
//        JPanel p = new JPanel();
//        p.setLayout(new FlowLayout(FlowLayout.LEFT));
//        p.add(lbl);
//        p.add(cmb);
//        
//        Object[] message = new Object[2];
//        message[0] = msg;
//        message[1] = p;
//        
//        JOptionPane pane = new JOptionPane(
//                message,
//                JOptionPane.QUESTION_MESSAGE,
//                JOptionPane.DEFAULT_OPTION,
//                null,
//                new Object[]{save, cancel},
//                save);
//        
//        final JDialog dialog = pane.createDialog(getUI(), ClientContext.getFrameTitle("家族登録"));
//        dialog.setModal(true);
//        
//        save.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                dialog.setVisible(false);
//                dialog.dispose();
//                StringBuilder sb = new StringBuilder();
//                sb.append((String)cmb.getSelectedItem());
//                sb.append("-");
//                sb.append(patientId);
//                System.err.println(sb.toString());
//            }
//        });
//        cancel.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                dialog.setVisible(false);
//                dialog.dispose();
//            }
//        });
//        dialog.setVisible(true);
//    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        StringBuilder sb = new StringBuilder();
        int numRows = pTable.getSelectedRowCount();
        int[] rowsSelected = pTable.getSelectedRows();
        int numColumns =   pTable.getColumnCount();

        for (int i = 0; i < numRows; i++) {

            StringBuilder s = new StringBuilder();
            for (int col = 0; col < numColumns; col++) {
                Object o = pTable.getValueAt(rowsSelected[i], col);
                if (o!=null) {
                    //s.append(o.toString());
                    if(col == 0 && i < rowsSelected.length && rowsSelected[i] < PATIENT_ATTRS_TEMP.length) {
                        s.append(PATIENT_ATTRS_TEMP[rowsSelected[i]]);
                    }else{
                        s.append(o.toString());
                    }
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
     * 患者情報を表示する TableModel クラス。
     */
    protected class PatientInfoTableModel extends AbstractTableModel {
        
        // 患者モデル
        private final PatientModel patient;
        
        // 属性名の配列
        private final String[] attributes;
        
        // カラム名の配列
        private final String[] columnNames;
        
        public PatientInfoTableModel(PatientModel patient, String[] attrs, String[] columnNames) {
            this.patient = patient;
            this.attributes = attrs;
            this.columnNames = columnNames;
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public int getRowCount() {
            return PATIENT_ATTRS.length;
        }
        
        @Override
        public boolean isCellEditable(int row, int col) {
//minagawa^ 排他制御            
            // 編集可能な行である場合に true 
            if (getContext().isReadOnly()) {
                return false;
            }
            boolean ret = false;
            if (col == 1) {
                for (int i = 0; i < EDITABLE_ROWS.length; i++) {
                    if (row == EDITABLE_ROWS[i]) {
                        ret = true;
                        break;
                    }
                }
            }
            return ret;
        }
        
        @Override
        public Object getValueAt(int row, int col) {
            
            String ret = null;
            
            if (col == 0) {
                //
                // 属性名を返す
                //
                ret = attributes[row];
                
            } else if (col == 1 && patient != null) {
                
                //
                // 患者属性を返す
                //
                
                switch (row) {
                    
                    case 0:
                        ret = patient.getPatientId();
                        break;
                        
                    case 1:
                        ret = patient.getFullName();
                        break;
                        
                    case 2:
                        ret = patient.getKanaName();
                        break;
                        
                    case 3:
                        ret = patient.getRomanName();
                        break;
                        
                    case 4:
                        ret = patient.getGender();
                        ret = ret.toLowerCase();
                        java.util.ResourceBundle bundle = ClientContext.getMyBundle(PatientInfoDocument.class);
                        String male = bundle.getString("text.male");
                        String female = bundle.getString("text.female");
                        if (ret.startsWith("m") || ret.startsWith(male)) {
                            ret = male;
                        } else if (ret.startsWith("f") || ret.startsWith(female)) {
                            ret = female;
                        }
                        break;
                        
                    case 5:
                        int[] spec = AgeCalculater.getAgeSpec(patient.getBirthday());
                        StringBuilder sb = new StringBuilder();
                        if (spec[0]!=-1) {
                            String fmt = ClientContext.getMyBundle(PatientInfoDocument.class).getString("meesageFormat.age");
                            MessageFormat msf = new MessageFormat(fmt);
                            sb.append(msf.format(new Object[]{spec[0],spec[1],spec[2]}));
                        }
                        sb.append(" (").append(patient.getBirthday()).append(")");
                        ret = sb.toString();
                        break;
                        
                    case 6:
                        ret = patient.getNationality();
                        break;
                        
                    case 7:
                        ret = patient.getMaritalStatus();
                        break;
                        
                    case 8:
                        ret = patient.contactZipCode();
                        break;
                        
                    case 9:
                        ret = patient.contactAddress();
                        if (ret != null) {
                            ret = ret.replaceAll("　", " ");
                        }
                        break;
                        
                    case 10:
                        ret = patient.getTelephone();
                        break;
                        
                    case 11:
                        ret = patient.getMobilePhone();
                        break;
                        
                    case 12:
                        ret = patient.getEmail();
                        break;
                        
//s.oh^ 2014/08/29 患者情報の追加
                    case 13:
                        ret = patient.getReserve1();
                        break;
                    case 14:
                        ret = patient.getReserve2();
                        break;
                    case 15:
                        ret = patient.getReserve3();
                        break;
                    case 16:
                        ret = patient.getReserve4();
                        break;
                    case 17:
                        ret = patient.getReserve5();
                        break;
                    case 18:
                        ret = patient.getReserve6();
                        break;
//s.oh$
                }
            }
            return ret;
        }
        
        
        /**
         * 属性値を変更する。
         * @param value 属性値
         * @param row 行
         * @param col 列
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            
            if (value == null || value.equals("") || col == 0) {
                return;
            }
            
            String strValue = (String) value;
            
            switch (row) {
                
                case 3:
                    //
                    // ローマ字
                    //
                    patient.setRomanName(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                    
                case 6:
                    //
                    // 国籍
                    //
                    patient.setNationality(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                    
                case 7:
                    //
                    // 婚姻状況
                    //
                    patient.setMaritalStatus(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                    
               case 11:
                    //
                    // 携帯電話
                    //
                    patient.setMobilePhone(strValue);
                    stateMgr.processDirtyEvent();
                    break;     
                    
                case 12:
                    //
                    // 電子メール
                    //
                    patient.setEmail(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                    
//s.oh^ 2014/08/29 患者情報の追加
                case 13:
                    patient.setReserve1(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                case 14:
                    patient.setReserve2(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                case 15:
                    patient.setReserve3(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                case 16:
                    patient.setReserve4(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                case 17:
                    patient.setReserve5(strValue);
                    stateMgr.processDirtyEvent();
                    break;
                case 18:
                    patient.setReserve6(strValue);
                    stateMgr.processDirtyEvent();
                    break;
//s.oh$
            }
        }
    }
    
    /**
     * 保険情報を表示する TableModel クラス。
     */
    protected class HealthInsuranceTableModel extends AbstractTableModel {
        
        private final String[] columnNames;
        
        private final ArrayList<String[]> data;
        
        public HealthInsuranceTableModel(PVTHealthInsuranceModel insurance,
                String[] columnNames) {
            this.columnNames = columnNames;
            data = getData(insurance);
        }
        
        private ArrayList getData(PVTHealthInsuranceModel insurance) {
            
            if (insurance == null) {
                return null;
            }
            
            ArrayList<String[]> list = new ArrayList<>();
            
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(PatientInfoDocument.class);
            
            String[] rowData = new String[2];
            rowData[0] = bundle.getString("hitem.0");
            rowData[1] = insurance.getInsuranceClass();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.1");
            rowData[1] = insurance.getInsuranceClassCode();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.2");
            rowData[1] = insurance.getInsuranceNumber();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.3");
            rowData[1] = insurance.getClientGroup();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.4");
            rowData[1] = insurance.getClientNumber();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.5");
            String test = insurance.getFamilyClass();
            if (test.equals("true")) {
                test = bundle.getString("hitem.self");
            } else {
                test = bundle.getString("hitem.family");
            }
            rowData[1] = test;
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.6");
            rowData[1] = insurance.getStartDate();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.7");
            rowData[1] = insurance.getExpiredDate();
            list.add(rowData);
            
            String[] vals = insurance.getContinuedDisease();
            if (vals != null) {
                int count = vals.length;
                for (int i = 0; i < count; i++) {
                    rowData = new String[2];
                    rowData[0] = bundle.getString("hitem.8");
                    rowData[1] = vals[i];
                    list.add(rowData);
                }
            }
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.9");
            rowData[1] = insurance.getPayInRatio();
            list.add(rowData);
            
            rowData = new String[2];
            rowData[0] = bundle.getString("hitem.10");
            rowData[1] = insurance.getPayOutRatio();
            list.add(rowData);
            
            PVTPublicInsuranceItemModel[] pbi = insurance
                    .getPVTPublicInsuranceItem();
            if (pbi == null) {
                return list;
            }
            int count = pbi.length;
            for (int i = 0; i < count; i++) {
                PVTPublicInsuranceItemModel item = pbi[i];
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.11");
                rowData[1] = item.getPriority();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.12");
                rowData[1] = item.getProviderName();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.13");
                rowData[1] = item.getProvider();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.14");
                rowData[1] = item.getRecipient();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.15");
                rowData[1] = item.getStartDate();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.16");
                rowData[1] = item.getExpiredDate();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.17");
                rowData[1] = item.getPaymentRatio();
                list.add(rowData);
                
                rowData = new String[2];
                rowData[0] = bundle.getString("hitem.18");
                rowData[1] = item.getPaymentRatioType();
                list.add(rowData);
            }
            
            return list;
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public int getRowCount() {
            return data != null ? data.size() : 5;
        }
        
        @Override
        public Object getValueAt(int row, int col) {
            
            if (data == null) {
                return null;
            }
            
            if (row >= data.size()) {
                return null;
            }
            
            String[] rowData = (String[]) data.get(row);
            
            return (Object) rowData[col];
        }
    }
    
    abstract class State {
        
        public abstract void enter();
        
    }
    
    class CleanState extends State {
        
        @Override
        public void enter() {
            saveBtn.setEnabled(false);
            setDirty(false);
        }
    }
    
    class DirtyState extends State {
        
        @Override
        public void enter() {
            saveBtn.setEnabled(true);
        }
    }
    
    class StateContext {
        
        private final CleanState cleanState = new CleanState();
        private final DirtyState dirtyState = new DirtyState();
        private State curState;
        
        public StateContext() {
            curState = cleanState;
        }
        
        public void enter() {
            curState.enter();
        }
        
        public void processSavedEvent() {
            curState = cleanState;
            this.enter();
        }
        
        public void processDirtyEvent() {
            if (!isDirtyState()) {
                curState = dirtyState;
                this.enter();
            }
        }
        
        public boolean isDirtyState() {
            return curState == dirtyState;
        }
    }
}