package open.dolphin.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import open.dolphin.delegater.PVTDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import open.dolphin.table.ObjectReflectTableModel;
import org.apache.log4j.Logger;

/**
 * 受付リスト。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class WatingListImpl extends AbstractMainComponent {
    
    private static final String NAME = "受付リスト";
    
    // 診察終了アイコン
    private static final ImageIcon FLAG_ICON = ClientContext.getImageIcon("flag_16.gif");
    
    // カルテオープンアイコン 
    private static final ImageIcon OPEN_ICON = ClientContext.getImageIcon("open_16.gif");
    
    // JTableレンダラ用の男性カラー 
    private static final Color MALE_COLOR = ClientContext.getColor("watingList.color.male");
    
    // JTableレンダラ用の女性カラー
    private static final Color FEMALE_COLOR = ClientContext.getColor("watingList.color.female");
    
    // JTableレンダラ用の奇数カラー
    private static final Color ODD_COLOR = ClientContext.getColor("color.odd");
    
    // JTableレンダラ用の偶数カラー 
    private static final Color EVEN_COLOR = ClientContext.getColor("color.even");
    
    // 受付キャンセルカラー 
    private static final Color CANCEL_PVT_COLOR = ClientContext.getColor("watingList.color.pvtCancel");
    
    // 来院情報のチェック間隔オブジェクト
    private NameValuePair[] intervalObjects = ClientContext.getNameValuePair("watingList.interval");
    
    // デフォルトのチェック間隔 
    private int CHECK_INTERVAL = 30; // デフォルト値
    
    // 来院情報テーブルのステータスカラム
    private int STATE_COLUMN = 7;
    
    // 年齢表示カラム 
    private final int AGE_COLUMN = 4;
    
    // 年齢生年月日メソッド 
    private final String[] AGE_METHOD = new String[]{"getPatientAgeBirthday", "getPatientBirthday"};
    
    // PVT Table 
    private JTable pvtTable;
    private ObjectReflectTableModel pvtTableModel;
    
    // Preference 
    private Preferences preferences;
    
    // 性別レンダラフラグ 
    private boolean sexRenderer;
    
    // 年齢表示 
    private boolean ageDisplay;
    
    // 運転日 
    private Date operationDate;
    
    // 受付 DB をチェックした Date 
    private Date checkedTime;
    
    // 来院患者数 
    private int pvtCount;
    
    // チェック間隔
    private int checkInterval;
    
    // 選択されている患者情報 
    private PatientVisitModel selectedPvt;
    
    private int saveSelectedIndex;
    
    private ScheduledFuture timerHandler;
    
    private PvtChecker pvtChecker;
    
    private Logger logger;
    
    private WatingListView view;
    
    /** 
     * Creates new WatingList 
     */
    public WatingListImpl() {
        setName(NAME);
    }
    
    /**
     * ロガー等を取得する。
     */
    private void setup() {
        logger = ClientContext.getBootLogger();
        preferences = Preferences.userNodeForPackage(this.getClass());
        sexRenderer = preferences.getBoolean("sexRenderer", false);
        ageDisplay = preferences.getBoolean("ageDisplay", true);
        checkInterval = preferences.getInt("checkInterval", CHECK_INTERVAL);  
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        setup();
        initComponents();
        connect();
        restartCheckTimer();
    }
    
    /**
     * メインウインドウのタブで受付リストに切り替わった時
     * コールされる。
     */
    @Override
    public void enter() {
        controlMenu();
    }
    
    /**
     * プログラムを終了する。
     */
    public void stop() {
    }
    
    /**
     * 選択されている来院情報の患者オブジェクトを返す。
     * @return 患者オブジェクト
     */
    public PatientModel getPatinet() {
        return selectedPvt != null ? selectedPvt.getPatient() : null;
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
        preferences.putBoolean("sexRenderer", sexRenderer);
        if (pvtTable != null) {
            pvtTableModel.fireTableDataChanged();
        }
    }
    
    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
        ageDisplay = !ageDisplay;
        preferences.putBoolean("ageDisplay", ageDisplay);
        if (pvtTable != null) {
            String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
            pvtTableModel.setMethodName(method, AGE_COLUMN);
        }
    }
    
    /**
     * 来院情報を取得する日を設定する。
     * @param date 取得する日
     */
    public void setOperationDate(Date date) {
        operationDate = date;
        String formatStr = ClientContext.getString("watingList.state.dateFormat");
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr); // 2006-11-20(水)
        view.getDateLbl().setText(sdf.format(operationDate));
    }
    
    /**
     * 来院情報をチェックした時刻を設定する。
     * @param date チェックした時刻
     */
    public void setCheckedTime(Date date) {
        checkedTime = date;
        String formatStr = ClientContext.getString("watingList.state.timeFormat");
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        view.getCheckedTimeLbl().setText(sdf.format(checkedTime));
    }
    
    /**
     * 来院情報のチェック間隔(Timer delay)を設定する。
     * @param interval チェック間隔 sec
     */
    public void setCheckInterval(int interval) {
        
        checkInterval = interval;
        String intervalSt = String.valueOf(checkInterval);
        for (NameValuePair pair : intervalObjects) {
            if (intervalSt.equals(pair.getValue())) {
                String text = ClientContext.getString("watingList.state.checkText");
                text += pair.getName(); // チェック間隔:30秒
                view.getIntervalLbl().setText(text);
                break;
            }
        }
    }
    
    /**
     * 来院数を設定する。
     * @param cnt 来院数
     */
    public void setPvtCount(int cnt) {
        pvtCount = cnt;
        String text = ClientContext.getString("watingList.state.pvtCountText");
        text += String.valueOf(pvtCount); // 来院数:20
        view.getCountLbl().setText(text);
    }
    
    /**
     * テーブル及び靴アイコンの enable/diable 制御を行う。
     * @param busy pvt 検索中は true
     */
    public void setBusy(boolean busy) {
        
        view.getKutuBtn().setEnabled(!busy);
        
        if (busy) {
            getContext().block();
            saveSelectedIndex = pvtTable.getSelectedRow();
        } else {
            getContext().unblock();
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
    public void restartCheckTimer() {
        
        if (timerHandler != null) {
            timerHandler.cancel(false);
        }
        
        if (pvtChecker == null) {
            pvtChecker = new PvtChecker();
        }
        
        ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
        timerHandler = schedule.scheduleWithFixedDelay(pvtChecker, 0, checkInterval, TimeUnit.SECONDS);
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
        
        view = new WatingListView();
        setUI(view);
        
        // 来院テーブル用のパラメータを取得する
        String[] columnNames = ClientContext.getStringArray("watingList.columnNames");
        String[] methodNames = ClientContext.getStringArray("watingList.methodNames");
        Class[] classes = ClientContext.getClassArray("watingList.columnClasses");
        int[] columnWidth = ClientContext.getIntArray("watingList.columnWidth");
        int startNumRows = ClientContext.getInt("watingList.startNumRows");
        int rowHeight = ClientContext.getInt("watingList.rowHeight");
        
        // 年齢表示をしない場合はメソッドを変更する
        if (!ageDisplay) {
            methodNames[AGE_COLUMN] = AGE_METHOD[1];
        }
        
        // 生成する
        pvtTable = view.getTable();
        pvtTableModel = new ObjectReflectTableModel(columnNames,startNumRows, methodNames, classes);
        pvtTable.setModel(pvtTableModel);
        
        // コンテキストメニューを登録する
        pvtTable.addMouseListener(new ContextListener());     
        
        // 来院情報テーブルの属性を設定する
        pvtTable.setRowHeight(rowHeight);
        pvtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (int i = 0; i <columnWidth.length; i++) {
            pvtTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        }
        
        // 性別レンダラを生成する
        MaleFemaleRenderer sRenderer = new MaleFemaleRenderer();
        pvtTable.getColumnModel().getColumn(0).setCellRenderer(sRenderer);
        pvtTable.getColumnModel().getColumn(2).setCellRenderer(sRenderer);
        pvtTable.getColumnModel().getColumn(4).setCellRenderer(sRenderer);
        pvtTable.getColumnModel().getColumn(5).setCellRenderer(sRenderer);
        pvtTable.getColumnModel().getColumn(6).setCellRenderer(sRenderer);
        
        // Center Renderer
        CenterRenderer centerRenderer = new CenterRenderer();
        pvtTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        pvtTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // カルテ状態レンダラ
        KarteStateRenderer renderer = new KarteStateRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        pvtTable.getColumnModel().getColumn(STATE_COLUMN).setCellRenderer(renderer);
               
        // 日付ラベルに値を設定する
        setOperationDate(new Date());
        
        // チェック間隔情報を設定する
        setCheckInterval(checkInterval);
        
        // 来院数を設定する
        setPvtCount(0);
    }
    
    /**
     * コンポーネントにイベントハンドラーを登録し相互に接続する。
     */
    private void connect() {
        
        //
        // Chart のリスナになる
        // 患者カルテの Open/Save/SaveTemp の通知を受けて受付リストの表示を制御する
        //
        ChartImpl.addPropertyChangeListener(ChartImpl.CHART_STATE, 
            (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "updateState", "newValue"));
                
        new EventAdapter(view.getTable());
        
        //
        // 靴のアイコンをクリックした時来院情報を検索する
        //
        view.getKutuBtn().addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "checkFullPvt"));
    }
    
    class EventAdapter implements ListSelectionListener, MouseListener {
        
        public EventAdapter(JTable tbl) {
            
            tbl.getSelectionModel().addListSelectionListener(this);
            tbl.addMouseListener(this);
        }
        
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
                JTable table = view.getTable();
                ObjectReflectTableModel tableModel = (ObjectReflectTableModel) table.getModel();
                int row = table.getSelectedRow();
                PatientVisitModel patient = (PatientVisitModel) tableModel.getObject(row);
                setSelectedPvt(patient);
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable table = (JTable) e.getSource();
                ObjectReflectTableModel tableModel = (ObjectReflectTableModel) table.getModel();
                PatientVisitModel value = (PatientVisitModel) tableModel.getObject(table.getSelectedRow());
                if (value != null) {
                    openKarte(value);
                }
            }
        }

        public void mousePressed(MouseEvent arg0) {}

        public void mouseReleased(MouseEvent arg0) {}

        public void mouseEntered(MouseEvent arg0) {}

        public void mouseExited(MouseEvent arg0) {}
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
    public void updateState(final PatientVisitModel updated) {
        
        // 受付リストの状態カラムを更新する
        List pvtList = pvtTableModel.getObjectList();
        int cnt = pvtList.size();
        boolean found = false;
        for (int i = 0; i < cnt; i++) {
            //
            // カルテをオープンし記録している途中で
            // 受付リストが更新され、要素が別オブジェクトに
            // なっている場合があるため、レコードIDで比較する
            //
            PatientVisitModel test = (PatientVisitModel) pvtList.get(i);
            if (updated.getId() == test.getId()) {
                test.setState(updated.getState());
                pvtTableModel.fireTableRowsUpdated(i, i);
                found = true;
                break;
            }
        }
        
        //
        // データベースを更新する
        //
        if (found && updated.getState() == ChartImpl.CLOSE_SAVE) {
            Runnable r = new Runnable() {
                public void run() {
                    PVTDelegater pdl = new PVTDelegater();
                    pdl.updatePvtState(updated.getId(), updated.getState());
                }
            };
            Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        }
    }
    
    public void checkFullPvt() {
        
        if (timerHandler != null) {
            timerHandler.cancel(false);
        }
        
        try {
            
            FutureTask<Integer> task = new FutureTask<Integer>(new PvtChecker2());
            new Thread(task).start();

            Integer result = task.get(120, TimeUnit.SECONDS); // 2分
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (pvtChecker == null) {
            pvtChecker = new PvtChecker();
        }
        
        ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
        timerHandler = schedule.scheduleWithFixedDelay(pvtChecker, checkInterval, checkInterval, TimeUnit.SECONDS);
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
            if (pvtModel.getState() == ChartImpl.CANCEL_PVT) {
                return true;
            }
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
                String pop3 = ClientContext.getString("watingList.popup.oddEvenRenderer");
                String pop4 = ClientContext.getString("watingList.popup.sexRenderer");
                String pop5 = "年齢表示";
                
                int row = pvtTable.rowAtPoint(e.getPoint());
                Object obj = pvtTableModel.getObject(row);
                int selected = pvtTable.getSelectedRow();
                
                if (row == selected && obj != null) {
                    String pop1 = ClientContext.getString("watingList.popup.openKarte");
                    String pop2 = ClientContext.getString("watingList.popup.cancelVisit");
                    contextMenu.add(new JMenuItem(new ReflectAction(pop1, WatingListImpl.this, "openKarte")));
                    contextMenu.add(new JMenuItem(new ReflectAction(pop2, WatingListImpl.this, "cancelVisit")));
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
                
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
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
     * 選択した患者の受付をキャンセルする。
     */
    public void cancelVisit() {
        
        final int selected = pvtTable.getSelectedRow();
        Object obj = pvtTableModel.getObject(selected);
        final PatientVisitModel pvtModel = (PatientVisitModel) obj;
        
        //
        // ダイアログを表示し確認する
        //
        Object[] cstOptions = new Object[]{"はい", "いいえ"};
        
        StringBuilder sb = new StringBuilder(pvtModel.getPatientName());
        sb.append("様の受付を取り消しますか?");
        
        int select = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(pvtTable),
                sb.toString(),
                ClientContext.getFrameTitle(getName()),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ClientContext.getImageIcon("cancl_32.gif"),
                cstOptions,"はい");
        
        // 受付を取り消す
        if (select == 0) {
            Runnable r = new Runnable() {
                public void run() {
                    pvtModel.setState(ChartImpl.CANCEL_PVT);
                    PVTDelegater pdl = new PVTDelegater();
                    pdl.updatePvtState(pvtModel.getId(), pvtModel.getState());
                    pvtTableModel.fireTableRowsUpdated(selected, selected);
                }
            };
            Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.run();
        }
    }   
    
    /**
     * 患者来院情報を定期的にチェックするタイマータスククラス。
     */
    protected class PvtChecker implements Runnable {
        
        /**
         * Creates new Task
         */
        public PvtChecker() {
        }
        
        private PVTDelegater getDelegater() {
            return new PVTDelegater();
        }
        
        /**
         * ＤＢの検索タスク
         */
        public void run() {
            
            Runnable awt1 = new Runnable() {
                public void run() {
                    setBusy(true);
                }
            };
            EventQueue.invokeLater(awt1);
            
            final Date date = new Date();
            final String[] dateToSerach = getSearchDateAsString(date);
            
            // Hibernate の firstResult を現在の件数を保存する
            List dataList = pvtTableModel.getObjectList();
            int firstResult = dataList != null ? dataList.size() : 0;
            
            logger.info("check PVT at " + date);
            logger.info("first result = " + firstResult);
            
            // 検索する
            final ArrayList result = (ArrayList) getDelegater().getPvt(dateToSerach, firstResult);
            int newVisitCount = result != null ? result.size() : 0;
            logger.info("new visits = " + newVisitCount);
            
            // 結果を追加する
            if (newVisitCount > 0) {
                for (int i = 0; i < newVisitCount; i++) {
                    dataList.add(result.get(i));
                }
                pvtTableModel.fireTableRowsInserted(firstResult, dataList.size() - 1);
            }
            
            Runnable awt2 = new Runnable() {
                public void run() {
                    setCheckedTime(date);
                    setPvtCount(pvtTableModel.getObjectCount());
                    setBusy(false);
                }
            };
            EventQueue.invokeLater(awt2);
        }
    }
    
        
    /**
     * 患者来院情報を定期的にチェックするタイマータスククラス。
     */
    protected class PvtChecker2 implements Callable<Integer> {
        
        /**
         * Creates new Task
         */
        public PvtChecker2() {
        }
        
        private PVTDelegater getDelegater() {
            return new PVTDelegater();
        }
        
        /**
         * ＤＢの検索タスク
         */
        public Integer call() {
            
            Runnable awt1 = new Runnable() {
                public void run() {
                    setBusy(true);
                }
            };
            EventQueue.invokeLater(awt1);
            
            final Date date = new Date();
            final String[] dateToSerach = getSearchDateAsString(date);
            
            //
            // 最初に現れる診察未終了レコードを Hibernate の firstResult にする
            // 現在の件数を保存する
            //
            List dataList = pvtTableModel.getObjectList();
            int firstResult = 0;
            int curCount = dataList != null ? dataList.size() : 0;
            
            if (dataList != null && curCount > 0) {
                boolean found = false;
                int cnt = curCount;
                for (int i = 0; i < cnt; i++) {
                    PatientVisitModel pvt = (PatientVisitModel) dataList.get(i);
                    if (pvt.getState() == ChartImpl.CLOSE_NONE) {
                        //
                        // 診察未終了レコードがあった場合
                        // firstResult = i;
                        //
                        firstResult = i;
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    //
                    // firstResult = 無かった場合はレコード件数
                    //
                    firstResult = cnt;
                }
            }
            
            logger.info("check full PVT at " + date);
            logger.info("first result = " + firstResult);
            
            //
            // 検索する
            //
            final ArrayList result = (ArrayList) getDelegater().getPvt(dateToSerach, firstResult);
            
            int checkCount = result != null ? result.size() : 0;
            logger.info("check visits = " + checkCount);
            
            //
            // 結果を合成する
            //
            if (checkCount > 0) {
                //
                // firstResult から cnt までは現在のレコードを使用する 
                //
                int index = 0;
                for (int i = firstResult; i < curCount; i++) {
                    PatientVisitModel pvtC = (PatientVisitModel) dataList.get(i);
                    PatientVisitModel pvtU = (PatientVisitModel) result.get(index++);
                    //
                    // 終了していたら設定する
                    //
                    if (pvtU.getState() == ChartImpl.CLOSE_SAVE && (!isKarteOpened(pvtU))) {
                        pvtC.setState(pvtU.getState());
                    }
                }
                
                //
                // cnt 以降は新しいレコードなのでそのまま追加する
                //
                for (int i = index; i < result.size(); i++) {
                    dataList.add(result.get(index++));
                }
                
                pvtTableModel.fireTableDataChanged();
                
            }
            
            Runnable awt2 = new Runnable() {
                public void run() {
                    setCheckedTime(date);
                    setPvtCount(pvtTableModel.getObjectCount());
                    setBusy(false);
                }
            };
            EventQueue.invokeLater(awt2);
            
            return new Integer(checkCount);
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

                    if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.MALE)) {
                        this.setBackground(MALE_COLOR);
                    } else if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.FEMALE)) {
                        this.setBackground(FEMALE_COLOR);
                    } else {
                        this.setBackground(Color.WHITE);
                    }

                } else {
                    if (row % 2 == 0) {
                        this.setBackground(EVEN_COLOR);
                    } else {
                        this.setBackground(ODD_COLOR);
                    }
                }
                
                Color fore = pvt != null && pvt.getState() == ChartImpl.CANCEL_PVT ? CANCEL_PVT_COLOR : table.getForeground();
                this.setForeground(fore);
            }
            
            if (value != null && value instanceof Integer) {
                
                int state = ((Integer) value).intValue();
                
                switch (state) {
                    
                    case ChartImpl.CLOSE_NONE:
                        //
                        // アイコンなし
                        //
                        this.setIcon(null);
                        break;
                        
                    case ChartImpl.CLOSE_SAVE:
                        //
                        // 診察が終了している場合は旗
                        //
                        this.setIcon(FLAG_ICON);
                        break;
                        
                    case ChartImpl.OPEN_NONE:
                    case ChartImpl.OPEN_SAVE:
                        //
                        // オープンしている場合はオープン
                        //
                        this.setIcon(OPEN_ICON);
                        break;    
                        
                    default:
                        this.setIcon(null);
                        break;
                }
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

                    if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.MALE)) {
                        this.setBackground(MALE_COLOR);
                    } else if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.FEMALE)) {
                        this.setBackground(FEMALE_COLOR);
                    } else {
                        this.setBackground(Color.WHITE);
                    }

                } else {

                    if (row % 2 == 0) {
                        this.setBackground(EVEN_COLOR);
                    } else {
                        this.setBackground(ODD_COLOR);
                    }
                }
                
                Color fore = pvt != null && pvt.getState() == ChartImpl.CANCEL_PVT ? CANCEL_PVT_COLOR : table.getForeground();
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
}