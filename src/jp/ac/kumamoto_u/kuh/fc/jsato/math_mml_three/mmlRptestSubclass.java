/**
 *
 * mmlRptestSubclass.java
 * Created on 2003/1/4 2:30:17
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
public class mmlRptestSubclass extends MMLObject {
	
	/* fields */
	private String __mmlRptestSubclassCode = null;
	private String __mmlRptestSubclassCodeId = null;

	private String text = null;
	
	public mmlRptestSubclass() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRptestSubclassCode != null ) pw.print(" " + "mmlRp:testSubclassCode" +  "=" + "'" + __mmlRptestSubclassCode + "'");
			if ( __mmlRptestSubclassCodeId != null ) pw.print(" " + "mmlRp:testSubclassCodeId" +  "=" + "'" + __mmlRptestSubclassCodeId + "'");

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
		if (qName.equals("mmlRp:testSubclass") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRptestSubclass obj = new mmlRptestSubclass();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRptestSubclass)builder.getElement()).setNamespace( getNamespace() );
			((mmlRptestSubclass)builder.getElement()).setLocalName( getLocalName() );
			((mmlRptestSubclass)builder.getElement()).setQName( getQName() );
			((mmlRptestSubclass)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRp:testSubclassCode") ) {
						set__mmlRptestSubclassCode( atts.getValue(i) );
						((mmlRptestSubclass)builder.getElement()).set__mmlRptestSubclassCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:testSubclassCodeId") ) {
						set__mmlRptestSubclassCodeId( atts.getValue(i) );
						((mmlRptestSubclass)builder.getElement()).set__mmlRptestSubclassCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:testSubclass") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:information")) {
				((mmlRpinformation)builder.getParent()).set_testSubclass((mmlRptestSubclass)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:testSubclass")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRptestSubclass)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlRptestSubclassCode(String __mmlRptestSubclassCode) {
		this.__mmlRptestSubclassCode = __mmlRptestSubclassCode;
	}
	public String get__mmlRptestSubclassCode() {
		return __mmlRptestSubclassCode;
	}
	public void set__mmlRptestSubclassCodeId(String __mmlRptestSubclassCodeId) {
		this.__mmlRptestSubclassCodeId = __mmlRptestSubclassCodeId;
	}
	public String get__mmlRptestSubclassCodeId() {
		return __mmlRptestSubclassCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}