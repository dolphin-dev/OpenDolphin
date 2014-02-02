package open.dolphin.impl.scheam.schemahelper;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * @author Minagawa,Kazushi
 */
public class ShapeIconMaker {
    
    public static ImageIcon createRectFillIcon(Color color, Dimension size) {
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_BGR);
        Rectangle2D rect = new Rectangle2D.Double(0, 0, size.width, size.height);
        Graphics2D g2 = image.createGraphics();
        g2.setPaint(color);
        g2.fill(rect);
        return new ImageIcon(image);
    }
    
    public static ImageIcon createCircleFillIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_BGR);
        Ellipse2D rect = new Ellipse2D.Double(0, 0, size, size);
        Graphics2D g2 = image.createGraphics();
        g2.setPaint(color);
        g2.fill(rect);
        return new ImageIcon(image);
    }

    /**
     * icon からカーソルを作る
     * @param icon
     * @return
     */
    public static Cursor createIconCursor(ImageIcon icon) {
        // cursor　icon は 32x32 で作る
        return Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), new Point(16, 16), "");
    }
}
