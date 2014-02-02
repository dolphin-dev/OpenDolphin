/*
 * ClaimAmountApp.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Test tool to recieve Patient Visit Info.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public class ClaimAmountApp extends JPanel {
    
    private ClaimAmountServer server;
    private JTextField portField;
    private JButton serverButton;
    private JTextField statusField;
    private TextAreaTracer debugArea;
    private JComboBox encCombo;
    private boolean running;

    /** Creates new PVTApp */
    public ClaimAmountApp() {
        super(new BorderLayout());
        
        JPanel compo = createComponent();
        compo.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        this.add(compo, BorderLayout.CENTER);
    }
    
    private JPanel createComponent() {
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JLabel l = new JLabel("ポート番号:");
        l.setFont(new Font("dialog", Font.PLAIN, 12));
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        portField = new JTextField("5004");
        portField.setPreferredSize(new Dimension(80, 21));
        portField.setMaximumSize(new Dimension(80, 21));
        p.add(portField);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        l = new JLabel("エンコーディング:");
        l.setFont(new Font("dialog", Font.PLAIN, 12));
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        encCombo = new JComboBox(new String[]{"UTF8", "JISAutoDetect"});
        Dimension comboDimension = new Dimension(100, 21);
        encCombo.setPreferredSize(comboDimension);
        encCombo.setMaximumSize(comboDimension);
        encCombo.setMinimumSize(comboDimension);
        encCombo.addItemListener(new ItemListener() {
            
            public void itemStateChanged(ItemEvent e) {
                String val = (String)encCombo.getSelectedItem();
                if (server != null) {
                    server.setEncoding(val);
                }
            }
        });
        p.add(encCombo);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        p.add(Box.createHorizontalGlue());
        
        serverButton = new JButton("Start");
        serverButton.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                
                if (! running) {
                    try {
                        server = new ClaimAmountServer();
                        int port = Integer.parseInt(portField.getText());
                        server.setPort(port);
                        server.setTrace(debugArea);
                        String val = (String)encCombo.getSelectedItem();
                        server.setEncoding(val);
                        server.startServer();
                        statusField.setText("Server listening on port: " + port);
                        serverButton.setText("Stop");
                        running = true;
                    }
                    catch (Exception ex) {
                        statusField.setText("Exception creating socket: " + e.toString());
                    }
                }
                else {
                    try {
                        server.stopServer();
                        statusField.setText("Server stopped");
                        serverButton.setText("Start");
                        running = false;
                        server = null;
                    }
                    catch (Exception ex) {
                        statusField.setText("Exception closing socket: " + e.toString());
                    }
                }
            }
        });
        p.add(serverButton);        
        
        
        p.add(Box.createRigidArea(new Dimension(11,0)));
        l = new JLabel("ステータス:");
        l.setFont(new Font("dialog", Font.PLAIN, 12));
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        statusField = new JTextField();
        statusField.setPreferredSize(new Dimension(240, 21));
        statusField.setMaximumSize(new Dimension(240, 21));
        statusField.setEditable(false);
        statusField.setText("Server is not running");
        p.add(statusField);
        panel.add(p);
        panel.add(Box.createRigidArea(new Dimension(0, 7)));
        
        debugArea = new TextAreaTracer();
        debugArea.setDebug(true);
        JScrollPane scroller = new JScrollPane(debugArea);
        panel.add(scroller);
        
        return panel;
        
    }
    
    public static void main(String args[]) {
        
        ClaimAmountApp app = new ClaimAmountApp();
        
        JFrame f = new JFrame("Dolphin: 点数金額サーバテスト");
        f.getContentPane().add(app);
        f.setDefaultCloseOperation(3);
        f.setBounds(20, 20, 900, 700);
        f.setVisible(true);
    }
}
