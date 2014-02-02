package open.dolphin.impl.scheam.holder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 *
 * @author pns
 */
public class PathHolder implements DrawingHolder {
    
    private GeneralPath path;
    private Stroke stroke;
    private Paint paint;
    private AlphaComposite ac;
    private boolean fill = false;
    

    public PathHolder() {
    }
    
    public PathHolder(GeneralPath path, Stroke stroke, Paint paint, AlphaComposite ac) {
        this.path = path;
        this.stroke = stroke;
        this.paint = paint;
        this.ac = ac;
    }
    
    public Shape getPath() {
        return this.path;
    }

    public void setPath(GeneralPath path) {
        this.path = path;
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
    
    public boolean contains(Point p) {
        PathIterator iter = path.getPathIterator(null);
        double[] coords = new double[6];
        int seg;
        double x; double y;

        // segment の各点のうち，p と 5 ドット未満に近いものがあれば contains と判断する
        while(!iter.isDone()) {
            seg = iter.currentSegment(coords);
            if (seg == PathIterator.SEG_MOVETO || seg == PathIterator.SEG_LINETO) {
                x = coords[0];
                y = coords[1];
                if (-5 < (p.x - x) && (p.x - x) < 5 && -5 < (p.y - y) && (p.y - y) < 5) {
                    return true;
                }
            }
            iter.next();
        }
        return false;
    }
    
    public void draw(Graphics2D g2d) {
                
        g2d.setComposite(ac);
        g2d.setPaint(paint);
        g2d.setStroke(stroke);

        g2d.draw(path);
    }
    
    public void translate(double x, double y) {
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        path.transform(trans);
    }
    
    public void rotate(double theta) {
        AffineTransform rotate = AffineTransform.getRotateInstance(theta);
        path.transform(rotate);
    }

    public void expand(double sx, double sy) {
        // stroke の拡大
        BasicStroke s = (BasicStroke) stroke;
        float w = (float) (s.getLineWidth() * sx); // 常に sx = sy なので
        stroke = new BasicStroke( w, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());

        // 形状の拡大
        AffineTransform expand = AffineTransform.getScaleInstance(sx, sy);
        path.transform(expand);
    }
}
