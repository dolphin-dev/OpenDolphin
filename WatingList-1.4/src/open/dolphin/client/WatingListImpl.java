package open.dolphin.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.Executors;
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

import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.WorkerService;
import open.dolphin.table.ListTableModel;

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
    //private NameValuePair[] intervalObjects = ClientContext.getNameValuePair("watingList.interval");

    private static final String TEXT_PVT_COUNT = "来院数: ";

    private static final String TEXT_SECONDS = "秒";

    private static final String TEXT_CHECK_INTERVAL = "チェック間隔: ";

    // 来院テーブル用のパラメータ
    private final String[] COLUMN_NAMES = new String[]{"患者ID","来院時間","氏   名","性別","生年月日","診療科","予約","状態"};

    private static final String[] PROPERTY_NAMES = new String[]{
        "getPatientId","getPvtDateTrimDate","getPatientName","getPatientGenderDesc",
        "getPatientAgeBirthday","getDepartment","getAppointment","getStateInteger"};

    private static final Class[] COLUMN_CLASSES = new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,Integer.class};

    private static final int[] COLUMN_WIDTH = new int[]{80,60,140,40,150,50,40,30};

    private static final int START_NUM_ROWS = 30;

    //private static final int ROW_HEIGHT = 18;
    
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
    private ListTableModel<PatientVisitModel> pvtTableModel;
    
    // Preference 
    private Preferences preferences;
    
    // 性別レンダラフラグ 
    private boolean sexRenderer;
    
    // 年齢表示 
    private boolean ageDisplay;
    
    // チェック間隔
    private int checkInterval;
    
    // 選択されている患者情報 
    private PatientVisitModel selectedPvt;
    
    private int saveSelectedIndex;

    private ScheduledExecutorService schedule;
    
    private ScheduledFuture timerHandler;
    
    private PvtChecker pvtChecker;

    // View class
    private WatingListView view;

    private SimpleDateFormat timeFormatter;

    // Status　情報
    private String statusInfo;
    
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
        preferences = Preferences.userNodeForPackage(this.getClass());
        sexRenderer = preferences.getBoolean("sexRenderer", false);
        ageDisplay = preferences.getBoolean("ageDisplay", true);
        checkInterval = preferences.getInt("checkInterval", CHECK_INTERVAL);
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
        restartCheckTimer();
    }
    
    /**
     * メインウインドウのタブで受付リストに切り替わった時
     * コールされる。
     */
    @Override
    public void enter() {
        controlMenu();
        getContext().getStatusLabel().setText(getStatusInfo());
    }
    
    /**
     * プログラムを終了する。
     */
    @Override
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
            pvtTableModel.setProperty(method, AGE_COLUMN);
        }
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String info) {
        this.statusInfo = info;
    }
    
    /**
     * 来院情報のチェック間隔(Timer delay)を設定する。
     * @param interval チェック間隔 sec
     */
    public void setCheckInterval(int interval) {
        checkInterval = interval;
        StringBuilder sb = new StringBuilder();
        sb.append(TEXT_CHECK_INTERVAL);
        sb.append(interval);
        sb.append(TEXT_SECONDS);
        setStatusInfo(sb.toString());
    }

    public void updatePvtInfo(Date date, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(timeFormatter.format(date));
        sb.append(" | ");
        sb.append(TEXT_PVT_COUNT);
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
                getContext().block();
                getContext().getProgressBar().setIndeterminate(true);
            }
            saveSelectedIndex = pvtTable.getSelectedRow();
        } else {
            view.getKutuBtn().setEnabled(true);
            if (getContext().getCurrentComponent() == getUI()) {
                getContext().unblock();
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
    public void restartCheckTimer() {
        
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

        // View クラスを生成しこのプラグインの UI とする
        view = new WatingListView();
        setUI(view);

        view.getPvtInfoLbl().setText("");
        
        //
        // View のテーブルモデルを置き換える
        //
        pvtTable = view.getTable();
        pvtTableModel = new ListTableModel<PatientVisitModel>(COLUMN_NAMES, START_NUM_ROWS, PROPERTY_NAMES, COLUMN_CLASSES);
        // 年齢表示をしない場合はメソッドを変更する
        if (!ageDisplay) {
            pvtTableModel.setProperty(AGE_METHOD[1], AGE_COLUMN);
        }
        pvtTable.setModel(pvtTableModel);
        
        // コンテキストメニューを登録する
        pvtTable.addMouseListener(new ContextListener());     
        
        // 来院情報テーブルの属性を設定する
        pvtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pvtTable.setRowHeight(ClientContext.getHigherRowHeight());
        for (int i = 0; i < COLUMN_WIDTH.length; i++) {
            pvtTable.getColumnModel().getColumn(i).setPreferredWidth(COLUMN_WIDTH[i]);
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
        
        // チェック間隔情報を設定する
        setCheckInterval(checkInterval);
        getContext().getStatusLabel().setText(getStatusInfo());
    }
    
    /**
     * コンポーネントにイベントハンドラーを登録し相互に接続する。
     */
    private void connect() {
        
        // Chart のリスナになる
        // 患者カルテの Open/Save/SaveTemp の通知を受けて受付リストの表示を制御する
        ChartImpl.addPropertyChangeListener(ChartImpl.CHART_STATE, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ChartImpl.CHART_STATE)) {
                    updateState((PatientVisitModel) evt.getNewValue());
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
        
        // 靴のアイコンをクリックした時来院情報を検索する
        view.getKutuBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                checkFullPvt();
            }
        });
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
        List<PatientVisitModel> pvtList = pvtTableModel.getDataProvider();
        int cnt = pvtList != null ? pvtList.size() : 0;
        boolean found = false;
        for (int i = 0; i < cnt; i++) {
            //
            // カルテをオープンし記録している途中で
            // 受付リストが更新され、要素が別オブジェクトに
            // なっている場合があるため、レコードIDで比較する
            //
            PatientVisitModel test = pvtList.get(i);
            if (updated.getId() == test.getId()) {
                test.setState(updated.getState());
                //pvtTableModel.fireTableRowsUpdated(i, i);
                pvtTableModel.fireTableDataChanged();
                found = true;
                break;
            }
        }
        
        //
        // データベースを更新する
        //
        if (found && updated.getState() == ChartImpl.CLOSE_SAVE) {

            SimpleWorker worker = new SimpleWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    PVTDelegater pdl = new PVTDelegater();
                    pdl.updatePvtState(updated.getId(), updated.getState());
                    return null;
                }

                @Override
                protected void succeeded(Void result) {
                    ClientContext.getBootLogger().debug("ChartState の更新成功");
                }

                @Override
                protected void failed(Throwable cause) {
                    ClientContext.getBootLogger().warn("ChartState の更新失敗");
                }

            };

            worker.execute();
        }
    }
    
    public void checkFullPvt() {
        
        if (timerHandler != null && (!timerHandler.isCancelled())) {
            timerHandler.cancel(true);
            boolean cancelled = timerHandler.isCancelled();
            ClientContext.getBootLogger().debug("timerHandler isCancelled = " + cancelled);
            if (!cancelled) {
                return;
            }
        }

        SimpleWorker worker = new SimpleWorker<List<PatientVisitModel>, Void>() {

            private int saveCount;
            
            private int firstResult;

            private PVTDelegater getDelegater() {
                return new PVTDelegater();
            }

            @Override
            protected List<PatientVisitModel> doInBackground() throws Exception {
            
                ClientContext.getBootLogger().debug("checkFullPvt.doInBackground()");

                Date date = new Date();
                String[] dateToSerach = getSearchDateAsString(date);

                // 最初に現れる診察未終了レコードを Hibernate の firstResult にする
                // 現在の件数を保存する
                List<PatientVisitModel> dataList = pvtTableModel.getDataProvider();
                saveCount = pvtTableModel.getObjectCount();

                boolean found = false;

                for (int i = 0; i < saveCount; i++) {
                    PatientVisitModel pvt = dataList.get(i);
                    if (pvt.getState() == ChartImpl.CLOSE_NONE) {
                        // 診察未終了レコードがあった場合
                        // firstResult = i;
                        firstResult = i;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // firstResult = 無かった場合はレコード件数
                    firstResult = saveCount;
                }

                // 検索する
                PVTDelegater pdl = getDelegater();
                List<PatientVisitModel> result = (List<PatientVisitModel>) pdl.getPvt(dateToSerach, firstResult);

                if (pdl.isNoError()) {
                    return result;
                } else {
                    throw new Exception(pdl.getErrorMessage());
                }
            }

            @Override
            protected void succeeded(List<PatientVisitModel> result) {

                ClientContext.getBootLogger().debug("checkFullPvt.succeeded()");
                for (PatientVisitModel test : result) {
                    System.out.println(test.getState());
                }

                if (result!= null && result.size() > 0) {
                    //
                    // firstResult から saveCount までは現在のレコードを使用する
                    //
                    int index = 0;
                    for (int i = firstResult; i < saveCount; i++) {
                        PatientVisitModel pvtC = result.get(i);
                        PatientVisitModel pvtU = result.get(index++);

                        // 終了していたら設定する
                        if (pvtU.getState() == ChartImpl.CLOSE_SAVE && (!isKarteOpened(pvtU))) {
                            pvtC.setState(pvtU.getState());
                        }
                    }

                    // saveCount 以降は新しいレコードなのでそのまま追加する
                    for (int i = index; i < result.size(); i++) {
                        pvtTableModel.addObject(result.get(index++));
                    }
                }
            }

            @Override
            protected void failed(Throwable e) {
                ClientContext.getBootLogger().warn(e.getMessage());
            }
        };

        WorkerService service = new WorkerService() {

            @Override
            protected void startProgress() {
                setBusy(true);
            }

            @Override
            protected void stopProgress() {
                setBusy(false);

                // 受付受信をしない設定になっている可能性がある
                if (schedule != null) {
                    restartCheckTimer();
                }
            }
        };

        service.execute(worker);
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

        if (selected != 0) {
            return;
        }

        SimpleWorker worker = new SimpleWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                pvtModel.setState(ChartImpl.CANCEL_PVT);
                PVTDelegater pdl = new PVTDelegater();
                pdl.updatePvtState(pvtModel.getId(), pvtModel.getState());
                if (pdl.isNoError()) {
                    return new Boolean(true);
                } else {
                    throw new Exception(pdl.getErrorMessage());
                }
            }

            @Override
            protected void succeeded(Boolean result) {
                pvtTableModel.fireTableRowsUpdated(selected, selected);
            }
        };
        
        WorkerService service = new WorkerService();
        service.execute(worker);
    }   
    
    /**
     * 患者来院情報を定期的にチェックするタイマータスククラス。
     */
    protected class PvtChecker implements Runnable {
        
        /** Creates new Task */
        public PvtChecker() {
        }
        
        private PVTDelegater getDelegater() {
            return new PVTDelegater();
        }
        
        /**
         * ＤＢの検索タスク
         */
        @Override
        public void run() {
            
            Runnable awt1 = new Runnable() {
                @Override
                public void run() {
                    setBusy(true);
                }
            };
            EventQueue.invokeLater(awt1);
            
            final Date date = new Date();
            final String[] dateToSerach = getSearchDateAsString(date);
            
            // Hibernate の firstResult を現在の件数を保存する
            List<PatientVisitModel> dataList = pvtTableModel.getDataProvider();
            int firstResult = dataList != null ? dataList.size() : 0;
            
            // 検索する
            final ArrayList<PatientVisitModel> result = (ArrayList<PatientVisitModel>) getDelegater().getPvt(dateToSerach, firstResult);
            int newVisitCount = result != null ? result.size() : 0;
            
            // 結果を追加する
            if (newVisitCount > 0) {
                for (int i = 0; i < newVisitCount; i++) {
                    dataList.add(result.get(i));
                }
                pvtTableModel.fireTableRowsInserted(firstResult, dataList.size() - 1);
            }
            
            Runnable awt2 = new Runnable() {
                @Override
                public void run() {
                    updatePvtInfo(date, pvtTableModel.getObjectCount());
                    setBusy(false);
                }
            };
            EventQueue.invokeLater(awt2);
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