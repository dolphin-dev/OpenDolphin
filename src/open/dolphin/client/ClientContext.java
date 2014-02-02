/*
 * DolphinContext.java
 *
 * Copyright 2002 Dolphin Project. All rights resrerved.
 * Copyright 2004 Digital Globe, Inc. All rights resrerved.
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

import java.io.*;
import java.net.*;
import java.util.logging.Logger;
import java.util.prefs.*;
import java.awt.*;
import javax.swing.*;

import open.dolphin.plugin.IPlugin;
import open.dolphin.project.*;

import org.apache.velocity.VelocityContext;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClientContext {
    
    private static ClientContextStub stub;

    /** Creates new DolphinContext */
    public ClientContext() {
    }
    
    public static void setClientContextStub(ClientContextStub s) {
        stub = s;
    }
    
    public static ClientContextStub getClientContextStub() {
        return stub;
    } 
    
    public static boolean isDebug() {
    	return stub.isDebug();   
    }
        
    ///////////////////////////////////////////////////////////////////////////
    
    public static void loadServices() {
        stub.loadServices();
    }
    
    public static IPlugin getPlugin(String name) {
        return stub.getPlugin(name);
    }
    
    public static String[] getServiceNames() {
        return stub.getServiceNames();
    }
    
    public static Object[] getServiceProxies() {
        return stub.getServiceProxies();
    }
    
    public static void remove(IPlugin service) {
        stub.remove(service);
    }
    
    public static void addCurrentService(IPlugin s) {
        stub.addCurrentService(s);
    }
    
    public static void removeCurrentService(IPlugin s) {
        stub.removeCurrentService(s);
    }     
    
    public static VelocityContext getVelocityContext() {
        return stub.getVelocityContext();
    }
    
    public static Logger getLogger() {
        return stub.getLogger();
    }
    
    public static Preferences getPreferences() {
        return stub.getPreferences();
    }
    
    public static boolean getExit() {
        return stub.getExit();
    }
    
    public static void setExit(boolean b) {
        stub.setExit(b);
    } 
    
    /*public static boolean getLogin() {
        return stub.getLogin();
    }
        
    public static void setLogin(boolean b) {
        stub.setLogin(b);
    } */   
    
    public static ProjectStub loadProject() {
        return stub.loadProject();
    }
    
    public static void storeProject(ProjectStub ps) {
        stub.storeProject(ps);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public static String getVersion() {
        return stub.getVersion();
    }
    
    public static String getUserDirectory() {
        return stub.getUserDirectory();
    }
        
    public static URL getResource(String name) {
        return stub.getResource(name);
    }
    
    public static URL getImageResource(String name) {
        return stub.getImageResource(name);
    }
    
    public static InputStream getResourceAsStream(String name) {
        return stub.getResourceAsStream(name);
    }
    
	public static InputStream getTemplateAsStream(String name) {
		return stub.getTemplateAsStream(name);
	}
    
    public static String getString(String name) {
        return stub.getString(name);
    }
    
    public static String[] getStringArray(String name) {
        return stub.getStringArray(name);
    }
    
    public static int[] getIntArray(String name) {
        return stub.getIntArray(name);
    }
    
    public static Color getColor(String name){
        return stub.getColor(name);
    }
    
    public static Color[] getColorArray(String name) {
        return stub.getColorArray(name);
    }
    
    public static ImageIcon getImageIcon(String name) {
        return stub.getImageIcon(name);
    }
}