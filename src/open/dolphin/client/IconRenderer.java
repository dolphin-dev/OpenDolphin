/*
 * IconRenderer.java
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

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Icon Renderer. Core Java Foundation Class by Kim Topley.
 *
 */
public class IconRenderer extends DefaultTableCellRenderer {

    /** Creates new IconRenderer */
    public IconRenderer() {
        super();
        
        icon = new ColorFillIcon(Color.white, 10, 10, 1);
        setIcon(icon);
    }
    
    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean isFocused,
                            int row, int col) {
        Component c = super.getTableCellRendererComponent(
                                         table, 
                                         value,
                                         isSelected,
                                         isFocused, 
                                         row, col);
        if (value != null && value instanceof Integer) {
            int i = ((Integer)value).intValue();
            Color fill = null;
            switch (i) {
                case 0:
                    fill = Color.white;
                    break;
                case 1:
                    fill = Color.cyan;
                    break;
                case 2:
                    fill = Color.green;
                    break;
                case 3:
                    fill = Color.pink;
                    break;
            }                    
            icon.setFillColor(fill);
            ((JLabel)c).setText("");
        }
        else {
            icon.setFillColor(Color.white);
            ((JLabel)c).setText(value == null ? "" : value.toString());
        }
        return c;
    }
    
    protected ColorFillIcon icon;
}
