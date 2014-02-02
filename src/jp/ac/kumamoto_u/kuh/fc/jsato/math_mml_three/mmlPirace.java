/**
 *
 * mmlPirace.java
 * Created on 2003/1/4 2:30:0
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
public class mmlPirace extends MMLObject {
	
	/* fields */
	private String __mmlPiraceCode = null;
	private String __mmlPiraceCodeId = null;

	private String text = null;
	
	public mmlPirace() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlPiraceCode != null ) pw.print(" " + "mmlPi:raceCode" +  "=" + "'" + __mmlPiraceCode + "'");
			if ( __mmlPiraceCodeId != null ) pw.print(" " + "mmlPi:raceCodeId" +  "=" + "'" + __mmlPiraceCodeId + "'");

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
		if (qName.equals("mmlPi:race") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPirace obj = new mmlPirace();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPirace)builder.getElement()).setNamespace( getNamespace() );
			((mmlPirace)builder.getElement()).setLocalName( getLocalName() );
			((mmlPirace)builder.getElement()).setQName( getQName() );
			((mmlPirace)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlPi:raceCode") ) {
						set__mmlPiraceCode( atts.getValue(i) );
						((mmlPirace)builder.getElement()).set__mmlPiraceCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlPi:raceCodeId") ) {
						set__mmlPiraceCodeId( atts.getValue(i) );
						((mmlPirace)builder.getElement()).set__mmlPiraceCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPi:race") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPi:PatientModule")) {
				((mmlPiPatientModule)builder.getParent()).set_race((mmlPirace)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlPi:race")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlPirace)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlPiraceCode(String __mmlPiraceCode) {
		this.__mmlPiraceCode = __mmlPiraceCode;
	}
	public String get__mmlPiraceCode() {
		return __mmlPiraceCode;
	}
	public void set__mmlPiraceCodeId(String __mmlPiraceCodeId) {
		this.__mmlPiraceCodeId = __mmlPiraceCodeId;
	}
	public String get__mmlPiraceCodeId() {
		return __mmlPiraceCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}