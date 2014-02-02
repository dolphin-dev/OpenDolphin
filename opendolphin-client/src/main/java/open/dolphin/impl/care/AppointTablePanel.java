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
package open.dolphin.impl.care;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.OddEvenRowRenderer;
import open.dolphin.util.MMLDate;

/**
 * AppointTablePanel
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AppointTablePanel extends JPanel implements PropertyChangeListener {
    
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
            
            @Override
            public TableCellRenderer getCellRenderer(int row, int col) {
                
                AppointmentModel e = (AppointmentModel)tableModel.getObject(row);
                
                if (e != null && e.getDate() != null) {
                    String test = ModelUtils.getDateAsString(e.getDate());
                    if (test.equals(today)) {
                    Color c = parent.getAppointColor(e.getName());
                        todayRenderer.setBackground(c);
                        return todayRenderer;
                    } else {
                        return super.getCellRenderer(row, col);
                    }
                    
                } else {
                    return super.getCellRenderer(row, col);
                }
            }
        };
        careTable.setSurrendersFocusOnKeystroke(true);
        careTable.setRowSelectionAllowed(true);
        careTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        careTable.setRowHeight(ClientContext.getMoreHigherRowHeight());
        
        // CellEditor を設定する
        // NAME_COL clickCountToStart=1, IME=ON
        TableColumn column = careTable.getColumnModel().getColumn(MEMO_COLUMN);
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoKanjiListener.getInstance());
        DefaultCellEditor de = new DefaultCellEditor(tf);
        int ccts = Project.getInt("order.table.clickCountToStart", 1);
        de.setClickCountToStart(ccts);
        column.setCellEditor(de);
        
        // Set the column width
        if (COLUMN_WIDTH != null) {
            int len = COLUMN_WIDTH.length;
            for (int i = 0; i < len; i++) {
                column = careTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(COLUMN_WIDTH[i]);
            }
        }
        //careTable.setPreferredSize(new Dimension(500, 200));

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        final AbstractAction copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        careTable.getInputMap().put(copy, "Copy");
        careTable.getActionMap().put("Copy", copyAction);

        // 右クリックコピー
        careTable.addMouseListener(new MouseAdapter() {

            private void mabeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = careTable.rowAtPoint(e.getPoint());
                    if (row < 0) {
                        return;
                    }
                    JPopupMenu pop = new JPopupMenu();
                    JMenuItem item2 = new JMenuItem(copyAction);
                    pop.add(item2);
                    pop.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mabeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mabeShowPopup(e);
            }
        });
        
        JScrollPane scroller = new JScrollPane(careTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel cmd = new JPanel(new FlowLayout());
        cmd.add(updateBtn);
        updateBtn.setMargin(new Insets(0,0,0,0));   //2,2,2,2
        this.add(cmd, BorderLayout.EAST);
        this.add(scroller, BorderLayout.CENTER);
        
        today = MMLDate.getDate();
    }
    
    public void setParent(CareMapDocument doc) {
        parent = doc;
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        StringBuilder sb = new StringBuilder();
        int numRows = careTable.getSelectedRowCount();
        int[] rowsSelected = careTable.getSelectedRows();
        int numColumns =   careTable.getColumnCount();

        for (int i = 0; i < numRows; i++) {

            StringBuilder s = new StringBuilder();
            for (int col = 0; col < numColumns; col++) {
                Object o = careTable.getValueAt(rowsSelected[i], col);
                if (o!=null) {
                    s.append(o.toString());
                }
                s.append(",");
            }
            if (s.length()>0) {
                s.setLength(s.length()-1);
            }
            sb.append(s.toString()).append("\n");

        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals(CareMapDocument.CALENDAR_PROP)) {
            
            SimpleCalendarPanel[] calendars = (SimpleCalendarPanel[])e.getNewValue();
            
            int len = calendars.length;
            ArrayList<AppointmentModel> list = new ArrayList<AppointmentModel>();
            
            for (int i = 0; i < len; i++) {
                
                ArrayList<AppointmentModel> results = calendars[i].getAppointDays();
                int size = results.size();
                for (int k = 0; k < size; k++) {
                    list.add(results.get(k));
                }
            }
            
            tableModel.setDataProvider(list);
            
        } else if (prop.equals(CareMapDocument.APPOINT_PROP)) {
            
            AppointmentModel appoint = (AppointmentModel)e.getNewValue();
            tableModel.updateAppoint(appoint);
            
        } else if (prop.equals(CareMapDocument.SELECTED_APPOINT_DATE_PROP)) {
            
            findAppoint((String)e.getNewValue());
            
        }
    }
    
    private void findAppoint(String date) {
        int size = tableModel.getObjectCount();
        String val;
        for (int i = 0; i < size; i++) {
            val = (String)tableModel.getValueAt(i, 0);
            if (val.equals(date)) {
                careTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * AppointmentModel table model
     */
    protected class CareTableModel extends ListTableModel<AppointmentModel> {
        
        public CareTableModel(String[] columnNames, int numRows) {
            super(columnNames, numRows);
        }
        
        @Override
        public boolean isCellEditable(int row, int col) {
            return (isValidRow(row) && col == MEMO_COLUMN) ? true : false;
        }
        
        @Override
        public Object getValueAt(int row, int col) {
            
            AppointmentModel entry = getObject(row);
            
            if (entry == null) {
                return null;
            }
            
            String ret = null;
            
            switch (col) {
                
                case 0:
                    ret = ModelUtils.getDateAsString(entry.getDate());
                    break;
                    
                case 1:
                    ret = entry.getName();
                    break;
                    
                case 2:
                    ret = entry.getMemo();
                    break;
            }
            
            return (Object)ret;
        }
        
        @Override
        public void setValueAt(Object val, int row, int col) {
            
            String str = (String)val;
            if (col != MEMO_COLUMN || str == null || str.trim().equals("")) {
                return;
            }
            
            AppointmentModel entry = getObject(row);
            
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
                    deleteAt(row);
                }
            }
        }
        
        public void addAppointEntry(AppointmentModel entry) {
            addObject(entry);
            Collections.sort(getDataProvider());
            int index = getObjectCount() -1;
            fireTableRowsUpdated(0, index);
        }
        
        private int findAppointEntry(AppointmentModel appoint) {
            
            if (getDataProvider() == null) {
                return -1;
            }
            int len = getDataProvider().size();
            int row = -1;
            for (int i = 0; i < len; i++) {
                if (appoint == getObject(i)) {
                    row = i;
                    break;
                }
            }
            return row;
        }
        
        public Object[] getAppointEntries() {
            List list = getDataProvider();
            return list != null ? list.toArray() : null;
        }
        
    }
    
    protected class TodayRowRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 4422900791807822090L;
        
        public TodayRowRenderer() {
        }
    }
}