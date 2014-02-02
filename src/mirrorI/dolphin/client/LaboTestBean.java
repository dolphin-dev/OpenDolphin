/*
 * LaboTestBean.java
 *
 * Created on 2001/11/27, 3:47
 *
 * Last updated on 2003/03/06
 *
 */

package mirrorI.dolphin.client;


/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
import java.util.GregorianCalendar;
//----------------------------------------
import java.util.*;
//
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.Velocity;

import open.dolphin.client.*;
import open.dolphin.project.*;
import open.dolphin.util.*;
//
import java.awt.event.*;
//import java.io.BufferedWriter;
//import java.io.StringWriter;


public class LaboTestBean extends DefaultChartDocument {
    //---------------------------------------------------------------------------------------------------------------
    private String patientId;
    private boolean isLocalId = true;
   //---------------------------------------------------------------------------------------------------------------

    private static final int CELL_WIDTH                     = 120;
    private static final int MAX_ITEMS_NONE_FIXED_COLUMNS   = 8;
    private static final int DEFAULT_DIVIDER_LOC            = 210;
    private static final int DEFAULT_DIVIDER_WIDTH          = 10;
    
    private Object[] header;
    private Object[][] laboData;
    private JTable table;
    
    private Vector laboModules;
    private AllLaboTest allLaboTest;

    //To get INI file parameter
    private Properties laboTestParameter;
    
    private StatusPanel statusPanel;
    // 抽出期間名リスト
    private static final String[] periodList = ClientContext.getStringArray("filter.combo.periodName");
    // 抽出期間値リスト
    private static final String[] periodValueList = ClientContext.getStringArray("filter.combo.periodValue");
    private int periodIndex = 4;
    private JComboBox extractionCombo;
    private JTextField countField;
    
    private mirrorI.dolphin.client.LaboTestSearchTask searchTask;
    private javax.swing.Timer searchTimer;
    private final int SEARCH_TIMER_INTERVAL = 200;
    private JScrollPane jScrollPane1;
    private JRadioButton relativeRadio;
    private JRadioButton absoluteRadio;
    private LaboTestGraph laboTestGraph;
    private boolean fixedColumnMode;
    private int dividerWidth;
    private int dividerLoc;
    
    //For logging messages
    //private static Logger logger;
    //private static FileHandler fileHandler;
    
