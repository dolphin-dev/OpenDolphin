/**
 *
 * mmlRpconWard.java
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
public class mmlRpconWard extends MMLObject {
	
	/* fields */
	private String __mmlRpwardCode = null;
	private String __mmlRpwardCodeId = null;

	private String text = null;
	
	public mmlRpconWard() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpwardCode != null ) pw.print(" " + "mmlRp:wardCode" +  "=" + "'" + __mmlRpwardCode + "'");
			if ( __mmlRpwardCodeId != null ) pw.print(" " + "mmlRp:wardCodeId" +  "=" + "'" + __mmlRpwardCodeId + "'");

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
		if (qName.equals("mmlRp:conWard") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpconWard obj = new mmlRpconWard();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpconWard)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpconWard)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpconWard)builder.getElement()).setQName( getQName() );
			((mmlRpconWard)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRpwardCode( atts.getValue(namespaceURI, "wardCode") );
				((mmlRpconWard)builder.getElement()).setMmlRpwardCode( atts.getValue(namespaceURI, "wardCode") );
				setMmlRpwardCodeId( atts.getValue(namespaceURI, "wardCodeId") );
				((mmlRpconWard)builder.getElement()).setMmlRpwardCodeId( atts.getValue(namespaceURI, "wardCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:conWard") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:consultFrom")) {
				((mmlRpconsultFrom)builder.getParent()).setConWard((mmlRpconWard)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:conWard")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRpconWard)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRpwardCode(String __mmlRpwardCode) {
		this.__mmlRpwardCode = __mmlRpwardCode;
	}
	public String getMmlRpwardCode() {
		return __mmlRpwardCode;
	}
	public void setMmlRpwardCodeId(String __mmlRpwardCodeId) {
		this.__mmlRpwardCodeId = __mmlRpwardCodeId;
	}
	public String getMmlRpwardCodeId() {
		return __mmlRpwardCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}