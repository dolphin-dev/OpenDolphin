package open.dolphin.impl.scheam.schemastate;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class PencilState extends PolygonState {

    public PencilState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        // drag の時は fill しない
        properties.setIsFill(false);
        start = p;
        end = null;
        first = true;
        shape = new GeneralPath();
    }

    @Override
    public void mouseUp(Point p) {

        if (shape.getBounds().width != 0 || shape.getBounds().height != 0) {
            undoMgr.storeDraw();
            addPathShape((GeneralPath)shape);
        }
        shape = null;
    }

    /**
     * クリックならドット，ALT が押されていたらランダムに６ドット
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        undoMgr.storeDraw();
        
        // ドットの時は fill する
        properties.setIsFill(true);
        
        float r = properties.getLineWidth()/2 + 1; //ドットの半径
        if (e.isAltDown()) {
            //ランダムドット産生
            GeneralPath gp = new GeneralPath();
            java.util.Random rnd = new java.util.Random();
            gp.moveTo(start.x, start.y);
            gp.lineTo(start.x, start.y);
            for (int i=0; i<5; i++) {
                double t = (rnd.nextFloat()+(double)i) * Math.PI*2/5;
                gp.moveTo(start.x + (float) (2.5*r*Math.sin(t)), start.y + (float) (2.5*r*Math.cos(t)));
                gp.lineTo(start.x + (float) (2.5*r*Math.sin(t)), start.y + (float) (2.5*r*Math.cos(t)));
            }
            addPathShape(gp);
            
        } else {
            //普通にドット
            Ellipse2D dot = new Ellipse2D.Double();
            dot.setFrameFromCenter(start.x, start.y, start.x + r, start.y + r);
            addAreaShape(dot);
        }
        canvas.repaint();
    }
}
