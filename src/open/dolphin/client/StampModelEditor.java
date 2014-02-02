package open.dolphin.client;

import java.beans.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *　個々の StampEditor の root クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class StampModelEditor extends JPanel implements IStampModelEditor, ComponentListener {
    
    PropertyChangeSupport boundSupport;
    boolean isValidModel;
    String title;
    IStampEditorDialog context;
    
    /** Creates new StampModelEditor */
    public StampModelEditor() {
        boundSupport = new PropertyChangeSupport(this);
        addComponentListener(this);
    }
    
    public IStampEditorDialog getContext() {
        return context;
    }
    
    public void setContext(IStampEditorDialog context) {
        this.context = context;
    }
    
    public void start() {
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public boolean isValidModel() {
        return isValidModel;
    }
    
    public void setValidModel(boolean b) {
        boolean old = isValidModel;
        isValidModel = b;
        boundSupport.firePropertyChange("validData", old, isValidModel);
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        StringBuilder buf = new StringBuilder();
        buf.append(val);
        buf.append(ClientContext.getString("application.title.editorText"));
        buf.append(ClientContext.getString("application.title.separator"));
        buf.append(ClientContext.getString("application.title"));
        this.title = buf.toString();
    }
    
    public void dispose() {
    }
    
    public abstract Object getValue();
    
    public abstract void setValue(Object value);
    
    public void componentHidden(ComponentEvent e) {
    }
    
    public void componentMoved(ComponentEvent e) {
    }
    
    public void componentResized(ComponentEvent e) {
        Dimension dim = getSize();
        System.out.println("width=" + dim.width + " , height=" + dim.height);
    }
    
    public void componentShown(ComponentEvent e) {
    }
}