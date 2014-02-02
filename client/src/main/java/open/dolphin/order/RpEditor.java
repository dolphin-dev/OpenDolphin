package open.dolphin.order;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.StripeTableCellRenderer;
import open.dolphin.util.StringTool;
import open.dolphin.util.ZenkakuUtils;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class RpEditor extends AbstractStampEditor {

    private static final String[] COLUMN_NAMES = {"コード", "診療内容", "数量", "単位", " ", "日数/回数"};
    private static final String[] METHOD_NAMES = {"getCode", "getName", "getNumber", "getUnit", "getDummy", "getBundleNumber"};
    private static final int[] COLUMN_WIDTH = {50, 200, 10, 10, 10};
    private static final int ONEDAY_COLUMN = 2;
    private static final int BUNDLE_COLUMN = 5;

    private static final String[] SR_COLUMN_NAMES = {"種別", "コード", "名 称", "単位", "点数", "薬価基準"};
    private static final String[] SR_METHOD_NAMES = {"getSlot", "getSrycd", "getName", "getTaniname", "getTen","getYakkakjncd"};
    private static final int[] SR_COLUMN_WIDTH = {10, 50, 200, 10, 10, 10};
    private static final int SR_NUM_ROWS = 0;

    private static final String[] ADMIN_CODE_REGEXP = {"","0010001","0010002","0010003","0010004","(0010005|0010007)","0010006","0010008","0010009","001"};

    private static final String IN_MEDICINE     = "院内処方";
    private static final String EXT_MEDICINE    = "院外処方";
    
    private static final String[] RP_CODE = {"211", "212", "221", "222", "231", "232", "291", "292"};
    
    private static final String[] BUNDLE_MEMO = {"内用（院内処方）", "内用（院外処方）", "頓用（院内処方）", "頓用（院外処方）", "外用（院内処方）", "外用（院外処方）", "臨時（院内処方）", "臨時（院外処方）"};

    // 再編集の場合に保存しておくレセ電算コード
    private String saveReceiptCode;
    
    private IRpView view;

    private ListTableModel<MasterItem> tableModel;

    private ListTableModel<TensuMaster> searchResultModel;

    private int naiGaiTon;

    private int inExt;


    @Override
    public JPanel getView() {
        return (JPanel)view;
    }

    @Override
    public void dispose() {

        if (tableModel != null) {
            tableModel.clear();
        }

        if (searchResultModel != null) {
            searchResultModel.clear();
        }

        super.dispose();
    }

    private ModuleModel createModuleModel() {

        ModuleModel retModel = new ModuleModel();
        BundleMed med = new BundleMed();
        retModel.setModel(med);

        // StampInfoを設定する
        ModuleInfoBean moduleInfo = retModel.getModuleInfoBean();
        moduleInfo.setEntity(getEntity());
        moduleInfo.setStampRole(IInfoModel.ROLE_P);

        //　スタンプ名を設定する
        String stampName = view.getStampNameField().getText().trim();
        if (!stampName.equals("")) {
            moduleInfo.setStampName(stampName);
        } else {
            moduleInfo.setStampName(DEFAULT_STAMP_NAME);
        }

        return retModel;
    }

    @Override
    public Object getValue() {

        ModuleModel ret = createModuleModel();
        BundleMed bundle = (BundleMed) ret.getModel();

        List<MasterItem> items = tableModel.getDataProvider();

        // 頓用がチェックされているかどうか
        if (view.getTonyoChk().isSelected()) {
            naiGaiTon = 220;     // 内容=210,頓用=220, 外用=230, 臨時=290
            
        } else if (view.getTemporalChk().isSelected()) {
            naiGaiTon = 290;
            
//        } else if (saveReceiptCode!=null && saveReceiptCode.startsWith("29")) {
//            // ORCA で作成した臨時処方
//            naiGaiTon = 290;
            
        } else {
            naiGaiTon = 0;
        }

        // 院内処方 or 院外処方
        inExt = view.getInRadio().isSelected() ? 1 : 2;

        for (MasterItem mItem : items) {

            switch(mItem.getClassCode()) {

                case ClaimConst.YAKUZAI:

                    bundle.addClaimItem(masterToClaimItem(mItem));

                    if (naiGaiTon == 0) {

                        if (mItem.getYkzKbn() != null) {

                            // 頓用でない場合は剤型区分で内用か外用を決める
                            String test = mItem.getYkzKbn();

                            if (test.equals(ClaimConst.YKZ_KBN_NAIYO)) {

                                naiGaiTon = 210; // 内用

                            } else if (test.equals(ClaimConst.YKZ_KBN_GAIYO)) {

                                naiGaiTon = 230; // 外用
                            }
                        }
                    }
                    break;

                case ClaimConst.ADMIN:
                    String ommit = mItem.getName().replaceAll(REG_ADMIN_MARK, "");
                    bundle.setAdmin(ommit);
                    bundle.setAdminCode(mItem.getCode());
                    String bNum = trimToNullIfEmpty(mItem.getBundleNumber());
                    if (bNum != null) {
                        bNum = ZenkakuUtils.toHalfNumber(bNum);
                        bundle.setBundleNumber(bNum);
                    }
                    break;

                default:
                    bundle.addClaimItem(masterToClaimItem(mItem));
                    break;
            }
        }

        // レセ電算コード
        if (naiGaiTon != 0) {
            // 院内処方、院外処方で区分コードを確定する
            String rCode = String.valueOf(naiGaiTon + inExt);
            bundle.setClassCode(rCode);
        } else {
            // 旧スタンプの再編集で、トン用であったものがとん用以外へ変更された場合
            // 旧スタンプでは剤型区分が保存されていないため
        }
        
        if (bundle.getClassCode() == null) {

            // 既存のスタンプ
            if (saveReceiptCode != null) {
                
                StringBuilder sb = new StringBuilder();

                // 210 -> 21
                sb.append(saveReceiptCode.substring(0, 2));
                
                if (view.getInRadio().isSelected()) {
                    // 院内処方
                    sb.append("1");     // 211
                } else {
                    // 院外処方
                    sb.append("2");     // 212
                }
                
                bundle.setClassCode(sb.toString());
                
            } else {
                // 保険適用外の医薬品と用法でOKとするため
                bundle.setClassCode(ClaimConst.RECEIPT_CODE_NAIYO);
            }
        }

        // 2010-10-27 バンドルのメモを構成する
        // 内用(院外処方)...
        String cd = bundle.getClassCode();
        for (int i = 0; i < RP_CODE.length; i++) {
            if (cd.equals(RP_CODE[i])) {
                bundle.setMemo(BUNDLE_MEMO[i]);
                break;
            }
        }

        if (bundle.getMemo()==null) {
            bundle.setMemo(view.getInRadio().isSelected() ? IN_MEDICINE : EXT_MEDICINE);
        }
        
        bundle.setClassCodeSystem(ClaimConst.CLASS_CODE_ID);
        bundle.setClassName(MMLTable.getClaimClassCodeName(bundle.getClassCode()));

        return ret;
    }
   
    @Override
    public void setValue(Object value) {

        // 連続して編集される場合があるのでテーブル内容等をクリアする
        clear();

        ModuleModel target = (ModuleModel) value;

        if (target == null) {
            return;
        }

        // Entityを保存する
        setEntity(target.getModuleInfoBean().getEntity());

        // スタンプ名を表示する
        String stampName = target.getModuleInfoBean().getStampName();
        boolean serialized = target.getModuleInfoBean().isSerialized();

        if (!serialized && stampName.startsWith(FROM_EDITOR_STAMP_NAME)) {
            stampName = DEFAULT_STAMP_NAME;
        } else if (stampName.equals("")) {
            stampName = DEFAULT_STAMP_NAME;
        }
        view.getStampNameField().setText(stampName);

        BundleMed med = (BundleMed) target.getModel();
        if (med == null) {
            return;
        }

        //-------------------------------
        // レセ電算コードを保存する
        //-------------------------------
        boolean unknownInExt = false;

        if (med.getClassCode() != null) {
            saveReceiptCode = med.getClassCode();

            // コードから頓用かどうかを判定し、チェックする
            if (saveReceiptCode.startsWith("22")) {
                view.getTonyoChk().setSelected(true);
                
            } else if (saveReceiptCode.startsWith("29")) {
                view.getTemporalChk().setSelected(true);
            }

            // 院内処方または院外処方をチェックする
            if (saveReceiptCode.endsWith("1")) {

                view.getInRadio().setSelected(true);

            } else if (saveReceiptCode.endsWith("2")) {

                view.getOutRadio().setSelected(true);

            } else {
                unknownInExt = true;
            }
        }

        // ClaimItemをMasterItemへ変換してテーブルへ追加する
        ClaimItem[] items = med.getClaimItem();
        for (ClaimItem item : items) {
            tableModel.addObject(claimToMasterItem(item));
        }

        // Save Administration
        if (med.getAdmin() != null) {
            MasterItem item = new MasterItem();
            item.setClassCode(ClaimConst.ADMIN);
            item.setCode(med.getAdminCode());
            item.setName(ADMIN_MARK + med.getAdmin());
            item.setDummy("X");
            String bNumber = med.getBundleNumber();
            bNumber = ZenkakuUtils.toHalfNumber(bNumber);
            item.setBundleNumber(bNumber);
            tableModel.addObject(item);
        }

        // Memo
        if (unknownInExt) {
            String memo = med.getMemo();
            if (memo != null && memo.equals(IN_MEDICINE)) {
                view.getInRadio().setSelected(true);
            } else {
                view.getOutRadio().setSelected(true);
            }
        }
        
        checkValidation();
    }

    @Override
    protected void search(final String text, boolean hitRettun) {

        boolean pass = true;
        pass = pass && ipOk();

        final int searchType = getSearchType(text, hitRettun);

        pass = pass && (searchType!=TT_INVALID);
        pass = pass && (searchType!=TT_LIST_TECH);

        if (!pass) {
            return;
        }

        // 件数をゼロにしておく
        view.getCountField().setText("0");

        SwingWorker worker = new SwingWorker<List<TensuMaster>, Void>() {

            @Override
            protected List<TensuMaster> doInBackground() throws Exception {
                //SqlMasterDao dao = (SqlMasterDao) SqlDaoFactory.create("dao.master");
                //OrcaRestDelegater dao = new OrcaRestDelegater();
                OrcaDelegater dao = OrcaDelegaterFactory.create();
                String d = new SimpleDateFormat("yyyyMMdd").format(new Date());
                List<TensuMaster> result = null;

                switch (searchType) {

                    case TT_LIST_TECH:
                        break;

                    case TT_TENSU_SEARCH:
                        String ten = text.substring(3);
                        result = dao.getTensuMasterByTen(ZenkakuUtils.toHalfNumber(ten), d);
                        break;

                    case TT_85_SEARCH:
                        result = dao.getTensuMasterByCode("0085", d);
                        break;

                    case TT_CODE_SEARCH:
                        result = dao.getTensuMasterByCode(ZenkakuUtils.toHalfNumber(text), d);
                        break;

                    case TT_LETTER_SEARCH:
                        result = dao.getTensuMasterByName(StringTool.hiraganaToKatakana(text), d, view.getPartialChk().isSelected());
                        break;
                }

//                if (!dao.isNoError()) {
//                    throw new Exception(dao.getErrorMessage());
//                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    List<TensuMaster> result = get();
                    searchResultModel.setDataProvider(result);
                    int cnt = searchResultModel.getObjectCount();
                    view.getCountField().setText(String.valueOf(cnt));
                    Rectangle r = view.getSearchResultTable().getCellRect(0, 0, true);
                    view.getSearchResultTable().scrollRectToVisible(r);

                } catch (InterruptedException ex) {

                } catch (ExecutionException ex) {
                    alertSearchError(ex.getMessage());
                }
            }
        };

        worker.execute(); 
    }

    private void getUsage(final String regExp) {

        if (!ipOk()) {
            return;
        }

        // 件数をゼロにしておく
        view.getCountField().setText("0");

        SwingWorker worker = new SwingWorker<List<TensuMaster>, Void>() {

            @Override
            protected List<TensuMaster> doInBackground() throws Exception {
                //SqlMasterDao dao = (SqlMasterDao) SqlDaoFactory.create("dao.master");
                //OrcaRestDelegater dao = new OrcaRestDelegater();
                OrcaDelegater dao = OrcaDelegaterFactory.create();
                String d = new SimpleDateFormat("yyyyMMdd").format(new Date());
                
                List<TensuMaster> result = dao.getTensuMasterByCode(regExp, d);

//                if (!dao.isNoError()) {
//                    throw new Exception(dao.getErrorMessage());
//                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    List<TensuMaster> result = get();
                    searchResultModel.setDataProvider(result);
                    int cnt = searchResultModel.getObjectCount();
                    view.getCountField().setText(String.valueOf(cnt));

                } catch (InterruptedException ex) {

                } catch (ExecutionException ex) {
                    alertSearchError(ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    @Override
    protected void checkValidation() {

        setIsEmpty = tableModel.getObjectCount() == 0 ? true : false;

        if (setIsEmpty) {
            view.getStampNameField().setText(DEFAULT_STAMP_NAME);
        }

        setIsValid = true;

        // 薬剤またはその他と用法があること
        int medCnt = 0;
        int useCnt = 0;
        int other = 0;

        List<MasterItem> itemList = tableModel.getDataProvider();

        for (MasterItem item : itemList) {

            if (item.getClassCode() == ClaimConst.YAKUZAI) {
                medCnt++;

            } else if (item.getClassCode() == ClaimConst.ADMIN) {
                useCnt++;

            } else {
                // 2010-03-09
                // 保険適用外医薬品等を許可する
                // ただし何かは不明
                other++;
            }
        }

        setIsValid = setIsValid && (medCnt > 0 || other > 0);
        setIsValid = setIsValid && (useCnt == 1);

        // ButtonControl
        view.getClearBtn().setEnabled(!setIsEmpty);
//minagawa^ LSC Test
        //view.getOkCntBtn().setEnabled(setIsValid && getFromStampEditor());
        view.getOkCntBtn().setEnabled(setIsValid && getFromStampEditor() && !modifyFromStampHolder);
//minagawa$ 
        view.getOkBtn().setEnabled(setIsValid && getFromStampEditor());

        view.getMedicineCheck().setSelected((medCnt > 0));
        view.getUsageCheck().setSelected((useCnt == 1));

        // 通知する
        super.checkValidation();
    }

    @Override
    protected void addSelectedTensu(TensuMaster tm) {

        // 項目の受け入れ試験
        String test = tm.getSlot();

        if (passPattern==null || (!passPattern.matcher(test).find())) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // MasterItem に変換する
        MasterItem item = tensuToMasterItem(tm);

        // item が用法であった場合、テーブルのアイテムをスキャンし、外用薬があった場合は数量を1にする
        if (item.getClassCode()==ClaimConst.ADMIN) {
            List<MasterItem> list = tableModel.getDataProvider();
            if (list!=null) {
                for (MasterItem mi : list) {
                    if (mi.getYkzKbn()!=null && mi.getYkzKbn().equals(ClaimConst.YKZ_KBN_GAIYO)) {
                        item.setBundleNumber("1");
                        break;
                    }
                }
            }
        }

        // 医薬品名をスタンプ名の候補にする
        String name = view.getStampNameField().getText().trim();
        if (name.equals("") || name.equals(DEFAULT_STAMP_NAME)) {
            view.getStampNameField().setText(item.getName());
        }

        // テーブルへ追加する
        tableModel.addObject(item);

        // バリデーションを実行する
        checkValidation();
    }

    @Override
    protected void clear() {
        tableModel.clear();
        view.getStampNameField().setText(DEFAULT_STAMP_NAME);
        checkValidation();
    }

    @Override
    protected void initComponents() {

        // View
        view = editorButtonTypeIsIcon() ? new RpView() : new RpViewText();

        // Info Label
        view.getInfoLabel().setText(this.getInfo());
//minagawa^ Icon Server
        view.getInfoLabel().setIcon(ClientContext.getImageIconArias("icon_info_small"));
//minagawa$         

        // セットテーブルを生成する
        tableModel = new ListTableModel<MasterItem>(COLUMN_NAMES, 0, METHOD_NAMES, null) {

            // 数量と回数のみ編集可能
            @Override
            public boolean isCellEditable(int row, int col) {

                // 元町皮膚科
                if (col == 1) {
                    String code = (String) this.getValueAt(row, 0);
                    return AbstractStampEditor.isNameEditableComment(code);
                }

                // 用法
                if (col == BUNDLE_COLUMN) {
                    String code = (String) this.getValueAt(row, 0);
                    return (code!=null && code.startsWith(ClaimConst.ADMIN_CODE_START)) ? true : false;
                }

                // 数量
                if (col == ONEDAY_COLUMN) {
                    String code = (String) this.getValueAt(row, 0);
                    boolean editableComment = AbstractStampEditor.isNameEditableComment(code);
                    boolean codeIsAdmin = (code!=null && code.startsWith(ClaimConst.ADMIN_CODE_START));
                    boolean codeIs82Comment = (AbstractStampEditor.is82Comment(code));
                    return (code==null || editableComment || codeIsAdmin || codeIs82Comment) ? false : true;
                }

                return false;
            }

            @Override
            public void setValueAt(Object o, int row, int col) {

                MasterItem mItem = getObject(row);

                if (mItem == null) {
                    return;
                }

                String value = (String) o;
                if (value != null) {
                    value = value.trim();
                }

                // コメント編集 元町皮膚科
                if (col == 1 && AbstractStampEditor.isNameEditableComment(mItem.getCode())) {
                    mItem.setName(value);
                    return;
                }

                // null
                if (value == null || value.equals("")) {
                    boolean test = (col == ONEDAY_COLUMN && (mItem.getClassCode()==ClaimConst.SYUGI || mItem.getClassCode()==ClaimConst.OTHER))
                                 ? true
                                 : false;
                    if (test) {
                        mItem.setNumber(null);
                        mItem.setUnit(null);
                    }
                    checkValidation();
                    return;
                }

                if (col == ONEDAY_COLUMN && mItem.getClassCode()!=ClaimConst.ADMIN) {
                    mItem.setNumber(value);
                    checkValidation();
                    return;
                }

                if (col == BUNDLE_COLUMN && mItem.getClassCode()==ClaimConst.ADMIN) {
                    mItem.setBundleNumber(value);
                    checkValidation();
                }
            }
        };
        
        JTable setTable = view.getSetTable();
        setTable.setModel(tableModel);
        
        // 数量入力: リターンキーで次のセルに移動するため
        setTable.setCellSelectionEnabled(true);

        // DnD
        setTable.setDragEnabled(true);
        setTable.setDropMode(DropMode.INSERT);                          // INSERT
        setTable.setTransferHandler(new MasterItemTransferHandler()); // TransferHandler
        // Selection Mode
        setTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 選択モード
        setTable.setRowSelectionAllowed(true);
        ListSelectionModel m = setTable.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int row = view.getSetTable().getSelectedRow();
                    if (tableModel.getObject(row)!= null) {
                        view.getDeleteBtn().setEnabled(true);
                    } else {
                        view.getDeleteBtn().setEnabled(false);
                    }
                }
            }
        });

        // 列幅を設定する
        TableColumn column;
        int len = COLUMN_WIDTH.length;
        for (int i = 0; i < len; i++) {
            column = setTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(COLUMN_WIDTH[i]);
        }
        //setTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        StripeTableCellRenderer rederer = new StripeTableCellRenderer();
        rederer.setTable(setTable);
        rederer.setDefaultRenderer();
        // Set Table の行の高さ
        setTable.setRowHeight(ClientContext.getMoreHigherRowHeight());

        // 数量カラムにセルエディタを設定する
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoRomanListener.getInstance());
        column = setTable.getColumnModel().getColumn(ONEDAY_COLUMN);
        DefaultCellEditor de = new DefaultCellEditor(tf);
        int ccts = Project.getInt("order.table.clickCountToStart", 1);
        de.setClickCountToStart(ccts);
        column.setCellEditor(de);

        // 診療内容カラム(column number = 1)にセルエディタを設定する 元町皮膚科
        JTextField tf2 = new JTextField();
        tf2.addFocusListener(AutoKanjiListener.getInstance());
        column = setTable.getColumnModel().getColumn(1);
        DefaultCellEditor de2 = new DefaultCellEditor(tf2);
        de2.setClickCountToStart(ccts);
        column.setCellEditor(de2);

        // 日数回数カラム
        JTextField tf3 = new JTextField();
        tf3.addFocusListener(AutoRomanListener.getInstance());
        column = setTable.getColumnModel().getColumn(BUNDLE_COLUMN);
        DefaultCellEditor de3 = new DefaultCellEditor(tf3);
        de3.setClickCountToStart(ccts);
        column.setCellEditor(de3);
        
        //
        // 検索結果テーブルを生成する
        //
        JTable searchResultTable = view.getSearchResultTable();
        searchResultModel = new ListTableModel<TensuMaster>(SR_COLUMN_NAMES, SR_NUM_ROWS, SR_METHOD_NAMES, null);
        searchResultTable.setModel(searchResultModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultTable.setRowSelectionAllowed(true);
        ListSelectionModel lm = searchResultTable.getSelectionModel();
        lm.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int row = view.getSearchResultTable().getSelectedRow();
                    TensuMaster o = searchResultModel.getObject(row);
                    if (o != null) {
                        addSelectedTensu(o);
                        searchTextField.requestFocus();
                    }
                }
            }
        });
        
        len = SR_COLUMN_WIDTH.length;
        for (int i = 0; i < len; i++) {
            column = searchResultTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(SR_COLUMN_WIDTH[i]);
        }
        
        //searchResultTable.setDefaultRenderer(Object.class, new TensuItemRenderer(passPattern, shinkuPattern));
        if (Project.getBoolean("masterItemColoring", true)) {
            searchResultTable.setDefaultRenderer(Object.class, new TensuItemRenderer(passPattern, shinkuPattern));
        } else {
            //searchResultTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
            StripeTableCellRenderer str = new StripeTableCellRenderer();
            str.setTable(searchResultTable);
            str.setDefaultRenderer();
        }
        searchResultTable.setRowHeight(ClientContext.getHigherRowHeight());

        // 検索フィールド
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (view.getRtCheck().isSelected()) {
                    search(view.getSearchTextField().getText().trim(),false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (view.getRtCheck().isSelected()) {
                    search(view.getSearchTextField().getText().trim(),false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (view.getRtCheck().isSelected()) {
                    search(view.getSearchTextField().getText().trim(),false);
                }
            }
        };
        searchTextField = view.getSearchTextField();
        searchTextField.getDocument().addDocumentListener(dl);
        searchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search(view.getSearchTextField().getText().trim(),true);
            }
        });
        searchTextField.addFocusListener(AutoKanjiListener.getInstance());
        // マスター検索ができない場合を追加
        searchTextField.setEnabled(Project.canSearchMaster());

        // Real Time Search
        boolean rt = Project.getBoolean("masterSearch.realTime", true);
        view.getRtCheck().setSelected(rt);
        view.getRtCheck().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Project.setBoolean("masterSearch.realTime", view.getRtCheck().isSelected());
            }
        });

        // 部分一致
        boolean pmatch = Project.getBoolean("masterSearch.partialMatch", false);
        view.getPartialChk().setSelected(pmatch);
        view.getPartialChk().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Project.setBoolean("masterSearch.partialMatch", view.getPartialChk().isSelected());
            }
        });

        // 用法検索
        JComboBox usage = view.getUsageCombo();
        usage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JComboBox cb = (JComboBox)ae.getSource();
                int index = cb.getSelectedIndex();
                String regExp = ADMIN_CODE_REGEXP[index];
                if (!regExp.equals("")) {
                    getUsage(regExp);
                }
            }
        });
        
