/*
 * Created on 2005/09/03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.table;

import java.lang.reflect.Method;

import javax.swing.table.AbstractTableModel;

/**
 * PropertyTableModel
 * 
 * @author Minagawa,Kazushi
 */
public class PropertyTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 2526602991294064807L;
	
	private String[] columnNames;
	private String[] attrNames;
	private String[] methodNames;
	private Object target;
	
	public PropertyTableModel(String[] columnNames, String[] attrNames, String[] methodNames) {
		super();
		this.columnNames = columnNames;
		this.attrNames = attrNames;
		this.methodNames = methodNames;
	}
	
	public PropertyTableModel(String[] attrNames, String[] methodNames) {
		this(new String[]{"çÄñ⁄", "íl"},attrNames,methodNames);
	}
	
	public String getColumnName(int col) {
        return columnNames[col];
    } 
	
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public int getRowCount() {
		return attrNames.length;
	}
	
	public Object getValueAt(int row, int col) {
		
		if (col == 0) {
			return attrNames[row];
		}
		
		if (target != null && methodNames != null) {
		
			try {
				Method targetMethod = target.getClass().getMethod(methodNames[row], (Class[])null);
	    			return targetMethod.invoke(target, (Object[])null);
				
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public void setObject(Object o) {
		this.target = o;
		this.fireTableDataChanged();
	}

}
