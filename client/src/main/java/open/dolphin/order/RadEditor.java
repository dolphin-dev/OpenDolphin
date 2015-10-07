package open.dolphin.order;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import open.dolphin.client.AutoKanjiListener;
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
public final class RadEditor extends AbstractStampEditor {
    
    private String[] COLUMN_NAMES;
    private String[] METHOD_NAMES;
    private int[] COLUMN_WIDTH;
    private int NUMBER_COLUMN;

    private String[] SR_COLUMN_NAMES;
    private String[] SR_METHOD_NAMES;
    private int[] SR_COLUMN_WIDTH;
    private int SR_NUM_ROWS;

    private IRadView view;

    private ListTableModel<MasterItem> tableModel;

    private ListTableModel<TensuMaster> searchResultModel;
    
////s.oh^ 2014/07/11 スタンプエディタのフォーカス制御
//    private boolean editCellStart;
//    private boolean editCellEnter;
////s.oh$

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

    @Override
    public Object getValue() {

        // 常に新規のモデルとして返す
        ModuleModel retModel = new ModuleModel();
        ModuleInfoBean moduleInfo = retModel.getModuleInfoBean();
        moduleInfo.setEntity(getEntity());
        moduleInfo.setStampRole(IInfoModel.ROLE_P);

        // スタンプ名を設定する
        String text = view.getStampNameField().getText().trim();
        if (!text.equals("")) {
            moduleInfo.setStampName(text);
        } else {
            moduleInfo.setStampName(getDefaultStampName());
        }

        // BundleDolphin を生成する
        BundleDolphin bundle = new BundleDolphin();

        // Dolphin Appli で使用するオーダ名称を設定する
        // StampHolder で使用される（タブ名に相当）
        bundle.setOrderName(getOrderName());

        // セットテーブルのマスターアイテムを取得する
        List<MasterItem> itemList = tableModel.getDataProvider();

        List<ClaimItem> tmpList = new ArrayList<>();

        // 診療行為があるかどうかのフラグ
        // 増田外科
        //boolean found = false;

        for (MasterItem masterItem : itemList) {

            ClaimItem item = masterToClaimItem(masterItem);

//             増田外科
//            // 診区(集計先)を設定する
//            // 最初に見つかった手技の診区をあとで ClaimBundle に設定する
//            if ((masterItem.getClassCode() == ClaimConst.SYUGI) && (!found)) {
//
//                // 集計先をマスタアイテム自体へ持たせている
//                String c007 = getClaim007Code(masterItem.getClaimClassCode());
//
//                if (c007 != null) {
//                    setClassCode(c007);
//                    found = true;
//                }
//            }

            // 部位を最初のアイテムにする
            if (item.getCode().startsWith(ClaimConst.RBUI_CODE_START)) {
                tmpList.add(0, item);

            } else {
                tmpList.add(item);
            }
        }

        for (ClaimItem ci : tmpList) {
            bundle.addClaimItem(ci);
        }

        // 診療行為区分
        //String c007 = getClassCode()!=null ? getClassCode() : getImplied007();
        // 増田外科
        String c007 = "700";
        
        if (c007!=null) {

            // 700 画像診断
            bundle.setClassCode(c007);
            
            // Claim007 固定の値
            bundle.setClassCodeSystem(getClassCodeId());

            // 上記テーブルで定義されている診療行為の名称
            java.util.ResourceBundle resBundle = ClientContext.getClaimBundle();
            bundle.setClassName(resBundle.getString(c007));
        }

        // バンドル数
        //bundle.setBundleNumber((String) view.getNumberCombo().getSelectedItem());
//s.oh^ 2014/03/31 スタンプ回数対応
        //bundle.setBundleNumber("1");
        bundle.setBundleNumber((String)view.getNumberCombo().getSelectedItem());
//s.oh$

        retModel.setModel((InfoModel) bundle);

        return (ModuleModel) retModel;
    }

