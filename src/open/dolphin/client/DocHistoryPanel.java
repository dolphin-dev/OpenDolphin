/*
 * DocHistoryPanel.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import open.dolphin.infomodel.DocInfo;
import open.dolphin.plugin.IChartContext;
import open.dolphin.table.ObjectTableModel;
import open.dolphin.util.MMLDate;

/**
 * Document History UI class.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
//public final class DocHistoryPanel extends JPanel implements PropertyChangeListener  {
public final class DocHistoryPanel extends JPanel {
        
    public static final String SELECTED_DOCUMENT_PROP = "selectedDocument";
    
    // Property
    private DocInfo selectedDocument;
	private PropertyChangeSupport boundSupport;
    
	// GUI コンポーネント
	private JTextField countField;              // 件数フィールド
	private JComboBox contentCombo;             // コンテンツ選択コンボボックス
	private JComboBox extractionCombo;          // 抽出期間選択コンボボックス
	private JRadioButton modifyOn;				// 修正履歴表示 ON
	private JRadioButton modifyOff;				// 修正履歴表示 OFF
    private ObjectTableModel tableModel;
    private JTable table;						// 文書履歴テーブル
	private String[] contentDictionary;
	private String[] periodList;
	
    // 文書履歴検索パラメータ
    private String pid;							// 患者ID
    private String contentProp;					// 文書のタイプ
    private String periodProp;					// 抽出期間
    private boolean modifyDisplay;				// 修正履歴表示
    
	private IChartContext context;
    private StatusPanel statusPanel;
    
    /** * Creates new DocHistory */
    public DocHistoryPanel(ObjectTableModel tm) {
    	
		super(new BorderLayout());
        
        // 文書履歴テーブル
		this.tableModel = tm;
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		ListSelectionModel m = table.getSelectionModel();
		m.addListSelectionListener(new ListSelectionListener() {
            
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					int row = table.getSelectedRow();
					DocInfo docInfo = (DocInfo)tableModel.getObject(row);
					if (docInfo != null) {
						setSelectedDocument(docInfo);
					}
				}
			}
		});
		JScrollPane scroller = new JScrollPane(table, 
								   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroller, BorderLayout.CENTER);
		
		// 文書履歴フィルター
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalStrut(7));
        
		// コンテント（ドキュメント）タイプ選択コンボボックス
		contentDictionary = ClientContext.getStringArray("filter.combo.docFilterDic");
		Dimension comboDimension = new Dimension(80,20);
		p.add(new JLabel(ClientContext.getString("filter.label.docType")));
		p.add(Box.createHorizontalStrut(7));
		String[] contentList = ClientContext.getStringArray("filter.combo.docFilter");
		contentCombo = new JComboBox(contentList);
		contentCombo.setPreferredSize(comboDimension);
		contentCombo.setMaximumSize(comboDimension);
		contentCombo.setMinimumSize(comboDimension);
		contentCombo.addItemListener(new ItemListener() {
            
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int index = contentCombo.getSelectedIndex();
					contentProp = contentDictionary[index];
					get();
				}
			}
		});
		p.add(contentCombo);
        
		p.add(Box.createHorizontalStrut(11));
        
		// 抽出期間コンボボックス
		p.add(new JLabel(ClientContext.getString("filter.label.extPeriod")));
		p.add(Box.createHorizontalStrut(7));
		periodList = ClientContext.getStringArray("filter.combo.periodName");     
		extractionCombo = new JComboBox(periodList);
		extractionCombo.setPreferredSize(comboDimension);
		extractionCombo.setMaximumSize(comboDimension);
		extractionCombo.setMinimumSize(comboDimension);
		extractionCombo.addItemListener(new ItemListener() {
            
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int index = extractionCombo.getSelectedIndex();
					periodProp = getFilterDate(index);
					get();
				}
			}
		});
		p.add(extractionCombo);
        
		p.add(Box.createHorizontalStrut(11));
        
		// 修正履歴表示　ON/OFF
		p.add(new JLabel(ClientContext.getString("filter.label.modifyDisplay")));
		p.add(Box.createHorizontalStrut(7));
		modifyDisplay = ClientContext.getPreferences().getBoolean("filter.historyDisplay", false);
		modifyOn = new JRadioButton(ClientContext.getString("filter.radio.modifyOn"));
		modifyOff = new JRadioButton(ClientContext.getString("filter.radio.modifyOff"));
		ButtonGroup bg = new ButtonGroup();
		bg.add(modifyOn);
		bg.add(modifyOff);
		modifyOn.setSelected(modifyDisplay);
		modifyOff.setSelected(! modifyDisplay);
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean nHistory = modifyOn.isSelected();
				if (nHistory != modifyDisplay) {
					modifyDisplay = nHistory;
					ClientContext.getPreferences().putBoolean("filter.historyDisplay", modifyDisplay);
					get();
				}
			}
		};
		modifyOn.addActionListener(al);
		modifyOff.addActionListener(al);
		p.add(modifyOn);
		p.add(modifyOff);
        
		p.add(Box.createHorizontalGlue());
        
		// 件数フィールド
		p.add (new JLabel(ClientContext.getString("filter.label.count")));
		p.add(Box.createHorizontalStrut(7));        
		countField = new JTextField();
		Dimension countFieldDimension = new Dimension(40,20);
		countField.setPreferredSize(countFieldDimension);
		countField.setMaximumSize(countFieldDimension);
		countField.setMinimumSize(countFieldDimension);
		countField.setEditable(false);
		p.add(countField);
        
		p.add(Box.createHorizontalStrut(7));
        
		p.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 7));
		
		this.add(p, BorderLayout.SOUTH);
		
		// 文書履歴検索の初期値を設定する
		contentProp = contentDictionary[0];
		periodProp = getFilterDate(0);
		
		// 束縛サポートを生成しておく
		boundSupport = new PropertyChangeSupport(this);
    }
    
    public DocInfo getSelectedDocument() {
    	return selectedDocument;
    }
    
    public void setSelectedDocument(DocInfo newInfo) {
    	DocInfo oldInfo = selectedDocument;
		selectedDocument = newInfo;
		boundSupport.firePropertyChange(SELECTED_DOCUMENT_PROP, oldInfo, selectedDocument);
    }
    
	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		boundSupport.addPropertyChangeListener(prop, l);
	}
    
	public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
		boundSupport.removePropertyChangeListener(prop, l);
	} 
    
	public JTable getTable() {
		return table;
	}
	
	public void setColumnWidth(int[] width) {
		TableColumn column = null;
		int len = width.length;
		for (int i = 0; i < len; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(width[i]);
		}
	}  
        
	protected void fireBusy(boolean b) {
		boundSupport.firePropertyChange("busyProp", !b, b);
	}
    
	protected void fireCount() {
		int count = tableModel.getDataSize();
		countField.setText(String.valueOf(count));
	}	    
        
    public void setStatusPanel(StatusPanel sp) {
        this.statusPanel = sp;
    }    
    
    public void setChartContext(IChartContext context) {
        this.context = context;
    }
    
    public void setPatientId(String pid) {
        this.pid = pid;
    }
    
    public void setContentType(String val) {
        this.contentProp = val;
    }
    
    public void setExtractionPeriod(String val) {
        this.periodProp = val;
    }
    
    public void setHistoryDisplay(boolean b) {
    	modifyDisplay = b;
    }
    
        
    /**
     * GUI が実体化されたあとに検索を実行する
     */
    public void get() {
        
        if ( (pid != null) && (contentProp != null) && (periodProp != null) ) {
                        
            Runnable r = new Runnable() {
                
                public void run() {
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {                            
                            if (statusPanel != null) {
                                statusPanel.start("ドキュメントリストを取得しています...");
                            }
                        }
                    });
                    
                    ArrayList results = context.getDocumentHistory(pid, contentProp, periodProp, modifyDisplay);
                    tableModel.setObjectList(results);
                    fireCount();
                                    
                    SwingUtilities.invokeLater(new Runnable() {                        
                        public void run() {
                           if (statusPanel != null) {
                                statusPanel.stop("");
								if (tableModel.getDataSize() > 0) {
									table.getSelectionModel().addSelectionInterval(0, 0);
								} 
                            }
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();            
        } 
    }
    
	private String getFilterDate(int index) {

		GregorianCalendar today = new GregorianCalendar(); 

		switch(index) {

			case 0:
				today.add(GregorianCalendar.YEAR, -1);
				break;
			case 1:    
				today.add(GregorianCalendar.DATE, -30); 
				break;
			case 2:        
				today.add(GregorianCalendar.DATE, -60);
				break;
			case 3:        
				today.add(GregorianCalendar.DATE, -90);
				break;
			case 4:        
				today.add(GregorianCalendar.DATE, -180); 
				break; 
			case 5:        
				today.add(GregorianCalendar.YEAR, -2);
				break;     
			case 6:        
				today.add(GregorianCalendar.YEAR, -3);
				break;    
			case 7:        
				today.add(GregorianCalendar.YEAR, -5);
				break;                
		}
        
		return MMLDate.getDate(today);
	}      
}