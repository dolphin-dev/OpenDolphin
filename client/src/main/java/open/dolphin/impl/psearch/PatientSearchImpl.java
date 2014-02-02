package open.dolphin.impl.psearch;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import open.dolphin.client.*;
import open.dolphin.delegater.PVTDelegater1;
import open.dolphin.delegater.PatientDelegater;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.helper.KeyBlocker;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.impl.pvt.WatingListImpl;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;
import open.dolphin.table.ColumnSpec;
import open.dolphin.table.ColumnSpecHelper;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.ListTableSorter;
import open.dolphin.table.StripeTableCellRenderer;
import open.dolphin.util.AgeCalculater;
import open.dolphin.util.StringTool;

/**
 * 患者検索PatientSearchPlugin
 *
 * @author Kazushi Minagawa
 * @author modified by masuda, Masuda Naika
 */
public class PatientSearchImpl extends AbstractMainComponent implements PropertyChangeListener {

    private int number = 10000;
    
    private static final String NAME = "患者検索";
    
    private static final String[] COLUMN_NAMES 
            = {"ID", "氏名", "カナ", "性別", "生年月日", "受診日", "状態"};
    
    private final String[] PROPERTY_NAMES 
            = {"patientId", "fullName", "kanaName", "genderDesc", "ageBirthday", "pvtDateTrimTime", "isOpened"};
    
    private static final Class[] COLUMN_CLASSES = {
        String.class, String.class, String.class, String.class, String.class, 
        String.class, String.class};
    
    private final int[] COLUMN_WIDTH = {50, 100, 120, 30, 100, 80, 20};
    
    //private final int START_NUM_ROWS = 30;
   
    // カラム仕様名
    private static final String COLUMN_SPEC_NAME = "patientSearchTable.withoutAddress.column.spec";
    
//minagawa^ lsctest    
    // 状態カラムの識別名
    private static final String COLUMN_IDENTIFIER_STATE = "stateColumn";
//minagawa$
    
    // カラム仕様ヘルパー
    private ColumnSpecHelper columnHelper;
    
    private static final String KEY_AGE_DISPLAY = "patientSearchTable.withoutAddress.ageDisplay";

    // 選択されている患者情報
    private PatientModel selectedPatient;

    // 年齢表示
    private boolean ageDisplay;
    
    // 年齢生年月日メソッド
    private final String[] AGE_METHOD = new String[]{"ageBirthday", "birthday"};

    // View
    private PatientSearchView view;
    private KeyBlocker keyBlocker;
    private int sortItem;

    // カラム仕様リスト
    //private List<ColumnSpec> columnSpecs;

    private int ageColumn;
    //private int pvtDateColumn;
    private int stateColumn; // 追加

    private ListTableModel tableModel;
    private ListTableSorter sorter;

    private AbstractAction copyAction;
    
//masuda^
    private String clientUUID;
    private ChartEventHandler cel;
//masuda$    

    /** Creates new PatientSearch */
    public PatientSearchImpl() {
        setName(NAME);
        cel = ChartEventHandler.getInstance();
        clientUUID = cel.getClientUUID();
        cel.addPropertyChangeListener(PatientSearchImpl.this);
    }

    @Override
    public void start() {
        setup();
        initComponents();
        connect();
        enter();
    }

    @Override
    public void enter() {
        controlMenu();
//pns^ 入ってきたら，キーワードフィールドにフォーカス
        //view.getKeywordFld().requestFocusInWindow();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                view.getKeywordFld().requestFocusInWindow();
                view.getKeywordFld().selectAll();
            }
        });
//pns$        
    }

    @Override
    public void stop() {
        
        cel.removePropertyChangeListener(this);
        
        // ColumnSpecsを保存する
        if (columnHelper != null) {
            columnHelper.saveProperty();
        }
    }

    public PatientModel getSelectedPatinet() {
        return selectedPatient;
    }

    public void setSelectedPatinet(PatientModel model) {
        selectedPatient = model;
        controlMenu();
    }

    public ListTableModel<PatientModel> getTableModel() {
        //return (ListTableModel<PatientModel>) view.getTable().getModel();
        return (ListTableModel<PatientModel>) sorter.getTableModel();
    }

    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
