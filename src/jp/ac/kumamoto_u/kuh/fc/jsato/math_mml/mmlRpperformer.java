/**
 *
 * mmlRpperformer.java
 * Created on 2002/7/30 10:0:36
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
public class mmlRpperformer extends MMLObject {
	
	/* fields */
	private String __mmlRpperformerCode = null;
	private String __mmlRpperformerCodeId = null;

	private String text = null;
	
	public mmlRpperformer() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpperformerCode != null ) pw.print(" " + "mmlRp:performerCode" +  "=" + "'" + __mmlRpperformerCode + "'");
			if ( __mmlRpperformerCodeId != null ) pw.print(" " + "mmlRp:performerCodeId" +  "=" + "'" + __mmlRpperformerCodeId + "'");

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
		if (qName.equals("mmlRp:performer") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpperformer obj = new mmlRpperformer();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpperformer)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpperformer)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpperformer)builder.getElement()).setQName( getQName() );
			((mmlRpperformer)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRpperformerCode( atts.getValue(namespaceURI, "performerCode") );
				((mmlRpperformer)builder.getElement()).setMmlRpperformerCode( atts.getValue(namespaceURI, "performerCode") );
				setMmlRpperformerCodeId( atts.getValue(namespaceURI, "performerCodeId") );
				((mmlRpperformer)builder.getElement()).setMmlRpperformerCodeId( atts.getValue(namespaceURI, "performerCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:performer") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:perform")) {
				((mmlRpperform)builder.getParent()).setPerformer((mmlRpperformer)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:performer")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRpperformer)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRpperformerCode(String __mmlRpperformerCode) {
		this.__mmlRpperformerCode = __mmlRpperformerCode;
	}
	public String getMmlRpperformerCode() {
		return __mmlRpperformerCode;
	}
	public void setMmlRpperformerCodeId(String __mmlRpperformerCodeId) {
		this.__mmlRpperformerCodeId = __mmlRpperformerCodeId;
	}
	public String getMmlRpperformerCodeId() {
		return __mmlRpperformerCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}