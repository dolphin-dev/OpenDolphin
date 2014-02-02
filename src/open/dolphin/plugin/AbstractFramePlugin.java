/*
 * AbstractFramePlugin.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.plugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import open.dolphin.client.*;
import open.dolphin.exception.PluginException;


/**
 * MainWindow から起動されるプラグインの Abstract class
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractFramePlugin extends JFrame implements IMainWindowPlugin {    
               
    /** Creates new AbstractDolphinService */
    public AbstractFramePlugin() {
    }
               
    public void init() throws PluginException {
    	initFrame();
    	initComponent();
    }
    
	public void start() {
		this.setVisible(true);
	}
    
	public void stop() {
		saveFrameLocation();
		saveFrameDimension();
		this.setVisible(false);
		this.dispose();
        
		// Tells the plugin-container closed this plugin.
		try {
			ClientContext.remove(this);
            
		} catch (NullPointerException ne) {
			// ignore
			System.out.println(ne);
            
		} catch (Exception ne2) {
			// ignore
			System.out.println(ne2);
		}
	}    
                
    public void showUI() {
        this.setVisible(true);
        this.toFront();
    }
    
    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
        
        String prop = e.getPropertyName();
        if ( ! prop.equals("exitProp")) {
            return;
        }
        
        boolean exit = ((Boolean)e.getNewValue()).booleanValue();
        if (! exit) {
            return;
        }   
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        if (prop.equals("exitProp")) {
            boolean b = ((Boolean)e.getNewValue()).booleanValue();
            if (b) {
                stop();
            }
        }
    }    
    
	public void initFrame() {
		
		this.setTitle(getTitle() + "-" + ClientContext.getString("application.title"));
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.addComponentListener(new ComponentListener() {
			
			public void componentMoved(java.awt.event.ComponentEvent componentEvent) {
				Point loc = getLocation();
				System.out.println(getName() + " : x=" + loc.x+ " y=" + loc.y);
			}
    
			public void componentResized(java.awt.event.ComponentEvent componentEvent) {
				int width = getWidth();
				int height = getHeight();
				System.out.println(getName() + " : width=" + width + " height=" + height);
			}
    
			public void componentShown(java.awt.event.ComponentEvent componentEvent) {
			}
    
			public void componentHidden(java.awt.event.ComponentEvent componentEvent) {
			}			
		});
		
		this.addWindowListener(new WindowAdapter() {
		
			public void windowClosing(WindowEvent e) {
				processWindowClosing();
			}
			
			public void windowOpened(WindowEvent e) {
				processWindowOpened();
			}			
		});		
	}
	
	public abstract void initComponent();
    
    public void processWindowClosing() {
        stop();
    }
    
	public void processWindowOpened() {
	}    
    
    public void centerFrame(Dimension size, Component c) { 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - size.width) / 2;
        int y = (screenSize.height - size.height) / 2;
        this.getContentPane().add(c);
		setToPreferenceBounds(x, y, size.width, size.height);
    }
    
    void saveFrameLocation() {
        Preferences prefs = ClientContext.getPreferences();
        Point loc = this.getLocation();
        prefs.putInt(this.getName() + ".locX", loc.x);
        prefs.putInt(this.getName() + ".locY", loc.y);
    }
    
    void saveFrameDimension() {
        int width = this.getWidth();
        int height = this.getHeight();
        Preferences prefs = ClientContext.getPreferences();
        prefs.putInt(this.getName() + ".width", width);
        prefs.putInt(this.getName() + ".height", height);
    }
    
    public void setToPreferenceBounds(int defaultX, int defaultY,int defaultW, int defaultH) {
        Preferences prefs = ClientContext.getPreferences();
        String id = this.getName();
        int x = prefs.getInt(id + ".locX", defaultX);
        int y = prefs.getInt(id + ".locY", defaultY);
        int width = prefs.getInt(id + ".width", defaultW);
        int height = prefs.getInt(id + ".height", defaultH);
		this.setBounds(x, y, width, height);
    }
    
    public void debug(String msg) {
    	if (ClientContext.isDebug()) {
    		System.out.println(msg);
    	}
    }
}