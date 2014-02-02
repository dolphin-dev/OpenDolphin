package open.dolphin.impl.scheam;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 *
 * @author Kazushi Minagawa.
 */
public final class OpenPathHolder implements DrawingHolder {
    
    private GeneralPath area;
    private Stroke stroke;
    private Paint paint;
    private AlphaComposite ac;
    private boolean fill;
    

    public OpenPathHolder() {
    }
    
    public OpenPathHolder(GeneralPath area, Stroke stroke, Paint paint, AlphaComposite ac, boolean fill) {
        this();
        setArea(area);
        setStroke(stroke);
        setPaint(paint);
        setAlphaComposite(ac);
        setFill(fill);
    }
    
    public Shape getArea() {
        return area;
    }

    public void setArea(GeneralPath area) {
        this.area = area;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint color) {
        this.paint = color;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    
    public AlphaComposite getAlphaComposite() {
        return ac;
    }
    
    public void setAlphaComposite(AlphaComposite ac) {
        this.ac = ac;
    }
    
    @Override
    public boolean contains(Point p) {
        return area.contains(p.getX(), p.getY());
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        
        Stroke saveStroke = g2d.getStroke();
        Paint savePaint = g2d.getPaint();
        Composite saveComposite = g2d.getComposite();
        
        g2d.setComposite(getAlphaComposite());
        g2d.setPaint(getPaint());
        
        if (isFill()) {
            if (getStroke() != null) {
                g2d.setStroke(getStroke());
            }
            g2d.fill(getArea());
        } else {
            if (getStroke() != null) {
                g2d.setStroke(getStroke());
            }
            g2d.draw(getArea());
        }
        
        g2d.setStroke(saveStroke); 
        g2d.setPaint(savePaint);
        g2d.setComposite(saveComposite);
    }
    
    @Override
    public void translate(double x, double y) {
        AffineTransform trans = AffineTransform.getTranslateInstance(
                x,
                y);
	area.transform(trans);
    }
}
