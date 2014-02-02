/**
 *
 * confirmDate.java
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
public class confirmDate extends MMLObject {
	
	/* fields */
	private String __start = null;
	private String __end = null;

	private String text = null;
	
	public confirmDate() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __start != null ) pw.print(" " + "start" +  "=" + "'" + __start + "'");
			if ( __end != null ) pw.print(" " + "end" +  "=" + "'" + __end + "'");

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
		if (qName.equals("confirmDate") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			confirmDate obj = new confirmDate();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((confirmDate)builder.getElement()).setNamespace( getNamespace() );
			((confirmDate)builder.getElement()).setLocalName( getLocalName() );
			((confirmDate)builder.getElement()).setQName( getQName() );
			((confirmDate)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setStart( atts.getValue(namespaceURI, "start") );
				((confirmDate)builder.getElement()).setStart( atts.getValue(namespaceURI, "start") );
				setEnd( atts.getValue(namespaceURI, "end") );
				((confirmDate)builder.getElement()).setEnd( atts.getValue(namespaceURI, "end") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("confirmDate") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("docInfo")) {
				((docInfo)builder.getParent()).setConfirmDate((confirmDate)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("confirmDate")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((confirmDate)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setStart(String __start) {
		this.__start = __start;
	}
	public String getStart() {
		return __start;
	}
	public void setEnd(String __end) {
		this.__end = __end;
	}
	public String getEnd() {
		return __end;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}