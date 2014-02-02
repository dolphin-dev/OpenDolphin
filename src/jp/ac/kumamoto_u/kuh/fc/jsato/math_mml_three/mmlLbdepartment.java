/**
 *
 * mmlLbdepartment.java
 * Created on 2003/1/4 2:30:11
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
public class mmlLbdepartment extends MMLObject {
	
	/* fields */
	private String __mmlLbdepCode = null;
	private String __mmlLbdepCodeId = null;

	private String text = null;
	
	public mmlLbdepartment() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbdepCode != null ) pw.print(" " + "mmlLb:depCode" +  "=" + "'" + __mmlLbdepCode + "'");
			if ( __mmlLbdepCodeId != null ) pw.print(" " + "mmlLb:depCodeId" +  "=" + "'" + __mmlLbdepCodeId + "'");

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
		if (qName.equals("mmlLb:department") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbdepartment obj = new mmlLbdepartment();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbdepartment)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbdepartment)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbdepartment)builder.getElement()).setQName( getQName() );
			((mmlLbdepartment)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:depCode") ) {
						set__mmlLbdepCode( atts.getValue(i) );
						((mmlLbdepartment)builder.getElement()).set__mmlLbdepCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:depCodeId") ) {
						set__mmlLbdepCodeId( atts.getValue(i) );
						((mmlLbdepartment)builder.getElement()).set__mmlLbdepCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:department") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).set_department((mmlLbdepartment)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:department")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbdepartment)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbdepCode(String __mmlLbdepCode) {
		this.__mmlLbdepCode = __mmlLbdepCode;
	}
	public String get__mmlLbdepCode() {
		return __mmlLbdepCode;
	}
	public void set__mmlLbdepCodeId(String __mmlLbdepCodeId) {
		this.__mmlLbdepCodeId = __mmlLbdepCodeId;
	}
	public String get__mmlLbdepCodeId() {
		return __mmlLbdepCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}