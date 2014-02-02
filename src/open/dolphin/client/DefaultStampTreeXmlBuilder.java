/*
 * DefaultStampTreeXmlBuilder.java
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

import java.io.*;
import java.util.*;

import open.dolphin.infomodel.ModuleInfo;


/**
 * StampTree XML builder.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultStampTreeXmlBuilder {
    
	/** Control staffs */
	private LinkedList linkedList;
	private BufferedWriter writer;
	private StringWriter stringWriter;
	private StampTreeNode rootNode;
    
	private ITrace trace;

	/** Creates new DefaultStampTreeXmlBuilder */
	public DefaultStampTreeXmlBuilder() {
		super();
	}
    
	public void setTrace(ITrace trace) {
		this.trace = trace;
	}
    
	/**
	 * Return the product of this builder
	 * @return StampTree XML data
	 */
	public String getProduct() {
		return stringWriter.toString();
	}
    
	public void buildStart() throws IOException {
		if (trace != null) {
			trace.debug("Build start");
		}
		stringWriter = new StringWriter();
		writer = new BufferedWriter(stringWriter);
		writer.write("<stampTree project=");
		writer.write(addQuote("open.dolphin"));
		writer.write(" version=");
		writer.write(addQuote("1.0"));
		writer.write(">\n");
	}
    
	public void buildRoot(StampTreeNode root) throws IOException {
		if (trace != null) {
			trace.debug("Build root: " + root.toString());
		}
		rootNode = root;
		writer.write("<root name=");
		writer.write(addQuote(rootNode.toString()));
		writer.write(">\n");
		linkedList = new LinkedList();
		linkedList.addFirst(rootNode);
	}
    
	public void buildNode(StampTreeNode node) throws IOException {
        
		// skip 傷病名
		String name = (String)(node.getUserObject().toString());
		if (name.equals("傷病名") || name.equals("傷病名シート...")) {
			return;
		}
        
		if ( node.isLeaf() ) {
			buildLeafNode(node);
		}
		else {
			buildDirectoryNode(node);
		}
	}
    
	private void buildDirectoryNode(StampTreeNode node) throws IOException {

		/********************************************************
		 ** 子ノードを持たないディレクトリノードは書き出さない **
		 ********************************************************/
		if (node.getChildCount() != 0) {
            
			if (trace != null) {
				trace.debug("Build directory node: " + node.toString());
			}

			StampTreeNode myParent = (StampTreeNode)node.getParent();
			StampTreeNode curNode = getCurrentNode();
            
			if (myParent != curNode) {                
				closeBeforeMyParent(myParent);
			}
			linkedList.addFirst(node);
			writer.write("<node name=");
			writer.write(addQuote(node.toString()));
			writer.write(">\n");
		}
	}
    
	private void buildLeafNode(StampTreeNode node) throws IOException {
           
		if (trace != null) {
			trace.debug("Build leaf node: " + node.toString());
		}
        
		StampTreeNode myParent = (StampTreeNode)node.getParent();
		StampTreeNode curNode = getCurrentNode();
                
		if (myParent != curNode) {
			closeBeforeMyParent(myParent);
		}
                    
		writer.write("<stampInfo name=");
		writer.write(addQuote(node.toString()));

		ModuleInfo info = (ModuleInfo)node.getUserObject();
		writer.write(" role=");
		writer.write(addQuote (info.getRole()));
		writer.write(" entity=");
		writer.write(addQuote (info.getEntity()));
		String val = String.valueOf(info.isEditable());
		writer.write(" editable=");
		writer.write(addQuote (val));
		val = info.getMemo();
		if (val != null) {
			writer.write(" memo=");
			writer.write(addQuote(val));
		}
		if (info.isSerialized()) {
			val = info.getStampId();
			writer.write(" stampId=");
			writer.write(addQuote(val));
		}
		if (info.isASP()) {
			writer.write(" asp=");
			writer.write(addQuote("true"));
		}
		//val = info.getGcpVisit();
		//if (val != null) {
			//writer.write(" gcpVisit=");
			//writer.write(addQuote(val));
		//}
		writer.write("/>\n");
	}
    
	public void buildRootEnd() throws IOException {
        
		if (trace != null) {
			trace.debug("Build root end");
		}
		closeBeforeMyParent(rootNode);
		writer.write("</root>\n");
	}
    
	public void buildEnd() throws IOException {
		if (trace != null) {
			trace.debug("Build end");
		}
		writer.write("</stampTree>\n");
		writer.flush();
	}
            
	private StampTreeNode getCurrentNode() {
		return (StampTreeNode)linkedList.getFirst();
	}
    
	private void closeBeforeMyParent(StampTreeNode parent) throws IOException {
        
		int index = linkedList.indexOf(parent);
        
		if (trace != null) {
			trace.debug("Close before my parent: " + index);
		}
		for (int j = 0; j < index; j++) {
			writer.write("</node>\n");
			linkedList.removeFirst();
		}
	}
    
	private String addQuote(String s) {
		StringBuffer buf = new StringBuffer();
		buf.append("\"");
		buf.append(s);
		buf.append("\"");
		return buf.toString();
	}
}