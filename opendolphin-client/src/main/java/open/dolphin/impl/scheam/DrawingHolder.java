package open.dolphin.impl.scheam;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface DrawingHolder {
    
    public boolean contains(Point p);
    
    public void draw(Graphics2D g2);
    
    public void translate(double x, double y);

}