    @Override
     public void setValue(Object value) {

        // 連続して編集される場合があるのでテーブル内容等をクリアする
        clear();

        ModuleModel target = (ModuleModel) value;

        // null であればリターンする
        if (target == null) {
            return;
        }

        // Entityを保存する
        setEntity(target.getModuleInfoBean().getEntity());

        // Stamp 名と表示形式を設定する
        String stampName = target.getModuleInfoBean().getStampName();
        boolean serialized = target.getModuleInfoBean().isSerialized();

        // スタンプ名がエディタから発行の場合はデフォルトの名称にする
        if (!serialized && stampName.startsWith(getStampNameFromEditor())) {
            stampName = getDefaultStampName();
            
        } else if (stampName.equals("")) {
            stampName = getDefaultStampName();
        }
        view.getStampNameField().setText(stampName);

        // Model を表示する
        BundleDolphin bundle = (BundleDolphin) target.getModel();
        if (bundle == null) {
            return;
        }

        //
        // Bundle の 診療行為区分を保存
        //
        setClassCode(bundle.getClassCode());

        // ClaimItemをMasterItemへ変換してテーブルへ追加する
        ClaimItem[] items = bundle.getClaimItem();
        for (ClaimItem item : items) {
            tableModel.addObject(claimToMasterItem(item));
        }

//        // 数量コンボでバンドル数を選択する
//        String number = bundle.getBundleNumber();
//        if (number != null && (!number.equals(""))) {
//            number = ZenkakuUtils.toHalfNumber(number);
//            view.getNumberCombo().setSelectedItem(number);
//        }
//s.oh^ 2014/03/31 スタンプ回数対応
        String number = bundle.getBundleNumber();
        if (number != null && (!number.equals(""))) {
            number = ZenkakuUtils.toHalfNumber(number);
            view.getNumberCombo().setSelectedItem(number);
        }
//s.oh$

        // Stateを変更する
        checkValidation();
    }

    @Override
    protected void checkValidation() {

        setIsEmpty = tableModel.getObjectCount() == 0;

        if (setIsEmpty) {
            view.getStampNameField().setText(getDefaultStampName());
        }

        setIsValid = true;

        int techCnt = 0;        // 診療行為
        int partCnt = 0;        // 部位
        int other = 0;

        List<MasterItem> itemList = tableModel.getDataProvider();

        for (MasterItem item : itemList) {

            if (item.getCode().startsWith("002")) {
                // 互換性
                partCnt++;

            } else if (item.getClassCode() == ClaimConst.SYUGI) {
                techCnt++;

//s.oh^ 2014/08/08 スタンプ編集制御
            //} else {
            } else if(Project.getBoolean("masteritem.all.permission", true)) {
//s.oh$
                other++;
            }
        }

        // 何かあればよい事にする（したいそうだ）
        setIsValid = setIsValid && (techCnt > 0 || partCnt > 0 || other > 0);

        // ButtonControl
        view.getClearBtn().setEnabled(!setIsEmpty);
        view.getOkCntBtn().setEnabled(setIsValid && getFromStampEditor() && !modifyFromStampHolder);
        view.getOkBtn().setEnabled(setIsValid && getFromStampEditor());

        view.getTechCheck().setSelected((techCnt > 0));
        view.getPartCheck().setSelected((partCnt > 0));

        // 通知する
        super.checkValidation();
    }

    @Override
    protected void search(final String text, boolean hitReturn) {

        boolean pass = true;
        pass = pass && ipOk();

        final int searchType = getSearchType(text, hitReturn);

        pass = pass && (searchType!=TT_INVALID);

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
                        result = dao.getTensuMasterByShinku(getShinkuRegExp(), d);
                        break;

                    case TT_TENSU_SEARCH:
                        String ten = text.substring(3);
                        result = dao.getTensuMasterByTen(ZenkakuUtils.toHalfNumber(ten), d);
                        break;

                    case TT_CODE_SEARCH:
                        result = dao.getTensuMasterByCode(ZenkakuUtils.toHalfNumber(text), d);
                        break;

                    case TT_LETTER_SEARCH:
                        result = dao.getTensuMasterByName(StringTool.hiraganaToKatakana(text), d, view.getPartialChk().isSelected());
//s.oh^ 2013/11/08 傷病名検索不具合
                        if(result == null || result.size() <= 0) {
                            result = dao.getTensuMasterByName(text, d, view.getPartialChk().isSelected());
                        }
//s.oh$
                        break;

                    case TT_SHINKU_SERACH:
                        String shin = ZenkakuUtils.toHalfNumber(text);
                        StringBuilder sb = new StringBuilder();
                        sb.append("^");
                        sb.append(shin.substring(1));
                        result = dao.getTensuMasterByShinku(sb.toString(), d);
                        break;
                }
                
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

