/**
 *
 * mmlRptestMemo.java
 * Created on 2003/1/4 2:30:23
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRp:tmCodeName") ) {
						set__mmlRptmCodeName( atts.getValue(i) );
						((mmlRptestMemo)builder.getElement()).set__mmlRptmCodeName( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:tmCode") ) {
						set__mmlRptmCode( atts.getValue(i) );
						((mmlRptestMemo)builder.getElement()).set__mmlRptmCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:tmCodeId") ) {
						set__mmlRptmCodeId( atts.getValue(i) );
						((mmlRptestMemo)builder.getElement()).set__mmlRptmCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:testMemo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:reportBody")) {
				Vector v = ((mmlRpreportBody)builder.getParent()).get_testMemo();
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
	public void set__mmlRptmCodeName(String __mmlRptmCodeName) {
		this.__mmlRptmCodeName = __mmlRptmCodeName;
	}
	public String get__mmlRptmCodeName() {
		return __mmlRptmCodeName;
	}
	public void set__mmlRptmCode(String __mmlRptmCode) {
		this.__mmlRptmCode = __mmlRptmCode;
	}
	public String get__mmlRptmCode() {
		return __mmlRptmCode;
	}
	public void set__mmlRptmCodeId(String __mmlRptmCodeId) {
		this.__mmlRptmCodeId = __mmlRptmCodeId;
	}
	public String get__mmlRptmCodeId() {
		return __mmlRptmCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}