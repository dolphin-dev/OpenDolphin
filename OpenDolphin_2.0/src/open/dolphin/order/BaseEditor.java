package open.dolphin.order;

import java.awt.Rectangle;
import open.dolphin.infomodel.ClaimConst;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.dao.SqlDaoFactory;
import open.dolphin.dao.SqlMasterDao;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.TensuMaster;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.OddEvenRowRenderer;
import open.dolphin.util.StringTool;
import open.dolphin.util.ZenkakuUtils;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class BaseEditor extends AbstractStampEditor {
    
    private static final String[] COLUMN_NAMES = {"コード", "診療内容", "数 量", "単 位"};
    private static final String[] METHOD_NAMES = {"getCode", "getName", "getNumber", "getUnit"};
    private static final int[] COLUMN_WIDTH = {50, 200, 10, 10};
    private static final int NUMBER_COLUMN = 2;

    private static final String[] SR_COLUMN_NAMES = {"種別", "コード", "名 称", "単位", "点数", "診区", "病診", "入外", "社老"};
    private static final String[] SR_METHOD_NAMES = {
        "getSlot", "getSrycd", "getName", "getTaniname", "getTen","getSrysyukbn", "getHospsrykbn", "getNyugaitekkbn", "getRoutekkbn"};
    private static final int[] SR_COLUMN_WIDTH = {10, 50, 200, 10, 10, 10, 5, 5, 5};
    private static final int SR_NUM_ROWS = 20;

    private BaseView view;

    private ListTableModel<MasterItem> tableModel;

    private ListTableModel<TensuMaster> searchResultModel;


    @Override
    public JPanel getView() {
        return view;
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

        // 診療行為があるかどうかのフラグ
        boolean found = false;

        for (MasterItem masterItem : itemList) {

            // マスタアイテムを ClaimItem に変換する
            ClaimItem item = masterToClaimItem(masterItem);

            // 診区を設定する
            // 最初に見つかった手技の診区をあとで ClaimBundle に設定する
            if ((masterItem.getClassCode() == ClaimConst.SYUGI) && (!found)) {

                // 集計先をマスタアイテム自体へ持たせている
                String c007 = getClaim007Code(masterItem.getClaimClassCode());

                if (c007 != null) {
                    setClassCode(c007);
                    found = true;
                }
            }

            bundle.addClaimItem(item);
        }

        // 診療行為区分
        String c007 = getClassCode()!=null ? getClassCode() : getImplied007();

        if (c007 != null) {

            bundle.setClassCode(c007);

            // Claim007 固定の値
            bundle.setClassCodeSystem(getClassCodeId());

            // 上記テーブルで定義されている診療行為の名称
            bundle.setClassName(MMLTable.getClaimClassCodeName(c007));
        }

        // バンドル数
        bundle.setBundleNumber((String) view.getNumberCombo().getSelectedItem());

        retModel.setModel((InfoModel) bundle);

        return (Object)retModel;
    }

    @Override
    public void setValue(Object value) {

        // 連続して編集される場合があるのでテーブル内容等をクリアする
        clear();

        // 編集するModuleModel(Model+Info)
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

        //-----------------------------
        // Bundle の 診療行為区分を保存
        //-----------------------------
        setClassCode(bundle.getClassCode());

        // ClaimItemをMasterItemへ変換してテーブルへ追加する
        ClaimItem[] items = bundle.getClaimItem();
        for (ClaimItem item : items) {
            tableModel.addObject(claimToMasterItem(item));
        }

        // バンドル数を数量コンボへ設定する
        String number = bundle.getBundleNumber();
        if (number != null && (!number.trim().equals(""))) {
            number = ZenkakuUtils.toHalfNumber(number.trim());
            view.getNumberCombo().setSelectedItem(number);
        }

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

        int techCnt = 0;
        int other = 0;

        List<MasterItem> itemList = tableModel.getDataProvider();

        for (MasterItem item : itemList) {

            if (item.getClassCode() == ClaimConst.SYUGI) {
                techCnt++;

            } else {
                other++;
            }
        }

        // 何かあればOK
        setIsValid = setIsValid && (techCnt > 0 || other > 0);

        // ButtonControl
        view.getClearBtn().setEnabled(!setIsEmpty);
        view.getOkCntBtn().setEnabled(setIsValid && getFromStampEditor());
        view.getOkBtn().setEnabled(setIsValid && getFromStampEditor());

        view.getTechChk().setSelected((techCnt > 0));

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

        // 診療区分の受け入れ試験
        if (test.equals(ClaimConst.SLOT_SYUGI)) {
            String shinku = tm.getSrysyukbn();
            if (shinkuPattern==null || (!shinkuPattern.matcher(shinku).find())) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }

        // MasterItem に変換する 0xFF0D
        MasterItem item = tensuToMasterItem(tm);
        
        // 手技の場合にスタンプ名を設定する
        if (item.getClassCode() == ClaimConst.SYUGI) {
            String stName = view.getStampNameField().getText().trim();
            if (stName.equals("") || stName.equals(DEFAULT_STAMP_NAME)) {
                view.getStampNameField().setText(item.getName());
            }
        }

        // テーブルへ追加する
        tableModel.addObject(item);

        // バリデーションを実行する
        checkValidation();
    }

    @Override
    protected void search(final String text, boolean hitRet) {

        boolean pass = true;
        pass = pass && ipOk();

        final int searchType = getSearchType(text, hitRet);

        pass = pass && (searchType!=TT_INVALID);

        if (!pass) {
            return;
        }

        // 件数をゼロにしておく
        countField.setText("0");

        SwingWorker worker = new SwingWorker<List<TensuMaster>, Void>() {

            @Override
            protected List<TensuMaster> doInBackground() throws Exception {
                SqlMasterDao dao = (SqlMasterDao) SqlDaoFactory.create("dao.master");
                String d = effectiveFormat.format(new Date());
                List<TensuMaster> result = null;

                switch (searchType) {

                    case TT_LIST_TECH:
                        result = dao.getTensuMasterByShinku(getShinkuRegExp(), d);
                        break;

                    case TT_TENSU_SEARCH:
                        String ten = text.substring(3);
                        ten = ZenkakuUtils.toHalfNumber(ten);
                        result = dao.getTensuMasterByTen(ten, d);
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

                if (!dao.isNoError()) {
                    throw new Exception(dao.getErrorMessage());
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

    @Override
    protected void clear() {
        tableModel.clear();
        view.getStampNameField().setText(DEFAULT_STAMP_NAME);
        checkValidation();
    }

    @Override
    protected void initComponents() {

        // View
        view = new BaseView();

        // Info Label
        view.getInfoLabel().setText(this.getInfo());

        //------------------------------------------
        // セットテーブルを生成する
        //------------------------------------------
        tableModel = new ListTableModel<MasterItem>(COLUMN_NAMES, START_NUM_ROWS, METHOD_NAMES, null) {

            // NUMBER_COLUMN を編集可能にする
            @Override
            public boolean isCellEditable(int row, int col) {
                // 元町皮膚科
                if (col == 1) {
                    String code = (String) this.getValueAt(row, 0);
                    return AbstractStampEditor.isEditableComment(code);
                }
                // 数量
                if (col == NUMBER_COLUMN) {
                    String code = (String) this.getValueAt(row, 0);
                    boolean editableComment = AbstractStampEditor.isEditableComment(code);
                    return (code==null || editableComment) ? false : true;
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
                if (col == 1 && AbstractStampEditor.isEditableComment(mItem.getCode())) {
                    mItem.setName(value);
                    return;
                }

                // 数量
                int code = mItem.getClassCode();

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

                mItem.setNumber(value);
                checkValidation();
            }
        };
        
        JTable setTable = view.getSetTable();
        setTable.setModel(tableModel);

        // Set Table の行の高さ
        setTable.setRowHeight(ClientContext.getMoreHigherRowHeight());

        setTable.setTransferHandler(new MasterItemTransferHandler()); // TransferHandler
        setTable.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask)
                    ? TransferHandler.COPY
                    : TransferHandler.MOVE;
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, action);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
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
        TableColumn column = null;
        int len = COLUMN_WIDTH.length;
        for (int i = 0; i < len; i++) {
            column = setTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(COLUMN_WIDTH[i]);
        }
        setTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());

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
        
        //--------------------------
        // 検索結果テーブルを生成する
        //--------------------------
        JTable searchResultTable = view.getSearchResultTable();
        searchResultModel = new ListTableModel<TensuMaster>(SR_COLUMN_NAMES, SR_NUM_ROWS, SR_METHOD_NAMES, null) {

            @Override
            public Object getValueAt(int row, int col) {

                Object ret = super.getValueAt(row, col);

                switch (col) {

                    case 6:
                        // 病診
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
        searchResultTable.setRowHeight(ClientContext.getHigherRowHeight());
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
                    }
                }
            }
        });
        
        column = null;
        len = SR_COLUMN_WIDTH.length;
        for (int i = 0; i < len; i++) {
            column = searchResultTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(SR_COLUMN_WIDTH[i]);
        }

        if (Project.getBoolean("masterItemColoring", true)) {
            searchResultTable.setDefaultRenderer(Object.class, new TensuItemRenderer(passPattern, shinkuPattern));
        } else {
            searchResultTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        }

        // 検索フィールド
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (view.getRtBtn().isSelected()) {
                    search(view.getSearchTextField().getText().trim(), false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (view.getRtBtn().isSelected()) {
                    search(view.getSearchTextField().getText().trim(), false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (view.getRtBtn().isSelected()) {
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

        // Real Time Search
        boolean rt = Project.getBoolean("masterSearch.realTime", true);
        view.getRtBtn().setSelected(rt);
        view.getRtBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Project.setBoolean("masterSearch.realTime", view.getRtBtn().isSelected());
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

        // 件数フィールド
        countField = view.getCountField();

        // スタンプ名フィールド
        view.getStampNameField().addFocusListener(AutoKanjiListener.getInstance());

        // OK & 連続ボタン
        view.getOkCntBtn().setEnabled(false);
        view.getOkCntBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boundSupport.firePropertyChange(VALUE_PROP, null, getValue());
                clear();
            }
        });

        // OK ボタン
        view.getOkBtn().setEnabled(false);
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
        view.getClearBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }

    public BaseEditor() {
    }

    public BaseEditor(String entity) {
        super(entity, true);
    }

    public BaseEditor(String entity, boolean mode) {
        super(entity, mode);
    }
}
