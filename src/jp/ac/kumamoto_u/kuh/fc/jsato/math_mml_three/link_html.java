/**
 *
 * link_html.java
 * Created on 2003/1/4 2:30:30
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
public class link_html extends MMLObject {
	
	/* fields */
	private String __name = null;
	private String __href = null;
	private String __rel = null;
	private String __rev = null;
	private String __title = null;
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;

	private String text = null;
	
	public link_html() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __name != null ) pw.print(" " + "name" +  "=" + "'" + __name + "'");
			if ( __href != null ) pw.print(" " + "href" +  "=" + "'" + __href + "'");
			if ( __rel != null ) pw.print(" " + "rel" +  "=" + "'" + __rel + "'");
			if ( __rev != null ) pw.print(" " + "rev" +  "=" + "'" + __rev + "'");
			if ( __title != null ) pw.print(" " + "title" +  "=" + "'" + __title + "'");
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __originator != null ) pw.print(" " + "originator" +  "=" + "'" + __originator + "'");
			if ( __confidentiality != null ) pw.print(" " + "confidentiality" +  "=" + "'" + __confidentiality + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");

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
		if (qName.equals("link_html") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			link_html obj = new link_html();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((link_html)builder.getElement()).setNamespace( getNamespace() );
			((link_html)builder.getElement()).setLocalName( getLocalName() );
			((link_html)builder.getElement()).setQName( getQName() );
			((link_html)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("name") ) {
						set__name( atts.getValue(i) );
						((link_html)builder.getElement()).set__name( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("href") ) {
						set__href( atts.getValue(i) );
						((link_html)builder.getElement()).set__href( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("rel") ) {
						set__rel( atts.getValue(i) );
						((link_html)builder.getElement()).set__rel( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("rev") ) {
						set__rev( atts.getValue(i) );
						((link_html)builder.getElement()).set__rev( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("title") ) {
						set__title( atts.getValue(i) );
						((link_html)builder.getElement()).set__title( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((link_html)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((link_html)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((link_html)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((link_html)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("link_html") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("link")) {
				((link)builder.getParent()).set_link_html((link_html)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("link_html")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((link_html)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__name(String __name) {
		this.__name = __name;
	}
	public String get__name() {
		return __name;
	}
	public void set__href(String __href) {
		this.__href = __href;
	}
	public String get__href() {
		return __href;
	}
	public void set__rel(String __rel) {
		this.__rel = __rel;
	}
	public String get__rel() {
		return __rel;
	}
	public void set__rev(String __rev) {
		this.__rev = __rev;
	}
	public String get__rev() {
		return __rev;
	}
	public void set__title(String __title) {
		this.__title = __title;
	}
	public String get__title() {
		return __title;
	}
	public void set__ID(String __ID) {
		this.__ID = __ID;
	}
	public String get__ID() {
		return __ID;
	}
	public void set__originator(String __originator) {
		this.__originator = __originator;
	}
	public String get__originator() {
		return __originator;
	}
	public void set__confidentiality(String __confidentiality) {
		this.__confidentiality = __confidentiality;
	}
	public String get__confidentiality() {
		return __confidentiality;
	}
	public void set__xmllang(String __xmllang) {
		this.__xmllang = __xmllang;
	}
	public String get__xmllang() {
		return __xmllang;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}