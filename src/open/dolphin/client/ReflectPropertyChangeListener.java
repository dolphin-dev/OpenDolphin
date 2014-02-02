package open.dolphin.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

/**
 * ReflectPropertyChangeListener
 *
 * @author Minagawa, Kazushi
 *
 */
public class ReflectPropertyChangeListener implements PropertyChangeListener {
    
    private Object target;
    private String method;
    private Class[] argClasses;
    
    public ReflectPropertyChangeListener(Object target, String method, Class[] argClasses) {
        this.target = target;
        this.method = method;
        this.argClasses = argClasses;
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        
        Object obj = e.getNewValue();
        if (obj != null && target != null && method !=null && argClasses != null) {
            try {
                Method mth = target.getClass().getMethod(method, argClasses);
                mth.invoke(target, new Object[]{obj});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}