/*
 * Panel2.java
 *
 * Created on 2001/11/13, 2:13
 */

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// Junzo SATO

package jp.ac.kumamoto_u.kuh.fc.jsato;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import java.io.*;
import java.net.*;
import java.awt.print.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class Panel2 extends JPanel implements Printable {
    String patientName = "患者様 カルテ";

    /** Creates new Panel2 */
    public Panel2() {
        super();
    }
    
    public void printPanel(
        PageFormat pageFormat, 
        int numOfCopies,
        boolean useDialog ) {
        
        if ( this.getRootPane() != null &&
             this.getRootPane().getParent() != null && 
             this.getRootPane().getParent().getClass().getName().equals("open.dolphin.client.Karte") ) {
            JFrame f = (JFrame)this.getRootPane().getParent();
            patientName = f.getTitle() + " 患者様 カルテ";
        }
        
        boolean buffered = this.isDoubleBuffered();
        this.setDoubleBuffered(false);
        //----------------------------------------------------------------------
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj != null) {
            pj.setCopies(numOfCopies);
            pj.setJobName(patientName + " by Dolphin");
            pj.setPrintable(this, pageFormat);

            if (useDialog) {
                if (pj.printDialog()) {
                    try {
                        pj.print();
                    } catch (PrinterException printErr) {
                        printErr.printStackTrace();
                    }
                }
            } else {
                try {
                    pj.print();
                } catch (PrinterException printErr) {
                    printErr.printStackTrace();
                }
            }
        }
        //----------------------------------------------------------------------
        this.setDoubleBuffered(buffered);
    }
    
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        Graphics2D g2 = (Graphics2D)g;
        Font f = new Font("Courier", Font.ITALIC, 9);
        g2.setFont(f);
        g2.setColor(Color.black);
        //
        int fontHeight = g2.getFontMetrics().getHeight();
        int fontDescent = g2.getFontMetrics().getDescent();
        double footerHeight = fontHeight;
        double pageHeight = pf.getImageableHeight() - footerHeight;
        double pageWidth = pf.getImageableWidth();
        //
        double componentHeight = this.getSize().getHeight();
        double componentWidth = this.getSize().getWidth();
        //
        double scale = 1;
        if (componentWidth >= pageWidth) {
            scale = pageWidth / componentWidth;// shrink
        }
        //
        double scaledComponentHeight = componentHeight*scale;
        int totalNumPages = (int)Math.ceil(scaledComponentHeight/pageHeight);

        if (pi >= totalNumPages) {
           return Printable.NO_SUCH_PAGE;
        }

        // footer
        g2.translate(pf.getImageableX(), pf.getImageableY());
        String footerString = patientName + "  Page: " + (pi + 1) + " of " + totalNumPages;
        int strW = SwingUtilities.computeStringWidth(g2.getFontMetrics(), footerString);
        g2.drawString(
            footerString, 
            (int)pageWidth/2 - strW/2,
            (int)(pageHeight + fontHeight - fontDescent)
        );

        // page
        g2.translate(0f, 0f);
        g2.translate(0f, - pi * pageHeight);

        if (pi == totalNumPages - 1) {
            g2.setClip(
                0, (int)(pageHeight * pi),
                (int)Math.ceil(pageWidth),
                (int)(scaledComponentHeight - pageHeight * (totalNumPages - 1))
            );
        } else {
            g2.setClip(
                0, (int)(pageHeight * pi),
                (int)Math.ceil(pageWidth),
                (int)Math.ceil(pageHeight)
            );
        }

        g2.scale(scale, scale);

        boolean wasBuffered = isDoubleBuffered();
        paint(g2);
        setDoubleBuffered(wasBuffered);

        return Printable.PAGE_EXISTS;
    }
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX