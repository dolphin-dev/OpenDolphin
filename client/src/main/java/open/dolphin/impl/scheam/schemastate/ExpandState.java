package open.dolphin.impl.scheam.schemastate;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import open.dolphin.impl.scheam.SchemaCanvasDialog2;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 * 拡大／縮小ダイアログを出す
 * @author pns
 */
public class ExpandState extends AbstractState {

    public ExpandState(SchemaEditorImpl context) {
        super(context);
    }

    public void expand() {

        ExpandPanel panel = new ExpandPanel();
        SchemaCanvasDialog2 dialog = new SchemaCanvasDialog2(context.getCanvasView(), true);
        dialog.setTitle("拡大・縮小率");
        dialog.addContent(panel);
        dialog.setVisible(true);

        if (dialog.getResult() == JOptionPane.CANCEL_OPTION) return;

        double ratio = (double) panel.getValue()/100;
        undoMgr.storeExpand(ratio);

        //DrawingHolder
        for (DrawingHolder h : drawingList) h.expand(ratio, ratio);

        BufferedImage src = canvas.getBaseImage();
        BufferedImage dist = changeSize(src, (int)(src.getWidth()*ratio), (int)(src.getHeight()*ratio));

        canvas.setBaseImage(dist);
        context.recomputeViewBounds(dist);
    }

    private BufferedImage changeSize(BufferedImage image, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width,height, image.getType());
        Graphics2D g2d = scaledImage.createGraphics();
        
//        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        g2d.drawImage(image, 0, 0, width, height, null);
        return scaledImage;
    }

    @Override
    public void mouseDown(Point p) {}

    @Override
    public void mouseDragged(Point p) {}

    @Override
    public void mouseUp(Point p) {}
}
