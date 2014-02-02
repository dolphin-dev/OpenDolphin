package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import open.dolphin.delegater.PatientDelegater;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

import java.awt.event.*;
import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import open.dolphin.delegater.PVTDelegater;
import open.dolphin.helper.KeyBlocker;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.WorkerService;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.table.ColumnSpec;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.OddEvenRowRenderer;
import open.dolphin.util.AgeCalculater;
import open.dolphin.util.StringTool;

/**
 * 患者検索PatientSearchPlugin
 *
 * @author Kazushi Minagawa
 */
public class PatientSearchImpl extends AbstractMainComponent {

    private int number = 10000;
    private final String NAME = "患者検索";
    private final String[] COLUMN_NAMES = {"ID", "氏名", "カナ", "性別", "生年月日"};
    private final String[] METHOD_NAMES = {"patientId", "fullName", "kanaName", "genderDesc", "ageBirthday"};
    private final int[] COLUMN_WIDTH = {80, 120, 120, 30, 80};
    private final int START_NUM_ROWS = 30;
    
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
    private List<ColumnSpec> columnSpecs;

    private int ageColumn;

    private ListTableModel tableModel;

    private AbstractAction copyAction;

    /** Creates new PatientSearch */
    public PatientSearchImpl() {
        setName(NAME);
    }

    @Override
    public void start() {
        initComponents();
        connect();
        enter();
    }

    @Override
    public void enter() {
        controlMenu();
    }

    @Override
    public void stop() {
        if (columnSpecs!=null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < columnSpecs.size(); i++) {
                ColumnSpec cs = columnSpecs.get(i);
                cs.setWidth(view.getTable().getColumnModel().getColumn(i).getPreferredWidth());
                sb.append(cs.getName()).append(",");
                sb.append(cs.getMethod()).append(",");
                sb.append(cs.getCls()).append(",");
                sb.append(cs.getWidth()).append(",");
            }
            sb.setLength(sb.length()-1);
            String line = sb.toString();
            Project.setString("patientSearchTable.withoutAddress.column.spec", line);
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
        return (ListTableModel<PatientModel>) view.getTable().getModel();
    }

    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
        if (view.getTable() != null) {
            ageDisplay = !ageDisplay;
            Project.setBoolean("patientSearchTable.withoutAddress.ageDisplay", ageDisplay);
            String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
            ListTableModel tModel = getTableModel();
            tModel.setProperty(method, ageColumn);
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

    /**
     * GUI コンポーネントを初期化する。
     *
     */
    private void initComponents() {

        // Table deafult
        String defaultLine = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            String name = COLUMN_NAMES[i];
            String method = METHOD_NAMES[i];
            String cls = String.class.getName();
            String width = String.valueOf(COLUMN_WIDTH[i]);
            sb.append(name).append(",");
            sb.append(method).append(",");
            sb.append(cls).append(",");
            sb.append(width).append(",");
        }
        sb.setLength(sb.length()-1);
        defaultLine = sb.toString();

        // preference から
        String line = Project.getString("patientSearchTable.withoutAddress.column.spec", defaultLine);

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

        // Scan して age カラムを設定する
        for (int i = 0; i < columnSpecs.size(); i++) {
            ColumnSpec cs = columnSpecs.get(i);
            String test = cs.getMethod();
            if (test.toLowerCase().endsWith("birthday")) {
                ageColumn = i;
                break;
            }
        }

        // View
        view = new PatientSearchView();
        setUI(view);

        ageDisplay = Project.getBoolean("patientSearchTable.withoutAddress.ageDisplay", true);

        len = columnSpecs.size();
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
        tableModel = new ListTableModel<PatientModel>(colunNames, START_NUM_ROWS, methods, cls) {

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

        // 行高
        if (ClientContext.isWin()) {
            view.getTable().setRowHeight(ClientContext.getMoreHigherRowHeight());
        } else {
            view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
        }

        // カラム幅を変更する
        for (int i = 0; i < COLUMN_WIDTH.length; i++) {
            view.getTable().getColumnModel().getColumn(i).setPreferredWidth(
                    width[i]);
        }

        // レンダラを設定する
        view.getTable().setDefaultRenderer(Object.class, new OddEvenRowRenderer());

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
    }

    /**
     * コンポーンントにリスナを登録し接続する。
     */
    private void connect() {

        // Table のカラム変更関連イベント
        view.getTable().getColumnModel().addColumnModelListener(new TableColumnModelListener() {

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
                PatientModel patient = (PatientModel) tableModel.getObject(row);
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
            pvt.setJmariNumber(Project.getJMARICode());

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
        pvt.setJmariNumber(Project.getJMARICode());

        // 来院日
        pvt.setPvtDate(ModelUtils.getDateTimeAsString(new Date()));

        final PatientVisitModel fPvt = pvt;

        SimpleWorker worker = new SimpleWorker<Void, Void>() {
            
            @Override
            protected Void doInBackground() {
                PVTDelegater pdl = new PVTDelegater();
                pdl.setLogger(ClientContext.getPvtLogger());
                pdl.addPvt(fPvt);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
            }

            @Override
            protected void failed(Throwable cause) {
            }
        };

        // 実行とモニタリングサービス
        WorkerService service = new WorkerService() {
            @Override
            protected void startProgress() {
                doStartProgress();
            }

            @Override
            protected void stopProgress() {
                doStopProgress();
            }
        };
        service.execute(worker);
    }


    /**
     * 検索を実行する。
     * @param text キーワード
     */
    private void find(String text) {

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

                ListTableModel<PatientModel> tableModel = getTableModel();
                tableModel.setDataProvider(list);
                int cnt = result != null ? result.size() : 0;
                String cntStr = String.valueOf(cnt);
                view.getCountLbl().setText(cntStr + " 件");
            }

            @Override
            protected void failed(Throwable cause) {
            }
        };

        // 実行とモニタリングサービス
        WorkerService service = new WorkerService() {
            @Override
            protected void startProgress() {
                doStartProgress();
            }

            @Override
            protected void stopProgress() {
                doStopProgress();
            }
        };
        service.execute(worker);
    }

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
}