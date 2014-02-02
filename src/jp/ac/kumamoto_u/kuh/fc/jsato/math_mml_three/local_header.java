/**
 *
 * local_header.java
 * Created on 2003/1/4 2:30:0
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
public class local_header extends MMLObject {
	
	/* fields */
	private String __ignore = null;
	private String __descriptor = null;
	private String __render = null;
	private String __ID = null;
	private String __xmllang = null;

	private Vector vt = new Vector();
	
	public local_header() {
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
		if (qName.equals("local_header") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			local_header obj = new local_header();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((local_header)builder.getElement()).setNamespace( getNamespace() );
			((local_header)builder.getElement()).setLocalName( getLocalName() );
			((local_header)builder.getElement()).setQName( getQName() );
			((local_header)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ignore") ) {
						set__ignore( atts.getValue(i) );
						((local_header)builder.getElement()).set__ignore( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("descriptor") ) {
						set__descriptor( atts.getValue(i) );
						((local_header)builder.getElement()).set__descriptor( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("render") ) {
						set__render( atts.getValue(i) );
						((local_header)builder.getElement()).set__render( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((local_header)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((local_header)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("local_header") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_header")) {
				Vector v = ((local_header)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (local_header)builder.getElement() );
			}

			if (parentElement.getQName().equals("transcriptionist")) {
				Vector v = ((transcriptionist)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("service_target")) {
				Vector v = ((service_target)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("service_location")) {
				Vector v = ((service_location)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("service_actor")) {
				Vector v = ((service_actor)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("responsibility")) {
				Vector v = ((responsibility)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("related_document")) {
				Vector v = ((related_document)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("provider")) {
				Vector v = ((provider)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("person_name")) {
				Vector v = ((person_name)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("person")) {
				Vector v = ((person)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("patient_encounter")) {
				Vector v = ((patient_encounter)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("patient")) {
				Vector v = ((patient)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("originator")) {
				Vector v = ((originator)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("originating_organization")) {
				Vector v = ((originating_organization)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("originating_device")) {
				Vector v = ((originating_device)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("organization")) {
				Vector v = ((organization)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("order")) {
				Vector v = ((order)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("legal_authenticator")) {
				Vector v = ((legal_authenticator)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("is_known_to")) {
				Vector v = ((is_known_to)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("is_known_by")) {
				Vector v = ((is_known_by)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("intended_recipient")) {
				Vector v = ((intended_recipient)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("fulfills_order")) {
				Vector v = ((fulfills_order)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("document_relationship")) {
				Vector v = ((document_relationship)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("device")) {
				Vector v = ((device)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("clinical_document_header")) {
				Vector v = ((clinical_document_header)builder.getParent()).get_local_header();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("authenticator")) {
				Vector v = ((authenticator)builder.getParent()).get_local_header();
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
		if (builder.getCurrentElement().getQName().equals("local_header")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((local_header)builder.getElement()).getVt().addElement(buffer.toString());
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