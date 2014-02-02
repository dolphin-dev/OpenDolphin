/*
 * Panel2.java
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
package open.dolphin.client;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

/**
 *
 * @author  Junzo SATO
 */
public class Panel2 extends JPanel implements Printable {
    
    String patientName;
    
    /** Creates a new instance of Panel2 */
    public Panel2() {
    }
    
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    public void printPanel(
        PageFormat pageFormat, 
        int numOfCopies,
        boolean useDialog, String name ) {
        
        /*if ( this.getRootPane() != null &&
             this.getRootPane().getParent() != null && 
             this.getRootPane().getParent().getClass().getName().equals("open.dolphin.client.ChartService") ) {
            JFrame f = (JFrame)this.getRootPane().getParent();
            patientName = f.getTitle() + " 患者 カルテ";
        }*/
        patientName = name + " カルテ";
        
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
        g2.setPaint(Color.black);
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
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    
    
}
