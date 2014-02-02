package open.dolphin.impl.pvt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import open.dolphin.client.*;
import open.dolphin.delegater.PVTDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;
import open.dolphin.table.ColumnSpec;
import open.dolphin.table.ListTableModel;
import open.dolphin.util.AgeCalculater;

/**
 * 受付リスト。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class WatingListImpl extends AbstractMainComponent {

    // Window Title
    private static final String NAME = "受付リスト";

    // 担当分のみを表示するかどうかの preference key
    private static final String ASSIGNED_ONLY = "assignedOnly";

    // 修正送信アイコンの配列インデックス
    private static final int INDEX_MODIFY_SEND_ICON = 1;

    // 担当医未定の ORCA 医師ID
    private static final String UN_ASSIGNED_ID = "18080";
    
    // JTableレンダラ用の男性カラー  ClientContext.getColor("watingList.color.male")
    private static final Color MALE_COLOR = new Color(237,243,254);
    
    // JTableレンダラ用の女性カラー ClientContext.getColor("watingList.color.female")
    private static final Color FEMALE_COLOR = new Color(254,221,242);
    
    // JTableレンダラ用の奇数カラー
    private static final Color ODD_COLOR = ClientContext.getColor("color.odd");
    
    // JTableレンダラ用の偶数カラー 
    private static final Color EVEN_COLOR = ClientContext.getColor("color.even");
    
    // 受付キャンセルカラー ClientContext.getColor("watingList.color.pvtCancel")
    private static final Color CANCEL_PVT_COLOR = new Color(128,128,128);

    // 来院テーブルのカラム名
    private static final String[] COLUMN_NAMES = new String[]{"患者ID","来院時間","氏   名","性別","保険","生年月日","担当医","診療科","予約","メモ", "状態"};

    // 来院テーブルのカラムメソッド
    private static final String[] PROPERTY_NAMES = new String[]{
        "getPatientId","getPvtDateTrimDate","getPatientName","getPatientGenderDesc", "getFirstInsurance",
        "getPatientAgeBirthday","getDoctorName", "getDeptName","getAppointment","getMemo","getStateInteger"};

    // 来院テーブルのクラス名
    private static final Class[] COLUMN_CLASSES = new Class[]{
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Integer.class};

    // 来院テーブルのカラム幅
    private static final int[] COLUMN_WIDTH = new int[]{80, 60, 100, 40, 130, 130, 50, 60, 40, 80, 30};
    
    // 年齢生年月日メソッド 
    private final String[] AGE_METHOD = new String[]{"getPatientAgeBirthday", "getPatientBirthday"};

    // 来院テーブルの開始行数
    private static final int START_NUM_ROWS = 30;
    
    // カラム仕様リスト
    private List<ColumnSpec> columnSpecs;

    private int visitedTimeColumn;

    private int sexColumn;

    // 年齢表示カラム
    private int ageColumn;

    // 来院情報テーブルのメモカラム
    private int memoColumn;

    // 来院情報テーブルのステータスカラム
    private int stateColumn;
    
    // デフォルトのチェック間隔 
    private int CHECK_INTERVAL = 30; // デフォルト値
    
    // PVT Table 
    private JTable pvtTable;

    // Table Model
    private ListTableModel<PatientVisitModel> pvtTableModel;
    
    // 性別レンダラフラグ 
    private boolean sexRenderer;
    
    // 年齢表示 
    private boolean ageDisplay;
    
    // チェック間隔
    private int checkInterval;
    
    // 選択されている患者情報 
    private PatientVisitModel selectedPvt;

    // 選択されている行を保存
    private int saveSelectedIndex;

    // ScheduledExecutorService
    private ScheduledExecutorService schedule;

    // ScheduledFuture
    private ScheduledFuture timerHandler;

    // 来院情報をチェックするハンドラ
    private PvtChecker pvtChecker;

    // View class
    private WatingListView view;

    // 更新時刻フォーマッタ
    private SimpleDateFormat timeFormatter;

    // Chart State
    private Integer[] chartBitArray = {
        new Integer(ChartImpl.BIT_OPEN), new Integer(ChartImpl.BIT_MODIFY_CLAIM), new Integer(ChartImpl.BIT_SAVE_CLAIM)};

    // Chart State を表示するアイコン
    private ImageIcon[] chartIconArray = {
        ClientContext.getImageIcon("open_16.gif"),ClientContext.getImageIcon("sinfo_16.gif"),ClientContext.getImageIcon("flag_16.gif")};

    // State ComboBox
    private Integer[] userBitArray = {
        new Integer(0), new Integer(3), new Integer(4), new Integer(5), new Integer(6)};
    private ImageIcon[] userIconArray = {
        null, ClientContext.getImageIcon("apps_16.gif"), ClientContext.getImageIcon("fastf_16.gif"), ClientContext.getImageIcon("cart_16.gif"), ClientContext.getImageIcon("cancl_16.gif")};

    private ImageIcon modifySendIcon;

    // キャンセルビット
    private final int bitCancel = 6;

    // State 設定用のcombobox model
    private BitAndIconPair[] stateComboArray;

    // State 設定用のcombobox
    private JComboBox stateCmb;

    private AbstractAction copyAction;
    
    private boolean DEBUG;
    
    
    /**  Creates new WatingList */
    public WatingListImpl() {
        setName(NAME);
    }
    
    private void setup() {

        // pvtTable deafult
        String defaultLine;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            String name = COLUMN_NAMES[i];
            String method = PROPERTY_NAMES[i];
            String cls = COLUMN_CLASSES[i].getName();
            String width = String.valueOf(COLUMN_WIDTH[i]);
            sb.append(name).append(",");
            sb.append(method).append(",");
            sb.append(cls).append(",");
            sb.append(width).append(",");
        }
        sb.setLength(sb.length()-1);
        defaultLine = sb.toString();

        // preference から
        String line = Project.getString("pvtTable.column.spec", defaultLine);

        // 仕様を保存
        columnSpecs = new ArrayList<ColumnSpec>();
        String[] params = line.split(",");
        int len = params.length / 4;
        for (int i = 0; i < len; i++) {
            int k = 4*i;
            String name = params[k];
            String method = params[k+1];
            String cls = params[k+2];
            int width = Integer.parseInt(params[k+3]);
            ColumnSpec cp = new ColumnSpec(name, method, cls, width);
            columnSpecs.add(cp);
        }

        // Scan して age, memo, state カラムを設定する
        for (int i = 0; i < columnSpecs.size(); i++) {
            ColumnSpec cs = columnSpecs.get(i);
            String test = cs.getMethod();
            
            if (test.equals("getPvtDateTrimDate")) {
                visitedTimeColumn = i;
                
            } else if(test.equals("getPatientGenderDesc")) {
                sexColumn = i;

            } else if(test.endsWith("Birthday")) {
                ageColumn = i;

            } else if (test.equals("getMemo")) {
                memoColumn = i;

            } else if (test.equals("getStateInteger")) {
                stateColumn = i;
            }
        }

        // 修正送信アイコンを決める
        if (Project.getBoolean("change.icon.modify.send", true)) {
            modifySendIcon = ClientContext.getImageIcon("sinfo_16.gif");
        } else {
            modifySendIcon = ClientContext.getImageIcon("flag_16.gif");
        }
        chartIconArray[INDEX_MODIFY_SEND_ICON] = modifySendIcon;

        stateComboArray = new BitAndIconPair[userBitArray.length];
        for (int i = 0; i < userBitArray.length; i++) {
            stateComboArray[i] = new BitAndIconPair(userBitArray[i], userIconArray[i]);
        }
        stateCmb = new JComboBox(stateComboArray);
        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(30, ClientContext.getHigherRowHeight()));
        stateCmb.setRenderer(renderer);
        stateCmb.setMaximumRowCount(userBitArray.length);

        sexRenderer = Project.getBoolean("sexRenderer", false);
        ageDisplay = Project.getBoolean("ageDisplay", true);
        checkInterval = Project.getInt("checkInterval", CHECK_INTERVAL);
        timeFormatter = new SimpleDateFormat("HH:mm");
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        setup();
        initComponents();
        connect();
        restartCheckTimer(0);
    }
    
    /**
     * メインウインドウのタブで受付リストに切り替わった時
     * コールされる。
     */
    @Override
    public void enter() {
        controlMenu();
        getContext().getStatusLabel().setText(constractStatusInfo());
    }
    
    /**
     * プログラムを終了する。
     */
    @Override
    public void stop() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnSpecs.size(); i++) {
            ColumnSpec cs = columnSpecs.get(i);
            cs.setWidth(pvtTable.getColumnModel().getColumn(i).getPreferredWidth());
            sb.append(cs.getName()).append(",");
            sb.append(cs.getMethod()).append(",");
            sb.append(cs.getCls()).append(",");
            sb.append(cs.getWidth()).append(",");
        }
        sb.setLength(sb.length()-1);
        String line = sb.toString();
        Project.setString("pvtTable.column.spec", line);
    }
    
    /**
     * 選択されている来院情報の患者オブジェクトを返す。
     * @return 患者オブジェクト
     */
    public PatientModel getPatinet() {
        return selectedPvt != null ? selectedPvt.getPatientModel() : null;
    }
    
    /**
     * 性別レンダラかどうかを返す。
     * @return 性別レンダラの時 true
     */
    public boolean isSexRenderer() {
        return sexRenderer;
    }
    
    /**
     * レンダラをトグルで切り替える。
     */
    public void switchRenderere() {
        sexRenderer = !sexRenderer;
        Project.setBoolean("sexRenderer", sexRenderer);
        if (pvtTable != null) {
            pvtTableModel.fireTableDataChanged();
        }
    }
    
    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
        if (pvtTable != null) {
            ageDisplay = !ageDisplay;
            Project.setBoolean("ageDisplay", ageDisplay);
            String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
            pvtTableModel.setProperty(method, ageColumn);
            for (int i = 0; i < columnSpecs.size(); i++) {
                ColumnSpec cs = columnSpecs.get(i);
                String test = cs.getMethod();
                if (test.toLowerCase().endsWith("birthday")) {
                    cs.setMethod(method);
                    break;
                }
            }
        }
    }

    public void updatePvtInfo(Date date, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(timeFormatter.format(date));
        sb.append("　来院数: ");
        sb.append(count);
        view.getPvtInfoLbl().setText(sb.toString());
    }

    /**
     * テーブル及び靴アイコンの enable/diable 制御を行う。
     * @param busy pvt 検索中は true
     */
    public void setBusy(boolean busy) {
        
        if (busy) {
            view.getKutuBtn().setEnabled(false);
            if (getContext().getCurrentComponent() == getUI()) {
                //getContext().block();
                getContext().getProgressBar().setIndeterminate(true);
            }
            saveSelectedIndex = pvtTable.getSelectedRow();
        } else {
            view.getKutuBtn().setEnabled(true);
            if (getContext().getCurrentComponent() == getUI()) {
                //getContext().unblock();
                getContext().getProgressBar().setIndeterminate(false);
                getContext().getProgressBar().setValue(0);
            }
            pvtTable.getSelectionModel().addSelectionInterval(saveSelectedIndex, saveSelectedIndex);
        }
    }
    
    /**
     * 選択されている来院情報を設定返す。
     * @return 選択されている来院情報
     */
    public PatientVisitModel getSelectedPvt() {
        return selectedPvt;
    }
    
    /**
     * 選択された来院情報を設定する。
     * @param 選択された来院情報
     */
    public void setSelectedPvt(PatientVisitModel selectedPvt) {
        PatientVisitModel old = this.selectedPvt;
        this.selectedPvt = selectedPvt;
        controlMenu();
    }
      
    /**
     * チェックタイマーをリスタートする。
     */
    public void restartCheckTimer(int delay) {
        
        if (timerHandler != null && (!timerHandler.isCancelled())) {
            timerHandler.cancel(true);
            boolean cancelled = timerHandler.isCancelled();
            ClientContext.getBootLogger().debug("timerHandler isCancelled = " + cancelled);
            if (!cancelled) {
                return;
            }
        }
        
        if (pvtChecker == null) {
            pvtChecker = new PvtChecker();
        }

        if (schedule == null) {
            schedule = Executors.newSingleThreadScheduledExecutor();
        }
        
        timerHandler = schedule.scheduleWithFixedDelay(pvtChecker, delay, checkInterval, TimeUnit.SECONDS);
    }
    
    /**
     * カルテオープンメニューを制御する。
     */
    private void controlMenu() {
        PatientVisitModel pvt = getSelectedPvt();
        boolean enabled = canOpen(pvt);
        getContext().enabledAction(GUIConst.ACTION_OPEN_KARTE, enabled);
    }
    
    /**
     * GUI コンポーネントを初期化しレアイアウトする。
     */
    private void initComponents() {

        // View クラスを生成しこのプラグインの UI とする
        view = new WatingListView();
        setUI(view);

        view.getPvtInfoLbl().setText("");
        
        //------------------------------------------
        // View のテーブルモデルを置き換える
        //------------------------------------------
        int len = columnSpecs.size();
        String[] colunNames = new String[len];
        String[] methods = new String[len];
        Class[] cls = new Class[len];
        int[] width = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                ColumnSpec cp = columnSpecs.get(i);
                colunNames[i] = cp.getName();
                methods[i] = cp.getMethod();
                cls[i] = Class.forName(cp.getCls());
                width[i] = cp.getWidth();
            }
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
        pvtTable = view.getTable();
        pvtTableModel = new ListTableModel<PatientVisitModel>(colunNames, START_NUM_ROWS, methods, cls) {

            @Override
            public boolean isCellEditable(int row, int col) {

                boolean canEdit = true;

                // メモか状態カラムの場合
                canEdit = canEdit && ((col==memoColumn) || (col==stateColumn));

                // null でない場合
                canEdit = canEdit && (getObject(row)!=null);

                if (!canEdit) {
                    return false;
                }

                // statusをチェックする
                PatientVisitModel pm = getObject(row);
                int state = pm.getState();

                if ((state & (1<<bitCancel))!=0) {
                    // cancel case
                    canEdit = false;

                } else {
                    // Chartビットがたっている場合は不可
                    for (int i = 0; i < chartBitArray.length; i++) {
                        if ((state & (1<<chartBitArray[i]))!=0) {
                            canEdit = false;
                            break;
                        }
                    }
                }

                return canEdit;
            }

            @Override
            public Object getValueAt(int row, int col) {

                Object ret = null;

                if (col == ageColumn && ageDisplay) {

                    PatientVisitModel p = getObject(row);

                    if (p != null) {
                        int showMonth = Project.getInt("ageToNeedMonth", 6);
                        ret = AgeCalculater.getAgeAndBirthday(p.getPatientModel().getBirthday(), showMonth);
                    }
                } else {

                    ret = super.getValueAt(row, col);
                }

                return ret;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {

                final PatientVisitModel target = getObject(row);
                if (target==null || value==null) {
                    return;
                }

                // Memo
                if (col == memoColumn) {
                    String memo = ((String)value).trim();
                    doUpdateMemoTask(target, memo);

                } else if (col == stateColumn) {

                    // State ComboBox の value
                    BitAndIconPair pair = (BitAndIconPair)value;
                    int theBit = pair.getBit().intValue();

                    if (theBit == bitCancel) {

                        Object[] cstOptions = new Object[]{"はい", "いいえ"};

                        StringBuilder sb = new StringBuilder(target.getPatientName());
                        sb.append("様の受付を取り消しますか?");
                        String msg = sb.toString();

                        int select = JOptionPane.showOptionDialog(
                                SwingUtilities.getWindowAncestor(pvtTable),
                                msg,
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                ClientContext.getImageIcon("cancl_32.gif"),
                                cstOptions,"はい");

                        System.err.println("select=" + select);

                        if (select != 0) {
                            return;
                        }
                    }

                    // unset all
                    int state = 0;

                    // set the bit
                    if (theBit!=0) {
                        state = state | (1<<theBit);
                    }
                    
                    doUpdateStateTask(target, state);
                }
            }
        };
        
        pvtTable.setModel(pvtTableModel);
        
        // 選択モード
        pvtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 行高
        if (ClientContext.isWin()) {
            pvtTable.setRowHeight(ClientContext.getMoreHigherRowHeight());
        } else {
            pvtTable.setRowHeight(ClientContext.getHigherRowHeight());
        }

        // カラム幅
        for (int i = 0; i < width.length; i++) {
            pvtTable.getColumnModel().getColumn(i).setPreferredWidth(width[i]);
        }

        // Memo 欄 clickCountToStart=1
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoKanjiListener.getInstance());
        DefaultCellEditor de = new DefaultCellEditor(tf);
        de.setClickCountToStart(1);
        pvtTable.getColumnModel().getColumn(memoColumn).setCellEditor(de);
        
        // 性別レンダラを生成する
        MaleFemaleRenderer sRenderer = new MaleFemaleRenderer();
        // Center Renderer
        CenterRenderer centerRenderer = new CenterRenderer();

        for (int i = 0; i < columnSpecs.size(); i++) {

            if (i == visitedTimeColumn || i == sexColumn) {
                pvtTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

            } else if(i == stateColumn) {
                // カルテ(PVT)状態レンダラ
                KarteStateRenderer renderer = new KarteStateRenderer();
                renderer.setHorizontalAlignment(JLabel.CENTER);
                pvtTable.getColumnModel().getColumn(i).setCellRenderer(renderer);

            } else {
                pvtTable.getColumnModel().getColumn(i).setCellRenderer(sRenderer);
            }
        }

        // PVT状態設定エディタ
        pvtTable.getColumnModel().getColumn(stateColumn).setCellEditor(new DefaultCellEditor(stateCmb));
        
        // チェック間隔情報を設定する
        getContext().getStatusLabel().setText(constractStatusInfo());

        // 担当分のみを表示するかどうかにチェックする
        //view.getAssignedMeChk().setSelected(Project.getBoolean(FILTER_ASSIGNED_FOR_ME, false));
    }
    
    /**
     * コンポーネントにイベントハンドラーを登録し相互に接続する。
     */
    private void connect() {

        // pvtTableModel のカラム変更関連イベント
        pvtTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent tcme) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent tcme) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent tcme) {
                int from = tcme.getFromIndex();
                int to = tcme.getToIndex();
                ColumnSpec moved = columnSpecs.remove(from);
                columnSpecs.add(to, moved);
            }

            @Override
            public void columnMarginChanged(ChangeEvent ce) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent lse) {
            }
        });
        
        // Chart のリスナになる
        // 患者カルテの Open/Save/SaveTemp の通知を受けて受付リストの表示を制御する
        ChartImpl.addPropertyChangeListener(ChartImpl.CHART_STATE, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ChartImpl.CHART_STATE)) {
                    findAndUpdateState((PatientVisitModel) evt.getNewValue());
                }
            }
        });

        // 来院リストテーブル 選択
        pvtTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    PatientVisitModel patient = pvtTableModel.getObject(pvtTable.getSelectedRow());
                    setSelectedPvt(patient);
                }
            }
        });

        // 来院リストテーブル ダブルクリック
        view.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    PatientVisitModel patient = pvtTableModel.getObject(pvtTable.getSelectedRow());
                    openKarte(patient);
                }
            }
        });

        // コンテキストメニューを登録する
        view.getTable().addMouseListener(new ContextListener());
        
        // 靴のアイコンをクリックした時来院情報を検索する
        view.getKutuBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                checkPVT(true);
            }
        });

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow() ;
            }
        };
        pvtTable.getInputMap().put(copy, "Copy");
        pvtTable.getActionMap().put("Copy", copyAction);
    }
    
    public void openKarte(PatientVisitModel pvtModel) {
        
        if (pvtModel != null && canOpen(pvtModel)) {
            getContext().openKarte(pvtModel);

        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
        
    /**
     * チャートステートの状態をデータベースに書き込む。
     */
    public void findAndUpdateState(PatientVisitModel updated) {
        
        // 受付リストの状態カラムを更新する
        List<PatientVisitModel> pvtList = pvtTableModel.getDataProvider();

        if (pvtList==null || pvtList.isEmpty()) {
            return;
        }

        boolean found = false;

        for (PatientVisitModel test : pvtList) {

            if (test == updated) {
                found = true;
                pvtTableModel.fireTableDataChanged();
                break;
            }
        }

        if (found) {
            return;
        }

        for (PatientVisitModel test : pvtList) {
            if (test.getId() == updated.getId()) {
                if (test.getState()!=updated.getState()) {
                    test.setState(updated.getState());
                    pvtTableModel.fireTableDataChanged();
                }
                break;
            }
        }
    }

    /**
     * メモを書き込む
     * @param updated
     */
    private void doUpdateMemoTask(final PatientVisitModel updated, final String memo) {

        SwingWorker worker = new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground() throws Exception {
                PVTDelegater pdl = new PVTDelegater();
                int cnt = pdl.updateMemo(updated.getId(), memo);
                return new Integer(cnt);
            }

            @Override
            protected void done() {
                try {
                    Integer cnt = get();
                    System.err.println("updated cnt=" + cnt);
                    if (cnt.intValue()==1) {
                        updated.setMemo(memo);
                        pvtTableModel.fireTableDataChanged();
                    }
                } catch (InterruptedException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (ExecutionException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (Throwable t) {
                    ClientContext.getBootLogger().warn(t);
                }
            }
        };

        worker.execute();
    }

    /**
     * StateをDBへ書き込む。
     * @param updated
     */
    private void doUpdateStateTask(final PatientVisitModel updated, final int state) {

        SwingWorker worker = new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground() throws Exception {
                PVTDelegater pdl = new PVTDelegater();
                int cnt = pdl.updatePvtState(updated.getId(), state);
                return new Integer(cnt);
            }

            @Override
            protected void done() {
                try {
                    Integer i = get();
                    if (i.intValue()==1) {
                        updated.setState(state);
                        pvtTableModel.fireTableDataChanged();
                    }
                } catch (InterruptedException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (ExecutionException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (Throwable t) {
                    ClientContext.getBootLogger().warn(t);
                }
            }
        };

        worker.execute();
    }
    
    /**
     * カルテを開くことが可能かどうかを返す。
     * @return 開くことが可能な時 true
     */
    private boolean canOpen(PatientVisitModel pvt) {
        if (pvt == null) {
            return false;
        }
        if (isKarteOpened(pvt)) {
            return false;
        }
        if (isKarteCanceled(pvt)) {
            return false;
        }
        return true;
    }
    
    /**
     * カルテがオープンされているかどうかを返す。
     * @return オープンされている時 true
     */
    private boolean isKarteOpened(PatientVisitModel pvtModel) {
        if (pvtModel != null) {
            boolean opened = false;
            List<ChartImpl> allCharts = ChartImpl.getAllChart();
            for (ChartImpl chart : allCharts) {
                if (chart.getPatientVisit().getId() == pvtModel.getId()) {
                    opened = true;
                    break;
                }
            }
            return opened;
        }
        return false;
    }
    
    /**
     * 受付がキャンセルされているかどうかを返す。
     * @return キャンセルされている時 true
     */
    private boolean isKarteCanceled(PatientVisitModel pvtModel) {
        if (pvtModel != null) {
            if ((pvtModel.getState() & (1<<bitCancel)) != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isFilterPatients() {
        boolean filter = true;
        filter = filter && (Project.getUserModel().getOrcaId() != null);
        filter = filter && Project.getBoolean(ASSIGNED_ONLY, false);
        return filter;
    }

    private String constractStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("チェック間隔: ");
        sb.append(checkInterval);
        sb.append("秒");
        return sb.toString();
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
                String pop3 = "偶数奇数レンダラを使用する";
                String pop4 = "性別レンダラを使用する";
                String pop5 = "年齢表示";
                String pop6 = "担当分のみ表示";
                String pop7 = "修正送信を注意アイコンにする";
                
                int row = pvtTable.rowAtPoint(e.getPoint());
                Object obj = pvtTableModel.getObject(row);
                int selected = pvtTable.getSelectedRow();
                
                if (row == selected && obj != null) {
                    String pop1 = "カルテを開く";
                    contextMenu.add(new JMenuItem(new ReflectAction(pop1, WatingListImpl.this, "openKarte")));
                    contextMenu.addSeparator();
                    contextMenu.add(new JMenuItem(copyAction));
                    contextMenu.addSeparator();
                }
                
                JRadioButtonMenuItem oddEven = new JRadioButtonMenuItem(new ReflectAction(pop3, WatingListImpl.this, "switchRenderere"));
                JRadioButtonMenuItem sex = new JRadioButtonMenuItem(new ReflectAction(pop4, WatingListImpl.this, "switchRenderere"));
                ButtonGroup bg = new ButtonGroup();
                bg.add(oddEven);
                bg.add(sex);
                contextMenu.add(oddEven);
                contextMenu.add(sex);
                if (sexRenderer) {
                    sex.setSelected(true);
                } else {
                    oddEven.setSelected(true);
                }
                
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(pop5);
                contextMenu.add(item);
                item.setSelected(ageDisplay);
                item.addActionListener((ActionListener)EventHandler.create(ActionListener.class, WatingListImpl.this, "switchAgeDisplay"));

                // 担当分のみ表示: getOrcaId() != nullでメニュー
                if (Project.getUserModel().getOrcaId() != null) {
                    contextMenu.addSeparator();

                    // 担当分のみ表示
                    JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(pop6);
                    contextMenu.add(item2);
                    item2.setSelected(Project.getBoolean(ASSIGNED_ONLY, false));
                    item2.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            boolean now = Project.getBoolean(ASSIGNED_ONLY, false);
                            Project.setBoolean(ASSIGNED_ONLY, !now);
                            filterPatients();
                        }
                    });
                }

                // 修正送信を注意アイコンにする ON/OF default = ON
                JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(pop7);
                contextMenu.add(item3);
                item3.setSelected(Project.getBoolean("change.icon.modify.send", true));
                item3.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        boolean curIcon = Project.getBoolean("change.icon.modify.send", true);
                        boolean change = !curIcon;
                        Project.setBoolean("change.icon.modify.send", change);
                        changeModiSendIcon();
                    }
                });

                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    /**
     * 修正送信アイコンを決める
     * @param change
     */
    private void changeModiSendIcon() {

        // 修正送信アイコンを決める
        if (Project.getBoolean("change.icon.modify.send", true)) {
            modifySendIcon = ClientContext.getImageIcon("sinfo_16.gif");
        } else {
            modifySendIcon = ClientContext.getImageIcon("flag_16.gif");
        }
        chartIconArray[INDEX_MODIFY_SEND_ICON] = modifySendIcon;

        // 表示を更新する
        pvtTableModel.fireTableDataChanged();
    }
    
    /**
     * Popupメニューから選択されている患者のカルテを開く。
     */
    public void openKarte() {
        PatientVisitModel pvtModel = getSelectedPvt();
        if (canOpen(pvtModel)) {
            openKarte(pvtModel);
        }
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {

        StringBuilder sb = new StringBuilder();
        int numRows = pvtTable.getSelectedRowCount();
        int[] rowsSelected = pvtTable.getSelectedRows();
        int numColumns =   pvtTable.getColumnCount();

        for (int i = 0; i < numRows; i++) {
            if (pvtTableModel.getObject(rowsSelected[i]) != null) {
                StringBuilder s = new StringBuilder();
                for (int col = 0; col < numColumns; col++) {
                    Object o = pvtTable.getValueAt(rowsSelected[i], col);
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
     * 来院情報を検索する。
     */
    private void checkPVT(final boolean restTimer) {

        if (restTimer && timerHandler != null && (!timerHandler.isCancelled())) {
            timerHandler.cancel(true);
            boolean cancelled = timerHandler.isCancelled();
            ClientContext.getBootLogger().debug("timerHandler isCancelled = " + cancelled);
            if (!cancelled) {
                return;
            }
        }

        final SwingWorker worker = new SwingWorker<List<PatientVisitModel>, Void>() {

            List<PatientVisitModel> dataList;
            private int curSize;
            private Date date;

            @Override
            protected List<PatientVisitModel> doInBackground() throws Exception {

                PVTDelegater pdl = new PVTDelegater();
                List<PatientVisitModel> result;

                date = new Date();
                String[] dateToSerach = getSearchDateAsString(date);

                // 現在の件数を保存しておく
                dataList = pvtTableModel.getDataProvider();
                curSize = dataList != null ? dataList.size() : 0;

                // 全件検索する
                if (isFilterPatients()) {
                    result = (List<PatientVisitModel>)pdl.getPvtForAssigned(Project.getUserModel().getOrcaId(), UN_ASSIGNED_ID, dateToSerach, 0);  //0
                } else {
                    result = (List<PatientVisitModel>)pdl.getPvt(dateToSerach, 0);  //0
                }
                
                if (DEBUG) {
                    trace("------------------------------------");
                    if (result!=null) {
                        trace("shose: result size=" + result.size());
                        for (int i = 0; i < result.size(); i++) {
                            PatientVisitModel pvt = result.get(i);
                            StringBuilder sb = new StringBuilder();
                            sb.append(pvt.getPatientId()).append("=").append(pvt.getState());
                            trace(sb.toString());
                        }
                    } else {
                        trace("result=null");
                    }
                }

                return result;
            }

            @Override
            protected void done() {
                try {

                    List<PatientVisitModel> result = get();
                    int size = result.size();

                    if (size==0) {
                        return;
                    }

                    // curSize までは status を更新する
                    for (int i = 0; i < curSize; i++) {
                        PatientVisitModel pvt = result.get(i);
                        for (PatientVisitModel cur : dataList) {
                            if (pvt.getId()==cur.getId()) {
                                cur.setState(pvt.getState());
                                cur.setMemo(pvt.getMemo());
                                break;
                            }
                        }
                    }

                    // 新規分を追加する
                    for (int i = curSize; i < size; i++) {
                         dataList.add(result.get(i));
                    }

                    // 通知する
                    pvtTableModel.fireTableDataChanged();

                    // 表示を更新する
                    updatePvtInfo(date, pvtTableModel.getObjectCount());

                } catch (InterruptedException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (ExecutionException ex) {
                    ClientContext.getBootLogger().warn(ex);
                }
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                        setBusy(false);
                        if (restTimer && schedule != null) {
                            restartCheckTimer(checkInterval);
                        }
                        worker.removePropertyChangeListener(this);
                    } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                        setBusy(true);
                    }
                }
            }
        });

        worker.execute();
    }

    /**
     * 担当分のみを表示するかどうかのフィルタリングを行う。
     * @param b
     */
    private void filterPatients() {

        if (timerHandler != null && (!timerHandler.isCancelled())) {
            timerHandler.cancel(true);
            boolean cancelled = timerHandler.isCancelled();
            ClientContext.getBootLogger().debug("timerHandler isCancelled = " + cancelled);
            if (!cancelled) {
                return;
            }
        }

        final SwingWorker worker = new SwingWorker<List<PatientVisitModel>, Void>() {

            private Date date;

            @Override
            protected List<PatientVisitModel> doInBackground() throws Exception {

                PVTDelegater pdl = new PVTDelegater();
                List<PatientVisitModel> result;

                date = new Date();
                String[] dateToSerach = getSearchDateAsString(date);

                // 全件検索する
                if (isFilterPatients()) {
                    result = (List<PatientVisitModel>)pdl.getPvtForAssigned(Project.getUserModel().getOrcaId(), UN_ASSIGNED_ID, dateToSerach, 0);
                } else {
                    result = (List<PatientVisitModel>)pdl.getPvt(dateToSerach, 0);
                }

                return result;
            }

            @Override
            protected void done() {
                try {

                    List<PatientVisitModel> result = get();

                    pvtTableModel.setDataProvider(result);

                    // 通知する
                    pvtTableModel.fireTableDataChanged();

                    // 表示を更新する
                    updatePvtInfo(date, pvtTableModel.getObjectCount());

                } catch (InterruptedException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (ExecutionException ex) {
                    ClientContext.getBootLogger().warn(ex);
                }
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                        setBusy(false);
                        if (schedule != null) {
                            restartCheckTimer(checkInterval);
                        }
                        worker.removePropertyChangeListener(this);
                    } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                        setBusy(true);
                    }
                }
            }
        });

        worker.execute();
    }
    
    /**
     * 患者来院情報を定期的にチェックするタイマータスククラス。
     */
    protected class PvtChecker implements Runnable {
        
        @Override
        public void run() {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setBusy(true);
                }
            });

            final Date date = new Date();
            String[] dateToSerach = getSearchDateAsString(date);
            List<PatientVisitModel> dataList = pvtTableModel.getDataProvider();
            
            // 現在のレコード数
            int curSize = dataList!=null ? dataList.size() : 0;

            // 診察終了及びキャンセル以外の最初のレコードを見つける
            int firstResult = 0;
            for (int i=0; i<curSize; i++) {

                PatientVisitModel pvm = dataList.get(i);
                int state = pvm.getState();

                // 修正フラグの場合、ステータスはこれ以上変化しない
                if ((state & (1<<ChartImpl.BIT_MODIFY_CLAIM))!=0) {
                    continue;
                }

                // キャンセルフラグの場合、ステータスはこれ以上変化しない
                if ((state & (1<<bitCancel))!=0) {
                    continue;
                }

                firstResult = i;
                break;
            }
            
            if (DEBUG) {
                trace("------------------------------------");
                trace("firstResult="+firstResult);
                trace("curSize=" + curSize);
            }
            
            List<PatientVisitModel> result;

            try {
                // firstResult移行を検索する
                PVTDelegater pdl = new PVTDelegater();
                if (isFilterPatients()) {
                    result = (List<PatientVisitModel>)pdl.getPvtForAssigned(Project.getUserModel().getOrcaId(), UN_ASSIGNED_ID, dateToSerach, firstResult);
                } else {
                    result = (List<PatientVisitModel>)pdl.getPvt(dateToSerach, firstResult);  //0
                }
                
                if (result!=null && result.size()>0) {
                    
                    trace("result size=" + result.size());
                    if (DEBUG) {
                        for (int i = 0; i < result.size(); i++) {
                            PatientVisitModel pvt = result.get(i);
                            StringBuilder sb = new StringBuilder();
                            sb.append(pvt.getPatientId()).append("=").append(pvt.getState());
                            trace(sb.toString());
                        }
                    }

                    // firstResultから curSize status を更新する
                    int len = curSize - firstResult;
                    for (int i = 0; i < len; i++) {
                        PatientVisitModel pvt = result.get(i);
                        for (PatientVisitModel cur : dataList) {
                            if (pvt.getId()==cur.getId()) {
                                cur.setState(pvt.getState());
                                cur.setMemo(pvt.getMemo());
                                break;
                            }
                        }
                    }

                    // 新規分を追加する
                    for (int i = len; i < result.size(); i++) {
                         dataList.add(result.get(i));
                    }

                    // ! AWT で OK
                    pvtTableModel.fireTableDataChanged();
                    
                } else {
                    trace("result size=0" );
                }

            } catch (Throwable e) {
                e.printStackTrace(System.err);
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updatePvtInfo(date, pvtTableModel.getObjectCount());
                    setBusy(false);
                }
            });
        }
    }
    
    private String[] getSearchDateAsString(Date date) {
            
        String[] ret = new String[3];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ret[0] = sdf.format(date);

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);

        gc.add(Calendar.DAY_OF_MONTH, -2);
        date = gc.getTime();
        ret[1] = sdf.format(date);

        gc.add(Calendar.DAY_OF_MONTH, 2);
        date = gc.getTime();
        ret[2] = sdf.format(date);

        return ret;
    }
    
    private void trace(String msg) {
        if (DEBUG) {
            System.err.println(msg);
        }
    }
    
    /**
     * KarteStateRenderer
     * カルテ（チャート）の状態をレンダリングするクラス。
     */
    protected class KarteStateRenderer extends DefaultTableCellRenderer {
        
        /** Creates new IconRenderer */
        public KarteStateRenderer() {
            super();
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            
            PatientVisitModel pvt = (PatientVisitModel) pvtTableModel.getObject(row);
            
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
                this.setForeground(table.getSelectionForeground());
                
            } else {
                
                if (isSexRenderer()) {

                    if (pvt !=null && pvt.getPatientModel().getGender().equals(IInfoModel.MALE)) {
                        this.setBackground(MALE_COLOR);
                    } else if (pvt !=null && pvt.getPatientModel().getGender().equals(IInfoModel.FEMALE)) {
                        this.setBackground(FEMALE_COLOR);
                    } else {
                        this.setBackground(Color.WHITE);
                    }

                } else {
                    if ((row & (1)) == 0) {
                        this.setBackground(EVEN_COLOR);
                    } else {
                        this.setBackground(ODD_COLOR);
                    }
                }

                Color fore = pvt != null && (pvt.getState() & (1<<bitCancel))!=0 ? CANCEL_PVT_COLOR : table.getForeground();
                this.setForeground(fore);
            }
            
            if (value != null && value instanceof Integer) {
                
                int state = ((Integer) value).intValue();
                
                ImageIcon icon = null;

                // 最初に chart bit をテストする
                for (int i = 0; i < chartBitArray.length; i++) {
                    if ((state & (1<<chartBitArray[i]))!=0) {
                        icon = chartIconArray[i];
                        break;
                    }
                }

                // user bit をテストする
                if (icon == null) {

                    // bit 0 はパス
                    for (int i = 1; i < userBitArray.length; i++) {

                        int bit = userBitArray[i].intValue();
                        if ( (state & (1<<bit))!=0 ) {
                            icon = userIconArray[i];
                            break;
                        }
                    }
                }
                
                this.setIcon(icon);
                this.setText("");
                
            } else {
                setIcon(null);
                this.setText(value == null ? "" : value.toString());
            }
            return this;
        }
    }
    
    /**
     * KarteStateRenderer
     * カルテ（チャート）の状態をレンダリングするクラス。
     */
    protected class MaleFemaleRenderer extends DefaultTableCellRenderer {
        
        /** Creates new IconRenderer */
        public MaleFemaleRenderer() {
            super();
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            
            PatientVisitModel pvt = (PatientVisitModel) pvtTableModel.getObject(row);
            
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
                this.setForeground(table.getSelectionForeground());
                
            } else {
                if (isSexRenderer()) {

                    if (pvt !=null && pvt.getPatientModel().getGender().equals(IInfoModel.MALE)) {
                        this.setBackground(MALE_COLOR);
                    } else if (pvt !=null && pvt.getPatientModel().getGender().equals(IInfoModel.FEMALE)) {
                        this.setBackground(FEMALE_COLOR);
                    } else {
                        this.setBackground(Color.WHITE);
                    }

                } else {

                    if ((row & (1)) == 0) {
                        this.setBackground(EVEN_COLOR);
                    } else {
                        this.setBackground(ODD_COLOR);
                    }
                }
                
                Color fore = pvt != null && (pvt.getState() & (1<<bitCancel))!=0 ? CANCEL_PVT_COLOR : table.getForeground();
                this.setForeground(fore);
            }
            
            if (value != null && value instanceof String) {
                this.setText((String) value);
            } else {
                setIcon(null);
                this.setText(value == null ? "" : value.toString());
            }
            return this;
        }
    }
    
    protected class CenterRenderer extends MaleFemaleRenderer {
        
        /** Creates new IconRenderer */
        public CenterRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    /**
     * Iconを表示するJComboBox Renderer.
     */
    protected class ComboBoxRenderer extends JLabel
                           implements ListCellRenderer {

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus) {

            BitAndIconPair pair = (BitAndIconPair)value;

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setIcon(pair.getIcon());
            return this;
        }
    }

    class BitAndIconPair {

        private Integer bit;
        private ImageIcon icon;

        public BitAndIconPair(Integer bit, ImageIcon icon) {
            this.bit = bit;
            this.icon = icon;
        }

        public Integer getBit() {
            return bit;
        }

        public ImageIcon getIcon() {
            return icon;
        }
    }
}