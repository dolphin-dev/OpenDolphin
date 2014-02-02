package open.dolphin.impl.scheam;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import open.dolphin.impl.scheam.holder.DrawingHolder;
import open.dolphin.impl.scheam.schemastate.*;

/**
 *
 * @author pns
 */
public class StateMgr {
    
    private SchemaEditorImpl context;
    private SchemaEditorProperties properties;
    private ArrayList<DrawingHolder> drawingList;
    private UndoMgr undoMgr;

    private AbstractState selectState;
    private AbstractState lineState;
    private AbstractState rectState;
    private AbstractState ellipseState;
    private AbstractState polygonState;
    private AbstractState pencilState;
    private AbstractState eraserState;
    private AbstractState textState;
    private RotationState rotationState;
    private AbstractState curState;
    private AbstractState clippingState;

    private DotsState dotsState;
    private ExpandState expandState;

    public StateMgr(SchemaEditorImpl context) {
        this.context = context;
        this.drawingList = context.getDrawingList();
        this.properties = context.getProperties();
        this.undoMgr = context.getUndoMgr();
        
        selectState = new SelectState(context);
        lineState = new LineState(context);
        rectState = new RectState(context);
        ellipseState = new EllipseState(context);
        polygonState = new PolygonState(context);
        pencilState = new PencilState(context);
        eraserState = new EraserState(context);
        textState = new TextState(context);

        rotationState = new RotationState(context);
        expandState = new ExpandState(context);
        clippingState = new ClippingState(context);
        dotsState = new DotsState(context);

        curState = rectState;
    }

    // ツールボタンから呼ばれる methods
    public void startSelect() { 
        curState = selectState;
    }
    public void startLine() {
        curState = lineState;
        properties.setIsFill(false);
    }
    public void startRect() {
        curState = rectState;
        properties.setIsFill(false);
    }
    public void startEllipse() { 
        curState = ellipseState;
        properties.setIsFill(false);
    }
    public void startPolygon() { 
        curState = polygonState;
        properties.setIsFill(false);
    }
    public void startRectFill() { 
        curState = rectState;
        properties.setIsFill(true);
    }
    public void startEllipseFill() { 
        curState = ellipseState;
        properties.setIsFill(true);
    }
    public void startPolygonFill() { 
        curState = polygonState;
        properties.setIsFill(true);
    }
    public void startPencil() { 
        curState = pencilState;
        properties.setIsFill(false);
    }
    public void startEraser() { 
        curState = eraserState;
        properties.setIsFill(false);
    }
    public void startText() { 
        curState = textState;
        properties.setIsFill(false);
    }
    public void startClipping() {
        curState = clippingState;
    }
    public void startNetSparse() {
        curState = dotsState;
        dotsState.setDots(DotsState.NET_SPARSE);
    }
    public void startNetMedium() {
        curState = dotsState;
        dotsState.setDots(DotsState.NET_MEDIUM);
    }
    public void startNetDense() {
        curState = dotsState;
        dotsState.setDots(DotsState.NET_DENSE);
    }
    public void startDotsSparse() {
        curState = dotsState;
        dotsState.setDots(DotsState.DOTS_SPARSE);
    }
    public void startDotsMedium() {
        curState = dotsState;
        dotsState.setDots(DotsState.DOTS_MEDIUM);
    }
    public void startDotsDense() {
        curState = dotsState;
        dotsState.setDots(DotsState.DOTS_DENSE);
    }

    // mouseListener から呼ばれる methods
    public void mouseDown(Point p) { curState.mouseDown(p); }
    public void mouseDragged(Point p) { curState.mouseDragged(p); }
    public void mouseUp(Point p) { curState.mouseUp(p); }
    public void mouseClicked(MouseEvent e) { curState.mouseClicked(e); }
    public void setMouseEvent(MouseEvent e) { curState.setMouseEvent(e); }

    /**
     * SchemaCanvas から呼ばれる
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        // canvas を全部書き直す
        for (DrawingHolder d : drawingList) d.draw(g2d);
        // 現在の色，ストロークなどを設定
        properties.setGraphicsState(g2d);
        curState.draw(g2d);
    }

    // 押すだけの State （マウス drag したりしない処理）
    // curState.draw が呼ばれるので，その前の state は mouseUp で必ず shape = null しておく必要がある
    public void undo() {
        undoMgr.undo();
        context.getCanvas().repaint();
    }
    public void redo() {
        undoMgr.redo();
        context.getCanvas().repaint();
    }
    public void clear() {
        undoMgr.storeDraw();
        drawingList.clear();
        context.getCanvas().repaint();
    }
    public void rotateRight() {
        properties.setIsRightRotation(true);
        rotationState.rotate();
    }
    public void rotateLeft() {
        properties.setIsRightRotation(false);
        rotationState.rotate();
    }
    public void expand() {
        expandState.expand();
    }
}

