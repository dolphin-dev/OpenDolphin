package open.dolphin.client;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.Icon;


public class ReflectAction extends AbstractAction {
    
    private static final long serialVersionUID = 4592935637937407137L;
    
    private Object target;
    private String method;
    
    
    public ReflectAction(String text) {
        super(text);
    }
    
    public void setTarget(Object target) {
        this.target = target;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public ReflectAction(String text, Object target, String method) {
        super(text);
        this.target = target;
        this.method = method;
    }
    
    public ReflectAction(String text, Icon icon, Object target, String method) {
        super(text, icon);
        this.target = target;
        this.method = method;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        try {
            Method mth = target.getClass().getMethod(method, (Class[]) null);
            mth.invoke(target, (Object[])null);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}