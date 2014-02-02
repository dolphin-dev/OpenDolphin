package open.dolphin.impl.scheam.schemahelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author pns
 */
public class SchemaUtils {
    /**
     * Color -> "255,255,255" 変換
     * @param color
     * @return string
     */
    public static String colorToString(Color color) {
        StringBuilder buf = new StringBuilder();
        buf.append(String.valueOf(color.getRed()));
        buf.append(",");
        buf.append(String.valueOf(color.getGreen()));
        buf.append(",");
        buf.append(String.valueOf(color.getBlue()));
        return buf.toString();
    }
    /**
     * "255,255,255" -> Color 変換
     * @param string
     * @return color
     */
    public static Color stringToColor(String str) {
        String[] rgb = str.split("\\s*,\\s*");
        int r = Integer.parseInt(rgb[0]);
        int g = Integer.parseInt(rgb[1]);
        int b = Integer.parseInt(rgb[2]);
        Color c = new Color(r, g, b);
        return c;
    }
    /**
     * Rectangle -> "x,y,width,height" 変換
     * @param r
     * @return
     */
    public static String rectangleToString(Rectangle r) {
        StringBuilder buf = new StringBuilder();
        buf.append(String.valueOf(r.x));
        buf.append(",");
        buf.append(String.valueOf(r.y));
        buf.append(",");
        buf.append(String.valueOf(r.width));
        buf.append(",");
        buf.append(String.valueOf(r.height));
        return buf.toString();
    }
    /**
     * "x,y,width,height" -> Rectangle 変換
     * @param str
     * @return
     */
    public static Rectangle stringToRectangle(String str) {
        if (str == null) return null;

        String[] r = str.split("\\s*,\\s*");
        int x = Integer.parseInt(r[0]);
        int y = Integer.parseInt(r[1]);
        int width = Integer.parseInt(r[2]);
        int height = Integer.parseInt(r[3]);
        return new Rectangle(x, y, width, height);
    }
    /**
     * Image から BufferedImage に変換
     * @param src
     * @return
     */
    public static BufferedImage imageToBufferedImage(Image src) {
        int width = 0;
        int height = 0;
        
        if (src != null) {
            width = src.getWidth(null);
            height = src.getHeight(null);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        
        return image;
    }
    /**
     * ImageIcon から BufferedImage に変換
     * @param src
     * @return
     */
    public static BufferedImage imageToBufferedImage(ImageIcon src) {
        return imageToBufferedImage(src.getImage());
    }
    /**
     * shape の bound と p が n ドット以内にいるかどうか判定
     * @param shape
     * @param p
     * @return
     */
    public static boolean isNear(Shape shape, Point p, int n) {
        Rectangle r = shape.getBounds();
        Rectangle expanded = new Rectangle(r.x - n, r.y - n, 2*n + r.width, 2*n + r.height);
        return expanded.contains(p);
    }

}
