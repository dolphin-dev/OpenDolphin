package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.table.OddEvenRowRenderer;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class AboutDolphin  {
    
    private UserModel user;
    private String[] copyrights;
    private JDialog dialog;
    
    public AboutDolphin() {
        this.user = Project.getUserModel();
        String line = ClientContext.getString("copyrightString");
        copyrights = line.split(",");
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
                //Server-ORCA連携^
                return Project.claimSenderIsClient() ? 27 : 21;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int row, int col) {
                
                String ret = null;
                
                switch (row) {
                    
                    case 0:
                        ret = col==0 ? "製品名" : ClientContext.getString("productString");
                        break;
                    
                    case 1:
                        ret = col==0 ? "バージョン" : ClientContext.getVersion();
                        break;
                        
                    case 2:
                        ret = col==0 ? "ライセンス" : ClientContext.getString("softwareLicense");
                        break;     
                        
                    case 3:
                        ret = col==0 ? "著作権" : copyrights[0];
                        break;
                        
                    case 4:
                        ret = col==0 ? "" : copyrights[1];
                        break;
                        
                    case 5:
                        ret = col==0 ? "テクノロジー" : ClientContext.getString("technologies.used");
                        break;
                        
                    case 6:
                        ret = col==0 ? "ソースコード使用謝辞" : "元町皮ふ科（札幌） 増田内科（和歌山市） 新宿ヒロクリニック";
                        break;    
                        
                    case 7:
                        ret = col==0 ? "サポートURL" : ClientContext.getString("url.support");
                        break;     
                        
                    case 8:
                        ret = col==0 ? "医療機関ID" : user.getFacilityModel().getFacilityId();
                        break;
                        
                    case 9:
                        ret = col==0 ? "ログインユーザー" : Project.getUserId();
                        break;
                        
                    case 10:
                        ret = col==0 ? "氏　名" : user.getCommonName();
                        break; 
                        
                   case 11:
                        ret = col==0 ? "医療資格" : user.getLicenseModel().getLicenseDesc();
                        break; 
                       
                   case 12:
                        ret = col==0 ? "麻薬施用者免許番号" : user.getUseDrugId();
                        break;     
                        
                    case 13:
                        if (col==0) {
                            ret = "電子カルテサーバー";
                        } else {
                            if (ClientContext.is5mTest()) {
                                ret = "test.open.dolphin.pro";
                            } else if (ClientContext.isOpenDolphin()) {
                                ret = "cloud.open.dolphin";
                            } else if (ClientContext.isDolphinPro()) {
                                String val = Project.getBaseURI();
                                int index = val.lastIndexOf("/open");
                                ret = val.substring(0, index);
                            } else {
                                ret = "";
                            }
                        }
                        break;
                        
                    case 14:
                        if (col==0) {
                            ret = "MML出力";
                            
                        } else {
                            if (Project.getBoolean(GUIConst.SEND_MML_IS_RUNNING)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("起動中 ").append(Project.getString(Project.SEND_MML_DIRECTORY));
                                ret = sb.toString();
                            } else {
                                ret = "停止中";
                            }
                        }
                        break;
                        
                   case 15:
                        if (col==0) {
                            ret = "カルテPDF出力";
                            
                        } else {
                            if (Project.getBoolean(Project.KARTE_PDF_SEND_AT_SAVE)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("起動中 ").append(Project.getString(Project.KARTE_PDF_SEND_DIRECTORY));
                                ret = sb.toString();
                            } else {
                                ret = "停止中";
                            }
                        }
                        break;     
                        
                    case 16:
                        if (col==0) {
                            ret = "受付リレー";
                            
                        } else {
                            if (Project.getBoolean(GUIConst.PVT_RELAY_IS_RUNNING)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("起動中 ").append(Project.getString(Project.PVT_RELAY_DIRECTORY));
                                sb.append(" ").append(Project.getString(Project.PVT_RELAY_ENCODING));
                                ret = sb.toString();
                            } else {
                                ret = "停止中";
                            }
                        }
                        break;
                        
                    case 17:
                        if (col==0) {
                            ret = "ORCAとの接続";
                        } else {
                            ret = Project.claimSenderIsClient() ? "クライアント" : "サーバー";
                        }
                        break;

                    case 18:
                        ret = col==0 ? "保険医療機関コード" : Project.getBasicInfo();
                        break;      
                        
                   case 19:
                        ret = col==0 ? "JMARI コード" : Project.getString(Project.JMARI_CODE);
                        break;
                       
                   case 20:
                        ret = col==0 ? "ORCA ユーザーID" : user.getOrcaId();
                        break;    
                       
                  // 以降はClient-ORCA接続の時     
                        
                   case 21:
                        if (col==0) {
                            ret = "ORCA サーバ";
                        }
                        else {
                            String test = Project.getString(Project.CLAIM_ADDRESS);
                            ret = test!=null ? test : null;
                        }
                        break;
                          
                   case 22:
                        if (col==0) {
                            ret = "CLAIM 送信";
                        }
                        else {
                            ret = Project.getBoolean(GUIConst.SEND_CLAIM_IS_RUNNING) ? "起動中" : "停止中";
                        }
                        break;
                       
                   case 23:
                        if (col==0) {
                            ret = "送信ポート";
                        } else {
                            ret = Project.getString(Project.CLAIM_PORT);
                        }
                        break;    
                       
                   case 24:
                        if (col==0) {
                            ret = "受付受信";
                        }
                        else {
                            ret = (Project.getBoolean(GUIConst.PVT_SERVER_IS_RUNNING)) ? "起動中" : "停止中";
                        }
                        break;
                       
                    case 25:
                        if (col==0) {
                            ret = "受付バインドアドレス";
                        } else {
                            String test = Project.getString(Project.CLAIM_BIND_ADDRESS);
                            ret = test!=null ? test : null;
                        }
                        break;
                        
                   case 26:
                        ret = col==0 ? "受付ポート" : "5002";
                        break;    
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
        OddEvenRowRenderer c1r = new OddEvenRowRenderer();
        c1r.setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setCellRenderer(c0r);
        table.getColumnModel().getColumn(1).setCellRenderer(c1r);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(450);
        
        table.setBorder(BorderFactory.createEtchedBorder());
        
        JButton done = new JButton("閉じる");
        done.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.white);
        btnPanel.setOpaque(true);
        btnPanel.add(done);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.white);
        contentPane.setOpaque(true);
        contentPane.add(table,BorderLayout.CENTER);
        contentPane.add(btnPanel, BorderLayout.SOUTH);
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        dialog = new JDialog(new JFrame(), ClientContext.getString("productString"), true);
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
}
