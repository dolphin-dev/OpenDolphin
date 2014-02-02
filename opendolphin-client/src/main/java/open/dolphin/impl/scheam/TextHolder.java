package open.dolphin.impl.scheam;

import java.awt.AlphaComposite;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Kazushi Minagawa.
 */
public class TextHolder extends AreaHolder {
    
    public TextHolder(Area area, Stroke stroke, Paint paint, AlphaComposite ac, boolean fill) {
        super(area, stroke, paint, ac, fill);
    }
    
    @Override
    public boolean contains(Point p) {
        Rectangle2D r = getArea().getBounds2D();
        return r.contains(p);
    }
}
