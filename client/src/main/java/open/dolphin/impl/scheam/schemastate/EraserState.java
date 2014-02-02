package open.dolphin.impl.scheam.schemastate;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.AreaHolder;
import open.dolphin.impl.scheam.holder.PathHolder;

/**
 *
 * @author pns
 */
public class EraserState extends AbstractState {

    private GeneralPath gpath;
    private Stroke eraserStroke = properties.getEraserStroke();

    public EraserState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        start = p;
        end = null;
        first = true;
        gpath = null;
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        if (first) {
            gpath = new GeneralPath();
            gpath.moveTo(start.x, start.y);
            gpath.lineTo(end.x, end.y);
            first = false;
        } else {
            gpath.lineTo(end.x, end.y);
        }
        canvas.repaint();
        start = end;
    }

    @Override
    public void mouseUp(Point p) {
        undoMgr.storeDraw();

        end = p;
        // ドラッグした場合
        if (gpath != null) { 
            PathHolder sh = new PathHolder(new GeneralPath(gpath), eraserStroke, Color.WHITE, AlphaComposite.SrcOver);
            addShape(sh);
            gpath = null;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // クリックの場合
        Ellipse2D whiteSpot = new Ellipse2D.Double();
        whiteSpot.setFrameFromCenter(start.x, start.y, start.x+8.0f, start.y+8.0f);
        AreaHolder sh = new AreaHolder(new Area(whiteSpot), eraserStroke, Color.WHITE, AlphaComposite.SrcOver, true);
        addShape(sh);
        canvas.repaint();
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (gpath == null) return;

        g2d.setStroke(eraserStroke);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setPaint(Color.WHITE);

        g2d.draw(gpath);
    }
}

