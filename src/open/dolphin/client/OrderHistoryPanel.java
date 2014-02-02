/*
 * OrderHistoryPanel.java
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

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Module;
import open.dolphin.plugin.IChartContext;
import open.dolphin.table.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.awt.*;
import java.util.*;
import java.beans.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Table to fetch Oreder History of patient.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class OrderHistoryPanel extends JPanel implements PropertyChangeListener {
        
    private ObjectTableModel tModel;
    private JTable table;
    private JLabel contents;
    private String pid;
    private String markEvent;
    private String startDate;
    private String endDate;
    private CareMapDocument parent;
    
    private Dimension contentSize = new Dimension(240,300);

    /** Creates new OrderHistoryPanel */
    public OrderHistoryPanel() {
        
        super(new BorderLayout(5, 0));
        
        String[] columnNames = ClientContext.getStringArray("orderhistory.table.columnNames");
        int startNumRows = 12;
        
        tModel = new ObjectTableModel(columnNames, startNumRows) {
            
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            public Object getValueAt(int row, int col) {
                
                Object[] o = (Object[])getObject(row);
                if (o == null) {
                    return null;
                }
                Object ret = null;
                
                switch (col) {
                    
                    case 0:
                        String val = (String)o[0];
                        int index = val.indexOf('T');
                        ret = index > 0 ? val.substring(0, index) : val;
                        break;
                        
                    case 1:
                        Module stamp = (Module)o[1];
                        ret = stamp.getModuleInfo().getName();
                        break;
                }
                
                return ret;
            }
        };
        
        table = new JTable(tModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);   
        
        // 行クリックで内容を表示する
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        ListSelectionModel m = table.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int index = table.getSelectedRow();
                    displayOrder(index);
                }
            }
        });
        setColumnWidth(new int[]{50, 240});
        
        JScrollPane scroller = new JScrollPane(table,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroller, BorderLayout.CENTER);
        
        // 内容表示用 TextArea
        contents = new JLabel();
		contents.setBackground(Color.white);
        //contents.setEditable(false);
        //contents.setLineWrap(true);
        //contents.setMargin(new Insets(3,3,3,3));
        JScrollPane cs = new JScrollPane(contents, 
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cs.setPreferredSize(contentSize);
        cs.setMaximumSize(contentSize);
        add(cs, BorderLayout.EAST);
    }
    
    public void setColumnWidth(int[] columnWidth) {
        int len = columnWidth.length;
        for (int i = 0; i < len; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidth[i]);
        }        
    }
    
    public String getPid() {
        return pid;
    }
    
    public void setPid(String val) {
        pid = val;
    }
        
    public void setParent(CareMapDocument val) {
        parent = val;
    }
    
    public void propertyChange(PropertyChangeEvent e) {
     
        String prop = e.getPropertyName();
        
        if (prop.equals(CareMapDocument.MARK_EVENT_PROP)) {
            markEvent = (String)e.getNewValue();
            if (isMyCode()) {
                //System.out.println("my propertyChange: " + markEvent);
                fetch();
            }
            
        } else if (prop.equals(CareMapDocument.PERIOD_PROP)) {
            Period p = (Period)e.getNewValue();
            startDate = p.getStartDate() + "T00:00:00";
            endDate = p.getEndDate() + "T23:59:59";
            //System.out.println(startDate);
            //System.out.println(endDate);
            if (isMyCode()) {
                //System.out.println("my propertyChange: " + startDate + " " + endDate);
                fetch();
            }
        
        } else if (prop.equals(CareMapDocument.SELECTED_DATE_PROP)) {
          
            String date = (String)e.getNewValue();
            if (isMyCode()) {
                //System.out.println("my propertyChange: " + date);
                findDate(date);
            }
        }
    }
    
	private boolean isMyCode() {
		return ( markEvent.equals("medOrder") || 
				 markEvent.equals("treatmentOrder") || 
				 markEvent.equals("testOrder")) ? true : false;
	}    
    
    public void fetch() {
        
        if (markEvent == null || startDate == null || endDate == null) {
            return;
        }
        
        Runnable r = new Runnable() {
            
            public void run() {
                //if (dao == null) {
                    //dao = (OrderHistoryDao)DaoFactory.create(this, "orderHistory");
                //}
                SwingUtilities.invokeLater(new Runnable() {                    
                    public void run() {
                        
                        ChartPlugin cs = (ChartPlugin)parent.context;
                        StatusPanel sp = cs.getStatusPanel();
                        sp.start("履歴を取得しています...");
                    }
                });

                //SqlKarteDao dao = parent.getKarteDao();
				IChartContext ctx = parent.getChartContext();
                ArrayList orders = ctx.getOrderHistory(pid, markEvent, startDate, endDate);

                if (orders != null) {
                    
					System.out.println("got order");
                    tModel.setObjectList(orders);
                    
                    
                } else {
					System.out.println("no order");
                    tModel.clear();
					
                }
                
                SwingUtilities.invokeLater(new Runnable() {                    
                    public void run() {
                        
                        ChartPlugin cs = (ChartPlugin)parent.context;
                        StatusPanel sp = cs.getStatusPanel();
                        sp.stop("");
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
        
    private void displayOrder(int index) {
        
        contents.setText("");
        
        Object[] o = (Object[])tModel.getObject(index);
        if (o == null) {
            return;
        }
        
        Module stamp = (Module)o[1];
		IInfoModel model = stamp.getModel();
        
		try {
			VelocityContext context = ClientContext.getVelocityContext();
			context.put("model", model);
			context.put("stampName", stamp.getModuleInfo().getName());
			
			// このスタンプのテンプレートファイルを得る
			String templateFile = stamp.getModel().getClass().getName() + ".vm";
			//debug(templateFile);
    
			// Merge する
			StringWriter sw = new StringWriter();
			BufferedWriter bw = new BufferedWriter(sw);
			InputStream instream = ClientContext.getTemplateAsStream(templateFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "SHIFT_JIS"));
			Velocity.evaluate(context, bw, "stmpHolder", reader);
			bw.flush();
			bw.close();
			reader.close();
			contents.setText(sw.toString());
        
		} catch (Exception e) {
			System.out.println("Execption while setting the stamp text: " + e.toString());
			e.printStackTrace();
		}        
    }  
        
    private void findDate(String date) {
        
        //System.out.println("selected date = " + date);
        int size = tModel.getDataSize();
        for (int i = 0; i < size; i++) {
            String rowDate = (String)tModel.getValueAt(i,0);
            if (rowDate.equals(date)) {
                table.setRowSelectionInterval(i,i);
                break;
            }
        }
    }
}