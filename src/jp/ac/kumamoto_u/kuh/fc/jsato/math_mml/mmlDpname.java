/**
 *
 * mmlDpname.java
 * Created on 2002/7/30 10:0:24
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
public class mmlDpname extends MMLObject {
	
	/* fields */
	private String __mmlDprepCode = null;
	private String __mmlDptableId = null;

	private String text = null;
	
	public mmlDpname() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlDprepCode != null ) pw.print(" " + "mmlDp:repCode" +  "=" + "'" + __mmlDprepCode + "'");
			if ( __mmlDptableId != null ) pw.print(" " + "mmlDp:tableId" +  "=" + "'" + __mmlDptableId + "'");

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
		if (qName.equals("mmlDp:name") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlDpname obj = new mmlDpname();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlDpname)builder.getElement()).setNamespace( getNamespace() );
			((mmlDpname)builder.getElement()).setLocalName( getLocalName() );
			((mmlDpname)builder.getElement()).setQName( getQName() );
			((mmlDpname)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlDprepCode( atts.getValue(namespaceURI, "repCode") );
				((mmlDpname)builder.getElement()).setMmlDprepCode( atts.getValue(namespaceURI, "repCode") );
				setMmlDptableId( atts.getValue(namespaceURI, "tableId") );
				((mmlDpname)builder.getElement()).setMmlDptableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlDp:name") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlDp:Department")) {
				Vector v = ((mmlDpDepartment)builder.getParent()).getName();
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
		if (builder.getCurrentElement().getQName().equals("mmlDp:name")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlDpname)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlDprepCode(String __mmlDprepCode) {
		this.__mmlDprepCode = __mmlDprepCode;
	}
	public String getMmlDprepCode() {
		return __mmlDprepCode;
	}
	public void setMmlDptableId(String __mmlDptableId) {
		this.__mmlDptableId = __mmlDptableId;
	}
	public String getMmlDptableId() {
		return __mmlDptableId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}