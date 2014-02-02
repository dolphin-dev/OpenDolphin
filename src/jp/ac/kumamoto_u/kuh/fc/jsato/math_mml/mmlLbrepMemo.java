/**
 *
 * mmlLbrepMemo.java
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
				setMmlLbrepCodeName( atts.getValue(namespaceURI, "repCodeName") );
				((mmlLbrepMemo)builder.getElement()).setMmlLbrepCodeName( atts.getValue(namespaceURI, "repCodeName") );
				setMmlLbrepCode( atts.getValue(namespaceURI, "repCode") );
				((mmlLbrepMemo)builder.getElement()).setMmlLbrepCode( atts.getValue(namespaceURI, "repCode") );
				setMmlLbrepCodeId( atts.getValue(namespaceURI, "repCodeId") );
				((mmlLbrepMemo)builder.getElement()).setMmlLbrepCodeId( atts.getValue(namespaceURI, "repCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:repMemo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				Vector v = ((mmlLbinformation)builder.getParent()).getRepMemo();
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
	public void setMmlLbrepCodeName(String __mmlLbrepCodeName) {
		this.__mmlLbrepCodeName = __mmlLbrepCodeName;
	}
	public String getMmlLbrepCodeName() {
		return __mmlLbrepCodeName;
	}
	public void setMmlLbrepCode(String __mmlLbrepCode) {
		this.__mmlLbrepCode = __mmlLbrepCode;
	}
	public String getMmlLbrepCode() {
		return __mmlLbrepCode;
	}
	public void setMmlLbrepCodeId(String __mmlLbrepCodeId) {
		this.__mmlLbrepCodeId = __mmlLbrepCodeId;
	}
	public String getMmlLbrepCodeId() {
		return __mmlLbrepCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}