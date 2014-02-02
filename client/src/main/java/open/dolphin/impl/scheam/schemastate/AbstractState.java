package open.dolphin.impl.scheam.schemastate;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import open.dolphin.impl.scheam.SchemaCanvas;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.SchemaEditorProperties;
import open.dolphin.impl.scheam.UndoMgr;
import open.dolphin.impl.scheam.holder.*;

/**
 * stateMgr から指定された state に応じて描画する
 * @author pns
 */
public abstract class AbstractState {

    protected SchemaEditorImpl context;
    protected SchemaEditorProperties properties;
    protected SchemaCanvas canvas;
    protected ArrayList<DrawingHolder> drawingList;
    protected UndoMgr undoMgr;

    protected Shape shape;
    protected boolean first;
    protected Point start;
    protected Point end;

    public AbstractState() {}

    public AbstractState(SchemaEditorImpl context) {
        this();
        this.context = context;
        this.canvas = context.getCanvas();
        this.drawingList = context.getDrawingList();
        this.properties = context.getProperties();
        this.undoMgr = context.getUndoMgr();
    }

    public abstract void mouseDown(Point p);

    public abstract void mouseDragged(Point p);

    // mouseUp で必ず shape = null すること！
    public abstract void mouseUp(Point p);

    // 必要に応じて override する
    public void mouseClicked(MouseEvent e) {}
    public void setMouseEvent(MouseEvent e) {}

    /** 
     * mouseDown から mouseUp まで，drag されている間の途中経過を描く　
     * SchemaCanvas の paintComponent から StateMgr 経由で呼ばれる
     */
    public void draw(Graphics2D g2d) {
        if (shape != null) {
            if (properties.isFill()) g2d.fill(shape);
            else g2d.draw(shape);
        }
    }

    /**
     * DrawingHolder を drawingList に加える
     * @param DrawingHolder s
     */
    public void addShape(DrawingHolder s) {
        drawingList.add(s);
    }
    /**
     * Shape を AreaHolder に入れて drawingList に加える
     * @param Shape shape
     */
    public void addAreaShape(Shape shape) {
        AreaHolder sh = new AreaHolder(new Area(shape),
                properties.getStroke(),
                properties.getFillColor(),
                properties.getAlphaComposite(),
                properties.isFill());
        addShape(sh);
    }
    /**
     * Shape を Line2DHolder に入れて drawingList に加える
     * @param shape
     */
    public void addLineShape(Line2D.Double shape) {
        Line2DHolder sh = new Line2DHolder(shape, 
                properties.getStroke(), 
                properties.getFillColor(), 
                properties.getAlphaComposite());
        addShape(sh);        
    }
    /**
     * Shape を PathHolder に入れて drawingList に加える
     * @param shape
     */
    public void addPathShape(Shape shape) {
        PathHolder sh = new PathHolder(new GeneralPath(shape),
                properties.getStroke(),
                properties.getFillColor(),
                properties.getAlphaComposite());
        addShape(sh);
    }
    /**
     * Shape を TextHolder に入れて drawingList に加える
     * @param shape
     */
    public void addTextShape(Shape shape) {
        AreaHolder sh = new TextHolder(new Area(shape),
                properties.getTextStroke(),
                properties.getTextColor(),
                properties.getTextComposite(),
                true);
        addShape(sh);
    }
    /**
     * 最後に追加した絵を list から除去する
     */
    public void removeLastShape() {
        if (!drawingList.isEmpty()) drawingList.remove(drawingList.size()-1);
    }
}
