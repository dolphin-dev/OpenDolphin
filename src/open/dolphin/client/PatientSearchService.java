/*
 * PatientSearch.java
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
import javax.swing.table.*;
import javax.swing.event.*;

import open.dolphin.dao.*;
import open.dolphin.exception.PluginException;
import open.dolphin.infomodel.DInsuranceInfo;
import open.dolphin.infomodel.Patient;
import open.dolphin.infomodel.PatientVisit;
import open.dolphin.order.*;
import open.dolphin.plugin.*;
import open.dolphin.project.*;
import open.dolphin.table.*;
import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.awt.im.InputSubset;

/**
 * 患者検索サービス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PatientSearchService extends AbstractFramePlugin {
    
    private int number = 10000;
    
    // GUI components
    private JTextField pidField;
    private JButton pidBtn;
    private JTextField nameField;
    private JButton nameBtn;
    private PatientListTableModel tModel;
    private JTextField countField;
    private JProgressBar progressBar;

    /** Creates new PatientSearch */
    public PatientSearchService() {
    }
    
    public void initComponent() {
        JPanel ui = createContent();
        ui.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        centerFrame(new Dimension(830,300), ui);        
    }
        
    private JPanel createContent() {
        
        JPanel ui = new JPanel();
        
        // Set layout
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));
        
        // Search panel
        JPanel search = createSearchPanel();
        ui.add(search);
        ui.add(createVSpace(11));
        
        // Serach Result Table
        String[] columnNames = new String[]{"患者ID", "氏  名", "カ  ナ", "性別","生年月日","住  所"};
        tModel = new PatientListTableModel(columnNames, 3);
        final JTable table = new JTable(tModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setColumnWidths(table, new int[]{80, 120, 120, 30, 80, 250});
        JScrollPane scroller = new JScrollPane(table);
        ui.add(scroller);
        ui.add(createVSpace(11));
        
        // Status panel
        JPanel status = createStatusPanel();
        ui.add(status);
        
        // Coonect
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final Patient  o = (Patient)tModel.getObject(table.getSelectedRow());
                    if (o != null) {
                    	openKarte(o);
                    }
                }
            }
        });
        
        tModel.addPropertyChangeListener("busyProp", this);
        tModel.addPropertyChangeListener("countProp", this);
        
        return ui;
    }
    
    private int setColumnWidths(JTable t, int[] widths ) {
        Enumeration en = t.getColumnModel().getColumns();
        int i = 0;
        int width = 0;
        while( en.hasMoreElements() ) {
            TableColumn col = (TableColumn)en.nextElement();
            col.setMinWidth( widths[i] );
            col.setPreferredWidth( widths[i] );
            col.setResizable( true );
            width += col.getPreferredWidth();
            i++;
        }
        t.sizeColumnsToFit( 0 );
        return width;
    }        
    
    private JPanel createSearchPanel() {
        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkButtons();
            }

            public void removeUpdate(DocumentEvent e) {
                checkButtons();    
            }  

            public void changedUpdate(DocumentEvent e) {
            } 
        };
        
        // Search panel
        JPanel search = createBoxLayoutPanel(0);
        
        // Search panel by Id
        JPanel p = createBoxLayoutPanel(0);
        p.add(new JLabel("ID:"));
        p.add(createHSpace(11));
        pidField = new JTextField();
        pidField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
               //pidField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                pidField.getInputContext().setCharacterSubsets(null);
            }
            public void focusLosted(FocusEvent event) {
               pidField.getInputContext().setCharacterSubsets(null);
            }
        });
        Dimension dim = new Dimension(80, 20);
        pidField.setPreferredSize(dim);
        pidField.setMaximumSize(dim);
        pidField.getDocument().addDocumentListener(dl);
        pidField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pidBtn.doClick();
            }
        });
        p.add(pidField);
        p.add(createHSpace(11));
        pidBtn = new JButton("(I)",ClientContext.getImageIcon("Zoom24.gif"));
        pidBtn.setMnemonic('I');
        pidBtn.setEnabled(false);
        pidBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pid = pidField.getText().trim();
                if (pid != null) {
                    //doPidSearch(pid);
                    tModel.getById(pid);
                }
            }
        });
        p.add(pidBtn);
        //setTitledBorder(p, "ID検索");
        search.add(p);
        search.add(createHSpace(11));
        
        // Search panel by name
        p = createBoxLayoutPanel(0);
        p.add(new JLabel("姓 / 名 (漢字・カタカナ・ひらかな):"));
        p.add(createHSpace(11));
        nameField = new JTextField();
        nameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                nameField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
            public void focusLosted(FocusEvent event) {
                nameField.getInputContext().setCharacterSubsets(null);
            }
        });
        dim = new Dimension(160, 20);
        nameField.setPreferredSize(dim);
        nameField.setMaximumSize(dim);
        nameField.getDocument().addDocumentListener(dl);
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = nameField.getText().trim();
                if (text != null){
                    nameBtn.doClick();
                }
            }
        });
        p.add(nameField);
        p.add(createHSpace(11));
        nameBtn = new JButton("(N)",ClientContext.getImageIcon("Zoom24.gif"));
        nameBtn.setMnemonic('N');
        nameBtn.setEnabled(false);
        nameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (name != null) {
                    //doNameSearch(name);
                    tModel.getByName(name);
                }
            }
        });
        p.add(nameBtn);
        search.add(p);
        search.add(Box.createHorizontalGlue());
        
        return search;
    }
    
    private JPanel createStatusPanel() {
        JPanel p = createBoxLayoutPanel(0);
        
        p.add(Box.createHorizontalGlue());
        p.add(new JLabel("該当件数:"));
        p.add(createHSpace(11));
        countField = new JTextField();
        countField.setEditable(false);
        Dimension dim = new Dimension(30, 20);
        countField.setPreferredSize(dim);
        countField.setMaximumSize(dim);
        p.add(countField);
        p.add(createHSpace(11));
        
        progressBar = new JProgressBar(0, 100);
        dim = new Dimension(150, 17);
        progressBar.setPreferredSize(dim);
        progressBar.setMaximumSize(dim);
        p.add(progressBar);
        
        return p;
    }   
    
    ///////////////////////////////////////////////////////////////////////////
    
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        
        if (prop.equals("busyProp")) {
            
            boolean b = ((Boolean)e.getNewValue()).booleanValue();
            
            if (b) {
                progressBar.setIndeterminate(true);
                
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setValue(0);
            }
            
            return;
            
        } else if (prop.equals("countProp")) {
            Integer i = (Integer)e.getNewValue();
            countField.setText(String.valueOf(i.intValue()));
            return;
            
        } else if (prop.equals("pvtNumber")) {
            return;
        }
        
        super.propertyChange(e);
    }
        
    private void openKarte(Patient entry) {
        
        // Patient Id
        String pid = entry.getId();
        
        // Fujitsu
        SqlPvtDao dao = (SqlPvtDao)SqlDaoFactory.create(this, "dao.pvt");
        ArrayList results = dao.getHealthInsurance(pid);
            
        PatientVisit pvt = new PatientVisit();
        pvt.setNumber(number++);
        pvt.setPatient(entry);
        
        if (results != null) {
            int size = results.size();
            for (int i = 0; i < size; i++) {
                pvt.addInsuranceInfo((DInsuranceInfo)results.get(i));
            }
        }
        
        // Fujitsu 対応
        //pvt.setNoVisit(false);
        
        // 2003-10-30
        //pvt.department = Project.getCreatorInfo().getPersonalizedInfo().getDepartment().getDepartmentName(0).getName();
        String deptId = Project.getUserProfileEntry().getDepartmentId();
        pvt.setDepartment(MMLTable.getDepartmentName(deptId));
        
        ChartPlugin chart = (ChartPlugin)ClassFactory.create("open.dolphin.client.ChartPlugin");
        chart.setTitle(entry.getName());
        chart.setPatientVisit(pvt);
        chart.setReadOnly(Project.isReadOnly());
        chart.addPropertyChangeListener("pvtNumber", this);
        try {
        	chart.init();
			chart.start();
			ClientContextStub stub = (ClientContextStub)ClientContext.getClientContextStub();
			stub.addCurrentService(chart);
        } catch (PluginException e) {
			System.out.println("Exception while opening the chart: " + e.toString());
			e.printStackTrace();        	
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    private void checkButtons() {
        boolean pidText = (pidField.getText().equals("")) ? false : true;
        pidBtn.setEnabled(pidText);
        
        boolean nameText = (nameField.getText().equals("")) ? false : true;
        nameBtn.setEnabled(nameText);
    }
    
    private JPanel createBoxLayoutPanel(int direction) {
        
        JPanel p = new JPanel();
        
        if (direction == 0) {
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
            
        } else {
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        }
        
        return p;
    }
    
    private Component createHSpace(int space) {
        return Box.createRigidArea(new Dimension(space, 0));
    }
    
    private Component createVSpace(int space) {
        return Box.createRigidArea(new Dimension(0, space));
    }
    
    private void setTitledBorder(JPanel p, String title) {
        p.setBorder(BorderFactory.createTitledBorder(title));
    }
       
    private void debugString(String s) {
        System.out.println(s);
    }
       
    protected final class PatientListTableModel extends ObjectTableModel {

        private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);

        /** Creates a new instance of AbstractDBTableModel */
        public PatientListTableModel(String[] columnNames, int startNumRows) {
            super(columnNames, startNumRows);
        }

        public Object getValueAt(int row, int col) {
            
            Patient entry = (Patient)getObject(row);
            
            if (entry == null) {
                return null;
            }
            
            Object o = null;
            //COLUMN_NAMES = {"患者ID", "氏  名", "カ  ナ", "性別","生年月日","住  所"};
            
            switch(col) {
                
                case 0:
                    o = entry.getId();
                    break;
                    
                case 1:
                    o = entry.getName();
                    break;
                    
                case 2:
                    o = entry.getKanaName();
                    break;
                    
                case 3:
                    String val = entry.getGender();
                    if (val.equals("female")) {
                        o = "女性";
                    } else if (val.equals("male")) {
                        o = "男性";
                    } else if (val != null) {
                        o = val;
                    }
                    break;
                    
                case 4:
                    val = entry.getBirthday();
                    String age = null;
                    try {
                        age = AgeCalculator.getAge(val);
                    
                    } catch (Exception e) {
                        age = null;
                    }
                    StringBuffer buf = new StringBuffer();
                    buf.append(val);
                    if (age != null) {
                        buf.append("(");
                        buf.append(age);
                        buf.append(")");
                    }
                    o = buf.toString();
                    break;
                    
                case 5:
                    o = entry.getHomeAddress();
                    break;
                    
                default:
                    //assert false : "invalid col";
            }
            
            return o;   
        }
    
        public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
            boundSupport.addPropertyChangeListener(prop, l);
        }
        
        public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
            boundSupport.removePropertyChangeListener(prop, l);
        }     
                
        private void fireBusy(boolean b) {
            boundSupport.firePropertyChange("busyProp", !b, b);
        }

        private void fireCount() {
            int count = getObjectCount();
            boundSupport.firePropertyChange("countProp", -1, count);
        }
               
        public void getByName(final String name) {
        
            final SqlPatientDao dao = (SqlPatientDao)SqlDaoFactory.create(this, "dao.patient");

            Runnable r = new Runnable() {
                public void run() {
                    fireBusy(true);
                    ArrayList results = dao.searchByName(name);
                    setObjectList(results);
                    fireCount();
                    fireBusy(false);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        
        public void getById(final String id) {

            final SqlPatientDao dao = (SqlPatientDao)SqlDaoFactory.create(this, "dao.patient");

            Runnable r = new Runnable() {
                public void run() {
                    fireBusy(true);
                    ArrayList results = dao.searchById(id);
                    setObjectList(results);
                    fireCount();
                    fireBusy(false);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }
}