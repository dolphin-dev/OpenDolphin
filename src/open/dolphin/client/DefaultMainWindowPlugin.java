/*
 * Created on 2005/07/12
 *
 */
package open.dolphin.client;

import javax.swing.JFrame;
import javax.swing.JPanel;

import open.dolphin.plugin.ILongTask;

public class DefaultMainWindowPlugin implements IMainWindowPlugin {
    
    private String jndiName;
    private MainWindow context;
    private String title;
    private JPanel ui;
    
    public DefaultMainWindowPlugin() {
        setUI(new JPanel());
    }
    
    public String getJNDIname() {
        return jndiName;
    }
    
    public void setJNDIName(String jndiName) {
        this.jndiName = jndiName;
    }
    
    public MainWindow getContext() {
        return context;
    }
    
    public void setContext(MainWindow ctx) {
        context = ctx;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void start() {
        context.pluginStarted(this);
    }
    
    public void stop() {
        context.pluginStopped(this);
    }
    
    public void enter() {}
    
    public ILongTask getStartUpTask() {
        return null;
    }
    
    public ILongTask getStoppingTask() {
        return null;
    }
    
    public JPanel getUI() {
        return ui;
    }
    
    public void setUI(JPanel ui) {
        this.ui = ui;
    }
    
    public JFrame getFrame() {
        return getContext().getFrame();
    }
    
    public void toFront() {
    }
}
