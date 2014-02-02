/**
 *
 * mmlLbitemName.java
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
public class mmlLbitemName extends MMLObject {
	
	/* fields */
	private String __mmlLbitCode = null;
	private String __mmlLbitCodeId = null;
	private String __mmlLbAcode = null;
	private String __mmlLbIcode = null;
	private String __mmlLbScode = null;
	private String __mmlLbMcode = null;
	private String __mmlLbRcode = null;

	private String text = null;
	
	public mmlLbitemName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbitCode != null ) pw.print(" " + "mmlLb:itCode" +  "=" + "'" + __mmlLbitCode + "'");
			if ( __mmlLbitCodeId != null ) pw.print(" " + "mmlLb:itCodeId" +  "=" + "'" + __mmlLbitCodeId + "'");
			if ( __mmlLbAcode != null ) pw.print(" " + "mmlLb:Acode" +  "=" + "'" + __mmlLbAcode + "'");
			if ( __mmlLbIcode != null ) pw.print(" " + "mmlLb:Icode" +  "=" + "'" + __mmlLbIcode + "'");
			if ( __mmlLbScode != null ) pw.print(" " + "mmlLb:Scode" +  "=" + "'" + __mmlLbScode + "'");
			if ( __mmlLbMcode != null ) pw.print(" " + "mmlLb:Mcode" +  "=" + "'" + __mmlLbMcode + "'");
			if ( __mmlLbRcode != null ) pw.print(" " + "mmlLb:Rcode" +  "=" + "'" + __mmlLbRcode + "'");

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
		if (qName.equals("mmlLb:itemName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbitemName obj = new mmlLbitemName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbitemName)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbitemName)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbitemName)builder.getElement()).setQName( getQName() );
			((mmlLbitemName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlLbitCode( atts.getValue(namespaceURI, "itCode") );
				((mmlLbitemName)builder.getElement()).setMmlLbitCode( atts.getValue(namespaceURI, "itCode") );
				setMmlLbitCodeId( atts.getValue(namespaceURI, "itCodeId") );
				((mmlLbitemName)builder.getElement()).setMmlLbitCodeId( atts.getValue(namespaceURI, "itCodeId") );
				setMmlLbAcode( atts.getValue(namespaceURI, "Acode") );
				((mmlLbitemName)builder.getElement()).setMmlLbAcode( atts.getValue(namespaceURI, "Acode") );
				setMmlLbIcode( atts.getValue(namespaceURI, "Icode") );
				((mmlLbitemName)builder.getElement()).setMmlLbIcode( atts.getValue(namespaceURI, "Icode") );
				setMmlLbScode( atts.getValue(namespaceURI, "Scode") );
				((mmlLbitemName)builder.getElement()).setMmlLbScode( atts.getValue(namespaceURI, "Scode") );
				setMmlLbMcode( atts.getValue(namespaceURI, "Mcode") );
				((mmlLbitemName)builder.getElement()).setMmlLbMcode( atts.getValue(namespaceURI, "Mcode") );
				setMmlLbRcode( atts.getValue(namespaceURI, "Rcode") );
				((mmlLbitemName)builder.getElement()).setMmlLbRcode( atts.getValue(namespaceURI, "Rcode") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:itemName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:item")) {
				((mmlLbitem)builder.getParent()).setItemName((mmlLbitemName)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:itemName")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbitemName)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlLbitCode(String __mmlLbitCode) {
		this.__mmlLbitCode = __mmlLbitCode;
	}
	public String getMmlLbitCode() {
		return __mmlLbitCode;
	}
	public void setMmlLbitCodeId(String __mmlLbitCodeId) {
		this.__mmlLbitCodeId = __mmlLbitCodeId;
	}
	public String getMmlLbitCodeId() {
		return __mmlLbitCodeId;
	}
	public void setMmlLbAcode(String __mmlLbAcode) {
		this.__mmlLbAcode = __mmlLbAcode;
	}
	public String getMmlLbAcode() {
		return __mmlLbAcode;
	}
	public void setMmlLbIcode(String __mmlLbIcode) {
		this.__mmlLbIcode = __mmlLbIcode;
	}
	public String getMmlLbIcode() {
		return __mmlLbIcode;
	}
	public void setMmlLbScode(String __mmlLbScode) {
		this.__mmlLbScode = __mmlLbScode;
	}
	public String getMmlLbScode() {
		return __mmlLbScode;
	}
	public void setMmlLbMcode(String __mmlLbMcode) {
		this.__mmlLbMcode = __mmlLbMcode;
	}
	public String getMmlLbMcode() {
		return __mmlLbMcode;
	}
	public void setMmlLbRcode(String __mmlLbRcode) {
		this.__mmlLbRcode = __mmlLbRcode;
	}
	public String getMmlLbRcode() {
		return __mmlLbRcode;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}