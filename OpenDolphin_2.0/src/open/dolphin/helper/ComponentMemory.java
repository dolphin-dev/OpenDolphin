package open.dolphin.helper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;

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
    private String name;
    private boolean report = true;
    
    public ComponentMemory(Component target, Point loc, Dimension size, Object object) {
        this.target = target;
        this.defaultLocation = loc;
        this.defaultSise = size;
        if (object != null) {
            //this.prefs = Preferences.userNodeForPackage(object.getClass());
            this.name = object.getClass().getName();
        }
        target.setLocation(this.defaultLocation);
        target.setSize(this.defaultSise);
        target.addComponentListener(ComponentMemory.this);
    }
    
    @Override
    public void componentMoved(java.awt.event.ComponentEvent e) {
        Point loc = target.getLocation();
        //if (prefs != null) {
            Project.setInt(name + "_x", loc.x);
            Project.setInt(name + "_y", loc.y);
        //}
        if (report) {
            StringBuilder buf = new StringBuilder();
            buf.append(name);
            buf.append(" loc=(");
            buf.append(loc.x);
            buf.append(",");
            buf.append(loc.y);
            buf.append(")");
        }
    }
    
    @Override
    public void componentResized(java.awt.event.ComponentEvent e) {
        int width = target.getWidth();
        int height = target.getHeight();
        //if (prefs != null) {
            Project.setInt(name + "_width", width);
            Project.setInt(name + "_height", height);
        //}
        if (report) {
            StringBuilder buf = new StringBuilder();
            buf.append(name);
            buf.append(" size=(");
            buf.append(width);
            buf.append(",");
            buf.append(height);
            buf.append(")");
        }
    }
    
    @Override
    public void componentShown(java.awt.event.ComponentEvent e) {}
    
    @Override
    public void componentHidden(java.awt.event.ComponentEvent e) {}
    
    public void setToPreferenceBounds() {
        //if (prefs != null) {
            int x = Project.getInt(name + "_x", defaultLocation.x);
            int y = Project.getInt(name + "_y", defaultLocation.y);
            int width = Project.getInt(name + "_width", defaultSise.width);
            int height = Project.getInt(name + "_height", defaultSise.height);
            target.setBounds(x, y, width, height);
        //}
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
