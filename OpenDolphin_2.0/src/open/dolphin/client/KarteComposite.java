package open.dolphin.client;

import java.awt.Component;
import javax.swing.ActionMap;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public interface KarteComposite {
    
    public void enter(ActionMap map);
    
    public void exit(ActionMap map);
    
    public Component getComponent();
    
}
