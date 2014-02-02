/**
 *
 * mmlRpclient.java
 * Created on 2002/7/30 10:0:36
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
public class mmlRpclient extends MMLObject {
	
	/* fields */
	private String __mmlRpclientCode = null;
	private String __mmlRpclientCodeId = null;

	private String text = null;
	
	public mmlRpclient() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpclientCode != null ) pw.print(" " + "mmlRp:clientCode" +  "=" + "'" + __mmlRpclientCode + "'");
			if ( __mmlRpclientCodeId != null ) pw.print(" " + "mmlRp:clientCodeId" +  "=" + "'" + __mmlRpclientCodeId + "'");

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
		if (qName.equals("mmlRp:client") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpclient obj = new mmlRpclient();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpclient)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpclient)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpclient)builder.getElement()).setQName( getQName() );
			((mmlRpclient)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRpclientCode( atts.getValue(namespaceURI, "clientCode") );
				((mmlRpclient)builder.getElement()).setMmlRpclientCode( atts.getValue(namespaceURI, "clientCode") );
				setMmlRpclientCodeId( atts.getValue(namespaceURI, "clientCodeId") );
				((mmlRpclient)builder.getElement()).setMmlRpclientCodeId( atts.getValue(namespaceURI, "clientCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:client") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:consultFrom")) {
				((mmlRpconsultFrom)builder.getParent()).setClient((mmlRpclient)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:client")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRpclient)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRpclientCode(String __mmlRpclientCode) {
		this.__mmlRpclientCode = __mmlRpclientCode;
	}
	public String getMmlRpclientCode() {
		return __mmlRpclientCode;
	}
	public void setMmlRpclientCodeId(String __mmlRpclientCodeId) {
		this.__mmlRpclientCodeId = __mmlRpclientCodeId;
	}
	public String getMmlRpclientCodeId() {
		return __mmlRpclientCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}