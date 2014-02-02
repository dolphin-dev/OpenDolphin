/*
 * MainWindow.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import open.dolphin.exception.PluginException;
import open.dolphin.infomodel.*;
import open.dolphin.plugin.*;
import open.dolphin.plugin.event.*;
import open.dolphin.project.*;
import open.dolphin.util.*;

/**
 * アプリケーションのメインウインドウクラス。 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MainWindow implements PvtMessageListener {
    
    private JPanel ui;
    private JFrame frame;
    private Mediator mediator;
    private StateManager stateMgr;
    
    private int locX = 80;
    private int locY = 80;
    private int frameWidth = 460;
    private int frameHeight = 320;
    
    // Service staffs
    private IWatingList watingList;
    private StampBoxService stampBoxService;
    
    /** Creates new MainWindow */
    public MainWindow() {
    }
        
    public void start() {
                
        stateMgr = new StateManager();
        mediator = new Mediator(stateMgr);
        
        ui = createUi();
        String title = ClientContext.getString("mainWindow.title");
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setJMenuBar(createMenuBar());
        frame.getContentPane().add(ui);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mediator.exit();
            }
        });
        frame.addComponentListener(new ComponentListener() {
            
            public void componentResized(java.awt.event.ComponentEvent e) {
                Component c = (Component)e.getSource();
                System.out.println(c.getWidth() + "," + c.getHeight());
            }
            
            public void componentMoved(java.awt.event.ComponentEvent e) {
                Component c = (Component)e.getSource();
                System.out.println(c.getX() + "," + c.getY());
            }
            
            public void componentShown(java.awt.event.ComponentEvent e) {
            }
            
            public void componentHidden(java.awt.event.ComponentEvent e) {
            }
            
        });
        frame.setLocation(locX, locY);
        frame.pack();
        //frame.setSize(frameWidth, frameHeight);
        frame.setVisible(true);
        
        // Launch services 
        stampBoxService = (StampBoxService)ClientContext.getPlugin("mainWindow.stampBox");
        try {
            watingList = (IWatingList)ClientContext.getPlugin("mainWindow.watingList");
            watingList.addPvtMessageListener(MainWindow.this);
            
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException while creating the WatingList: " + e.toString());
            e.printStackTrace();
        }
    }  
        
    public void pvtMessageEvent(PvtMessageEvent e) {
        // Not in the event dispatch thread
        PatientVisit newVisit = e.getPatientVisit();
        ChartPlugin chart = (ChartPlugin)ClassFactory.create("open.dolphin.client.ChartPlugin");
        chart.setTitle(newVisit.getPatient().getName());
        chart.setPatientVisit(newVisit);
        chart.setReadOnly(Project.isReadOnly());        // RedaOnlyProp
        chart.addPropertyChangeListener("pvtNumber", watingList);
        try {
        	chart.init();
			chart.start();
			ClientContext.addCurrentService(chart);
        } catch (PluginException pe) {
        	System.out.println("Exception while opening the chart: " + e.toString());
        	pe.printStackTrace();
        }
    }
                
    private JPanel createUi() {
     
        // 白色の JPanel を生成する
        JPanel content = new JPanel(new BorderLayout());
        //BgImagePanel content = new BgImagePanel();
        //ImageIcon icon = DolphinContext.createImageIcon("WebComponent24.gif");
        //content.setImage(icon.getImage());
        content.setBackground(Color.white);
        //content.setOpaque(true);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        // 表示するサービスを得る
        Object[] allServices = ClientContext.getServiceProxies();
        int len = allServices.length;
        ArrayList visible = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            ServiceProxy proxy = (ServiceProxy)allServices[i];
            if (proxy.isVisible()) {
                visible.add(proxy);
            }
        }
        
        JTable table = new JTable(new ServiceTableModel(visible.toArray(), 3));
        table.setDefaultRenderer(open.dolphin.client.ServiceProxy.class,
                                 new ProxyRenderer());
        //table.setOpaque(true);
        table.setCellSelectionEnabled(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setRowHeight(60);
        TableColumn column = null;
        for (int i = 0; i < 3; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(120);
        }
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable t = (JTable)e.getSource();
                    int row = t.getSelectedRow();
                    int col = t.getSelectedColumn();
                    ServiceProxy p = (ServiceProxy)t.getValueAt(row, col);
                    if (p != null) {
                        //mediator.openService(p.getServiceName());
                        mediator.openService(p.getServiceID());
                    }
                }
            }
        });

        content.add(table, BorderLayout.CENTER);
        return content;
    }
    
    protected class ServiceTableModel extends AbstractTableModel {
            
        private Object[] data;
        private int numCols;
        private int numRows;

        public ServiceTableModel(Object[] data, int numCols) {
            this.data = data;
            this.numCols = numCols;
            int len = data.length;
            int mod = len % numCols;
            this.numRows = len / numCols;
            if (mod != 0) {
                numRows++;
            }
            /*System.out.println(len);
            System.out.println(numCols);
            System.out.println(numRows);*/
        }

        public int getRowCount() {
            return numRows;
        }

        public int getColumnCount() {
            return numCols;
        }
        
        public Class getColumnClass(int col) {
            return open.dolphin.client.ServiceProxy.class;
        }

        public boolean isCellEditable() {
            return false;
        }

        public Object getValueAt(int row, int col) {
            int index = row * numCols + col;
            return index < data.length ? data[index] : null;
        }
    }
    
    protected class ProxyRenderer extends DefaultTableCellRenderer {
        
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
                    
            //this.setOpaque(true);        
            if (value != null) {
                ServiceProxy proxy = (ServiceProxy)value;
                setText(proxy.getServiceName());
                if (isSelected) {
                    setIcon(proxy.getSelectedIcon());
                    
                } else {
                    setIcon(proxy.getIcon());
                }
                this.setHorizontalAlignment(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.CENTER);
                setVerticalTextPosition(SwingConstants.BOTTOM);
                
            } else {
                setText("");
                setIcon(null);
            }
            return this;
        }
    }
    
    private JMenuBar createMenuBar() {
          
        // Creates Actions
        HashMap table = new HashMap();
        
        /**
         * Builds menubar 
         */
        DefaultMenuBarBuilder builder = new DefaultMenuBarBuilder();
        builder.setActionListener(mediator);
        builder.setRoutingTarget(mediator);
        builder.setActionTable(table);
        MenuBarDirector director = new MenuBarDirector(builder);
        
        try {
            director.build(ClientContext.getResource("mainWindowMenuBar.xml"));
            mediator.registerActions(table);
            
        } catch (Exception e) {
            System.out.println("Exception while creating MenuBar: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
        
        return builder.getJMenuBar();
    }
                 
    /**
     * Mediator of this window.
     */
    protected final class Mediator extends MouseAdapter 
    implements ActionListener, IRoutingTarget {
        
        /** Colleagues */
        private DlAction exitAction;
        private DlAction changePasswordAction;
        private DlAction addUserAction;
        private DlAction updateAction;
        private StateManager stateMgr;
        
        /** Creates new Mediator */
        public Mediator(StateManager stateMgr) {            
            this.stateMgr = stateMgr;
        }
        
        public void registerActions(HashMap map) {
            exitAction = (DlAction)map.get("exitAction");
            changePasswordAction = (DlAction)map.get("changePasswordAction");
            addUserAction = (DlAction)map.get("addUserAction");
            updateAction = (DlAction)map.get("updateAction");
            
            boolean admin = Project.getUserProfileEntry().isAdmin() ? true : false;
            addUserAction.setEnabled(admin);
        }
        
        public void actionRouted(Action source) {
            
            if (source == exitAction) {
                exit();
                
            } else if (source == changePasswordAction) {
                changePassword();
            
            } else if (source == addUserAction) {
                addUser();
            
            } else if (source == updateAction) {
                update();
            }
        }
        
        ////////////////////////////////////////////////////////////////////////
        public void openService(String name) {
            stateMgr.openService(name);
        }
        
        public void exit() {
            
            ClientContext.setExit(true);
            
            if (ClientContext.getExit()) {
                frame.setVisible(false);
                frame.dispose();
                System.exit(1);
            }
        }
        
        public void changePassword() {
            ClientContext.getPlugin("mainWindow.changePassword");
        }
        
        public void addUser() {
            ClientContext.getPlugin("mainWindow.addUser");
        }
        
        public void update() {
        	
            DolphinUpdater updater = (DolphinUpdater)ClientContext.getPlugin("mainWindow.updateDolphin");
            if (updater == null) {
                return;
            }
            
            boolean demo = Project.getName().equals("debug") ? true : false;
            if (! demo) {
                updater.setRemoteURL(ClientContext.getString("updater.url"));
                
            } else {
                updater.setRemoteURL(ClientContext.getString("updater.demo.url"));
            }
            
            //String remoteUrl = Project.getUpdateRemoteURL();
            //updater.setRemoteURL(remoteUrl);
            
            int result = updater.checkUpdateAvailable();
            String msg = null;
            String title = ClientContext.getString("updater.dialog.title");
            
            switch (result) {
                
                case -1:
                    msg = ClientContext.getString("updater.msg.noConnection");
                    JOptionPane.showMessageDialog(null,
                                        msg,
                                        title,
                                        JOptionPane.WARNING_MESSAGE);
                    ClientContext.remove((IPlugin)updater);
                    break;
                    
                case 0:
                    msg = ClientContext.getString("updater.msg.noUpdate");
                    JOptionPane.showMessageDialog(null,
                                     msg,
                                     title,
                                     JOptionPane.INFORMATION_MESSAGE);
                    ClientContext.remove((IPlugin)updater);
                    break;
                    
                case 1:
                    msg = ClientContext.getString("updater.msg.available");
                    int select = JOptionPane.showConfirmDialog(null,
                                                   msg,
                                                   title,
                                                   JOptionPane.YES_NO_OPTION);
         
                    // NO を選択した場合
                    if (select == JOptionPane.NO_OPTION) {
                    	//System.out.println("NO_OPTION");
						ClientContext.remove((IPlugin)updater);
                    }
                    else if (select == JOptionPane.YES_OPTION){
						//System.out.println("YES_OPTION");
                        updater.update();
                    }
					else {
						//System.out.println("OTHER_OPTION");
						ClientContext.remove((IPlugin)updater);
					}
                    break;
                    
                default:
					//System.out.println("defaultXXX");
					ClientContext.remove((IPlugin)updater);
                    break;
            }
        }
        
        ///////////////////////////////
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            String label = item.getText();
            
            String text = ClientContext.getString("mainWindow.menu.dolphinSupportText");
            if (label.equals(text)) { 
                browseURL(ClientContext.getString("mainWindow.menu.dolphinSupportUrl"));
                return;
            } 
            
            text = ClientContext.getString("mainWindow.menu.dolphinText");
            if (label.equals(text)) { 
                browseURL(ClientContext.getString("mainWindow.menu.dolphinUrl"));
                return;
            } 
            
            text = ClientContext.getString("mainWindow.menu.medXmlText");
            if (label.equals(text)) {
                browseURL(ClientContext.getString("mainWindow.menu.medXmlUrl"));
                return;
            } 
            
            text = ClientContext.getString("mainWindow.menu.seaGaiaText");
            if (label.equals(text)) {
                browseURL(ClientContext.getString("mainWindow.menu.seaGaiaUrl"));
                return;
            }
            
            text = ClientContext.getString("mainWindow.menu.aboutText");
            if (label.equals(text)) {
                showAbout();
                return;
            }
            
            //assert false : label;
        }
        
        private void browseURL(String url) {
            try {
                Runtime.getRuntime().exec( new String[] { "cmd.exe", "/c", "start", url } );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        private void showAbout() {
            AbstractProjectFactory f = AbstractProjectFactory.getProjectFactory(Project.getName());
            f.createAboutDialog();
        }
    }    
        
    class ServiceManagerState {
        
        public ServiceManagerState() {
            super();
        }
        
        public void openService(String name) {
        }
    }
    
    class LoginState extends ServiceManagerState {
        
        public LoginState() {
        }
        
        public void openService(String name) {
            IMainWindowPlugin service = (IMainWindowPlugin)ClientContext.getPlugin(name);
            if (service != null) {
                service.showUI();
            }
        }
    }
    
    class LogoffState extends ServiceManagerState {
        
        public LogoffState() {
        }
        
        public void openService(String name) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    class StateManager {
        
        private ServiceManagerState loginState = new LoginState();
        private ServiceManagerState logoffState = new LogoffState();
        private ServiceManagerState currentState = loginState;
        
        public StateManager() {
        }
        
        public void openService(String name) {
            currentState.openService(name);
        }
        
        public void setLogin(boolean b) {
            currentState = b ? loginState : logoffState;
        }
    }
}