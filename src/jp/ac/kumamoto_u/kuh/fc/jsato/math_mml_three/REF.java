/**
 *
 * REF.java
 * Created on 2003/1/4 2:29:52
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
public class REF extends MMLObject {
	
	/* fields */
	private String __T = null;
	private String __NULL = null;
	private String __V = null;
	private String __V_T = null;
	private String __V_HL7_NAME = null;
	private String __USE = null;
	private String __USE_T = null;
	private String __USE_DOMAIN = null;
	private String __USE_HL7_NAME = null;
	private String __VT = null;
	private String __VT_T = null;
	private String __VT_HL7_NAME = null;
	private String __PROB = null;
	private String __PROB_T = null;
	private String __PROB_HL7_NAME = null;
	private String __HL7_NAME = null;

	private NOTE _NOTE = null;
	private CONFID _CONFID = null;
	
	public REF() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __T != null ) pw.print(" " + "T" +  "=" + "'" + __T + "'");
			if ( __NULL != null ) pw.print(" " + "NULL" +  "=" + "'" + __NULL + "'");
			if ( __V != null ) pw.print(" " + "V" +  "=" + "'" + __V + "'");
			if ( __V_T != null ) pw.print(" " + "V-T" +  "=" + "'" + __V_T + "'");
			if ( __V_HL7_NAME != null ) pw.print(" " + "V-HL7_NAME" +  "=" + "'" + __V_HL7_NAME + "'");
			if ( __USE != null ) pw.print(" " + "USE" +  "=" + "'" + __USE + "'");
			if ( __USE_T != null ) pw.print(" " + "USE-T" +  "=" + "'" + __USE_T + "'");
			if ( __USE_DOMAIN != null ) pw.print(" " + "USE-DOMAIN" +  "=" + "'" + __USE_DOMAIN + "'");
			if ( __USE_HL7_NAME != null ) pw.print(" " + "USE-HL7_NAME" +  "=" + "'" + __USE_HL7_NAME + "'");
			if ( __VT != null ) pw.print(" " + "VT" +  "=" + "'" + __VT + "'");
			if ( __VT_T != null ) pw.print(" " + "VT-T" +  "=" + "'" + __VT_T + "'");
			if ( __VT_HL7_NAME != null ) pw.print(" " + "VT-HL7_NAME" +  "=" + "'" + __VT_HL7_NAME + "'");
			if ( __PROB != null ) pw.print(" " + "PROB" +  "=" + "'" + __PROB + "'");
			if ( __PROB_T != null ) pw.print(" " + "PROB-T" +  "=" + "'" + __PROB_T + "'");
			if ( __PROB_HL7_NAME != null ) pw.print(" " + "PROB-HL7_NAME" +  "=" + "'" + __PROB_HL7_NAME + "'");
			if ( __HL7_NAME != null ) pw.print(" " + "HL7_NAME" +  "=" + "'" + __HL7_NAME + "'");

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
		if (qName.equals("REF") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			REF obj = new REF();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((REF)builder.getElement()).setNamespace( getNamespace() );
			((REF)builder.getElement()).setLocalName( getLocalName() );
			((REF)builder.getElement()).setQName( getQName() );
			((REF)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((REF)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("NULL") ) {
						set__NULL( atts.getValue(i) );
						((REF)builder.getElement()).set__NULL( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V") ) {
						set__V( atts.getValue(i) );
						((REF)builder.getElement()).set__V( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-T") ) {
						set__V_T( atts.getValue(i) );
						((REF)builder.getElement()).set__V_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-HL7_NAME") ) {
						set__V_HL7_NAME( atts.getValue(i) );
						((REF)builder.getElement()).set__V_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("USE") ) {
						set__USE( atts.getValue(i) );
						((REF)builder.getElement()).set__USE( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("USE-T") ) {
						set__USE_T( atts.getValue(i) );
						((REF)builder.getElement()).set__USE_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("USE-DOMAIN") ) {
						set__USE_DOMAIN( atts.getValue(i) );
						((REF)builder.getElement()).set__USE_DOMAIN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("USE-HL7_NAME") ) {
						set__USE_HL7_NAME( atts.getValue(i) );
						((REF)builder.getElement()).set__USE_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT") ) {
						set__VT( atts.getValue(i) );
						((REF)builder.getElement()).set__VT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-T") ) {
						set__VT_T( atts.getValue(i) );
						((REF)builder.getElement()).set__VT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-HL7_NAME") ) {
						set__VT_HL7_NAME( atts.getValue(i) );
						((REF)builder.getElement()).set__VT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB") ) {
						set__PROB( atts.getValue(i) );
						((REF)builder.getElement()).set__PROB( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-T") ) {
						set__PROB_T( atts.getValue(i) );
						((REF)builder.getElement()).set__PROB_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-HL7_NAME") ) {
						set__PROB_HL7_NAME( atts.getValue(i) );
						((REF)builder.getElement()).set__PROB_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7_NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((REF)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("REF") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("THUMBNAIL")) {
				Vector v = ((THUMBNAIL)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (REF)builder.getElement() );
			}

			if (parentElement.getQName().equals("ORIGTXT")) {
				Vector v = ((ORIGTXT)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (REF)builder.getElement() );
			}

			if (parentElement.getQName().equals("observation_media.value")) {
				Vector v = ((observation_media__value)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (REF)builder.getElement() );
			}

			if (parentElement.getQName().equals("non_xml")) {
				Vector v = ((non_xml)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (REF)builder.getElement() );
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
	public void set__NULL(String __NULL) {
		this.__NULL = __NULL;
	}
	public String get__NULL() {
		return __NULL;
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
	public void set__USE(String __USE) {
		this.__USE = __USE;
	}
	public String get__USE() {
		return __USE;
	}
	public void set__USE_T(String __USE_T) {
		this.__USE_T = __USE_T;
	}
	public String get__USE_T() {
		return __USE_T;
	}
	public void set__USE_DOMAIN(String __USE_DOMAIN) {
		this.__USE_DOMAIN = __USE_DOMAIN;
	}
	public String get__USE_DOMAIN() {
		return __USE_DOMAIN;
	}
	public void set__USE_HL7_NAME(String __USE_HL7_NAME) {
		this.__USE_HL7_NAME = __USE_HL7_NAME;
	}
	public String get__USE_HL7_NAME() {
		return __USE_HL7_NAME;
	}
	public void set__VT(String __VT) {
		this.__VT = __VT;
	}
	public String get__VT() {
		return __VT;
	}
	public void set__VT_T(String __VT_T) {
		this.__VT_T = __VT_T;
	}
	public String get__VT_T() {
		return __VT_T;
	}
	public void set__VT_HL7_NAME(String __VT_HL7_NAME) {
		this.__VT_HL7_NAME = __VT_HL7_NAME;
	}
	public String get__VT_HL7_NAME() {
		return __VT_HL7_NAME;
	}
	public void set__PROB(String __PROB) {
		this.__PROB = __PROB;
	}
	public String get__PROB() {
		return __PROB;
	}
	public void set__PROB_T(String __PROB_T) {
		this.__PROB_T = __PROB_T;
	}
	public String get__PROB_T() {
		return __PROB_T;
	}
	public void set__PROB_HL7_NAME(String __PROB_HL7_NAME) {
		this.__PROB_HL7_NAME = __PROB_HL7_NAME;
	}
	public String get__PROB_HL7_NAME() {
		return __PROB_HL7_NAME;
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