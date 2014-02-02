/*
 * OverlayImageToolFreeform.java
 *
 * Created on 2001/09/04, 5:53
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

public class OverlayImageToolFillPoly extends OverlayImageTool {

    /** Creates new OverlayToolFreeLine */
    public OverlayImageToolFillPoly(OverlayImagePanelBean pnl) {
        super(pnl);
    }
    
    public void whileDragging(Point oldPt, Point newPt) {
        // add current point
        draggedPoints.add(newPt);//////////////////////////////
        
        Graphics g = panel.getOverlayG();
        if (g != null) {
            
            g.setColor(penColor);
            //g.setXORMode(panel.getBackground());
            g.drawLine(oldPt.x, oldPt.y, newPt.x, newPt.y);
     
            panel.repaintNow();
        }
    }
    
    public void stopDragging(Point endPt) {
        // keep last point
        draggedPoints.add(endPt);/////////////////////////////

        Graphics g = panel.getOverlayG();
        if (g != null) {
            g.setColor(penColor);
            // draw polygon
            // you can construct a polygon while dragging by adding new point
            // instead of after dragging operation
            
            Polygon poly = new Polygon();
            for (int i = 0; i < draggedPoints.size(); ++i) {
                Point p = (Point)draggedPoints.elementAt(i);
                poly.addPoint(p.x, p.y);
            }
            
            //g.setXORMode(panel.getBackground());
            //g.drawPolygon(poly);
            
            g.setPaintMode();
            g.drawPolygon(poly);
            //-----------------------------------------------
            //g.fillPolygon(poly);
        
            Graphics2D g2 = (Graphics2D)g;
            float alpha = 0.5f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);  
            g2.setComposite(ac);
            // construct Graphics2D polygon (I know this is overhead.)

            GeneralPath polygon = new GeneralPath(
                GeneralPath.WIND_EVEN_ODD,
                poly.xpoints.length
            );
            
            polygon.moveTo(poly.xpoints[0], poly.ypoints[0]);
            //System.out.println(poly.xpoints[0] + ", " + poly.ypoints[0]);
            for (int index = 1; index < poly.xpoints.length; ++index) {
                if ( poly.xpoints[index]==0 && poly.ypoints[index]==0 ) {
                    // skip (0,0)
                    continue;
                }
                polygon.lineTo(poly.xpoints[index], poly.ypoints[index]);
                //System.out.println(poly.xpoints[index] + ", " + poly.ypoints[index]);
            }
            polygon.closePath();
            
            // fill polygon 
            g2.fill(polygon);
            //-----------------------------------------------
            panel.repaintNow();
        }

        // clear all points in the list
        draggedPoints.removeAllElements();
    }
}
