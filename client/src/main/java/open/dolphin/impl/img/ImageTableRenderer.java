package open.dolphin.impl.img;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class ImageTableRenderer extends DefaultTableCellRenderer {

    private final AbstractBrowser context;
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
        
        if (value!=null) {

            ImageEntry entry = (ImageEntry)value;
            
            if (context.showFilename()) {
                fileName = context.displayIsFilename()
                        ? entry.getFileName()
                        : getLastModified(entry.getLastModified());
            }
            icon = entry.getImageIcon();
        }
        l.setIcon(icon);
        l.setText(fileName);
        return compo;
    }
    
    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    private String getLastModified(long l) {
        String dateFmt = ClientContext.getMyBundle(AbstractBrowser.class).getString("dateFormat.imageBrowser");
        return new SimpleDateFormat(dateFmt).format(new Date(l));
    }
}