//masuda^              
        if (view.getTable() == null) {
            return;
        }

        ageDisplay = !ageDisplay;
        Project.setBoolean(KEY_AGE_DISPLAY, ageDisplay);
        String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
        ListTableModel tModel = getTableModel();
        tModel.setProperty(method, ageColumn);

        List<ColumnSpec> columnSpecs = columnHelper.getColumnSpecs();
        for (int i = 0; i < columnSpecs.size(); i++) {
            ColumnSpec cs = columnSpecs.get(i);
            String test = cs.getMethod();
            if (test.toLowerCase().endsWith("birthday")) {
                cs.setMethod(method);
                break;
            }
        }
//masuda$        
    }

    /**
     * メニューを制御する
     */
    private void controlMenu() {
        PatientModel pvt = getSelectedPatinet();
        boolean enabled = canOpen(pvt);
        getContext().enabledAction(GUIConst.ACTION_OPEN_KARTE, enabled);
    }

    /**
     * カルテを開くことが可能かどうかを返す。
     * @return 開くことが可能な時 true
     */
    private boolean canOpen(PatientModel patient) {
        if (patient == null) {
            return false;
        }

        if (isKarteOpened(patient)) {
            return false;
        }

        return true;
    }

    /**
     * カルテがオープンされているかどうかを返す。
     * @return オープンされている時 true
     */
    private boolean isKarteOpened(PatientModel patient) {
        if (patient != null) {
            boolean opened = false;
            List<ChartImpl> allCharts = ChartImpl.getAllChart();
            for (ChartImpl chart : allCharts) {
                if (chart.getPatient().getId() == patient.getId()) {
                    opened = true;
                    break;
                }
            }
            return opened;
        }
        return false;
    }

    /**
     * 受付リストのコンテキストメニュークラス。
     */
    class ContextListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            mabeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mabeShowPopup(e);
        }

        public void mabeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {

                final JPopupMenu contextMenu = new JPopupMenu();

                int row = view.getTable().rowAtPoint(e.getPoint());
                ListTableModel<PatientModel> tModel = getTableModel();
                PatientModel obj = tModel.getObject(row);
                int selected = view.getTable().getSelectedRow();

                if (row == selected && obj != null) {
                    contextMenu.add(new JMenuItem(new ReflectAction("カルテを開く", PatientSearchImpl.this, "openKarte")));
                    contextMenu.addSeparator();
                    contextMenu.add(new JMenuItem(copyAction));
                    contextMenu.add(new JMenuItem(new ReflectAction("受付登録", PatientSearchImpl.this, "addAsPvt")));
                    contextMenu.addSeparator();
                }

                JCheckBoxMenuItem item = new JCheckBoxMenuItem("年齢表示");
                contextMenu.add(item);
                item.setSelected(ageDisplay);
                item.addActionListener((ActionListener) EventHandler.create(ActionListener.class, PatientSearchImpl.this, "switchAgeDisplay"));

                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    private void setup() {
        
 //masuda^ ColumnSpecHelperを準備する
        columnHelper = new ColumnSpecHelper(COLUMN_SPEC_NAME,
                COLUMN_NAMES, PROPERTY_NAMES, COLUMN_CLASSES, COLUMN_WIDTH);
        columnHelper.loadProperty();

        // Scan して age / pvtDate カラムを設定する
        ageColumn = columnHelper.getColumnPositionEndsWith("irthday"); //irthday
        //pvtDateColumn = columnHelper.getColumnPositionStartWith("pvtDate");
        stateColumn = columnHelper.getColumnPosition("isOpened");
        
        ageDisplay = Project.getBoolean(KEY_AGE_DISPLAY, true);
//masuda$        
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {

        // View
        view = new PatientSearchView();
        setUI(view);
        
        // ColumnSpecHelperにテーブルを設定する
        columnHelper.setTable(view.getTable());

        //------------------------------------------
        // View のテーブルモデルを置き換える
        //------------------------------------------
        String[] columnNames = columnHelper.getTableModelColumnNames();
        String[] methods = columnHelper.getTableModelColumnMethods();
        Class[] cls = columnHelper.getTableModelColumnClasses();

        tableModel = new ListTableModel<PatientModel>(columnNames, 0, methods, cls) {

            @Override
            public Object getValueAt(int row, int col) {

                Object ret = null;

                if (col == ageColumn && ageDisplay) {

                    PatientModel p = getObject(row);

                    if (p != null) {
                        int showMonth = Project.getInt("ageToNeedMonth", 6);
                        ret = AgeCalculater.getAgeAndBirthday(p.getBirthday(), showMonth);
                    }
                } else {

                    ret = super.getValueAt(row, col);
                }

                return ret;
            }
        };
        view.getTable().setModel(tableModel);
        
//masuda^   table sorter 組み込み
        sorter = new ListTableSorter(tableModel);
        view.getTable().setModel(sorter);
        sorter.setTableHeader(view.getTable().getTableHeader());
//masuda$
        // カラム幅更新
        columnHelper.updateColumnWidth();
//minagawa^ lsctest        
        view.getTable().getColumnModel().getColumn(stateColumn).setIdentifier(COLUMN_IDENTIFIER_STATE);
//minagawa$        
        
//masuda^        
        // レンダラを設定する
        //view.getTable().setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        // 連ドラ、梅ちゃん先生
        PatientListTableRenderer renderer = new PatientListTableRenderer();
        renderer.setTable(view.getTable());
        renderer.setDefaultRenderer();
//masuda$  
        
        // ソートアイテム
        sortItem = Project.getInt("sortItem", 0);
        view.getSortItem().setSelectedIndex(sortItem);

        // Auto IME Windows の時のみ
        if (!ClientContext.isMac()) {
            // デフォルトは true
            boolean autoIme = Project.getBoolean("autoIme", true);
            view.getAutoIme().setSelected(autoIme);
        } else {
            // MAC は disabled
            //view.getAutoIme().setEnabled(false);
            view.getAutoIme().setVisible(false);
        }
        
        // 行高
        if (ClientContext.isWin()) {
            view.getTable().setRowHeight(ClientContext.getMoreHigherRowHeight());
        } else {
            view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
        }
    }

    /**
     * コンポーンントにリスナを登録し接続する。
     */
    private void connect() {

        // ColumnHelperでカラム変更関連イベントを設定する
        columnHelper.connect();
        
        EventAdapter adp = new EventAdapter(view.getKeywordFld(), view.getTable());

        // 自動IME ボタン
        view.getAutoIme().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox check = (JCheckBox) e.getSource();
                boolean selected = check.isSelected();
                Project.setBoolean("autoIme", selected);
                
                if (selected) {
                    // 選択されたらIME ON
                    view.getKeywordFld().addFocusListener(AutoKanjiListener.getInstance());
                } else {
                    // されなければ OFF
                    view.getKeywordFld().addFocusListener(AutoRomanListener.getInstance());
                }
            }
        });

        // Sort アイテム
        view.getSortItem().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox cb = (JComboBox) e.getSource();
                    sortItem = cb.getSelectedIndex();
                    Project.setInt("sortItem", sortItem);
                }
            }
        });

        // カレンダによる日付検索を設定する
        PopupListener pl = new PopupListener(view.getKeywordFld());

        // コンテキストメニューを設定する
        view.getTable().addMouseListener(new ContextListener());

        keyBlocker = new KeyBlocker(view.getKeywordFld());

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        view.getTable().getInputMap().put(copy, "Copy");
        view.getTable().getActionMap().put("Copy", copyAction);
        