    class ImageTableCellRenderer extends JLabel implements TableCellRenderer {
        Color penCol = Color.black;

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
                
                } else if (value instanceof mirrorI.dolphin.client.SimpleLaboTestItem) {
                    
                    SimpleLaboTestItem testItem = (SimpleLaboTestItem)value;
                    penCol = testItem.getStatusColor();
                    setForeground(penCol);
                    setText(testItem.toString());
                    
                    // ToolTips を設定する
                    StringBuffer buf = new StringBuffer();
                    buf.append("上限値(");
                    if (testItem.getUp() != null) {
                        buf.append(testItem.getUp());
                    }
                    buf.append(")");
                    
                    buf.append(" 下限値(");
                    if (testItem.getLow() != null) {
                        buf.append(testItem.getLow());
                    }
                    buf.append(")");
                    
                    buf.append(" 基準値(");
                    if (testItem.getNormal() != null) {
                        buf.append(testItem.getNormal());
                    }
                    buf.append(")");
                    
                    /*buf.append(" 異常値フラグ(");
                    if (testItem.getOut() != null) {
                        buf.append(testItem.getOut());
                    }
                    buf.append(")");*/
                    
                    if (buf.length() > 0) {
                    
                        setToolTipText(buf.toString());
                    }
                
                } else if (value instanceof mirrorI.dolphin.client.SimpleLaboSpecimen) {
                    
                    SimpleLaboSpecimen specimen = (SimpleLaboSpecimen)value;
                    setBackground(Color.yellow);
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

    class MyTableModel extends AbstractTableModel {
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
    
    public void generateObjectsForTable() {
        
        if (laboModules == null || laboModules.size() == 0) {
            statusPanel.setMessage("該当する検査結果はありません。");
            laboModules = null;
            return;
        }
        int moduleCount = laboModules.size();
        
        header = new Object[moduleCount + 1];
        header[0] = " 項  目";
        for (int i = 1; i <= moduleCount; i++) {
            header[i] = ((SimpleLaboModule)laboModules.get(i-1)).getHeader();
        }
        
        int rowCount = allLaboTest.getRowCount() + 6;
        laboData = new Object[rowCount][moduleCount + 1];
        laboData[rowCount -6][0] = "";
        laboData[rowCount -5][0] = "登 録";
        laboData[rowCount -4][0] = "報 告";
        laboData[rowCount -3][0] = "ステータス";
        laboData[rowCount -2][0] = "検査センター";
        laboData[rowCount -1][0] = "セット名";
        
        allLaboTest.fillRow(laboData, 0, 0);
        
        for (int j = 1; j <= moduleCount; j++) {
                            
            SimpleLaboModule sm = (SimpleLaboModule)laboModules.get(j-1); 
            sm.fillNormaliedData(laboData, j, allLaboTest);

        }
    }
    
    public void constructTable() {
        //
        // construct table.
        //
        if (header != null && header.length > 0 && laboData != null) {
            
            // 固定列の実装
            if (laboModules.size() > MAX_ITEMS_NONE_FIXED_COLUMNS) {
                
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
                
                fixedColumnMode = true;
                
            } else {
                
                MyTableModel model = new MyTableModel(header, laboData);
                table.setModel(model);
                jScrollPane1.setViewportView(table);
                
                fixedColumnMode = false;
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
        else {
            statusPanel.setMessage("データ構築に失敗しました。");
        }
    }    

    //==========================================================================

    /** Creates new form LaboTestBean */
    public LaboTestBean(String patientId, boolean isLocalId, StatusPanel statusPanel) {
        super();
        
        this.statusPanel =statusPanel;
        initComponents();
        this.patientId = patientId;
        this.isLocalId = isLocalId;

        laboTestParameter = new Properties();
        String Driver 	= "org.postgresql.Driver";
        String Host 	= Project.getHostAddress();
        String Port 	= String.valueOf(5432);
        String DBName	= "dolphin";
        String DBUser	= "dolphin";
        String DBPwd	= "";
        laboTestParameter.put("Driver", Driver);
        laboTestParameter.put("Host", Host);
        laboTestParameter.put("Port", Port);
        laboTestParameter.put("DBName", DBName);
        laboTestParameter.put("DBUser", DBUser);
        laboTestParameter.put("DBPwd", DBPwd);
        
        extractionCombo.setSelectedIndex(periodIndex);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        
        // Divider
        dividerWidth = DEFAULT_DIVIDER_WIDTH;
        dividerLoc = DEFAULT_DIVIDER_LOC;
                
        JPanel controlPanel = createControlPanel();
        
        laboTestGraph = new LaboTestGraph();
        laboTestGraph.setPreferredSize(new Dimension(500,dividerLoc));
        
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
        
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    }

    //==================================================================================================================

    private void searchLaboTest(String fromDate) {
        
        // Add your handling code here:
        //logger.finer("Search button is pressed");

        statusPanel.setMessage("サーバーへ接続中...");
        countField.setText("");
        
        String toDate = MMLDate.getDate();

        table.removeAll();
        table = new JTable();
        jScrollPane1.setViewportView(table);

        searchTask = new mirrorI.dolphin.client.LaboTestSearchTask(patientId, isLocalId, fromDate, toDate, laboTestParameter);
        statusPanel.setMessage(searchTask.getMessage());
        searchTimer = new javax.swing.Timer(SEARCH_TIMER_INTERVAL, new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {

                statusPanel.setMessage(searchTask.getMessage());
                
                if (searchTask.done()) {
                    Toolkit.getDefaultToolkit().beep();
                    searchTimer.stop();
                    statusPanel.stop();

					System.out.println(searchTask.getMessage());
                    // check the result
                    //if (searchTask.getMessage().equals("データ取得が終了しました。")) {
					if (true){
                        statusPanel.setMessage("テーブルを作成しています...");
                                                
                        laboModules = searchTask.getLaboModuleColumns();
                        allLaboTest = searchTask.getAllLaboTest();
                        int count = laboModules != null ? laboModules.size() : 0;
                        countField.setText(String.valueOf(count));
                        
                        // Test Print
                        /*if (count > 0) {
							SimpleLaboModule module = (SimpleLaboModule)laboModules.get(0);
							VelocityContext ctx = ClientContext.getVelocityContext();
							ctx.put("laboModule", module);
							
							try {
								String templateFile = "gcp.LaboTestCrf.vm";
								StringWriter sw = new StringWriter();
								BufferedWriter bw = new BufferedWriter(sw);
								Velocity.mergeTemplate(templateFile, "sjis", ctx, bw);
								bw.flush();
								bw.close();
								System.out.println(sw.toString());
							} catch (Exception e) {
								System.out.println("Exception while the velocity test: " + e.toString());
								e.printStackTrace();
							}
                        }*/
                        /*if (count > 0) {
                        	for (int i = 0; i < count; i++) {
                        		SimpleLaboModule module = (SimpleLaboModule)laboModules.get(i);
                        		ArrayList laboTests = module.getSimpleLaboTest();
                        		if (laboTests != null && laboTests.size() >0) {
                        			int cnt = laboTests.size();
                        			for (int k=0; k < cnt; k++) {
										SimpleLaboTest st = (SimpleLaboTest)laboTests.get(k);
                        				ArrayList testItems = st.getSimpleLaboTestItem();
                        				if (testItems != null && testItems.size()>0) {
                        					for (int j = 0; j < testItems.size(); j++) {
												SimpleLaboTestItem item = (SimpleLaboTestItem)testItems.get(j);
												System.out.println(item.getItemName() + " = " + item.getItemValue());
                        					}
                        				}
                        			}
                        		}
                        	}
                        }*/
                        
                        generateObjectsForTable();
                        constructTable();
                        statusPanel.setMessage("テーブルを作成しました");
                        
                        searchTask=null;
                    }
                }
            }
        });

        statusPanel.start();
        searchTask.go();
        searchTimer.start();
    }
    
    
    private void createLaboTestGraph() {
        
        int[] selectedRows = table.getSelectedRows();
        
        if (selectedRows == null || selectedRows.length == 0 || laboTestGraph == null) {
            return;
        }
        
        ArrayList retList = null;
        ArrayList list = null;
        
        int columnCount = table.getColumnCount();
        
        int startCol = fixedColumnMode ? 0 : 1;
        boolean hasNonNull = false;
        
        for (int i = 0; i < selectedRows.length; i++) {
            
            list = new ArrayList();
            hasNonNull = false;
                    
            for (int j = 1; j < columnCount; j++) {
               
                Object o = table.getValueAt(selectedRows[i], j);
                
                if (o != null && o instanceof SimpleLaboTestItem) {
                    
                    SimpleLaboTestItem item = (SimpleLaboTestItem)o;
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
            String val = (String)header[j];
            int index = val.indexOf(": ");
            sampleTime[j-1] = index > 0 ? val.substring(index + 1) : val;
            //System.out.println(sampleTime[j-1]);
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
        p.add(new JLabel("抽出期間 過去： "));        
        extractionCombo = new JComboBox(periodList);
        Dimension dim = new Dimension(80, 20);
        extractionCombo.setPreferredSize(dim);
        extractionCombo.setMaximumSize(dim);
        extractionCombo.setMinimumSize(dim);
        extractionCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int index = extractionCombo.getSelectedIndex();
                    String s = getFilterDate(index);
                    
                    searchLaboTest(s);
                }
            }
        });
        p.add(extractionCombo);

        // スペース
        p.add(Box.createHorizontalStrut(7));
        // グル
        //p.add(Box.createHorizontalGlue());

        // 件数フィールド
        p.add (new JLabel("件数： "));        
        countField = new JTextField();
        dim = new Dimension(40, 20);
        countField.setPreferredSize(dim);
        countField.setMaximumSize(dim);
        countField.setMinimumSize(dim);
        countField.setEditable(false);
        p.add(countField);
        
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
        
        boolean bAbsolute = ClientContext.getPreferences().getBoolean("laboTestDocument.absoluteGraphProp", true);
        relativeRadio.setSelected(! bAbsolute);
        absoluteRadio.setSelected(bAbsolute);
        
        ActionListener al = new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                boolean b = absoluteRadio.isSelected();
                ClientContext.getPreferences().putBoolean("laboTestDocument.absoluteGraphProp", b);
           
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
    
    private String getFilterDate(int index) {

        index *= 2;
        String flag = periodValueList[index++];
        String val = periodValueList[index];
        int n = Integer.parseInt(val);
        
        GregorianCalendar today = new GregorianCalendar();
        
        if (flag.equals("YEAR")) {
            today.add(GregorianCalendar.YEAR, n);
        
        } else if (flag.equals("MONTH")) {
            today.add(GregorianCalendar.MONTH, n);
        
        } else if (flag.equals("DATE")) {
            today.add(GregorianCalendar.DATE, n);
        
        } else {
            //assert false : "Invalid Calendar Field: " + flag;
            //System.out.println("Invalid Calendar Field: " + flag);
        }
        
        return MMLDate.getDate(today);
    }
    
    
}
