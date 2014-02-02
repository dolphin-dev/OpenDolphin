package open.dolphin.impl.scheam.holder;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author kazm
 */
public interface DrawingHolder {
    
    public boolean contains(Point p);
    
    public void draw(Graphics2D g2);
    
    public void translate(double x, double y);

    public void rotate(double theta);

    public void expand(double sx, double sy);
   
}
