/*
 * DolphinTestClient.java
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
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DolphinTestClient extends JPanel {
    
    private static final String[] encList = {"UTF8"};
    private static final int LIST_CELL_WIDTH = 120;
    private static final Object[] samples = {
        "Sample-1", "1_Claim","2_Claim","3_Claim","4_Claim","5_Claim","6_Claim","7_Claim","8_Claim"
    };
    private JList sampleList;
    private JTextField portField;
    private JTextField hostField;
    private JComboBox encCombo;
    private JButton sendButton;
    private TextArea debugArea;
    private String enc = "UTF8";
    

    /** Creates new DolphinTestClient */
    public DolphinTestClient() {
        super(new BorderLayout());
        
        Font font = new Font("Dialog", Font.PLAIN, 12);
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("List.font", font);
        
        JPanel c = createComponent();
        c.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        this.add(c, BorderLayout.CENTER);
    }
    
    private JPanel createComponent() {
     
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        // Sample List
        sampleList = new JList(samples);
        sampleList.setFixedCellWidth(LIST_CELL_WIDTH);
        sampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sampleList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                if (e.getValueIsAdjusting() == false) {
                    String o = (String)sampleList.getSelectedValue();
                    showSample(o);
                    checkButton();
                }
            }
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(sampleList);
        p.setBorder(BorderFactory.createTitledBorder("サンプル選択"));
        panel.add(p);
        
        // LeftPanel
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        
        // Command panel   
        DocumentListener dl = new DocumentListener() {
            
            public void insertUpdate(DocumentEvent e) {
                checkButton();
            }
            
            public void removeUpdate(DocumentEvent e) {
                checkButton();
            }
            
            public void changedUpdate(DocumentEvent e) {
            }
        };
        
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createRigidArea(new Dimension(5,0)));
        JLabel l = new JLabel("サーバポート:");
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(5,0)));
        
        portField = new JTextField("5001");
        portField.getDocument().addDocumentListener(dl);
        portField.setPreferredSize(new Dimension(80, 21));
        portField.setMaximumSize(new Dimension(80, 21));
        p.add(portField);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        l = new JLabel("サーバIPアドレス:");
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(5,0)));
        
        hostField = new JTextField("127.0.0.1");
        hostField.getDocument().addDocumentListener(dl);
        hostField.setPreferredSize(new Dimension(110, 21));
        hostField.setMaximumSize(new Dimension(110, 21));
        p.add(hostField);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        l = new JLabel("Encoding:");
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        encCombo = new JComboBox(encList);
        //Dimension comboDimension = new Dimension(100, 21);
        //encCombo.setPreferredSize(comboDimension);
        //encCombo.setMaximumSize(comboDimension);
        //encCombo.setMinimumSize(comboDimension);
        encCombo.addItemListener(new ItemListener() {
            
            public void itemStateChanged(ItemEvent e) {
                String val = (String)encCombo.getSelectedItem();                
            }
        });
        p.add(encCombo);
        p.add(Box.createHorizontalGlue());
        
        sendButton = new JButton("送 信");
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                sendData();
            }
        });
        sendButton.setEnabled(false);
        p.add(sendButton);
        p.setBorder(BorderFactory.createTitledBorder("送 信"));
        p.add(Box.createRigidArea(new Dimension(7, 0)));
        
        left.add(p);
        left.add(Box.createRigidArea(new Dimension(0, 7)));
        
        // TextArea
        p = new JPanel(new BorderLayout());
        debugArea = new TextArea();
        JScrollPane scroller = new JScrollPane(debugArea);
        p.add(scroller);
        p.setBorder(BorderFactory.createTitledBorder("コンテンツ"));
        left.add(p);
        
        panel.add(Box.createRigidArea(new Dimension(11,0)));
        panel.add(left);
        return panel;        
    }
    
    private void checkButton() {
        boolean ok = ( (! portField.getText().equals("")) &&
                       (! hostField.getText().equals("")) &&
                       (sampleList.getSelectedValue() != null) ) ? true : false;
       sendButton.setEnabled(ok);
    }
    
    private void sendData() {
    
        try {           
            String fileName = (String)sampleList.getSelectedValue();
            String data = getSampleData(fileName);
            byte[] bytes = data.getBytes(enc);
            
            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText());
            Socket s = new Socket(host, port);
            
            BufferedOutputStream writer = new BufferedOutputStream(new DataOutputStream(s.getOutputStream()));
            BufferedInputStream reader = new BufferedInputStream(new DataInputStream(s.getInputStream()));
            
            // Write UTF8 data
            writer.write(bytes);
            writer.write(0x04);
            writer.flush();
            
            // Read result
            int c = reader.read();
            if (c == 0x06) {
                debugArea.append("Recieved ACK" + "\n");
            }
            else {
                debugArea.append("Recieved NAK" + "\n");
            }
            s.close();            
        }
        catch (Exception e) {
            debugArea.append("Exception while sending data: " + e.toString() + "\n");
        }
    }
    
    private void showSample(String fileName) {    
        debugArea.setText("");
        debugArea.setText(getSampleData(fileName));
    }
    
    private String getSampleData(String fileName) {
        
        String ret = null;
        try {
            InputStream in = this.getClass().getResourceAsStream("/open/dolphin/resources/" + fileName + ".xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "SHIFT_JIS"));
            StringBuffer buf = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append("\n");
            }
            ret = buf.toString();
        }
        catch (Exception e) {
             debugArea.append("Exception while reading data from resource: " + e.toString());
        }
        return ret;
    }
    
    public static void main(String args[]) {
        
        DolphinTestClient app = new DolphinTestClient();
        
        JFrame f = new JFrame("Dolphin: TestClient");
        f.getContentPane().add(app);
        f.setDefaultCloseOperation(3);
        f.setBounds(20, 20, 980, 700);
        f.setVisible(true);
    }
}