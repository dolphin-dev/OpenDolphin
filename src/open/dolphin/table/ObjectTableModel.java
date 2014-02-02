/*
 * ObjectTableModel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
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

import java.lang.reflect.Method;
import java.util.*;
import javax.swing.table.*;

/**
 * ObjectTableModel
 *
 * @author  kazushi Minagawa, Digital Globe, Inc.
 */
public class ObjectTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 38474601689966826L;
    
    private String[] columnNames;
    private String[] methodNames;
    
    private int startNumRows;
    
    private List objectList;
    
    public ObjectTableModel() {
    }
    
    /** Creates a new instance of ObjectTableModel */
    public ObjectTableModel(String[] columnNames, int startNumRows) {
        this();
        setColumnNames(columnNames);
        setStartNumRows(startNumRows);
    }
    
    /** Creates a new instance of ObjectTableModel */
    public ObjectTableModel(String[] columnNames, int startNumRows, String[] methodNames) {
        this(columnNames, startNumRows);
        setMethodNames(methodNames);
    }
    
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    public void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
    }
    
    public String[] getMethodNames() {
        return methodNames;
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
        
        Object target = getObject(row);
        if (target != null && getMethodNames() != null) {
            try {
                Method method = target.getClass().getMethod(methodNames[col], (Class[])null);
                return method.invoke(target, (Object[])null);
            } catch (Exception e) {
                return null;
            }
        }
        return target;
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
    
    @SuppressWarnings("unchecked")
    public void addRow(Object o) {
        if (objectList == null) {
            objectList = new ArrayList();
        }
        int index = objectList.size();
        objectList.add(o);
        fireTableRowsInserted(index, index);
    }
    
    @SuppressWarnings("unchecked")
    public void insertRow(int index, Object o) {
        if (objectList == null) {
            objectList = new ArrayList();
        }
        
        if ( (index == 0 && objectList.size() == 0) || isValidRow(index) ) {
            objectList.add(index, o);
            fireTableRowsInserted(index, index);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void moveRow(int from, int to) {
        if (! isValidRow(from) || ! isValidRow(to)) {
            return;
        }
        if (from == to) {
            return;
        }
        Object o = objectList.remove(from);
        objectList.add(to, o);
        fireTableRowsUpdated(0, getObjectCount());
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
    
    public void setObjectList(List list) {
        if (objectList != null) {
            objectList.clear();
        }
        objectList = list;
        this.fireTableDataChanged();
        
    }
    
    public List getObjectList() {
        return objectList;
    }
    
    public int getDataSize() {
        return getObjectCount();
    }
}