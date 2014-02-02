package open.dolphin.impl.scheam.schemastate;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class EllipseState extends AbstractState {

    public EllipseState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        shape = new Ellipse2D.Double();
        start = p;
        end = null;
        first = true;
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        ((Ellipse2D)shape).setFrameFromDiagonal(start, end);
        canvas.repaint();
    }

    @Override
    public void mouseUp(Point p) {

        if (shape.getBounds().width != 0 && shape.getBounds().height != 0) {
            undoMgr.storeDraw();
            addAreaShape((Ellipse2D) shape);
            shape = null;
        } else {
            shape = null;
            canvas.repaint();
        }
    }
}
