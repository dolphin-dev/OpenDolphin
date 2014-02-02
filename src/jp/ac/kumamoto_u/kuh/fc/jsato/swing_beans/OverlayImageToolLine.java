/*
 * OverlayImageToolLine.java
 *
 * Created on 2001/09/03, 15:21
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.awt.geom.*;
/**
 *
 * @author  Junzo SATO
 * @copyright   Copyright (c) 2001, Junzo SATO. All rights reserved.
 */

public class OverlayImageToolLine extends OverlayImageTool {
    /** Creates new OverlayImageToolLine */
    public OverlayImageToolLine(OverlayImagePanelBean pnl) {
        super(pnl);
    }
    
    //int penSize = 2;
    public void drawShape(Graphics g, Point p1, Point p2) {
        /*
         // draw line using pen size
        if (p1.x == p2.x) {
            // vertical 
            for (int i = 0; i < penSize; ++i) {
                g.drawLine(p1.x+i, p1.y, p2.x+i, p2.y);
            }
        } else if (p1.y == p2.y) {
            // horizontal
            for (int j = 0; j < penSize; ++j) {
                g.drawLine(p1.x, p1.y+j, p2.x, p2.y+j);
            }
        } else {
            for (int i = 0; i < penSize; ++i) {
                g.drawLine(p1.x+i, p1.y, p2.x+i, p2.y);
                g.drawLine(p1.x+i, p1.y + penSize -1, p2.x+i, p2.y + penSize -1);
            }

            for (int j = 0; j < penSize; ++j) {
                g.drawLine(p1.x, p1.y+j, p2.x, p2.y+j);
                g.drawLine(p1.x + penSize -1, p1.y+j, p2.x + penSize -1, p2.y+j);
            }
        }
         */
        Graphics2D g2 = (Graphics2D)g;
        Stroke saved = g2.getStroke();
            BasicStroke stroke = new BasicStroke(this.getPenSize());
            g2.setStroke(stroke);
            g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
        g2.setStroke(saved);
    }
    
    public void whileDragging(Point oldPt, Point newPt) {
        // add current point
        draggedPoints.add(newPt);//////////////////////////////

        // draw XOR line
        Graphics g = panel.getOverlayG();
        if (g != null) {
            Point startPt = (Point)draggedPoints.firstElement();
            g.setXORMode(panel.getBackground());
            drawShape(g, startPt, oldPt);
            drawShape(g, startPt, newPt);
     
            panel.repaintNow();
        }
    }
    
    public void stopDragging(Point endPt) {
        Graphics g = panel.getOverlayG();
        if (g != null) {
            Point startPt = (Point)draggedPoints.firstElement();
            Point lastPt = (Point)draggedPoints.lastElement();
            g.setXORMode(panel.getBackground());
            drawShape(g, startPt, lastPt);

            g.setColor(penColor);
            g.setPaintMode();
            drawShape(g, startPt, endPt);
            
            panel.repaintNow();
        }
        
        /* Of course I know it's waste of memory to store all dragged points
         * for this tool because only two points are necessary. :-)
         */
        
        // keep last point
        draggedPoints.add(endPt);/////////////////////////////

        // clear all points in the list
        draggedPoints.removeAllElements();
    }
}
