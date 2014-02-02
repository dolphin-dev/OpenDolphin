/*
 * MasterMaintePanel.java
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
import java.util.*;
import java.util.prefs.*;
import java.awt.im.InputSubset;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import open.dolphin.dao.*;
import open.dolphin.infomodel.DiseaseEntry;
import open.dolphin.infomodel.MedicineEntry;
import open.dolphin.infomodel.ToolMaterialEntry;
import open.dolphin.infomodel.TreatmentEntry;
import open.dolphin.plugin.*;
import open.dolphin.table.*;

/**
 *
 * @author  kazushi Minagawa, Digital Globe, Inc.
 */
public class MasterMainteService extends AbstractFramePlugin {
    
    //protected Logger logger = ClientContext.getLogger();
    protected Preferences prefs = ClientContext.getPreferences();
    
    protected final Color[] masterColors = ClientContext.getColorArray("masterSearch.masterColors");
    
    protected final String[] masterNames = ClientContext.getStringArray("masterSearch.masterNames");
    protected final String[] masterTabNames = ClientContext.getStringArray("masterSearch.masterTabNames");
    protected final String[] diseaseColumns = ClientContext.getStringArray("masterSearch.disease.columnNames");
    protected final String[] medicineColumns = ClientContext.getStringArray("masterSearch.medicine.columnNames");
    protected final String[] toolMaterialColumns = ClientContext.getStringArray("masterSearch.toolMaterial.columnNames");
    protected final String[] treatmentColumns = ClientContext.getStringArray("masterSearch.treatment.columnNames");
    
    protected final String[] matchButtonText = ClientContext.getStringArray("masterSearch.text.matchBbttons");
    protected final String closeButtonText = ClientContext.getString("masterSearch.text.closeButton");
    protected final String keywordBorderTitle = ClientContext.getString("masterSearch.text.keywordBorderTitle");
    protected final String countLabelText = ClientContext.getString("masterSearch.text.countLabel");
    
    protected final String medicineFlag = ClientContext.getString("masterSearch.medicine.medicineFlag");
    protected final String injectionFlag = ClientContext.getString("masterSearch.medicine.injectionFlag");
    protected final String[] costFlags = ClientContext.getStringArray("masterSearch.medicine.costFlags");
    
    protected final String[] toolMaterialCostFlags = ClientContext.getStringArray("masterSearch.toolMaterial.costFlags");
    
    protected final String[] treatmentCostFlags = ClientContext.getStringArray("masterSearch.treatment.costFlags");
    protected final String[] inOutFlags = ClientContext.getStringArray("masterSearch.treatment.inOutFlags");
    protected final String[] hospClinicFlags= ClientContext.getStringArray("masterSearch.treatment.hospitalClinicFlags");
    protected final String[] oldFlags = ClientContext.getStringArray("masterSearch.treatment.oldFlags");
    protected final String[] treatmentNames = ClientContext.getStringArray("masterSearch.treatment.names");
    protected final String[] treatmentCodes = ClientContext.getStringArray("masterSearch.treatment.codes");
    
    private final int DEFAULT_WIDTH     = 990;
    private final int DEFAULT_HEIGHT    = 660;
    private final int START_NUM_ROWS    = 20;
    
    protected final String underSearchMsg = ClientContext.getString("search.status.underSearchMsg");
    protected final String endSearchMsg = ClientContext.getString("search.status.endSearchMsg");
    
    /** Creates a new instance of MasterMaintePanel */
    public MasterMainteService() {
    }
    
