/**
 *
 * col.java
 * Created on 2003/1/4 2:30:33
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
public class col extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;
	private String __span = null;
	private String __width = null;
	private String __align = null;
	private String __char = null;
	private String __charoff = null;
	private String __valign = null;

	
	public col() {
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
			if ( __span != null ) pw.print(" " + "span" +  "=" + "'" + __span + "'");
			if ( __width != null ) pw.print(" " + "width" +  "=" + "'" + __width + "'");
			if ( __align != null ) pw.print(" " + "align" +  "=" + "'" + __align + "'");
			if ( __char != null ) pw.print(" " + "char" +  "=" + "'" + __char + "'");
			if ( __charoff != null ) pw.print(" " + "charoff" +  "=" + "'" + __charoff + "'");
			if ( __valign != null ) pw.print(" " + "valign" +  "=" + "'" + __valign + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */

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
		if (qName.equals("col") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			col obj = new col();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((col)builder.getElement()).setNamespace( getNamespace() );
			((col)builder.getElement()).setLocalName( getLocalName() );
			((col)builder.getElement()).setQName( getQName() );
			((col)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((col)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((col)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((col)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((col)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("span") ) {
						set__span( atts.getValue(i) );
						((col)builder.getElement()).set__span( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("width") ) {
						set__width( atts.getValue(i) );
						((col)builder.getElement()).set__width( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("align") ) {
						set__align( atts.getValue(i) );
						((col)builder.getElement()).set__align( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("char") ) {
						set__char( atts.getValue(i) );
						((col)builder.getElement()).set__char( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("charoff") ) {
						set__charoff( atts.getValue(i) );
						((col)builder.getElement()).set__charoff( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("valign") ) {
						set__valign( atts.getValue(i) );
						((col)builder.getElement()).set__valign( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("col") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("colgroup")) {
				Vector v = ((colgroup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (col)builder.getElement() );
			}

			if (parentElement.getQName().equals("table")) {
				Vector v = ((table)builder.getParent()).get_col();
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
	public void set__span(String __span) {
		this.__span = __span;
	}
	public String get__span() {
		return __span;
	}
	public void set__width(String __width) {
		this.__width = __width;
	}
	public String get__width() {
		return __width;
	}
	public void set__align(String __align) {
		this.__align = __align;
	}
	public String get__align() {
		return __align;
	}
	public void set__char(String __char) {
		this.__char = __char;
	}
	public String get__char() {
		return __char;
	}
	public void set__charoff(String __charoff) {
		this.__charoff = __charoff;
	}
	public String get__charoff() {
		return __charoff;
	}
	public void set__valign(String __valign) {
		this.__valign = __valign;
	}
	public String get__valign() {
		return __valign;
	}

	
}