/**
 *
 * local_markup.java
 * Created on 2003/1/4 2:30:31
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
public class local_markup extends MMLObject {
	
	/* fields */
	private String __ignore = null;
	private String __descriptor = null;
	private String __render = null;
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;

	private Vector vt = new Vector();
	
	public local_markup() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __ignore != null ) pw.print(" " + "ignore" +  "=" + "'" + __ignore + "'");
			if ( __descriptor != null ) pw.print(" " + "descriptor" +  "=" + "'" + __descriptor + "'");
			if ( __render != null ) pw.print(" " + "render" +  "=" + "'" + __render + "'");
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __originator != null ) pw.print(" " + "originator" +  "=" + "'" + __originator + "'");
			if ( __confidentiality != null ) pw.print(" " + "confidentiality" +  "=" + "'" + __confidentiality + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");

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
		if (qName.equals("local_markup") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			local_markup obj = new local_markup();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((local_markup)builder.getElement()).setNamespace( getNamespace() );
			((local_markup)builder.getElement()).setLocalName( getLocalName() );
			((local_markup)builder.getElement()).setQName( getQName() );
			((local_markup)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ignore") ) {
						set__ignore( atts.getValue(i) );
						((local_markup)builder.getElement()).set__ignore( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("descriptor") ) {
						set__descriptor( atts.getValue(i) );
						((local_markup)builder.getElement()).set__descriptor( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("render") ) {
						set__render( atts.getValue(i) );
						((local_markup)builder.getElement()).set__render( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((local_markup)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((local_markup)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((local_markup)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((local_markup)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("local_markup") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("td")) {
				Vector v = ((td)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (local_markup)builder.getElement() );
			}

			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (local_markup)builder.getElement() );
			}

			if (parentElement.getQName().equals("content")) {
				Vector v = ((content)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (local_markup)builder.getElement() );
			}

			if (parentElement.getQName().equals("observation_media")) {
				Vector v = ((observation_media)builder.getParent()).get_local_markup();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("coded_entry")) {
				Vector v = ((coded_entry)builder.getParent()).get_local_markup();
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
		if (builder.getCurrentElement().getQName().equals("local_markup")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((local_markup)builder.getElement()).getVt().addElement(buffer.toString());
			printlnStatus(parentElement.getQName() + " " + this.getQName() + ":" + buffer.toString());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__ignore(String __ignore) {
		this.__ignore = __ignore;
	}
	public String get__ignore() {
		return __ignore;
	}
	public void set__descriptor(String __descriptor) {
		this.__descriptor = __descriptor;
	}
	public String get__descriptor() {
		return __descriptor;
	}
	public void set__render(String __render) {
		this.__render = __render;
	}
	public String get__render() {
		return __render;
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