package open.dolphin.impl.lbtest;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.ClientContext;
import open.dolphin.client.NameValuePair;
import open.dolphin.delegater.LaboDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.*;
import open.dolphin.table.ListTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * LaboTestBean
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class LaboTestBean extends AbstractChartDocument {

    private static final String TITLE = "ラボテスト";
    private static final int DEFAULT_DIVIDER_LOC = 210;
    private static final int DEFAULT_DIVIDER_WIDTH = 10;
    private static final String COLUMN_HEADER_ITEM = "項 目";
    private static final String GRAPH_TITLE = "検査結果";
    private static final String X_AXIS_LABEL = "検体採取日";
    private static final String GRAPH_TITLE_LINUX = "Lab. Test";
    private static final String X_AXIS_LABEL_LINUX = "Sampled Date";
    private static final int FONT_SIZE_WIN = 12;
    private static final String FONT_MS_GOTHIC = "MSGothic";
    //private static final int MAX_RESULT = 5;
    //private static final String[] EXTRACTION_MENU = new String[]{"5回分", "0", "6~10回分", "5"};
    private static final int MAX_RESULT = 6;
    private static final String[] EXTRACTION_MENU = new String[]{"6回分", "0", "7~12回分", "6"};

    private ListTableModel<LabTestRowObject> tableModel;
    private JTable table;
    private JPanel graphPanel;

    private JComboBox extractionCombo;
    private JTextField countField;
    private LaboDelegater ldl;
    private int dividerWidth;
    private int dividerLoc;

    // １回の検索で得る抽出件数
    private int maxResult = MAX_RESULT;

    // 抽出メニュー
    private String[] extractionMenu = EXTRACTION_MENU;

    private boolean widthAdjusted;
    
    public LaboTestBean() {
        setTitle(TITLE);
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public String[] getExtractionMenu() {
        return extractionMenu;
    }

    public void setExtractionMenu(String[] extractionMenu) {
        this.extractionMenu = extractionMenu;
    }

    public void createTable(List<NLaboModule> modules) {

        // 現在のデータをクリアする
        if (tableModel != null && tableModel.getDataProvider() != null) {
            tableModel.getDataProvider().clear();
        }

        // グラフもクリアする
        graphPanel.removeAll();
        graphPanel.validate();

        // Table のカラムヘッダーを生成する
        String[] header = new String[getMaxResult() + 1];
        header[0] = COLUMN_HEADER_ITEM;
        for (int col = 1; col < header.length; col++) {
            header[col] = "";
        }

        // 結果がゼロであれば返る
        if (modules == null || modules.isEmpty()) {
            tableModel = new ListTableModel<LabTestRowObject>(header, 0);
            table.setModel(tableModel);
            setColumnWidth();
            return;
        }

        // 検体採取日の降順なので昇順にソートする
        Collections.sort(modules, new SampleDateComparator());

        // テスト項目全てに対応する rowObject を生成する
        List<LabTestRowObject> dataProvider = new ArrayList<LabTestRowObject>();

        int moduleIndex = 0;

        for (NLaboModule module : modules) {

            // 検体採取日
            header[moduleIndex+1] = module.getSampleDate();

            // モジュールに含まれる検査項目
            Collection<NLaboItem> c = module.getItems();

            for (NLaboItem item : c) {

                // RowObject を生成し dataProvider へ加える
                // 最初のモジュールのテスト項目は無条件に加える
                if (moduleIndex == 0) {
                    // row
                    LabTestRowObject row = new LabTestRowObject();
                    row.setLabCode(item.getLaboCode());
                    row.setGroupCode(item.getGroupCode());
                    row.setParentCode(item.getParentCode());
                    row.setItemCode(item.getItemCode());
                    row.setItemName(item.getItemName());
                    row.setUnit(item.getUnit());
                    row.setNormalValue(item.getNormalValue());
                    // valueを moduleIndex番目にセットする
                    LabTestValueObject value = new LabTestValueObject();
                    value.setSampleDate(module.getSampleDate());
                    value.setValue(item.getValue());
                    value.setOut(item.getAbnormalFlg());
                    value.setComment1(item.getComment1());
                    value.setComment2(item.getComment2());
                    row.addLabTestValueObjectAt(moduleIndex, value);
                    //
                    dataProvider.add(row);
                    continue;
                }

                // 二つ目のモジュールからは無かったら加える
                boolean found = false;

                for (LabTestRowObject rowObject : dataProvider) {
                    if (item.getItemCode().equals(rowObject.getItemCode())) {
                        found = true;
                        LabTestValueObject value = new LabTestValueObject();
                        value.setSampleDate(module.getSampleDate());
                        value.setValue(item.getValue());
                        value.setOut(item.getAbnormalFlg());
                        value.setComment1(item.getComment1());
                        value.setComment2(item.getComment2());
                        rowObject.addLabTestValueObjectAt(moduleIndex, value);
                        break;
                    }
                }

                if (!found) {
                    LabTestRowObject row = new LabTestRowObject();
                    row.setLabCode(item.getLaboCode());
                    row.setGroupCode(item.getGroupCode());
                    row.setParentCode(item.getParentCode());
                    row.setItemCode(item.getItemCode());
                    row.setItemName(item.getItemName());
                    row.setUnit(item.getUnit());
                    row.setNormalValue(item.getNormalValue());
                    //
                    LabTestValueObject value = new LabTestValueObject();
                    value.setSampleDate(module.getSampleDate());
                    value.setValue(item.getValue());
                    value.setOut(item.getAbnormalFlg());
                    value.setComment1(item.getComment1());
                    value.setComment2(item.getComment2());
                    row.addLabTestValueObjectAt(moduleIndex, value);
                    //
                    dataProvider.add(row);
                }
            }

            moduleIndex++;
        }

        // dataProvider の要素 rowObject をソートする
        // grpuCode,parentCode,itemcode;
        Collections.sort(dataProvider);

        // Table Model
        tableModel = new ListTableModel<LabTestRowObject>(header, 0);

        // 検査結果テーブルを生成する
        table.setModel(tableModel);
        setColumnWidth();

        // dataProvider
        tableModel.setDataProvider(dataProvider);
    }

    /**
     * Tableのカラム幅を調整する。
     */
    private void setColumnWidth() {
        // カラム幅を調整する
        if (!widthAdjusted) {
            table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(180);
            table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getTableHeader().getColumnModel().getColumn(4).setPreferredWidth(100);
            table.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(100);
            widthAdjusted = true;
        }
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private void initialize() {

        // Divider
        dividerWidth = DEFAULT_DIVIDER_WIDTH;
        dividerLoc = DEFAULT_DIVIDER_LOC;

        JPanel controlPanel = createControlPanel();

        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setPreferredSize(new Dimension(500, dividerLoc));

        // 検査結果テーブルを生成する
        table = new JTable();

        // 行高
        table.setRowHeight(ClientContext.getHigherRowHeight());

        // Rendererを設定する
        table.setDefaultRenderer(Object.class, new LabTestRenderer());

        // 行選択を可能にする
        table.setRowSelectionAllowed(true);

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        final AbstractAction copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        
        final AbstractAction copyLatestAction = new AbstractAction("直近の結果のみコピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyLatest();
            }
        };

        table.getInputMap().put(copy, "Copy");
        table.getActionMap().put("Copy", copyLatestAction);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                mabeShowPopup(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mabeShowPopup(me);
            }

            public void mabeShowPopup(MouseEvent e) {

                if (!e.isPopupTrigger()) {
                    return;
                }

                int row = table.rowAtPoint(e.getPoint());

                if (row < 0 ) {
                    return;
                }

                JPopupMenu contextMenu = new JPopupMenu();
                contextMenu.add(new JMenuItem(copyLatestAction));
                contextMenu.add(new JMenuItem(copyAction));

                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });


        // グラフ表示のリスナを登録する
        ListSelectionModel m = table.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    createAndShowGraph(table.getSelectedRows());
                }
            }
        });

        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(table);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(3, 600));

        JPanel tablePanel = new JPanel(new BorderLayout(0, 7));
        tablePanel.add(controlPanel, BorderLayout.SOUTH);
        tablePanel.add(jScrollPane1, BorderLayout.CENTER);

        // Lyouts
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphPanel, tablePanel);
        splitPane.setDividerSize(dividerWidth);
        splitPane.setContinuousLayout(false);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(dividerLoc);

        getUI().setLayout(new BorderLayout());
        getUI().add(splitPane, BorderLayout.CENTER);

        getUI().setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    }

    @Override
    public void start() {
        initialize();
        NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
        String value = pair.getValue();
        int firstResult = Integer.parseInt(value);
        searchLaboTest(firstResult);
    }

    @Override
    public void stop() {
        if (tableModel != null && tableModel.getDataProvider() != null) {
            tableModel.getDataProvider().clear();
        }
    }

    /**
     * 選択されている行で直近のデータをコピーする。
     */
    public void copyLatest() {
        StringBuilder sb = new StringBuilder();
        int numRows=table.getSelectedRowCount();
        int[] rowsSelected=table.getSelectedRows();
        for (int i = 0; i < numRows; i++) {
            LabTestRowObject rdm = tableModel.getObject(rowsSelected[i]);
            if (rdm != null) {
                sb.append(rdm.toClipboardLatest()).append("\n");
            }
        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        StringBuilder sb = new StringBuilder();
        int numRows=table.getSelectedRowCount();
        int[] rowsSelected=table.getSelectedRows();
        for (int i = 0; i < numRows; i++) {
            LabTestRowObject rdm = tableModel.getObject(rowsSelected[i]);
            if (rdm != null) {
                sb.append(rdm.toClipboard()).append("\n");
            }
        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }

    /**
     * LaboTest の検索タスクをコールする。
     */
    private void searchLaboTest(final int firstResult) {

        final String pid = getContext().getPatient().getPatientId();
        ldl = new LaboDelegater();

        DBTask task = new DBTask<List<NLaboModule>, Void>(getContext()) {

            @Override
            protected List<NLaboModule> doInBackground() throws Exception {

                List<NLaboModule> modules = ldl.getLaboTest(pid, firstResult, getMaxResult());
                return modules;
            }

            @Override
            protected void succeeded(List<NLaboModule> modules) {
                int moduleCount = modules != null ? modules.size() : 0;
                countField.setText(String.valueOf(moduleCount));
                createTable(modules);
            }
        };

        task.execute();

    }

    /**
     * 検査結果テーブルで選択された行（検査項目）の折れ線グラフを生成する。
     * 複数選択対応
     * JFreeChart を使用する。
     */
    private void createAndShowGraph(int[] selectedRows) {

        if (selectedRows == null || selectedRows.length == 0) {
            return;
        }
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 選択されている行（検査項目）をイテレートし、dataset へ値を設定する
        for (int cnt = 0; cnt < selectedRows.length; cnt++) {

            int row = selectedRows[cnt];
            List<LabTestRowObject> dataProvider = tableModel.getDataProvider();
            LabTestRowObject rowObj = dataProvider.get(row);
            List<LabTestValueObject> values = rowObj.getValues();

            boolean valueIsNumber = true;
            
            // 検体採取日ごとの値を設定する
            // カラムの１番目から採取日がセットされている
            for (int col = 1; col < getMaxResult(); col++) {

                String sampleTime = tableModel.getColumnName(col);

                // 検体採取日="" -> 検査なし
                if (sampleTime.equals("")) {
                    break;
                }

                LabTestValueObject value = values.get(col -1);

                try {
                    if (value != null) {
                        double val = Double.parseDouble(value.getValue());
                        dataset.setValue(val, rowObj.nameWithUnit(), sampleTime);
                    } else {
                        dataset.setValue(null, rowObj.nameWithUnit(), sampleTime);
                    }

                } catch (Exception e) {
                    valueIsNumber = false;
                    break;
                }
            }

            if (!valueIsNumber) {
                return;
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                    getGraphTitle(),                // Title
                    getXLabel(),                    // x-axis Label
                    "",                             // y-axis Label
                    dataset,                        // Dataset
                    PlotOrientation.VERTICAL,       // Plot Orientation
                    true,                           // Show Legend
                    true,                           // Use tooltips
                    false                           // Configure chart to generate URLs?
                    );

        // Win の文字化け
        if (ClientContext.isWin()) {
            chart.getTitle().setFont(getWinFont());
            chart.getLegend().setItemFont(getWinFont());
            chart.getCategoryPlot().getDomainAxis().setLabelFont(getWinFont());
            chart.getCategoryPlot().getDomainAxis().setTickLabelFont(getWinFont());
        }

        ChartPanel chartPanel = new ChartPanel(chart);

        graphPanel.removeAll();
        graphPanel.add(chartPanel, BorderLayout.CENTER);
        graphPanel.validate();
    }

    //====================================================================
    private String getGraphTitle() {
        return ClientContext.isLinux() ? GRAPH_TITLE_LINUX : GRAPH_TITLE;
    }

    private String getXLabel() {
        return ClientContext.isLinux() ? X_AXIS_LABEL_LINUX : X_AXIS_LABEL;
    }

    private Font getWinFont() {
        return new Font(FONT_MS_GOTHIC, Font.PLAIN, FONT_SIZE_WIN);
    }
    //====================================================================

    /**
     * 抽出期間パネルを返す
     */
    private JPanel createControlPanel() {

        String[] menu = getExtractionMenu();
        int cnt = menu.length / 2;
        NameValuePair[] periodObject = new NameValuePair[cnt];
        int valIndex = 0;
        for (int i = 0; i < cnt; i++) {
            periodObject[i] = new NameValuePair(menu[valIndex], menu[valIndex+1]);
            valIndex += 2;
        }

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(7));

        // 抽出期間コンボボックス
        p.add(new JLabel("過去"));
        p.add(Box.createRigidArea(new Dimension(5, 0)));
        extractionCombo = new JComboBox(periodObject);

        extractionCombo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
                    int firstResult = Integer.parseInt(pair.getValue());
                    searchLaboTest(firstResult);
                }
            }
        });
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboPanel.add(extractionCombo);

        p.add(comboPanel);

        // グル
        p.add(Box.createHorizontalGlue());

        // 件数フィールド
        p.add(new JLabel("件数"));
        p.add(Box.createRigidArea(new Dimension(5, 0)));
        countField = new JTextField(2);
        countField.setEditable(false);
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        countPanel.add(countField);
        p.add(countPanel);

        // スペース
        p.add(Box.createHorizontalStrut(7));

        return p;
    }
}
