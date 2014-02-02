/**
 *
 * mmlconfirmDate.java
 * Created on 2003/1/4 2:29:56
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml_three;

import java.awt.*;
import java.util.*;
import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class mmlconfirmDate extends MMLObject {
	
	/* fields */
	private String __start = null;
	private String __end = null;
	private String __firstConfirmDate = null;
	private String __eventDate = null;

	private String text = null;
	
	public mmlconfirmDate() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __start != null ) pw.print(" " + "start" +  "=" + "'" + __start + "'");
			if ( __end != null ) pw.print(" " + "end" +  "=" + "'" + __end + "'");
			if ( __firstConfirmDate != null ) pw.print(" " + "firstConfirmDate" +  "=" + "'" + __firstConfirmDate + "'");
			if ( __eventDate != null ) pw.print(" " + "eventDate" +  "=" + "'" + __eventDate + "'");

			if ( this.getLocalName().equals("levelone") ) {
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
		if (qName.equals("mml:confirmDate") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlconfirmDate obj = new mmlconfirmDate();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlconfirmDate)builder.getElement()).setNamespace( getNamespace() );
			((mmlconfirmDate)builder.getElement()).setLocalName( getLocalName() );
			((mmlconfirmDate)builder.getElement()).setQName( getQName() );
			((mmlconfirmDate)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("start") ) {
						set__start( atts.getValue(i) );
						((mmlconfirmDate)builder.getElement()).set__start( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("end") ) {
						set__end( atts.getValue(i) );
						((mmlconfirmDate)builder.getElement()).set__end( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("firstConfirmDate") ) {
						set__firstConfirmDate( atts.getValue(i) );
						((mmlconfirmDate)builder.getElement()).set__firstConfirmDate( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("eventDate") ) {
						set__eventDate( atts.getValue(i) );
						((mmlconfirmDate)builder.getElement()).set__eventDate( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:confirmDate") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:docInfo")) {
				((mmldocInfo)builder.getParent()).set_confirmDate((mmlconfirmDate)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mml:confirmDate")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlconfirmDate)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__start(String __start) {
		this.__start = __start;
	}
	public String get__start() {
		return __start;
	}
	public void set__end(String __end) {
		this.__end = __end;
	}
	public String get__end() {
		return __end;
	}
	public void set__firstConfirmDate(String __firstConfirmDate) {
		this.__firstConfirmDate = __firstConfirmDate;
	}
	public String get__firstConfirmDate() {
		return __firstConfirmDate;
	}
	public void set__eventDate(String __eventDate) {
		this.__eventDate = __eventDate;
	}
	public String get__eventDate() {
		return __eventDate;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}