/**
 *
 * mmlLbreportStatus.java
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
public class mmlLbreportStatus extends MMLObject {
	
	/* fields */
	private String __mmlLbstatusCode = null;
	private String __mmlLbstatusCodeId = null;

	private String text = null;
	
	public mmlLbreportStatus() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbstatusCode != null ) pw.print(" " + "mmlLb:statusCode" +  "=" + "'" + __mmlLbstatusCode + "'");
			if ( __mmlLbstatusCodeId != null ) pw.print(" " + "mmlLb:statusCodeId" +  "=" + "'" + __mmlLbstatusCodeId + "'");

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
		if (qName.equals("mmlLb:reportStatus") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbreportStatus obj = new mmlLbreportStatus();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbreportStatus)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbreportStatus)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbreportStatus)builder.getElement()).setQName( getQName() );
			((mmlLbreportStatus)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlLbstatusCode( atts.getValue(namespaceURI, "statusCode") );
				((mmlLbreportStatus)builder.getElement()).setMmlLbstatusCode( atts.getValue(namespaceURI, "statusCode") );
				setMmlLbstatusCodeId( atts.getValue(namespaceURI, "statusCodeId") );
				((mmlLbreportStatus)builder.getElement()).setMmlLbstatusCodeId( atts.getValue(namespaceURI, "statusCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:reportStatus") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).setReportStatus((mmlLbreportStatus)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:reportStatus")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbreportStatus)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlLbstatusCode(String __mmlLbstatusCode) {
		this.__mmlLbstatusCode = __mmlLbstatusCode;
	}
	public String getMmlLbstatusCode() {
		return __mmlLbstatusCode;
	}
	public void setMmlLbstatusCodeId(String __mmlLbstatusCodeId) {
		this.__mmlLbstatusCodeId = __mmlLbstatusCodeId;
	}
	public String getMmlLbstatusCodeId() {
		return __mmlLbstatusCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}