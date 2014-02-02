package open.dolphin.impl.scheam;

import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Shortcut key を登録する
 * @author pns
 */
public class ShortcutKey extends AbstractAction {
    private AbstractButton button;
    
    private ShortcutKey(AbstractButton button) {
        this.button = button;
    }

    public static void register(SchemaCanvasView view, AbstractButton b, int key, int modifier, String name) {
        InputMap im = view.getRootPane().getInputMap();
        ActionMap am = view.getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(key, modifier), name);
        am.put(name, new ShortcutKey(b));
    }

    public void actionPerformed(ActionEvent e) {
        button.doClick();
    }
}
