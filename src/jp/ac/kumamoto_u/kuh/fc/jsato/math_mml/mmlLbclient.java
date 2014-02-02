/**
 *
 * mmlLbclient.java
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
public class mmlLbclient extends MMLObject {
	
	/* fields */
	private String __mmlLbclientCode = null;
	private String __mmlLbclientCodeId = null;

	private String text = null;
	
	public mmlLbclient() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbclientCode != null ) pw.print(" " + "mmlLb:clientCode" +  "=" + "'" + __mmlLbclientCode + "'");
			if ( __mmlLbclientCodeId != null ) pw.print(" " + "mmlLb:clientCodeId" +  "=" + "'" + __mmlLbclientCodeId + "'");

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
		if (qName.equals("mmlLb:client") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbclient obj = new mmlLbclient();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbclient)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbclient)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbclient)builder.getElement()).setQName( getQName() );
			((mmlLbclient)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlLbclientCode( atts.getValue(namespaceURI, "clientCode") );
				((mmlLbclient)builder.getElement()).setMmlLbclientCode( atts.getValue(namespaceURI, "clientCode") );
				setMmlLbclientCodeId( atts.getValue(namespaceURI, "clientCodeId") );
				((mmlLbclient)builder.getElement()).setMmlLbclientCodeId( atts.getValue(namespaceURI, "clientCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:client") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).setClient((mmlLbclient)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:client")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbclient)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlLbclientCode(String __mmlLbclientCode) {
		this.__mmlLbclientCode = __mmlLbclientCode;
	}
	public String getMmlLbclientCode() {
		return __mmlLbclientCode;
	}
	public void setMmlLbclientCodeId(String __mmlLbclientCodeId) {
		this.__mmlLbclientCodeId = __mmlLbclientCodeId;
	}
	public String getMmlLbclientCodeId() {
		return __mmlLbclientCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}