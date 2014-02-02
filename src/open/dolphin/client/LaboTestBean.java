/*
 * LaboTestBean.java
 *
 * Created on 2001/11/27, 3:47
 *
 * Last updated on 2003/03/06
 *
 */

package open.dolphin.client;

import java.util.*;
import java.util.prefs.Preferences;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import open.dolphin.delegater.LaboDelegater;
import open.dolphin.dto.LaboSearchSpec;
import open.dolphin.project.Project;
import open.dolphin.util.*;

import java.awt.event.*;

/**
 * LaboTestBean
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class LaboTestBean extends DefaultChartDocument {

    private static final int CELL_WIDTH                     = 120;
    private static final int MAX_ITEMS_NONE_FIXED_COLUMNS   = 4;
    private static final int DEFAULT_DIVIDER_LOC            = 210;
    private static final int DEFAULT_DIVIDER_WIDTH          = 10;
    
    private Object[] header;
    private Object[][] laboData;
    private JTable table;
    
    private Vector<SimpleLaboModule> laboModules;
    private AllLaboTest allLaboTest;
    
    private IStatusPanel statusPanel;
    // 抽出期間名リスト
    private NameValuePair[] periodObject = ClientContext.getNameValuePair("docHistory.combo.period");
    private JComboBox extractionCombo;
    private JTextField countField;
    
    private open.dolphin.client.LaboSearchTask searchTask;
    private javax.swing.Timer searchTimer;
    private LaboDelegater ldl;
    
    private JScrollPane jScrollPane1;
    private JRadioButton relativeRadio;
    private JRadioButton absoluteRadio;
    private LaboTestGraph laboTestGraph;
    private int dividerWidth;
    private int dividerLoc;
    
    // 標本及び検査値の表示カラー
    private Color specimenColor = ClientContext.getColor("labotest.color.specimen");
    private Color lowColor = ClientContext.getColor("labotest.color.low");
    private Color normalColor = ClientContext.getColor("labotest.color.normal");
    private Color highColor = ClientContext.getColor("labotest.color.high");
    
    private Preferences myPrefs = Preferences.userNodeForPackage(this.getClass());
    
    class ImageTableCellRenderer extends JLabel implements TableCellRenderer {
        
    	private static final long serialVersionUID = -520905432722518156L;
		
    	private Color penCol = Color.black;
    	private String upperValueText = ClientContext.getString("labotest.value.upperText");
    	private String standardValueText = ClientContext.getString("labotest.value.standardText");
    	private String lowerValueText = ClientContext.getString("labotest.value.lowerText");
    	
        public ImageTableCellRenderer() {
            setOpaque(true);
            setBackground(Color.white);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }

        public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column ) {

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            //-------------------------------------------------------
            if (value != null) {
                
                if (value instanceof java.lang.String) {
                    penCol = Color.black;
                    setForeground(penCol);
                    setText((String)value);
                    setToolTipText("");
                
                } else if (value instanceof open.dolphin.client.SimpleLaboTestItem) {
                    
                    SimpleLaboTestItem testItem = (SimpleLaboTestItem) value;
                    // 検査値表示用カラーを得る
                    String out = testItem.getOut();
                    if (out == null) {
                    	penCol = Color.black;
                    	
                    } else if (out.equals("L")) {
                    	penCol = lowColor;
                        
                    } else if (out.equals("N")) {
                    	penCol = normalColor;
                        
                    } else if (out.equals("H")) {
                    	penCol = highColor;
                        
                    } else {
                    	penCol = Color.black;
                    }
                    
                    setForeground(penCol);
                    setText(testItem.toString());
                    
                    // ToolTips を設定する
                    StringBuilder buf = new StringBuilder();
                    if (testItem.getUp() != null) {
                    	buf.append(upperValueText);
                        buf.append(testItem.getUp());
                        buf.append(") ");
                    }
                    
                    if (testItem.getLow() != null) {
                    	buf.append(lowerValueText);
                        buf.append(testItem.getLow());
                        buf.append(") ");
                    }
                    
                    if (testItem.getNormal() != null) {
                    	buf.append(standardValueText);
                        buf.append(testItem.getNormal());
                        buf.append(")");
                    }
                    
                    if (buf.length() > 0) {
                        setToolTipText(buf.toString());
                    }
                
                } else if (value instanceof open.dolphin.client.SimpleLaboSpecimen) {
                    SimpleLaboSpecimen specimen = (SimpleLaboSpecimen) value;
                    setBackground(specimenColor);	// 標本表示カラー
                    setForeground(Color.black);
                    setText(specimen.toString());
                }
                
            } else {
                penCol = Color.black;
                setForeground(penCol);
                setText("");
                setToolTipText("");
            }
            //-------------------------------------------------------

            return this;
        }
    }

    /**
     * MyTableModel
     */
    class MyTableModel extends AbstractTableModel {
       
    	private static final long serialVersionUID = 3686975270538007030L;
		
    	Object[] columnNames;
        Object[][] data;

        public MyTableModel(Object[] names, Object[][] d) {
            columnNames = names;
            data = d;
        }
        
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return (String)columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }
    
    /**
     * テーブルデータを設定する。
     */
    public void generateObjectsForTable() {
        
        if (laboModules == null || laboModules.size() == 0) {
            statusPanel.setMessage("該当する検査結果はありません。");
            laboModules = null;
            return;
        }
        // 表示用のラベルテキストを得る
        String itemText = ClientContext.getString("labotest.table.itemText");
        String registText = ClientContext.getString("labotest.table.registText");
        String reportText = ClientContext.getString("labotest.table.reportText");
        String statusText = ClientContext.getString("labotest.table.statusText");
        String laboCenterText = ClientContext.getString("labotest.table.laboCenterText");
        String setNameText = ClientContext.getString("labotest.table.setNameText");
        
        // 検索期間内のモジュール数 + 1 がテーブルのカラム数
        int moduleCount = laboModules.size();
        //int columnCount = moduleCount > MAX_ITEMS_NONE_FIXED_COLUMNS ? moduleCount : MAX_ITEMS_NONE_FIXED_COLUMNS;
        
        header = new Object[moduleCount + 1];
        header[0] = itemText;
        for (int i = 1; i <= moduleCount; i++) {
            header[i] = laboModules.get(i-1).getHeader();
        }
        
        // テーブルの行数 = 全テスト項目 + 6
        int rowCount = allLaboTest.getRowCount() + 6;
        laboData = new Object[rowCount][moduleCount + 1];
        laboData[rowCount -6][0] = "";
        laboData[rowCount -5][0] = registText;
        laboData[rowCount -4][0] = reportText;
        laboData[rowCount -3][0] = statusText;
        laboData[rowCount -2][0] = laboCenterText;
        laboData[rowCount -1][0] = setNameText;
        
        // データ配列を生成する
        allLaboTest.fillRow(laboData, 0, 0);
        
        for (int j = 1; j <= moduleCount; j++) {
            SimpleLaboModule sm = laboModules.get(j-1); 
            sm.fillNormaliedData(laboData, j, allLaboTest);
        }
    }
    
    /**
     * テーブルを生成する。
     */
    public void constructTable() {
    	
    	if (laboModules == null) {
    		return;
    	}
        //
        // construct table.
        //
        if (header != null && header.length > 0 && laboData != null) {
            
            // 固定列を実装する
        	// 
            if (laboModules != null && laboModules.size() > MAX_ITEMS_NONE_FIXED_COLUMNS) {
                
                DefaultTableModel model = new DefaultTableModel(laboData, header);
                table.setModel(model);
                table.getTableHeader().setUpdateTableInRealTime(false);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                
                JTable table2 = new JTable(model);
                table2.getTableHeader().setUpdateTableInRealTime(false);
                table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                
                TableColumnModel tcm = table.getColumnModel();
                TableColumnModel tcm2 = new DefaultTableColumnModel();

                // 検査項目名の列を table から削除し table2 へ加える
                TableColumn col = tcm.getColumn(0);
                tcm.removeColumn(col);
                tcm2.addColumn(col);

                col = tcm2.getColumn(0);
                col.setMinWidth(CELL_WIDTH);
                col.setPreferredWidth(CELL_WIDTH);
                table2.setColumnModel(tcm2);
                table2.setPreferredScrollableViewportSize(table2.getPreferredSize());

                tcm = table.getColumnModel();
                int cols = tcm.getColumnCount();
                for (int i = 0; i < cols; i++) {
                    col = tcm.getColumn(i);
                    col.setMinWidth(CELL_WIDTH);
                    col.setPreferredWidth(CELL_WIDTH);
                }
                jScrollPane1.setViewportView(table);
                jScrollPane1.setRowHeaderView(table2);
                jScrollPane1.setCorner(JScrollPane.UPPER_LEFT_CORNER, table2.getTableHeader());
                
            } else {
                
                MyTableModel model = new MyTableModel(header, laboData);
                table.setModel(model);
                jScrollPane1.setViewportView(table);
            }
            
            table.setRowSelectionAllowed(true);
        
            ListSelectionModel m = table.getSelectionModel();
            m.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        createLaboTestGraph();
                    }
                }
            });
            
            table.setDefaultRenderer(table.getColumnClass(0), new ImageTableCellRenderer());
            
            allLaboTest.clear();
            allLaboTest = null;
            laboModules.clear();
            laboModules = null;
        }
    }    

    /**
     * GUIコンポーネントを初期化する。
     */
    public void initialize() {
    	
    	this.statusPanel = getContext().getStatusPanel();
        
        // Divider
        dividerWidth = DEFAULT_DIVIDER_WIDTH;
        dividerLoc = DEFAULT_DIVIDER_LOC;
                
        JPanel controlPanel = createControlPanel();
        
        laboTestGraph = new LaboTestGraph();
        laboTestGraph.setPreferredSize(new Dimension(500, dividerLoc));
        
        table = new JTable();
        
        jScrollPane1 = new JScrollPane();
        jScrollPane1.setPreferredSize(new java.awt.Dimension(3, 600));
        
        JPanel tablePanel = new JPanel(new BorderLayout(0, 7));
        tablePanel.add(controlPanel, BorderLayout.SOUTH);
        tablePanel.add(jScrollPane1, BorderLayout.CENTER);
        
        // Lyouts
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, laboTestGraph, tablePanel);
        splitPane.setDividerSize(dividerWidth);
        splitPane.setContinuousLayout(false);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(dividerLoc);
        
        getUI().setLayout(new BorderLayout());
        getUI().add(splitPane, BorderLayout.CENTER);
        
        getUI().setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    }

    /**
     * ラボモジュールを検索、表示する。
     */
    public void start() {
    	NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
		String value = pair.getValue();
		int addValue = Integer.parseInt(value);
		GregorianCalendar today = new GregorianCalendar(); 
		today.add(GregorianCalendar.MONTH, addValue);                    
        searchLaboTest(MMLDate.getDate(today));
    }

    /**
     * LaboTest の検索タスクをコールする。
     */
    private void searchLaboTest(String fromDate) {
        
        statusPanel.setMessage("サーバーへ接続中...");
        countField.setText("");

        table.removeAll();
        table = new JTable();
        jScrollPane1.setViewportView(table);

        LaboSearchSpec spec = new LaboSearchSpec();
        spec.setKarteId(getContext().getKarte().getId());
        spec.setFromDate(fromDate);
        spec.setToDate(MMLDate.getDate());
        ldl = new LaboDelegater();
        
        int delay = ClientContext.getInt("labotest.task.delay");
        int maxEstimation = ClientContext.getInt("labotest.task.maxEstimation");
        searchTask = new LaboSearchTask(spec, ldl, maxEstimation/delay);
        
        statusPanel.setMessage(searchTask.getMessage());
        searchTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {

            	searchTask.getCurrent();
                statusPanel.setMessage(searchTask.getMessage());
                
                if (searchTask.isDone()) {
                    searchTimer.stop();
                    statusPanel.stop(searchTask.getMessage());
                    
                    if (ldl.isNoError()) {
	                    laboModules = searchTask.getLaboModuleColumns();
	                    allLaboTest = searchTask.getAllLaboTest();
	                    int count = laboModules != null ? laboModules.size() : 0;
	                    countField.setText(String.valueOf(count));
	                    generateObjectsForTable();
	                    constructTable();
	                    
                    } else {
                    	warning(ClientContext.getString("labotest.title"), ldl.getErrorMessage());
                    }
                    
                } else if (searchTask.isTimeOver()) {
                	statusPanel.stop();
                	searchTimer.stop();
					JFrame parent = getContext().getFrame();
					String title = ClientContext.getString("labotest.title");
					new TimeoutWarning(parent, title, null).start();
                }
            }
        });
        searchTask.start();
        statusPanel.start();
        searchTimer.start();
    }
    
    
    @SuppressWarnings({"unchecked"})
	private void createLaboTestGraph() {
        
        int[] selectedRows = table.getSelectedRows();
        
        if (selectedRows == null || selectedRows.length == 0 || laboTestGraph == null) {
            return;
        }
        
        ArrayList retList = null;
        ArrayList list = null;
        
        int columnCount = table.getColumnCount();
        //int columnCount = laboModules.size();
        
        //int startCol = fixedColumnMode ? 0 : 1;
        boolean hasNonNull = false;
        
        for (int i = 0; i < selectedRows.length; i++) {
            
            list = new ArrayList();
            hasNonNull = false;
                    
            for (int j = 1; j < columnCount; j++) {
               
                Object o = table.getValueAt(selectedRows[i], j);
                
                if (o != null && o instanceof SimpleLaboTestItem) {
                    
                    SimpleLaboTestItem item = (SimpleLaboTestItem) o;
                    String value = item.getItemValue();
                    
                    if (value != null) {
                        
                        try {
                            Float.parseFloat(value);
                            list.add(item);
                            hasNonNull = true;
                        } catch (NullPointerException nulle) {
                            list.add(null);
                        } catch (NumberFormatException ne) {
                            list.add(null);
                        } catch (Exception oe) {
                            list.add(null);
                        }
                    
                    } else {
                        list.add(null);
                    }
                
                } else {
                    list.add(null);
                }
            }
            
            if (hasNonNull) {
                if (retList == null) {
                    retList = new ArrayList();
                }
                retList.add(list);
            }
        }
        
        // Test
        if (retList == null || retList.size() == 0) {
            return;
        }
        
        String[] sampleTime = new String[header.length - 1];
        for (int j = 1; j < header.length; j++) {
            String val = (String) header[j];
            if (val != null) {
	            int index = val.indexOf(": ");
	            sampleTime[j-1] = index > 0 ? val.substring(index + 1) : val;
            } else {
            	sampleTime[j-1] = null;
            }
        }
        
        laboTestGraph.setTestValue(sampleTime, retList, getMyGraphMode());
    }
    
    private int getMyGraphMode() {
        return absoluteRadio.isSelected() ? 0 : 1;
    }
    
    /**
     * 抽出期間パネルを返す
     */
    private JPanel createControlPanel() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(7));

        // 抽出期間コンボボックス
        p.add(new JLabel("抽出期間(過去)"));
        p.add(Box.createRigidArea(new Dimension(5,0)));
        extractionCombo = new JComboBox(periodObject);
        
        // Preference値を選択
        int past = Project.getPreferences().getInt(Project.LABOTEST_PERIOD, -6);
        int index = NameValuePair.getIndex(String.valueOf(past), periodObject);
        extractionCombo.setSelectedIndex(index);
        extractionCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	NameValuePair pair = (NameValuePair) extractionCombo.getSelectedItem();
					String value = pair.getValue();
					int addValue = Integer.parseInt(value);
					GregorianCalendar today = new GregorianCalendar(); 
					today.add(GregorianCalendar.MONTH, addValue);                    
                    searchLaboTest(MMLDate.getDate(today));
                }
            }
        });
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        comboPanel.add(extractionCombo);
        
        p.add(comboPanel);

        // スペース
        p.add(Box.createHorizontalStrut(7));

        // 件数フィールド
        p.add (new JLabel("件数"));
        p.add(Box.createRigidArea(new Dimension(5,0)));
        countField = new JTextField(2);
        countField.setEditable(false);
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        countPanel.add(countField);
        p.add(countPanel);
        
        // グル
        p.add(Box.createHorizontalGlue());
     
        relativeRadio = new JRadioButton("相対グラフ");
        absoluteRadio = new JRadioButton("絶対値グラフ");
        ButtonGroup bg = new ButtonGroup();
        bg.add(relativeRadio);
        bg.add(absoluteRadio);
        p.add(relativeRadio);
        p.add(Box.createHorizontalStrut(5));
        p.add(absoluteRadio);
        
        boolean bAbsolute = myPrefs.getBoolean("laboTestDocument.absoluteGraphProp", true);
        relativeRadio.setSelected(! bAbsolute);
        absoluteRadio.setSelected(bAbsolute);
        
        ActionListener al = new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                boolean b = absoluteRadio.isSelected();
                myPrefs.putBoolean("laboTestDocument.absoluteGraphProp", b);
           
                if (laboTestGraph == null) {
                    return;
                }
                
                int myMode = getMyGraphMode();
                int mode = laboTestGraph.getMode();
                if (myMode != mode) {
                    if (laboTestGraph != null) {
                        laboTestGraph.setMode(myMode);
                    }
                }
            }
        };
        relativeRadio.addActionListener(al);
        absoluteRadio.addActionListener(al);

        // スペース
        p.add(Box.createHorizontalStrut(7));
        
        return p;
    }
}
