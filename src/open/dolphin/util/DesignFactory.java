/*
 * DGL.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

/**
 * Utilitys based on Java Design Guide Lines.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DesignFactory {
    
    //public static Insets frameInsets = new Insets(23, 4, 4, 4);
    
    static Color dropOkColor = new Color(0, 12, 156); // StampColor

    /** Creates new DGL */
    public DesignFactory() {
    }
    
    public static Color getDropOkColor() {
        return dropOkColor;
    }
    
    /*public static Border createDropOkBorder() {
        return BorderFactory.createLineBorder(dropOkColor, 2);
    }*/
    
    public static JProgressBar createProgressBar(Dimension size, boolean border) {
        JProgressBar bar = new JProgressBar(0,100);
        bar.setPreferredSize(size);
        bar.setBorderPainted(border);
        return bar;
    }    
    
    public static JProgressBar createProgressBar() {
        Dimension dim = new Dimension(200, 12);
        JProgressBar bar = new JProgressBar(0,100);
        bar.setPreferredSize(dim);
        bar.setBorderPainted(false);
        return bar;
    }
    
    public static AnimationLabel createUltraSonicWave(Dimension size) {
        AnimationLabel animation = new AnimationLabel();
        ImageIcon[] frames = new ImageIcon[10];
        for (int i = 1; i <=10; i++) {
            frames[i-1] = ClientContext.getImageIcon("us" + i + ".gif");
        }
        animation.setup(frames);
        animation.setPreferredSize(size);
        return animation;
    }
    
    public static AnimationLabel createUltraSonicWave() {
        return createUltraSonicWave(new Dimension(200, 12));
    }    

    public static Border createtDialogSpace() {
        return BorderFactory.createEmptyBorder(12, 12, 11, 11);
    }
    
    public static Component createtButtonHSpace() {
        return Box.createRigidArea(new Dimension(5,0));
    }
    
    public static Component createtButtonVSpace() {
        return Box.createRigidArea(new Dimension(0,5));
    }    
    
    public static Component createtComponentHSpace() {
        return Box.createRigidArea(new Dimension(11,0));
    } 
    
    public static Component createtComponentVSpace() {
        return Box.createRigidArea(new Dimension(0,11));
    }     
    
    public static Point getCenterLoc(int width, int height) {
        Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize ();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height ) / 3;
        return new Point(x, y);
    }
}