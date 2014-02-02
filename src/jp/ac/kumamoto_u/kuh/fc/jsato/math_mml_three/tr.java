/**
 *
 * tr.java
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
public class tr extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;
	private String __align = null;
	private String __char = null;
	private String __charoff = null;
	private String __valign = null;

	private Vector vt = new Vector();
	
	public tr() {
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
			if ( __align != null ) pw.print(" " + "align" +  "=" + "'" + __align + "'");
			if ( __char != null ) pw.print(" " + "char" +  "=" + "'" + __char + "'");
			if ( __charoff != null ) pw.print(" " + "charoff" +  "=" + "'" + __charoff + "'");
			if ( __valign != null ) pw.print(" " + "valign" +  "=" + "'" + __valign + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if (vt != null) {
				pw.print( "\n" );
				for (int i = 0; i < vt.size(); ++i) {
					visitor.setIgnoreTab( false );
					pw.print("\n");
					if (vt.elementAt(i).getClass().getName().equals("java.lang.String")) {
						//#PCDATA
						if ( ((String)vt.elementAt(i)).equals("") == false ) {
							pw.print( visitor.getTabPadding() + vt.elementAt(i) );
						}
					} else {
						//MMLObject
						((MMLObject)vt.elementAt(i)).printObject(pw, visitor);
					}
				}
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
		if (qName.equals("tr") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			tr obj = new tr();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((tr)builder.getElement()).setNamespace( getNamespace() );
			((tr)builder.getElement()).setLocalName( getLocalName() );
			((tr)builder.getElement()).setQName( getQName() );
			((tr)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((tr)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((tr)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((tr)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((tr)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("align") ) {
						set__align( atts.getValue(i) );
						((tr)builder.getElement()).set__align( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("char") ) {
						set__char( atts.getValue(i) );
						((tr)builder.getElement()).set__char( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("charoff") ) {
						set__charoff( atts.getValue(i) );
						((tr)builder.getElement()).set__charoff( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("valign") ) {
						set__valign( atts.getValue(i) );
						((tr)builder.getElement()).set__valign( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("tr") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("thead")) {
				Vector v = ((thead)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (tr)builder.getElement() );
			}

			if (parentElement.getQName().equals("tfoot")) {
				Vector v = ((tfoot)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (tr)builder.getElement() );
			}

			if (parentElement.getQName().equals("tbody")) {
				Vector v = ((tbody)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (tr)builder.getElement() );
			}

			if (parentElement.getQName().equals("table")) {
				Vector v = ((table)builder.getParent()).get_tr();
				v.addElement(builder.getElement());
			}

			
			printlnStatus(parentElement.getQName() + " /" + qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
		if (builder.getCurrentElement().getQName().equals("tr")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((tr)builder.getElement()).getVt().addElement(buffer.toString());
			printlnStatus(parentElement.getQName() + " " + this.getQName() + ":" + buffer.toString());
			return true;
		}
		return false;
	}
	
	
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

	public void setVt(Vector vt) {
		// copy entire elements in the vector
		if (this.vt != null) this.vt.removeAllElements();
		this.vt = new Vector();
		for (int i = 0; i < vt.size(); ++i) {
			this.vt.addElement( vt.elementAt(i) );
		}
	}
	public Vector getVt() {
		return vt;
	}
	
}