//minagawa^ 仮保存カルテ取得対応
        view.getTmpKarteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTmpKarte();
            }
        });
//minagawa$
        
//        //-----------------------------------------------
//        // 家族カルテ機能 DnD ^
//        //-----------------------------------------------
//        view.getTable().setDragEnabled(true);
//        view.getTable().setTransferHandler(new PatientSearchTransferHandler());
    }

    class EventAdapter implements ActionListener, ListSelectionListener, MouseListener {

        public EventAdapter(JTextField tf, JTable tbl) {

            boolean autoIme = Project.getBoolean("autoIme", true);
            if (autoIme) {
                tf.addFocusListener(AutoKanjiListener.getInstance());
            } else {
                tf.addFocusListener(AutoRomanListener.getInstance());
            }
            tf.addActionListener(EventAdapter.this);
            
            tbl.getSelectionModel().addListSelectionListener(EventAdapter.this);
            tbl.addMouseListener(EventAdapter.this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField tf = (JTextField) e.getSource();
            String test = tf.getText().trim();
            if (!test.equals("")) {
                find(test);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
                JTable table = view.getTable();
                ListTableModel<PatientModel> tableModel = getTableModel();
                int row = table.getSelectedRow();
                //PatientModel patient = (PatientModel) tableModel.getObject(row);
                PatientModel patient = (PatientModel)sorter.getObject(row);
                setSelectedPatinet(patient);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable table = (JTable) e.getSource();
                ListTableModel<PatientModel> tableModel = getTableModel();
                PatientModel value = (PatientModel) tableModel.getObject(table.getSelectedRow());
                if (value != null) {
                    openKarte();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        }
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        
        StringBuilder sb = new StringBuilder();
        int numRows = view.getTable().getSelectedRowCount();
        int[] rowsSelected = view.getTable().getSelectedRows();
        int numColumns =   view.getTable().getColumnCount();

        for (int i = 0; i < numRows; i++) {
            if (tableModel.getObject(rowsSelected[i]) != null) {
                StringBuilder s = new StringBuilder();
                for (int col = 0; col < numColumns; col++) {
                    Object o = view.getTable().getValueAt(rowsSelected[i], col);
                    if (o!=null) {
                        s.append(o.toString());
                    }
                    s.append(",");
                }
                if (s.length()>0) {
                    s.setLength(s.length()-1);
                }
                sb.append(s.toString()).append("\n");
            }
        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }

    /**
     * カルテを開く。
     * @param value 対象患者
     */
    public void openKarte() {

        if (canOpen(getSelectedPatinet())) {

            // 来院情報を生成する
            PatientVisitModel pvt = new PatientVisitModel();
            pvt.setId(0L);
            pvt.setNumber(number++);
            pvt.setPatientModel(getSelectedPatinet());
            
            //--------------------------------------------------------
            // 受け付けを通していないのでログイン情報及び設定ファイルを使用する
            // 診療科名、診療科コード、医師名、医師コード、JMARI
            // 2.0
            //---------------------------------------------------------
            pvt.setDeptName(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
            pvt.setDeptCode(Project.getUserModel().getDepartmentModel().getDepartment());
            pvt.setDoctorName(Project.getUserModel().getCommonName());
            if (Project.getUserModel().getOrcaId()!=null) {
                pvt.setDoctorId(Project.getUserModel().getOrcaId());
            } else {
                pvt.setDoctorId(Project.getUserModel().getUserId());
            }
            pvt.setJmariNumber(Project.getString(Project.JMARI_CODE));
            
            // 来院日
            pvt.setFacilityId(Project.getUserModel().getFacilityModel().getFacilityId());
            pvt.setPvtDate(ModelUtils.getDateTimeAsString(new Date()));

            // カルテコンテナを生成する
            getContext().openKarte(pvt);
        }
    }

    // EVT から
    private void doStartProgress() {
        view.getCountLbl().setText(" 件");
        getContext().getProgressBar().setIndeterminate(true);
        getContext().getGlassPane().block();
        keyBlocker.block();
    }

    // EVT から
    private void doStopProgress() {
        getContext().getProgressBar().setIndeterminate(false);
        getContext().getProgressBar().setValue(0);
        getContext().getGlassPane().unblock();
        keyBlocker.unblock();
    }

    /**
     * リストで選択された患者を受付に登録する。
     */
    public void addAsPvt() {

        // 来院情報を生成する
        PatientVisitModel pvt = new PatientVisitModel();
        pvt.setId(0L);
        pvt.setNumber(number++);
        pvt.setPatientModel(getSelectedPatinet());

        //--------------------------------------------------------
        // 受け付けを通していないのでログイン情報及び設定ファイルを使用する
        // 診療科名、診療科コード、医師名、医師コード、JMARI
        // 2.0
        //---------------------------------------------------------
        pvt.setDeptName(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
        pvt.setDeptCode(Project.getUserModel().getDepartmentModel().getDepartment());
        pvt.setDoctorName(Project.getUserModel().getCommonName());
        if (Project.getUserModel().getOrcaId()!=null) {
            pvt.setDoctorId(Project.getUserModel().getOrcaId());
        } else {
            pvt.setDoctorId(Project.getUserModel().getUserId());
        }
        pvt.setJmariNumber(Project.getString(Project.JMARI_CODE));

        // 来院日
        pvt.setPvtDate(ModelUtils.getDateTimeAsString(new Date()));
//        GregorianCalendar gc = new GregorianCalendar();
//        gc.add(Calendar.DAY_OF_MONTH,1);
//        pvt.setPvtDate(ModelUtils.getDateTimeAsString(gc.getTime()));

        final PatientVisitModel fPvt = pvt;

        SimpleWorker worker = new SimpleWorker<Void, Void>() {
            
            @Override
            protected Void doInBackground() throws Exception {
                PVTDelegater1 pdl = new PVTDelegater1();
                pdl.addPvt(fPvt);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
            }

            @Override
            protected void failed(Throwable cause) {
            }

            @Override
            protected void startProgress() {
                doStartProgress();
            }

            @Override
            protected void stopProgress() {
                doStopProgress();
            }
        };

        worker.execute();
    }


    /**
     * 検索を実行する。
     * @param text キーワード
     */
    private void find(String text) {
        
        // 全角スペースをkill
        text = text.replaceAll("　", " ");

        PatientSearchSpec spec = new PatientSearchSpec();

        if (isDate(text)) {
            spec.setCode(PatientSearchSpec.DATE_SEARCH);
            spec.setDigit(text);

        } else if (StringTool.startsWithKatakana(text)) {
            spec.setCode(PatientSearchSpec.KANA_SEARCH);
            spec.setName(text);

        } else if (StringTool.startsWithHiragana(text)) {
            text = StringTool.hiraganaToKatakana(text);
            spec.setCode(PatientSearchSpec.KANA_SEARCH);
            spec.setName(text);

        } else if (isNameAddress(text)) {
            spec.setCode(PatientSearchSpec.NAME_SEARCH);
            spec.setName(text);

        } else {

            if (Project.getBoolean("zero.paddings.id.search", false)) {
                int len = text.length();
                int paddings = Project.getInt("patient.id.length", 0) - len;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < paddings; i++) {
                    sb.append("0");
                }
                sb.append(text);
                text = sb.toString();
            }
            
            spec.setCode(PatientSearchSpec.DIGIT_SEARCH);
            spec.setDigit(text);
        }

        final PatientSearchSpec searchSpec = spec;

        SimpleWorker worker = new SimpleWorker<Collection, Void>() {

            @Override
            protected Collection doInBackground() throws Exception {
                PatientDelegater pdl = new PatientDelegater();
                Collection result = pdl.getPatients(searchSpec);
                return result;
            }

            @Override
            protected void succeeded(Collection result) {
                
                List<PatientModel> list = (List<PatientModel>) result;
                
                if (list != null && list.size() > 0) {
                    
//minagawa^ 仮保存カルテ取得対応
                    /*
                    boolean sorted = true;
                    for (int i=0; i < COLUMN_NAMES.length; i++) {
                        if (sorter.getSortingStatus(i)==0) {
                            sorted = false;
                            break;
                        }
                    }
                    
                    if (!sorted) {

                        switch (sortItem) {
                            case 0:
                                Comparator c = new Comparator<PatientModel>() {

                                    @Override
                                    public int compare(PatientModel o1, PatientModel o2) {
                                        return o1.getPatientId().compareTo(o2.getPatientId());
                                    }
                                };
                                Collections.sort(list, c);
                                break;
                            case 1:
                              Comparator c2 = new Comparator<PatientModel>() {

                                @Override
                                 public int compare(PatientModel p1, PatientModel p2) {
                                    String kana1 = p1.getKanaName();
                                    String kana2 = p2.getKanaName();
                                    if (kana1 != null && kana2 != null) {
                                        return p1.getKanaName().compareTo(p2.getKanaName());
                                    } else if (kana1 != null && kana2 == null) {
                                        return -1;
                                    } else if (kana1 == null && kana2 != null) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                              };
                            Collections.sort(list, c2);
                            break;
                        }
                    }
                    */
                    sortList(list);
//minagawa$
                    
                    tableModel.setDataProvider(list);
                } else {
                    tableModel.clear();
                }
                updateStatusLabel();
            }

            @Override
            protected void failed(Throwable cause) {
            }

            @Override
            protected void startProgress() {
                doStartProgress();
            }

            @Override
            protected void stopProgress() {
                doStopProgress();
            }
        };

        worker.execute();
    }
    
//minagawa^ 仮保存カルテ取得対応
    public void getTmpKarte() {
        
        SimpleWorker worker = new SimpleWorker<List, Void>() {

            @Override
            protected List doInBackground() throws Exception {
                PatientDelegater pdl = new PatientDelegater();
                List result = pdl.getTmpKarte();
                return result;
            }

            @Override
            protected void succeeded(List result) {
                if (result != null && result.size()>0) {
                    sortList(result);
                    tableModel.setDataProvider(result);
                } else {
                    tableModel.clear();
                }
                updateStatusLabel();
            }

            @Override
            protected void failed(Throwable cause) {
            }

            @Override
            protected void startProgress() {
                doStartProgress();
            }

            @Override
            protected void stopProgress() {
                doStopProgress();
            }
        };

        worker.execute();
    }
    
    private void sortList(List<PatientModel> list) {
                 
        boolean sorted = true;
        for (int i=0; i < COLUMN_NAMES.length; i++) {
            if (sorter.getSortingStatus(i)==0) {
                sorted = false;
                break;
            }
        }

        if (!sorted) {

            switch (sortItem) {
                case 0:
                    Comparator c = new Comparator<PatientModel>() {

                        @Override
                        public int compare(PatientModel o1, PatientModel o2) {
                            return o1.getPatientId().compareTo(o2.getPatientId());
                        }
                    };
                    Collections.sort(list, c);
                    break;
                case 1:
                  Comparator c2 = new Comparator<PatientModel>() {

                    @Override
                     public int compare(PatientModel p1, PatientModel p2) {
                        String kana1 = p1.getKanaName();
                        String kana2 = p2.getKanaName();
                        if (kana1 != null && kana2 != null) {
                            return p1.getKanaName().compareTo(p2.getKanaName());
                        } else if (kana1 != null && kana2 == null) {
                            return -1;
                        } else if (kana1 == null && kana2 != null) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                  };
                Collections.sort(list, c2);
                break;
            }
        }
    }
//minagawa$

    private boolean isDate(String text) {
        boolean maybe = false;
        if (text != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.parse(text);
                maybe = true;

            } catch (Exception e) {
            }
        }

        return maybe;
    }

    private boolean isKana(String text) {
        boolean maybe = true;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (!StringTool.isKatakana(c)) {
                    maybe = false;
                    break;
                }
            }
            return maybe;
        }

        return false;
    }

    private boolean isNameAddress(String text) {
        boolean maybe = false;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.getType(c) == Character.OTHER_LETTER) {
                    maybe = true;
                    break;
                }
            }
        }
        return maybe;
    }

    private boolean isId(String text) {
        boolean maybe = true;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                int type = Character.getType(c);
                if (type == Character.UPPERCASE_LETTER ||
                        type == Character.LOWERCASE_LETTER ||
                        type == Character.DECIMAL_DIGIT_NUMBER) {
                    continue;
                } else {
                    maybe = false;
                    break;
                }
            }
            return maybe;
        }
        return false;
    }

    private boolean isTelephoneZip(String text) {
        boolean maybe = true;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                int type = Character.getType(c);
                if (type == Character.DECIMAL_DIGIT_NUMBER ||
                        c == '-' ||
                        c == '(' ||
                        c == ')') {
                    continue;
                } else {
                    maybe = false;
                    break;
                }
            }
            return maybe;
        }
        return false;
    }
    
