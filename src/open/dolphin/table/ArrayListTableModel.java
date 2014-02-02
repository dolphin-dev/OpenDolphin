/*
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
package open.dolphin.table;

import java.util.*;
import javax.swing.table.*;

/**
 * ArrayList をデータモデルに持つテーブルモデルクラスス。
 * テーブルの生成時に任意の数の空白行を持つことができる。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ArrayListTableModel extends AbstractTableModel {
        
    // カラム名配列
    String[] columnNames;
    
    // スタート時の空白行数
    int startNumRows;
    
    // データモデル
    ArrayList dataList;
    
    /** 
     * Creates new ArrayListTableModel
     * @param colNames テーブルのカラム名配列
     * @param startNumRows　テーブルのスタート時の空白行数
     */
    public ArrayListTableModel(final String[] colNames, final int startNumRows) {
        this.columnNames = colNames;
        this.startNumRows = startNumRows;
        dataList = new ArrayList();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public int getRowCount() {
        int size = getDataCount();
        return (size < startNumRows) ? startNumRows : size;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int row, int col) {
        Object[] o = getRowData(row);
        return o != null ? o[col] : null;
    }
        
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public ArrayList getDataList() {
        return dataList;
    }    
    
    public int getDataCount() {
        return dataList.size();
    }
    
    public boolean isValidRow(int row) {        
        return (row > -1 && row < getDataCount()) ? true : false;
    }
    
    public Object[] getRowData(int row) {
        return isValidRow(row) ? (Object[])dataList.get(row) : null;
    }
    
    public void addRow(Object[] o) {
        int index = dataList.size();
        dataList.add(o);
        fireTableRowsInserted(index, index);
    }
    
    public void insertRow(int index, Object[] o) {
        boolean b = (index == 0 && getDataCount() == 0) ? true : false;
        b = b ? true : isValidRow(index);
        if (b) {
            dataList.add(index, o);
            fireTableRowsInserted(index, index);
        }
    }
    
    public void removeRow(int index) {
        if (isValidRow(index)) {
            dataList.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public void clear() {
        int size = dataList.size();
        size = size > 0 ? size - 1 : 0;
        dataList.clear();
        fireTableRowsDeleted(0, size);
    }
}