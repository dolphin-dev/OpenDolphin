/*
 * IComponentHolder.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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

import javax.swing.text.*;
import java.beans.*;

/**
 * IComponentHolder
 *
 * @author  Kauzshi Minagawa
 */
public interface IComponentHolder extends PropertyChangeListener {
    
    public static final int TT_STAMP = 0;
    
    public static final int TT_IMAGE = 1;
    
    public int getContentType();
    
    public KartePane getKartePane();
    
    public boolean isSelected();
    
    public void setSelected(boolean b);
    
    public void edit();
    
    public void propertyChange(PropertyChangeEvent e);
    
    public void setEntry(Position start, Position end);
    
    public int getStartPos();
    
    public int getEndPos();

}