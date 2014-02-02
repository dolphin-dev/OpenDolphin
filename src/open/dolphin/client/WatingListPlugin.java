/*
 * WatingListService.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.table.*;

import open.dolphin.delegater.PVTDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import org.apache.log4j.Logger;

/**
 * 受付リスト。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class WatingListPlugin extends DefaultMainWindowPlugin {
    
    /** 診察終了アイコン */
    private static final ImageIcon FLAG_ICON = ClientContext.getImageIcon("flag_16.gif");
    
    /** カルテオープンアイコン */
    private static final ImageIcon OPEN_ICON = ClientContext.getImageIcon("open_16.gif");
    
    /** JTableレンダラ用の男性カラー */
    private static final Color MALE_COLOR = ClientContext.getColor("watingList.color.male");
    
    /** JTableレンダラ用の女性カラー */
    private static final Color FEMALE_COLOR = ClientContext.getColor("watingList.color.female");
    
    /** JTableレンダラ用の奇数カラー */
    private static final Color ODD_COLOR = ClientContext.getColor("color.odd");
    
    /** JTableレンダラ用の偶数カラー */
    private static final Color EVEN_COLOR = ClientContext.getColor("color.even");
    
    /** 受付キャンセルカラー */
    private static final Color CANCEL_PVT_COLOR = ClientContext.getColor("watingList.color.pvtCancel");
    
    /** 来院情報のチェック間隔オブジェクト */
    private NameValuePair[] intervalObjects = ClientContext.getNameValuePair("watingList.interval");
    
    /** デフォルトのチェック間隔 */
    private int CHECK_INTERVAL = 30; // デフォルト値
    
    // 来院情報テーブルのステータスカラム
    private int STATE_COLUMN = 7;
    
    /** 年齢表示カラム */
    private final int AGE_COLUMN = 4;
    
    /** 年齢生年月日メソッド */
    private final String[] AGE_METHOD = new String[]{"getPatientAgeBirthday", "getPatientBirthday"};
    
    //
    // GUI コンポーネント
    //
    /** PVT Table */
    private ObjectListTable pvtTable;
    
    /** 靴アイコンボタン */
    private JButton shoes;
    
    /** 来院件数表示ラベル */
    private JLabel countLabel;
    
    /** チェック時刻表示ラベル */
    private JLabel checkTimeLabel;
    
    /** チェック間隔表示ラベル */
    private JLabel checkIntervalLabel;
    
    /** 運転日表示ラベル */
    private JLabel dayLabel;
    
    //
    // 検索関連
    //
    private String PVT_DELEGATER_JNDI = "delegater/pvt";
    
    /** Preference */
    private Preferences preferences = Preferences.userNodeForPackage(this.getClass());
    
    /** 性別レンダラフラグ */
    private boolean sexRenderer;
    
    /** 年齢表示 */
    private boolean ageDisplay;
    
    /** 運転日 */
    private Date operationDate;
    
    /** 受付 DB をチェックした Date  */
    private Date checkedTime;
    
    /** 来院患者数 */
    private int pvtCount;
    
    /** チェック間隔 */
    private int checkInterval;
    
    /** 選択されている患者情報 */
    private PatientVisitModel selectedPvt;
    
    private int saveSelectedIndex;
    
    private ScheduledFuture timerHandler;
    
    private PvtChecker pvtChecker;
    
    private Logger logger;
    
    /** 
     * Creates new WatingList 
     */
    public WatingListPlugin() {
        logger = ClientContext.getLogger("boot");
        sexRenderer = preferences.getBoolean("sexRenderer", false);
        ageDisplay = preferences.getBoolean("ageDisplay", true);
        checkInterval = preferences.getInt("checkInterval", CHECK_INTERVAL);
        
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        // コンポーネントを初期化し接続する
        initComponents();
        connect();
        super.start();
    }
    
    /**
     * メインウインドウのタブで受付リストに切り替わった時
     * コールされる。
     */
    public void enter() {
        controlMenu();
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
            pvtTable.getTableModel().fireTableDataChanged();
        }
    }
    
    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
        ageDisplay = !ageDisplay;
        preferences.putBoolean("ageDisplay", ageDisplay);
        if (pvtTable != null) {
            //String coumnName = ageDisplay ? "年齢(生年月日)" : "生年月日";
            String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
            //pvtTable.getTableModel().setColumnName(coumnName, AGE_COLUMN);
            pvtTable.getTableModel().setMethodName(method, AGE_COLUMN);
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
        dayLabel.setText(sdf.format(operationDate));
    }
    
    /**
     * 来院情報をチェックした時刻を設定する。
     * @param date チェックした時刻
     */
    public void setCheckedTime(Date date) {
        checkedTime = date;
        String formatStr = ClientContext.getString("watingList.state.timeFormat");
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        checkTimeLabel.setText(sdf.format(checkedTime));
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
                checkIntervalLabel.setText(text);
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
        countLabel.setText(text);
    }
    
    /**
     * テーブル及び靴アイコンの enable/diable 制御を行う。
     * @param busy pvt 検索中は true
     */
    public void setBusy(boolean busy) {
        
        shoes.setEnabled(!busy);
        
        if (busy) {
            getContext().block();
            saveSelectedIndex = pvtTable.getTable().getSelectedRow();
        } else {
            getContext().unblock();
            pvtTable.getTable().getSelectionModel().addSelectionInterval(saveSelectedIndex, saveSelectedIndex);
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
    
    public void maybeSelectedPvt(Object newValue) {
        Object[] obj = (Object[]) newValue;
        // 情報をリフレッシュするため null かどうかに関係なくセットし通知する必要がある
        selectedPvt = (obj != null && obj.length > 0) 
                        ? (PatientVisitModel) obj[0]
                        : null;
        setSelectedPvt(selectedPvt);
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
        Action action = getContext().getAction("openKarte");
        boolean enabled = canOpen(pvt);
        if (action != null) {
            action.setEnabled(enabled);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * GUI コンポーネントを初期化しレアイアウトする。
     */
    private void initComponents() {
        
        //
        // 来院テーブル用のパラメータを取得する
        //
        String[] columnNames = ClientContext.getStringArray("watingList.columnNames");
        String[] methodNames = ClientContext.getStringArray("watingList.methodNames");
        Class[] classes = ClientContext.getClassArray("watingList.columnClasses");
        int[] columnWidth = ClientContext.getIntArray("watingList.columnWidth");
        int startNumRows = ClientContext.getInt("watingList.startNumRows");
        int rowHeight = ClientContext.getInt("watingList.rowHeight");
        Dimension cellSpacing = ClientContext.getDimension("watingList.cellSpacing");
        
        //
        // 年齢表示をしない場合はメソッドを変更する
        //
        if (!ageDisplay) {
            //columnNames[AGE_COLUMN] = "生年月日";
            methodNames[AGE_COLUMN] = AGE_METHOD[1];
        }
        
        //
        // 生成する
        //
        pvtTable = new ObjectListTable(columnNames,startNumRows, methodNames, classes, false);
        pvtTable.getTable().setRowHeight(rowHeight);
        //pvtTable.getTable().setIntercellSpacing(cellSpacing);
        
        // コンテキストメニューを登録する
        pvtTable.getTable().addMouseListener(new ContextListener());     
        
        
        // 来院情報テーブルの属性を設定する
        pvtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pvtTable.setColumnWidth(columnWidth);
        
        // 性別レンダラを生成する
        MaleFemaleRenderer sRenderer = new MaleFemaleRenderer();
        pvtTable.getTable().getColumnModel().getColumn(0).setCellRenderer(sRenderer);
        pvtTable.getTable().getColumnModel().getColumn(2).setCellRenderer(sRenderer);
        pvtTable.getTable().getColumnModel().getColumn(4).setCellRenderer(sRenderer);
        pvtTable.getTable().getColumnModel().getColumn(5).setCellRenderer(sRenderer);
        pvtTable.getTable().getColumnModel().getColumn(6).setCellRenderer(sRenderer);
        
        // Center Renderer
        CenterRenderer centerRenderer = new CenterRenderer();
        pvtTable.getTable().getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        pvtTable.getTable().getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // カルテ状態レンダラ
        KarteStateRenderer renderer = new KarteStateRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        pvtTable.getTable().getColumnModel().getColumn(STATE_COLUMN).setCellRenderer(renderer);
        JScrollPane pvtScroller = pvtTable.getScroller();
        
        // ステータス情報ラベルを生成する
        Font font = new Font("Dialog", Font.PLAIN, ClientContext.getInt("watingList.state.font.size"));
        shoes = new JButton(ClientContext.getImageIcon("kutu01.gif"));
        countLabel = new JLabel("");
        checkTimeLabel = new JLabel("");
        checkIntervalLabel = new JLabel("");
        dayLabel = new JLabel("");
        countLabel.setFont(font);
        checkTimeLabel.setFont(font);
        checkIntervalLabel.setFont(font);
        dayLabel.setFont(font);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        checkTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        checkIntervalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        shoes.setToolTipText(ClientContext.getString("watingList.tooltip.shoesBtn"));
        checkTimeLabel.setToolTipText(ClientContext.getString("watingList.tooltip.checkTimeLabel"));
        countLabel.setToolTipText(ClientContext.getString("watingList.tooltip.countLabel"));
        checkIntervalLabel.setToolTipText(ClientContext.getString("watingList.tooltip.checkIntervalLabel"));
        
        // 日付ラベルに値を設定する
        setOperationDate(new Date());
        
        // チェック間隔情報を設定する
        setCheckInterval(checkInterval);
        
        // 来院数を設定する
        setPvtCount(0);
        
        // 凡例パネルを生成する
        String openText = ClientContext.getString("watingList.state.openText");
        JPanel exp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exp.add(new JLabel(openText, OPEN_ICON, SwingConstants.CENTER));
        exp.add(new JLabel("診察終了", FLAG_ICON, SwingConstants.CENTER));
        
        // Status パネルの左側を生成する
        JPanel kutuP = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        kutuP.add(checkTimeLabel);
        kutuP.add(new SeparatorPanel());
        kutuP.add(countLabel);
        // Status パネルの右側を生成する
        JPanel rightS = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        rightS.add(checkIntervalLabel);
        rightS.add(new SeparatorPanel());
        rightS.add(dayLabel);
        // Status パネルを生成する
        JPanel statusP = new JPanel();
        statusP.setLayout(new BoxLayout(statusP, BoxLayout.X_AXIS));
        statusP.add(shoes);
        statusP.add(Box.createHorizontalStrut(5));
        statusP.add(kutuP);
        statusP.add(Box.createHorizontalGlue());
        statusP.add(rightS);
        
        // 全体をレイアウトする
        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout(0, 7));
        myPanel.add(exp, BorderLayout.NORTH);
        myPanel.add(pvtScroller, BorderLayout.CENTER);
        myPanel.add(statusP, BorderLayout.SOUTH);
        
        getUI().setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
    }
    
    /**
     * コンポーネントにイベントハンドラーを登録し相互に接続する。
     */
    private void connect() {
        
        //
        // Chart のリスナになる
        // 患者カルテの Open/Save/SaveTemp の通知を受けて受付リストの表示を制御する
        //
        ChartPlugin.addPropertyChangeListener(ChartPlugin.CHART_STATE, 
            (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "updateState", "newValue"));
        
        //
        // 選択した来院情報をインスペクタへ通知する
        //
        pvtTable.addPropertyChangeListener(ObjectListTable.SELECTED_OBJECT, 
            (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "maybeSelectedPvt", "newValue"));
        
        //
        // ダブルクリックされた来院情報からカルテを開く
        //
        pvtTable.addPropertyChangeListener(ObjectListTable.DOUBLE_CLICKED_OBJECT,
            (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "openKarte", "newValue"));
        
        //
        // 靴のアイコンをクリックした時来院情報を検索する
        //
        shoes.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "checkFullPvt"));
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
        
        //
        // 受付リストの状態カラムを更新する
        //
        List pvtList = pvtTable.getTableModel().getObjectList();
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
                pvtTable.getTableModel().fireTableRowsUpdated(i, i);
                found = true;
                break;
            }
        }
        
        //
        // データベースを更新する
        //
        if (found && updated.getState() == ChartPlugin.CLOSE_SAVE) {
            
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
            List<ChartPlugin> allCharts = ChartPlugin.getAllChart();
            for (ChartPlugin chart : allCharts) {
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
            if (pvtModel.getState() == ChartPlugin.CANCEL_PVT) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 受付リストのコンテキストメニュークラス。
     */
    class ContextListener extends MouseAdapter {
        
        public void mousePressed(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mabeShowPopup(MouseEvent e) {
            
            if (e.isPopupTrigger()) {
                
                final JPopupMenu contextMenu = new JPopupMenu();
                String pop3 = ClientContext.getString("watingList.popup.oddEvenRenderer");
                String pop4 = ClientContext.getString("watingList.popup.sexRenderer");
                String pop5 = "年齢表示";
                
                int row = pvtTable.getTable().rowAtPoint(e.getPoint());
                Object obj = pvtTable.getTableModel().getObject(row);
                int selected = pvtTable.getTable().getSelectedRow();
                
                if (row == selected && obj != null) {
                    String pop1 = ClientContext.getString("watingList.popup.openKarte");
                    String pop2 = ClientContext.getString("watingList.popup.cancelVisit");
                    contextMenu.add(new JMenuItem(new ReflectAction(pop1, WatingListPlugin.this, "openKarte")));
                    contextMenu.add(new JMenuItem(new ReflectAction(pop2, WatingListPlugin.this, "cancelVisit")));
                    contextMenu.addSeparator();
                }
                
                JRadioButtonMenuItem oddEven = new JRadioButtonMenuItem(new ReflectAction(pop3, WatingListPlugin.this, "switchRenderere"));
                JRadioButtonMenuItem sex = new JRadioButtonMenuItem(new ReflectAction(pop4, WatingListPlugin.this, "switchRenderere"));
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
                item.addActionListener((ActionListener)EventHandler.create(ActionListener.class, WatingListPlugin.this, "switchAgeDisplay"));
                
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
        
        final int selected = pvtTable.getTable().getSelectedRow();
        Object obj = pvtTable.getTableModel().getObject(selected);
        final PatientVisitModel pvtModel = (PatientVisitModel) obj;
        
        //
        // ダイアログを表示し確認する
        //
        
        Object[] cstOptions = new Object[]{"はい", "いいえ"};
        
        StringBuilder sb = new StringBuilder(pvtModel.getPatientName());
        sb.append("様の受付を取り消しますか?");
        
        int select = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(pvtTable.getTable()),
                sb.toString(),
                ClientContext.getFrameTitle(getTitle()),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ClientContext.getImageIcon("cancl_32.gif"),
                cstOptions,
                "はい");
        
        //
        // 受付を取り消す
        //
        if (select == 0) {
//            if (checkTimer != null) {
//                checkTimer.cancel();
//                checkTimer = null;
//            };
            Runnable r = new Runnable() {
                public void run() {
                    pvtModel.setState(ChartPlugin.CANCEL_PVT);
                    PVTDelegater pdl = new PVTDelegater();
                    pdl.updatePvtState(pvtModel.getId(), pvtModel.getState());
                    pvtTable.getTableModel().fireTableRowsUpdated(selected, selected);
//                    Runnable awt = new Runnable() {
//                        public void run() {
//                            restartCheckTimer();
//                        }
//                    };
//                    SwingUtilities.invokeLater(awt);
                }
            };
            Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.run();
        }
    }   
    
    ////////////////////////////////////////////////////////////////////////////
    
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
            SwingUtilities.invokeLater(awt1);
            
            final Date date = new Date();
            final String[] dateToSerach = getSearchDateAsString(date);
            
            //
            // Hibernate の firstResult を現在の件数を保存する
            //
            List dataList = pvtTable.getTableModel().getObjectList();
            int firstResult = dataList != null ? dataList.size() : 0;
            
            logger.info("check PVT at " + date);
            logger.info("first result = " + firstResult);
            
            //
            // 検索する
            //
            final ArrayList result = (ArrayList) getDelegater().getPvt(dateToSerach, firstResult);
            int newVisitCount = result != null ? result.size() : 0;
            logger.info("new visits = " + newVisitCount);
            
            //
            // 結果を追加する
            //
            if (newVisitCount > 0) {
                
                for (int i = 0; i < newVisitCount; i++) {
                    dataList.add(result.get(i));
                }
                
                pvtTable.getTableModel().fireTableRowsInserted(firstResult, dataList.size() - 1);
                
            }
            
            Runnable awt2 = new Runnable() {
                @SuppressWarnings("unchecked")
                public void run() {
                    setCheckedTime(date);
                    setPvtCount(pvtTable.getTableModel().getObjectCount());
                    setBusy(false);
                }
            };
            SwingUtilities.invokeLater(awt2);
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
            SwingUtilities.invokeLater(awt1);
            
            final Date date = new Date();
            final String[] dateToSerach = getSearchDateAsString(date);
            
            //
            // 最初に現れる診察未終了レコードを Hibernate の firstResult にする
            // 現在の件数を保存する
            //
            List dataList = pvtTable.getTableModel().getObjectList();
            int firstResult = 0;
            int curCount = dataList != null ? dataList.size() : 0;
            
            if (dataList != null && curCount > 0) {
                boolean found = false;
                int cnt = curCount;
                for (int i = 0; i < cnt; i++) {
                    PatientVisitModel pvt = (PatientVisitModel) dataList.get(i);
                    if (pvt.getState() == ChartPlugin.CLOSE_NONE) {
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
                    if (pvtU.getState() == ChartPlugin.CLOSE_SAVE && (!isKarteOpened(pvtU))) {
                        pvtC.setState(pvtU.getState());
                    }
                }
                
                //
                // cnt 以降は新しいレコードなのでそのまま追加する
                //
                for (int i = index; i < result.size(); i++) {
                    dataList.add(result.get(index++));
                }
                
                pvtTable.getTableModel().fireTableDataChanged();
                
            }
            
            Runnable awt2 = new Runnable() {
                @SuppressWarnings("unchecked")
                public void run() {
                    setCheckedTime(date);
                    setPvtCount(pvtTable.getTableModel().getObjectCount());
                    setBusy(false);
                }
            };
            SwingUtilities.invokeLater(awt2);
            
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
        
        private static final long serialVersionUID = 7134379493874260895L;
        
        /** Creates new IconRenderer */
        public KarteStateRenderer() {
            super();
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused, row, col);
            
            PatientVisitModel pvt = (PatientVisitModel) pvtTable.getTableModel().getObject(row);
            
            if (isSexRenderer()) {
                
                if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.MALE)) {
                    setBackground(MALE_COLOR);
                } else if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.FEMALE)) {
                    setBackground(FEMALE_COLOR);
                } else {
                    setBackground(Color.WHITE);
                }
                
            } else {
                if (row % 2 == 0) {
                    setBackground(EVEN_COLOR);
                } else {
                    setBackground(ODD_COLOR);
                }
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            Color fore = pvt != null && pvt.getState() == ChartPlugin.CANCEL_PVT ? CANCEL_PVT_COLOR : Color.BLACK;
            this.setForeground(fore);
            
            if (value != null && value instanceof Integer) {
                
                int state = ((Integer) value).intValue();
                
                switch (state) {
                    
                    case ChartPlugin.CLOSE_NONE:
                        //
                        // アイコンなし
                        //
                        setIcon(null);
                        break;
                        
                    case ChartPlugin.CLOSE_SAVE:
                        //
                        // 診察が終了している場合は旗
                        //
                        setIcon(FLAG_ICON);
                        break;
                        
                    case ChartPlugin.OPEN_NONE:
                    case ChartPlugin.OPEN_SAVE:
                        //
                        // オープンしている場合はオープン
                        //
                        setIcon(OPEN_ICON);
                        break;    
                        
                    default:
                        setIcon(null);
                        break;
                }
                ((JLabel) c).setText("");
                
            } else {
                setIcon(null);
                ((JLabel) c).setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
    
    /**
     * KarteStateRenderer
     * カルテ（チャート）の状態をレンダリングするクラス。
     */
    protected class MaleFemaleRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 7134379493874260895L;
        
        /** Creates new IconRenderer */
        public MaleFemaleRenderer() {
            super();
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused, row, col);
            
            PatientVisitModel pvt = (PatientVisitModel) pvtTable.getTableModel().getObject(row);
            
            if (isSexRenderer()) {
                
                if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.MALE)) {
                    setBackground(MALE_COLOR);
                } else if (pvt !=null && pvt.getPatient().getGender().equals(IInfoModel.FEMALE)) {
                    setBackground(FEMALE_COLOR);
                } else {
                    setBackground(Color.WHITE);
                }
                
            } else {
                
                if (row % 2 == 0) {
                    setBackground(EVEN_COLOR);
                } else {
                    setBackground(ODD_COLOR);
                }
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            Color fore = pvt != null && pvt.getState() == ChartPlugin.CANCEL_PVT ? CANCEL_PVT_COLOR : Color.BLACK;
            this.setForeground(fore);
            
            if (value != null && value instanceof String) {
                ((JLabel) c).setText((String) value);
            } else {
                setIcon(null);
                ((JLabel) c).setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
    
    protected class CenterRenderer extends MaleFemaleRenderer {
        
        private static final long serialVersionUID = -4050639296626793056L;
        
        /** Creates new IconRenderer */
        public CenterRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }
    }
}