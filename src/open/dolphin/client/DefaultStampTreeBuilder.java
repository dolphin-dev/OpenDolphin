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

import open.dolphin.infomodel.ModuleInfo;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultStampTreeBuilder {
    
	private String[] rootNames = {        
		"汎 用","その他","処 置","手 術","放射線","検体検査","生体検査",
		"細菌検査","注 射","処 方","初診・再診","指導・在宅"
	};
	private String[] entities = {        
		"generalOrder","otherOrder","treatmentOrder","surgeryOrder","radiologyOrder","testOrder","physiologyOrder",
		"bacteriaOrder","injectionOrder","medOrder","baseChargeOrder","instractionChargeOrder"
	};
    
	/** Control staffs */
	private String rootName;
	private boolean hasEditor;
	private StampTreeNode rootNode;
	private StampTreeNode node;
	private ModuleInfo info;
	private LinkedList linkedList;
	private ArrayList products;
    
	/** Debug */
	private ITrace trace;

	/** Creates new DefaultStampTreeBuilder */
	public DefaultStampTreeBuilder() {
	}
    
	public void setTrace(ITrace trace) {
		this.trace = trace;
	}
    
	/**
	 * Returns the product of this builder
	 * @return vector that contains StampTree instances
	 */
	public ArrayList getProduct() {
		return products;
	}
    
	public void buildStart() {
		products = new ArrayList();
		if (trace != null) {
			trace.debug("Build start");
		}
	}
    
	public void buildRoot(String name) {        
		// New root
		if (trace != null) {
			trace.debug("Root=" + name);
		}
		linkedList = new LinkedList();
		rootNode = new StampTreeNode(name);
        
		hasEditor = false;
		rootName = name;
		linkedList.addFirst(rootNode);
	}
    
	public void buildNode(String name) {
		// New node
		if (trace != null) {
			trace.debug("Node=" + name);
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
							   String id,
							   String asp,
							   String gcpVisit) {
        
		if (trace != null) {
			trace.debug(name + "," + role + "," + entity + "," + memo);
		}
        
		info = new ModuleInfo();
		info.setName(name);
		info.setRole(role);
		info.setEntity(entity);
		if (editable != null) {
			info.setEditable(Boolean.valueOf(editable).booleanValue());
		}
		if (memo != null) {
			info.setMemo(memo);
		}
		if ( id != null ) {
			info.setStampId(id);
		}
		if (asp != null && Boolean.valueOf(asp).booleanValue() ) {
			info.setASP(true);
		}
		//if (gcpVisit != null ) {
			//info.setGcpVisit(gcpVisit);
		//}

		// StampInfo から TreeNode を生成し現在のノードへ追加する
		node = new StampTreeNode(info);
		getCurrentNode().add(node);
        
		// エディタから発行を持っているか
		if (info.getName().equals("エディタから発行...") && (! info.isSerialized()) ) {
			hasEditor = true;
		}
	}
    
	public void buildNodeEnd() {
		if (trace != null) {
			trace.debug("End node");
		}
		linkedList.removeFirst();
	}
    
	public void buildRootEnd() {
        
		if (! hasEditor && (getEntity(rootName) != null) ) {
            
			// エディタから発行...を削除された場合に追加
			ModuleInfo si = new ModuleInfo();
			si.setName("エディタから発行...");
			si.setRole("p");
			si.setEntity(getEntity(rootName));            
			StampTreeNode sn = new StampTreeNode(si);
			rootNode.add(sn);
		}
        
		StampTree tree = new StampTree(new StampTreeModel(rootNode));
		products.add(tree);
        
		if (trace != null) {
			int pCount = products.size();
			trace.debug("End root " + "count=" + pCount);
		}
	}
    
	public void buildEnd() {
		if (trace != null) {
			trace.debug("Build end");
		}
	}
    
	private StampTreeNode getCurrentNode() {
		return (StampTreeNode)linkedList.getFirst();
	}
    
	private String getEntity(String rn) {
        
		String ret = null;
        
		if (rn == null) {
			return ret;
		}
        
		for (int i = 0; i < entities.length; i++) {
			if (rootNames[i].equals(rn)) {
				ret = entities[i];
				break;
			}
		}
        
		return ret;
	}
}