/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.helper;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author kazm
 */
public class ImageHelper {

    public static BufferedImage getFirstScaledInstance(BufferedImage inImage, int maxDim) {
        
        if (inImage.getWidth() <= maxDim && inImage.getHeight() <= maxDim) {
            return inImage;
        }

        BufferedImage outImage = null;
        
        try {
            // Determine the scale.
            double scale = (double) maxDim / (double) inImage.getHeight(null);
            if (inImage.getWidth(null) > inImage.getHeight(null)) {
                scale = (double) maxDim / (double) inImage.getWidth(null);
            }

            // Determine size of new image. 
            // One of them should equal maxDim.
            int scaledW = (int) (scale * inImage.getWidth(null));
            int scaledH = (int) (scale * inImage.getHeight(null));

            // Create an image buffer in which to paint on.
            outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_BGR);

            // Set the scale.
            AffineTransform tx = new AffineTransform();

            // If the image is smaller than the desired image size,
            // don't bother scaling.
            if (scale < 1.0d) {
                tx.scale(scale, scale);
            }

            // Paint image.
            Graphics2D g2d = outImage.createGraphics();
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                             RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(inImage, tx, null);
            g2d.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outImage;
    }
}
