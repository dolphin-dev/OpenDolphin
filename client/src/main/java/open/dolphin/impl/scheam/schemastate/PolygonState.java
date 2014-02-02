package open.dolphin.impl.scheam.schemastate;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class PolygonState extends AbstractState {

    private Stroke outlineStroke = properties.getOutlineStroke();

    public PolygonState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        start = p;
        end = null;
        first = true;
        shape = new GeneralPath();
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        if (first) {
            ((GeneralPath)shape).moveTo(start.x, start.y);
            ((GeneralPath)shape).lineTo(end.x, end.y);
            first = false;
        } else {
            ((GeneralPath)shape).lineTo(end.x, end.y);
        }
        canvas.repaint();
        start = end;
    }

    @Override
    public void mouseUp(Point p) {

        if (shape.getBounds().width != 0 && shape.getBounds().height != 0) {

            undoMgr.storeDraw();
            ((GeneralPath)shape).closePath();
            addAreaShape((GeneralPath)shape);
        }
        // null にしておかないと undo の時などに描画されてしまう
        shape = null;
        canvas.repaint();
    }

    /**
     * polygon の時は，途中経過も線の方がわかりやすい
     * @param g2d
     */
    @Override
    public void draw(Graphics2D g2d) {

        if (shape != null) {
            if (properties.isFill()) g2d.setStroke(outlineStroke);
            g2d.draw(shape);
        }
    }
}
