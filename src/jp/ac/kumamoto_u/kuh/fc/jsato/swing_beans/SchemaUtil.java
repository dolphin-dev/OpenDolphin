/*
 * SchemaUtil.java
 *
 * Created on 2002/06/25, 20:06
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

import com.sun.image.codec.jpeg.*;
import java.io.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class SchemaUtil {

    /** Creates new SchemaUtil */
    public SchemaUtil() {
    }

    static public Image scaleImage(ImageIcon srcIcon, int scaleHeight) {
        Image img = srcIcon.getImage();
        int w = srcIcon.getIconWidth();
        int h = srcIcon.getIconHeight();
        if (w < 0 || h < 0) return null;
        //System.out.println("(w,h) = " + String.valueOf(w) + "," + String.valueOf(h));

        // calculate the size of thumbnail
        int ww, hh;
        if (w > h) {
            if (scaleHeight > w) {
                ww = w;
                hh = h;
            } else {
                ww = scaleHeight;
                hh = (int)(h * ((float)scaleHeight/w));
            }
        } else {
            if (scaleHeight > h) {
                ww = w;
                hh = h;
            } else {
                ww = (int)(w * ((float)scaleHeight/h));
                hh = scaleHeight;
            }
        }
        //System.out.println("(ww,hh) = " + ww + "," + hh);
        
        // get the resized image
        Image dstImg = img.getScaledInstance(ww, hh, Image.SCALE_DEFAULT);
        return dstImg;
    }
    
    static public byte[] convertToJpegData(Image image, ImageObserver observer) {
        // create buffered image
        ImageIcon icn = new ImageIcon(image);
        int w = icn.getIconWidth();
        int h = icn.getIconHeight();
        
        BufferedImage bf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = bf.getGraphics();
        g.setColor(Color.white);
        g.drawImage(image, 0, 0, w, h, observer);
        ByteArrayOutputStream dest = null;
        try {
            dest = new ByteArrayOutputStream();
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(dest);
            encoder.encode(bf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return dest.toByteArray();
    }
}
