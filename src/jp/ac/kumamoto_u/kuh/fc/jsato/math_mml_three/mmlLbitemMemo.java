/**
 *
 * mmlLbitemMemo.java
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
public class mmlLbitemMemo extends MMLObject {
	
	/* fields */
	private String __mmlLbimCodeName = null;
	private String __mmlLbimCode = null;
	private String __mmlLbimCodeId = null;

	private String text = null;
	
	public mmlLbitemMemo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbimCodeName != null ) pw.print(" " + "mmlLb:imCodeName" +  "=" + "'" + __mmlLbimCodeName + "'");
			if ( __mmlLbimCode != null ) pw.print(" " + "mmlLb:imCode" +  "=" + "'" + __mmlLbimCode + "'");
			if ( __mmlLbimCodeId != null ) pw.print(" " + "mmlLb:imCodeId" +  "=" + "'" + __mmlLbimCodeId + "'");

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
		if (qName.equals("mmlLb:itemMemo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbitemMemo obj = new mmlLbitemMemo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbitemMemo)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbitemMemo)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbitemMemo)builder.getElement()).setQName( getQName() );
			((mmlLbitemMemo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:imCodeName") ) {
						set__mmlLbimCodeName( atts.getValue(i) );
						((mmlLbitemMemo)builder.getElement()).set__mmlLbimCodeName( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:imCode") ) {
						set__mmlLbimCode( atts.getValue(i) );
						((mmlLbitemMemo)builder.getElement()).set__mmlLbimCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:imCodeId") ) {
						set__mmlLbimCodeId( atts.getValue(i) );
						((mmlLbitemMemo)builder.getElement()).set__mmlLbimCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:itemMemo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:item")) {
				Vector v = ((mmlLbitem)builder.getParent()).get_itemMemo();
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:itemMemo")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbitemMemo)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbimCodeName(String __mmlLbimCodeName) {
		this.__mmlLbimCodeName = __mmlLbimCodeName;
	}
	public String get__mmlLbimCodeName() {
		return __mmlLbimCodeName;
	}
	public void set__mmlLbimCode(String __mmlLbimCode) {
		this.__mmlLbimCode = __mmlLbimCode;
	}
	public String get__mmlLbimCode() {
		return __mmlLbimCode;
	}
	public void set__mmlLbimCodeId(String __mmlLbimCodeId) {
		this.__mmlLbimCodeId = __mmlLbimCodeId;
	}
	public String get__mmlLbimCodeId() {
		return __mmlLbimCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}