/*
 * LocalIdNumbering.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
import open.dolphin.infomodel.Patient;
import open.dolphin.plugin.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Documet to show Patient and Health Insurance info.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LocalIdNumberingService extends AbstractFramePlugin {
    
    private static String[] PROPERTIES = {
        "施設ID", "地域ID", "氏 名","カ ナ","性 別","生年月日","郵便番号","住 所","電 話"
    };
    
    private JTextField pidField;
    private JTextField localIdField;
    private JTextField statusField;
    private JButton searchButton;
    private JButton clearButton;
    private JButton okButton;
    private JTable patientTable;
    private SqlPatientDao dao;

    /** Creates new PatientInfoDocument */
    public LocalIdNumberingService() {
    }
    
    public void initComponent() {
        JPanel ui = createContent();
        ui.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        centerFrame(new Dimension(680,300), ui);        
    }
          
    private JPanel createContent() {
        
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
        JLabel l = new JLabel("患者 ID:");
        l.setFont(font);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        Dimension dim = new Dimension(150, 21);
        
        pidField = new JTextField();
        pidField.setPreferredSize(dim);
        pidField.getDocument().addDocumentListener(dl);
        pidField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (! pidField.getText().trim().equals("")) {
                    searchButton.doClick();
                }
            }
        });
        p.add(pidField);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        searchButton = new JButton("検索(F)");
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
        
        l = new JLabel("地域 ID:");
        l.setFont(font);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(11,0)));
        
        localIdField = new JTextField();
        localIdField.setPreferredSize(dim);
        localIdField.getDocument().addDocumentListener(dl);
        localIdField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (! localIdField.getText().trim().equals("")) {
                    okButton.doClick();
                }
            }
        });
        p.add(localIdField);
        p.add(Box.createHorizontalGlue());
        
        clearButton = new JButton("クリア(C)");
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
        
        okButton = new JButton("発番(O)");
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
        column0.setHeaderValue("項 目");
        column0.setPreferredWidth(100);
        
        TableColumn column1 = cmodel.getColumn(1);
        column1.setHeaderValue("値");
        column1.setPreferredWidth(300);
        
        table.setRowHeight(22);
        table.setRowSelectionAllowed(true);
                
        for (int i = 0; i < len; i++) {
            table.setValueAt(PROPERTIES[i], i, 0);
        }
        return table;
    }
    
    private void checkButton() {
        boolean pidEmpty = pidField.getText().trim().equals("") ? true : false;
        searchButton.setEnabled(! pidEmpty);
        
        boolean localIdEmpty = localIdField.getText().trim().equals("") ? true : false;
        clearButton.setEnabled(! localIdEmpty);
        
        boolean ok = ( (pidEmpty == false) && (localIdEmpty == false) ) ? true : false;
        okButton.setEnabled(ok);
    }
    
    private void search() {
     
        clearData();
        String pid = pidField.getText().trim();
        
        if (dao == null) {
            dao = (SqlPatientDao)SqlDaoFactory.create(this, "dao.patient");
            if (dao == null) {
                log("データベースに接続できません。");
                return;
            }
        }
        
        Patient entry = (Patient)dao.getById(pid);
        
        if (entry == null) {
            
            clearData();
            log("患者 ID  " + pid + "  は存在しません。");
            return;
        
        } else {
            
            // 施設 ID
            patientTable.setValueAt(entry.getId(), 0, 1);
            
            // 地域 ID
            String val = entry.getLocalId();
            patientTable.setValueAt(val, 1, 1);
            
            // 氏 名
            patientTable.setValueAt(entry.getName(), 2, 1);
            
            // カ ナ
            patientTable.setValueAt(entry.getKanaName(), 3, 1);
            
            // 性 別
            patientTable.setValueAt(entry.getGender(), 4, 1);
            
            // 生年月日
            patientTable.setValueAt(entry.getBirthday(), 5, 1);
            
            // 郵便番号
            patientTable.setValueAt(entry.getHomePostalCode(), 6, 1);
            
            // 住 所
            patientTable.setValueAt(entry.getHomeAddress(), 7, 1);
            
            // 電 話
            patientTable.setValueAt(entry.getHomePhone(), 8, 1);
            
            if ( (val != null) && ! val.equals("") ) {
                
                log("地域ID は発番ずみです。");
                
            } else {
                log("地域ID は発番されていません。");
            }
        }
    }
    
    private void modify() {
    
        String pid = pidField.getText().trim();
        String localId = localIdField.getText().trim();
        
        if (dao == null) {
            dao = (SqlPatientDao)SqlDaoFactory.create(this, "dao.patient");
            if (dao == null) {
                log("データベースに接続できません。");
                return;
            }
        }
            
        boolean result = dao.modifyLocalId(pid, localId);
        
        String msg = result ? "発番しました。" : "発番できませんでした。";
        
        log(msg);

    }
    
    private void log() {
        statusField.setText("");
    }
    
    private void log(String msg) {
        statusField.setText(msg);
    }
    
    private void clearData() {
        for (int i = 0; i < PROPERTIES.length; i++) {
            patientTable.setValueAt(null, i, 1);
        }
        localIdField.setText("");
        statusField.setText("");
    }
}