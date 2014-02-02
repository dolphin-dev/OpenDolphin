package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * ImagePalette
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 */
public class ImagePalette extends JPanel implements DragSourceListener, DragGestureListener {
    
    private static final int DEFAULT_COLUMN_COUNT 	=   3;
    private static final int DEFAULT_IMAGE_WIDTH 	= 120;
    private static final int DEFAULT_IMAGE_HEIGHT 	= 120;
    private static final String[] DEFAULT_IMAGE_SUFFIX = {".jpg"};
    private static final int RES_COUNT = 57;
    private static final String RES_EXTENTION = ".JPG";
    private static final String RES_PREFIX = "img";
    private static final String RES_BASE = "/open/dolphin/resources/schema/";
    
    private ImageTableModel imageTableModel;
    private int imageWidth;
    private int imageHeight;
    private JTable imageTable;
    private DragSource dragSource;
    private File imageDirectory;
    private String[] suffix = DEFAULT_IMAGE_SUFFIX;
    private boolean showHeader;

    
    public ImagePalette(String[] columnNames, int columnCount, int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        imageTableModel = new ImageTableModel(columnNames, columnCount);
        initComponent(columnCount);
        connect();
    }
    
    public ImagePalette() {
        this(null, DEFAULT_COLUMN_COUNT, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }
    
    public List getImageList() {
        return imageTableModel.getImageList();
    }
    
    public void setImageList(ArrayList list) {
        imageTableModel.setImageList(list);
    }
    
    public JTable getable() {
        return imageTable;
    }
    
    public String[] getimageSuffix() {
        return suffix;
    }
    
    public void setImageSuffix(String[] suffix) {
        this.suffix = suffix;
    }

    public void setupDefaultSchema() {
        ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
        for (int j= 1; j <= RES_COUNT; j++) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(RES_BASE);
                sb.append(RES_PREFIX);
                if (j < 10) {
                    sb.append("0");
                }
                sb.append(j).append(RES_EXTENTION);
                URL url = this.getClass().getResource(sb.toString());
                ImageEntry entry = new ImageEntry();
                entry.setUrl(url.toString());
                imageList.add(entry);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        imageTableModel.setImageList(imageList);
    }
    
    public File getImageDirectory() {
        return imageDirectory;
    }
    
    public void setImageDirectory(File imageDirectory) {
        this.imageDirectory = imageDirectory;
        refresh();
    }
    
    public void dispose() {
        if (imageTableModel != null) {
            imageTableModel.clear();
        }
    }
    
    public void refresh() {
        
        if (imageDirectory==null || 
           (!imageDirectory.exists()) ||
           (!imageDirectory.isDirectory())) {
            return;
        }

        File[] imageFiles = listImageFiles(imageDirectory, suffix);
        if (imageFiles != null && imageFiles.length > 0) {
            ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
            for (int j= 0; j < imageFiles.length; j++) {
                try {
                    URL url = imageFiles[j].toURI().toURL();
                    ImageEntry entry = new ImageEntry();
                    entry.setUrl(url.toString());
                    imageList.add(entry);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            imageTableModel.setImageList(imageList);
        }
    }
    
    private void initComponent(int columnCount) {
        
        // Image table を生成する
        imageTable = new JTable(imageTableModel);
        imageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageTable.setCellSelectionEnabled(true);
        imageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        TableColumn column = null;
        for (int i = 0; i < columnCount; i++) {
            column = imageTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(imageWidth);
        }
        imageTable.setRowHeight(imageHeight);
        
        ImageRenderer imageRenderer = new ImageRenderer();
        imageRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        imageTable.setDefaultRenderer(java.lang.Object.class, imageRenderer);
        
        this.setLayout(new BorderLayout());
        if (showHeader) {
            this.add(new JScrollPane(imageTable));
        } else {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(imageTable);
            this.add(new JScrollPane(panel));
        }
    }
    
    private void connect() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(imageTable, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }
    
    private File[] listImageFiles(File dir, String[] suffix) {
        ImageFileFilter filter = new ImageFileFilter(suffix);
        return dir.listFiles(filter);
    }
    
    class ImageFileFilter implements FilenameFilter {
        
        private String[] suffix;
        
        public ImageFileFilter(String[] suffix) {
            this.suffix = suffix;
        }
        
        @Override
        public boolean accept(File dir, String name) {
            
            boolean accept = false;
            for (int i = 0; i < suffix.length; i++) {
                if (name.toLowerCase().endsWith(suffix[i])) {
                    accept = true;
                    break;
                }
            }
            return accept;
        }
    }
    
    @Override
    public void dragDropEnd(DragSourceDropEvent event) {
    }
    
    @Override
    public void dragEnter(DragSourceDragEvent event) {
    }
    
    @Override
    public void dragOver(DragSourceDragEvent event) {
    }
    
    @Override
    public void dragExit(DragSourceEvent event) {
    }
    
    @Override
    public void dropActionChanged( DragSourceDragEvent event) {
    }
    
    @Override
    public void dragGestureRecognized(DragGestureEvent event) {
        
        try {
            int row = imageTable.getSelectedRow();
            int col = imageTable.getSelectedColumn();
            if (row != -1 && col != -1) {
                ImageEntry entry = (ImageEntry)imageTable.getValueAt(row, col);
                Transferable t = new ImageEntryTransferable(entry);
                dragSource.startDrag(event, DragSource.DefaultCopyDrop, t, this);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    protected class ImageRenderer extends DefaultTableCellRenderer {
        
        public ImageRenderer() {
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalTextPosition(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component compo = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused,
                    row, col);
            JLabel l = (JLabel)compo;

            ImageIcon icon = null;

            if (value!=null) {
                ImageEntry entry = (ImageEntry)value;
                try {
                    URL url = new URL(entry.getUrl());
                    ImageIcon ic = new ImageIcon(url);
                    icon = adjustImageSize(ic, imageWidth, imageHeight);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace(System.err);
                }
            }
            l.setIcon(icon);
            l.setText(null);
            return compo;
        }
    }

    private ImageIcon adjustImageSize(ImageIcon icon, int width, int height) {

        if ( (icon.getIconHeight() > height) || (icon.getIconWidth() > width) ) {
            Image img = icon.getImage();
            float hRatio = (float)icon.getIconHeight() / height;
            float wRatio = (float)icon.getIconWidth() / width;
            int h, w;
            if (hRatio > wRatio) {
                h = height;
                w = (int)(icon.getIconWidth() / hRatio);
            } else {
                w = width;
                h = (int)(icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);

        } else {
            return icon;
        }
    }
}