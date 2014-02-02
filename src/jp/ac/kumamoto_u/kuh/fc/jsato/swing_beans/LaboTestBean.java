/*
 * LaboTestBean.java
 *
 * Created on 2001/11/27, 3:47
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author  Junzo SATO
 */
import java.util.Calendar;
import java.util.GregorianCalendar;
//----------------------------------------
import netscape.ldap.*;
import netscape.ldap.beans.*;
import java.util.*;
//
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JTable.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.*;
//
import java.awt.event.*;

import java.io.*;

public class LaboTestBean extends javax.swing.JPanel {
    //---------------------------------------------------------------------------------------------------------------
    String patientId = null;
    boolean isLocalId = true;
    String fromDate = null;
    String toDate = null;
    //---------------------------------------------------------------------------------------------------------------
    
    public String getDate(int monthOffset) {
        GregorianCalendar gc = new GregorianCalendar();
        //
        gc.add(Calendar.MONTH, monthOffset);
        //
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH) + 1;
        int day = gc.get(Calendar.DAY_OF_MONTH);
        
        String date = String.valueOf(year);
        date = date + "-";
        if (month < 10) {
            date = date + "0" + String.valueOf(month);
        } else {
            date = date + String.valueOf(month);
        }
        date = date + "-";
        if (day < 10) {
            date = date + "0" + String.valueOf(day);
        } else {
            date = date + String.valueOf(day);            
        }
        return date;
    }

    // JPopupMenu
    JPopupMenu popup = new JPopupMenu("item info");
    // For JTable
    JTable table = new JTable();  
    
    class ImageTableCellRenderer extends JLabel implements TableCellRenderer {
        Color penCol = Color.black;
        
        public ImageTableCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            //setHorizontalAlignment(RIGHT);
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
                MyTableCellData data = (MyTableCellData)value;
                if (data.isItem()) {
                    penCol = data.getStatusColor();
                    setText(data.toString());
                } else {
                    penCol = Color.black;
                    setText(data.toString());
                }
            }
            //-------------------------------------------------------
            
            return this;
        }
        
        public void paint(Graphics g) {
            //super.paint(g);
            
            String s = this.getText();
            g.setColor(penCol);
            g.drawString(s,0,12);
            //g.drawLine(0,0,100,20);////////////////////////
        }
    }
    
    class MyTableModel extends AbstractTableModel {
        String[] columnNames;
        Object[][] data;

        Class[] types = null;
        public MyTableModel(String[] names, Object[][] d) {
            columnNames = names;
            data = d;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }

    // headerV and columns are for references to Vectors defined in LaboTestXXXTask
    Vector headerV = null;
    Vector columns = null;
    Vector row = null;
    
    String[] header = null;
    Object[][] laboData = null;
    int ncols = -1;
    int maxrows = -1;

    public void generateObjectsForTable() {
        // header and laboData are set by Vectors headerV and columns
        
        //----------
        // header 
        //----------
        if (headerV.size() <= 0) {
            //printlnMessage("該当する検査結果はありません。");
            lblStatus.setText("該当する検査結果はありません。");
            headerV = null;
            return;
        }
        
        //System.out.println("Copying header string.");
        header = new String[headerV.size()];
        for (int i = 0; i < headerV.size(); ++i) {
            header[i] = (String)headerV.elementAt(i);
            //System.out.println(header[i]);
        }
        headerV.removeAllElements();
        headerV = null;
        
        //----------
        // columns
        //----------
        ncols = columns.size();
        if (ncols <= 0) {
            lblStatus.setText("該当する検査結果はありません。");
            return;
        }
        
        maxrows = -1;
        for (int k = 0; k < ncols; ++k) {
            if (maxrows < ((Vector)columns.elementAt(k)).size()) {
                maxrows = ((Vector)columns.elementAt(k)).size();
            }
        }
        //
        laboData = new Object[maxrows][ncols];////////////
        
        for (int j = 0; j < ncols; ++j) {
            row = ((Vector)columns.elementAt(j));
            for (int i = 0; i < row.size(); ++i) {
                if (row.elementAt(i).getClass().getName().equals("java.lang.String")) {
                    laboData[i][j] = new MyTableCellData((String)row.elementAt(i));
                } else {
                    laboData[i][j] = row.elementAt(i);// MyTableCellData
                }
            }
            for (int i = row.size(); i < maxrows; ++i) {
                laboData[i][j] = new MyTableCellData(null);
            }
            row.removeAllElements();
            row = null;
        }
        columns.removeAllElements();
        columns = null;
    }

    public void constructTable() {
        //
        // construct table.
        //
        if (header != null && header.length > 0 && laboData != null) {
            MyTableModel model = new MyTableModel(header, laboData);
            table.setModel(model);
            table.setDefaultRenderer(table.getColumnClass(0), new ImageTableCellRenderer());
            table.setEnabled(false);

            jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumn column = null;
            for (int i = 0; i < ncols; ++i) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(200);
            }

            jScrollPane1.setViewportView(table);

            //------------------
            // JPopupMenu
            table.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent me) {
                    showPopup(me);
                }

                public void mouseReleased(MouseEvent me) {
                    showPopup(me);
                }

                public void showPopup(MouseEvent me) {
                    if (popup.isPopupTrigger(me)) {
                        int r = table.rowAtPoint(me.getPoint());
                        int c = table.columnAtPoint(me.getPoint());
                        Object value = table.getModel().getValueAt(r,c);
                        if (value != null &&
                            value.getClass().getName().equals(
                                //==========================================================
                                "jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.MyTableCellData"
                                //==========================================================
                            )) {
                            MyTableCellData data = (MyTableCellData)value;
                            if (data.isItem() ==  true) {
                            // USER CLICKED LABOITEM //
                                // clear all items in the popup so that we can reuse it.
                                popup.removeAll();

                                // add items
                                if (data.getUp() != null) {
                                    popup.add(new JMenuItem(data.getUp()));
                                }
                                if (data.getLow() != null) {
                                    popup.add(new JMenuItem(data.getLow()));
                                }
                                if (data.getNormal() != null) {
                                    popup.add(new JMenuItem(data.getNormal()));
                                }
                                if (data.getOut() != null) {
                                    popup.add(new JMenuItem(data.getOut()));
                                }

                                if (data.getMemo() != null) {
                                    Vector v = data.getMemo();
                                    if (v.size() > 0) { 
                                        popup.addSeparator();
                                        for (int i = 0; i < v.size(); ++i) {
                                            popup.add(new JMenuItem((String)v.elementAt(i)));
                                        }
                                    }
                                }

                                popup.show(
                                    me.getComponent(),
                                    me.getX(),
                                    me.getY() - popup.getBounds().height
                                );
                            }
                        }
                    }
                }
            });

        } else {
            //System.out.println("Something wrong with header or laboData");
            lblStatus.setText("データ構築に失敗しました。");
        }
    }
    
    //==========================================================================
    
    /** Creates new form LaboTestBean */
    public LaboTestBean(String patientId, boolean isLocalId) {
        super();
        initComponents();
        this.patientId = patientId;
        this.isLocalId = isLocalId;
        
        // DEBUG
        //this.patientId = "99010000025";
        //this.isLocalId = true;        
        
        lblStatus.setText(/*"患者様ID: " + this.patientId + " の*/"検索の準備が出来ています。");
        lblStatus2.setText(/*"患者様ID: " + this.patientId + " の*/"ファイル出力の準備が出来ています。");
        
        // set term by some months before today...
        fromDate = getDate(-3);// the day before one year from today.
        toDate = getDate(0);// today
        
        tfFromDate.setText(fromDate);
        tfToDate.setText(toDate);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfFromDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfToDate = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        lblStatus2 = new javax.swing.JLabel();
        btnExport = new javax.swing.JButton();
        jProgressBar2 = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        
        jPanel1.setMinimumSize(new java.awt.Dimension(10, 32));
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 32));
        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setText("\u691c\u4f53\u63a1\u53d6\u65e5(CCYY-MM-DD)\u3067");
        jPanel1.add(jLabel6);
        
        tfFromDate.setText("2001-01-01");
        tfFromDate.setPreferredSize(new java.awt.Dimension(80, 24));
        jPanel1.add(tfFromDate);
        
        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText("\u304b\u3089");
        jPanel1.add(jLabel4);
        
        tfToDate.setText("2002-01-01");
        tfToDate.setPreferredSize(new java.awt.Dimension(80, 24));
        jPanel1.add(tfToDate);
        
        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setText("\u307e\u3067");
        jPanel1.add(jLabel5);
        
        add(jPanel1);
        
        jPanel2.setMinimumSize(new java.awt.Dimension(10, 32));
        jPanel2.setPreferredSize(new java.awt.Dimension(10, 32));
        lblStatus.setFont(new java.awt.Font("Dialog", 0, 12));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("\u30b9\u30c6\u30fc\u30bf\u30b9");
        lblStatus.setPreferredSize(new java.awt.Dimension(360, 16));
        jPanel2.add(lblStatus);
        
        btnSearch.setFont(new java.awt.Font("Dialog", 0, 12));
        btnSearch.setText("\u691c\u7d22");
        btnSearch.setMinimumSize(new java.awt.Dimension(58, 24));
        btnSearch.setPreferredSize(new java.awt.Dimension(58, 24));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        
        jPanel2.add(btnSearch);
        
        jProgressBar1.setPreferredSize(new java.awt.Dimension(120, 14));
        jPanel2.add(jProgressBar1);
        
        add(jPanel2);
        
        jPanel3.setMinimumSize(new java.awt.Dimension(10, 32));
        jPanel3.setPreferredSize(new java.awt.Dimension(10, 32));
        lblStatus2.setFont(new java.awt.Font("Dialog", 0, 12));
        lblStatus2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus2.setText("\u30b9\u30c6\u30fc\u30bf\u30b9");
        lblStatus2.setPreferredSize(new java.awt.Dimension(300, 16));
        jPanel3.add(lblStatus2);
        
        btnExport.setFont(new java.awt.Font("Dialog", 0, 12));
        btnExport.setText("\u30a8\u30af\u30b9\u30dd\u30fc\u30c8...");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        
        jPanel3.add(btnExport);
        
        jProgressBar2.setPreferredSize(new java.awt.Dimension(120, 14));
        jPanel3.add(jProgressBar2);
        
        add(jPanel3);
        
        jScrollPane1.setPreferredSize(new java.awt.Dimension(3, 500));
        add(jScrollPane1);
        
    }//GEN-END:initComponents

    //***********************************************
    LaboTestExportTask exportTask = null;
    javax.swing.Timer exportTimer = null;
    final int EXPORT_TIMER_INTERVAL = 200;
    
    FileWriter fw = null;
    //***********************************************

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // Add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int selected = chooser.showSaveDialog(this);
        if (selected == JFileChooser.CANCEL_OPTION) {
            //System.out.println("Cancel");
            return;
        } else if (selected == JFileChooser.APPROVE_OPTION) {
            fromDate = tfFromDate.getText();
            toDate = tfToDate.getText();
            
            File f = chooser.getSelectedFile();
            if (f == null) {
                lblStatus2.setText("保存先ファイルの取得に失敗しました。");
                return;
            }
            
            try {
                fw = new FileWriter(f);
            } catch (IOException e) {
                lblStatus2.setText("ファイル書き出しに失敗しました。");
                return;
            }
                    
            exportTask = new LaboTestExportTask(fw, patientId, isLocalId, fromDate, toDate);
            jProgressBar2.setMinimum(0);
            jProgressBar2.setMaximum(exportTask.getLengthOfTask());
            jProgressBar2.setValue(jProgressBar2.getMinimum());

            exportTimer = new javax.swing.Timer(EXPORT_TIMER_INTERVAL, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    jProgressBar2.setValue(exportTask.getCurrent());
                    lblStatus2.setText(exportTask.getMessage());
                    if (exportTask.done()) {
                        Toolkit.getDefaultToolkit().beep();
                        exportTimer.stop();                    
                        // stop indeterminate progress bar
                        jProgressBar2.setIndeterminate(false);
                        jProgressBar2.setValue(jProgressBar2.getMinimum());                   
                        btnExport.setEnabled(true);

                        // check the result
                        if (exportTask.getMessage().equals("ファイル書き出しが終了しました。")) {
                            // :-)
                        }
                        
                        if (fw != null) {
                            try {
                                fw.flush();
                                fw.close();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                    }
                }
            });

            btnExport.setEnabled(false);
            // start indeterminate progress bar
            jProgressBar2.setIndeterminate(true);
            exportTask.go();
            exportTimer.start();
        }
    }//GEN-LAST:event_btnExportActionPerformed

    //==================================================================================================================
    
    //***********************************************
    LaboTestSearchTask searchTask = null;
    javax.swing.Timer searchTimer = null;
    final int SEARCH_TIMER_INTERVAL = 200;
    //***********************************************
    
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // Add your handling code here:
        lblStatus.setText("サーバーへ接続中...");

        
        fromDate = tfFromDate.getText();
        toDate = tfToDate.getText();
        
        
        table.removeAll();
        table = new JTable();
        jScrollPane1.setViewportView(table);
        
        
        searchTask = new LaboTestSearchTask(patientId, isLocalId, fromDate, toDate);
        jProgressBar1.setMinimum(0);
        jProgressBar1.setMaximum(searchTask.getLengthOfTask());
        jProgressBar1.setValue(jProgressBar1.getMinimum());
        
        searchTimer = new javax.swing.Timer(SEARCH_TIMER_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jProgressBar1.setValue(searchTask.getCurrent());
                lblStatus.setText(searchTask.getMessage());
                
                if (searchTask.done()) {
                    Toolkit.getDefaultToolkit().beep();
                    searchTimer.stop();                    
                    // stop indeterminate progress bar
                    jProgressBar1.setIndeterminate(false);
                    jProgressBar1.setValue(jProgressBar1.getMinimum());                   
                    btnSearch.setEnabled(true);
                    
                    // check the result
                    if (searchTask.getMessage().equals("データ取得が終了しました。")) {
                        lblStatus.setText("テーブルを作成しています...");
                        // get references of vectors
                        headerV = searchTask.getHeaderV();
                        columns = searchTask.getColumns();
                        // generate table from these vectors
                        header = null;
                        laboData = null;         
                        ncols = -1;
                        maxrows = -1;
                        generateObjectsForTable();
                        constructTable();
                    }
                }
            }
        });
        
        btnSearch.setEnabled(false);
        // start indeterminate progress bar
        jProgressBar1.setIndeterminate(true);
        searchTask.go();
        searchTimer.start();
    }//GEN-LAST:event_btnSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField tfFromDate;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField tfToDate;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JButton btnSearch;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblStatus2;
    private javax.swing.JButton btnExport;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
