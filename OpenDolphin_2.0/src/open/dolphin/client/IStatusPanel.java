package open.dolphin.client;

import java.beans.PropertyChangeListener;
import javax.swing.JProgressBar;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface IStatusPanel extends PropertyChangeListener {
    
    public void setMessage(String msg);
    
    public void setRightInfo(String info);
    
    public void setLeftInfo(String info);
    
    public void setTimeInfo(long time);
    
    public JProgressBar getProgressBar();
    
}
