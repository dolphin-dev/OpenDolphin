/*
 * RnDialog.java
 *
 * Created on 2002/04/03, 11:13
 */

package open.dolphin.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class RnDialog extends JDialog {
    
    Frame parent;
    Object value;
    
    /** 
     * Creates new RnDialog 
     */
    public RnDialog(Frame parent, String title, boolean modal) {
        
        super(parent, title, modal);
        this.parent = parent;
        
        JPanel compo = createComponent();
        compo.setBorder(BorderFactory.createEmptyBorder(12, 11, 12, 11));
        this.getContentPane().add(compo, BorderLayout.CENTER);
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                value = null;
            }
        });
    }
    
    public Object getValue() {
        return value;
    }
    
    public abstract void setValue(Object value);
    
    protected void centerDialog() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight() ) / 3;
        this.setLocation(x, y);
    }
    
    protected void centerFrame() {
        Dimension size = parent.getSize();
        Point loc = parent.getLocation();
        int x = loc.x + (size.width - this.getWidth()) / 2;
        int y = loc.y + (size.height - this.getHeight() ) / 3;
        this.setLocation(x, y);
    }    
    
    protected abstract JPanel createComponent();   
    
    protected void start() {
        this.pack();
        centerFrame();
        this.show();
    }   
}