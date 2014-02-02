package open.dolphin.impl.scheam.schemastate;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class RectState extends AbstractState {

    public RectState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        shape = new Rectangle2D.Double();
        start = p;
        end = null;
        first = true;
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        ((Rectangle2D) shape).setFrameFromDiagonal(start, end);
        canvas.repaint();
    }

    @Override
    public void mouseUp(Point p) {

        if (shape.getBounds().width != 0 && shape.getBounds().height != 0){
            undoMgr.storeDraw();
            addAreaShape((Rectangle2D)shape);
            shape = null;
        } else {
            shape = null;
            canvas.repaint();
        }
    }
}
