package open.dolphin.impl.schedule;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.EventHandler;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import open.dolphin.client.*;
import open.dolphin.delegater.ScheduleDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PostSchedule;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;
import open.dolphin.table.ColumnSpec;
import open.dolphin.table.ColumnSpecHelper;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.ListTableSorter;
import open.dolphin.table.StripeTableCellRenderer;
import open.dolphin.util.AgeCalculator;

/**
 * 患者検索PatientSearchPlugin
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa
 */
public class PatientScheduleImpl extends AbstractMainComponent {
    
//    private static final Color DEFAULT_ODD_COLOR = ClientContext.getColor("color.odd");
//    private static final Color DEFAULT_EVEN_COLOR = ClientContext.getColor("color.even");
//minagawa^ Icon Server    
    //private static final ImageIcon SCHEDULE_ICON = ClientContext.getImageIcon("favs_16.gif");
//minagawa$    
    
    private static final String NAME = "予定患者";
    
    private static final String[] COLUMN_NAMES = {
        "患者ID", "氏   名", "性別", "保険", "生年月日", "担当医", "診療科", "カルテ"};
    
    // 来院テーブルのカラムメソッド
    private static final String[] PROPERTY_NAMES = {
        "getPatientId", "getPatientName", "getPatientGenderDesc", "getFirstInsurance",
        "getPatientAgeBirthday", "getDoctorName", "getDeptName", "getLastDocDate"};
    
    private static final Class[] COLUMN_CLASSES = {
        String.class, String.class, String.class, String.class, String.class, 
        String.class, String.class, String.class};
    
    private static final int[] COLUMN_WIDTH = {80, 100, 40, 130, 130, 50, 60, 40};
   
    // カラム仕様名
    private static final String COLUMN_SPEC_NAME = "patientScheduleTable.withoutAddress.column.spec";
     
    // 状態カラムの識別名
    private static final String COLUMN_IDENTIFIER_STATE = "stateColumn";
    
    private static final String KEY_AGE_DISPLAY = "patientScheduleTable.withoutAddress.ageDisplay";
    
    // カラム仕様ヘルパー
    private ColumnSpecHelper columnHelper;
    
    // 予定日
    private GregorianCalendar scheduleDate;
    
    private boolean assignedOnly;

    // 選択されている患者情報
    private PatientVisitModel selectedVisit;

    // 年齢表示
    private boolean ageDisplay;
    
    // 年齢生年月日メソッド
    private final String[] AGE_METHOD = {"getPatientAgeBirthday", "getPatientBirthday"};

    // View
    private PatientScheduleView view;

    private int ageColumn;
    private int stateColumn;

    private ListTableModel tableModel;
    private ListTableSorter sorter;

    // Copy Action
    private AbstractAction copyAction;
    
    // 未来処方適用ボタン
    private AbstractAction applyRpAction;
    
    // 更新 Action
    private AbstractAction updateAction;
    
    // CLAIM 送信 Action
    private AbstractAction claimAction;
    
    // 処方適用カルテ作成と同時にCLAIM送信するかどうか
    // デフォルトは false
    private boolean sendClaim;
    

