/*
 * ItemTableModel.java
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
package open.dolphin.order;

import javax.swing.table.*;
import java.util.*;

/**
 * マスタアイテムテーブルのモデルクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ItemTableModel extends AbstractTableModel {

    private String[] columnNames;
    private int startNumRows;
    
    // 数量カラム
    private int numberCol;

    // ArryList to hold items
    private ArrayList dataList;

    public ItemTableModel(final String[] columnNames, final int startNumRows, final int numberCol) {
        super();
        
        this.columnNames = columnNames;
        this.startNumRows = startNumRows;
        this.numberCol = numberCol;
        dataList = new ArrayList();        
    }
    
    /**
     * 数量カラムのみ編集可能にする
     */
    public boolean isCellEditable(int row, int col) {
        return col == numberCol ? true : false;
    }

    public String getColumnName(int col) {
       return columnNames[col];
    }

    public int getColumnCount() {
       return columnNames.length;
    }

    public int getRowCount() {
        int size = dataList.size();
        return ( size < startNumRows ) ? startNumRows : size;
    }

    public Object getValueAt(int row, int col) {

        Object ret = null;
        MasterItem item = getItem(row);

        if (item != null) {

            switch (col) {

               case 0:
                  ret = item.name;
                  break;

               case 1:
                  ret = item.number;
                  break;

               case 2:
                  ret = item.unit;
                  break;
            }
        }
        return ret;
    }
    
    public void setValueAt(Object val, int row, int col) {
        
        if (col == numberCol) {
            MasterItem item = getItem(row);
            if (item != null) {
               // try {
                    //Integer.parseInt((String)value);
                    item.number = (String)val;
                    fireTableCellUpdated(row, col);
               // }
               // catch (Exception e) {
                    
               // }
            }
        }
    }

    public Class getColumnClass(int c) {
        return java.lang.String.class;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public void addItem(MasterItem item) {
        
        int index = dataList.size();
        dataList.add(item);
        
        if (index < startNumRows) {
          fireTableRowsUpdated(index, index);
        }
        else {
          fireTableRowsInserted(index, index);
        }
    }

    public MasterItem getItem(int row) {
        return row < dataList.size() ? (MasterItem)dataList.get(row) : null;
    }

    public void clear() {
        int last = dataList.size();
        dataList.clear();
        fireTableRowsUpdated(0, startNumRows - 1);
        if (last > startNumRows) {
            fireTableRowsDeleted(startNumRows, last -2);
        }
    }
        
    public void deleteRow(int row) { 
        
        if ( row < dataList.size() ) {             
            dataList.remove(row);
            fireTableRowsUpdated(0, startNumRows - 1);
            
            if (row >= startNumRows) {
                fireTableRowsDeleted(row, row);
            }
        }
    }   
    
    public void up(int row) {
        if (row < dataList.size()) {
            Object o = dataList.remove(row);
            dataList.add(row -1, o);
            fireTableRowsUpdated(row -1, row);
        }
    }
    
    public void down(int row) {
        if (row < dataList.size()) {
            Object o = dataList.remove(row);
            dataList.add(row + 1, o);
            fireTableRowsUpdated(row, row + 1);
        }        
    }
    
    public void moveRow(int fromIndex, int toIndex) {
        //int toIndex = getValidRow(toIndex);
        Object o = dataList.remove(fromIndex);
        dataList.add(toIndex, o);
        if (fromIndex <= toIndex) {
            fireTableRowsUpdated(fromIndex, toIndex);
        }
        else {
            fireTableRowsUpdated(toIndex, fromIndex);
        }
    }
    
    public int getDataSize() {
        return dataList.size();
    }
    
    public Object[] getItems() {
        return dataList.toArray();
    }
    
    public boolean isNumberOk() {
        int len = dataList.size();
        if (len == 0) {
            return false;
        }
        boolean ret = true;
        boolean hasTreatment = false;
        
        for (int i = 0; i < len; i++) {
            MasterItem item = (MasterItem)dataList.get(i);
            if (item.classCode != 0) {
                if ( (item.number == null) || 
                     (item.number.startsWith("0")) ||
                     (item.number.startsWith("-")) ) {
                    ret = false;
                    break;
                }
            }
            else {
                hasTreatment = true;
            }
        }
        return ret && hasTreatment ? true : false;
    }        
}