//masuda^
    // ステータスラベルに検索件数を表示
    private void updateStatusLabel() {
        int count = tableModel.getObjectCount();
        String msg = String.valueOf(count) + "件";
        //this.getContext().getStatusLabel().setText(msg);
        view.getCountLbl().setText(msg);
    }

    /**
     * テキストフィールドへ日付を入力するためのカレンダーポップアップメニュークラス。
     */
    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        /** ポップアップメニュー */
        private JPopupMenu popup;
        /** ターゲットのテキストフィールド */
        private JTextField tf;

        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(PopupListener.this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[]{-12, 0});
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                tf.setText(SimpleDate.simpleDateToMmldate(sd));
                popup.setVisible(false);
                popup = null;
                String test = tf.getText().trim();
                if (!test.equals("")) {
                    find(test);
                }
            }
        }
    }
    
//masuda$
    // ChartEventListener
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        
        if (tableModel==null) {
            return;
        }
        
        ChartEventModel evt = (ChartEventModel)pce.getNewValue();
        
        int sRow = -1;
        long ptPk = evt.getPtPk();
        List<PatientModel> list = tableModel.getDataProvider();
        
//minagawa^        
        //ChartEventModel.EVENT eventType = evt.getEventType();
        int eventType =  evt.getEventType();
//minagawa$        
        
        switch (eventType) {
            case ChartEventModel.PVT_STATE:
                for (int row = 0; row < list.size(); ++row) {
                    PatientModel pm = list.get(row);
                    if (ptPk == pm.getId()) {
                        sRow = row;
                        pm.setOwnerUUID(evt.getOwnerUUID());
                        break;
                    }
                }
                break;
            case ChartEventModel.PM_MERGE:
                for (int row = 0; row < list.size(); ++row) {
                    PatientModel pm = list.get(row);
                    if (ptPk == pm.getId()) {
                        sRow = row;
                        //pm = msg.getPatientModel();
                        list.set(row, evt.getPatientModel());
                        break;
                    }
                }
                break;            
            case ChartEventModel.PVT_MERGE:
                for (int row = 0; row < list.size(); ++row) {
                    PatientModel pm = list.get(row);
                    if (ptPk == pm.getId()) {
                        sRow = row;
                        //pm = msg.getPatientVisitModel().getPatientModel();
                        list.set(row, evt.getPatientVisitModel().getPatientModel());
                        break;
                    }
                }
                break;
            default:
                break;
        }
        
        if (sRow != -1) {
            tableModel.fireTableRowsUpdated(sRow, sRow);
        }
    }
    
