/**
 *
 * originating_organization__type_cd.java
 * Created on 2003/1/4 2:29:58
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
public class originating_organization__type_cd extends MMLObject {
	
	/* fields */
	private String __T = null;
	private String __V = null;
	private String __V_T = null;
	private String __V_HL7_NAME = null;
	private String __DN = null;
	private String __DN_T = null;
	private String __DN_HL7_NAME = null;
	private String __ID = null;
	private String __HL7_NAME = null;

	private NOTE _NOTE = null;
	private CONFID _CONFID = null;
	
	public originating_organization__type_cd() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __T != null ) pw.print(" " + "T" +  "=" + "'" + __T + "'");
			if ( __V != null ) pw.print(" " + "V" +  "=" + "'" + __V + "'");
			if ( __V_T != null ) pw.print(" " + "V-T" +  "=" + "'" + __V_T + "'");
			if ( __V_HL7_NAME != null ) pw.print(" " + "V-HL7_NAME" +  "=" + "'" + __V_HL7_NAME + "'");
			if ( __DN != null ) pw.print(" " + "DN" +  "=" + "'" + __DN + "'");
			if ( __DN_T != null ) pw.print(" " + "DN-T" +  "=" + "'" + __DN_T + "'");
			if ( __DN_HL7_NAME != null ) pw.print(" " + "DN-HL7_NAME" +  "=" + "'" + __DN_HL7_NAME + "'");
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __HL7_NAME != null ) pw.print(" " + "HL7-NAME" +  "=" + "'" + __HL7_NAME + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _NOTE != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_NOTE.printObject(pw, visitor);
			}
			if ( _CONFID != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_CONFID.printObject(pw, visitor);
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
		if (qName.equals("originating_organization.type_cd") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			originating_organization__type_cd obj = new originating_organization__type_cd();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((originating_organization__type_cd)builder.getElement()).setNamespace( getNamespace() );
			((originating_organization__type_cd)builder.getElement()).setLocalName( getLocalName() );
			((originating_organization__type_cd)builder.getElement()).setQName( getQName() );
			((originating_organization__type_cd)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V") ) {
						set__V( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__V( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-T") ) {
						set__V_T( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__V_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-HL7_NAME") ) {
						set__V_HL7_NAME( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__V_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("DN") ) {
						set__DN( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__DN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("DN-T") ) {
						set__DN_T( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__DN_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("DN-HL7_NAME") ) {
						set__DN_HL7_NAME( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__DN_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((originating_organization__type_cd)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("originating_organization.type_cd") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("originating_organization")) {
				((originating_organization)builder.getParent()).set_originating_organization__type_cd((originating_organization__type_cd)builder.getElement());
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
	public void set__T(String __T) {
		this.__T = __T;
	}
	public String get__T() {
		return __T;
	}
	public void set__V(String __V) {
		this.__V = __V;
	}
	public String get__V() {
		return __V;
	}
	public void set__V_T(String __V_T) {
		this.__V_T = __V_T;
	}
	public String get__V_T() {
		return __V_T;
	}
	public void set__V_HL7_NAME(String __V_HL7_NAME) {
		this.__V_HL7_NAME = __V_HL7_NAME;
	}
	public String get__V_HL7_NAME() {
		return __V_HL7_NAME;
	}
	public void set__DN(String __DN) {
		this.__DN = __DN;
	}
	public String get__DN() {
		return __DN;
	}
	public void set__DN_T(String __DN_T) {
		this.__DN_T = __DN_T;
	}
	public String get__DN_T() {
		return __DN_T;
	}
	public void set__DN_HL7_NAME(String __DN_HL7_NAME) {
		this.__DN_HL7_NAME = __DN_HL7_NAME;
	}
	public String get__DN_HL7_NAME() {
		return __DN_HL7_NAME;
	}
	public void set__ID(String __ID) {
		this.__ID = __ID;
	}
	public String get__ID() {
		return __ID;
	}
	public void set__HL7_NAME(String __HL7_NAME) {
		this.__HL7_NAME = __HL7_NAME;
	}
	public String get__HL7_NAME() {
		return __HL7_NAME;
	}

	public void set_NOTE(NOTE _NOTE) {
		this._NOTE = _NOTE;
	}
	public NOTE get_NOTE() {
		return _NOTE;
	}
	public void set_CONFID(CONFID _CONFID) {
		this._CONFID = _CONFID;
	}
	public CONFID get_CONFID() {
		return _CONFID;
	}
	
}