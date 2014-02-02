/*
 * IStampModelEditor.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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

import java.beans.*;

/**
 * Stamp Model Editor が実装するインターフェイス。 
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 **/
public interface IStampModelEditor {
    
    public String getTitle();
    
    public Object getValue();
    
    public void setValue(Object o);
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l);
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l);
    
    public void dispose();
}