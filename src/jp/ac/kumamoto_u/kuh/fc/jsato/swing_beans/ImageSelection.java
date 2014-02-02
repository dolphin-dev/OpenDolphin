/*
 * ImageSelection.java
 *
 * Created on 2001/09/01, 14:22
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.awt.image.*;

/**
 *
 * @author  Junzo SATO
 * @copyright   Copyright (c) 2001, Junzo SATO. All rights reserved.
 */

public class ImageSelection implements Transferable, ClipboardOwner {
    Image image;
    int width, height;
    
    public static DataFlavor imageFlavor;
    public static DataFlavor gifFlavor;
    DataFlavor[] flavors = {imageFlavor, gifFlavor};
    
    static {
        try {
            imageFlavor = new DataFlavor(Class.forName("java.awt.Image"), "Java Image");
            gifFlavor = new DataFlavor("image/gif", "GIF Image");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /** Creates new ImageSelection */
    public ImageSelection(Image image) {
        this.image = image;
    }
    
    public ImageSelection(Image image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }
    
    // Transferable
    public synchronized DataFlavor[] getTransferDataFlavors() {
        // return supported flavors
        return flavors;
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        // see if the flavor is contained in our list
        return flavor.equals(imageFlavor) || flavor.equals(gifFlavor);
    }
        
    private int[] imageToPixels() {
        int[] pixels = new int[width*height];
        PixelGrabber pg = new PixelGrabber(image,0,0,width,height,pixels,0,width);
        try{
            pg.grabPixels();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
        return pixels;
    }
    
    public synchronized Object getTransferData(DataFlavor flavor) 
        throws UnsupportedFlavorException, IOException {
            
        if ( flavor.equals(imageFlavor) ) {
            return this.image;
        } else if ( flavor.equals(gifFlavor) ) {
            return imageToPixels();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    // ClipboardOwner
    public void lostOwnership(Clipboard c, Transferable t) {
         System.out.println("lostOwnership: " + c.getName());
    }
}
