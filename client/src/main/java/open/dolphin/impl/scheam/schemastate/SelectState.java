package open.dolphin.impl.scheam.schemastate;

import java.awt.Point;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 *
 * @author pns
 */
public class SelectState extends AbstractState {

    private DrawingHolder moving;
    private Point temp; // ドラッグ途中で使う

    public SelectState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        moving = findDrawing(p);
        if (moving != null) {
            start = p;
            temp = p;
            end = null;
        }
    }

    @Override
    public void mouseDragged(Point p) {
        if (moving != null) {
            end = p;
            moving.translate(end.getX() - temp.getX(), end.getY() - temp.getY());
            canvas.repaint();
            temp = end;
        }
    }

    @Override
    public void mouseUp(Point p) {
        if (moving != null) {
            undoMgr.storeMove(moving, end.getX() - start.getX(), end.getY() - start.getY());
        }
    }

    /**
     * マウス位置の DrawingHolder を返す
     * @param p
     * @return
     */
    public DrawingHolder findDrawing(Point p) {
        DrawingHolder found = null;
        int cnt = drawingList.size();
        if (cnt > 0) {
            for (int i = cnt; i > 0; i--) {
                DrawingHolder d = drawingList.get(i-1);
                if (d.contains(p)) {
                    found = d;
                    break;
                }
            }
        }
        return found;
    }
}
