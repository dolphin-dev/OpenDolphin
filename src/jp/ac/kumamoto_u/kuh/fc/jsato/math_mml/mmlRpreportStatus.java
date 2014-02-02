/**
 *
 * mmlRpreportStatus.java
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
public class mmlRpreportStatus extends MMLObject {
	
	/* fields */
	private String __mmlRpstatusCode = null;
	private String __mmlRpstatusCodeId = null;

	private String text = null;
	
	public mmlRpreportStatus() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpstatusCode != null ) pw.print(" " + "mmlRp:statusCode" +  "=" + "'" + __mmlRpstatusCode + "'");
			if ( __mmlRpstatusCodeId != null ) pw.print(" " + "mmlRp:statusCodeId" +  "=" + "'" + __mmlRpstatusCodeId + "'");

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
		if (qName.equals("mmlRp:reportStatus") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpreportStatus obj = new mmlRpreportStatus();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpreportStatus)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpreportStatus)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpreportStatus)builder.getElement()).setQName( getQName() );
			((mmlRpreportStatus)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRpstatusCode( atts.getValue(namespaceURI, "statusCode") );
				((mmlRpreportStatus)builder.getElement()).setMmlRpstatusCode( atts.getValue(namespaceURI, "statusCode") );
				setMmlRpstatusCodeId( atts.getValue(namespaceURI, "statusCodeId") );
				((mmlRpreportStatus)builder.getElement()).setMmlRpstatusCodeId( atts.getValue(namespaceURI, "statusCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:reportStatus") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:information")) {
				((mmlRpinformation)builder.getParent()).setReportStatus((mmlRpreportStatus)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:reportStatus")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRpreportStatus)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRpstatusCode(String __mmlRpstatusCode) {
		this.__mmlRpstatusCode = __mmlRpstatusCode;
	}
	public String getMmlRpstatusCode() {
		return __mmlRpstatusCode;
	}
	public void setMmlRpstatusCodeId(String __mmlRpstatusCodeId) {
		this.__mmlRpstatusCodeId = __mmlRpstatusCodeId;
	}
	public String getMmlRpstatusCodeId() {
		return __mmlRpstatusCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}