package open.dolphin.labrcv;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ListSelectionEvent;
import open.dolphin.client.*;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.Window;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeListener;
import java.io.File;

import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import open.dolphin.delegater.LaboDelegater;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.OddEvenRowRenderer;

/**
 * LabTestImporter
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class NLaboTestImporter extends AbstractMainComponent {
    
    private static final String NAME = "ラボレシーバ";
    private static final String DAT_EXT = "DAT";
    private static final String SUCCESS = "成功";
    private static final String ERROR = "エラー";
    
    // 選択されている患者情報
    private NLaboImportSummary selectedLabo;
    private int number = 100000;
   
    // View
    private NLabTestImportView view;

    
    /** Creates new NLaboTestImporter */
    public NLaboTestImporter() {
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
    }

    public JProgressBar getProgressBar() {
        return getContext().getProgressBar();
    }

    public ListTableModel<NLaboImportSummary> getTableModel() {
        return (ListTableModel<NLaboImportSummary>) view.getTable().getModel();
    }
    
    public NLaboImportSummary getSelectedLabo() {
        return selectedLabo;
    }

    public void setSelectedLabo(NLaboImportSummary selectedLabo) {
        this.selectedLabo = selectedLabo;
        controlMenu();
    }
    
    public void openKarte() {
        
        final Preferences pref = Preferences.userNodeForPackage(this.getClass());
        boolean showReceiptMessage = pref.getBoolean("showReceiptMessage", true);
        if (showReceiptMessage) {
            JLabel msg1 = new JLabel("受付リストからオープンしないと診療データをレセコンに");
            JLabel msg2 = new JLabel("送信することができません。続けますか?");
            final JCheckBox cb = new JCheckBox("今後このメッセージを表示しない");
            cb.setFont(new Font("Dialog", Font.PLAIN, 10));
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pref.putBoolean("showReceiptMessage", !cb.isSelected());
                }
            });
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            p1.add(msg1);
            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            p2.add(msg2);
            JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
            p3.add(cb);
            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            box.add(p1);
            box.add(p2);
            box.add(p3);
            box.setBorder(BorderFactory.createEmptyBorder(0, 0, 11, 11));
            
            int option = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(getUI()),
                    new Object[]{box},
                    ClientContext.getFrameTitle(getName()),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    ClientContext.getImageIcon("about_32.gif"));
            
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        PatientModel patient = selectedLabo.getPatient();
        PatientVisitModel pvt = new PatientVisitModel();
        pvt.setNumber(number++);
        pvt.setPatient(patient);
        
        // 受け付けを通していないので診療科はユーザ登録してあるものを使用する
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
        sb.append(",");
        sb.append(Project.getUserModel().getDepartmentModel().getDepartment());
        // Doctor name, id, JMARI code
        pvt.setDepartment(sb.toString());
        
        // カルテコンテナを生成する
        getContext().openKarte(pvt);
    }
    
    
    /**
     * 検索結果件数を設定しステータスパネルへ表示する。
     * @param cnt 件数
     */
    public void updateCount() {
        int count = getTableModel().getObjectCount();
        String text = String.valueOf(count);
        text += "件";
        view.getCountLbl().setText(text);
    }
    
    /**
     * メニューを制御する
     */
    private void controlMenu() {
        
        PatientModel pvt = getSelectedLabo() != null 
                         ? getSelectedLabo().getPatient() 
                         : null;
        
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
            java.util.List<ChartImpl> allCharts = ChartImpl.getAllChart();
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
     * 検査結果ファイルを選択し、パースする。
     */
    private void selectAndParseLabFile() {

        // 検査結果ファイルを選択する
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DAT File", DAT_EXT);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showOpenDialog(getUI());

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // パースしてテーブルへ表示する
        // 登録ボタンをアクティブにする
        final File labFile = new File(chooser.getSelectedFile().getPath());

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<List<NLaboImportSummary>, Void>() {

            @Override
            protected List<NLaboImportSummary> doInBackground() throws Exception {
                NLabParser parse = new NLabParser();
                List<NLaboImportSummary> dataList = parse.parse(labFile);
                return dataList;
            }

            @Override
            protected void done() {

                try {
                    List<NLaboImportSummary> allModules = get();
                    getTableModel().setDataProvider(allModules);

                } catch (Exception e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    Window parent = SwingUtilities.getWindowAncestor(getUI());
                    String message = "パースできないファイルがあります。\n検査報告書フォーマットを確認してください。\n" + why;
                    String title = "ラボレシーバ";
                    JOptionPane.showMessageDialog(parent, message, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
                }
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(javax.swing.SwingWorker.StateValue.STARTED)) {
                    getProgressBar().setIndeterminate(true);
                } else if (evt.getNewValue().equals(javax.swing.SwingWorker.StateValue.DONE)) {
                    getProgressBar().setIndeterminate(false);
                    getProgressBar().setValue(0);
                    worker.removePropertyChangeListener(this);
                }
            }
        });

        worker.execute();
    }

    /**
     * パースした検査結果を登録する。
     */
    private void addLabtest() {

        final List<NLaboImportSummary> modules = getTableModel().getDataProvider();

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                LaboDelegater laboDelegater = new LaboDelegater();

                for (NLaboImportSummary summary : modules) {

                    PatientModel pm = laboDelegater.putNLaboModule(summary.getModule());

                    if (pm != null && laboDelegater.isNoError()) {
                        summary.setPatient(pm);
                        summary.setResult(SUCCESS);

                    } else {
                        summary.setResult(ERROR);
                    }

                    // Table 更新
                    Runnable awt = new Runnable() {
                        @Override
                        public void run() {
                            getTableModel().fireTableDataChanged();
                        }
                    };
                    EventQueue.invokeLater(awt);
                }

                return null;
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(javax.swing.SwingWorker.StateValue.STARTED)) {
                    getProgressBar().setIndeterminate(true);
                } else if (evt.getNewValue().equals(javax.swing.SwingWorker.StateValue.DONE)) {
                    getProgressBar().setIndeterminate(false);
                    getProgressBar().setValue(0);
                    worker.removePropertyChangeListener(this);
                }
            }
        });

        worker.execute();
    }

    
    /**
     * コンポーンントにリスナを登録し接続する。
     */
    private void connect() {

        // ファイル選択ボタン
        view.getFileBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // ファイル選択
                selectAndParseLabFile();
            }
        });

        // 登録ボタン
        view.getAddBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 検査結果登録
                view.getAddBtn().setEnabled(false);
                addLabtest();
            }
        });
        view.getAddBtn().setEnabled(false);

        // クリアボタン
        view.getClearBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 検査結果登録
                getTableModel().setDataProvider(null);
            }
        });
        view.getClearBtn().setEnabled(false);
        
        // 行選択
        view.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    NLaboImportSummary lab = getTableModel().getObject(view.getTable().getSelectedRow());
                    if (lab != null) {
                        setSelectedLabo(lab);
                    }
                }
            }
        });
        
        // ダブルクリック
        view.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    NLaboImportSummary lab = getTableModel().getObject(view.getTable().getSelectedRow());
                    if (lab != null) {
                        openKarte();
                    }
                }
            }
        });

        // コンテキストメニューリスナを設定する
        view.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mabeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mabeShowPopup(e);
            }

            private void mabeShowPopup(MouseEvent e) {

                if (e.isPopupTrigger()) {

                    final JPopupMenu contextMenu = new JPopupMenu();

                    JTable table = view.getTable();
                    int row = table.rowAtPoint(e.getPoint());
                    Object obj = (Object) getTableModel().getObject(row);
                    int selected = table.getSelectedRow();

                    if (row == selected && obj != null) {
                        String pop1 = ClientContext.getString("watingList.popup.openKarte");
                        contextMenu.add(new JMenuItem(new ReflectAction(pop1, NLaboTestImporter.this, "openKarte")));
                    }
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // data 件数リスナ
        getTableModel().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<NLaboImportSummary> list = (List<NLaboImportSummary>) evt.getNewValue();
                boolean enabled = (list != null && list.size() > 0) ? true : false;
                view.getAddBtn().setEnabled(enabled);
                view.getClearBtn().setEnabled(enabled);
                updateCount();
            }
        });
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {

        view = new NLabTestImportView();
        setUI(view);

        String[] columnNames = new String[]{"ラボ", "患者ID", "カナ", "性別", "検体採取日", "項目数", "登録"};
        String[] propNames = new String[]{"laboCode", "patientId", "patientName", "patientSex", "sampleDate", "numOfTestItems", "result"};
        int[] columnWidth = new int[]{50, 120, 120, 50, 120, 50, 80};

        ListTableModel<NLaboImportSummary> tableModel = new ListTableModel<NLaboImportSummary>(
                columnNames, 30, propNames, null);
        view.getTable().setModel(tableModel);
        view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
        view.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTable().setTransferHandler(new NLaboTestFileTransferHandler(this));

        // カラム幅を変更する
        for (int i = 0; i < columnWidth.length; i++) {
            view.getTable().getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        }

        // レンダラを設定する
        view.getTable().setDefaultRenderer(Object.class, new OddEvenRowRenderer());

        // カウント値０を設定する
        updateCount();
    }
}