//        usage.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    int index = view.getUsageCombo().getSelectedIndex();
//                    String regExp = ADMIN_CODE_REGEXP[index];
//                    if (!regExp.equals("")) {
//                        getUsage(regExp);
//                    }
//                }
//            }
//        });

        // 件数フィールド
        countField = view.getCountField();

        // 院内、院外ボタン
        JRadioButton inBtn = view.getInRadio();
        JRadioButton outBtn = view.getOutRadio();
        ButtonGroup g = new ButtonGroup();
        g.add(inBtn);
        g.add(outBtn);
        boolean bOut = Project.getBoolean(Project.RP_OUT, true);
        outBtn.setSelected(bOut);
        inBtn.setSelected(!bOut);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean b = view.getOutRadio().isSelected();
                Project.setBoolean(Project.RP_OUT, b);
            }
        };
        inBtn.addActionListener(al);
        outBtn.addActionListener(al);
        
        // 頓用と臨時処方 on の場合は排他制御が必要
        ActionListener al2 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JCheckBox cb = (JCheckBox)ae.getSource();
                if (cb.isSelected()) {
                    if (cb==view.getTonyoChk()) {
                        if (view.getTemporalChk().isSelected()) {
                            view.getTemporalChk().setSelected(false);
                        }
                    } else if (cb==view.getTemporalChk()) {
                        if (view.getTonyoChk().isSelected()) {
                            view.getTonyoChk().setSelected(false);
                        }
                    }
                }
            }
        };
        view.getTonyoChk().addActionListener(al2);
        view.getTemporalChk().addActionListener(al2);

        // スタンプ名フィールド
        view.getStampNameField().addFocusListener(AutoKanjiListener.getInstance());

        // OK & 連続ボタン
        view.getOkCntBtn().setEnabled(false);
   //minagawa^ Icon Server
        view.getOkCntBtn().setIcon(ClientContext.getImageIconArias("icon_gear_small"));
