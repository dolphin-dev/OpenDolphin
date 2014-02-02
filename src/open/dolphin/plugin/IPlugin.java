/*
 * IPlugin.java
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

import java.io.Serializable;

import open.dolphin.exception.PluginException;


/**
 * Plugin interface.
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public interface IPlugin extends Serializable {
	
	public String getName();
    
	public void setName(String name);
    
	public String getTitle();
    
	public void setTitle(String name);
	
	public void init() throws PluginException;
        
	public void start();
        
	public void stop();
}
