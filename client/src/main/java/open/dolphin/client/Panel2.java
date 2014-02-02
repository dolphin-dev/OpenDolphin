package open.dolphin.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author  Junzo SATO
 */
public class Panel2 extends JPanel implements Printable {
	
    private String patientName;

    private boolean printName;
    
    private int height;
    
    /** Creates a new instance of Panel2 */
    public Panel2() {
    }
    
    public void printPanel(PageFormat pageFormat, 
                           int numOfCopies,
                           boolean useDialog, String patientName, int height, boolean printName) {
        
        this.patientName = patientName + " 様カルテ";
        this.height = height;
        this.printName = printName;
        
        boolean buffered = this.isDoubleBuffered();
        this.setDoubleBuffered(false);
        useDialog = true;
        
        //----------------------------------------------------------------------
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setCopies(numOfCopies);
        pj.setJobName(patientName + " by Dolphin");
        if (pageFormat == null) {
            pageFormat = pj.defaultPage();
        }
        pj.setPrintable(this, pageFormat);
        
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException printErr) {
                printErr.printStackTrace(System.err);
            }
        }
        //----------------------------------------------------------------------
        this.setDoubleBuffered(buffered);
    }
    
    @Override
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        
        Graphics2D g2 = (Graphics2D) g;
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
        double componentHeight = height == 0 ? this.getSize().getHeight() : (double) height;
        double componentWidth = this.getSize().getWidth();
        
        //
        double scale = 1d;
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
        StringBuilder sb = new StringBuilder();
        if (printName) {
            sb.append(patientName);
        }
        sb.append("  Page: ");
        sb.append((pi + 1));
        sb.append(" of ");
        sb.append(totalNumPages);
        String footerString =  sb.toString();
        int strW = SwingUtilities.computeStringWidth(g2.getFontMetrics(), footerString);
//s.oh^ 不具合修正
        if(ClientContext.isWin()) {
            Font footerFont = new Font("MS UI Gothic", Font.PLAIN, 9);
            g2.setFont(footerFont);
            fontDescent = g2.getFontMetrics().getDescent();
        }
//s.oh$
        g2.drawString(
            footerString, 
            (int)pageWidth/2 - strW/2,
            (int)(pageHeight + fontHeight - fontDescent)
            //(int)(pageHeight + fontHeight)
        );
//s.oh^ 不具合修正
        if(ClientContext.isWin()) {
            g2.setFont(f);
        }
//s.oh$

        // page
        g2.translate(0d, 0d);
        g2.translate(0d, - pi * pageHeight);

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
        //paint(g2);
        print(g2);
        setDoubleBuffered(wasBuffered);

        return Printable.PAGE_EXISTS;
    }    
}
