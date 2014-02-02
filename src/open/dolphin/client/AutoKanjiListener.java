/*
 * AutoIMEListener.java
 *
 * Created on 2007/09/21, 19:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.im.InputSubset;
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

    public void focusGained(FocusEvent e) {
        JTextComponent tc = (JTextComponent) e.getSource();
        tc.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
    }

    public void focusLost(FocusEvent focusEvent) {
    }
}
