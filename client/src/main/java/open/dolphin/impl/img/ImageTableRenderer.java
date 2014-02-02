package open.dolphin.impl.img;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.helper.ImageHelper;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class ImageTableRenderer extends DefaultTableCellRenderer {

    private AbstractBrowser context;
    private int imageSize = 120;

    public ImageTableRenderer(AbstractBrowser context) {
        this.context = context;
        setHorizontalAlignment(SwingConstants.CENTER);
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
        String fileName = null;
//        String ext;
        
//        boolean even = ((row & (1))==0);
//        int rowHeight = even ? 20 : 140;

        if (value!=null) {

            ImageEntry entry = (ImageEntry)value;
            
            if (context.showFilename()) {
                fileName = context.displayIsFilename()
                        ? entry.getFileName()
                        : getLastModified(entry.getLastModified());
            }

            //fileName = (even) ? getLastModified(entry.getLastModified()):entry.getFileName();
            //icon = (even) ? null : entry.getImageIcon();
            icon = entry.getImageIcon();
//            ext = context.getSuffix(entry.getFileName());
            
//            if (icon==null && context.isImage(ext)) {
//                try {
//                    BufferedImage image = null;
//                    if (ext.endsWith("dcm")) {
//                        //image = dicomToImage(entry.getPath());
//                        //image = ImageIO.read(new File(entry.getPath()));
//                        //DicomThumbnailMaker maker = new DicomThumbnailMaker();
//                        //image = maker.read(entry.getPath());
//                    }
//                    else {
//                        image = ImageIO.read(new File(entry.getPath()));
//                    }
//                    //image = ImageIO.read(new File(entry.getPath()));
//                    if (image!=null) {
//                        image = ImageHelper.getFirstScaledInstance(image, getImageSize());
//                        icon = new ImageIcon(image);
//                        entry.setImageIcon(icon);
//                    }
//                } catch (FileNotFoundException ex) {
//                    ex.printStackTrace(System.err);
//                } catch (IOException ex) {
//                    ex.printStackTrace(System.err);
//                } catch (Throwable e) {
//                    e.printStackTrace(System.err);
//                }
//
//            } else if (icon==null) {
//                int index = context.isDocument(ext);
//                String iconStr = index>=0 ? AbstractBrowser.ACCEPT_DOC_ICONS[index] : null;
//                icon = iconStr!=null ? ClientContext.getImageIcon(iconStr) : null;
//                if (icon==null) {
//                    icon = ClientContext.getImageIcon(AbstractBrowser.DEFAULT_DOC_ICON);
//                }
//                entry.setImageIcon(icon);
//            }
        }
        l.setIcon(icon);
        l.setText(fileName);
        //table.setRowHeight(row, rowHeight);
        return compo;
    }
    
    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    private String getLastModified(long l) {
        return new SimpleDateFormat(AbstractBrowser.DATE_FORMAT).format(new Date(l));
    }

    private BufferedImage dicomToImage(String dcmFile) throws IOException,Exception {
        BufferedImage image = null;
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("dcm");
        if (readers.hasNext()) {
            ImageReader reader = (ImageReader)readers.next();
            //DicomThumbnailMaker maker = new DicomThumbnailMaker();
            //byte[] bytes = maker.make(dcmFile);
            //reader.setInput(new ByteArrayInputStream(bytes), true);
            image = reader.read(0, null);
        }
//        return image;
//        DicomThumbnailMaker maker = new DicomThumbnailMaker();
//        byte[] bytes = maker.make( f.getPath());
//        image = ImageIO.read(new ByteArrayInputStream(bytes));
        return image;
    }
}
