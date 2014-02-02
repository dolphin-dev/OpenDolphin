package open.dolphin.impl.scheam.holder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import open.dolphin.impl.scheam.schemahelper.SchemaUtils;

/**
 *
 * @author kazm
 */
public class AreaHolder implements DrawingHolder {
    
    private Area area;
    private Stroke stroke;
    private Paint paint;
    private AlphaComposite ac;
    private boolean fill;

    public AreaHolder() {
    }
    
    public AreaHolder(Area area, Stroke stroke, Paint paint, AlphaComposite ac, boolean fill) {
        this.area = area;
        this.stroke = stroke;
        this.paint = paint;
        this.ac = ac;
        this.fill = fill;
    }
    
    public Shape getArea() {
        return area;
    }

    public void setArea(Area area) {
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
    
    public boolean contains(Point p) {
        // 細すぎるとつかめないので，近くだったらつかめるようにする
        if (area.getBounds().width < 5 || area.getBounds().height < 5) {
            return SchemaUtils.isNear(area, p, 5);
        } else {
            return area.contains(p);
        }
    }
    
    public void draw(Graphics2D g2d) {
        g2d.setStroke(stroke);
        g2d.setComposite(ac);
        g2d.setPaint(paint);
        
        if (fill) g2d.fill(area);
        else g2d.draw(area);
    }
    
    public void translate(double x, double y) {
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
	area.transform(trans);
    }

    public void rotate(double theta) {
        AffineTransform rotate = AffineTransform.getRotateInstance(theta);
        area.transform(rotate);
    }

    public void expand(double sx, double sy) {
        // stroke の拡大
        BasicStroke s = (BasicStroke) stroke;
        float w = (float) (s.getLineWidth() * sx); // 常に sx = sy なので
        stroke = new BasicStroke( w, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());

        // 形状の拡大
        AffineTransform expand = AffineTransform.getScaleInstance(sx, sy);
        area.transform(expand);
    }
}
