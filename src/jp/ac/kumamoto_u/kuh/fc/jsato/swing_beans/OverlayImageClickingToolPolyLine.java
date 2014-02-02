/*
 * OverlayImageClickingToolPolyLine.java
 *
 * Created on 2002/04/22, 11:25
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.awt.geom.*;
import java.text.*;
import java.awt.font.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class OverlayImageClickingToolPolyLine extends OverlayImageClickingTool {

    /** Creates new OverlayImageClickingToolPolyLine */
    public OverlayImageClickingToolPolyLine(OverlayImagePanelBean pnl) {
        super(pnl);
    }

    public boolean clicked(Point clickedPoint, Color col) {
        if (panel.hasOverlay() == false) {
            panel.createOverlay();
        } else {
            // store current overlay to srcImg
            //
            // this is an option...
            // if you'd like to keep srcImg clean and to controll refreshing srcImg,
            // comment out the following line.
            panel.storeOverlay();
        }
        
        penColor = col;
        
        // save starting point
        clickedPoints.add(clickedPoint);

        //-----------------------------------------------
        Graphics g = panel.getOverlayG();
        if (g != null) {
            g.setColor(penColor);
            g.setPaintMode();
            
                Point lt = new Point(clickedPoint);
                Point rb = new Point(clickedPoint);
                lt.x -= 2;
                lt.y -= 2;
                rb.x = lt.x + 4;
                rb.y = lt.y + 4;
                
                Graphics2D g2 = (Graphics2D)g;
                Stroke saved = g2.getStroke();
                    BasicStroke stroke = new BasicStroke(this.getPenSize());
                    g2.setStroke(stroke);
                    g2.draw(new Ellipse2D.Double(
                        lt.x,
                        lt.y,
                        rb.x - lt.x,
                        rb.y - lt.y
                    ));
                g2.setStroke(saved);
                
                if (clickedPoints.size() > 1) {
                    drawShape(g, (Point)clickedPoints.elementAt(clickedPoints.size() - 2), clickedPoint);
                }
                
            panel.repaintNow();
        }
        //-----------------------------------------------
        
        return false;
    }
    
    public boolean doubleClicked(Point clickedPoint, Color col) {
        // remove all points
        clickedPoints.removeAllElements();
        
        return true;
    }
    
    public void interruptClickingTool() {
        // remove all points
        clickedPoints.removeAllElements();
    }
    
    public void drawShape(Graphics g, Point p1, Point p2) {
        Graphics2D g2 = (Graphics2D)g;
        Stroke saved = g2.getStroke();
            BasicStroke stroke = new BasicStroke(this.getPenSize());
            g2.setStroke(stroke);
            g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
        g2.setStroke(saved);
    }
}
