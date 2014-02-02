/*
 * Created on 2005/07/12
 *
 */
package open.dolphin.client;

import javax.swing.JFrame;
import javax.swing.JPanel;

import open.dolphin.plugin.ILongTask;

public interface IMainWindowPlugin {
    
    public String getJNDIname();
    
    public void setJNDIName(String name);
    
    public MainWindow getContext();
    
    public void setContext(MainWindow ctx);
    
    public String getTitle();
    
    public void setTitle(String title);
    
    public void start();
    
    public void stop();
    
    public void enter();
    
    public ILongTask getStartUpTask();
    
    public ILongTask getStoppingTask();
    
    public JPanel getUI();
    
    public void setUI(JPanel ui);
    
    public JFrame getFrame();
    
    public void toFront();
    
}
