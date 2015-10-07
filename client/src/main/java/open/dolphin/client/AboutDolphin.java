package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.table.StripeTableCellRenderer;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class AboutDolphin  {
    
    private final UserModel user;
    private final String[] copyrights;
    private JDialog dialog;
    private final String[] rowItems;
    private final String textRunning;
    private final String textUnderStop;
    
    public AboutDolphin() {
        this.user = Project.getUserModel();
        copyrights = ClientContext.getString("copyrightString").split(",");
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AboutDolphin.class);
        rowItems = bundle.getString("rowItems.table").split(",");
        
        textRunning = bundle.getString("text.running");
        textUnderStop = bundle.getString("text.underStop");
    }
    
    public void start() {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        
        final java.util.ResourceBundle bundle = ClientContext.getMyBundle(AboutDolphin.class);
        
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
                        ret = col==0 ? rowItems[0] : ClientContext.getString("productString");
                        break;
                    
                    case 1:
                        ret = col==0 ? rowItems[1] : ClientContext.getVersion();
                        break;
                        
                    case 2:
                        ret = col==0 ? rowItems[2] : ClientContext.getString("softwareLicense");
                        break;     
                        
                    case 3:
                        ret = col==0 ? rowItems[3] : copyrights[0];
                        break;
                        
                    case 4:
                        ret = col==0 ? rowItems[4] : copyrights[1];
                        break;
                        
                    case 5:
                        ret = col==0 ? rowItems[5] : ClientContext.getString("technologies.used");
                        break;
                        
                    case 6:
                        ret = col==0 ? rowItems[6] : bundle.getString("names.contributers");
                        break;    
                        
                    case 7:
                        ret = col==0 ? rowItems[7] : ClientContext.getString("url.support");
                        break;     
                        
                    case 8:
                        ret = col==0 ? rowItems[8] : user.getFacilityModel().getFacilityId();
                        break;
                        
                    case 9:
                        ret = col==0 ? rowItems[9] : Project.getUserId();
                        break;
                        
                    case 10:
                        ret = col==0 ? rowItems[10] : user.getCommonName();
                        break; 
                        
                   case 11:
                        ret = col==0 ? rowItems[11] : user.getLicenseModel().getLicenseDesc();
                        break; 
                       
                   case 12:
                        ret = col==0 ? rowItems[12] : user.getUseDrugId();
                        break;     
                        
                    case 13:
                        if (col==0) {
                            ret = rowItems[13];
                        } else {
//                            if (ClientContext.is5mTest()) {
//                                ret = "test.open.dolphin.pro";
//                            } else if (ClientContext.isOpenDolphin()) {
//                                ret = "cloud.open.dolphin";
//                            } else if (ClientContext.isDolphinPro()) {
//                                String val = Project.getBaseURI();
//                                int index = val.lastIndexOf("/open");
//                                ret = val.substring(0, index);
//                            } else {
//                                ret = "";
//                            }
                           ret = Project.getServer();
                        }
                        break;
                        
                    case 14:
                        if (col==0) {
                            ret = rowItems[14];
                            
                        } else {
                            if (Project.getBoolean(GUIConst.SEND_MML_IS_RUNNING)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(textRunning).append(" ").append(Project.getString(Project.SEND_MML_DIRECTORY));
                                ret = sb.toString();
                            } else {
                                ret = textUnderStop;
                            }
                        }
                        break;
                        
                   case 15:
                        if (col==0) {
                            ret = rowItems[15];
                            
                        } else {
                            if (Project.getBoolean(Project.KARTE_PDF_SEND_AT_SAVE)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(textRunning).append(" ").append(Project.getString(Project.KARTE_PDF_SEND_DIRECTORY));
                                ret = sb.toString();
                            } else {
                                ret = textUnderStop;
                            }
                        }
                        break;     
                        
                    case 16:
                        if (col==0) {
                            ret = rowItems[16];
                            
                        } else {
                            if (Project.getBoolean(GUIConst.PVT_RELAY_IS_RUNNING)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(textRunning).append(" ").append(Project.getString(Project.PVT_RELAY_DIRECTORY));
                                sb.append(" ").append(Project.getString(Project.PVT_RELAY_ENCODING));
                                ret = sb.toString();
                            } else {
                                ret = textUnderStop;
                            }
                        }
                        break;
                        
                    case 17:
                        if (col==0) {
                            ret = rowItems[17];
                        } else {
                            String clientText = bundle.getString("text.client");
                            String serverText = bundle.getString("text.server");
                            ret = Project.claimSenderIsClient() ? clientText : serverText;
                        }
                        break;

                    case 18:
                        ret = col==0 ? rowItems[18] : Project.getBasicInfo();
                        break;      
                        
                   case 19:
                        ret = col==0 ? rowItems[19] : Project.getString(Project.JMARI_CODE);
                        break;
                       
                   case 20:
                        ret = col==0 ? rowItems[20] : user.getOrcaId();
                        break;    
                       
                  // 以降はClient-ORCA接続の時     
                        
                   case 21:
                        if (col==0) {
                            ret = rowItems[21];
                        }
                        else {
                            String test = Project.getString(Project.CLAIM_ADDRESS);
                            ret = test!=null ? test : null;
                        }
                        break;
                          
                   case 22:
                        if (col==0) {
                            ret = rowItems[22];
                        }
                        else {
                            ret = Project.getBoolean(GUIConst.SEND_CLAIM_IS_RUNNING) ? textRunning : textUnderStop;
                        }
                        break;
                       
                   case 23:
                        if (col==0) {
                            ret = rowItems[23];
                        } else {
                            ret = Project.getString(Project.CLAIM_PORT);
                        }
                        break;    
                       
                   case 24:
                        if (col==0) {
                            ret = rowItems[24];
                        }
                        else {
                            ret = (Project.getBoolean(GUIConst.PVT_SERVER_IS_RUNNING)) ? textRunning : textUnderStop;
                        }
                        break;
                       
                    case 25:
                        if (col==0) {
                            ret = rowItems[25];
                        } else {
                            String test = Project.getString(Project.CLAIM_BIND_ADDRESS);
                            ret = test!=null ? test : null;
                        }
                        break;
                        
                   case 26:
                        ret = col==0 ? rowItems[26] : "5002";
                        break;    
                }
                
                return ret;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);
        //table.setIntercellSpacing(new Dimension(5,5));
        //table.setRowHeight(ClientContext.getMoreHigherRowHeight());
        
        StripeTableCellRenderer c0r = new StripeTableCellRenderer();
        c0r.setHorizontalAlignment(SwingConstants.RIGHT);
        c0r.setTable(table);
        StripeTableCellRenderer c1r = new StripeTableCellRenderer();
        c1r.setHorizontalAlignment(SwingConstants.LEFT);
        c1r.setTable(table);

        table.getColumnModel().getColumn(0).setCellRenderer(c0r);
        table.getColumnModel().getColumn(1).setCellRenderer(c1r);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(450);
        
        // StripeTableCellRendererの場合はsetTable以降で行う
        table.setIntercellSpacing(new Dimension(7,7));
        table.setRowHeight(ClientContext.getMoreHigherRowHeight());
        
        table.setBorder(BorderFactory.createEtchedBorder());
        
        String actionText = bundle.getString("actionText.close");
        JButton done = new JButton(actionText);
        done.addActionListener((ActionEvent ae) -> {
            dialog.setVisible(false);
            dialog.dispose();
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
