package open.dolphin.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

/**
 * ReflectActionListener
 *
 * @author Minagawa,Kazushi
 *
 */
public class ReflectActionListener implements ActionListener {
    
    private Object target;
    private String method;
    private Class[] argClasses;
    private Object[] args;
    
    public ReflectActionListener() {
    }
    
    public ReflectActionListener(Object target, String method) {
        this();
        setTarget(target);
        setMethod(method);
    }
    
    public ReflectActionListener(Object target, String method, Class[] argClasses) {
        this();
        setTarget(target);
        setMethod(method);
        setArgClasses(argClasses);
    }
    
    public ReflectActionListener(Object target, String method, Class[] argClasses, Object[] args) {
        this(target, method, argClasses);
        setArgs(args);
    }
    
    public Class[] getArgClasses() {
        return argClasses;
    }
    
    public void setArgClasses(Class[] argClasses) {
        this.argClasses = argClasses;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void setArgs(Object[] args) {
        this.args = args;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Object getTarget() {
        return target;
    }
    
    public void setTarget(Object target) {
        this.target = target;
    }
    
    public void actionPerformed(ActionEvent e) {
        
        if (target != null && method != null) {
            
            try {
                Method mth = target.getClass().getMethod(method, argClasses);
                mth.invoke(target, args);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

