/*
 * Created on 2005/02/22
 *
 */
package open.dolphin.client;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import open.dolphin.infomodel.SimpleDate;


/**
 * CalendarTableModel
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class CalendarTableModel extends AbstractTableModel {
    
    private static final String[] COLUMN_NAMES = {
        "日", "月", "火", "水", "木", "金", "土"
    };
    private String[] columnNames = COLUMN_NAMES;
    private Object[][] data;
    private Collection markDates;
    private int year;
    private int month;
    private int startDay;
    private int firstCell;
    private int lastCell;
    private int numCols = columnNames.length;
    private int numRows;
    private int numDaysOfMonth;
    
    //private GregorianCalendar firstDate;
    //private GregorianCalendar lastDate;
    private GregorianCalendar startDate;
    
    
    /**
     * CalendarTableModel を生成する。
     * @param year   カレンダの年
     * @param month　 カレンダの月
     */
    public CalendarTableModel(int year, int month) {
        
        this.year = year;
        this.month = month;
        
        // 作成する月の最初の日  yyyyMM1
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        //firstDate = (GregorianCalendar) gc.clone();
        
        // 最初の日は週の何日目か
        // 1=SUN 6=SAT
        firstCell = gc.get(Calendar.DAY_OF_WEEK);
        firstCell--;  // table のセル番号へ変換する
        
        // この月の日数を得る
        numDaysOfMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // その月の最後の日を求める 1日 + （日数-1）
        gc.add(Calendar.DAY_OF_MONTH, numDaysOfMonth - 1);
        //lastDate = (GregorianCalendar) gc.clone();
        
        // 最後の日はその月の何週目か
        numRows = gc.get(Calendar.WEEK_OF_MONTH);
        
        // それは週の何日目か
        lastCell = gc.get(Calendar.DAY_OF_WEEK);
        lastCell--;
        
        // １次元のセル番号へ変換する
        lastCell += (numRows-1)*numCols; // table のセル番号へ変換する
        
        // このカレンダの表示開始日を求める
        // 一度一日に戻し、それからさらにカラム番号分の日数を引く
        gc.add(Calendar.DAY_OF_MONTH, 1 - numDaysOfMonth);
        gc.add(Calendar.DAY_OF_MONTH, -firstCell);
        startDate = (GregorianCalendar) gc.clone();
        
        startDay = gc.get(Calendar.DAY_OF_MONTH);
        
        // 空のデータ配列
        data = new Object[numRows][numCols];
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getRowCount() {
        return numRows;
    }
    
    public int getColumnCount() {
        return numCols;
    }
    
    public Object getValueAt(int row, int col) {
        
        // Cell 番号を得る
        int cellNumber = row*numCols + col;
        Object ret = null;
        
        // 先月か
        if (cellNumber < firstCell) {
            ret = String.valueOf(startDay + cellNumber);
            
            // 来月か
        } else if (cellNumber > lastCell) {
            ret = String.valueOf(cellNumber - lastCell);
            
            // 当月	の場合
        } else {
            // data 配列から取り出す
            ret = data[row][col];
            
            // null でなければそれを返す
            // null なら日を返す
            if (ret == null) {
                
                return String.valueOf(1 + cellNumber - firstCell);
            }
        }
        
        return ret;
    }
    
    public void setValueAt(Object value, int row, int col) {
        
        int cellNumber = row*numCols + col;
        
        // 先月または来月の時は何もしない
        if ( (cellNumber < firstCell) || (cellNumber > lastCell) ) {
            return;
        }
        
        // 当月の場合はそれを単純に設定する
        data[row][col] = value;
    }
    
    public void setMarkDates(Collection c) {
        
        this.markDates = c;
        clear();
        if (markDates != null) {
            Iterator iter = markDates.iterator();
            SimpleDate date = null;
            
            while (iter.hasNext()) {
                date = (SimpleDate)iter.next();
                if ( (year != date.getYear()) || (month != date.getMonth()) ) {
                    continue;
                }
                int day = date.getDay();
                int cellNumber = firstCell + (day-1);
                int row = cellNumber / numCols;
                int col = cellNumber % numCols;
                setValueAt(date, row, col);
            }
        }
        this.fireTableDataChanged();
    }
    
    public Collection getMarkDates() {
        return markDates;
    }
    
    public void clear() {
        data = new Object[numRows][numCols];
    }
    
    public boolean isOutOfMonth(int row, int col) {
        int cellNumber = row*numCols + col;
        return ( (cellNumber < firstCell) || (cellNumber > lastCell) ) ? true : false;
    }
    
    public SimpleDate getFirstDate() {
        return new SimpleDate(year, month, 1);
    }
    
    public SimpleDate getLastDate() {
        return new SimpleDate(year, month, numDaysOfMonth);
    }

    public SimpleDate getDate(int row, int col) {
        int cellNumber = row*numCols + col;
        GregorianCalendar gc = (GregorianCalendar) startDate.clone();
        gc.add(Calendar.DAY_OF_MONTH, cellNumber);
        int y = gc.get(Calendar.YEAR);
        int m = gc.get(Calendar.MONTH);
        int d = gc.get(Calendar.DAY_OF_MONTH);
        return new SimpleDate(y, m, d);
    }
}
