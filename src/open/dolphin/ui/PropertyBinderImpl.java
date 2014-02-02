/*
 * PropertyBinder.java
 *
 * Created on 2007/09/24, 21:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.ui;

import java.lang.reflect.Method;
import org.jboss.util.property.Property;

/**
 *
 * @author kazm
 */
public class PropertyBinderImpl implements PropertyBinder {
    
    private Object target;
    private String property;
    private Class paramType;
    
    /** 
     * Creates a new instance of PropertyBinder 
     */
    public PropertyBinderImpl() {
    }
    
    public PropertyBinderImpl(Object target, String property) {
        this.target = target;
        this.property = property;
    }
    
    public Object invokeGetter() {
        
        Object value = null;
        
        try {
            Method getter = target.getClass().getMethod(toGetter(property), (Class[]) null);
            if (paramType == null) {
                paramType = getter.getReturnType();
            }
            value = getter.invoke(target, (Object[]) null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return value;
    }
    
    public void invokeSetter(Object value) {
        
        try {
            Method setter = target.getClass().getMethod(toSetter(property), new Class[]{paramType});
            setter.invoke(target, new Object[]{value});
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String toGetter(String property) {
        StringBuilder sb = new StringBuilder();
        sb.append("get");
        sb.append(property.substring(0, 1).toUpperCase());
        sb.append(property.substring(1));
        return sb.toString();
    }
        
    private String toSetter(String property) {
        StringBuilder sb = new StringBuilder();
        sb.append("set");
        sb.append(property.substring(0, 1).toUpperCase());
        sb.append(property.substring(1));
        return sb.toString();
    }
}
