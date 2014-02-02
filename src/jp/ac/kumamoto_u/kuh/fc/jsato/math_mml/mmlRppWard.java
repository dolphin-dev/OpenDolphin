/**
 *
 * mmlRppWard.java
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
public class mmlRppWard extends MMLObject {
	
	/* fields */
	private String __mmlRpwardCode = null;
	private String __mmlRpwardCodeId = null;

	private String text = null;
	
	public mmlRppWard() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpwardCode != null ) pw.print(" " + "mmlRp:wardCode" +  "=" + "'" + __mmlRpwardCode + "'");
			if ( __mmlRpwardCodeId != null ) pw.print(" " + "mmlRp:wardCodeId" +  "=" + "'" + __mmlRpwardCodeId + "'");

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
		if (qName.equals("mmlRp:pWard") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRppWard obj = new mmlRppWard();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRppWard)builder.getElement()).setNamespace( getNamespace() );
			((mmlRppWard)builder.getElement()).setLocalName( getLocalName() );
			((mmlRppWard)builder.getElement()).setQName( getQName() );
			((mmlRppWard)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRpwardCode( atts.getValue(namespaceURI, "wardCode") );
				((mmlRppWard)builder.getElement()).setMmlRpwardCode( atts.getValue(namespaceURI, "wardCode") );
				setMmlRpwardCodeId( atts.getValue(namespaceURI, "wardCodeId") );
				((mmlRppWard)builder.getElement()).setMmlRpwardCodeId( atts.getValue(namespaceURI, "wardCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:pWard") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:perform")) {
				((mmlRpperform)builder.getParent()).setPWard((mmlRppWard)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:pWard")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRppWard)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRpwardCode(String __mmlRpwardCode) {
		this.__mmlRpwardCode = __mmlRpwardCode;
	}
	public String getMmlRpwardCode() {
		return __mmlRpwardCode;
	}
	public void setMmlRpwardCodeId(String __mmlRpwardCodeId) {
		this.__mmlRpwardCodeId = __mmlRpwardCodeId;
	}
	public String getMmlRpwardCodeId() {
		return __mmlRpwardCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}