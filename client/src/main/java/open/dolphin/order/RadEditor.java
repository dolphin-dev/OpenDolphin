package open.dolphin.order;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public final class RadEditor extends AbstractStampEditor {
    
    private static final String[] COLUMN_NAMES = {"コード", "診療内容", "数 量", "単 位"};
    private static final String[] METHOD_NAMES = {"getCode", "getName", "getNumber", "getUnit"};
    private static final int[] COLUMN_WIDTH = {50, 200, 10, 10};
    private static final int NUMBER_COLUMN = 2;

    private static final String[] SR_COLUMN_NAMES = {"種別", "コード", "名 称", "単位", "点数", "診区", "病診", "入外", "社老"};
    private static final String[] SR_METHOD_NAMES = {"getSlot", "getSrycd", "getName", "getTaniname", "getTen","getSrysyukbn", "getHospsrykbn", "getNyugaitekkbn", "getRoutekkbn"};
    private static final int[] SR_COLUMN_WIDTH = {10, 50, 200, 10, 10, 10, 5, 5, 5};
    private static final int SR_NUM_ROWS = 20;

    private IRadView view;

    private ListTableModel<MasterItem> tableModel;

    private ListTableModel<TensuMaster> searchResultModel;

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
            moduleInfo.setStampName(DEFAULT_STAMP_NAME);
        }

        // BundleDolphin を生成する
        BundleDolphin bundle = new BundleDolphin();

        // Dolphin Appli で使用するオーダ名称を設定する
        // StampHolder で使用される（タブ名に相当）
        bundle.setOrderName(getOrderName());

        // セットテーブルのマスターアイテムを取得する
        List<MasterItem> itemList = tableModel.getDataProvider();

        List<ClaimItem> tmpList = new ArrayList<ClaimItem>();

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
            bundle.setClassName(MMLTable.getClaimClassCodeName(c007));
        }

        // バンドル数
        //bundle.setBundleNumber((String) view.getNumberCombo().getSelectedItem());
        bundle.setBundleNumber("1");

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
        // 歴史的なごり
        if (!serialized && stampName.startsWith(FROM_EDITOR_STAMP_NAME)) {
            stampName = DEFAULT_STAMP_NAME;
            
        } else if (stampName.equals("")) {
            stampName = DEFAULT_STAMP_NAME;
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

        // Stateを変更する
        checkValidation();
    }

    @Override
    protected void checkValidation() {

        setIsEmpty = tableModel.getObjectCount() == 0 ? true : false;

        if (setIsEmpty) {
            view.getStampNameField().setText(DEFAULT_STAMP_NAME);
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

            } else {
                other++;
            }
        }

        // 何かあればよい事にする（したいそうだ）
        setIsValid = setIsValid && (techCnt > 0 || partCnt > 0 || other > 0);

        // ButtonControl
        view.getClearBtn().setEnabled(!setIsEmpty);
 //minagawa^ LSC Test
        //view.getOkCntBtn().setEnabled(setIsValid && getFromStampEditor());
        view.getOkCntBtn().setEnabled(setIsValid && getFromStampEditor() && !modifyFromStampHolder);
//minagawa$ 
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

                    case TT_85_SEARCH:
                        result = dao.getTensuMasterByCode("0085", d);
                        break;

                    case TT_CODE_SEARCH:
                        result = dao.getTensuMasterByCode(ZenkakuUtils.toHalfNumber(text), d);
                        break;

                    case TT_LETTER_SEARCH:
                        result = dao.getTensuMasterByName(StringTool.hiraganaToKatakana(text), d, view.getPartialChk().isSelected());
                        break;

                    case TT_SHINKU_SERACH:
                        String shin = ZenkakuUtils.toHalfNumber(text);
                        StringBuilder sb = new StringBuilder();
                        sb.append("^");
                        sb.append(shin.substring(1));
                        result = dao.getTensuMasterByShinku(sb.toString(), d);
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

    private void getPart() {

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
                //List<TensuMaster> result = dao.getTensuMasterByCode("^002", d);
                List<TensuMaster> result = dao.getTensuMasterByCode("002", d);

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
    protected void addSelectedTensu(TensuMaster tm) {

        // 項目の受け入れ試験
        String test = tm.getSlot();

        if (passPattern==null || (!passPattern.matcher(test).find())) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // 診療区分の受け入れ試験
        if (test.equals(ClaimConst.SLOT_SYUGI)) {
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
            if (name.equals("") || name.equals(DEFAULT_STAMP_NAME)) {
                view.getStampNameField().setText(item.getName());
            }
        }

        // テーブルへ追加する
        tableModel.addObject(item);

        // バリデーションを実行する
        checkValidation();
    }

    @Override
    protected void initComponents() {

        // View
        view = editorButtonTypeIsIcon() ? new RadView() : new RadViewText();

        // Info Label
        view.getInfoLabel().setText(this.getInfo());
//minagawa^ Icon Server
        view.getInfoLabel().setIcon(ClientContext.getImageIconArias("icon_info_small"));
//minagawa$         

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
                    return AbstractStampEditor.isNameEditableComment(code);
                }
                // 数量
                if (col == NUMBER_COLUMN) {
                    String code = (String) this.getValueAt(row, 0);
                    if (code==null) {
                        return false;
                    }
                    else if (AbstractStampEditor.isNameEditableComment(code)) {
                        return false;
                    } else if (AbstractStampEditor.is82Comment(code)) {
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                return col == NUMBER_COLUMN ? true : false;
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
                if (col == 1 && AbstractStampEditor.isNameEditableComment(mItem.getCode())) {
                    mItem.setName(value);
                    return;
                }

                // 数量
                int code = mItem.getClassCode();

                // null ok
                if (value == null || value.equals("")) {

                    boolean test = (code==ClaimConst.SYUGI ||
                                    code==ClaimConst.OTHER ||
                                    code==ClaimConst.BUI) ? true : false;
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
        column = setTable.getColumnModel().getColumn(NUMBER_COLUMN);
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
                            ret = HOSPITAL_CLINIC_FLAGS[index];
                        }
                        break;

                    case 7:
                        // 入外
                        if (ret!=null) {
                            int index = Integer.parseInt((String) ret);
                            ret = IN_OUT_FLAGS[index];
                        }
                        break;

                    case 8:
                        // 社老
                        if (ret!=null) {
                            int index = Integer.parseInt((String) ret);
                            ret = OLD_FLAGS[index];
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
                    search(view.getSearchTextField().getText().trim(), false);
                }
            }
        };
        searchTextField = view.getSearchTextField();
        searchTextField.getDocument().addDocumentListener(dl);
        searchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search(view.getSearchTextField().getText().trim(), true);
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

        // 部位検索ボタン
        view.getPartBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getPart();
            }
        });

        // 件数フィールド
        countField = view.getCountField();

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

    @Override
    protected void clear() {
        tableModel.clear();
        view.getStampNameField().setText(DEFAULT_STAMP_NAME);
        checkValidation();
    }

    public RadEditor() {
        super();
    }

    public RadEditor(String entity) {
        super(entity, true);
    }

    public RadEditor(String entity, boolean mode) {
        super(entity, mode);
    }
}
