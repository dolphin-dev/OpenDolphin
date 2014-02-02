package open.dolphin.client;

import javax.swing.ActionMap;
import javax.swing.JComponent;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public interface IKarteTransferHandler {

    public JComponent getComponent();

    public void enter(ActionMap map);

    public void exit(ActionMap map);
}
