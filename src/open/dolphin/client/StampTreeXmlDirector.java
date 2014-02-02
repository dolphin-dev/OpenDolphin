/*
 * StampTreeXmlDirector.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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

import java.util.*;
import java.io.*;
import javax.swing.tree.*;


/**
 * Director to build StampTree XML data.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampTreeXmlDirector {
    
	private DefaultStampTreeXmlBuilder builder;

	/** Creates new StampTreeXmlDirector */
	public StampTreeXmlDirector(DefaultStampTreeXmlBuilder builder) {
		super();
        
		this.builder = builder;
	}
    
	/*public ArrayList build(ArrayList allTree) {
    	
		ArrayList product = null;
        
		try {
			int size = allTree.size();
			product = new ArrayList(size);
			StampTree tree = null;
			StampTreeEntry entry = null;
			
			for (int i = 0; i < size; i++) {
				
				tree = (StampTree)allTree.get(i);
				entry = new StampTreeEntry();
				//entry.setUserId(tree.getUserId());
				//entry.setId(tree.getId());
				//entry.setUse(tree.isUse());
				//entry.setNumber(tree.getNumber());
				
				builder.buildStart();
				lbuild(tree);
				builder.buildEnd();
				entry.setTreeXml(builder.getProduct());
				
				product.add(entry);
			}
		}
		catch (Exception e) {
			System.out.println("Exception while building the StampTree XML data:" + e.toString());
			e.printStackTrace();
		}
        
		return product;
	}*/
	
	public String build(ArrayList allTree) {
        
		try {
			builder.buildStart();
			int size = allTree.size();
			for (int i = 0; i < size; i++) {
				lbuild((StampTree)allTree.get(i));
			}
			builder.buildEnd();
		}
		catch (Exception e) {
			System.out.println("Exception while building the StampTree XML data:" + e.toString());
			e.printStackTrace();
		}
        
		return builder.getProduct();
	}	
    
	private void lbuild(StampTree tree) throws IOException {
        
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)tree.getModel().getRoot();
		Enumeration e = rootNode.preorderEnumeration();
		StampTreeNode node = (StampTreeNode)e.nextElement();
        
		builder.buildRoot(node);
        
		while (e.hasMoreElements()) {
            
			builder.buildNode((StampTreeNode)e.nextElement());
		}
        
		builder.buildRootEnd();
	}
}