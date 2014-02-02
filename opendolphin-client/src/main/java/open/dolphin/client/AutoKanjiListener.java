package open.dolphin.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;

/**
 * 
 * @author Minagawa, Kazushi
 */
public class AutoKanjiListener implements FocusListener {
    
    private static AutoKanjiListener instance = new AutoKanjiListener();
    
    /** Creates a new instance of AutoIMEListener */
    private AutoKanjiListener() {
    }
    
    public static AutoKanjiListener getInstance() {
        return instance;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (ClientContext.isWin()) {
            Object source = e.getSource();
            if (source != null && source instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) source;
                if (tc.getInputContext() != null) {
                    tc.getInputContext().setCompositionEnabled(true);
                }
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (ClientContext.isWin()) {
            Object source = e.getSource();
            if (source != null && source instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) source;
                if (tc.getInputContext() != null) {
                    tc.getInputContext().setCompositionEnabled(false);
                }
            }
        }
    }
}