    /** Creates new PatientSearch */
    public PatientScheduleImpl() {
        setName(NAME);
        assignedOnly = Project.getBoolean("PATIENT_SCHEDULE_ASSIGNED_ONLY", false);
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
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                view.getKeywordFld().requestFocusInWindow();
                view.getKeywordFld().selectAll();
            }
        });
    }

    @Override
    public void stop() {  
        // ColumnSpecsを保存する
        if (columnHelper != null) {
            columnHelper.saveProperty();
        }
    }

    public PatientVisitModel getSelectedVisit() {
        return selectedVisit;
    }

    public void setSelectedVisit(PatientVisitModel model) {
        selectedVisit = model;
        controlMenu();
    }
    
    public GregorianCalendar getScheduleDate() {
        return scheduleDate;
    }
    
    public void setScheduleDate(GregorianCalendar sd) {
        scheduleDate = sd;
        if (scheduleDate==null) {
            updateAction.setEnabled(false);
            view.getKeywordFld().setText("");
            return;
        }
        SimpleDateFormat frmt = new SimpleDateFormat(IInfoModel.DATE_FORMAT_FOR_SCHEDULE);
        view.getKeywordFld().setText(frmt.format(scheduleDate.getTime()));
        String test = stringFromCalendar(scheduleDate);
        find(test);
    }
    
    public boolean isSendClaim() {
        return sendClaim;
    }
    
    public void setSendClaim(boolean b) {
        sendClaim = b;
    }
    
    public boolean isAssignedOnly() {
        return assignedOnly;
    }
    
    public void setAssignedOnly(boolean b) {
        assignedOnly = b;
        Project.setBoolean("PATIENT_SCHEDULE_ASSIGNED_ONLY", assignedOnly);
        setScheduleDate(getScheduleDate());
    }

    public ListTableModel<PatientVisitModel> getTableModel() {
        return (ListTableModel<PatientVisitModel>)sorter.getTableModel();
    }

    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {           
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
        columnHelper.saveProperty();
    }

    /**
     * メニューを制御する
     */
    private void controlMenu() {
        PatientVisitModel pvt = getSelectedVisit();
        boolean enabled = pvt!=null && canOpen(pvt.getPatientModel());
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
                ListTableModel<PatientVisitModel> tModel = getTableModel();
                PatientVisitModel obj = tModel.getObject(row);
                int selected = view.getTable().getSelectedRow();

                if (row == selected && obj != null) {
                    contextMenu.add(new JMenuItem(new ReflectAction("カルテを開く", PatientScheduleImpl.this, "openKarte")));
                    contextMenu.addSeparator();
                    contextMenu.add(new JMenuItem(copyAction));
                    contextMenu.add(new JMenuItem(new ReflectAction("予定削除", PatientScheduleImpl.this, "remove")));
                    contextMenu.addSeparator();
                }
                
                if (Project.getUserModel().getOrcaId()!=null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("担当分(").append(Project.getUserModel().getCommonName()).append(")のみ表示");
                    JCheckBoxMenuItem item = new JCheckBoxMenuItem(sb.toString());
                    item.setSelected(isAssignedOnly());
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setAssignedOnly(!isAssignedOnly());
                        }
                    });
                    contextMenu.add(item);
                    contextMenu.addSeparator();
                }

                JCheckBoxMenuItem item = new JCheckBoxMenuItem("年齢表示");
                contextMenu.add(item);
                item.setSelected(ageDisplay);
                item.addActionListener((ActionListener) EventHandler.create(ActionListener.class, PatientScheduleImpl.this, "switchAgeDisplay"));

                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    private void setup() {
        columnHelper = new ColumnSpecHelper(COLUMN_SPEC_NAME,
                COLUMN_NAMES, PROPERTY_NAMES, COLUMN_CLASSES, COLUMN_WIDTH);
        
        columnHelper.loadProperty();
        // Scan して ageカラムを設定する
        ageColumn = columnHelper.getColumnPositionEndsWith("irthday"); //irthday
        ageDisplay = Project.getBoolean(KEY_AGE_DISPLAY, true);
        stateColumn = columnHelper.getColumnPositionEndsWith("LastDocDate");
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {

        // View
        view = new PatientScheduleView();
        setUI(view);
        
        // ColumnSpecHelperにテーブルを設定する
        columnHelper.setTable(view.getTable());

        //------------------------------------------
        // View のテーブルモデルを置き換える
        //------------------------------------------
        String[] columnNames = columnHelper.getTableModelColumnNames();
        String[] methods = columnHelper.getTableModelColumnMethods();
        Class[] cls = columnHelper.getTableModelColumnClasses();

        // TableModel
        tableModel = new ListTableModel<PatientVisitModel>(columnNames, 0, methods, cls) {
            
            @Override
            public Object getValueAt(int row, int col) {

                Object ret=null;

                if (col==stateColumn) {
                    PatientVisitModel pvt = getObject(row);
                    if (pvt!=null) {
                        Date last = pvt.getLastDocDate();
                        boolean hasKarte = (last!=null && last.equals(getScheduleDate().getTime()));
                        ret = hasKarte;
                    } else {
                        ret = false;
                    }
                } else if (col==ageColumn) {

                    PatientVisitModel p = getObject(row);

                    if (p != null && ageDisplay) {
                        int showMonth = Project.getInt("ageToNeedMonth", 6);
                        ret = AgeCalculator.getAgeAndBirthday(p.getPatientModel().getBirthday(), showMonth);
                    } else if (p != null){
                        ret = p.getPatientBirthday();
                    }
                    
                } else {
                    ret = super.getValueAt(row, col);
                }

                return ret;
            }
        };
        view.getTable().setModel(tableModel);
        view.getTable().getTableHeader().setReorderingAllowed(false);
        
        // Sorter
        sorter = new ListTableSorter(tableModel);
        view.getTable().setModel(sorter);
        sorter.setTableHeader(view.getTable().getTableHeader());
        
        // カラム幅更新
        columnHelper.updateColumnWidth();      
        view.getTable().getColumnModel().getColumn(stateColumn).setIdentifier(COLUMN_IDENTIFIER_STATE);       
        
        // レンダラー
//        StripeTableCellRenderer sr = new StripeTableCellRenderer();
//        sr.setTable(view.getTable());
//        sr.setDefaultRenderer();
//        // ChecBox用
//        view.getTable().getColumnModel().getColumn(stateColumn).setCellRenderer(new CheckBoxRenderer());
        PatientListTableRenderer render = new PatientListTableRenderer();
        render.setTable(view.getTable());
        render.setDefaultRenderer();
        
        // 行高
        if (ClientContext.isWin()) {
            view.getTable().setRowHeight(ClientContext.getMoreHigherRowHeight());
        } else {
            view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
        }
        
        view.getKeywordFld().setEditable(false);
    }

    /**
     * コンポーンントにリスナを登録し接続する。
     */
    private void connect() {

        // ColumnHelperでカラム変更関連イベントを設定する
        columnHelper.connect();
        
        EventAdapter adp = new EventAdapter(view.getKeywordFld(), view.getTable());
 
        // カレンダによる日付検索を設定する
        // 今月から３ヶ月先まで
        int[] range = {0, 3};
        // 今日以降でないと駄目
        SimpleDate[] acceptRange = new SimpleDate[2];
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DAY_OF_MONTH, 1);
        acceptRange[0] = new SimpleDate(gc);
        acceptRange[1] = null;
        PopupListener pl = new PopupListener(view.getKeywordFld(), range, acceptRange);

        // コンテキストメニューを設定する
        view.getTable().addMouseListener(new ContextListener());

        // Copy 機能を実装する
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        view.getTable().getInputMap().put(copy, "Copy");
        view.getTable().getActionMap().put("Copy", copyAction);
        
        // 未来処方適用ボタン
        applyRpAction = new AbstractAction("処方適用") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                applyRp();
            }
        };
        view.getRpButton().setAction(applyRpAction);
        view.getRpButton().setToolTipText("前回処方を適用し、予定日のカルテを作成します。");
        applyRpAction.setEnabled(false);
        
        updateAction = new AbstractAction("更 新") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setScheduleDate(getScheduleDate());
            }
        };
        view.getUpdateButton().setAction(updateAction);
        view.getUpdateButton().setToolTipText("予定リストを更新します。");
        updateAction.setEnabled(false);
        
        claimAction = new AbstractAction("CLAIM送信") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setSendClaim(view.getClaimChk().isSelected());
            }
        };
        view.getClaimChk().setAction(claimAction);
        view.getClaimChk().setToolTipText("カルテの作成と同時にORCAへ送信します。");
        claimAction.setEnabled(Project.claimSenderIsServer());
        view.getClaimChk().setVisible(Project.claimSenderIsServer());
    }

    class EventAdapter implements ListSelectionListener, MouseListener {

        public EventAdapter(JTextField tf, JTable tbl) {
            tbl.getSelectionModel().addListSelectionListener(EventAdapter.this);
            tbl.addMouseListener(EventAdapter.this);
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
                JTable table = view.getTable();
                int row = table.getSelectedRow();
                PatientVisitModel patient = (PatientVisitModel)sorter.getObject(row);
                setSelectedVisit(patient);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount()==2) {
                JTable table = (JTable) e.getSource();
                ListTableModel<PatientVisitModel> tableModel = getTableModel();
                PatientVisitModel value = (PatientVisitModel)tableModel.getObject(table.getSelectedRow());
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
     * 予定患者の全てに未来処方を適用する。
     */
    public void applyRp() {
        
        SimpleWorker worker = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                List<PatientVisitModel> list = tableModel.getDataProvider();
                for (PatientVisitModel pvt : list) {
                    if (pvt.getLastDocDate()!=null) {
                        continue;
                    }
                    PostSchedule ps = new PostSchedule();
                    ps.setPvtPK(pvt.getId());
                    ps.setPtPK(pvt.getPatientModel().getId());
                    ps.setPhPK(Project.getUserModel().getId());
                    // 00:00:00 この時刻でstartedが作成される
                    // = で検索が可能
                    ps.setScheduleDate(getScheduleDate().getTime());
                    ps.setSendClaim(isSendClaim());
                    ScheduleDelegater ddl = ScheduleDelegater.getInstance();
                    int cnt = ddl.postSchedule(ps);
                    if (cnt==1) {
                        // ScheduleDateでカルテが作成されている
                        pvt.setLastDocDate(getScheduleDate().getTime());
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                tableModel.fireTableDataChanged();
                            }
                        }); 
                    }
                }
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                //setScheduleDate(getScheduleDate());
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
     * カルテを開く。
     * @param value 対象患者
     */
    public void openKarte() {

        if (canOpen(getSelectedVisit().getPatientModel())) {

            // 来院情報を生成する
            PatientVisitModel pvt = getSelectedVisit();
            pvt.setFromSchedule(true);

            // カルテコンテナを生成する
            getContext().openKarte(pvt);
        }
    }

    // EVT から
    private void doStartProgress() {
        updateAction.setEnabled(false);
        view.getCountLbl().setText("0 件");
        getContext().getProgressBar().setIndeterminate(true);
        getContext().getGlassPane().block();
        applyRpAction.setEnabled(false);
    }

    // EVT から
    private void doStopProgress() {
        getContext().getProgressBar().setIndeterminate(false);
        getContext().getProgressBar().setValue(0);
        getContext().getGlassPane().unblock();
        updateAction.setEnabled(true);
        updateStatusLabel();
        List<PatientVisitModel> list = tableModel.getDataProvider();
        if (list==null || list.isEmpty()) {
            applyRpAction.setEnabled(false);
            return;
        }
        for (PatientVisitModel pvt : list) {
            if (pvt.getLastDocDate()==null) {
                applyRpAction.setEnabled(true);
                break;
            }
        }   
    }

    /**
     * 検索を実行する。
     * @param text キーワード
     */
    private void find(final String text) {

        SimpleWorker worker = new SimpleWorker<Collection, Void>() {

            @Override
            protected Collection doInBackground() throws Exception {
                ScheduleDelegater sdl = ScheduleDelegater.getInstance();
                Collection result;
                if (isAssignedOnly()) {
                    result = sdl.getAssingedPvtList(text, Project.getUserModel().getOrcaId(), "18080");
                } else {
                    result = sdl.getPvtList(text);
                }
                return result;
            }

            @Override
            protected void succeeded(Collection result) {
                
                List<PatientVisitModel> list = (List<PatientVisitModel>)result;
                
                if (list != null && list.size() > 0) {
                    boolean sorted = true;
                    for (int i=0; i < COLUMN_NAMES.length; i++) {
                        if (sorter.getSortingStatus(i)==0) {
                            sorted = false;
                            break;
                        }
                    }
                    if (!sorted) {
                        Comparator c = new Comparator<PatientVisitModel>() {
                            @Override
                            public int compare(PatientVisitModel o1, PatientVisitModel o2) {
                                return o1.getPatientModel().getPatientId().compareTo(o2.getPatientModel().getPatientId());
                            }
                        };
                        Collections.sort(list, c);
                    }
                    tableModel.setDataProvider(list);
                    
                } else {
                    tableModel.clear();
                }
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
    
    // ステータスラベルに検索件数を表示
    private void updateStatusLabel() {
        int count = tableModel.getObjectCount();
        String msg = String.valueOf(count) + " 件";
        view.getCountLbl().setText(msg);
    }
    
    private String stringFromCalendar(GregorianCalendar gc) {
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String test = frmt.format(gc.getTime());
        return test;
    }
    
    private class PopupListener extends PopupCalendarListener {
        
        private PopupListener(JTextField tf, int[] range, SimpleDate[] disabled) {
            super(tf, range, disabled);
        }     

        @Override
        public void setValue(SimpleDate sd) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.clear();
            gc.set(GregorianCalendar.YEAR, sd.getYear());
            gc.set(GregorianCalendar.MONTH, sd.getMonth());
            gc.set(GregorianCalendar.DATE, sd.getDay());
            setScheduleDate(gc);
        }
    }
    
    public void remove() {
        
        PatientVisitModel pvtModel = getSelectedVisit();
        final long pvtPK = pvtModel.getId();
        final long ptPK = pvtModel.getPatientModel().getId();
        final String startDate = stringFromCalendar(getScheduleDate());

        // ダイアログを表示し確認する
        StringBuilder sb = new StringBuilder(pvtModel.getPatientName());
        sb.append("様の予定を削除しますか?");
        if (!showCancelDialog(sb.toString())) {
            return;
        }
        
        SimpleWorker worker = new SimpleWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground() throws Exception {
                ScheduleDelegater sdl = ScheduleDelegater.getInstance();
                int cnt = sdl.removePvt(pvtPK, ptPK, startDate);
                return cnt;
            }

            @Override
            protected void succeeded(Integer result) {
                setScheduleDate(getScheduleDate());
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
    
    private boolean showCancelDialog(String msg) {

        final String[] cstOptions = new String[]{"はい", "いいえ"};

        int select = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(view.getTable()),
                msg,
                ClientContext.getFrameTitle(getName()),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
//minagawa^ Icon Server                
                //ClientContext.getImageIcon("cancl_32.gif"),
                ClientContext.getImageIconArias("icon_caution"),
//minagawa$                
                cstOptions, cstOptions[1]);
        
        return (select == 0);
    } 
    
//    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
//
//        CheckBoxRenderer() {
//            setHorizontalAlignment(JLabel.CENTER);
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                boolean isSelected, boolean hasFocus, int row, int column) {
//            if (isSelected) {
//                setForeground(table.getSelectionForeground());
//                setBackground(table.getSelectionBackground());
//            } else {
//                if ((row & (1)) == 0) {
//                    this.setBackground(DEFAULT_EVEN_COLOR);
//                } else {
//                    this.setBackground(DEFAULT_ODD_COLOR);
//                }
//                setForeground(table.getForeground());
//            }
//            setSelected((value != null && ((Boolean) value).booleanValue()));
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
            
            PatientVisitModel pm = (PatientVisitModel)sorter.getObject(row);           
            boolean bStateColumn = (view.getTable().getColumnModel().getColumn(col).getIdentifier()!=null &&
                                    view.getTable().getColumnModel().getColumn(col).getIdentifier().equals(COLUMN_IDENTIFIER_STATE));
               
            if (pm != null && bStateColumn) {            
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null && ((Boolean)value).booleanValue()) {
//minagawa^ Icon Server                    
                    //setIcon(SCHEDULE_ICON);
                    setIcon(ClientContext.getImageIconArias("icon_star_small"));
//minagawa$                    
                }
                else {
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
}