/*
 * StampTreeModel.java
 * Copyright (C) 2001,2003,2004 Digital Globe, Inc. All rights reserved.
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

import javax.swing.tree.*;

import open.dolphin.infomodel.ModuleInfoBean;

/**
 * スタンプツリーのモデルクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampTreeModel extends DefaultTreeModel {
    
    private static final long serialVersionUID = -2227174337081687786L;

	/**
     * デフォルトコンストラクタ
     */
    public StampTreeModel(TreeNode node) {
        super(node);
    }
    
    /**
     * ノード名の変更をインターセプトして処理する
     */
    public void valueForPathChanged (TreePath path, Object newValue) {
        
        // 変更ノードを取得する
        StampTreeNode node = (StampTreeNode) path.getLastPathComponent();
        
        // Debug
        //String oldString = node.toString ();
        String newString = (String) newValue;
        //System.out.println (oldString + " -> " + newString);
        
        /**
         * 葉ノードの場合は StampInfo の name を変更する
         * そうでない場合は新しい文字列を userObject に設定する
         */
        if (node.isLeaf()) {
            ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
            info.setStampName(newString);
        
        } else {
            node.setUserObject(newString);
        }
        
        // リスナへ通知する
        nodeChanged (node);       
    }
}