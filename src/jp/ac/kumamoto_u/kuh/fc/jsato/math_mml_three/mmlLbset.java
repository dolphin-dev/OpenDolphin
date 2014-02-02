/**
 *
 * mmlLbset.java
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
public class mmlLbset extends MMLObject {
	
	/* fields */
	private String __mmlLbsetCode = null;
	private String __mmlLbsetCodeId = null;

	private String text = null;
	
	public mmlLbset() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbsetCode != null ) pw.print(" " + "mmlLb:setCode" +  "=" + "'" + __mmlLbsetCode + "'");
			if ( __mmlLbsetCodeId != null ) pw.print(" " + "mmlLb:setCodeId" +  "=" + "'" + __mmlLbsetCodeId + "'");

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
		if (qName.equals("mmlLb:set") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbset obj = new mmlLbset();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbset)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbset)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbset)builder.getElement()).setQName( getQName() );
			((mmlLbset)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:setCode") ) {
						set__mmlLbsetCode( atts.getValue(i) );
						((mmlLbset)builder.getElement()).set__mmlLbsetCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:setCodeId") ) {
						set__mmlLbsetCodeId( atts.getValue(i) );
						((mmlLbset)builder.getElement()).set__mmlLbsetCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:set") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).set_set((mmlLbset)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:set")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbset)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbsetCode(String __mmlLbsetCode) {
		this.__mmlLbsetCode = __mmlLbsetCode;
	}
	public String get__mmlLbsetCode() {
		return __mmlLbsetCode;
	}
	public void set__mmlLbsetCodeId(String __mmlLbsetCodeId) {
		this.__mmlLbsetCodeId = __mmlLbsetCodeId;
	}
	public String get__mmlLbsetCodeId() {
		return __mmlLbsetCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}