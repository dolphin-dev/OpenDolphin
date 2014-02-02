/**
 *
 * mmlRptestMemo.java
 * Created on 2002/7/30 10:0:37
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
public class mmlRptestMemo extends MMLObject {
	
	/* fields */
	private String __mmlRptmCodeName = null;
	private String __mmlRptmCode = null;
	private String __mmlRptmCodeId = null;

	private String text = null;
	
	public mmlRptestMemo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRptmCodeName != null ) pw.print(" " + "mmlRp:tmCodeName" +  "=" + "'" + __mmlRptmCodeName + "'");
			if ( __mmlRptmCode != null ) pw.print(" " + "mmlRp:tmCode" +  "=" + "'" + __mmlRptmCode + "'");
			if ( __mmlRptmCodeId != null ) pw.print(" " + "mmlRp:tmCodeId" +  "=" + "'" + __mmlRptmCodeId + "'");

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
		if (qName.equals("mmlRp:testMemo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRptestMemo obj = new mmlRptestMemo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRptestMemo)builder.getElement()).setNamespace( getNamespace() );
			((mmlRptestMemo)builder.getElement()).setLocalName( getLocalName() );
			((mmlRptestMemo)builder.getElement()).setQName( getQName() );
			((mmlRptestMemo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRptmCodeName( atts.getValue(namespaceURI, "tmCodeName") );
				((mmlRptestMemo)builder.getElement()).setMmlRptmCodeName( atts.getValue(namespaceURI, "tmCodeName") );
				setMmlRptmCode( atts.getValue(namespaceURI, "tmCode") );
				((mmlRptestMemo)builder.getElement()).setMmlRptmCode( atts.getValue(namespaceURI, "tmCode") );
				setMmlRptmCodeId( atts.getValue(namespaceURI, "tmCodeId") );
				((mmlRptestMemo)builder.getElement()).setMmlRptmCodeId( atts.getValue(namespaceURI, "tmCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:testMemo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:reportBody")) {
				Vector v = ((mmlRpreportBody)builder.getParent()).getTestMemo();
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:testMemo")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRptestMemo)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRptmCodeName(String __mmlRptmCodeName) {
		this.__mmlRptmCodeName = __mmlRptmCodeName;
	}
	public String getMmlRptmCodeName() {
		return __mmlRptmCodeName;
	}
	public void setMmlRptmCode(String __mmlRptmCode) {
		this.__mmlRptmCode = __mmlRptmCode;
	}
	public String getMmlRptmCode() {
		return __mmlRptmCode;
	}
	public void setMmlRptmCodeId(String __mmlRptmCodeId) {
		this.__mmlRptmCodeId = __mmlRptmCodeId;
	}
	public String getMmlRptmCodeId() {
		return __mmlRptmCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}