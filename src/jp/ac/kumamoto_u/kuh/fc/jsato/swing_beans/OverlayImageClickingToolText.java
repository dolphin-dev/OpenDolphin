/*
 * OverlayImageClickingToolPoint.java
 *
 * Created on 2002/04/10, 14:19
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
public class OverlayImageClickingToolText extends OverlayImageClickingTool {

    /** Creates new OverlayImageClickingToolPoint */
    public OverlayImageClickingToolText(OverlayImagePanelBean pnl) {
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
            
            drawText(g, clickedPoint);
            
            panel.repaintNow();
        }
        //-----------------------------------------------
        
        // remove all points
        clickedPoints.removeAllElements();
        
        return true;
    }
    
    public void drawText(Graphics g, Point pt) {
        Graphics2D g2 = (Graphics2D)g;
        Stroke saved = g2.getStroke();
            BasicStroke stroke = new BasicStroke(this.getPenSize());
            g2.setStroke(stroke);

            //------------------------------
            String text = JOptionPane.showInputDialog(
                (Component)panel, 
                new String("Input Text"),// message
                new String("Question"),// title
                JOptionPane.QUESTION_MESSAGE);
            if (text == null) {
                g2.setStroke(saved);
                return;
            }
            if (text != null && text.length() <= 0) {
                g2.setStroke(saved);
                return;
            }
            //------------------------------
            
            AttributedString as = new AttributedString(text);
            Font font = new Font("sanserif", Font.BOLD | Font.ITALIC, 20);
            as.addAttribute(TextAttribute.FONT, font, 0, text.length());
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout tl = new TextLayout(text, font, frc);
            tl.draw(g2, pt.x, pt.y);
        g2.setStroke(saved);        
        /*
        Point lt = new Point(pt);
        Point rb = new Point(pt);
        rb.x = lt.x + 10;
        rb.y = lt.y + 10;

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
         */
    }
}
