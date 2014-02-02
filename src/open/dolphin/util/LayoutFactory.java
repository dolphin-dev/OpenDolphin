/*
 * LayoutFactory.java
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
package open.dolphin.util;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  kazm
 */
public class LayoutFactory {
    
    /** Creates a new instance of LayoutFactory */
    public LayoutFactory() {
    }
    
    public static LayoutManager create(JPanel panel, String layout,
                                       String rows, String cols, 
                                       String hgap, String vgap
                                       ) {
        
        LayoutManager ret = null;
        
        layout = layout.toUpperCase();
        
        if (layout.equals("BOXX")) {
            ret = new BoxLayout(panel, BoxLayout.X_AXIS);
        
        } else if (layout.equals("BOXY")) {
            ret = new BoxLayout(panel, BoxLayout.Y_AXIS);
        
        } else if (layout.equals("BORDER")) {
            ret = new BorderLayout();
        
        } else if (layout.equals("FLOW")) {
            ret = new FlowLayout();
        
        } else if (layout.equals("GRID")) {
            System.out.println("LayoutFactory: Gridlayout");
            int row = Integer.parseInt(rows);
            int col = Integer.parseInt(cols);
            int h = Integer.parseInt(hgap);
            int v = Integer.parseInt(vgap);
            ret = new GridLayout(row, col, h, v);
        }
        
        return ret;
    }
    
}
