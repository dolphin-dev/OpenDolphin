package open.dolphin.impl.scheam.schemastate;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 *
 * @author pns
 */
public class RotationState extends AbstractState {

    public RotationState(SchemaEditorImpl context) {
        super(context);
    }

    public void rotate() {

        undoMgr.storeRotate(properties.isRightRotation()? Math.PI/2: -Math.PI/2);

        // baseImage の rotate
        BufferedImage src = canvas.getBaseImage();
        // 90度回転なので height-width を入れ替える
        BufferedImage dist = new BufferedImage(src.getHeight(), src.getWidth(), src.getType());

        //DrawingHolder の回転
        for (DrawingHolder h : drawingList) {
            if (properties.isRightRotation()) {
                // 原点中心に 90度右回転 → 右方向に幅の分移動
                h.rotate(Math.PI/2);
                h.translate(dist.getWidth(), 0);
            } else {
                // 原点中心に 90度左回転 → 下方向に高さ分移動
                h.rotate(-Math.PI/2);
                h.translate(0, dist.getHeight());
            }
        }

        AffineTransform rotate;
        // baseImage の transform は src の立場で考える orz
        if (properties.isRightRotation()) {
            // 原点中心に 90度右回転 → 上方向（src から見て）に高さ分移動
            rotate = AffineTransform.getQuadrantRotateInstance(1);
            rotate.concatenate(AffineTransform.getTranslateInstance(0, -src.getHeight()));
        } else {
            // 原点中心に 90度左回転 → 左方向（src から見て）に幅の分移動
            rotate = AffineTransform.getQuadrantRotateInstance(-1);
            rotate.concatenate(AffineTransform.getTranslateInstance(-src.getWidth(), 0));
        }

        Graphics2D g = dist.createGraphics();
        g.setTransform(rotate);
        g.drawImage(src, null, 0, 0);
        g.dispose();
        // 回転したイメージを canvas にセットし直す
        canvas.setBaseImage(dist);

       // 縦横が変わるので，canvas の大きさ変更が必要
        context.recomputeViewBounds(dist);
    }

    @Override
    public void mouseDown(Point p) {}

    @Override
    public void mouseDragged(Point p) {}

    @Override
    public void mouseUp(Point p) {}
}
