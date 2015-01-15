package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import open.dolphin.infomodel.ActivityModel;
import open.dolphin.table.OddEvenRowRenderer;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class AboutActivities  {
    
    // 行数
    private static final int rowCount = 9;
    
    // 統計値のカラム幅
    private final int columnWidth = 100;
    
    // 当月(as title)
    private final String targetMonth;
    
    // カラム名
    private final String[] columnNames;
    
    private JDialog dialog;
    private final ActivityModel[] am;
    
    public AboutActivities(ActivityModel[] am) {
        this.am = am;
        columnNames = new String[am.length+1];
        
        columnNames[0]  = "";
        int lastIndex = columnNames.length-1;
        columnNames[lastIndex] = "総 数";
        columnNames[--lastIndex] = "当 月";
        lastIndex-=1;
        for (int col=0; col < lastIndex; col++) {
            if (am[col].getFromDate()!=null) {
                columnNames[col+1] = yearMontSringFromDate(am[col].getFromDate());
            }
        }
        
        targetMonth = new SimpleDateFormat("yyyy'年'M'月'd'日'").format(new Date());
    }
    
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private void createAndShowGUI() {
        
        AbstractTableModel model = new AbstractTableModel() {

            @Override
            public int getRowCount() {
                return rowCount;
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }
            
            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Object getValueAt(int row, int col) {
                
                String ret = null;
                
                switch (row) {
                    
                    case 0:
                        ret = col==0 ? "登録患者数" : stringFromLong(am[col-1].getNumOfPatients());
                        break;
                    
                    case 1:
                        ret = col==0 ? "受付数" : stringFromLong(am[col-1].getNumOfPatientVisits());
                        break;
                        
                    case 2:
                        ret = col==0 ? "カルテ枚数" : stringFromLong(am[col-1].getNumOfKarte());
                        break;     
                        
                    case 3:
                        ret = col==0 ? "画像数" : stringFromLong(am[col-1].getNumOfImages());
                        break;
                        
                    case 4:
                        ret = col==0 ? "添付書類数" : stringFromLong(am[col-1].getNumOfAttachments());
                        break;
                        
                    case 5:
                        ret = col==0 ? "病名数" : stringFromLong(am[col-1].getNumOfDiagnosis());
                        break;
                        
                    case 6:
                        ret = col==0 ? "紹介状数" : stringFromLong(am[col-1].getNumOfLetters());
                        break;    
                        
                    case 7:
                        ret = col==0 ? "検査数" : stringFromLong(am[col-1].getNumOfLabTests());
                        break;     
                        
                    case 8:
                        ret = col==0 ? "利用者数" : col!=am.length ? "---" : stringFromLong(am[am.length-1].getNumOfUsers());
                        break;
                        
//                    case 9:
//                        ret = col==0 ? "データベース容量" : col!=am.length ? "NA" : am[am.length-1].getDbSize();
//                        break;  
                }
                
                return ret;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(5,5));
        table.setRowHeight(ClientContext.getMoreHigherRowHeight());
        
        OddEvenRowRenderer c0r = new OddEvenRowRenderer();
        c0r.setHorizontalAlignment(SwingConstants.RIGHT);

        for (int col=0; col<columnNames.length; col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(c0r);
        }
        
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        for (int col=1; col<columnNames.length; col++) {
            table.getColumnModel().getColumn(col).setPreferredWidth(columnWidth);
        }
        
        table.setBorder(BorderFactory.createEtchedBorder());
        
        JButton done = new JButton("閉じる");
        done.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        
        // 集計月を表示するパネル
        JLabel titleLabel = new JLabel(targetMonth, SwingConstants.CENTER);
        JPanel titleP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleP.add(titleLabel);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);
        
        // コマンドパネル
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.white);
        btnPanel.setOpaque(true);
        btnPanel.add(done);
        
        // Dialogの content panel
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.white);
        contentPane.setOpaque(true);
        contentPane.add(titleP, BorderLayout.NORTH);
        contentPane.add(tablePanel,BorderLayout.CENTER);
        contentPane.add(btnPanel, BorderLayout.SOUTH);
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        dialog = new JDialog(new JFrame(), ClientContext.getFrameTitle("統計情報"), true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setContentPane(contentPane);
        dialog.getRootPane().setDefaultButton(done);
        dialog.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int x = (screen.width - dialog.getPreferredSize().width) / 2;
        int y = (screen.height - dialog.getPreferredSize().height) / n;
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }
    
    private String yearMontSringFromDate(Date d) {
        return new SimpleDateFormat("yyyy'年'M'月'").format(d);
    }
    
    private String stringFromLong(long l) {
        return NumberFormat.getNumberInstance().format(l);
    }
}
