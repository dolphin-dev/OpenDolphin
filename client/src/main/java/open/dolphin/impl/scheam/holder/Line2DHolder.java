package open.dolphin.impl.scheam.holder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
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
    
    public void draw(Graphics2D g2d) {
        
        g2d.setStroke(stroke); 
        g2d.setPaint(paint);
        g2d.setComposite(ac);
        g2d.draw(line2D);
    }
    
    public void translate(double x, double y) {
        
        double x1 = line2D.getX1() + x;
        double y1 = line2D.getY1() + y;
        double x2 = line2D.getX2() + x;
        double y2 = line2D.getY2() + y;
        
        line2D.setLine(x1, y1, x2, y2);
    }
/*
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
*/
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

    private void transform(AffineTransform trans) {
        double[] coords = new double[6];
        double x1=0; double y1=0; double x2=0; double y2=0;

        PathIterator iter = line2D.getPathIterator(trans);

        while(!iter.isDone()) {
            int ret = iter.currentSegment(coords);
            if (ret == PathIterator.SEG_MOVETO) {
                x1 = coords[0];
                y1 = coords[1];
            } else if (ret == PathIterator.SEG_LINETO) {
                x2 = coords[0];
                y2 = coords[1];
            }
            iter.next();
        }
        line2D.setLine(x1, y1, x2, y2);
    }

    public void rotate(double theta) {
        //WIND_EVEN_ODD	= 0;
        //WIND_NON_ZERO	= 1;
        //SEG_MOVETO		= 0;
        //SEG_LINETO		= 1;
        //SEG_QUADTO		= 2;
        //SEG_CUBICTO		= 3;
        //SEG_CLOSE		= 4;

        AffineTransform trans = AffineTransform.getRotateInstance(theta);
        transform(trans);
    }

    public void expand(double sx, double sy) {
        AffineTransform expand = AffineTransform.getScaleInstance(sx, sy);
        transform(expand);
    }
}
