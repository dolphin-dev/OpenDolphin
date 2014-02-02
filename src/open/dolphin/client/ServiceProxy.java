/*
 * ServiceProxy.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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

import java.util.*;
import javax.swing.*;

import open.dolphin.plugin.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ServiceProxy {
    
    private String name;
    private String id;
    private ImageIcon icon;
    private ImageIcon selectedIcon;
    private String className;
    private boolean startup;
    private boolean enabled;
    private boolean visible;
    private transient IPlugin service;
    private static ArrayList allServices = new ArrayList(20);

    /** Creates new ServiceProxy */
    public ServiceProxy() {
    }
    
    public ServiceProxy(String id, 
                        String name,
                        ImageIcon icon,
                        ImageIcon selectedIcon,
                        String className,
                        boolean startup,
                        boolean visible) {
        this();
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.selectedIcon = selectedIcon;
        this.className = className;
        this.startup = startup;
        this.visible = visible;
        enabled = true;
        allServices.add(this);
    }
    
    public static ArrayList getAllServices() {
        return allServices;
    }
    
    public String getServiceID() {
        return id;
    }

    public void setServiceID(String val) {
        id = val;
    }
    
    public String getServiceName() {
        return name;
    }

    public void setServiceName(String val) {
        name = val;
    }
   
    public String getClassName() {
        return className;
    }

    public void setClassName(String val) {
        className = val;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    public ImageIcon getSelectedIcon() {
        return selectedIcon;
    }
    
    public void setSelectedIcon(ImageIcon icon) {
        this.selectedIcon = icon;
    }
    
    public boolean isStartup() {
        return startup;
    }
    
    public void setStartup(boolean b) {
        startup = b;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean b) {
        visible = b;
    }    
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean b) {
        enabled = b;
    }    
    
    public IPlugin getService() {
        return service;
    }
    
    public void setService(IPlugin s) {
        service = s;
    }
}