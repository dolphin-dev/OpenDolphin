
package open.dolphin.client.impl;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import open.dolphin.client.*;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.table.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.beans.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * オーダ履歴を表示するパネルクラス。 表示するオーダと抽出期間は PropertyChange で通知される。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class OrderHistoryPanel extends JPanel implements
        PropertyChangeListener {
    
    /**
     *
     */
    private static final long serialVersionUID = -2302784717739085879L;
    
    private ObjectTableModel tModel;
    
    private JTable table;
    
    private JLabel contents;
    
    private String pid;
    
    //private String markEvent;
    
    //private String startDate;
    
    //private String endDate;
    
    //private CareMapDocument parent;
    
    private Dimension contentSize = new Dimension(240, 300);
    
    /** Creates new OrderHistoryPanel */
    public OrderHistoryPanel() {
        
        super(new BorderLayout(5, 0));
        
        String[] columnNames = ClientContext
                .getStringArray("orderhistory.table.columnNames");
        int startNumRows = 12;
        
        // オーダの履歴(確定日|スタンプ名)を表示する TableModel
        // 各行は ModuleModel
        tModel = new ObjectTableModel(columnNames, startNumRows) {
            
            private static final long serialVersionUID = 1684645192401100170L;
            
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            @Override
            public Object getValueAt(int row, int col) {
                
                ModuleModel module = (ModuleModel) getObject(row);
                if (module == null) {
                    return null;
                }
                ModuleInfoBean info = module.getModuleInfo();
                String ret = null;
                
                switch (col) {
                    
                    case 0:
                        //ret = ModelUtils.getDateAsString(info.getConfirmDate());
                        ret = ModelUtils.getDateAsString(module.getConfirmed());
                        //String val = info.getConfirmDate();
                        //int index = val.indexOf('T');
                        //ret = index > 0 ? val.substring(0, index) : val;
                        break;
                        
                    case 1:
                        ret = info.getStampName();
                        break;
                }
                
                return ret;
            }
        };
        
        table = new JTable(tModel);
        table.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // 行クリックで内容を表示する
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        ListSelectionModel m = table.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int index = table.getSelectedRow();
                    displayOrder(index);
                }
            }
        });
        setColumnWidth(new int[] { 50, 240 });
        
        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroller, BorderLayout.CENTER);
        
        // 内容表示用 TextArea
        contents = new JLabel();
        contents.setBackground(Color.white);
        // contents.setEditable(false);
        // contents.setLineWrap(true);
        // contents.setMargin(new Insets(3,3,3,3));
        JScrollPane cs = new JScrollPane(contents,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cs.setPreferredSize(contentSize);
        cs.setMaximumSize(contentSize);
        add(cs, BorderLayout.EAST);
    }
    
    public void setColumnWidth(int[] columnWidth) {
        int len = columnWidth.length;
        for (int i = 0; i < len; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidth[i]);
        }
    }
    
    public String getPid() {
        return pid;
    }
    
    public void setPid(String val) {
        pid = val;
    }
    
    //public void setParent(CareMapDocument val) {
    //parent = val;
    //}
    
    public void setModuleList(List allModules) {
        
        tModel.clear();
        
        if (allModules == null || allModules.size() == 0) {
            return;
        }
        
        int size = allModules.size();
        ArrayList<Object> list = new ArrayList<Object>();
        
        for (int i = 0; i < size; i++) {
            List l = (List) allModules.get(i);
            if (l != null) {
                for (int j = 0; j < l.size(); j++) {
                    list.add((Object)l.get(j));
                }
            }
        }
        
        tModel.setObjectList(list);
    }
    
    /**
     * カレンダーの日が選択されたときに通知を受け、テーブルで日付が一致するオーダの行を選択する。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals(CareMapDocument.SELECTED_DATE_PROP)) {
            
            String date = (String) e.getNewValue();
            findDate(date);
            // if (isMyCode()) {
            // System.out.println("my propertyChange: " + date);
            // findDate(date);
            // }
        }
    }
    
        /*private boolean isMyCode() {
                return (markEvent.equals("medOrder")
                                || markEvent.equals("treatmentOrder") || markEvent
                                .equals("testOrder")) ? true : false;
        }*/
    
    /**
     * オーダ履歴のテーブル行がクリックされたとき、データモデルの ModuleModel を表示する。
     */
    private void displayOrder(int index) {
        
        contents.setText("");
        
        ModuleModel stamp = (ModuleModel) tModel.getObject(index);
        if (stamp == null) {
            return;
        }
        
        IInfoModel model = stamp.getModel();
        
        try {
            VelocityContext context = ClientContext.getVelocityContext();
            context.put("model", model);
            context.put("stampName", stamp.getModuleInfo().getStampName());
            
            // このスタンプのテンプレートファイルを得る
            String templateFile = stamp.getModel().getClass().getName() + ".vm";
            // debug(templateFile);
            
            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext
                    .getTemplateAsStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    instream, "SHIFT_JIS"));
            Velocity.evaluate(context, bw, "stmpHolder", reader);
            bw.flush();
            bw.close();
            reader.close();
            contents.setText(sw.toString());
            
        } catch (Exception e) {
            System.out.println("Execption while setting the stamp text: "
                    + e.toString());
            e.printStackTrace();
        }
    }
    
    private void findDate(String date) {
        
        // System.out.println("selected date = " + date);
        int size = tModel.getDataSize();
        for (int i = 0; i < size; i++) {
            String rowDate = (String) tModel.getValueAt(i, 0);
            if (rowDate.equals(date)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
}