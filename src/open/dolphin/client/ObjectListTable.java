package open.dolphin.client;

import open.dolphin.table.OddEvenRowRenderer;
import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.table.ObjectReflectTableModel;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

/**
 * Oblect のリストをテーブルに表示するサポートクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ObjectListTable {
    
    // 束縛プロパティ名
    public static final String SELECTED_OBJECT = "selectedObjects";
    
    // 束縛プロパティ名
    public static final String DOUBLE_CLICKED_OBJECT = "doubleClickedObject";
    
    // 束縛プロパティ名
    public static final String OBJECT_VALUE = "objectValue";
    
    // 束縛サポート
    private PropertyChangeSupport boundSupport;
    
    // テーブルで選択されたオブジェクトの配列
    private Object[] selectedObjects;
    
    // テーブル上でダブルクリックされたオブジェクト
    private Object doubleClickedObject;
    
    // Table Model
    private ObjectReflectTableModel tableModel;
    
    // このクラスの JTable
    private JTable table;
    
    /**
     * Creates new ObjectListTable
     */
    public ObjectListTable(String[] columnNames, int startNumRows,
            String[] methodNames, Class[] classes) {
        this(columnNames, startNumRows, methodNames, classes, true);
    }
    
        
    /**
     * Creates new ObjectListTable
     */
    public ObjectListTable(String[] columnNames, int startNumRows,
            final String[] methodNames, Class[] classes, final int[] editableColumns) {
        
        tableModel = new ObjectReflectTableModel(columnNames, startNumRows, methodNames, classes);
        
        table = new JTable(tableModel) {
            
            @Override
            public boolean isCellEditable(int row, int col) {
                boolean editable = false;
                for (int i : editableColumns) {
                    if (i == col) {
                        editable = true;
                        break;
                    }
                }
                return editable;
            }
            
            @Override
            public void setValueAt(Object value, int row, int col) {
                
                if (value == null) {
                    return;
                }
                
                Object o = tableModel.getObject(row);
                if (o == null) {
                    return;
                }
                
                try {
                    String setter = "set" + methodNames[col].substring(3);
                    Method method = o.getClass().getMethod(setter, new Class[]{value.getClass()});
                    method.invoke(o, new Object[]{value});
                    notifyObjectValue(o);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        connect();
        table.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
    }
    
    /**
     * Creates new ObjectListTable
     */
    public ObjectListTable(String[] columnNames, int startNumRows,
            String[] methodNames, Class[] classes, boolean oddEvenColor) {
        tableModel = new ObjectReflectTableModel(columnNames, startNumRows,
                methodNames, classes);
        table = new JTable(tableModel);
        connect();
        if (oddEvenColor) {
            table.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        }
    }
    
    public void clear() {
        if (tableModel != null) {
            tableModel.clear();
        }
    }
    
    public ObjectReflectTableModel getTableModel() {
        return tableModel;
    }
    
    public JTable getTable() {
        return table;
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop,
            PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public Object[] getSelectedObject() {
        return selectedObjects;
    }
    
    public Object getDoubleClickedObject() {
        return doubleClickedObject;
    }
    
    public void setObjectList(List<Object> list) {
        tableModel.setObjectList(list);
    }
    
    public void setSelectionMode(int selectionMode) {
        table.setSelectionMode(selectionMode);
    }
    
    public void setColumnWidth(int[] columnWidth) {
        for (int i = 0; i < columnWidth.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(
                    columnWidth[i]);
        }
    }
    
    public JScrollPane getScroller() {
        return new JScrollPane(table);
    }
    
    public JPanel getPanel(boolean showHeader) {
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(table, BorderLayout.CENTER);
        if (showHeader) {
            ret.add(table.getTableHeader(), BorderLayout.NORTH);
        }
        return ret;
    }
    
    public JPanel getPanel() {
        return getPanel(true);
    }
    
    protected void connect() {
        
        // 選択されたオブジェクトを通知する
        // 利用する側で削除ボタンなどの制御をしている可能性があるためヌルの場合も通知する
        // 利用する側は Object[] obj = (Object[])e.getNewValue(), if (obj != null &&
        // obj.length > 0) で判断する
        ListSelectionModel sleModel = table.getSelectionModel();
        sleModel.addListSelectionListener((ListSelectionListener)
            EventHandler.create(ListSelectionListener.class, this, "listSelectionChanged", ""));
        
        // ダブルクリックされたオブジェクトを通知する
//        table.addMouseListener((MouseListener)
//            EventHandler.create(MouseListener.class, this, "mouseClicked", "processClick", ""));
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Object value = (Object) tableModel.getObject(table.getSelectedRow());
                    if (value != null) {
                        notifyDoubleClickedObject(value);
                    }
                }
            }
        });
    }
    
    public void listSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length > 0) {
                ArrayList<Object> list = new ArrayList<Object>(1);
                for (int i = 0; i < selectedRows.length; i++) {
                    Object obj = tableModel.getObject(selectedRows[i]);
                    if (obj != null) {
                        list.add(obj);
                    }
                }
                notifySelectedObjects(list.toArray());
            }
        }
    }
    
    protected void notifySelectedObjects(Object[] selected) {
        if (boundSupport != null) {
            Object[] old = selectedObjects;
            selectedObjects = selected;
            boundSupport.firePropertyChange(SELECTED_OBJECT, old, selectedObjects);
        }
    }
    
    protected void notifyDoubleClickedObject(Object clicked) {
        if (boundSupport != null) {
            Object old = doubleClickedObject;
            old = null;	// 強制通知
            doubleClickedObject = clicked;
            boundSupport.firePropertyChange(DOUBLE_CLICKED_OBJECT, old, doubleClickedObject);
        }
    }
    
    protected void notifyObjectValue(Object obj) {
        if (boundSupport != null) {
            Object old = null;
            boundSupport.firePropertyChange(OBJECT_VALUE, old, obj);
        }
    }
    
    // //////// テーブルへのデータ追加と削除の簡易サポート /////////
    
    public void addRow(Object add) {
        tableModel.addRow(add);
    }
    
    public void addRows(Collection c) {
        tableModel.addRows(c);
    }
    
    public void deleteSelectedRow() {
        int selected = table.getSelectedRow();
        if (selected > -1) {
            tableModel.deleteRow(selected);
        }
    }
    
    public void deleteSelectedRows() {
        Object[] objects = this.getSelectedObject();
        if (objects != null && objects.length > 0) {
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < objects.length; i++) {
                list.add(objects[i]);
            }
            tableModel.deleteRows(list);
        }
    }
}