//    public class PatientListTableRenderer extends OddEvenRowRenderer {
//
//        public PatientListTableRenderer() {
//            super();
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table,
//                Object value,
//                boolean isSelected,
//                boolean isFocused,
//                int row, int col) {
//
//            super.getTableCellRendererComponent(table, value, isSelected, isFocused, row, col);
//            
//            //PatientModel pm = (PatientModel)tableModel.getObject(row);
//            PatientModel pm = (PatientModel) sorter.getObject(row);
//            
//            if (isSelected) {
//                setBackground(table.getSelectionBackground());
//                setForeground(table.getSelectionForeground());
//
//            } else {
//
//                setForeground(table.getForeground());
//
//                if ((row & (1)) == 0) {
//                    setBackground(getEvenColor());
//                } else {
//                    setBackground(getOddColor());
//                }
//            }    
//            
//            if (pm != null && col == stateColumn) {
//                setHorizontalAlignment(JLabel.CENTER);
//                if (pm.isOpened()) {
//                    if (clientUUID.equals(pm.getOwnerUUID())) {
//                        setIcon(WatingListImpl.OPEN_ICON);
//                    } else {
//                        setIcon(WatingListImpl.NETWORK_ICON);
//                    }
//                } else {
//                    setIcon(null);
//                }
//                setText("");
//            } else {
//                setHorizontalAlignment(JLabel.LEFT);
//                setIcon(null);
//                setText(value == null ? "" : value.toString());
//            }
//
//            return this;
//        }
//    }
    
        
    private class PatientListTableRenderer extends StripeTableCellRenderer {

        public PatientListTableRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, isFocused, row, col);
            
            PatientModel pm = (PatientModel) sorter.getObject(row);
//minagawa^ lsctest            
            boolean bStateColumn = (view.getTable().getColumnModel().getColumn(col).getIdentifier()!=null &&
                                    view.getTable().getColumnModel().getColumn(col).getIdentifier().equals(COLUMN_IDENTIFIER_STATE));
            
            //if (pm != null && col == stateColumn) {     
            if (pm != null && bStateColumn) {
//minagawa$                
                setHorizontalAlignment(JLabel.CENTER);
                if (pm.isOpened()) {
                    if (clientUUID.equals(pm.getOwnerUUID())) {
                        setIcon(WatingListImpl.OPEN_ICON);
                    } else {
                        setIcon(WatingListImpl.NETWORK_ICON);
                    }
                } else {
                    setIcon(null);
                }
                setText("");
            } else {
                setHorizontalAlignment(JLabel.LEFT);
                setIcon(null);
                setText(value == null ? "" : value.toString());
            }

            return this;
        }
    }

//masuda$    
}