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

/**
 * リソースデータから PopupMenu を生成するクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PopupMenuFactory {
    
    private PopupMenuFactory () {
    }
    
    /**
     * リソースとターゲットオブジェクトから PopupMenu を生成して返す。
     * @param resource リソース名
     * @target メソッドを実行するオブジェクト
     */
    public static JPopupMenu create(String resource, Object target) {
        
        JPopupMenu popMenu = new JPopupMenu ();
        
        String[] itemLine = ClientContext.getStringArray(resource + ".items");
        String[] methodLine = ClientContext.getStringArray(resource + ".methods");
        
        for (int i = 0; i < itemLine.length; i++) {
            
            String name = itemLine[i];
            String method = methodLine[i];
            
            if (name.equals("-")) {
                popMenu.addSeparator();
            }
            else {
                ReflectAction action = new ReflectAction(name, target, method);
                JMenuItem item = new JMenuItem(action);
                popMenu.add(item);
            }
        }
        return popMenu;
    }
}


