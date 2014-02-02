/**
 *
 * body.java
 * Created on 2003/1/4 2:30:29
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
public class body extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;

	private Vector _section = new Vector();
	private non_xml _non_xml = null;
	
	public body() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __originator != null ) pw.print(" " + "originator" +  "=" + "'" + __originator + "'");
			if ( __confidentiality != null ) pw.print(" " + "confidentiality" +  "=" + "'" + __confidentiality + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if (this._section != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._section.size(); ++i ) {
					((section)this._section.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _non_xml != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_non_xml.printObject(pw, visitor);
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
		if (qName.equals("body") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			body obj = new body();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((body)builder.getElement()).setNamespace( getNamespace() );
			((body)builder.getElement()).setLocalName( getLocalName() );
			((body)builder.getElement()).setQName( getQName() );
			((body)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((body)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((body)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((body)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((body)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("body") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("levelone")) {
				((levelone)builder.getParent()).set_body((body)builder.getElement());
			}

			
			printlnStatus(parentElement.getQName()+" /"+qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	
	
	/* setters and getters */
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

	public void set_section(Vector _section) {
		if (this._section != null) this._section.removeAllElements();
		// copy entire elements in the vector
		this._section = new Vector();
		for (int i = 0; i < _section.size(); ++i) {
			this._section.addElement( _section.elementAt(i) );
		}
	}
	public Vector get_section() {
		return _section;
	}
	public void set_non_xml(non_xml _non_xml) {
		this._non_xml = _non_xml;
	}
	public non_xml get_non_xml() {
		return _non_xml;
	}
	
}