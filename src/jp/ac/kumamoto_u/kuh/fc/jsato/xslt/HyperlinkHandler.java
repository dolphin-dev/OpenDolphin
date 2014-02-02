/*
 * HyperLinkJEditorPane.java
 *
 * Created on 2002/01/12, 23:34
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.xslt;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.io.*;
import javax.swing.text.html.*;
import javax.swing.event.*;

public class HyperlinkHandler implements HyperlinkListener {
    private JEditorPane ep = null;
    
    public HyperlinkHandler(JEditorPane ep) {
        this.ep = ep;
    }
    
    public void ReadFileNoThreaded(File parentFile) {
        try {
            if (ep == null) {
                ep = new JEditorPane();
            }
            HTMLEditorKit htmlKit = new HTMLEditorKit();
            ep.setEditorKit(htmlKit);
            ep.addHyperlinkListener(this);
            ep.setEditable(false);
            //BufferedReader in = new BufferedReader(new StringReader("<HTML><TITLE>The Title</TITLE><BODY><I>italics</I></BODY></HTML>"));
            BufferedReader in = new BufferedReader(new FileReader(parentFile));
            htmlKit.read(in, ep.getDocument(),0);
        } catch (Exception e) {
            
        }
    }
    
    public void ReadFile(File parentFile) {
        try {
            if (ep == null) {
                ep = new JEditorPane();
            }
            HTMLEditorKit htmlKit = new HTMLEditorKit();
            ep.setEditorKit(htmlKit);
            ep.addHyperlinkListener(this);
            ep.setEditable(false);
            SwingUtilities.invokeLater(new MyLocalFileRun(
                ep,
                htmlKit,
                parentFile//new File("c:/HyperLink/hyperlink/a.htm")
            ));
        } catch (Exception e) {
            
        }
    }
    
    class MyLocalFileRun implements Runnable {
        JEditorPane pane = null;
        HTMLEditorKit kit = null;
        File file = null;
        
        public MyLocalFileRun(JEditorPane pane, HTMLEditorKit kit, File file) {
            MyLocalFileRun.this.pane = pane;
            MyLocalFileRun.this.kit = kit;
            MyLocalFileRun.this.file = file;
        }
        
        public void run() {
            try {
                //BufferedReader in = new BufferedReader(new StringReader("<HTML><TITLE>The Title</TITLE><BODY><I>italics</I></BODY></HTML>"));
                BufferedReader in = new BufferedReader(new FileReader(file));
                kit.read(in, pane.getDocument(),0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }  
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane)e.getSource();
            
            // If the link is a local file like 
            // <A href="file:///C:/HyperLink/hyperlink/p_ico018.gif">Link To File</A>
            // the specified href is detected.
            // However, if the href is only a filename, getting url would fail.
            //System.out.println(e.getURL().getPath());
            
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent)e;
                HTMLDocument doc = (HTMLDocument)pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                try {
                    //
                    //pane.setPage(e.getURL());
                    //
                    // XSLT.ShowEditorPaneInFrame(e.getURL().getPath());
                    XSLT.ShowSimpleEditorPaneInFrame(e.getURL());
                    //
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
