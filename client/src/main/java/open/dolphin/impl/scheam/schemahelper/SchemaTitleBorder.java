package open.dolphin.impl.scheam.schemahelper;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class SchemaTitleBorder extends AbstractBorder {

    private static String ICON16 = "/open/dolphin/impl/scheam/resources/Frame.titlePane.small.png";

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        // ImageIcon って便利
        ImageIcon icon = new ImageIcon(SchemaEditorImpl.class.getResource(ICON16));
        BufferedImage buf = SchemaUtils.imageToBufferedImage(icon);
        TexturePaint paint = new TexturePaint(buf, new Rectangle2D.Double(0, 0, buf.getWidth(), buf.getHeight()));
        g2d.setPaint(paint);
        g2d.fillRect(x, y, width, height);
    }

    @Override
    public Insets getBorderInsets(Component c){
        return new Insets(0,0,0,0);
    }
    
    @Override
    public boolean isBorderOpaque(){
        return false;
    }
}
