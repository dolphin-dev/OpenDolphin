/*
 * NewKarteDialog.java
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

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.infomodel.DInsuranceInfo;


import java.awt.*;
import java.awt.event.*;


/**
 * Dialog to select Health Insurance for new Karte.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class NewKarteDialog extends RnDialog {
    
    private JButton okButton;
    private JButton cancelButton;
    private JList insuranceList;
    //private JComboBox departmentCombo;
    private JLabel departmentLabel;
   
    /** Creates new OpenKarteDialog */
    public NewKarteDialog(Frame f, String title, boolean modal) {        
        super(f, title, modal);       
    }
            
    public void setValue(Object o) {
    }
    
    public void setDepartment(String dept) {
        if (dept != null) {
            //departmentCombo.setSelectedItem(dept);
            departmentLabel.setText(dept);
        }
    }
    
    public void setInsurance(Object[] o) {
        insuranceList.setListData(o);
    }
        
    protected JPanel createComponent() {
     
        //JPanel panel = new JPanel(new BorderLayout(0,17));
        
        // Department combo
        /*Object[] dept = Project.getDepartment();
        departmentCombo = new JComboBox(dept);
        JPanel dp = new JPanel();
        dp.setLayout(new BoxLayout(dp, BoxLayout.X_AXIS));
        dp.add(new JLabel("êfó√â»:"));
        dp.add(Box.createHorizontalStrut(11));
        dp.add(departmentCombo);*/
        
        departmentLabel = new JLabel();
        JPanel dp = new JPanel();
        dp.setLayout(new BoxLayout(dp, BoxLayout.X_AXIS));
        dp.add(new JLabel("êfó√â»:"));
        dp.add(Box.createHorizontalStrut(11));
        dp.add(departmentLabel);
        
        // Insurance List
        insuranceList = new JList();
        insuranceList.setFixedCellWidth(200);
        insuranceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        insuranceList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting() == false) {
                    Object o = insuranceList.getSelectedValue();
                    boolean ok = o != null ? true : false;
                    okButton.setEnabled(ok);
                }
            }
        });
        JPanel ip = new JPanel(new BorderLayout(9, 0));
        ip.setBorder(BorderFactory.createTitledBorder("ï€åØëIë"));
        ip.add(insuranceList, BorderLayout.CENTER);
        ip.add(new JLabel(ClientContext.getImageIcon("insurance.jpg")), BorderLayout.WEST);
        //panel.add(p, BorderLayout.CENTER);
        
        // Button panel
        JPanel bp = new JPanel();
        bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
        bp.add(Box.createHorizontalGlue());
        
        // OK button
        String buttonText =  (String)UIManager.get("OptionPane.okButtonText");
        okButton = new JButton(buttonText);
        okButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                doOk();
            }
        });
        okButton.setEnabled(false);
        bp.add(okButton);
        bp.add(Box.createRigidArea(new Dimension(5, 0)));
                
        // Cancel Button
        buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = new JButton(buttonText);
        cancelButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                doCancel();
            }
        });
        bp.add(cancelButton);        

        //panel.add(p, BorderLayout.SOUTH);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(dp);
        panel.add(Box.createVerticalStrut(7));
        panel.add(ip);
        panel.add(Box.createVerticalStrut(11));
        panel.add(bp);
        return panel;
    }
                
    private void doOk() {
        NewKarteParams params = new NewKarteParams();
        //params.setDepartment((String)departmentCombo.getSelectedItem());
        params.setDepartment(departmentLabel.getText());
        params.setDInsuranceInfo((DInsuranceInfo)insuranceList.getSelectedValue());
        value = (Object)params;
        dispose();
    }
        
    private void doCancel() {
        value = null;
        dispose();
    }
}