    public void initComponent() {
        
        // Master　検索パネルを生成する
        DiseasePanel disease = new DiseasePanel(masterNames[0]);                      // "disease"
        MedicinePanel medicine = new MedicinePanel(masterNames[1]);                   // "medicine"
        InjectionPanel injection = new InjectionPanel(masterNames[2]);                // "medicine"
        ToolMaterialPanel toolMaterial = new ToolMaterialPanel(masterNames[3]);       // "tool_material"
        TreatmentPanel treatment = new TreatmentPanel(masterNames[4]);                // "treatment"
        
        // TabbedPane に追加する
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(masterTabNames[0], disease);                                // "病　名"
        tabbedPane.addTab(masterTabNames[1], medicine);                               // "内用・外用薬"
        tabbedPane.addTab(masterTabNames[2], injection);                              // "注射薬"
        tabbedPane.addTab(masterTabNames[3], toolMaterial);                           // "器　材"
        tabbedPane.addTab(masterTabNames[4], treatment);                              // "診　療"
        
        // Tab 切り替え処理を登録する
        tabbedPane.addChangeListener(new ChangeListener() {
            
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane.getSelectedIndex();
                prefs.putInt("masterSearch.selectedTab", index);
                MasterPanel mp = (MasterPanel)tabbedPane.getComponentAt(index);
                mp.enter();
            }
        });
        
        // 前回選択されていたタブを選択する
        int lastIndex = prefs.getInt("masterSearch.selectedTab", 1);
        tabbedPane.setSelectedIndex(lastIndex);  // Select MedicinePanel when start up
        
