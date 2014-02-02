package open.dolphin.helper;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Kazushi Minagawa.
 */
public class KeyBlocker implements KeyListener {
    
    private Component target;
    
    public KeyBlocker(Component target) {
        this.target = target;
    }
    
    public void block() {
        target.addKeyListener(this);
    }
    
    public void unblock() {
        target.removeKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    /** Handle the key-pressed event from the text field. */
    @Override
    public void keyPressed(KeyEvent e) {
        e.consume();
    }

    /** Handle the key-released event from the text field. */
    @Override
    public void keyReleased(KeyEvent e) {
        e.consume();
    }
}
