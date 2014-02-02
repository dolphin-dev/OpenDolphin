/*
 * SettingPanel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2005 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;

import open.dolphin.project.ProjectStub;

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
    
    /** 
     * Creates a new instance of SettingPanel 
     */
    public AbstractSettingPanel() {
        setUI(new JPanel());
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
