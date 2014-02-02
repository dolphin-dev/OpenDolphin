/*
 * ObjectTableModel.java
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
package open.dolphin.table;

import java.util.*;
import javax.swing.table.*;

/**
 *
 * @author  kazushi Minagawa, Digital Globe, Inc.
 */
public class ObjectTableModel extends AbstractTableModel {
    
    String[] columnNames;
    
    int startNumRows;
    
    ArrayList objectList;
    
    public ObjectTableModel() {
    }
    
    /** Creates a new instance of ObjectTableModel */
    public ObjectTableModel(String[] columnNames, int startNumRows) {
    	this();
        setColumnNames(columnNames);
        setStartNumRows(startNumRows);
    }

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setStartNumRows(int startNumRows) {
		this.startNumRows = startNumRows;
	}

	protected int getStartNumRows() {
		return startNumRows;
	}
    
    public int getRowCount() {
        int size = getObjectCount();
        return size < getStartNumRows() ? getStartNumRows() : size;
    }

    public int getColumnCount() {
        return getColumnNames().length;
    }

    public Object getValueAt(int row, int col) {
        return getObject(row);
    }
        
    public String getColumnName(int col) {
        return getColumnNames()[col];
    }   
    
    public int getObjectCount() {
        return (objectList == null) ? 0 : objectList.size();
    }
    
    public Object getObject(int row) {
        return isValidRow(row) ? objectList.get(row) : null;
    }
    
    public boolean isValidRow(int row) {
        return ( (objectList != null) && (row > -1) && (row < objectList.size()) ) ? true : false;
    }
    
    public void addRow(Object o) {
        if (objectList == null) {
            objectList = new ArrayList();
        }
        int index = objectList.size();
        objectList.add(o);
        fireTableRowsInserted(index, index);
    }
    
    public void insertRow(int index, Object o) {
        if (objectList == null) {
            objectList = new ArrayList();
        }
        
        if ( (index == 0 && objectList.size() == 0) || isValidRow(index) ) {
            objectList.add(index, o);
            fireTableRowsInserted(index, index);
        }
    }
    
    public void removeRow(int index) {
        if (isValidRow(index)) {
            objectList.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public void deleteRow(int index) {
        removeRow(index);
    }
    
    public void clear() {
        if (objectList == null) {
            return;
        }
        int size = objectList.size();
        size = size > 0 ? size - 1 : 0;
        objectList.clear();
        fireTableRowsDeleted(0, size);
    }
    
    public void setObjectList(ArrayList list) {
        clear();
        objectList = list;
        if (objectList != null) {
            int index = objectList.size() - 1;
            if (index > -1) {
                fireTableRowsInserted(0, index);
            }
        }
    }
    
    public ArrayList getObjectList() {
        return objectList;
    }
    
    public int getDataSize() {
        return getObjectCount();
    }
}