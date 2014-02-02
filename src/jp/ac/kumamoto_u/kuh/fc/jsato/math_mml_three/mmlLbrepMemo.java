/**
 *
 * mmlLbrepMemo.java
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
public class mmlLbrepMemo extends MMLObject {
	
	/* fields */
	private String __mmlLbrepCodeName = null;
	private String __mmlLbrepCode = null;
	private String __mmlLbrepCodeId = null;

	private String text = null;
	
	public mmlLbrepMemo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbrepCodeName != null ) pw.print(" " + "mmlLb:repCodeName" +  "=" + "'" + __mmlLbrepCodeName + "'");
			if ( __mmlLbrepCode != null ) pw.print(" " + "mmlLb:repCode" +  "=" + "'" + __mmlLbrepCode + "'");
			if ( __mmlLbrepCodeId != null ) pw.print(" " + "mmlLb:repCodeId" +  "=" + "'" + __mmlLbrepCodeId + "'");

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
		if (qName.equals("mmlLb:repMemo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbrepMemo obj = new mmlLbrepMemo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbrepMemo)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbrepMemo)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbrepMemo)builder.getElement()).setQName( getQName() );
			((mmlLbrepMemo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:repCodeName") ) {
						set__mmlLbrepCodeName( atts.getValue(i) );
						((mmlLbrepMemo)builder.getElement()).set__mmlLbrepCodeName( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:repCode") ) {
						set__mmlLbrepCode( atts.getValue(i) );
						((mmlLbrepMemo)builder.getElement()).set__mmlLbrepCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:repCodeId") ) {
						set__mmlLbrepCodeId( atts.getValue(i) );
						((mmlLbrepMemo)builder.getElement()).set__mmlLbrepCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:repMemo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				Vector v = ((mmlLbinformation)builder.getParent()).get_repMemo();
				v.addElement(builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:repMemo")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbrepMemo)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbrepCodeName(String __mmlLbrepCodeName) {
		this.__mmlLbrepCodeName = __mmlLbrepCodeName;
	}
	public String get__mmlLbrepCodeName() {
		return __mmlLbrepCodeName;
	}
	public void set__mmlLbrepCode(String __mmlLbrepCode) {
		this.__mmlLbrepCode = __mmlLbrepCode;
	}
	public String get__mmlLbrepCode() {
		return __mmlLbrepCode;
	}
	public void set__mmlLbrepCodeId(String __mmlLbrepCodeId) {
		this.__mmlLbrepCodeId = __mmlLbrepCodeId;
	}
	public String get__mmlLbrepCodeId() {
		return __mmlLbrepCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}