        final JPanel content = new JPanel(new BorderLayout());
        content.add(tabbedPane);
        centerFrame(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT), content);
    }

    /**
     * マスタ検索パネルの root クラス
     */
    protected class MasterPanel extends JPanel {
        
        protected final int KEYWORD_WIDTH  = 250;
        protected final int KEYWORD_HEIGHT = 21;
        protected JTextField keywordField;
        protected JButton searchButton;
        protected JRadioButton startsWith;
        protected JRadioButton contains;
        protected JTable table;
        protected JLabel countLabel;
        protected JLabel statusLabel;
        protected JProgressBar progressBar;
        protected JButton cancelButton;
        protected ObjectTableModel model;
        
        protected String master;
        protected String category;
        protected String sortBy;
        protected String order;
        
        public MasterPanel(final String master) {    
            
            this.master = master;
            
            this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
            
            // キーワードフィールドを生成する
            keywordField = new JTextField();
            Dimension d = new Dimension(KEYWORD_WIDTH, KEYWORD_HEIGHT);
            keywordField.setPreferredSize(d);
            keywordField.setMaximumSize(d);
            
            // リターンキーが押された時の処理を登録する
            keywordField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    searchButton.doClick();
                }
            });
            
            // フォーカスされた時 IME をオンにする
            keywordField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                   keywordField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                }
                public void focusLosted(FocusEvent event) {
                   keywordField.getInputContext().setCharacterSubsets(null);
                }
            });

            // ドキュメントリスナを登録する
            keywordField.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    boolean b = (! keywordField.getText().trim().equals("")) ? true : false;
                    searchButton.setEnabled(b);
                }

                public void removeUpdate(DocumentEvent e) {
                    boolean b = (! keywordField.getText().trim().equals("")) ? true : false;
                    searchButton.setEnabled(b);     
                }  

                public void changedUpdate(DocumentEvent e) {
                } 
            });

            // 方一致,部分一致 ボタンを生成する
            startsWith = new JRadioButton(matchButtonText[0]);                    // "前方一致"
            contains = new JRadioButton(matchButtonText[1]);                      // "部分一致"
            ButtonGroup bg = new ButtonGroup();
            bg.add(startsWith);
            bg.add(contains);
            boolean bStartsWith = prefs.getBoolean(master + "keywordSearch.startsWith", false);
            startsWith.setSelected(bStartsWith);
            contains.setSelected(! bStartsWith);
            
            ActionListener al = new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    boolean b = startsWith.isSelected();
                    prefs.putBoolean(master + "keywordSearch.startsWith", b);
                }
            };
            startsWith.addActionListener(al);
            contains.addActionListener(al);

            // 検索ボタン
            String searchButtonText = ClientContext.getString("search.button.text");
            String searchButtonImage = ClientContext.getString("search.button.image");
            searchButton = new JButton(searchButtonText, ClientContext.getImageIcon(searchButtonImage));
            searchButton.setEnabled(false);
            setMnemonic(searchButton);
            searchButton.addActionListener(new ActionListener() {

                // ボタンクリックでマスタ検索を行う
                public void actionPerformed(ActionEvent e) {

                    // キワード文字を取得する
                    String text = keywordField.getText().trim();
                    if (! text.equals("")) {
                        getByName(text);
                    }
                }
            });
            
            // 件数 Label
            countLabel = new JLabel();
            
            // ステータスパネル
            statusLabel = new JLabel();
            
            // Progress Bar
            progressBar = new JProgressBar(0, 100);
            progressBar.setBorderPainted(false);
            
            // Cancel button
            cancelButton = new JButton(closeButtonText);     // 閉じる(C)
            setMnemonic(cancelButton);
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });   
        }
        
        public void close() {
            stop();
        }
        
        public void enter() {
        }
        
        protected void getByName(final String text) {
            
            //final String val = StringTool.toKatakana(text, false);
            
            final SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");

            Runnable r = new Runnable() {
                public void run() {
                    fireBusy(true);
                    ArrayList results = dao.getByName(master, text, startsWith.isSelected(), category, sortBy, order);
                    model.setObjectList(results);
                    int count = model.getObjectCount();
                    fireCount(count);
                    fireBusy(false);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        
        protected void fireBusy(final boolean b) {
            
            SwingUtilities.invokeLater(new Runnable() {
                
                public void run() {
                    
                    if (b) {
                        countLabel.setText("");
                        statusLabel.setText(underSearchMsg);
                        cancelButton.setEnabled(false);
                        progressBar.setIndeterminate(true);
                        
                    } else {
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(0);
                        cancelButton.setEnabled(true);
                        statusLabel.setText(endSearchMsg);
                    }
                }
            });
        }
        
        protected void fireCount(final int count) {
            
            SwingUtilities.invokeLater(new Runnable() {
                
                public void run() {
                
                    String str = String.valueOf(count);
                    countLabel.setText(str);
                }
            });
        }
    }
    
    /**
     * 病名検索パネル
     */
    protected final class DiseasePanel extends MasterPanel {
               
        public DiseasePanel(String master) {
            
            super(master);
                        
            // Table Model を生成する
            model = new ObjectTableModel(diseaseColumns, START_NUM_ROWS) {
                
                public Class getColumnClass(int col) {
                    return DiseaseEntry.class;
                }
            };
            
            // Table を生成する
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            
            TableColumn column = null;
            int[] width = new int[]{50, 200, 200, 40, 50};
            int len = width.length;
            for (int i = 0; i < len; i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(width[i]);
            }
     
            DiseaseMasterRenderer dr = new DiseaseMasterRenderer();
            dr.setBeforStartColor(masterColors[0]);
            dr.setInUseColor(masterColors[1]);
            dr.setAfterEndColor(masterColors[2]);
            table.setDefaultRenderer(DiseaseEntry.class, dr);
            
            // Layout
            // Keyword
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            top.add(new JLabel(masterTabNames[0] + ":"));
            top.add(Box.createHorizontalStrut(7));
            top.add(keywordField);
            top.add(Box.createHorizontalStrut(11));
            top.add(startsWith);
            top.add(Box.createHorizontalStrut(5));
            top.add(contains);
            top.add(Box.createHorizontalStrut(7));
            top.add(searchButton);
            top.add(Box.createHorizontalGlue());
            top.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));
            
            // Table
            JScrollPane scroller = new JScrollPane(table, 
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Command
            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            bottom.add(new JLabel(countLabelText));
            bottom.add(Box.createHorizontalStrut(7));
            bottom.add(countLabel);
            bottom.add(Box.createHorizontalStrut(17));
            
            bottom.add(statusLabel);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(progressBar);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(Box.createHorizontalGlue());
            bottom.add(cancelButton);
            
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(top);
            this.add(Box.createVerticalStrut(5));
            this.add(scroller);
            this.add(Box.createVerticalStrut(11));
            this.add(bottom);
        }
    }
    
    /**
     * 病名マスタ Table のレンダラー
     */
    protected final class DiseaseMasterRenderer extends MasterRenderer {
        
        private final int CODE_COLUMN       = 0;
        private final int NAME_COLUMN       = 1;
        private final int KANA_COLUMN       = 2;
        private final int ICD10_COLUMN      = 3;
        private final int DISUSES_COLUMN    = 4;
                
        public DiseaseMasterRenderer() {
        }
        
        public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean isSelected,
                                boolean isFocused,
                                int row, int col) {
            Component c = super.getTableCellRendererComponent(
                                             table, 
                                             value,
                                             isSelected,
                                             isFocused, 
                                             row, col);
            JLabel label = (JLabel)c;
            
            if (value != null && value instanceof DiseaseEntry) {
                
                DiseaseEntry entry = (DiseaseEntry)value;
               
                String disUseDate = entry.getDisUseDate();
                
                setColor(label, disUseDate);
                //label.setBackground(getRowColor(disUseDate));
                
                 switch(col) {

                    case CODE_COLUMN:
                        label.setText(entry.getCode());
                        break;

                    case NAME_COLUMN:
                        label.setText(entry.getName());
                        break;

                    case KANA_COLUMN:
                        label.setText(entry.getKana());
                        break;                            

                    case ICD10_COLUMN:
                        label.setText(entry.getIcdTen());
                        break;

                    case DISUSES_COLUMN:
                        if (disUseDate.startsWith("9")) {
                            label.setText("");
                        } else {
                            label.setText(disUseDate);
                        }
                        break;    
                }
                                    
            } else {
                label.setBackground(Color.white);
                label.setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }    
    
    /**
     * 医薬品検索パネル
     */
    protected final class MedicinePanel extends MasterPanel {
        
        public MedicinePanel(String master) {
            
            super(master);
            category = medicineFlag;
        
            //String[] columnNames = new String[]{"コード", "名  称", "単位", "識別", "点数/金額", "廃止年月日"};
            
            model = new ObjectTableModel(medicineColumns, START_NUM_ROWS) {
                
                public Class getColumnClass(int col) {
                    return MedicineEntry.class;
                }
            };
            
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            
            TableColumn column = null;
            int[] width = new int[]{50, 200, 200, 40, 30, 50, 50, 50, 50};
            int len = width.length;
            for (int i = 0; i < len; i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(width[i]);
            }
     
            // レンダラーを設定する
            MedicineMasterRenderer mr = new MedicineMasterRenderer();
            mr.setBeforStartColor(masterColors[0]);
            mr.setInUseColor(masterColors[1]);
            mr.setAfterEndColor(masterColors[2]);
            mr.setCostFlag(costFlags);
            table.setDefaultRenderer(MedicineEntry.class, mr);

            // Layout
            // Keyword
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            top.add(new JLabel(masterTabNames[1] + ":"));
            top.add(Box.createHorizontalStrut(7));
            top.add(keywordField);
            top.add(Box.createHorizontalStrut(11));
            top.add(startsWith);
            top.add(Box.createHorizontalStrut(5));
            top.add(contains);
            top.add(Box.createHorizontalStrut(7));
            top.add(searchButton);
            top.add(Box.createHorizontalGlue());
            top.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));
            
            // Table
            JScrollPane scroller = new JScrollPane(table, 
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Command
            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            bottom.add(new JLabel(countLabelText));
            bottom.add(Box.createHorizontalStrut(7));
            bottom.add(countLabel);
            bottom.add(Box.createHorizontalStrut(17));
            
            bottom.add(statusLabel);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(progressBar);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(Box.createHorizontalGlue());
            bottom.add(cancelButton);
            
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(top);
            this.add(Box.createVerticalStrut(5));
            this.add(scroller);
            this.add(Box.createVerticalStrut(11));
            this.add(bottom);
        }        
    }
    
    /**
     * 医薬品マスタ Table のレンダラー
     */
    protected final class MedicineMasterRenderer extends MasterRenderer {
        
        private final int CODE_COLUMN       = 0;
        private final int NAME_COLUMN       = 1;
        private final int KANA_COLUMN       = 2;
        private final int UNIT_COLUMN       = 3;
        private final int COST_FLAG_COLUMN  = 4;
        private final int COST_COLUMN       = 5;
        private final int JNCD_COLUMN       = 6;
        private final int START_COLUMN      = 7;
        private final int END_COLUMN        = 8;
        
        private String[] costFlags;
        
        public MedicineMasterRenderer() {
        }
        
        public String[] getCostFlag() {
            return costFlags;
        }
        
        public void setCostFlag(String[] val) {
            costFlags = val;
        }
        
        public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean isSelected,
                                boolean isFocused,
                                int row, int col) {
            Component c = super.getTableCellRendererComponent(
                                             table, 
                                             value,
                                             isSelected,
                                             isFocused, 
                                             row, col);
            JLabel label = (JLabel)c;
            
            if (value != null && value instanceof MedicineEntry) {
                
                MedicineEntry entry = (MedicineEntry)value;
                
                String startDate = entry.getStartDate();
                String endDate = entry.getEndDate();
                
                setColor(label, startDate, endDate);
                //label.setBackground(getRowColor(startDate, endDate));
                
                switch(col) {
                        
                    case CODE_COLUMN:
                        label.setText(entry.getCode());
                        break;

                    case NAME_COLUMN:
                        label.setText(entry.getName());
                        break;

                    case KANA_COLUMN:
                        label.setText(entry.getKana());
                        break;                              

                    case UNIT_COLUMN:
                        label.setText(entry.getUnit());
                        break;

                    case COST_FLAG_COLUMN:
                        try {
                            int index = Integer.parseInt(entry.getCostFlag());
                            label.setText(costFlags[index]); 
                        }
                        catch (Exception e) {
                            label.setText("");
                        }
                        break;

                    case COST_COLUMN:
                        label.setText(entry.getCost());
                        break;

                    case JNCD_COLUMN:
                        label.setText(entry.getJNCD());
                        break;     

                    case START_COLUMN:
                        if (startDate.startsWith("0")) {
                            label.setText("");
                        } else {
                            label.setText(startDate);
                        }
                        break;
                        
                    case END_COLUMN:
                        if (endDate.startsWith("9")) {
                            label.setText("");
                        } else {
                            label.setText(endDate);
                        }
                        break;    
                }
                                    
            } else {
                label.setBackground(Color.white);
                label.setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
    
    /**
     * 注射薬検索パネル
     */
    protected final class InjectionPanel extends MasterPanel {
                
        public InjectionPanel(String master) {

            super(master);
            category = injectionFlag;
            
            model = new ObjectTableModel(medicineColumns, START_NUM_ROWS) {
                
                public Class getColumnClass(int col) {
                    return MedicineEntry.class;
                }       
            };
            
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            
            TableColumn column = null;
            int[] width = new int[]{50, 200, 200, 40, 30, 50, 50, 50, 50};
            int len = width.length;
            for (int i = 0; i < len; i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(width[i]);
            }
     
            // レンダラーを設定する
            MedicineMasterRenderer mr = new MedicineMasterRenderer();
            mr.setBeforStartColor(masterColors[0]);
            mr.setInUseColor(masterColors[1]);
            mr.setAfterEndColor(masterColors[2]);
            mr.setCostFlag(costFlags);
            table.setDefaultRenderer(MedicineEntry.class, mr);

            // Layout
            // Keyword
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            top.add(new JLabel(masterTabNames[1] + ":"));
            top.add(Box.createHorizontalStrut(7));
            top.add(keywordField);
            top.add(Box.createHorizontalStrut(11));
            top.add(startsWith);
            top.add(Box.createHorizontalStrut(5));
            top.add(contains);
            top.add(Box.createHorizontalStrut(7));
            top.add(searchButton);
            top.add(Box.createHorizontalGlue());
            top.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));
            
            // Table
            JScrollPane scroller = new JScrollPane(table, 
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Command
            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            bottom.add(new JLabel(countLabelText));
            bottom.add(Box.createHorizontalStrut(7));
            bottom.add(countLabel);
            bottom.add(Box.createHorizontalStrut(17));
            
            bottom.add(statusLabel);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(progressBar);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(Box.createHorizontalGlue());
            bottom.add(cancelButton);
            
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(top);
            this.add(Box.createVerticalStrut(5));
            this.add(scroller);
            this.add(Box.createVerticalStrut(11));
            this.add(bottom);            
        }                
    }
    
    /**
     * 器材検索パネル
     */
    protected final class ToolMaterialPanel extends MasterPanel {
                
        public ToolMaterialPanel(String master) {
            
            super(master);
            
            model = new ObjectTableModel(toolMaterialColumns, START_NUM_ROWS) {
                        
                public Class getColumnClass(int col) {
                    return ToolMaterialEntry.class;
                }
            };

            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            
            TableColumn column = null;
            int[] width = new int[]{50, 200, 200, 40, 30, 50, 50, 50};
            int len = width.length;
            for (int i = 0; i < len; i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(width[i]);
            } 
            
            // カスタムレンダラー登録          
            ToolMaterialMasterRenderer tmr = new ToolMaterialMasterRenderer();
            tmr.setBeforStartColor(masterColors[0]);
            tmr.setInUseColor(masterColors[1]);
            tmr.setAfterEndColor(masterColors[2]);
            tmr.setCostFlag(toolMaterialCostFlags);
            table.setDefaultRenderer(ToolMaterialEntry.class, tmr);

            // Layout
            // Keyword
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            top.add(new JLabel(masterTabNames[2] + ":"));
            top.add(Box.createHorizontalStrut(7));
            top.add(keywordField);
            top.add(Box.createHorizontalStrut(11));
            top.add(startsWith);
            top.add(Box.createHorizontalStrut(5));
            top.add(contains);
            top.add(Box.createHorizontalStrut(7));
            top.add(searchButton);
            top.add(Box.createHorizontalGlue());
            top.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));
            
            // Table
            JScrollPane scroller = new JScrollPane(table, 
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Command
            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            bottom.add(new JLabel(countLabelText));
            bottom.add(Box.createHorizontalStrut(7));
            bottom.add(countLabel);
            bottom.add(Box.createHorizontalStrut(17));
            
            bottom.add(statusLabel);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(progressBar);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(Box.createHorizontalGlue());
            bottom.add(cancelButton);
            
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(top);
            this.add(Box.createVerticalStrut(5));
            this.add(scroller);
            this.add(Box.createVerticalStrut(11));
            this.add(bottom);
        }
    }
    
    /**
     * 器材マスタ Table のレンダラー
     */
    protected final class ToolMaterialMasterRenderer extends MasterRenderer {
                
        private final int CODE_COLUMN       = 0;
        private final int NAME_COLUMN       = 1;
        private final int KANA_COLUMN       = 2;
        private final int UNIT_COLUMN       = 3;
        private final int COST_FLAG_COLUMN  = 4;
        private final int COST_COLUMN       = 5;
        private final int START_COLUMN      = 6;
        private final int END_COLUMN        = 7;

        private String[] costFlags;
        
        public ToolMaterialMasterRenderer() {
        }
        
        public String[] getCostFlag() {
            return costFlags;
        }
        
        public void setCostFlag(String[] val) {
            costFlags = val;
        }
                
        public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean isSelected,
                                boolean isFocused,
                                int row, int col) {
            Component c = super.getTableCellRendererComponent(
                                             table, 
                                             value,
                                             isSelected,
                                             isFocused, 
                                             row, col);
            JLabel label = (JLabel)c;
            
            if (value != null && value instanceof ToolMaterialEntry) {
                
                ToolMaterialEntry entry = (ToolMaterialEntry)value;
                
                String startDate = entry.getStartDate();
                String endDate = entry.getEndDate();
                
                //label.setBackground(getRowColor(startDate, endDate));
                setColor(label, startDate, endDate);
                
                String tmp = null;
                
                switch(col) {

                    case CODE_COLUMN:
                        label.setText(entry.getCode());
                        break;

                    case NAME_COLUMN:
                        label.setText(entry.getName());
                        break;

                    case KANA_COLUMN:
                        label.setText(entry.getKana());
                        break;    
                        
                    case UNIT_COLUMN:
                        label.setText(entry.getUnit());
                        break;

                    case COST_FLAG_COLUMN:
                        tmp = entry.getCostFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(costFlags[index]); 
                            }
                            catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText(""); 
                        }
                        break;

                    case COST_COLUMN:
                        label.setText(entry.getCost());
                        break;    

                    case START_COLUMN:
                        if (startDate.startsWith("0")) {
                            label.setText("");
                        } else {
                            label.setText(startDate);
                        }
                        break;
                        
                    case END_COLUMN:
                        if (endDate.startsWith("9")) {
                            label.setText("");
                        } else {
                            label.setText(endDate);
                        }
                        break;  
                }
                                    
            } else {
                label.setBackground(Color.white);
                label.setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }        
    
    /**
     * 診療行為検索パネル
     */
    protected final class TreatmentPanel extends MasterPanel {
                
        private JComboBox orderCombo;
        
        public TreatmentPanel(String master) {
            
            super(master);
                       
            // オーダクラス選択
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
            p.add(new JLabel("診療種別:"));
            p.add(Box.createHorizontalStrut(7));
            orderCombo = new JComboBox(treatmentNames);
            Dimension comboDimension = new Dimension(200,20);
            orderCombo.setPreferredSize(comboDimension);
            orderCombo.setMaximumSize(comboDimension);
            orderCombo.setMinimumSize(comboDimension);
            orderCombo.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        int index = orderCombo.getSelectedIndex();
                        String code = treatmentCodes[index];
                        if (! code.equals("")) {
                            if (code.startsWith("rad-location")) {
                                getRadLocation();
                            } else {
                                getByClaimClass(code);
                            }
                        }
                    }
                }
            });
            p.add(orderCombo);
            p.setBorder(BorderFactory.createTitledBorder("診療種別検索"));
            
            model = new ObjectTableModel(treatmentColumns, START_NUM_ROWS) {
                        
                public Class getColumnClass(int col) {
                    return TreatmentEntry.class;
                }
            };
            
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            
            TableColumn column = null;
            int[] width = new int[]{50, 180, 180, 30, 50, 30, 30, 30, 50, 50};
            int len = width.length;
            for (int i = 0; i < len; i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(width[i]);
            }
            
            
            TreatmentMasterRenderer tr = new TreatmentMasterRenderer();
            tr.setBeforStartColor(masterColors[0]);
            tr.setInUseColor(masterColors[1]);
            tr.setAfterEndColor(masterColors[2]);
            tr.setCostFlag(toolMaterialCostFlags);
            tr.setCostFlag(treatmentCostFlags);
            tr.setInOutFlag(inOutFlags);
            tr.setOldFlag(oldFlags);
            tr.setHospitalClinicFlag(hospClinicFlags);
            table.setDefaultRenderer(TreatmentEntry.class, tr);
            
            // Layout
            // Keyword
            JPanel key = new JPanel();
            key.setLayout(new BoxLayout(key, BoxLayout.X_AXIS));
            key.add(new JLabel(masterTabNames[4] + ":"));
            key.add(Box.createHorizontalStrut(7));
            key.add(keywordField);
            key.add(Box.createHorizontalStrut(11));
            key.add(startsWith);
            key.add(Box.createHorizontalStrut(5));
            key.add(contains);
            key.add(Box.createHorizontalStrut(7));
            key.add(searchButton);
            key.add(Box.createHorizontalGlue());
            key.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));
            
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            top.add(key);
            top.add(Box.createHorizontalGlue());
            top.add(p);
            
            // Table
            JScrollPane scroller = new JScrollPane(table, 
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Command
            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            bottom.add(new JLabel(countLabelText));
            bottom.add(Box.createHorizontalStrut(7));
            bottom.add(countLabel);
            bottom.add(Box.createHorizontalStrut(17));
            
            bottom.add(statusLabel);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(progressBar);
            bottom.add(Box.createHorizontalStrut(11));
            
            bottom.add(Box.createHorizontalGlue());
            bottom.add(cancelButton);
            
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(top);
            this.add(Box.createVerticalStrut(5));
            this.add(scroller);
            this.add(Box.createVerticalStrut(11));
            this.add(bottom);
        }
        
        private void getByClaimClass(final String code) {
                        
            final SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");

            Runnable r = new Runnable() {
                public void run() {
                    fireBusy(true);
                    ArrayList results = dao.getByClaimClass(master, code, sortBy, order);
                    model.setObjectList(results);
                    int count = model.getObjectCount();
                    fireCount(count);
                    fireBusy(false);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        
        private void getRadLocation() {
                        
            final SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");

            Runnable r = new Runnable() {
                public void run() {
                    fireBusy(true);
                    ArrayList results = dao.getRadLocation(master, sortBy, order);
                    model.setObjectList(results);
                    int count = model.getObjectCount();
                    fireCount(count);
                    fireBusy(false);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }        
    }
    
    /**
     * 診療行為マスタ Table のレンダラー
     */
    protected final class TreatmentMasterRenderer extends MasterRenderer {
        
        private final int CODE_COLUMN       = 0;
        private final int NAME_COLUMN       = 1;
        private final int KANA_COLUMN       = 2;
        private final int COST_FLAG_COLUMN  = 3;
        private final int COST_COLUMN       = 4;
        private final int INOUT_COLUMN      = 5;
        private final int OLD_COLUMN        = 6;
        private final int HOSP_CLINIC_COLUMN = 7;
        private final int START_COLUMN      = 8;
        private final int END_COLUMN      = 9;

        private String[] costFlags;
        private String[] inOutFlags;
        private String[] oldFlags;
        private String[] hospitalClinicFlags;
        
        public TreatmentMasterRenderer() {
        }
        
        public String[] getCostFlag() {
            return costFlags;
        }
        
        public void setCostFlag(String[] val) {
            costFlags = val;
        }
        
        public String[] getInOutFlag() {
            return inOutFlags;
        }
        
        public void setInOutFlag(String[] val) {
            inOutFlags = val;
        }
        
        public String[] getOldFlag() {
            return oldFlags;
        }
        
        public void setOldFlag(String[] val) {
            oldFlags = val;
        }
        
        public String[] getHospitalClinicFlag() {
            return hospitalClinicFlags;
        }
        
        public void setHospitalClinicFlag(String[] val) {
            hospitalClinicFlags = val;
        }        
        
        public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean isSelected,
                                boolean isFocused,
                                int row, int col) {
            Component c = super.getTableCellRendererComponent(
                                             table, 
                                             value,
                                             isSelected,
                                             isFocused, 
                                             row, col);
            JLabel label = (JLabel)c;
            
            if (value != null && value instanceof TreatmentEntry) {
                
                TreatmentEntry entry = (TreatmentEntry)value;
                
                String startDate = entry.getStartDate();
                String endDate = entry.getEndDate();
                
                setColor(label, startDate, endDate);
                
                //label.setBackground(getRowColor(startDate, endDate));
                
                String tmp = null;
                
                switch(col) {

                    case CODE_COLUMN:
                        label.setText(entry.getCode());
                        break;

                    case NAME_COLUMN:
                        label.setText(entry.getName());
                        break;

                    case KANA_COLUMN:
                        label.setText(entry.getKana());
                        break;    

                    case COST_FLAG_COLUMN:
                        tmp = entry.getCostFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(costFlags[index]); 
                            }
                            catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText(""); 
                        }
                        break;

                    case COST_COLUMN:
                        label.setText(entry.getCost());
                        break;    

                    case INOUT_COLUMN:
                        tmp = entry.getInOutFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(inOutFlags[index]); 
                            }
                            catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText(""); 
                        }
                        break;

                    case OLD_COLUMN:
                        tmp = entry.getOldFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(oldFlags[index]); 
                            }
                            catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText(""); 
                        }
                        break;

                    case HOSP_CLINIC_COLUMN:
                        tmp = entry.getHospitalClinicFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(hospitalClinicFlags[index]); 
                            }
                            catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText(""); 
                        }
                        break;

                    case START_COLUMN:
                        if (startDate.startsWith("0")) {
                            label.setText("");
                        } else {
                            label.setText(startDate);
                        }
                        break;
                        
                    case END_COLUMN:
                        if (endDate.startsWith("9")) {
                            label.setText("");
                        } else {
                            label.setText(endDate);
                        }
                        break;  
                }
                                    
            } else {
                label.setBackground(Color.white);
                label.setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }    
    
    private void setMnemonic(JButton button) {
        
        String text = button.getText();
        int index = text.indexOf("(");
        if ( index > -1 ) {
            char c = text.charAt(index + 1);
            button.setMnemonic(c);
        }
    }
}