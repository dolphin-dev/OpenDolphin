/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import open.dolphin.utilities.common.MDIEvent;

/**
 *
 * @author oh
 */
public class MDI {
    
    JDesktopPane desktop;
    MDIEvent event;
    int childCount;
    
    
    public MDI() {
        childCount = 1;
    }
    
    public void createDesktopPane() {
        desktop = new JDesktopPane();
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        desktop.setBackground(Color.GRAY);
        event = new MDIEvent();
        desktop.setDesktopManager(event.getManager());
    }
    
    public void createChildFrame(String title, int x, int y, int width, int height) {
        JInternalFrame child = new JInternalFrame(title, true, true, true, true);
        child.setSize(width, height);
        child.setLocation(x, y);
        desktop.add(child, new Integer(childCount));
        childCount += 1;
        if(event != null) child.addInternalFrameListener(event.getListener());
        child.setVisible(true);
        try {
            child.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(MDI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("TestFrame");
        frame.setSize(500, 500);
        MDI mdi = new MDI();
        mdi.createDesktopPane();
        mdi.createChildFrame("Test", 0, 0, 200, 100);
        frame.setVisible(true);
    }
}
