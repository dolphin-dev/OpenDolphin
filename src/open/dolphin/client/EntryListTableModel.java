/*
 * EntryListTableModel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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

import javax.swing.table.*;
import java.util.*;


/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class EntryListTableModel extends AbstractTableModel {
    
    /** スタート時の行数 */
    private int startNumRows;
    
    /** カラムヘッダ名 */
    private String[] columnNames;
    
    /** カラムクラス */
    private Class[] columnClasses;
    
    /** 行データを格納する ArrayList */
    private ArrayList dataList;
    
    /**
     * デフォルトコンストラクタ
     */ 
    public EntryListTableModel() {
        dataList = new ArrayList();
    }
    
    /**
     * カラム名と行数からこのクラスを生成する
     */
    public EntryListTableModel(String[] names, int rows) {
        this();
        columnNames = names;
        startNumRows = rows;
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    public void setColumnNames(String[] names) {
        columnNames = names;
    }
    
    public int getStartNumRows() {
        return startNumRows;
    }
    
    public void setStartNumRows(int value) {
        startNumRows = value;
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public Class[] getColumnClasses() {
        return columnClasses;
    }
    
    public void setColumnClasses(Class[] classes) {
        columnClasses = classes;
    }
        
    public ArrayList getDataList() {
        return dataList;
    }
    
    public void setDataList(ArrayList list) {
        dataList = list;
    }    
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {
        int size = dataList.size();
        return ( size < startNumRows ) ? startNumRows : size;
    }

    public Class getColumnClass(int col) {        
        return columnClasses == null ? java.lang.String.class : columnClasses[col];
    }
    
    public Object getValueAt(int row, int col) {        
        Object[] data = getRowData(row);
        return data != null ? data[col] : null;
    }
    
    public void clear() {
        int size = dataList.size();
        dataList.clear();
        if (size <= startNumRows) {
            fireTableRowsDeleted(0, size -1);
        }
        else {
            fireTableRowsDeleted(startNumRows,size -1);
            fireTableRowsUpdated(0, startNumRows -1);
        }
    }
    
    public void addRow(String[] data) {
        int size = dataList.size();
        dataList.add(data);
        if (size < startNumRows) {
            fireTableRowsUpdated(size, size);
        }
        else {
            fireTableRowsInserted(startNumRows, 1);
        }
    }
    
    public void addRows(ArrayList rowList) {        
        int len = rowList.size();
        for (int i = 0; i < len; i++) {
            addRow((String[])rowList.get(i));
        }
    }
    
    public void insertRow(int index, String[] data) {
        if (index < dataList.size()) {
            dataList.add(index,data);
            fireTableRowsInserted(index,index);
        }
    }
    
    public Object[] getRowData(int row) {
        if (row < dataList.size()) {
            return (Object[])dataList.get(row);
        }
        else {
            return null;
        }
    }
    
    /**
     * DN: を返す
     */
    public String getDN(int row) {        
        if (row < dataList.size()) {
            // DN は行データの最後に格納されている
            String[] data = (String[])dataList.get(row);
            int len = data.length;
            len--;
            return data[len];
        }
        else {
            return null;
        }
    }
    
    /**
     * DataList のサイズを返す
     */
    public int getSize() {
        return dataList.size();
    }
}