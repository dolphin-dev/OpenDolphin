package open.dolphin.client;

import java.beans.*;

/**
 * Stamp Model Editor が実装するインターフェイス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 **/
public interface IStampModelEditor {
    
    public IStampEditorDialog getContext();
    
    public void setContext(IStampEditorDialog context);
    
    public void start();
    
    public String getTitle();
    
    public Object getValue();
    
    public void setValue(Object o);
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l);
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l);
    
    public boolean isValidModel();
    
    public void setValidModel(boolean b);
    
    public void dispose();
}