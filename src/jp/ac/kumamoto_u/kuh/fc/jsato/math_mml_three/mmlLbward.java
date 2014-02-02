/**
 *
 * mmlLbward.java
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
public class mmlLbward extends MMLObject {
	
	/* fields */
	private String __mmlLbwardCode = null;
	private String __mmlLbwardCodeId = null;

	private String text = null;
	
	public mmlLbward() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbwardCode != null ) pw.print(" " + "mmlLb:wardCode" +  "=" + "'" + __mmlLbwardCode + "'");
			if ( __mmlLbwardCodeId != null ) pw.print(" " + "mmlLb:wardCodeId" +  "=" + "'" + __mmlLbwardCodeId + "'");

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
		if (qName.equals("mmlLb:ward") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbward obj = new mmlLbward();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbward)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbward)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbward)builder.getElement()).setQName( getQName() );
			((mmlLbward)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:wardCode") ) {
						set__mmlLbwardCode( atts.getValue(i) );
						((mmlLbward)builder.getElement()).set__mmlLbwardCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:wardCodeId") ) {
						set__mmlLbwardCodeId( atts.getValue(i) );
						((mmlLbward)builder.getElement()).set__mmlLbwardCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:ward") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).set_ward((mmlLbward)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:ward")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbward)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbwardCode(String __mmlLbwardCode) {
		this.__mmlLbwardCode = __mmlLbwardCode;
	}
	public String get__mmlLbwardCode() {
		return __mmlLbwardCode;
	}
	public void set__mmlLbwardCodeId(String __mmlLbwardCodeId) {
		this.__mmlLbwardCodeId = __mmlLbwardCodeId;
	}
	public String get__mmlLbwardCodeId() {
		return __mmlLbwardCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}