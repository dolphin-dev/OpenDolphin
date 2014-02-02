package open.dolphin.client;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 *
 * @author Kazushi Minagawa.
 */
public class TransferActionListener implements ActionListener,
                                              PropertyChangeListener {
    private JComponent focusOwner;
    
    public TransferActionListener() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", TransferActionListener.this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        Object o = e.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent) o;
        } else {
            focusOwner = null;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (focusOwner == null) {
            return;
        }
        String action = (String) e.getActionCommand();
        // FocusOwnerのActionMapからActionを得る
        Action a = focusOwner.getActionMap().get(action);
        if (a != null) {
            a.actionPerformed(new ActionEvent(focusOwner,
                                              ActionEvent.ACTION_PERFORMED,
                                              null));
        }
    }
}