//minagawa$       
        view.getOkCntBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boundSupport.firePropertyChange(VALUE_PROP, null, getValue());
                clear();
            }
        });

        // OK ボタン
        view.getOkBtn().setEnabled(false);
   //minagawa^ Icon Server
        view.getOkBtn().setIcon(ClientContext.getImageIconArias("icon_accept_small"));
//minagawa$       
        view.getOkBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boundSupport.firePropertyChange(VALUE_PROP, null, getValue());
                dispose();
                boundSupport.firePropertyChange(EDIT_END_PROP, false, true);
            }
        });

        // 削除ボタン
        view.getDeleteBtn().setEnabled(false);
//minagawa^ Icon Server
        view.getDeleteBtn().setIcon(ClientContext.getImageIconArias("icon_delete_small"));
//minagawa$          
        view.getDeleteBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = view.getSetTable().getSelectedRow();
                if (tableModel.getObject(row) != null) {
                    tableModel.deleteAt(row);
                    checkValidation();
                }
            }
        });

        // クリアボタン
        view.getClearBtn().setEnabled(false);
//minagawa^ Icon Server
        view.getClearBtn().setIcon(ClientContext.getImageIconArias("icon_clear_small"));
//minagawa$         
        view.getClearBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }

    public RpEditor() {
        super();
    }

    public RpEditor(String entity) {
        super(entity, true);
    }

    public RpEditor(String entity, boolean mode) {
        super(entity, mode);
    }
}
