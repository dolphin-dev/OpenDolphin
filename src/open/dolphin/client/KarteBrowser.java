/*
 * KarteBrowser.java
 * Copyright(C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2004 Digital Globe, Inc. All rights reserved.
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
import javax.swing.*;

import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.Karte;
import open.dolphin.table.ObjectTableModel;

import java.beans.*;

/**
 * Document Browser Class.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class KarteBrowser extends DefaultChartDocument implements PropertyChangeListener {
    
    private static final int DIVIDER_LOC  = 420;
    private static final int DIVIDER_SIZE = 8;
    
    private static final String[] columnNames;
    static {
        columnNames = ClientContext.getStringArray("docHistory.table.columnNames");
    }
    
    private static final int[] columnWidths = new int[]{200, 100, 80, 80, 50, 50, 50, 50, 90};
        
    private DocHistoryPanel docHistory;
    
    private KarteEditor karteEditor;
        
    private StateMgr stateMgr;
    
    //private AnimationLabel animationLabel;
    private StatusPanel statusPanel;
       
    /**  Creates new KarteBrowser */
    public KarteBrowser() {
    }
    
    public KarteEditor getEditor() {
        return karteEditor;
    }
    
    public void start() {
        
        stateMgr = new StateMgr();
        
        statusPanel =((ChartPlugin)context).getStatusPanel();
        
        ObjectTableModel model = new ObjectTableModel(columnNames, 10) {
        	
        	public Class getColumnClass(int col) {
        		
        		switch (col) {
        			
        			case 4:
        			case 5:
        			case 6:
        			case 7:
        				return Boolean.class;
        				
        			default:
        				return String.class;	
        		}
        	}
            
            public Object getValueAt(int row, int col) {
                
                DocInfo entry = (DocInfo)getObject(row);
                if (entry == null) {
                    return null;
                }
                
                Object ret = null;
                
                switch (col) {
                        
                    case 0:
                        // タイトル
                        ret = entry.getTitle();
                        break;
                        
                    case 1:
                        // 確定日 時間をオミット
                        String val = entry.getFirstConfirmDate();
                        int index = val.indexOf("T");
                        ret = index > -1 ? val.substring(0, index) : val;
                        break;
                        
                    case 2:
                        // 診療科
                        ret = entry.getClaimInfo().getDepartment();
                        break;    
                        
                    case 3:
                         // 保険
                        ret = entry.getClaimInfo().getInsuranceClass();
                        break;
                        
                    case 4:
                    	// 画像
                    	ret = new Boolean(entry.getHasImage());
                    	break;
                    	
					case 5:
						// RP
						ret = new Boolean(entry.getHasRp());
						break;
						
					case 6:
						// 処置
						ret = new Boolean(entry.getHasTreatment());
						break;
						
					case 7:
						// LaboTest
						ret = new Boolean(entry.getHasLaboTest());
						break;												                    	
                        
                    case 8:
                         // Creator name
                        ret = entry.getCreator().getName();
                        break;    
                }
                return ret;
            }
        };
        docHistory = new DocHistoryPanel(model);
        docHistory.setColumnWidth(columnWidths);
        docHistory.setStatusPanel(statusPanel);
        docHistory.setChartContext(context);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(docHistory,BorderLayout.CENTER);
       
        karteEditor = new KarteEditor();
        karteEditor.setChartContext(context);
        karteEditor.setEditable(false);
        karteEditor.start();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, karteEditor, bottomPanel);
        splitPane.setDividerSize(DIVIDER_SIZE);
        splitPane.setContinuousLayout(false);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(DIVIDER_LOC);
        
        this.setLayout(new BorderLayout());
        add(splitPane,BorderLayout.CENTER);
               
        // Connects 
        docHistory.addPropertyChangeListener(DocHistoryPanel.SELECTED_DOCUMENT_PROP, this);
        
        // Force to fetch the latest karte
        docHistory.setPatientId(context.getPatient().getId());
		//docHistory.get();
        
        enter();
    }
    
    public boolean copyStamp() {
        return (karteEditor != null) ?  karteEditor.copyStamp() : false;
    }
    
    public void getHistory() {
        docHistory.get();
    }
    
    public void propertyChange(PropertyChangeEvent e) {
    
        String prop = e.getPropertyName();
        
        if (prop.equals(DocHistoryPanel.SELECTED_DOCUMENT_PROP)) {
        	
			final DocInfo info = (DocInfo)e.getNewValue();
						
			Runnable r = new Runnable() {
				
				public void run() {
					
					SwingUtilities.invokeLater(new Runnable() {
						
						public void run() {
							statusPanel.start("検索しています...");
						}
					});
					
					final Karte model = context.getKarte(info.getDocId());
					model.setDocInfo(info);
					karteEditor.setNewModel(model);	
					
					SwingUtilities.invokeLater(new Runnable() {
	
						public void run() {
							stateMgr.enterHasKarteState();
							statusPanel.stop("");
						}
					});
				}
			};
			
			Thread t = new Thread(r);
			t.start();
        }
    }
    
    public Karte getKarteModel() {
        return karteEditor.getModel();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public void enter() {
        super.enter();
        super.controlMenu();
        stateMgr.controlMenu();
    }
    
    protected abstract class BrowserState {
        
        public BrowserState() {
        }
        
        public abstract void controlMenu();
    }
    
    protected final class NoKarteState extends BrowserState {
        
        public NoKarteState() {
        }
        
        public void controlMenu() {
            ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
            mediator.copyNewKarteAction.setEnabled(false);      // コピー新規カルテ
            mediator.printAction.setEnabled(false);             // 印刷
            mediator.modifyKarteAction.setEnabled(false);       // 修正
        }
    }
    
    protected final class HasKarteState extends BrowserState {
        
        public HasKarteState() {
        }
        
        public void controlMenu() {

            ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
            
            // 2003-10-30 licenseCode によるコントロール
            boolean canEdit = isReadOnly() ? false : true;
            mediator.copyNewKarteAction.setEnabled(canEdit);        // コピー新規カルテ
            mediator.modifyKarteAction.setEnabled(canEdit);         // 修正
            
            mediator.printAction.setEnabled(true);                  // 印刷
        }
    }
    
    protected final class StateMgr {
        
        private BrowserState noKarteState = new NoKarteState();
        private BrowserState hasKarteState = new HasKarteState();
        private BrowserState currentState;
        
        public StateMgr() {
            currentState = noKarteState;
        }
        
        public void enterNoKarteState() {
            currentState = noKarteState;
            currentState.controlMenu();
        }
        
        public void enterHasKarteState() {
            if (currentState != hasKarteState) {
                currentState = hasKarteState;
                currentState.controlMenu();
            }
        }
        
        public void controlMenu() {
            currentState.controlMenu();
        }
    }
}