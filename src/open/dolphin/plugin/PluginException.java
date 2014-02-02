/*
 * Created on 2004/01/29
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
package open.dolphin.plugin;


/**
 * PluginException
 * 
 * @author Kazushi Minagawa
 *
 */
public class PluginException extends Exception {
	
	private static final long serialVersionUID = 3811316163084384968L;

	public PluginException(String msg) {
		super(msg);
	}
    
    public PluginException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public PluginException(String s, java.lang.Throwable t) {        
        super(s, t);
    }     

}