    private void getPart() {

        if (!ipOk()) {
            return;
        }

        // 件数をゼロにしておく
        view.getCountField().setText("0");

        SwingWorker worker = new SwingWorker<List<TensuMaster>, Void>() {

            @Override
            protected List<TensuMaster> doInBackground() throws Exception {
                
                OrcaDelegater dao = OrcaDelegaterFactory.create();
                String d = new SimpleDateFormat("yyyyMMdd").format(new Date());
                List<TensuMaster> result = dao.getTensuMasterByCode("002", d);
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
    protected void addSelectedTensu(TensuMaster tm) {

        // 項目の受け入れ試験
        String test = tm.getSlot();

        if (passPattern==null || (!passPattern.matcher(test).find())) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        ResourceBundle clb = ClientContext.getClaimBundle();

        // 診療区分の受け入れ試験
        if (test.equals(clb.getString("SLOT_SYUGI"))) {
            String shinku = tm.getSrysyukbn();
            if (shinkuPattern==null || (!shinkuPattern.matcher(shinku).find())) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }

        // MasterItem に変換する
        MasterItem item = tensuToMasterItem(tm);

        // 診療行為をスタンプ名に設定する
        if (item.getClassCode() == ClaimConst.SYUGI) {
            String name = view.getStampNameField().getText().trim();
            if (name.equals("") || name.equals(getDefaultStampName())) {
                view.getStampNameField().setText(item.getName());
            }
        }

        // テーブルへ追加する
        tableModel.addObject(item);

        // バリデーションを実行する
        checkValidation();
    }

    private final void initComponents() {
        
        // Resource Injection
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(RadEditor.class);
        
        // セットテーブルカラム名
        String line = bundle.getString("columnNames.setTable");
        COLUMN_NAMES = line.split(",");
        
        // セットテーブルMethod名
        line = bundle.getString("methods.setTable");
        METHOD_NAMES = line.split(",");

        // カラム幅
        COLUMN_WIDTH = new int[]{50, 200, 10, 10};
        
        // 数量カラム
        NUMBER_COLUMN = 2;
        
        // マスターテーブルカラム名
        line = bundle.getString("columnNames.masterTable");
        SR_COLUMN_NAMES = line.split(",");
        
        // マスターテーブルMethod名
        line = bundle.getString("methods.masterTable");
        SR_METHOD_NAMES = line.split(",");

        // カラム幅
        SR_COLUMN_WIDTH = new int[]{10, 50, 200, 10, 10, 10, 5, 5, 5};
        
        // 数量カラム
        SR_NUM_ROWS = 20;

        // View
        view = editorButtonTypeIsIcon() ? new RadView() : new RadViewText();

        // Info Label
        view.getInfoLabel().setText(this.getInfo());
        view.getInfoLabel().setIcon(ClientContext.getImageIconArias("icon_info_small"));        

        //------------------------------------------
        // セットテーブルを生成する
        //------------------------------------------
        tableModel = new ListTableModel<MasterItem>(COLUMN_NAMES, 0, METHOD_NAMES, null) {

            // NUMBER_COLUMN を編集可能にする
            @Override
            public boolean isCellEditable(int row, int col) {
                // 元町皮膚科
                if (col == 1) {
                    String code = (String) this.getValueAt(row, 0);
                    return isNameEditableComment(code);
                }
                // 数量
                if (col == NUMBER_COLUMN) {
                    String code = (String) this.getValueAt(row, 0);
                    if (code==null) {
                        return false;
                    }
                    else if (isNameEditableComment(code)) {
                        return false;
                    } else return !is82Comment(code);
                }
                return col == NUMBER_COLUMN;
            }

            // NUMBER_COLUMN に値を設定する
            @Override
            public void setValueAt(Object o, int row, int col) {

                MasterItem mItem = getObject(row);

                if (mItem == null) {
                    return;
                }

                String value = (String) o;
                if (o != null) {
                    value = value.trim();
                }

                // コメント編集 元町皮膚科
                if (col == 1 && isNameEditableComment(mItem.getCode())) {
                    mItem.setName(value);
                    return;
                }

                // 数量
                int code = mItem.getClassCode();

                // null ok
                if (value == null || value.equals("")) {

                    boolean test = (code==ClaimConst.SYUGI ||
                            code==ClaimConst.OTHER ||
                            code==ClaimConst.BUI);
                    if (test) {
                        mItem.setNumber(null);
                        mItem.setUnit(null);
                    }
                    checkValidation();
                    return;
                }

                // 数量を設定するのは勝手
                mItem.setNumber((String) o);
                checkValidation();
            }
        };
        
        JTable setTable = view.getSetTable();
        setTable.setModel(tableModel);
        
        // 数量入力: リターンキーで次のセルに移動するため
        setTable.setCellSelectionEnabled(true);

        setTable.setDragEnabled(true);
        setTable.setDropMode(DropMode.INSERT);                          // INSERT
        setTable.setTransferHandler(new MasterItemTransferHandler()); // TransferHandler
        setTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 選択モード
        setTable.setRowSelectionAllowed(true);
        ListSelectionModel m = setTable.getSelectionModel();
        m.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting() == false) {
                int row = view.getSetTable().getSelectedRow();
                if (tableModel.getObject(row)!= null) {
                    view.getDeleteBtn().setEnabled(true);
                } else {
                    view.getDeleteBtn().setEnabled(false);
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
//s.oh^ 2014/02/24 スタンプ内項目削除不具合
        //tf.addFocusListener(AutoRomanListener.getInstance());
        FocusListener flRoman = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                Object source = e.getSource();
                if (source != null && source instanceof JTextComponent) {
                    JTextComponent tc = (JTextComponent) source;
                    if (tc.getInputContext() != null) {
                        tc.getInputContext().setCharacterSubsets(null);
                    }
                }
                view.getDeleteBtn().setEnabled(false);
                view.getClearBtn().setEnabled(false);
////s.oh^ 2014/07/11 スタンプエディタのフォーカス制御
//                editCellStart = true;
////s.oh$
            }
            @Override
            public void focusLost(FocusEvent e) {
                if(view.getSetTable().isEditing()) {
                    view.getSetTable().getCellEditor().stopCellEditing();
                }
                int row = view.getSetTable().getSelectedRow();
                if (tableModel.getObject(row)!= null) {
                    view.getDeleteBtn().setEnabled(true);
                } else {
                    view.getDeleteBtn().setEnabled(false);
                }
                view.getClearBtn().setEnabled(!setIsEmpty);
////s.oh^ 2014/07/11 スタンプエディタのフォーカス制御
//                if(editCellStart && editCellEnter) {
//                    view.getSearchTextField().requestFocus();
//                }
//                editCellStart = false;
//                editCellEnter = false;
////s.oh$
            }
        };
        FocusListener flKanji = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (ClientContext.isWin()) {
                    Object source = e.getSource();
                    if (source != null && source instanceof JTextComponent) {
                        JTextComponent tc = (JTextComponent) source;
                        if (tc.getInputContext() != null) {
                            tc.getInputContext().setCompositionEnabled(true);
                        }
                    }
                }
                view.getDeleteBtn().setEnabled(false);
                view.getClearBtn().setEnabled(false);
////s.oh^ 2014/07/11 スタンプエディタのフォーカス制御
//                editCellStart = true;
////s.oh$
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (ClientContext.isWin()) {
                    Object source = e.getSource();
                    if (source != null && source instanceof JTextComponent) {
                        JTextComponent tc = (JTextComponent) source;
                        if (tc.getInputContext() != null) {
                            tc.getInputContext().setCompositionEnabled(false);
                        }
                    }
                }else{
                    if(view.getSetTable().isEditing()) {
                        view.getSetTable().getCellEditor().stopCellEditing();
                    }
                }
                int row = view.getSetTable().getSelectedRow();
                if (tableModel.getObject(row)!= null) {
                    view.getDeleteBtn().setEnabled(true);
                } else {
                    view.getDeleteBtn().setEnabled(false);
                }
                view.getClearBtn().setEnabled(!setIsEmpty);
////s.oh^ 2014/07/11 スタンプエディタのフォーカス制御
//                if(editCellStart && editCellEnter) {
//                    view.getSearchTextField().requestFocus();
//                }
//                editCellStart = false;
//                editCellEnter = false;
////s.oh$
            }
        };
        tf.addFocusListener(flRoman);
//s.oh$
        column = setTable.getColumnModel().getColumn(NUMBER_COLUMN);
        DefaultCellEditor de = new DefaultCellEditor(tf);
        int ccts = Project.getInt("order.table.clickCountToStart", 1);
        de.setClickCountToStart(ccts);
        column.setCellEditor(de);

        // 診療内容カラム(column number = 1)にセルエディタを設定する 元町皮膚科
        JTextField tf2 = new JTextField();
//s.oh^ 2014/02/24 スタンプ内項目削除不具合
        //tf2.addFocusListener(AutoKanjiListener.getInstance());
        tf2.addFocusListener(flKanji);
//s.oh$
        column = setTable.getColumnModel().getColumn(1);
        DefaultCellEditor de2 = new DefaultCellEditor(tf2);
        de2.setClickCountToStart(ccts);
        column.setCellEditor(de2);
        
////s.oh^ 2014/07/11 スタンプエディタのフォーカス制御
//        tf.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {}
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if(e.getKeyCode() == KeyEvent.VK_ENTER && editCellStart) {
//                    editCellEnter = true;
//                }else{
//                    editCellEnter = false;
//                }
//            }
//            @Override
//            public void keyReleased(KeyEvent e) {}
//        });
//        tf2.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {}
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if(e.getKeyCode() == KeyEvent.VK_ENTER && editCellStart) {
//                    editCellEnter = true;
//                }else{
//                    editCellEnter = false;
//                }
//            }
//            @Override
//            public void keyReleased(KeyEvent e) {}
//        });
////s.oh$
        
        //
        // 検索結果テーブルを生成する
        //
        JTable searchResultTable = view.getSearchResultTable();
        searchResultModel = new ListTableModel<TensuMaster>(SR_COLUMN_NAMES, SR_NUM_ROWS, SR_METHOD_NAMES, null) {

            @Override
            public Object getValueAt(int row, int col) {

                Object ret = super.getValueAt(row, col);

                switch (col) {

                    case 6:
                        // 病診
                        //System.out.println((String) ret);
                        if (ret!=null) {
                            int index = Integer.parseInt((String) ret);
                            String[] hospFlag = (String[])ClientContext.getClaimBundle().getObject("HOSPITAL_CLINIC_FLAGS");
                            ret = hospFlag[index];
                        }
                        break;

                    case 7:
                        // 入外
                        if (ret!=null) {
                            int index = Integer.parseInt((String) ret);
                            String[] inOutFlag = (String[])ClientContext.getClaimBundle().getObject("IN_OUT_FLAGS");
                            ret = inOutFlag[index];
                        }
                        break;

                    case 8:
                        // 社老
                        if (ret!=null) {
                            int index = Integer.parseInt((String) ret);
                            String[] oldFlag = (String[])ClientContext.getClaimBundle().getObject("OLD_FLAGS");
                            ret = oldFlag[index];
                        }
                        break;
                }

                return ret;

            }
        };
        searchResultTable.setModel(searchResultModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultTable.setRowSelectionAllowed(true);
        ListSelectionModel lm = searchResultTable.getSelectionModel();
        lm.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting() == false) {
                int row = view.getSearchResultTable().getSelectedRow();
                TensuMaster o = searchResultModel.getObject(row);
                if (o != null) {
                    addSelectedTensu(o);
                    searchTextField.requestFocus();
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
//s.oh^ 2014/04/14 RT検索改善
                //if (view.getRtCheck().isSelected()) {
                //    search(view.getSearchTextField().getText().trim(),false);
                //}
                SwingUtilities.invokeLater(() -> {
                    if (view.getRtCheck().isSelected()) {
                        search(view.getSearchTextField().getText().trim(),false);
                    }
                });
//s.oh$
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//s.oh^ 2014/04/14 RT検索改善
                //if (view.getRtCheck().isSelected()) {
                //    search(view.getSearchTextField().getText().trim(),false);
                //}
                SwingUtilities.invokeLater(() -> {
                    if (view.getRtCheck().isSelected()) {
                        search(view.getSearchTextField().getText().trim(),false);
                    }
                });
//s.oh$
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//s.oh^ 2014/04/14 RT検索改善
                //if (view.getRtCheck().isSelected()) {
                //    search(view.getSearchTextField().getText().trim(),false);
                //}
                SwingUtilities.invokeLater(() -> {
                    if (view.getRtCheck().isSelected()) {
                        search(view.getSearchTextField().getText().trim(),false);
                    }
                });
//s.oh$
            }
        };
        searchTextField = view.getSearchTextField();
        searchTextField.getDocument().addDocumentListener(dl);
        searchTextField.addActionListener((ActionEvent e) -> {
            search(view.getSearchTextField().getText().trim(), true);
        });
        searchTextField.addFocusListener(AutoKanjiListener.getInstance());
        // マスター検索ができない場合を追加
        searchTextField.setEnabled(Project.canSearchMaster());

        // Real Time Search
        boolean rt = Project.getBoolean("masterSearch.realTime", true);
        view.getRtCheck().setSelected(rt);
        view.getRtCheck().addActionListener((ActionEvent arg0) -> {
            Project.setBoolean("masterSearch.realTime", view.getRtCheck().isSelected());
        });

        // 部分一致
        boolean pmatch = Project.getBoolean("masterSearch.partialMatch", false);
        view.getPartialChk().setSelected(pmatch);
        view.getPartialChk().addActionListener((ActionEvent arg0) -> {
            Project.setBoolean("masterSearch.partialMatch", view.getPartialChk().isSelected());
        });

        // 部位検索ボタン
        view.getPartBtn().addActionListener((ActionEvent e) -> {
            getPart();
        });

        // 件数フィールド
        countField = view.getCountField();

        // スタンプ名フィールド
        view.getStampNameField().addFocusListener(AutoKanjiListener.getInstance());

        // OK & 連続ボタン
        view.getOkCntBtn().setEnabled(false);
        if(editorButtonTypeIsIcon()) {
            view.getOkCntBtn().setIcon(ClientContext.getImageIconArias("icon_gear_small"));
        }
////s.oh^ 2014/10/22 Icon表示
//        view.getSearchLabel().setIcon(ClientContext.getImageIconArias("icon_search_small"));
////s.oh$        
        view.getOkCntBtn().addActionListener((ActionEvent e) -> {
            boundSupport.firePropertyChange(VALUE_PROP, null, getValue());
            clear();
        });

        // OK ボタン
        view.getOkBtn().setEnabled(false);
        if(editorButtonTypeIsIcon()) {
            view.getOkBtn().setIcon(ClientContext.getImageIconArias("icon_accept_small"));
        }    
        view.getOkBtn().addActionListener((ActionEvent e) -> {
            boundSupport.firePropertyChange(VALUE_PROP, null, getValue());
            dispose();
            boundSupport.firePropertyChange(EDIT_END_PROP, false, true);
        });

        // 削除ボタン
        view.getDeleteBtn().setEnabled(false);
        if(editorButtonTypeIsIcon()) {
            view.getDeleteBtn().setIcon(ClientContext.getImageIconArias("icon_delete_small"));
        }    
        view.getDeleteBtn().addActionListener((ActionEvent e) -> {
            int row = view.getSetTable().getSelectedRow();
            if (tableModel.getObject(row) != null) {
                tableModel.deleteAt(row);
                checkValidation();
            }
        });

        // クリアボタン
        view.getClearBtn().setEnabled(false);
        if(editorButtonTypeIsIcon()) {
            view.getClearBtn().setIcon(ClientContext.getImageIconArias("icon_clear_small"));
        }     
        view.getClearBtn().addActionListener((ActionEvent e) -> {
            clear();
        });
    }

    @Override
    protected void clear() {
        tableModel.clear();
        view.getStampNameField().setText(getDefaultStampName());
        checkValidation();
    }

//    public RadEditor() {
//        super();
//    }

    public RadEditor(String entity) {
        this(entity, true);
    }

    public RadEditor(String entity, boolean mode) {
        super(entity, mode);
        initComponents();
    }
}
