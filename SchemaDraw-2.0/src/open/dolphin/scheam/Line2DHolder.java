package open.dolphin.scheam;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Minagawa, Kazushi
 */
public class Line2DHolder implements DrawingHolder {
    
    private Line2D.Double line2D;
    private Stroke stroke;
    private Paint paint;
    private AlphaComposite ac;
    private boolean p1;
    private boolean p2;
    
    
    public Line2DHolder(Line2D.Double line2D, Stroke stroke, Paint paint, AlphaComposite ac) {
        this.line2D = line2D;
        this.stroke = stroke;
        this.paint = paint;
        this.ac = ac;
    }
    
    @Override
    public boolean contains(Point p) {
        
        p1 = false;
        p2 = false;
        
        Rectangle2D r = getRectangle2D(p);
        
        if (isAtP1(r)) {
            p1 = true;
            return true;
        }
        
        if (isAtP2(r)) {
            p2 = true;
            return true;
        }
        
        if (line2D.intersects(r)) {
            return true;
        }
        return false;
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        
        Stroke saveStroke = g2d.getStroke();
        Paint savePaint = g2d.getPaint();
        Composite saveComposite = g2d.getComposite();
        
        g2d.setStroke(stroke); 
        g2d.setPaint(paint);
        g2d.setComposite(ac);
        g2d.draw(line2D);
        
        g2d.setStroke(saveStroke); 
        g2d.setPaint(savePaint);
        g2d.setComposite(saveComposite);
    }
    
    @Override
    public void translate(double x, double y) {
        
        double x1 = line2D.getX1();
        double y1 = line2D.getY1();
        double x2 = line2D.getX2();
        double y2 = line2D.getY2();
        
        if (p1) {
            x1 = line2D.getX1() + x;
            y1 = line2D.getY1() + y;
        } else if (p2) {
            x2 = line2D.getX2() + x;
            y2 = line2D.getY2() + y;
        } else {
            x1 = line2D.getX1() + x;
            y1 = line2D.getY1() + y;
            x2 = line2D.getX2() + x;
            y2 = line2D.getY2() + y;
        }
        
        line2D.setLine(x1, y1, x2, y2);
    }
    
    private boolean isAtP1(Rectangle2D r) {
        return r.contains(line2D.getP1());
    }
    
    private boolean isAtP2(Rectangle2D r) {
        return r.contains(line2D.getP2());
    }
    
    private Rectangle2D getRectangle2D(Point p) {
        Rectangle2D r = new Rectangle2D.Double();
        r.setFrameFromDiagonal(p.getX() - 3,p.getY() -3, p.getX() +3, p.getY() + 3);
        return r;
    }
}
