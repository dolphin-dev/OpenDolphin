package open.dolphin.client;

import java.lang.reflect.Method;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * CallbackDocumentListener
 *
 * @author Minagawa,Kazushi
 */
public class ReflectDocumentListener implements DocumentListener {
    
    private Object target;
    private String method;
    
    public ReflectDocumentListener(Object target, String method) {
        this.target = target;
        this.method = method;
    }
    
    public void changedUpdate(DocumentEvent e) {
    }
    
    public void insertUpdate(DocumentEvent e) {
        callback();
    }
    
    public void removeUpdate(DocumentEvent e) {
        callback();
    }
    
    private void callback() {
        
        if (target != null && method != null) {
            
            try {
                Method mth = target.getClass().getMethod(method, (Class[]) null);
                mth.invoke(target, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
