/**
 *
 * mmlLbunit.java
 * Created on 2003/1/4 2:30:13
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
public class mmlLbunit extends MMLObject {
	
	/* fields */
	private String __mmlLbuCode = null;
	private String __mmlLbuCodeId = null;

	private String text = null;
	
	public mmlLbunit() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbuCode != null ) pw.print(" " + "mmlLb:uCode" +  "=" + "'" + __mmlLbuCode + "'");
			if ( __mmlLbuCodeId != null ) pw.print(" " + "mmlLb:uCodeId" +  "=" + "'" + __mmlLbuCodeId + "'");

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
		if (qName.equals("mmlLb:unit") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbunit obj = new mmlLbunit();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbunit)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbunit)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbunit)builder.getElement()).setQName( getQName() );
			((mmlLbunit)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:uCode") ) {
						set__mmlLbuCode( atts.getValue(i) );
						((mmlLbunit)builder.getElement()).set__mmlLbuCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:uCodeId") ) {
						set__mmlLbuCodeId( atts.getValue(i) );
						((mmlLbunit)builder.getElement()).set__mmlLbuCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:unit") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:item")) {
				((mmlLbitem)builder.getParent()).set_unit((mmlLbunit)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:unit")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbunit)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbuCode(String __mmlLbuCode) {
		this.__mmlLbuCode = __mmlLbuCode;
	}
	public String get__mmlLbuCode() {
		return __mmlLbuCode;
	}
	public void set__mmlLbuCodeId(String __mmlLbuCodeId) {
		this.__mmlLbuCodeId = __mmlLbuCodeId;
	}
	public String get__mmlLbuCodeId() {
		return __mmlLbuCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}