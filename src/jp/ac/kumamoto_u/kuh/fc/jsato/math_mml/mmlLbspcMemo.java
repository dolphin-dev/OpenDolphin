/**
 *
 * mmlLbspcMemo.java
 * Created on 2002/7/30 10:0:35
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
public class mmlLbspcMemo extends MMLObject {
	
	/* fields */
	private String __mmlLbsmCodeName = null;
	private String __mmlLbsmCode = null;
	private String __mmlLbsmCodeId = null;

	private String text = null;
	
	public mmlLbspcMemo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbsmCodeName != null ) pw.print(" " + "mmlLb:smCodeName" +  "=" + "'" + __mmlLbsmCodeName + "'");
			if ( __mmlLbsmCode != null ) pw.print(" " + "mmlLb:smCode" +  "=" + "'" + __mmlLbsmCode + "'");
			if ( __mmlLbsmCodeId != null ) pw.print(" " + "mmlLb:smCodeId" +  "=" + "'" + __mmlLbsmCodeId + "'");

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
		if (qName.equals("mmlLb:spcMemo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbspcMemo obj = new mmlLbspcMemo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbspcMemo)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbspcMemo)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbspcMemo)builder.getElement()).setQName( getQName() );
			((mmlLbspcMemo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlLbsmCodeName( atts.getValue(namespaceURI, "smCodeName") );
				((mmlLbspcMemo)builder.getElement()).setMmlLbsmCodeName( atts.getValue(namespaceURI, "smCodeName") );
				setMmlLbsmCode( atts.getValue(namespaceURI, "smCode") );
				((mmlLbspcMemo)builder.getElement()).setMmlLbsmCode( atts.getValue(namespaceURI, "smCode") );
				setMmlLbsmCodeId( atts.getValue(namespaceURI, "smCodeId") );
				((mmlLbspcMemo)builder.getElement()).setMmlLbsmCodeId( atts.getValue(namespaceURI, "smCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:spcMemo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:specimen")) {
				Vector v = ((mmlLbspecimen)builder.getParent()).getSpcMemo();
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:spcMemo")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbspcMemo)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlLbsmCodeName(String __mmlLbsmCodeName) {
		this.__mmlLbsmCodeName = __mmlLbsmCodeName;
	}
	public String getMmlLbsmCodeName() {
		return __mmlLbsmCodeName;
	}
	public void setMmlLbsmCode(String __mmlLbsmCode) {
		this.__mmlLbsmCode = __mmlLbsmCode;
	}
	public String getMmlLbsmCode() {
		return __mmlLbsmCode;
	}
	public void setMmlLbsmCodeId(String __mmlLbsmCodeId) {
		this.__mmlLbsmCodeId = __mmlLbsmCodeId;
	}
	public String getMmlLbsmCodeId() {
		return __mmlLbsmCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}