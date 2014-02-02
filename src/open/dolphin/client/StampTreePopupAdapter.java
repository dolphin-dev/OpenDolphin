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

import open.dolphin.infomodel.ModuleInfo;

import java.awt.*;
import java.awt.event.*;

/**
 * StampTree で共有する PopupMenu アダプタクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampTreePopupAdapter extends MouseAdapter { 
        
    /** Popup メニュー */
    private JPopupMenu popUpMenu;
    
    /** 操作対象の Tree イベントから通知される */
    private StampTree tree;
    
    /**
     * デフォルトコンストラクタ
     */
    public StampTreePopupAdapter() {
        // popUp メニューを生成する
        popUpMenu = open.dolphin.client.PopupMenuFactory.create("stampTree.pop.", this);
    }

    public void mousePressed(MouseEvent evt) {        
        // JDK1.4
       if (popUpMenu.isPopupTrigger(evt)) {
           startService (evt);
       }         
    }
    
    public void mouseReleased(MouseEvent evt) {        
        // JDK1.4
       if (popUpMenu.isPopupTrigger(evt)) {
           startService (evt);
       }         
    }
    
    private void startService(MouseEvent evt) {
        
       tree = (StampTree)evt.getSource();
       int x = evt.getX();
       int y = evt.getY();
       
       TreePath destPath = tree.getPathForLocation(x, y);
       if (destPath == null) {
           return;
       }
       
       // システムで提供するスタンプエディタ等で、編集不可のスタンプが
       // 選択された場合はポップアップしない
       
       // ASP-Tree は編集不可 
       //int mode = tree.getMode();
       //if (mode == StampTree.ASP_USER) {
           //return;
       //}
       if (!tree.isEdiatble()) {
       		return;
       }
       
       // クリック位置の Node を得る
       StampTreeNode node =(StampTreeNode)destPath.getLastPathComponent();
       
       if (node.isLeaf()) {
  
           // Leaf なので StampInfo 　を得る 
           ModuleInfo info = (ModuleInfo)node.getUserObject();

           // Editable
           if ( ! info.isEditable() ) {
                Toolkit.getDefaultToolkit().beep();
                return;
           }
       }
       
       tree.setSelectionPath(destPath);
       popUpMenu.show(evt.getComponent(),x, y);       
    }
    
    /**
     * ノードの名前を変更する
     */
    public void doRename (ActionEvent e) {
        tree.renameNode();
    }
    
    /**
     * ノードを削除する
     */
    public void doDelete (ActionEvent e) {       
        tree.deleteNode ();
    }
     
    /**
     * 新規のフォルダノードを追加する
     */
    public void doNewFolder(ActionEvent e) {        
        tree.createNewFolder();
    }
}