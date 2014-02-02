/**
 *
 * mmlRdname.java
 * Created on 2003/1/4 2:30:2
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
public class mmlRdname extends MMLObject {
	
	/* fields */
	private String __mmlRdcode = null;
	private String __mmlRdsystem = null;

	private String text = null;
	
	public mmlRdname() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRdcode != null ) pw.print(" " + "mmlRd:code" +  "=" + "'" + __mmlRdcode + "'");
			if ( __mmlRdsystem != null ) pw.print(" " + "mmlRd:system" +  "=" + "'" + __mmlRdsystem + "'");

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
		if (qName.equals("mmlRd:name") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRdname obj = new mmlRdname();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRdname)builder.getElement()).setNamespace( getNamespace() );
			((mmlRdname)builder.getElement()).setLocalName( getLocalName() );
			((mmlRdname)builder.getElement()).setQName( getQName() );
			((mmlRdname)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRd:code") ) {
						set__mmlRdcode( atts.getValue(i) );
						((mmlRdname)builder.getElement()).set__mmlRdcode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRd:system") ) {
						set__mmlRdsystem( atts.getValue(i) );
						((mmlRdname)builder.getElement()).set__mmlRdsystem( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRd:name") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRd:dxItem")) {
				((mmlRddxItem)builder.getParent()).set_name((mmlRdname)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRd:name")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRdname)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlRdcode(String __mmlRdcode) {
		this.__mmlRdcode = __mmlRdcode;
	}
	public String get__mmlRdcode() {
		return __mmlRdcode;
	}
	public void set__mmlRdsystem(String __mmlRdsystem) {
		this.__mmlRdsystem = __mmlRdsystem;
	}
	public String get__mmlRdsystem() {
		return __mmlRdsystem;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}