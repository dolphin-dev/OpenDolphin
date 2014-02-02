/*
 * StampTreeDirector.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Director of StampTree builder.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampTreeDirector {
	
	private final int TT_STAMP_INFO  	= 0;
	private final int TT_NODE  			= 1;
	private final int TT_ROOT  			= 2;
	private final int TT_STAMP_TREE  	= 3;
	private final int TT_STAMP_BOX  	= 4;
	
	private DefaultStampTreeBuilder builder;

	/** 
	 * Creates new StampTreeDirector 
	 */
	public StampTreeDirector(DefaultStampTreeBuilder builder) {
		this.builder = builder;
	}
        
	public ArrayList build(BufferedReader reader) {
                
		SAXBuilder docBuilder = new SAXBuilder();

		try {
			Document doc = docBuilder.build(reader);
			Element root = doc.getRootElement();

			builder.buildStart();
			parseChildren(root);
			builder.buildEnd();     
		}
		// indicates a well-formedness error
		catch (JDOMException e) { 
			System.out.println("Not well-formed.");
			System.out.println(e.getMessage());
		}  
		catch (IOException e) { 
			System.out.println(e);
		}
		
		return builder.getProduct();
	}
    
	public ArrayList build(ArrayList entries) {
		
		BufferedReader reader = null;
		SAXBuilder docBuilder = null;
		Document doc = null;
		Element root = null;
                
		try {
			builder.buildStart();
			int cnt = entries.size();
			StampTreeEntry entry = null;
			
			for (int i = 0; i < cnt; i++) {
				
				entry = (StampTreeEntry)entries.get(i);
				reader = new BufferedReader(new StringReader(entry.getTreeXml()));
				docBuilder = new SAXBuilder();
				doc = docBuilder.build(reader);
				root = doc.getRootElement();

				parseChildren(root);
			}
			builder.buildEnd();
		}
		// indicates a well-formedness error
		catch (JDOMException e) {
			e.printStackTrace(); 
			System.out.println("Not well-formed.");
			System.out.println(e.getMessage());
		}  
		catch (IOException e) { 
			e.printStackTrace();
			System.out.println(e);
		}
	
		//return builder.getProduct();
		ArrayList list = builder.getProduct();
		ArrayList ret = new ArrayList();
		int cnt = entries.size();
		StampTree stampTree = null;
		StampTreeEntry entry = null;
		for (int i = 0; i < cnt; i++) {
			stampTree = (StampTree)list.get(i);
			entry = (StampTreeEntry)entries.get(i);
			//stampTree.setId(entry.getId());
			//stampTree.setUse(entry.isUse());
			//stampTree.setNumber(entry.getNumber());
			ret.add(stampTree);
		}
		
		return ret;
	}    
    
	public void parseChildren(Element current) {
   
		int eType = startElement(current.getName(), current);
        
		List children = current.getChildren();
		Iterator iterator = children.iterator();
        
		while (iterator.hasNext()) {
			Element child = (Element) iterator.next();
			parseChildren(child);
		}
        
		endElement(eType);
	}
    
	public int startElement(String eName, Element e) {
        
		if (eName.equals("stampInfo")) {
			builder.buildStampInfo(e.getAttributeValue("name"),
								   e.getAttributeValue("role"),
								   e.getAttributeValue("entity"),
								   e.getAttributeValue("editable"),
								   e.getAttributeValue("memo"),
								   e.getAttributeValue("stampId"),
								   e.getAttributeValue("asp"),
								   e.getAttributeValue("gcpVisit")
								  );
			return TT_STAMP_INFO;
		}
		else if (eName.equals("node")) {
			builder.buildNode(e.getAttributeValue("name"));
			return TT_NODE;
		}
		else if (eName.equals("root")) {
			builder.buildRoot(e.getAttributeValue("name"));
			return TT_ROOT;
		}
		else if (eName.equals("stampTree")) {
			//builder.buildStart();
			return TT_STAMP_TREE;
		}
		else if (eName.equals("stampBox")) {
			return TT_STAMP_BOX;
		}
		return -1;
	}
    
	public void endElement(int eType) {
        
		switch (eType) {
			case TT_NODE:
				builder.buildNodeEnd();
				break;
 				
			case TT_ROOT:
				builder.buildRootEnd();
				break;
 				
			case TT_STAMP_TREE:
				break;
 				
			case TT_STAMP_BOX:
				break;		
		}
	}
}