/**
 *
 * mmlLbspecimenName.java
 * Created on 2003/1/4 2:30:12
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
public class mmlLbspecimenName extends MMLObject {
	
	/* fields */
	private String __mmlLbspCode = null;
	private String __mmlLbspCodeId = null;

	private String text = null;
	
	public mmlLbspecimenName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbspCode != null ) pw.print(" " + "mmlLb:spCode" +  "=" + "'" + __mmlLbspCode + "'");
			if ( __mmlLbspCodeId != null ) pw.print(" " + "mmlLb:spCodeId" +  "=" + "'" + __mmlLbspCodeId + "'");

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
		if (qName.equals("mmlLb:specimenName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbspecimenName obj = new mmlLbspecimenName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbspecimenName)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbspecimenName)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbspecimenName)builder.getElement()).setQName( getQName() );
			((mmlLbspecimenName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:spCode") ) {
						set__mmlLbspCode( atts.getValue(i) );
						((mmlLbspecimenName)builder.getElement()).set__mmlLbspCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:spCodeId") ) {
						set__mmlLbspCodeId( atts.getValue(i) );
						((mmlLbspecimenName)builder.getElement()).set__mmlLbspCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:specimenName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:specimen")) {
				((mmlLbspecimen)builder.getParent()).set_specimenName((mmlLbspecimenName)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:specimenName")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbspecimenName)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbspCode(String __mmlLbspCode) {
		this.__mmlLbspCode = __mmlLbspCode;
	}
	public String get__mmlLbspCode() {
		return __mmlLbspCode;
	}
	public void set__mmlLbspCodeId(String __mmlLbspCodeId) {
		this.__mmlLbspCodeId = __mmlLbspCodeId;
	}
	public String get__mmlLbspCodeId() {
		return __mmlLbspCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}