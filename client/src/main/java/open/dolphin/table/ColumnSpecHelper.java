package open.dolphin.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import open.dolphin.project.Project;

/**
 * ColumnSpecHelper
 * @author masuda, Masuda Naika
 */
public class ColumnSpecHelper {
    
    private static final String CAMMA = ",";
    
    private JTable table;
    private String specName;
    private String[] columnNames;
    private String[] propNames;
    private Class[] columnClasses;
    private int[] columnWidth;
    
    // カラム仕様リスト
    private List<ColumnSpec> columnSpecs;
    
    
    public ColumnSpecHelper(String specName, 
            String[] columnNames, String[] propNames, Class[] columnClasses, int[] columnWidth) {
        
        this.specName = specName;
        this.columnNames = columnNames;
        this.propNames = propNames;
        this.columnClasses = columnClasses;
        this.columnWidth = columnWidth;
    }
    
    public void setTable(JTable table) {
        this.table = table;
    }
    
    public List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }
    
    public String[] getTableModelColumnNames() {
        int len = columnSpecs.size();
        String[] names = new String[len];
        for (int i = 0; i < len; i++) {
            ColumnSpec cp = columnSpecs.get(i);
            names[i] = cp.getName();
        }
        return names;
    }
    
    public String[] getTableModelColumnMethods() {
        int len = columnSpecs.size();
        String[] methods = new String[len];
        for (int i = 0; i < len; i++) {
            ColumnSpec cp = columnSpecs.get(i);
            methods[i] = cp.getMethod();
        }
        return methods;
    }
    
    public Class[] getTableModelColumnClasses() {
        int len = columnSpecs.size();
        Class[] classes = new Class[len];
        for (int i = 0; i < len; i++) {
            ColumnSpec cp = columnSpecs.get(i);
            try {
                classes[i] = Class.forName(cp.getCls());
            } catch (ClassNotFoundException ex) {
            }
        }
        return classes;
    }
    
    public void updateColumnWidth() {

        for (int i = 0; i < columnSpecs.size(); ++i) {
            ColumnSpec cs = columnSpecs.get(i);
            int width = cs.getWidth();
            TableColumn tc = table.getColumnModel().getColumn(i);
            if (width != 0) {
                tc.setMaxWidth(Integer.MAX_VALUE);
                tc.setPreferredWidth(width);
                tc.setWidth(width);
            } else {
                tc.setMaxWidth(0);
                tc.setMinWidth(0);
                tc.setPreferredWidth(0);
                tc.setWidth(0);

            }
        }
        table.repaint();
    }
    
    public int getColumnPosition(String propName) {

        for (int i = 0; i < columnSpecs.size(); ++i) {
            String name = columnSpecs.get(i).getMethod();
            if (name.equals(propName)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getColumnPositionEndsWith(String propName) {

        for (int i = 0; i < columnSpecs.size(); ++i) {
            String name = columnSpecs.get(i).getMethod();
            if (name.endsWith(propName)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getColumnPositionStartWith(String propName) {

        for (int i = 0; i < columnSpecs.size(); ++i) {
            String name = columnSpecs.get(i).getMethod();
            if (name.startsWith(propName)) {
                return i;
            }
        }
        return -1;
    }
    
    public void connect() {
        
        // Tableのカラム変更関連イベント
        table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent tcme) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent tcme) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent tcme) {
                int from = tcme.getFromIndex();
                int to = tcme.getToIndex();
                ColumnSpec moved = columnSpecs.remove(from);
                columnSpecs.add(to, moved);
            }

            @Override
            public void columnMarginChanged(ChangeEvent ce) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent lse) {
            }
        });
    }
    
    public void loadProperty() {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnNames.length; i++) {
            if (!first) {
                sb.append(CAMMA);
            } else {
                first = false;
            }
            String name = columnNames[i];
            String method = propNames[i];
            String cls = columnClasses[i].getName();
            String width = String.valueOf(columnWidth[i]);
            sb.append(name).append(CAMMA);
            sb.append(method).append(CAMMA);
            sb.append(cls).append(CAMMA);
            sb.append(width);
        }
        String defaultLine = sb.toString();

        // preference から
        String line = Project.getString(specName, defaultLine);

        // 仕様を保存
        columnSpecs = new ArrayList<ColumnSpec>();
        String[] params = line.split(",");

        // 保存していた名称・メソッド・クラスが同じか調べる
        int len = params.length / 4;
        // 項目数が同じか？
        boolean same = len == columnNames.length;
        // 各項目は同じか
        if (same) {
            List<String> savedColumns = new ArrayList<String>();
            List<String> savedProps = new ArrayList<String>();
            List<String> savedClasses = new ArrayList<String>();
            for (int i = 0; i < len; ++i) {
                int k = 4 * i;
                savedColumns.add(params[k]);
                savedProps.add(params[k + 1]);
                savedClasses.add(params[k + 2]);
            }
            for (int i = 0; i < len; ++i) {
                savedColumns.remove(columnNames[i]);
                savedProps.remove(propNames[i]);
                savedClasses.remove(columnClasses[i].getName());
            }
            // 同じならば空のはず
            same &= savedColumns.isEmpty() && savedProps.isEmpty() && savedClasses.isEmpty();
        }
        // 保存していた情報数が現在と違う場合は破棄
        if (!same) {
            params = defaultLine.split(",");
            len = columnNames.length;
        }
        
        // columnSpecリストを作成する
        for (int i = 0; i < len; i++) {
            int k = 4 * i;
            String name = params[k];
            String method = params[k + 1];
            String cls = params[k + 2];
            int width = 50;
            try {
                width = Integer.parseInt(params[k + 3]);
            } catch (Exception ex) {
            }
            ColumnSpec cp = new ColumnSpec(name, method, cls, width);
            columnSpecs.add(cp);
        }
    }
    
    public void saveProperty() {
        
        if (columnSpecs == null) {
            return;
        }

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnSpecs.size(); i++) {
            if (!first) {
                sb.append(CAMMA);
            } else {
                first = false;
            }
            ColumnSpec cs = columnSpecs.get(i);
            cs.setWidth(table.getColumnModel().getColumn(i).getWidth());
            sb.append(cs.getName()).append(CAMMA);
            sb.append(cs.getMethod()).append(CAMMA);
            sb.append(cs.getCls()).append(CAMMA);
            sb.append(cs.getWidth());
        }
        String line = sb.toString();
        Project.setString(specName, line);

    }
    
    public JMenu createMenuItem() {

        JMenu menu = new JMenu("表示カラム");
        for (ColumnSpec cs : columnSpecs) {
//minagawa^ lsctest 全てのカラムを非表示にする人がいるため
            if (cs.getName().equals("受付")){
                continue;
            }
//minagawa$            
            final MyCheckBoxMenuItem cbm = new MyCheckBoxMenuItem(cs.getName());
            cbm.setColumnSpec(cs);
            if (cs.getWidth() != 0) {
                cbm.setSelected(true);
            }
            cbm.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (cbm.isSelected()) {
                        cbm.getColumnSpec().setWidth(50);
                    } else {
                        cbm.getColumnSpec().setWidth(0);
                    }
                    updateColumnWidth();
                }
            });
            menu.add(cbm);
        }
        return menu;
    }
            
    private class MyCheckBoxMenuItem extends JCheckBoxMenuItem {
        
        private ColumnSpec cs;
        
        private MyCheckBoxMenuItem(String name) {
            super(name);
        }
        
        private void setColumnSpec(ColumnSpec cs) {
            this.cs = cs;
        }
        private ColumnSpec getColumnSpec() {
            return cs;
        }
    }
}
