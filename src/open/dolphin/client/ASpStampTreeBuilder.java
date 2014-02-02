/*
 * DefaultStampTreeBuilder.java
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

import java.util.*;

import org.apache.log4j.Logger;

import open.dolphin.infomodel.ModuleInfoBean;

/**
 * DefaultStampTreeBuilder
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ASpStampTreeBuilder extends AbstractStampTreeBuilder {
    
    /** Control staffs */
    private StampTreeNode rootNode;
    private StampTreeNode node;
    private ModuleInfoBean info;
    private LinkedList<StampTreeNode> linkedList;
    private List<StampTree> products;
    
    private Logger logger = ClientContext.getLogger("boot");
    
    
    /** Creates new DefaultStampTreeBuilder */
    public ASpStampTreeBuilder() {
    }
    
    /**
     * Returns the product of this builder
     * @return vector that contains StampTree instances
     */
    public List<StampTree> getProduct() {
        return products;
    }
    
    public void buildStart() {
        products = new ArrayList<StampTree>();
        if (logger != null) {
            logger.debug("Build StampTree start");
        }
    }
    
    public void buildRoot(String name, String entity) {
        // New root
        if (logger != null) {
            logger.debug("Root=" + name);
        }
        linkedList = new LinkedList<StampTreeNode>();
        
        // TreeInfo を rootNode に保存する
        TreeInfo treeInfo = new TreeInfo();
        treeInfo.setName(name);
        treeInfo.setEntity(entity);
        rootNode = new StampTreeNode(treeInfo);
        linkedList.addFirst(rootNode);
    }
    
    public void buildNode(String name) {
        // New node
        if (logger != null) {
            logger.debug("Node=" + name);
        }
        
        node = new StampTreeNode(name);
        getCurrentNode().add(node);
        
        // Add the new node to be current node
        linkedList.addFirst(node);
    }
    
    public void buildStampInfo(String name,
            String role,
            String entity,
            String editable,
            String memo,
            String id) {
        
        if (logger != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(",");
            sb.append(role);
            sb.append(",");
            sb.append(entity);
            sb.append(",");
            sb.append(editable);
            sb.append(",");
            sb.append(memo);
            sb.append(",");
            sb.append(id);
            logger.debug(sb.toString());
        }
        
         // ASP Tree なのでエディタから発行を無視する
        if (name.equals("エディタから発行...") && (id == null) && (role.equals("p")) ) {
            return;
        }
        
        info = new ModuleInfoBean();
        info.setStampName(name);
        info.setStampRole(role);
        info.setEntity(entity);
        if (editable != null) {
            info.setEditable(Boolean.valueOf(editable).booleanValue());
        }
        if (memo != null) {
            info.setStampMemo(memo);
        }
        if ( id != null ) {
            info.setStampId(id);
        }
        
        // StampInfo から TreeNode を生成し現在のノードへ追加する
        node = new StampTreeNode(info);
        getCurrentNode().add(node);
    }
    
    public void buildNodeEnd() {
        if (logger != null) {
            logger.debug("End node");
        }
        linkedList.removeFirst();
    }
    
    public void buildRootEnd() {
        
        StampTree tree = new StampTree(new StampTreeModel(rootNode));
        products.add(tree);
        
        if (logger != null) {
            int pCount = products.size();
            logger.debug("End root " + "count=" + pCount);
        }
    }
    
    public void buildEnd() {
        if (logger != null) {
            logger.debug("Build end");
        }
    }
    
    private StampTreeNode getCurrentNode() {
        return (StampTreeNode) linkedList.getFirst();
    }
    
}