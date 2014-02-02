/*
 * StampTreeNode.java
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

import open.dolphin.infomodel.ModuleInfo;

/**
 * StampTree のノードクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampTreeNode extends DefaultMutableTreeNode {
	
	private String treeId;

    /**
     * コンストラクタ
     */
    public StampTreeNode(Object userObject) {
        
        super(userObject);
        
        // StampInfo で初期化された場合は葉ノードにする
        if (userObject instanceof open.dolphin.infomodel.ModuleInfo) {
            this.allowsChildren = false;
        }
    }
    
    /**
     * 葉かどうかを返す
     */
    public boolean isLeaf () {
        return (! this.allowsChildren);
    }
    
    /**
     * StampInfo を返す
     */
    public ModuleInfo getStampInfo() {
        return (ModuleInfo)userObject;
    }

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public String getTreeId() {
		return treeId;
	}
}