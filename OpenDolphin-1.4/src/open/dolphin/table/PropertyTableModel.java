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
        this(new String[]{"çÄñ⁄", "íl"}, attrNames, methodNames);
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return attrNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {

        if (col == 0) {
            return attrNames[row];
        }

        Object retObj = null;

        if (target != null && methodNames != null) {

            try {
                Method targetMethod = target.getClass().getMethod(methodNames[row], (Class[]) null);
                retObj = targetMethod.invoke(target, (Object[]) null);

            } catch (Exception e) {
            }
        }
        return retObj;
    }

    public void setObject(Object o) {
        this.target = o;
        this.fireTableDataChanged();
    }
}
