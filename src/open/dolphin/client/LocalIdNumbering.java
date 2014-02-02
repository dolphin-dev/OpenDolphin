/*
 * LocalIdNumbering.java
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
package open.dolphin.client;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import open.dolphin.dao.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import netscape.ldap.*;

/**
 * Documet to show Patient and Health Insurance info.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LocalIdNumbering extends JPanel {
    
    private static String[] PROPERTIES = {
        "Facility ID", "Local ID", "Name","Kana","Sex","Birthday","Zip Code","Address","Phone"
    };
    private static String[] ATTRS = {      
      "mmlPid", "mmlLocalPid", "cn", "mmlKanaCn", "mmlSex", "mmlBirthday", "postalCode", "homePostalAddress", "homePhone"
    };
    private JTextField pidField;
    private JTextField localIdField;
    private JTextField statusField;
    private JButton searchButton;
    private JButton clearButton;
    private JButton okButton;
    private JTable patientTable;
    private LDAPConnection ld;
    

    /** Creates new PatientInfoDocument */
    public LocalIdNumbering() {
        super();
        
        JComponent compo = createComponent();
        compo.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        this.setLayout(new BorderLayout());
        this.add(compo);
    }
    
    public void setLDAPConnection(LDAPConnection ld) {
        this.ld = ld;
    }
       
    private JPanel createComponent() {
        
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
    
        Font font = new Font("Dialog", Font.PLAIN, 12);
        
        // Container
        JPanel panel = new JPanel(new BorderLayout(0, 11));
        
        // Patient ID panel
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JLabel l = new JLabel("Patient ID:");
        l.setFont(font);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        Dimension dim = new Dimension(150, 21);
        
        pidField = new JTextField();
        pidField.setPreferredSize(dim);
        pidField.getDocument().addDocumentListener(dl);
        p.add(pidField);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        searchButton = new JButton("Find(F)");
        searchButton.setFont(font);
        searchButton.setEnabled(false);
        searchButton.setMnemonic('F');
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        p.add(searchButton);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        l = new JLabel("Local ID:");
        l.setFont(font);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        localIdField = new JTextField();
        localIdField.setPreferredSize(dim);
        localIdField.getDocument().addDocumentListener(dl);
        p.add(localIdField);
        p.add(Box.createHorizontalGlue());
        
        clearButton = new JButton("Clear(C)");
        clearButton.setFont(font);
        clearButton.setEnabled(false);
        clearButton.setMnemonic('C');
        clearButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                localIdField.setText("");
            }
        });
        p.add(clearButton);
        p.add(Box.createRigidArea(new Dimension(5,0)));
        
        okButton = new JButton("Numbering(O)");
        okButton.setFont(font);
        okButton.setEnabled(false);
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                modify();
            }
        });
        p.add(okButton);
        
        // PatientInfo panel
        patientTable = createPatientTable();
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(patientTable.getTableHeader(), BorderLayout.NORTH);
        p2.add(patientTable, BorderLayout.CENTER);
        
        // Status field
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        
        l = new JLabel("Status:");
        l.setFont(font);
        p3.add(l);
        p3.add(Box.createRigidArea(new Dimension(11,0)));
        statusField = new JTextField();
        p3.add(statusField);
        
        panel.add(p, BorderLayout.NORTH);
        panel.add(p2, BorderLayout.CENTER);
        panel.add(p3, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createPatientTable() {
        
        int len = PROPERTIES.length;
        JTable table = new JTable(len, 2) {
            
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        DefaultTableColumnModel cmodel = (DefaultTableColumnModel)table.getColumnModel();
        TableColumn column0 = cmodel.getColumn(0);
        column0.setHeaderValue("Property");
        column0.setPreferredWidth(100);
        
        TableColumn column1 = cmodel.getColumn(1);
        column1.setHeaderValue("Value");
        column1.setPreferredWidth(300);
        
        table.setRowHeight(22);
        table.setRowSelectionAllowed(true);
                
        for (int i = 0; i < len; i++) {
            table.setValueAt(PROPERTIES[i], i, 0);
        }
        return table;
    }
    
    private void checkButton() {
        boolean pidEmpty = pidField.getText().equals("") ? true : false;
        searchButton.setEnabled(! pidEmpty);
        
        boolean localIdEmpty = localIdField.getText().equals("") ? true : false;
        clearButton.setEnabled(! localIdEmpty);
        
        boolean ok = ( (pidEmpty == false) && (localIdEmpty == false) ) ? true : false;
        okButton.setEnabled(ok);
    }
    
    private void search() {
        
        if (ld == null) {
            Toolkit.getDefaultToolkit().beep();
            log("No Server Connection");
            return;
        }
     
        log("");
        String pid = pidField.getText();
        String dn = getPatientDN(pid);
        
        Vector v = null;
        
        try {
            LDAPEntry entry = ld.read(dn, ATTRS);
            int len = ATTRS.length;
            v = new Vector();
            
            for (int i = 0; i < len; i++) {

                LDAPAttribute attr = entry.getAttribute(ATTRS[i]);
                if (attr == null) {
                    v.add(null);
                    continue;
                }
                // Enumerate on values for this attribute
                Enumeration enumVals = attr.getStringValues();
                if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                    v.add((String)enumVals.nextElement());
                }
            }
        }
        catch (Exception e) {
            log("Patient ID  " + pid + "  not found.");
            v = null;
        }
        
        if (v != null && v.size() > 0) {
            
            int size = v.size();
            for (int i = 0; i < size; i++) {
                patientTable.setValueAt((String)v.elementAt(i), i, 1);
            }
        }
    }
    
    private void modify() {
    
        String pid = pidField.getText();
        String localId = localIdField.getText();
        
        LDAPModificationSet mods = new LDAPModificationSet();        
        mods.add(LDAPModification.REPLACE, new LDAPAttribute("mmlLocalPid",localId));
        
        log("");
        try {
            ld.modify(getPatientDN(pid), mods);
            log(localId + "  successfully updated.");
        }
        catch (Exception e) {
            log(localId + "  could'nt updated.");
        }
    }
    
    private String getPatientDN(String pid) {     
        StringBuffer buf = new StringBuffer();
        buf.append("mmlPid=");
        buf.append(pid);
        buf.append(",");
        buf.append(LDAPDaoBean.getDN("patient"));
        return buf.toString();
    }
    
    private void log() {
        statusField.setText("");
    }
    
    private void log(String msg) {
        statusField.setText(msg);
    }
    
    public static void main (String args[]) {
        
        String host = null;
        if (args.length == 0) {
            System.out.println("Useage: open.dolphin.client.LocalIdNumbering DolphinIPAddress");
            System.exit(1);
            //host = "192.168.0.250";
        }
        else {
            host = args[0];
        }
        
        final LDAPConnection ld = new LDAPConnection();
        try {
            ld.connect(host, 389, "uid=directorymanager,ou=Managers,o=Dolphin","secret");
        }
        catch (Exception e) {
            //ld = null;
            Toolkit.getDefaultToolkit().beep();
            System.out.println("Could'nt connet to the Dolphin Server");
            System.exit(1);
        }
        
        LocalIdNumbering pt = new LocalIdNumbering();
        pt.setLDAPConnection(ld);
        
        final JFrame f = new JFrame("Dolphin: Local ID Management");
        f.addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent e) {
                f.setVisible(false);
                f.dispose();
                if (ld != null) {
                    try {
                        ld.disconnect();
                    }
                    catch(Exception e2) {
                    }
                }
                System.exit(1);
            }
        });
        f.getContentPane().add(pt, BorderLayout.CENTER);
        f.setLocation(200,300);
        f.pack();
        f.setVisible(true);
    }    
}