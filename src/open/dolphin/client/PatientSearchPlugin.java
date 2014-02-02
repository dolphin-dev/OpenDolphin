/*
 * PatientSearch.java
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

import javax.swing.*;
import javax.swing.Timer;

import open.dolphin.delegater.PatientDelegater;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.plugin.IPluginContext;
import open.dolphin.project.Project;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputSubset;
import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import open.dolphin.util.StringTool;

/**
 * 患者検索PatientSearchPlugin
 *
 * @author Kazushi Minagawa
 */
public class PatientSearchPlugin extends DefaultMainWindowPlugin {
    
    private int number = 10000;
    private final String CHART_JNDI = "mainWindow/chart";
    
    // 選択されている患者情報
    private PatientModel selectedPatient;
    private String keyWord;
    private int count;
    
    // GUI コンポーネント
    private JLabel findLabel;
    private JTextField keyWordField;
    private ObjectListTable patientListTable;
    private UltraSonicProgressLabel usp;
    private JLabel countLabel;
    private JLabel dateLabel;
    private ImageIcon findIcon = ClientContext.getImageIcon("srch_16.gif");
    private int keywordFieldLength = 15;
    
    /** 年齢表示 ?? */
    private boolean ageDisplay;
    
    /** 年齢表示カラム */
    private final int AGE_COLUMN = 4;
    
    /** 年齢生年月日メソッド */
    private final String[] AGE_METHOD = new String[]{"getAgeBirthday", "getBirthday"};
    
    // 検索用のスタッフ
    private Timer taskTimer;
    private PatientDelegater pdl;
    private PatientGetTask worker;
    
    
    /** Creates new PatientSearch */
    public PatientSearchPlugin() {
    }
    
    public void start() {
        initComponents();
        connect();
        enter();
        super.start();
    }
    
    // メインウインドウのタブペインでプラグインが切り替わった時、
    // インスペクタに選択されている患者を通知する
    public void enter() {
        controlMenu();
    }
    
    public PatientModel getSelectedPatinet() {
        return selectedPatient;
    }
    
    public void setSelectedPatinet(PatientModel model) {
        selectedPatient = model;
        controlMenu();
    }
    
    /**
     * 検索キーワードを設定し検索を開始する。
     * @param keyWord
     */
    public void setKeyword(String keyWord) {
        this.keyWord = keyWord;
        if (keyWord != null && (!keyWord.equals(""))) {
            find(this.keyWord);
        }
    }
    
    /**
     * 検索結果件数を設定しステータスパネルへ表示する。
     * @param cnt 件数
     */
    public void setCount(int count) {
        this.count = count;
        String text = ClientContext.getString("patientSearch.count.text");
        if (this.count >= 0) {
            text += String.valueOf(this.count);
        } else {
            text += "?";
        }
        countLabel.setText(text);
    }
   
    
    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
        ageDisplay = !ageDisplay;
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        preferences.putBoolean("ageDisplay", ageDisplay);
        
