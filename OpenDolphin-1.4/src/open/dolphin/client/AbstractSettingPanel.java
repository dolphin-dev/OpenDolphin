package open.dolphin.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;

import open.dolphin.project.ProjectStub;
import org.apache.log4j.Logger;

/**
 * AbstractSettingPanel
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractSettingPanel {
    
    public static final String STATE_PROP   = "stateProp";
    public enum State {NONE_STATE,VALID_STATE,INVALID_STATE};
    
    private ProjectSettingDialog context;
    private ProjectStub projectStub;
    private PropertyChangeSupport boundSupport;
    protected AbstractSettingPanel.State state = AbstractSettingPanel.State.NONE_STATE;
    private JPanel ui;
    private boolean loginState;
    private String title;
    private String icon;
    private String id;
    
    Logger logger;
    
    /** 
     * Creates a new instance of SettingPanel 
     */
    public AbstractSettingPanel() {
        setUI(new JPanel());
        logger = ClientContext.getBootLogger();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public ProjectSettingDialog getContext() {
        return context;
    }
    
    public void setContext(ProjectSettingDialog context) {
        this.context = context;
        this.addPropertyChangeListener(STATE_PROP, context);
        this.setLogInState(context.getLoginState());
    }
    
    public boolean isLoginState() {
        return loginState;
    }
    
    public void setLogInState(boolean login) {
        loginState = login;
    }
    
    public JPanel getUI() {
        return ui;
    }
    
    public void setUI(JPanel p) {
        ui = p;
    }
    
    public abstract void start();
    
    public abstract void save();
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public ProjectStub getProjectStub() {
        return projectStub;
    }
    
    public void setProjectStub(ProjectStub projectStub) {
        this.projectStub = projectStub;
    }
    
    /**
     * @param state The state to set.
     */
    protected void setState(AbstractSettingPanel.State state) {
        this.state = state;
        boundSupport.firePropertyChange(STATE_PROP, null, this.state);
    }
    
    /**
     * @return Returns the state.
     */
    protected AbstractSettingPanel.State getState() {
        return state;
    }
}
