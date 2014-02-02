/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * 共有関数クラス
 * @author Life Sciences Computing Corporation.
 */
public class Func {
    /**
     * 画像のリサイズ
     * @param srcImage 元画像
     * @param destWidth 横幅
     * @param destHeight 高さ
     * @return 
     */
    public BufferedImage rescaleImage(BufferedImage srcImage, int dstWidth, int dstHeight) {
        BufferedImage dstImage = null;
        if(srcImage.getColorModel() instanceof IndexColorModel) {
            dstImage = new BufferedImage(dstWidth, dstHeight, srcImage.getType(), (IndexColorModel)srcImage.getColorModel());
        }else{
            if(srcImage.getType() == 0) {
                dstImage = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_4BYTE_ABGR_PRE);
            }else{
                dstImage = new BufferedImage(dstWidth, dstHeight, srcImage.getType());
            }
        }
        
        double x = (double) dstWidth / srcImage.getWidth();
        double y = (double) dstHeight / srcImage.getHeight();
        AffineTransform af = AffineTransform.getScaleInstance(x, y);
        
        if(dstImage.getColorModel().hasAlpha() && dstImage.getColorModel() instanceof IndexColorModel) {
            int pixel = ((IndexColorModel) dstImage.getColorModel()).getTransparentPixel();
            for(int i = 0; i < dstImage.getWidth(); ++i) {
                for(int j = 0; j < dstImage.getHeight(); ++j) {
                    dstImage.setRGB(i, j, pixel);
                }
            }
        }
        
        Graphics2D g2 = (Graphics2D)dstImage.createGraphics();
        g2.drawImage(srcImage, af, null);
        g2.dispose();
        
        return dstImage;
    }
}