        if (patientListTable != null) {
            String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
            patientListTable.getTableModel().setMethodName(method, AGE_COLUMN);
        }
    }
    
    /**
     * メニューを制御する
     */
    private void controlMenu() {
        
        PatientModel pvt = getSelectedPatinet();
        Action action = getContext().getAction("openKarte");
        boolean enabled = canOpen(pvt);
        if (action != null) {
            action.setEnabled(enabled);
        }
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
            List<ChartPlugin> allCharts = ChartPlugin.getAllChart();
            for (ChartPlugin chart : allCharts) {
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
        
        public void mousePressed(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mabeShowPopup(MouseEvent e) {
            
            if (e.isPopupTrigger()) {
                
                final JPopupMenu contextMenu = new JPopupMenu();
                
                int row = patientListTable.getTable().rowAtPoint(e.getPoint());
                Object obj = patientListTable.getTableModel().getObject(row);
                int selected = patientListTable.getTable().getSelectedRow();
                
                if (row == selected && obj != null) {
                    String pop1 = ClientContext.getString("watingList.popup.openKarte");
                    contextMenu.add(new JMenuItem(new ReflectAction(pop1, PatientSearchPlugin.this, "openKarte")));
                }
                
                JCheckBoxMenuItem item = new JCheckBoxMenuItem("年齢表示");
                contextMenu.add(item);
                item.setSelected(ageDisplay);
                item.addActionListener((ActionListener)EventHandler.create(ActionListener.class, PatientSearchPlugin.this, "switchAgeDisplay"));
                
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    /**
     * GUI コンポーネントを初期化する。
     *
     */
    private void initComponents() {
        
        //
        // 年齢表示をしないなんて信じられない要望!
        //
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        ageDisplay = preferences.getBoolean("ageDisplay", true);
        
        
        // キーワードパネルを生成する
        findLabel = new JLabel(findIcon);
        keyWordField = new JTextField(keywordFieldLength);
        keyWordField.setToolTipText(ClientContext.getString("patientSearch.tooltip.keyword"));
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        textPanel.add(findLabel);
        textPanel.add(keyWordField);
        
        //
        // 患者リストテーブルを生成する
        //
        String[] columnNames = ClientContext.getStringArray("patientSearch.columnNames");
        int startNumRows = ClientContext.getInt("patientSearch.startNumRows");
        String[] methodNames = ClientContext.getStringArray("patientSearch.methodNames");
        int[] columnWidth = ClientContext.getIntArray("patientSearch.columnWidth");
        int rowHeight = ClientContext.getInt("patientSearch.rowHeight");
        patientListTable = new ObjectListTable(columnNames,startNumRows, methodNames, null);
        patientListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientListTable.setColumnWidth(columnWidth);
        patientListTable.getTable().setRowHeight(rowHeight);
        JScrollPane scroller = patientListTable.getScroller();
        
        //
        // 年齢表示をしない場合はメソッドを変更する
        //
        if (!ageDisplay) {
            methodNames[AGE_COLUMN] = AGE_METHOD[1];
        }
        
        // Status パネルを生成する
        usp = new UltraSonicProgressLabel();
        Font font = new Font("Dialog", Font.PLAIN, ClientContext.getInt("watingList.state.font.size"));
        countLabel = new JLabel("");
        dateLabel = new JLabel("");
        countLabel.setFont(font);
        dateLabel.setFont(font);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel statusP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        statusP.add(usp);
        statusP.add(countLabel);
        //statusP.add(new SeparatorPanel(Color.white, Color.lightGray));
        statusP.add(new SeparatorPanel());
        statusP.add(dateLabel);
        
        // カウント値０を設定する
        setCount(0);
        
        // 日付を設定する
        String formatStr = ClientContext.getString("watingList.state.dateFormat");
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr); // 2006-11-20(水)
        dateLabel.setText(sdf.format(new Date()));
        
        // 全体をレイアウトする
        JPanel myPanel = getUI();
        myPanel.setLayout(new BorderLayout(0, 11));
        myPanel.add(textPanel, BorderLayout.NORTH);
        myPanel.add(scroller, BorderLayout.CENTER);
        myPanel.add(statusP, BorderLayout.SOUTH);
        myPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    }
    
    /**
     * コンポーンントにリスナを登録し接続する。
     */
    private void connect() {
        
        //
        // TextField return で検索する
        //
        keyWordField.addActionListener((ActionListener) EventHandler.create(
                ActionListener.class, this, "keyword", "source.text"));
        keyWordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
        });
        
        //
        // カレンダによる日付検索を設定する
        //
        PopupListener pl = new PopupListener(keyWordField);
        
        //
        // 行選択で患者インスペクタへ通知する
        //
        patientListTable.addPropertyChangeListener(ObjectListTable.SELECTED_OBJECT, 
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "maybeSelectedPvt", "newValue"));
        
        //
        // ダブルクリックでカルテオープンする
        //
        patientListTable.addPropertyChangeListener(ObjectListTable.DOUBLE_CLICKED_OBJECT, 
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "maybeOpenKarte", "newValue"));
        
        //
        // コンテキストメニューを設定する
        //
        patientListTable.getTable().addMouseListener(new ContextListener());
        
    }
    
    public void maybeSelectedPvt(Object newValue) {
        if (newValue != null) {
            Object[] obj = (Object[]) newValue;
            // 情報をリフレッシュするため null かどうかに関係なくセットし通知する必要がある
            PatientModel selectedPatient = (obj != null && obj.length > 0) ? (PatientModel) obj[0] : null;
            setSelectedPatinet(selectedPatient);
        }
    }
    
    /**
     * ダブルクリックされた患者のカルテを開く。
     */
    public void maybeOpenKarte(PatientModel patient) {
        
        if (patient != null) {
            setSelectedPatinet(patient);
            openKarte();
        }
    }
    
    /**
     * カルテを開く。
     * @param value 対象患者
     */
    public void openKarte() {
        
//        final Preferences pref = Preferences.userNodeForPackage(this.getClass());
//        boolean showReceiptMessage = pref.getBoolean("showReceiptMessage", true);
//        //showReceiptMessage = true;
//        if (showReceiptMessage) {
//            JLabel msg1 = new JLabel("受付リストからオープンしないと診療データをレセコンに");
//            JLabel msg2 = new JLabel("送信することができません。続けますか?");
//            final JCheckBox cb = new JCheckBox("今後このメッセージを表示しない");
//            cb.setFont(new Font("Dialog", Font.PLAIN, 10));
//            cb.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    pref.putBoolean("showReceiptMessage", !cb.isSelected());
//                }
//            });
//            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
//            p1.add(msg1);
//            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
//            p2.add(msg2);
//            JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
//            p3.add(cb);
//            JPanel box = new JPanel();
//            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
//            box.add(p1);
//            box.add(p2);
//            box.add(p3);
//            box.setBorder(BorderFactory.createEmptyBorder(0, 0, 11, 11));
//            
//            int option = JOptionPane.showConfirmDialog(this.getContext().getFrame(),
//                    new Object[]{box},
//                    ClientContext.getFrameTitle(getTitle()),
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE,
//                    ClientContext.getImageIcon("about_32.gif"));
//            
//            if (option != JOptionPane.YES_OPTION) {
//                return;
//            }
//        }
        if (canOpen(getSelectedPatinet())) {
        
            // 来院情報を生成する
            PatientVisitModel pvt = new PatientVisitModel();
            pvt.setId(0L);
            pvt.setNumber(number++);
            pvt.setPatient(getSelectedPatinet());

            // 受け付けを通していないので診療科はユーザ登録してあるものを使用する
            StringBuilder sb = new StringBuilder();
            sb.append(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
            sb.append(",");
            sb.append(Project.getUserModel().getDepartmentModel().getDepartment());
            // JMARI codeなし
            pvt.setDepartment(sb.toString());
            

            // カルテコンテナを生成する
            try {
                IPluginContext plCtx = ClientContext.getPluginContext();
                ChartPlugin chart = (ChartPlugin) plCtx.lookup(CHART_JNDI);
                chart.setContext(getContext());
                chart.setPatientVisit(pvt);
                chart.setReadOnly(Project.isReadOnly());
                chart.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 検索を実行する。
     * @param text キーワード
     */
    private void find(String text) {
        
        final PatientSearchSpec spec = new PatientSearchSpec();
        
        if (text.startsWith("N ") || text.startsWith("n ")) {
            spec.setCode(PatientSearchSpec.NAME_SEARCH);
            text = text.substring(2);
            spec.setName(text);
            
        } else if (text.startsWith("K ") || text.startsWith("k ")) {
            spec.setCode(PatientSearchSpec.KANA_SEARCH);
            text = text.substring(2);
            spec.setName(text);
            
        } else if (text.startsWith("A ") || text.startsWith("a ")) {
            spec.setCode(PatientSearchSpec.ADDRESS_SEARCH);
            text = text.substring(2);
            spec.setAddress(text);
            
        } else if (text.startsWith("Z ") || text.startsWith("z ")) {
            spec.setCode(PatientSearchSpec.ZIPCODE_SEARCH);
            text = text.substring(2);
            spec.setZipCode(text);
            
        } else if (text.startsWith("T ") || text.startsWith("t ")) {
            spec.setCode(PatientSearchSpec.TELEPHONE_SEARCH);
            text = text.substring(2);
            spec.setTelephone(text);
            
        } else if (text.startsWith("I ") || text.startsWith("i ")) {
            spec.setCode(PatientSearchSpec.ID_SEARCH);
            text = text.substring(2);
            spec.setPatientId(text);
            
        } else if (text.startsWith("E ") || text.startsWith("e ")) {
            spec.setCode(PatientSearchSpec.EMAIL_SEARCH);
            text = text.substring(2);
            spec.setEmail(text);
            
        } else if (text.startsWith("O ") || text.startsWith("o ")) {
            spec.setCode(PatientSearchSpec.OTHERID_SEARCH);
            text = text.substring(2);
            spec.setOtherId(text);    
            
        } else if (isDate(text)) {
            //System.out.println("Date search");
            spec.setCode(PatientSearchSpec.DATE_SEARCH);
            spec.setDigit(text);
            
        } else if (isKatakana(text)) {
            spec.setCode(PatientSearchSpec.KANA_SEARCH);
            spec.setName(text);
            
            
        } else if (isNameAddress(text)) {
            spec.setCode(PatientSearchSpec.NAME_SEARCH);
            spec.setName(text);
            
        }  else if (isTelephoneZip(text)) {
            spec.setCode(PatientSearchSpec.DIGIT_SEARCH);
            spec.setDigit(text);
            
        } else if (isId(text)) {
            spec.setCode(PatientSearchSpec.ID_SEARCH);
            spec.setPatientId(text);
            
        } else {
            String msg = ClientContext.getString("patientSearch.unsuitableChar");
            JOptionPane.showMessageDialog(this.getFrame(), msg, this.getTitle(), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // デリゲータを生成する
        pdl = new PatientDelegater();
        
        // ワーカーを生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        worker = new PatientGetTask(spec, pdl, taskLength);
        
        // タイマーを起動する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                
                if (worker.isDone()) {
                    usp.stop();
                    taskTimer.stop();
                    
                    // エラーをチェックする
                    if (pdl.isNoError()) {
                        // 結果をテーブルへ表示する
                        List results = (ArrayList) pdl.getPatients(spec);
                        patientListTable.setObjectList(results);
                        int cnt = results != null ? results.size() : 0;
                        fireStop(cnt);
                    } else {
                        // エラーダイアログを表示する
                        fireStop(0);
                        JFrame parent = getContext().getFrame();
                        String title = PatientSearchPlugin.this.getTitle();
                        JOptionPane.showMessageDialog(
                                parent,
                                pdl.getErrorMessage(),
                                ClientContext.getFrameTitle(title),
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                } else if (worker.isTimeOver()) {
                    // タイムアウトダイアログを表示する
                    usp.stop();
                    taskTimer.stop();
                    fireStop(0);
                    String title = PatientSearchPlugin.this.getTitle();
                    new TimeoutWarning(getContext().getFrame(), title, null);
                }
            }
        });
        fireStart();
        usp.start();
        worker.start();
        taskTimer.start();
    }
    
    private void fireStart() {
        //keyWordField.setEnabled(false);
        getContext().block();
        setCount(-1);
    }
    
    private void fireStop(final int cnt) {
        setCount(cnt);
        getContext().unblock();
        //keyWordField.setEnabled(true);
    }
    
    private boolean isDate(String text) {
        boolean maybe = false;
        if(text!= null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.parse(text);
                maybe = true;

            } catch (Exception e) {
            }
        }
        
        return maybe;
    }
    
    private boolean isKatakana(String text) {
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
                        c == ')' ) {
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
    
    /**
     * 検索タスク。
     */
    class PatientGetTask extends AbstractInfiniteTask {
        
        // 結果を格納するリスト
        private List result;
        
        // Delegator
        private PatientDelegater pdl;
        
        // 検索パラメータを保持するオブジェクト
        private PatientSearchSpec spec;
        
        /**
         * タスクを生成する。
         * @param spec 検索パラメータを保持するオブジェクト
         * @param ddl Delegator
         */
        public PatientGetTask(PatientSearchSpec spec, PatientDelegater pdl, int taskLength) {
            this.spec = spec;
            this.pdl = pdl;
            setTaskLength(taskLength);
        }
        
        /**
         * 検索結果の文書履歴リストを返す。
         * @return 書履歴リスト
         */
        protected List getDocumentList() {
            return result;
        }
        
        /**
         * タスクを実行する。
         */
        protected void doTask() {
            result = (ArrayList)pdl.getPatients(spec);
            setDone(true);
        }
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
            tf.addMouseListener(this);
        }
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        private void maybeShowPopup(MouseEvent e) {
            
            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(getContext().getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[] { -12, 0 });
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
        
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                tf.setText(SimpleDate.simpleDateToMmldate(sd));
                popup.setVisible(false);
                popup = null;
                //
                // 検索をかける
                //
                setKeyword(tf.getText().trim());
            }
        }
    }
}





