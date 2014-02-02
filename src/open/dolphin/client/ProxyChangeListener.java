package open.dolphin.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.swing.event.ChangeListener;

/**
 * CallbackDocumentListener
 *
 * @author Minagawa,Kazushi
 */
public class ProxyChangeListener {
    
    public static ChangeListener create(final Object target, final String methodName) {
        
        Class cls = ChangeListener.class;
        ClassLoader cl = cls.getClassLoader();
        
        InvocationHandler handler = new InvocationHandler() {
            
            public Object invoke(Object proxy, Method method, Object[] args) {
                
                Object result = null;
                
                try {
                    
                    Method targetMethod = target.getClass().getMethod(methodName, (Class[]) null);
                    result = targetMethod.invoke(target, (Object[]) null);
                    
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
                    
                return result;
            }
        };
        
        return (ChangeListener) Proxy.newProxyInstance(cl, new Class[]{cls}, handler);
        
    }
}
