/*
 * FirstClinicStampEditor.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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

import open.dolphin.infomodel.BirthInfo;
import open.dolphin.infomodel.Childhood;
import open.dolphin.infomodel.FamilyHistory;
import open.dolphin.infomodel.FirstClinicModule;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.Vaccination;
import open.dolphin.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputSubset;
import java.util.ArrayList;


/**
 * 初診時特有情報モジュールエディタ
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class FirstClinicStampEditor extends StampModelEditor {
    
    static Dimension defaultSize = new Dimension(600, 230);
        
    FamilyHistoryPanel familyHistoryPanel;
    ChildhoodPanel childhoodPanel;
    PastHistoryPanel pastHistoryPanel;
    ChiefComplaintsPanel chiefComplaintsPanel;
    PresentIllnessPanel presentIllnessPanel;
    boolean okState;
    private Module savedStamp;

    /** Creates new FirstClinicEditor */
    public FirstClinicStampEditor() {
        
        this.title = "初診時特有情報";                
        JTabbedPane tabbedPane = new JTabbedPane();
        
        familyHistoryPanel = new FamilyHistoryPanel();
        childhoodPanel = new ChildhoodPanel();
        pastHistoryPanel = new PastHistoryPanel();
        chiefComplaintsPanel = new ChiefComplaintsPanel();
        presentIllnessPanel = new PresentIllnessPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(familyHistoryPanel);
        panel.add(Box.createVerticalStrut(11));
        panel.add(pastHistoryPanel);
        tabbedPane.addTab("家族歴・既往歴", panel);
        tabbedPane.addTab("小児期情報", childhoodPanel);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(chiefComplaintsPanel);
        panel.add(Box.createVerticalStrut(11));
        panel.add(presentIllnessPanel);
        tabbedPane.addTab("主訴・現病歴", panel);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public Object getValue() {

        FirstClinicModule model = new FirstClinicModule();

        // 家族歴
        FamilyHistory[] familyHistory = familyHistoryPanel.getValue();
        if (familyHistory != null) {
            model.setFamilyHistory(familyHistory);
        }

        // 小児期
        Childhood childhood = childhoodPanel.getValue();
        if (childhood != null) {
            model.setChildhood(childhood);
        }

        // 既往歴
        String text = pastHistoryPanel.getValue();
        if (text != null) {
            model.setPastHistory(text);
        }

        // 主訴
        text = chiefComplaintsPanel.getValue();
        if (text != null) {
            model.setChiefComplaints(text);
        }

        // 現病歴
        text = presentIllnessPanel.getValue();
        if (text != null) {
            model.setPresentIllnessNotes(text);
        }
        
        savedStamp.setModel(model);
        
        return (Object)savedStamp;
    }
    
    public void setValue(Object stamp) {
        
        savedStamp = (Module)stamp;
        IInfoModel model = (IInfoModel)savedStamp.getModel();
        if (model == null) return;
        
        familyHistoryPanel.setValue(((FirstClinicModule)model).getFamilyHistory());
        childhoodPanel.setValue(((FirstClinicModule)model).getChildhood());
        pastHistoryPanel.setValue(((FirstClinicModule)model).getPastHistory()); 
        chiefComplaintsPanel.setValue(((FirstClinicModule)model).getChiefComplaints()); 
        presentIllnessPanel.setValue(((FirstClinicModule)model).getPresentIllnessNotes());
    }
        
    ////////////////////////////////////////////////////////////////////////////
    
    class FamilyHistoryPanel extends JPanel {
        
        private AbstractTableModel familyModel;
        
        private JTable familyTable;
        
        private JComboBox relationCombo;
        
        public FamilyHistoryPanel() {
            super(new BorderLayout(1,1));

            familyTable = createFamilyTable();

            // カラムヘッダーを表示する
            add(familyTable.getTableHeader(), BorderLayout.NORTH);
            add(familyTable, BorderLayout.CENTER);
            setPreferredSize(defaultSize);
        }
        
        protected FamilyHistory[] getValue() {

            FamilyHistory[] familyHistory = null;
            ArrayList list = null;
            FamilyHistory item = null;

            int rowCount = familyTable.getRowCount();
            String data1 = null;
            String data2 = null;

            for (int i = 0; i < rowCount ; i++) {

                data1 = (String)familyTable.getValueAt(i, 0);        // relation
                data2 = (String)familyTable.getValueAt(i, 1);        // diagnosis

                if ( (!data1.equals("")) && (!data2.equals("")) ) {

                    // FamilyHistoryItem を生成
                    item = new FamilyHistory();

                    // relation & diagnosis
                    item.setRelation(data1);
                    item.setDiagnosis(data2);

                    // age
                    data1 = (String)familyTable.getValueAt(i, 2);
                    if (! data1.equals("")) {
                        item.setAge(data1);
                    }

                    // memo
                    data1 = (String)familyTable.getValueAt(i, 3);
                    if (! data1.equals("")) {
                        item.setMemo((String)data1);
                    }

                    if (list == null) {
                        list = new ArrayList();
                    }

                    list.add(item);
                }  
            }
            
            if (list != null && list.size() > 0) {
            	int cnt = list.size();
				familyHistory = new FamilyHistory[cnt];
				for (int i = 0; i < cnt; i++) {
					familyHistory[i] = (FamilyHistory)list.get(i);
				}
            }
            
            return familyHistory;
        }
        
        protected void setValue(FamilyHistory[] items) {
            
            if (items == null) return;
            
            int len = items.length;
            
            for (int i = 0; i < len; i++) {
                
                FamilyHistory item = items[i];
                
                String val = item.getRelation();
                String val2 = item.getDiagnosis();
                
                if (val != null && val2 != null) {
                    familyTable.setValueAt(val, i, 0);
                    familyTable.setValueAt(val, i, 1);
                }
                
                val = item.getAge();
                if (val != null) {
                    familyTable.setValueAt(val, i, 2);
                } 
                
                val = item.getMemo();
                if (val != null) {
                    familyTable.setValueAt(val, i, 3);
                }   
            }
        }
        
        private JTable createFamilyTable() {
        
            familyModel = new AbstractTableModel() {

                Object[][] data = {
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""}
                };

                String [] columnNames = ClientContext.getStringArray("familyHistory.columnNames");

                public int getRowCount() {
                    return data.length;
                }

                public int getColumnCount() {
                    return data[0].length;
                }

                public Object getValueAt(int row, int col) {
                    return data[row][col];
                }

                public String getColumnName(int col) {
                    return columnNames[col];
                }

                public Class getColumnClass(int col) {
                    return data[0][col].getClass();
                }

                public boolean isCellEditable(int row, int col) {
                    return true;
                }

                public void setValueAt(Object value, int row, int col) {
                    if (value == null || ((String)value).trim().equals("")) {
                        return;
                    }
                    data[row][col] = value;
                    fireTableCellUpdated(row, col);
                    
                    String val1 = (String)data[0][0];
                    String val2 = (String)data[0][1];
                    
                    boolean newOkState = ( (! val1.equals("")) && (! val2.equals("")) )
                                       ? true : false;
                    if (newOkState != okState) {
                        okState = newOkState;
                        setValidModel(okState);
                    }   
                }            
            };
            JTable table = new JTable(familyModel);
            table.setSurrendersFocusOnKeystroke(true);

            // Editor for IME-ON/OFFfields
            IMECellEditor imeOn = new IMECellEditor(new JTextField(), 1, true);
            IMECellEditor imeOff = new IMECellEditor(new JTextField(), 1, false);

            // 続柄に comboBox を設定する
            relationCombo = new JComboBox(ClientContext.getStringArray("familyHistory.relation"));
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setCellEditor(new DefaultCellEditor(relationCombo));
            
            // 疾患名フィールド
            column = table.getColumnModel().getColumn(1);
            column.setCellEditor(imeOn);
            
            // 年齢
            column = table.getColumnModel().getColumn(2);
            column.setCellEditor(imeOff);
            
            // メモ
            column = table.getColumnModel().getColumn(3);
            column.setCellEditor(imeOn);
            
            return table;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
      
    class ChildhoodPanel extends JPanel {
        
        // 小児期情報
        // 出生時情報
        private JTextField facilityName;
        private JTextField deliveryWeeks;
        private JTextField deliveryMethod;
        private JTextField bodyWeight;
        private JTextField bodyHeight;
        private JTextField chestCircumference;
        private JTextField headCircumference;
        private JTextField memo;
        
        private AbstractTableModel vaccinationModel;
        private JTable vaccinationTable;
        
        public ChildhoodPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(createBirthInfoPanel());
            add(Box.createVerticalStrut(7));
            add(createVaccinationPanel());
        }
        
        protected Childhood getValue() {

            Childhood childhood = new Childhood();

            BirthInfo birthInfo = getBirthInfo();
            if (birthInfo != null) {
                childhood.setBirthInfo(birthInfo);
            }

            Vaccination[] vaccination = getVaccination();
            if (vaccination != null) {
                childhood.setVaccination(vaccination);
            }
            return childhood.isValidModel() ? childhood : null;
        }
        
        protected void setValue(Childhood model) {
            
            if (model == null) return;
            
            BirthInfo birthInfo = model.getBirthInfo();
            
            if (birthInfo != null) {
                
                String val = birthInfo.getFacilityName();                
                if (val != null) {
                    facilityName.setText(val);
                }
                
                val = birthInfo.getDeliveryWeeks();                
                if (val != null) {
                    deliveryWeeks.setText(val);
                } 
                
                val = birthInfo.getDeliveryMethod();                
                if (val != null) {
                    deliveryMethod.setText(val);
                } 
                
                val = birthInfo.getBodyWeight();                
                if (val != null) {
                    bodyWeight.setText(val);
                } 
                
                val = birthInfo.getBodyHeight();                
                if (val != null) {
                    bodyHeight.setText(val);
                }   
                
                val = birthInfo.getChestCircumference();                
                if (val != null) {
                    chestCircumference.setText(val);
                } 
                
                val = birthInfo.getHeadCircumference();                
                if (val != null) {
                    headCircumference.setText(val);
                }
                
                val = birthInfo.getMemo();                
                if (val != null) {
                    memo.setText(val);
                }                
            }
            
            Vaccination[] items = model.getVaccination();
            if (items != null) {
                
                int len = items.length;
                
                for (int i = 0; i < len; i++) {
                    
                    Vaccination item = items[i];
                    
                    String val = item.getVaccine();
                    if (val != null) {
                        vaccinationTable.setValueAt(val, i, 0);
                    }
                    
                    val = item.getInjected();
                    if (val != null) {
                        vaccinationTable.setValueAt(val, i, 1);
                    }
                    
                    val = item.getAge();
                    if (val != null) {
                        vaccinationTable.setValueAt(val, i, 2);
                    }
                    
                    val = item.getMemo();
                    if (val != null) {
                        vaccinationTable.setValueAt(val, i, 3);
                    }
                }   
            }
        }
                    
        private final BirthInfo getBirthInfo() {

            BirthInfo birthInfo = new BirthInfo();

            String data = facilityName.getText().trim();
            if (!data.equals("")) {
                 birthInfo.setFacilityName((String)data);
            }

            data = deliveryWeeks.getText().trim();
            if (! data.equals ("")) {
                birthInfo.setDeliveryWeeks((String)data);
            }  

            data = deliveryMethod.getText().trim();
            if (! data.equals("")) {
                birthInfo.setDeliveryMethod((String)data);
            }

            data = bodyWeight.getText().trim();
            if (! data.equals("")) {
                birthInfo.setBodyWeight((String)data);
            }

            data = bodyHeight.getText().trim();
            if (! data.equals("")) {
                 birthInfo.setBodyHeight((String)data);
            } 

            data = chestCircumference.getText();
            if (! data.equals ("")) {
                birthInfo.setChestCircumference((String)data);
            }

            data = headCircumference.getText().trim();
            if (! data.equals ("")) {
                birthInfo.setHeadCircumference ((String) data);
            }

            data = memo.getText().trim();
            if (! data.equals ("")) {
                birthInfo.setMemo((String)data);
            }

            return birthInfo.isValidModel() ? birthInfo : null;
        }
        
        private final Vaccination[] getVaccination() {

            Vaccination[] vaccination = null;
            ArrayList list = null;
            Vaccination item = null;

            int rowCount = vaccinationTable.getRowCount();
            String data1 = null;
            String data2 = null;

            for (int i = 0; i < rowCount ; i++) {

                data1 = (String)vaccinationTable.getValueAt(i, 0);     // vaccination
                data2 = (String)vaccinationTable.getValueAt(i, 1);     // injection

                if ( (!data1.equals("")) && (!data2.equals ("")) ) {

                    // create VaccinationItem object
                    item = new Vaccination();

                    item.setVaccine(data1);
                    item.setInjected(data2);

                    // age
                    data1 = (String)vaccinationTable.getValueAt(i, 2);
                    if (!data1.equals("")) {
                        item.setAge(data1);
                    }

                    // memo
                    data1 = (String)vaccinationTable.getValueAt(i, 3);
                    if (!data1.equals("")) {
                        item.setMemo (data1);
                    }

                    if (list == null) {
                       list = new ArrayList();
                    }

                    list.add(item);
                }
            }
            
			if (list != null && list.size() > 0) {
				int cnt = list.size();
				vaccination = new Vaccination[cnt];
				for (int i = 0; i < cnt; i++) {
					vaccination[i] = (Vaccination)list.get(i);
				}
			}            
            
            return vaccination;
        }        
        
        private JPanel createBirthInfoPanel() {
            
            FocusListener on = new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    JTextField tf = (JTextField)event.getSource();
                    tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                }
                public void focusLosted(FocusEvent event) {
                    JTextField tf = (JTextField)event.getSource();
                    tf.getInputContext().setCharacterSubsets(null);
                }
            };
            
            FocusListener off = new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    JTextField tf = (JTextField)event.getSource();
                    tf.getInputContext().setCharacterSubsets(null);
                }
                public void focusLosted(FocusEvent event) {
                    JTextField tf = (JTextField)event.getSource();
                    tf.getInputContext().setCharacterSubsets(null);
                }
            };
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 5, 5);
            JPanel p = new JPanel(flowLayout);
            JLabel label = new JLabel("出生施設名： ");
            p.add(label);
            facilityName = new JTextField(10);
            facilityName.addFocusListener(on);
            p.add(facilityName);
            panel.add(p);

            p = new JPanel(flowLayout);
            label = new JLabel("分娩週数　： ");
            p.add(label);
            deliveryWeeks = new JTextField(10);
            deliveryWeeks.addFocusListener(off);
            p.add(deliveryWeeks);
            panel.add(p);     

            p = new JPanel(flowLayout);
            label = new JLabel("分娩方法　： ");
            p.add(label);
            deliveryMethod = new JTextField(10);
            deliveryMethod.addFocusListener(on);
            p.add(deliveryMethod);
            panel.add(p);      

            p = new JPanel(flowLayout);
            label = new JLabel ("出生時体重： ");
            p.add (label);
            bodyWeight = new JTextField(10);
            bodyWeight.addFocusListener(off);
            p.add (bodyWeight);
            label = new JLabel("  g");
            p.add(label);
            panel.add(p);     

            p = new JPanel(flowLayout);
            label = new JLabel ("出生時身長： ");
            p.add(label);
            bodyHeight = new JTextField(10);
            bodyHeight.addFocusListener(off);
            p.add(bodyHeight);
            label = new JLabel("  cm");
            p.add(label);
            panel.add(p);

            p = new JPanel(flowLayout);
            label = new JLabel("出生時胸囲： ");
            p.add(label);
            chestCircumference = new JTextField(10);
            chestCircumference.addFocusListener(off);
            p.add(chestCircumference);
            label = new JLabel("  cm");
            p.add(label);
            panel.add(p);

            p = new JPanel(flowLayout);
            label = new JLabel("出生時頭囲： ");
            p.add (label);
            headCircumference = new JTextField(10);
            headCircumference.addFocusListener(off);
            p.add (headCircumference);
            label = new JLabel("  cm");
            p.add(label);
            panel.add(p);

            p = new JPanel(flowLayout);
            label = new JLabel("出生時メモ： ");
            p.add(label);      
            memo = new JTextField(20);
            memo.addFocusListener(on);
            p.add(memo);
            panel.add(p);
            
            DocumentListener dl = new DocumentListener() {
              
                public void changedUpdate(DocumentEvent evt) {
                }

                public void insertUpdate(DocumentEvent evt) {
                    checkButtons();
                }

                public void removeUpdate(DocumentEvent evt) {
                    checkButtons();
                }
            };
            facilityName.getDocument().addDocumentListener(dl);
            deliveryWeeks.getDocument().addDocumentListener(dl);
            deliveryMethod.getDocument().addDocumentListener(dl);
            bodyWeight.getDocument().addDocumentListener(dl);
            bodyHeight.getDocument().addDocumentListener(dl);
            chestCircumference.getDocument().addDocumentListener(dl);
            headCircumference.getDocument().addDocumentListener(dl);
            memo.getDocument().addDocumentListener(dl);
            
            panel.setBorder(BorderFactory.createTitledBorder("出生時情報"));
            return panel;
        }
        
        private void checkButtons() {

            boolean newOkState = ( (! facilityName.getText().trim().equals("")) || 
                                   (! deliveryWeeks.getText().trim().equals("")) ||
                                   (! deliveryMethod.getText().trim().equals("")) ||
                                   (! bodyWeight.getText().trim().equals("")) ||
                                   (! bodyHeight.getText().trim().equals("")) ||
                                   (! chestCircumference.getText().trim().equals("")) ||
                                   (! headCircumference.getText().trim().equals("")) ||
                                   (! memo.getText().trim().equals("")) )
                                 ?
                                 true : false;
              
            if (newOkState != okState) {
                okState = newOkState;
                setValidModel(okState);
            }
        }        
        
        private JPanel createVaccinationPanel() {

            JPanel panel = new JPanel();
            panel.setLayout (new BorderLayout(1,1));
        
            vaccinationModel = new AbstractTableModel() {

                Object[][] data = {
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""},
                    { "","","",""}
                };

                String[] columnNames = ClientContext.getStringArray("vaccinationColumnNames");

                public int getRowCount() {
                    return data.length;      // 行数
                }

                public int getColumnCount() {
                    return data[0].length;
                }

                public Object getValueAt(int row, int col) {
                    return data [row][col];
                }

                public String getColumnName(int col) {
                    return columnNames [col];
                }

                public Class getColumnClass(int col) {
                    return data[0][col].getClass ();
                }

                public boolean isCellEditable(int row, int col) {
                    return true;
                }

                public void setValueAt(Object value, int row, int col) {
                    if (value == null || ((String)value).trim().equals("")) {
                        return;
                    }
                    data[row][col] = value;
                    fireTableCellUpdated (row, col);
                                        
                    String val1 = (String)data[0][0];
                    String val2 = (String)data[0][1];
                    
                    boolean newOkState = ( (! val1.equals("")) && (! val2.equals("")) )
                                       ? true : false;
                    if (newOkState != okState) {
                        okState = newOkState;
                        setValidModel(okState);
                    }   
                }                  
            };
            vaccinationTable = new JTable (vaccinationModel);
            vaccinationTable.setSurrendersFocusOnKeystroke(true);
          
          // Editor for IME-ON/OFFfields
          IMECellEditor imeOn = new IMECellEditor (new JTextField(), 1, true);
          IMECellEditor imeOff = new IMECellEditor (new JTextField(), 1, false);
          
          // ワクチン column
          TableColumn column = vaccinationTable.getColumnModel().getColumn(0);
          column.setCellEditor(imeOn);
          
          // 実施状態
          column = vaccinationTable.getColumnModel().getColumn(1);
          column.setCellEditor (imeOn);
          
          // 実施年齢
          column = vaccinationTable.getColumnModel().getColumn(2);
          column.setCellEditor (imeOff);
          
          // メモ
          column = vaccinationTable.getColumnModel().getColumn(3);
          column.setCellEditor (imeOn);
            
            panel.add(vaccinationTable.getTableHeader(), BorderLayout.NORTH);
            panel.add(vaccinationTable, BorderLayout.CENTER);
            panel.setBorder(BorderFactory.createTitledBorder("予防接種情報"));
            return panel;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    class PastHistoryPanel extends JPanel {
        
        private JTextArea pastHistory;
        
        public PastHistoryPanel() {
            super(new BorderLayout());
            pastHistory = new JTextArea();
            pastHistory.setLineWrap(true);
            pastHistory.setMargin(new Insets(5,5,4,4));
            
            pastHistory.getDocument().addDocumentListener(new DocumentListener() {
                
                public void changedUpdate(DocumentEvent evt) {
                }

                public void insertUpdate(DocumentEvent evt) {
                    checkButtons();
                }

                public void removeUpdate(DocumentEvent evt) {
                    checkButtons();
                }
            });
            
            FocusListener fl = new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    JTextArea ta = (JTextArea)event.getSource();
                    ta.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                }
                public void focusLosted(FocusEvent event) {
                    JTextArea ta = (JTextArea)event.getSource();
                    ta.getInputContext().setCharacterSubsets(null);
                }
            };
            pastHistory.addFocusListener(fl);
            
            JScrollPane scroller = new JScrollPane(pastHistory);
            scroller.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroller.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
            add(scroller);
            setBorder(BorderFactory.createTitledBorder("既往歴"));
            setPreferredSize(defaultSize);
        }
        
        private void checkButtons() {
            boolean newOkState = pastHistory.getDocument().getLength() > 0 ? true : false;
            if (newOkState != okState) {
                okState = newOkState;
                setValidModel(okState);
            }
        }
        
        protected String getValue() {
            String data = pastHistory.getText().trim();
            return (!data.equals("")) ? data : null;
        }
        
        protected void setValue(String value) {
            if (value != null) {
                pastHistory.setText(value);
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    class ChiefComplaintsPanel extends JPanel {
        
        private JTextArea chiefComplaints;
        
        public ChiefComplaintsPanel() {
            super(new BorderLayout());
            chiefComplaints = new JTextArea();
            chiefComplaints.setLineWrap(true);
            chiefComplaints.setMargin(new Insets(5,5,4,4));
            
            chiefComplaints.getDocument().addDocumentListener(new DocumentListener() {
                
                public void changedUpdate(DocumentEvent evt) {
                }

                public void insertUpdate(DocumentEvent evt) {
                    checkButtons();
                }

                public void removeUpdate(DocumentEvent evt) {
                    checkButtons();
                }
            });
            
            FocusListener fl = new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    JTextArea ta = (JTextArea)event.getSource();
                    ta.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                }
                public void focusLosted(FocusEvent event) {
                    JTextArea ta = (JTextArea)event.getSource();
                    ta.getInputContext().setCharacterSubsets(null);
                }
            };
            chiefComplaints.addFocusListener(fl);
            
            JScrollPane scroller = new JScrollPane(chiefComplaints);
            scroller.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroller.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
            add(scroller);
            setBorder(BorderFactory.createTitledBorder("主 訴"));
            setPreferredSize(defaultSize);
        }
        
        private void checkButtons() {
            boolean newOkState = chiefComplaints.getDocument().getLength() > 0 ? true : false;
            if (newOkState != okState) {
                okState = newOkState;
                setValidModel(okState);
            }
        }
        
        protected String getValue() {
            String data = chiefComplaints.getText().trim();
            return (!data.equals("")) ? data : null;
        }
        
        protected void setValue(String value) {
            if (value != null) {
                chiefComplaints.setText(value);
            }
        }
    }  
    
    ////////////////////////////////////////////////////////////////////////////
    
    class PresentIllnessPanel extends JPanel {
        
        private JTextArea presentIllness;
        
        public PresentIllnessPanel() {
            super(new BorderLayout());
            presentIllness = new JTextArea();
            presentIllness.setLineWrap(true);
            presentIllness.setMargin(new Insets(5,5,4,4));
            
            presentIllness.getDocument().addDocumentListener(new DocumentListener() {
                
                public void changedUpdate(DocumentEvent evt) {
                }

                public void insertUpdate(DocumentEvent evt) {
                    checkButtons();
                }

                public void removeUpdate(DocumentEvent evt) {
                    checkButtons();
                }
            });
            
            FocusListener fl = new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    JTextArea ta = (JTextArea)event.getSource();
                    ta.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                }
                public void focusLosted(FocusEvent event) {
                    JTextArea ta = (JTextArea)event.getSource();
                    ta.getInputContext().setCharacterSubsets(null);
                }
            };
            presentIllness.addFocusListener(fl);
            
            JScrollPane scroller = new JScrollPane(presentIllness);
            scroller.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroller.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
            add(scroller);
            setBorder(BorderFactory.createTitledBorder("現病歴"));
            setPreferredSize(defaultSize);
        }
        
        private void checkButtons() {
            boolean newOkState = presentIllness.getDocument().getLength() > 0 ? true : false;
            if (newOkState != okState) {
                okState = newOkState;
                setValidModel(okState);
            }
        }
        
        protected String getValue() {
            String data = presentIllness.getText().trim();
            return (!data.equals("")) ? data : null;
        }
        
        protected void setValue(String value) {
            if (value != null) {
                presentIllness.setText(value);
            }
        }
    }
}