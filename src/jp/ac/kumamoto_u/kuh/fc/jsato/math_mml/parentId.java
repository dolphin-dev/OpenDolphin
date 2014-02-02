/**
 *
 * parentId.java
 * Created on 2002/7/30 10:0:25
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import java.awt.*;
import java.util.*;
import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class parentId extends MMLObject {
	
	/* fields */
	private String __relation = null;

	private String text = null;
	
	public parentId() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __relation != null ) pw.print(" " + "relation" +  "=" + "'" + __relation + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			// this element need not to print tab padding before the closing tag.
			visitor.setIgnoreTab( true );
			if (text != null) {
				if ( this.getText().equals("") == false ) pw.print( this.getText() );
			}

			// only compound element requires to add tab padding before closing tag
			if ( visitor.getIgnoreTab() == false ) {
				pw.print( visitor.getTabPadding() );
			}
			pw.print( "</" + this.getQName() + ">\n" );
			pw.flush();
			visitor.setIgnoreTab( false );
			visitor.goUp();// adjust tab
		}
	}
	
	public boolean buildStart(String namespaceURI, String localName, String qName, Attributes atts, MMLBuilder builder) {
		if (qName.equals("parentId") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			parentId obj = new parentId();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((parentId)builder.getElement()).setNamespace( getNamespace() );
			((parentId)builder.getElement()).setLocalName( getLocalName() );
			((parentId)builder.getElement()).setQName( getQName() );
			((parentId)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setRelation( atts.getValue(namespaceURI, "relation") );
				((parentId)builder.getElement()).setRelation( atts.getValue(namespaceURI, "relation") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("parentId") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("docId")) {
				Vector v = ((docId)builder.getParent()).getParentId();
				v.addElement(builder.getElement());
			}

			
			printlnStatus(parentElement.getQName()+" /"+qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
		if (builder.getCurrentElement().getQName().equals("parentId")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((parentId)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setRelation(String __relation) {
		this.__relation = __relation;
	}
	public String getRelation() {
		return __relation;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}