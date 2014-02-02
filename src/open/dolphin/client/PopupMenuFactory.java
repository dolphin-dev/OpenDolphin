/*
 * PopupMenuFactory.java
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
import java.awt.event.*;
import java.util.*;

/**
 * リソースデータから PopupMenu を生成するクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PopupMenuFactory {
    
    public PopupMenuFactory () {
        
        super ();
    }
    
    /**
     * リソース名と ActionListener から PopupMenu を生成して返す。
     * @param resource リソース名
     * @target ActionListener
     */
    public static JPopupMenu create (String resource, Object target) {
        
        JPopupMenu popMenu = new JPopupMenu ();
        
        String itemLine = ClientContext.getString(resource + "items");
        String cmdLine = ClientContext.getString(resource + "actions");
        
        StringTokenizer itemSt = new StringTokenizer(itemLine, ",");
        StringTokenizer cmdSt = new StringTokenizer(cmdLine , ",");
        
        String name;
        String cmd;
        ActionListener action;
        JMenuItem item;
        
        while (itemSt.hasMoreElements()) {
            
            name = itemSt.nextToken();
            cmd = cmdSt.nextToken();
            
            if (name.equals("-")) {
                
                popMenu.addSeparator();
            }
            else {
                
                item = new JMenuItem(name);
                action = (ActionListener) (GenericListener.create (
                                    ActionListener.class,
                                    "actionPerformed",
                                    target,
                                    cmd));
                item.addActionListener(action);
                popMenu.add(item);
            }
        }
        return popMenu;
    }
}


