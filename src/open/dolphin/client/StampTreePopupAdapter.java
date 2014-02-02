/*
 * StampTreePopupAdapter.java
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
import javax.swing.tree.*;

import open.dolphin.infomodel.ModuleInfoBean;

import java.awt.*;
import java.awt.event.*;

/**
 * StampTreePopupAdapter
 *
 * @author  Kazushi Minagawa
 */
public class StampTreePopupAdapter extends MouseAdapter {
    
    public StampTreePopupAdapter() {
    }
    
    public void mousePressed(MouseEvent evt) {
        maybePopup(evt);
    }
    
    public void mouseReleased(MouseEvent evt) {
        maybePopup(evt);
    }
    
    private void maybePopup(MouseEvent evt) {
        
        if (evt.isPopupTrigger()) {
            
            // イベントソースの StampTree を取得する
            StampTree tree = (StampTree) evt.getSource();
            int x = evt.getX();
            int y = evt.getY();
            
            // クリック位置へのパスを得る
            TreePath destPath = tree.getPathForLocation(x, y);
            if (destPath == null) {
                return;
            }
            
            // クリック位置の Node を得る
            StampTreeNode node = (StampTreeNode) destPath.getLastPathComponent();
            
            if (node.isLeaf()) {
                // Leaf なので StampInfo 　を得る
                ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                
                // Editable
                if ( ! info.isEditable() ) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            
            // Popupする
            JPopupMenu popup = PopupMenuFactory.create("stampTree.pop", tree);
            popup.show(evt.getComponent(),x, y);
        }
    }
}