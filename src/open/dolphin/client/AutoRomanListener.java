/*
 * AutoRomanListener.java
 *
 * Created on 2007/09/21, 20:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author kazm
 */
public class AutoRomanListener implements FocusListener {
    
    private static AutoRomanListener instance = new AutoRomanListener();
    
    /** Creates a new instance of AutoRomanListener */
    private AutoRomanListener() {
    }
    
    public static AutoRomanListener getInstance() {
        return instance;
    }

    public void focusGained(FocusEvent e) {
        JTextComponent tc = (JTextComponent) e.getSource();
        tc.getInputContext().setCharacterSubsets(null);
    }

    public void focusLost(FocusEvent focusEvent) {
    }
    
}
