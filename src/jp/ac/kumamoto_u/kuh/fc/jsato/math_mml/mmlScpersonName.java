/**
 *
 * mmlScpersonName.java
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
public class mmlScpersonName extends MMLObject {
	
	/* fields */
	private String __mmlScpersonCode = null;
	private String __mmlSctableId = null;
	private String __mmlScpersonId = null;
	private String __mmlScpersonIdType = null;

	private String text = null;
	
	public mmlScpersonName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlScpersonCode != null ) pw.print(" " + "mmlSc:personCode" +  "=" + "'" + __mmlScpersonCode + "'");
			if ( __mmlSctableId != null ) pw.print(" " + "mmlSc:tableId" +  "=" + "'" + __mmlSctableId + "'");
			if ( __mmlScpersonId != null ) pw.print(" " + "mmlSc:personId" +  "=" + "'" + __mmlScpersonId + "'");
			if ( __mmlScpersonIdType != null ) pw.print(" " + "mmlSc:personIdType" +  "=" + "'" + __mmlScpersonIdType + "'");

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
		if (qName.equals("mmlSc:personName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlScpersonName obj = new mmlScpersonName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlScpersonName)builder.getElement()).setNamespace( getNamespace() );
			((mmlScpersonName)builder.getElement()).setLocalName( getLocalName() );
			((mmlScpersonName)builder.getElement()).setQName( getQName() );
			((mmlScpersonName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlScpersonCode( atts.getValue(namespaceURI, "personCode") );
				((mmlScpersonName)builder.getElement()).setMmlScpersonCode( atts.getValue(namespaceURI, "personCode") );
				setMmlSctableId( atts.getValue(namespaceURI, "tableId") );
				((mmlScpersonName)builder.getElement()).setMmlSctableId( atts.getValue(namespaceURI, "tableId") );
				setMmlScpersonId( atts.getValue(namespaceURI, "personId") );
				((mmlScpersonName)builder.getElement()).setMmlScpersonId( atts.getValue(namespaceURI, "personId") );
				setMmlScpersonIdType( atts.getValue(namespaceURI, "personIdType") );
				((mmlScpersonName)builder.getElement()).setMmlScpersonIdType( atts.getValue(namespaceURI, "personIdType") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:personName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSc:person")) {
				Vector v = ((mmlScperson)builder.getParent()).getPersonName();
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
		if (builder.getCurrentElement().getQName().equals("mmlSc:personName")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlScpersonName)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlScpersonCode(String __mmlScpersonCode) {
		this.__mmlScpersonCode = __mmlScpersonCode;
	}
	public String getMmlScpersonCode() {
		return __mmlScpersonCode;
	}
	public void setMmlSctableId(String __mmlSctableId) {
		this.__mmlSctableId = __mmlSctableId;
	}
	public String getMmlSctableId() {
		return __mmlSctableId;
	}
	public void setMmlScpersonId(String __mmlScpersonId) {
		this.__mmlScpersonId = __mmlScpersonId;
	}
	public String getMmlScpersonId() {
		return __mmlScpersonId;
	}
	public void setMmlScpersonIdType(String __mmlScpersonIdType) {
		this.__mmlScpersonIdType = __mmlScpersonIdType;
	}
	public String getMmlScpersonIdType() {
		return __mmlScpersonIdType;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}