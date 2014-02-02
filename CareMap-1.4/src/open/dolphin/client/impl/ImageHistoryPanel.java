
package open.dolphin.client.impl;

import java.beans.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import open.dolphin.client.*;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.SchemaModel;

/**
 * ImageHistoryPanel
 * 
 * @author Kazushi Minagawa
 */
public class ImageHistoryPanel extends JPanel implements PropertyChangeListener {
    
    private String pid;
    private CareMapDocument myParent;
    private ImageTableModel tModel;
    private JTable table;
    private int columnCount = 5;
    private int imageWidth = 132;
    private int imageHeight = 132;
    private javax.swing.Timer taskTimer;

    public ImageHistoryPanel() {

        super(new BorderLayout());

        tModel = new ImageTableModel();
        table = new JTable(tModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn column = null;
        for (int i = 0; i < columnCount; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(imageWidth);
        }
        table.setRowHeight(imageHeight + 20);

        ImageRenderer imageRenderer = new ImageRenderer();
        imageRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(java.lang.Object.class, imageRenderer);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                Point loc = e.getPoint();
                int row = table.rowAtPoint(loc);
                int col = table.columnAtPoint(loc);
                if (row != -1 && col != -1) {
                    openImage(row, col);
                }
            }
        });

        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller, BorderLayout.CENTER);
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String val) {
        pid = val;
    }

    public void setMyParent(CareMapDocument doc) {
        myParent = doc;
    }

    @SuppressWarnings("unchecked")
    public void setImageList(List allImages) {

        if (allImages != null) {

            int size = allImages.size();
            List list = new ArrayList();

            for (int i = 0; i < size; i++) {
                ArrayList l = (ArrayList) allImages.get(i);
                if (l != null) {
                    for (int j = 0; j < l.size(); j++) {
                        list.add(l.get(j));
                    }
                }
            }
            tModel.setImageList(list);
        } else {
            tModel.setImageList(allImages);
        }
    }

    public void propertyChange(PropertyChangeEvent e) {

        String prop = e.getPropertyName();

        if (prop.equals(CareMapDocument.SELECTED_DATE_PROP)) {

            String date = (String) e.getNewValue();
            // if (isMyCode()) {
            // System.out.println("my propertyChange: " + date);
            findDate(date);
        // }
        }

    }

    private void findDate(String date) {
        int index = tModel.findDate(date);
        if (index != -1) {
            int row = index / columnCount;
            int col = index % columnCount;
            table.setRowSelectionInterval(row, row);
            table.setColumnSelectionInterval(col, col);
        }
    }

    /*private boolean isMyCode() {
    // return markEvent.equals(imageCode) ? true : false;
    return markEvent.equals("image") ? true : false;
    }*/
    private void openImage(int row, int col) {

        ImageEntry entry = (ImageEntry) tModel.getValueAt(row, col);
        final ImageSearchSpec spec = new ImageSearchSpec();
        spec.setCode(ImageSearchSpec.ID_SEARCH);
        spec.setId(entry.getId());
        final DocumentDelegater ddl = new DocumentDelegater();
        
        DBTask task = new DBTask<SchemaModel, Void>(myParent.getContext()) {
            
            @Override
            public SchemaModel doInBackground() throws Exception {
                return ddl.getImage(spec.getId());
            }
            
            @Override
            public void succeeded(SchemaModel result) {
                openDialog(result);
            }
        };

        task.execute();
    }

    private void openDialog(SchemaModel schema) {

    }

    protected class ImageTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -2683619747572366737L;
        private List imageList;

        public int getColumnCount() {
            return columnCount;
        }

        public int getRowCount() {
            if (imageList == null) {
                return 0;
            }

            int size = imageList.size();
            int rowCount = size / columnCount;

            return ((size % columnCount) != 0) ? rowCount + 1 : rowCount;
        }

        public Object getValueAt(int row, int col) {
            int index = row * columnCount + col;
            if (!isValidIndex(index)) {
                return null;
            }

            ImageEntry s = (ImageEntry) imageList.get(index);
            return (Object) s;
        }

        public void setImageList(List list) {
            if (imageList != null) {
                int last = getRowCount();
                imageList.clear();
                fireTableRowsDeleted(0, last);
            }
            imageList = list;
            int last = getRowCount();
            fireTableRowsInserted(0, last);
        }

        private int findDate(String date) {

            int ret = -1;

            if (imageList == null) {
                return ret;
            }

            int size = imageList.size();
            for (int i = 0; i < size; i++) {
                ImageEntry entry = (ImageEntry) imageList.get(i);
                if (entry.getConfirmDate().startsWith(date)) {
                    ret = i;
                    break;
                }
            }
            return ret;
        }

        private boolean isValidIndex(int index) {
            return (imageList == null || index < 0 || index >= imageList.size()) ? false
                    : true;
        }
    }

    protected class ImageRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = -8136363583689791913L;

        public ImageRenderer() {
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalTextPosition(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean isFocused, int row,
                int col) {
            Component compo = super.getTableCellRendererComponent(table, value,
                    isSelected, isFocused, row, col);
            JLabel l = (JLabel) compo;

            if (value != null) {

                ImageEntry entry = (ImageEntry) value;
                l.setIcon(entry.getImageIcon());
                // String title = entry.getTitle();
                // if (title != null) {
                // l.setText(title);

                // } else {
                l.setText(entry.getConfirmDate().substring(0, 10));
            // }

            } else {
                l.setIcon(null);
            }
            return compo;
        }
    }
}
