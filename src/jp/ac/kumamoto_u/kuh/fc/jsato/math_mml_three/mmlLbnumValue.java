/**
 *
 * mmlLbnumValue.java
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
public class mmlLbnumValue extends MMLObject {
	
	/* fields */
	private String __mmlLbup = null;
	private String __mmlLblow = null;
	private String __mmlLbnormal = null;
	private String __mmlLbout = null;

	private String text = null;
	
	public mmlLbnumValue() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbup != null ) pw.print(" " + "mmlLb:up" +  "=" + "'" + __mmlLbup + "'");
			if ( __mmlLblow != null ) pw.print(" " + "mmlLb:low" +  "=" + "'" + __mmlLblow + "'");
			if ( __mmlLbnormal != null ) pw.print(" " + "mmlLb:normal" +  "=" + "'" + __mmlLbnormal + "'");
			if ( __mmlLbout != null ) pw.print(" " + "mmlLb:out" +  "=" + "'" + __mmlLbout + "'");

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
		if (qName.equals("mmlLb:numValue") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbnumValue obj = new mmlLbnumValue();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbnumValue)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbnumValue)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbnumValue)builder.getElement()).setQName( getQName() );
			((mmlLbnumValue)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:up") ) {
						set__mmlLbup( atts.getValue(i) );
						((mmlLbnumValue)builder.getElement()).set__mmlLbup( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:low") ) {
						set__mmlLblow( atts.getValue(i) );
						((mmlLbnumValue)builder.getElement()).set__mmlLblow( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:normal") ) {
						set__mmlLbnormal( atts.getValue(i) );
						((mmlLbnumValue)builder.getElement()).set__mmlLbnormal( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:out") ) {
						set__mmlLbout( atts.getValue(i) );
						((mmlLbnumValue)builder.getElement()).set__mmlLbout( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:numValue") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:item")) {
				((mmlLbitem)builder.getParent()).set_numValue((mmlLbnumValue)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:numValue")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbnumValue)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlLbup(String __mmlLbup) {
		this.__mmlLbup = __mmlLbup;
	}
	public String get__mmlLbup() {
		return __mmlLbup;
	}
	public void set__mmlLblow(String __mmlLblow) {
		this.__mmlLblow = __mmlLblow;
	}
	public String get__mmlLblow() {
		return __mmlLblow;
	}
	public void set__mmlLbnormal(String __mmlLbnormal) {
		this.__mmlLbnormal = __mmlLbnormal;
	}
	public String get__mmlLbnormal() {
		return __mmlLbnormal;
	}
	public void set__mmlLbout(String __mmlLbout) {
		this.__mmlLbout = __mmlLbout;
	}
	public String get__mmlLbout() {
		return __mmlLbout;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}