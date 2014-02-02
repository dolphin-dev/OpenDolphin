package open.dolphin.impl.img;


import java.util.List;
import javax.swing.table.AbstractTableModel;
import open.dolphin.client.ImageEntry;


/**
 * ImageTableModel
 *
 * @author Minagawa, Kazushi. Digital Globe, Inc.
 */
public class ImageTableModel extends AbstractTableModel {
    
    private String[] columnNames;
    private int columnCount;
    private List imageList;
    
    public ImageTableModel(String[] columnNames, int columnCount) {
        this.columnNames = columnNames;
        this.columnCount = columnCount;
    }
    
    @Override
    public String getColumnName(int col) {
        return (columnNames != null && col < columnNames.length) ? columnNames[col] : null;
    }
    
    @Override
    public int getColumnCount() {
        return columnCount;
    }
    
    @Override
    public int getRowCount() {
        
        if (imageList == null) {
            return 0;
        }
        
        int size = imageList.size();
        int rowCount = size / columnCount;
        
        return ( (size % columnCount) != 0 ) ? rowCount + 1 : rowCount;
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        int index = row * columnCount + col;
        if (!isValidIndex(index)) {
            return null;
        }
        
        ImageEntry entry = (ImageEntry)imageList.get(index);
        return (Object)entry;
    }
    
    public void setImageList(List list) {
        if (imageList != null) {
            imageList.clear();
            imageList = null;
        }
        imageList = list;
        this.fireTableDataChanged();
    }
    
    public List getImageList() {
        return imageList;
    }
    
    private boolean isValidIndex(int index) {
        return (imageList == null || index < 0 || index >= imageList.size()) ? false : true;
    }
    
    public void clear() {
        if (imageList != null) {
            imageList.clear();
            this.fireTableDataChanged();
        }
    }
}
