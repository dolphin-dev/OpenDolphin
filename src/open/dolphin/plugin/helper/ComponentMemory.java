/*
 * Created on 2005/06/17
 *
 */
package open.dolphin.plugin.helper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import java.util.prefs.Preferences;
import open.dolphin.client.ClientContext;

/**
 * ComponentMemory
 *
 * @author Kazushi Minagawa
 *
 */
public class ComponentMemory implements ComponentListener {
    
    private Component target;
    private Point defaultLocation;
    private Dimension defaultSise;
    private Preferences prefs;
    private String name;
    private boolean report = true;
    
    public ComponentMemory(Component target, Point loc, Dimension size, Object object) {
        this.target = target;
        this.defaultLocation = loc;
        this.defaultSise = size;
        if (object != null) {
            this.prefs = Preferences.userNodeForPackage(object.getClass());
            this.name = object.getClass().getName();
        }
        target.setLocation(this.defaultLocation);
        target.setSize(this.defaultSise);
        target.addComponentListener(this);
    }
    
    public void componentMoved(java.awt.event.ComponentEvent e) {
        Point loc = target.getLocation();
        if (prefs != null) {
            prefs.putInt(name + "_x", loc.x);
            prefs.putInt(name + "_y", loc.y);
        }
        if (report) {
            StringBuffer buf = new StringBuffer();
            buf.append(name);
            buf.append(" loc=(");
            buf.append(loc.x);
            buf.append(",");
            buf.append(loc.y);
            buf.append(")");
            System.out.println(buf.toString());
        }
    }
    
    public void componentResized(java.awt.event.ComponentEvent e) {
        int width = target.getWidth();
        int height = target.getHeight();
        if (prefs != null) {
            prefs.putInt(name + "_width", width);
            prefs.putInt(name + "_height", height);
        }
        if (report) {
            StringBuffer buf = new StringBuffer();
            buf.append(name);
            buf.append(" size=(");
            buf.append(width);
            buf.append(",");
            buf.append(height);
            buf.append(")");
            System.out.println(buf.toString());
        }
    }
    
    public void componentShown(java.awt.event.ComponentEvent e) {}
    
    public void componentHidden(java.awt.event.ComponentEvent e) {}
    
    public void setToPreferenceBounds() {
        if (prefs != null) {
            int x = prefs.getInt(name + "_x", defaultLocation.x);
            int y = prefs.getInt(name + "_y", defaultLocation.y);
            int width = prefs.getInt(name + "_width", defaultSise.width);
            int height = prefs.getInt(name + "_height", defaultSise.height);
            target.setBounds(x, y, width, height);
        }
    }
    
    public void putCenter() {
        if (ClientContext.isMac()) {
            putCenter(3);
        } else {
            putCenter(2);
        }
    }
    
    public void putCenter(int n) {
        n = n != 0 ? n : 2;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = target.getSize();
        int x = (screenSize.width - size.width) / 2;
        int y = (screenSize.height - size.height) / n;
        target.setBounds(x, y, size.width, size.height);
    }
}
