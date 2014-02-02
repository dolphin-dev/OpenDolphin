package open.dolphin.impl.scheam.schemastate;

import java.awt.Point;
import java.awt.geom.Line2D;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class LineState extends AbstractState {

    public LineState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        shape = new Line2D.Double();
        start = p;
        end = null;
        first = true;
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        ((Line2D.Double)shape).setLine(start, end);
        canvas.repaint();
    }

    @Override
    public void mouseUp(Point p) {
        if (end == null) {
            shape = null;
            return;
        }
        undoMgr.storeDraw();
        
        addLineShape((Line2D.Double)shape);
        shape = null;
    }
}
