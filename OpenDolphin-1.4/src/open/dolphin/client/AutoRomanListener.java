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

    @Override
    public void focusGained(FocusEvent e) {
        Object source = e.getSource();
        if (source != null && source instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) source;
            if (tc.getInputContext() != null) {
                tc.getInputContext().setCharacterSubsets(null);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}
