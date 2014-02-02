package open.dolphin.client;

import java.beans.PropertyChangeListener;
import javax.swing.JProgressBar;


public interface IStatusPanel extends PropertyChangeListener {
    
    public void setMessage(String msg);
    
//    public void start();
//    
//    public void start(String startMsg);
//    
//    public void stop();
//    
//    public void stop(String stopMsg);
    
    public void setRightInfo(String info);
    
    public void setLeftInfo(String info);
    
    //public void ready(TaskMonitor taskMonitor);
    
    public JProgressBar getProgressBar();
    
}
