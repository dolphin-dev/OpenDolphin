package open.dolphin.impl.scheam.schemastate;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 * 一部を選択して切り抜く
 * @author pns
 */
public class ClippingState extends AbstractState {

    public ClippingState(SchemaEditorImpl context) {
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
        if (end == null) return;

        BufferedImage src = canvas.getBaseImage();

        int x; int y; int width; int height;

        // はみ出した場合の対応
        start.x = rounding(start.x, src.getWidth());
        end.x = rounding(end.x, src.getWidth());
        start.y = rounding(start.y, src.getHeight());
        end.y = rounding(end.y, src.getHeight());

        // 反対からドラッグした場合対応
        x = (start.x < end.x)? start.x : end.x;
        y = (start.y < end.y)? start.y : end.y;
        width = Math.abs(start.x - end.x);
        height = Math.abs(start.y - end.y);

        // 切り抜きの大きさがなければ abort
        if (width == 0 || height == 0) {
            shape = null;
            canvas.repaint();
            return;
        }

        undoMgr.storeClipping(-x, -y);

        //DrawingHolder
        for (DrawingHolder h : drawingList) h.translate( -x, -y) ;
        
        BufferedImage dist = src.getSubimage(x, y, width, height);

        canvas.setBaseImage(dist);
        context.recomputeViewBounds(dist);

        shape = null;
    }

    /**
     * はみ出した分の修正
     * @param num
     * @param limit
     * @return
     */
    private int rounding(int num, int limit) {
        int n = num;
        if (n < 0) n = 0;
        else if (n >= limit) n = limit - 1;
        return n;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (shape == null) return;

        g2d.setStroke(properties.getOutlineStroke());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setPaint(Color.gray);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.draw(shape);
    }

}
