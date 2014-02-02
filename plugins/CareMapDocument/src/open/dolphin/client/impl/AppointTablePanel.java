/*
 * AppointTablePanel.java
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
package open.dolphin.client.impl;

import javax.swing.*;
import javax.swing.table.*;
import open.dolphin.client.*;

import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.table.*;
import open.dolphin.util.*;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;

/**
 * AppointTablePanel
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AppointTablePanel extends JPanel implements PropertyChangeListener {
    
    private static final long serialVersionUID = 1013931150179503017L;
    
    private final String[] COLUMN_NAMES   = ClientContext.getStringArray("appoint.table.columnNames");
    private final int[] COLUMN_WIDTH      = {90, 90,300};
    private final int NUM_ROWS            = 9;
    private final int MEMO_COLUMN         = 2;
    
    private CareTableModel tableModel;
    private JTable careTable;
    private TodayRowRenderer todayRenderer;
    private String today;   // = "2003-02-21";
    private CareMapDocument parent;
    private boolean dirty;
    
    /** Creates new AppointTablePanel */
    public AppointTablePanel(JButton updateBtn) {
        
        super(new BorderLayout(0, 5));
        
        todayRenderer = new TodayRowRenderer();
        tableModel = new CareTableModel(COLUMN_NAMES, NUM_ROWS);
        careTable = new JTable(tableModel) {
            
            private static final long serialVersionUID = -3446348785385967929L;
            
            public TableCellRenderer getCellRenderer(int row, int col) {
                
                AppointmentModel e = (AppointmentModel)tableModel.getObject(row);
                
                if (e != null && e.getDate().equals(today)) {
                    Color c = parent.getAppointColor(e.getName());
                    todayRenderer.setBackground(c);
                    return todayRenderer;
                    
                } else {
                    return super.getCellRenderer(row, col);
                }
            }
        };
        careTable.setSurrendersFocusOnKeystroke(true);
        careTable.setRowSelectionAllowed(true);
        careTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        
        // CellEditor Çê›íËÇ∑ÇÈ
        // NAME_COL clickCountToStart=1, IME=ON
        TableColumn column = careTable.getColumnModel().getColumn(MEMO_COLUMN);
        column.setCellEditor(new IMECellEditor(new JTextField(), 1, true));
        
        // Set the column width
        if (COLUMN_WIDTH != null) {
            int len = COLUMN_WIDTH.length;
            for (int i = 0; i < len; i++) {
                column = careTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(COLUMN_WIDTH[i]);
            }
        }
        //careTable.setPreferredSize(new Dimension(500, 200));
        
        JScrollPane scroller = new JScrollPane(careTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel cmd = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5,0));
        cmd.add(updateBtn);
        updateBtn.setMargin(new Insets(2,2,2,2));
        this.add(cmd, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
        
        today = MMLDate.getDate();
    }
    
    public void setParent(CareMapDocument doc) {
        parent = doc;
    }
    
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals(CareMapDocument.CALENDAR_PROP)) {
            
            SimpleCalendarPanel[] calendars = (SimpleCalendarPanel[])e.getNewValue();
            
            int len = calendars.length;
            ArrayList list = new ArrayList();
            
            for (int i = 0; i < len; i++) {
                
                ArrayList results = calendars[i].getAppointDays();
                int size = results.size();
                //System.out.println("Appoint size = " + size);
                for (int k = 0; k < size; k++) {
                    list.add(results.get(k));
                }
            }
            
            tableModel.setObjectList(list);
            
        } else if (prop.equals(CareMapDocument.APPOINT_PROP)) {
            
            AppointmentModel appoint = (AppointmentModel)e.getNewValue();
            tableModel.updateAppoint(appoint);
            
        } else if (prop.equals(CareMapDocument.SELECTED_APPOINT_DATE_PROP)) {
            
            findAppoint((String)e.getNewValue());
            
        }
    }
    
    private void findAppoint(String date) {
        System.out.println(date);
        int size = tableModel.getDataSize();
        String val = null;
        for (int i = 0; i < size; i++) {
            val = (String)tableModel.getValueAt(i, 0);
            if (val.equals(date)) {
                careTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    
    protected class CareTableModel extends ObjectTableModel {
        
        private static final long serialVersionUID = -5342312972368806563L;
        
        public CareTableModel(String[] columnNames, int numRows) {
            super(columnNames, numRows);
        }
        
        public boolean isCellEditable(int row, int col) {
            return (isValidRow(row) && col == MEMO_COLUMN) ? true : false;
        }
        
        public Object getValueAt(int row, int col) {
            
            AppointmentModel e = (AppointmentModel)getObject(row);
            
            if (e == null) {
                return null;
            }
            
            String ret = null;
            
            switch (col) {
                
                case 0:
                    ret = ModelUtils.getDateAsString(e.getDate());
                    break;
                    
                case 1:
                    ret = e.getName();
                    break;
                    
                case 2:
                    ret = e.getMemo();
                    break;
            }
            
            return (Object)ret;
        }
        
        public void setValueAt(Object val, int row, int col) {
            
            String str = (String)val;
            if (col != MEMO_COLUMN || str == null || str.trim().equals("")) {
                return;
            }
            
            AppointmentModel entry = (AppointmentModel)getObject(row);
            
            if (entry != null) {
                
                entry.setMemo(str);
                
                if (entry.getState() == AppointmentModel.TT_HAS) {
                    entry.setState(AppointmentModel.TT_REPLACE);
                }
                
                fireTableCellUpdated(row, col);
                
                if (! dirty) {
                    dirty = true;
                    parent.setDirty(dirty);
                }
            }
        }
        
        public void updateAppoint(AppointmentModel appoint) {
            
            int row = findAppointEntry(appoint);
            int state = appoint.getState();
            
            if (row == -1 && state == AppointmentModel.TT_NEW) {
                addAppointEntry(appoint);
                
            } else if (row >= 0) {
                
                if (appoint.getName() != null) {
                    fireTableRowsUpdated(row, row);
                    
                } else {
                    deleteRow(row);
                }
            }
        }
        
        @SuppressWarnings("unchecked")
        public void addAppointEntry(AppointmentModel entry) {
            addRow((Object)entry);
            Collections.sort(getObjectList());
            int index = getObjectCount() -1;
            fireTableRowsUpdated(0, index);
        }
        
        private int findAppointEntry(AppointmentModel appoint) {
            
            List objects = getObjectList();
            
            if (objects == null) {
                return -1;
            }
            int len = objects.size();
            int row = -1;
            for (int i = 0; i < len; i++) {
                if (appoint == (AppointmentModel)objects.get(i)) {
                    row = i;
                    break;
                }
            }
            return row;
        }
        
        public Object[] getAppointEntries() {
            List list = getObjectList();
            return list != null ? list.toArray() : null;
        }
        
    }
    
    protected class TodayRowRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 4422900791807822090L;
        
        public TodayRowRenderer() {
        }
    }
}