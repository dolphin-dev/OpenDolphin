/*
 * SaveDialog.java
 *
 * Created on 2001/10/24, 16:09
 */
package open.dolphin.client;

import javax.swing.*;

import open.dolphin.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputSubset;
import java.util.*;

/**
 * Dialog to set AccessRigth etc. when save dicument.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SaveDialog extends JDialog {
    
    private static final String[] PRINT_COUNT = {     
        "0", "1",  "2",  "3",  "4", "5"
    };
    private JLabel imageLabel;
    private JButton detailButton;
    private JCheckBox patientCheck;
    private JCheckBox clinicCheck;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField titleField;
    private JLabel sendMmlLabel;
    private JComboBox printCombo;
    //private JComboBox departmentCombo;
    private JLabel departmentLabel;
    private JPanel contentPanel;
    private JPanel topPanel;
    private JPanel buttonPanel;
    private Frame parent;
    private AccesRightTableModel accessModel;
    private StatusPanel statusPanel;
    private javax.swing.Timer timer;
    
    // 戻り値の
    private SaveParams value;
    
    /** Creates new OpenKarteDialog  */
    public SaveDialog(Frame parent) {
        
        super(parent, "Dolphin: ドキュメント保存", true);
        this.parent = parent;
        
        contentPanel = createComponent();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 11, 12, 11));
        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getRootPane().setDefaultButton(okButton);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                value = null;
            }
        });
        this.addComponentListener(new ComponentListener() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                JDialog d = (JDialog)e.getSource();
                System.out.println(d.getWidth() + "," + d.getHeight());
            }
            
            public void componentMoved(java.awt.event.ComponentEvent e) {
            }
            
            public void componentShown(java.awt.event.ComponentEvent e) {
            }
            
            public void componentHidden(java.awt.event.ComponentEvent e) {
            }
            
        });
        centerFrame();
    }
        
    public SaveParams getValue() {
        return value;
    }
    
    public void setValue(SaveParams params) {
        
        // Title 表示
        String val = params.getTitle();
        if (val != null) {
            titleField.setText(val);
        }
        
        // 診療科選択
        val = params.getDepartment();
        if (val != null) {
            //departmentCombo.setSelectedItem(val);
            departmentLabel.setText(val);
        }
        
        // 印刷部数
        int count = params.getPrintCount();
        if (count != -1) {
            printCombo.setSelectedItem(String.valueOf(count));
        
        } else {
            printCombo.setEnabled(false);
        }
        
        // アクセス権を設定する
        if (params.getSendMML()) {
            boolean permit = params.isAllowPatientRef();
            patientCheck.setSelected(permit);
            permit = params.isAllowClinicRef();
            clinicCheck.setSelected(permit);
        
        } else {
            // MML 送信をしないとき
            detailButton.setEnabled(false);
            patientCheck.setEnabled(false);
            clinicCheck.setEnabled(false);
            //sendMmlLabel.setText("センター送信は行いません");
        }
    }
    
    public void setTitle(String val) {
        titleField.setText(val);
    }
    
    protected void centerFrame() {
        pack();
        Dimension size = parent.getSize();
        Point loc = parent.getLocation();
        int x = loc.x + (size.width - this.getWidth()) / 2;
        int y = loc.y + (size.height - this.getHeight() ) / 3;
        this.setLocation(x, y);
    }       
    
    private JPanel createComponent() {
     
        JPanel retPanel = new JPanel();
        //retPanel.setLayout(new BoxLayout(retPanel, BoxLayout.Y_AXIS));
        retPanel.setLayout(new BorderLayout(0, 11));
        
        // topPanel
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        // Title
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(new JLabel("タイトル:"));
        p.add(Box.createRigidArea(new Dimension(11, 0)));
        titleField = new JTextField("NOP");
        titleField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
               titleField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
            public void focusLosted(FocusEvent event) {
               titleField.getInputContext().setCharacterSubsets(null);
            }
        });
        p.add(titleField);
        topPanel.add(p);
        
        topPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        
        // Department & Print
        /*JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.add(new JLabel("診療科:"));
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        Object[] depts = Project.getDepartment();
        departmentCombo = new JComboBox(depts);
        p1.add(departmentCombo);*/
        
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.add(new JLabel("診療科:"));
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        departmentLabel = new JLabel();
        p1.add(departmentLabel);
        
        //p1.add(Box.createHorizontalStrut(11));
        p1.add(Box.createHorizontalGlue());
        
        // Print
        //JPanel p2 = new JPanel();
        //p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p1.add(new JLabel("印刷部数:"));
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        printCombo = new JComboBox(PRINT_COUNT);
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        // Junzo SATO
        printCombo.setSelectedIndex(1);
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        p1.add(printCombo);
        
        // Department & Print
        //p = new JPanel();
        //p.setLayout(new BorderLayout());
        //p.add(p1, BorderLayout.WEST);
        //p.add(p2, BorderLayout.EAST);
        topPanel.add(p1);
        
        topPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        
        // AccessRight
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        patientCheck = new JCheckBox("患者に参照を許可する");
        //patientCheck.setSelected(false);
        p.add(patientCheck);
        clinicCheck = new JCheckBox("診療歴のある病院に参照を許可する");
        //clinicCheck.setSelected(false);
        p.add(clinicCheck);
        p.add(Box.createVerticalStrut(7));
        detailButton = new JButton("詳細設定...");
        detailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detailButton.setEnabled(false);
                showDetailRights();
            }
        });
        JPanel pb = new JPanel();
        pb.setLayout(new FlowLayout(SwingConstants.RIGHT));
        pb.add(detailButton);
        p.add(pb);
        p.setBorder(BorderFactory.createEmptyBorder(3,3,2,2));
        //p.setBorder(BorderFactory.createTitledBorder("アクセス権設定"));
        JPanel p3 = new JPanel(new BorderLayout(11, 0));
        //JButton detailSetting = new JButton("詳細設定",DolphinContext.createImageIcon("fish.jpg"));
        //detailSetting.setVerticalTextPosition(SwingConstants.BOTTOM);
        //detailSetting.setHorizontalTextPosition(SwingConstants.CENTER);
        //JPanel pp = new JPanel(new BorderLayout());
        //pp.add(detailSetting);
        //pp.setBorder(BorderFactory.createEmptyBorder(7,7,6,6));
        imageLabel = new JLabel(ClientContext.getImageIcon("fish.jpg"));
        p3.add(imageLabel, BorderLayout.CENTER);
        p3.add(p, BorderLayout.EAST);        
        p3.setBorder(BorderFactory.createTitledBorder("アクセス権設定"));
        //p3.setBorder(BorderFactory.createEmptyBorder(10,10,9,9));
        
        topPanel.add(p3);
        
        //topPanel.add(Box.createRigidArea(new Dimension(0, 17)));
        
        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        sendMmlLabel = new JLabel();
        buttonPanel.add(Box.createHorizontalStrut(7));
        buttonPanel.add(sendMmlLabel);
        buttonPanel.add(Box.createHorizontalGlue());
        
        // OK button
        String buttonText =  (String)UIManager.get("OptionPane.okButtonText");
        okButton = new JButton(buttonText);
        okButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                doOk();
            }
        });
        okButton.setEnabled(true);
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        
        // Cancel Button
        buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = new JButton(buttonText);
        cancelButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                doCancel();
            }
        });
        buttonPanel.add(cancelButton);        

        
        retPanel.add(topPanel, BorderLayout.NORTH);
        //retPanel.add(createTable());
        retPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return retPanel;
    }
            
    private void doOk() {
        
        // Return value
        value = new SaveParams();
        
        // Document titile
        String val = titleField.getText().trim();
        if (! val.equals("")) {
            value.setTitle(val);
        }
        
        // Department
        //val = (String)departmentCombo.getSelectedItem();
        val = departmentLabel.getText();
        value.setDepartment(val);
        
        // Print count
        int count = Integer.parseInt((String)printCombo.getSelectedItem());
        value.setPrintCount(count);
        
        // Access right for patient
        boolean b = patientCheck.isSelected();
        value.setAllowPatientRef(b);
        
        // Access right for clinics
        b = clinicCheck.isSelected();
        value.setAllowClinicRef(b);
        
        // Access right for facility
        if (accessModel != null) {
            value.setFacilityAccessList(accessModel.getAccessRights());
        }
        
        setVisible(false);
    }
        
    private void doCancel() {
        value = null;
        setVisible(false);
    }
    
    private void showDetailRights() {
        
        JPanel memberPanel = createAccessTable();
        contentPanel.removeAll();
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(memberPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.setPreferredSize(new Dimension(500,400));
        //contentPanel.setMinimumSize(new Dimension(500,400));
        contentPanel.revalidate();
        contentPanel.repaint();
        this.setSize(500, 400); 
        
        accessModel.getMember();
    }
    
    private JPanel createAccessTable() {
        
        accessModel = new AccesRightTableModel(new String[]{"施設名","住所","電話","許可"}, 10);
            
        JTable table = new JTable(accessModel);
        JScrollPane scroller = new JScrollPane(table);
        
        statusPanel = new StatusPanel();
        
        JPanel panel = new JPanel(new BorderLayout(0, 7));
        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(scroller, BorderLayout.CENTER);
        
        return panel;
    }
    
    protected class AccesRightTableModel extends ObjectTableModel {
        
        public AccesRightTableModel(String[] columnNames, int numRows) {
            super(columnNames, numRows);
        }
        
        public boolean isCellEditable(int row, int col) {
            return col == 3 ? true : false;
        }

        public Class getColumnClass(int col) {
            return col == 3 ? java.lang.Boolean.class : java.lang.String.class;
        }
        
        public Object getValueAt(int row, int col) {
            
            FacilityProfileEntry entry = (FacilityProfileEntry)this.getObject(row);
            if (entry == null) {
                return null;
            }
            
            switch (col) {
                case 0:
                    return entry.getFacilityName();
                    
                case 1:
                    return entry.getRegisteredAddress();
                    
                case 2:
                    return entry.getTelephoneNumber();
                    
                case 3:
                    return new Boolean(entry.getAccessRight());
            }
            
            return null;
        }
            
        public void setValueAt(Object o, int row, int col) {
            
            FacilityProfileEntry entry = (FacilityProfileEntry)this.getObject(row);
            if (entry == null) {
                return;
            }
            
            if (o != null) {
                boolean b = ((Boolean)o).booleanValue();
                entry.setAccessRight(b);
            }
            
        }
        
        public void getMember() {
                        
            final MemberSearchService service = (MemberSearchService)ClientContext.getPlugin("service.memberSearch");
            if (service == null) {
                //assert false : "MemberSearchService is null";
                System.out.println("MemberSearchService is null");
                return;
            }
            
            timer = new javax.swing.Timer(200, new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    
                    statusPanel.setMessage(service.getMessage());
                    
                    if (service.done()) {
                        timer.stop();
                        accessModel.setObjectList(service.getMemberList());
                        statusPanel.stop();
                        service.stop();
                    }
                }
            });
            
            service.go();
            statusPanel.start();
            timer.start();
        }
        
        public ArrayList getAccessRights() {
            
            ArrayList results = null;
            
            /*int facilityCount = getObjectCount();
            
            for (int i = 0; i < facilityCount; i++) {
                
                FacilityProfileEntry entry = (FacilityProfileEntry)getObject(i);
                
                if (entry.getAccessRight()) {
                    
                    ScFacilityName fn = new ScFacilityName();
                    
                    fn.setFacilityCode("individual");
                    fn.setTableId("MML0035");
                    fn.setFacilityId(entry.getFacilityId());
                    fn.setFacilityIdType("JMARI");
                    fn.setFacilityName(entry.getFacilityName());
                    
                    if (results == null) {
                        results = new ArrayList();
                    }
                    
                    results.add(fn);
                }
            }*/
            
            return results;
        }
    }
}