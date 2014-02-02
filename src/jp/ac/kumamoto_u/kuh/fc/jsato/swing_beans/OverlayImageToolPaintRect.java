/*
 * OverlayImageToolPaintRect.java
 *
 * Created on 2001/09/04, 10:02
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

public class OverlayImageToolPaintRect extends OverlayImageTool {

    /** Creates new OverlayImageToolPaintRect */
    public OverlayImageToolPaintRect(OverlayImagePanelBean pnl) {
        super(pnl);
    }
    
    public void drawShape(Graphics g, Point p1, Point p2) {
        Point lt = new Point(p1);
        Point rb = new Point(p2);
        // don't do lt = p1, rb = p2.
        // lt and rb are not reference but copy
        // if you let lt = p1, rb = p2, 
        // changing value of lt or rb propagates to p1 or p2.
        
        if (p1.x < p2.x) {
            lt.x = p1.x;
            rb.x = p2.x;
        } else {
            lt.x = p2.x;
            rb.x = p1.x;
        }
        
        if (p1.y < p2.y) {
            lt.y = p1.y;
            rb.y = p2.y;
        } else {
            lt.y = p2.y;
            rb.y = p1.y;
        }
        
        g.drawRect(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
    }
    
    public void drawShape2(Graphics g, Point p1, Point p2) {
        Point lt = new Point(p1);
        Point rb = new Point(p2);
        // don't do lt = p1, rb = p2.
        // lt and rb are not reference but copy
        // if you let lt = p1, rb = p2, 
        // changing value of lt or rb propagates to p1 or p2.
        
        if (p1.x < p2.x) {
            lt.x = p1.x;
            rb.x = p2.x;
        } else {
            lt.x = p2.x;
            rb.x = p1.x;
        }
        
        if (p1.y < p2.y) {
            lt.y = p1.y;
            rb.y = p2.y;
        } else {
            lt.y = p2.y;
            rb.y = p1.y;
        }
        
        //g.fillRect(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
        
        Graphics2D g2 = (Graphics2D)g;
        float alpha = 0.5f;
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);  
        g2.setComposite(ac);
        g2.fill(new Rectangle2D.Double(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y));
    }
    
    public void whileDragging(Point oldPt, Point newPt) {
        // add current point
        draggedPoints.add(newPt);//////////////////////////////

        // draw XOR rect
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
            drawShape2(g, startPt, endPt);
            
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
