/*
 * PropertyBinder.java
 *
 * Created on 2007/09/24, 22:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.ui;

/**
 *
 * @author kazm
 */
public interface PropertyBinder {
    
    public Object invokeGetter();
    
    public void invokeSetter(Object value);
    
}
