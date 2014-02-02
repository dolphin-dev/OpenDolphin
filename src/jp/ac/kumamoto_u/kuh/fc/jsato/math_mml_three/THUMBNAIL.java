/**
 *
 * THUMBNAIL.java
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
public class THUMBNAIL extends MMLObject {
	
	/* fields */
	private String __T = null;
	private String __NULL = null;
	private String __ENC = null;
	private String __MT = null;
	private String __MT_T = null;
	private String __MT_DOMAIN = null;
	private String __MT_HL7_NAME = null;
	private String __xmllang = null;
	private String __xmllang_T = null;
	private String __xmllang_HL7_NAME = null;
	private String __COMPN = null;
	private String __COMPN_T = null;
	private String __COMPN_HL7_NAME = null;
	private String __COMPN_DOMAIN = null;
	private String __IC = null;
	private String __IC_T = null;
	private String __IC_HL7_NAME = null;
	private String __VT = null;
	private String __VT_T = null;
	private String __VT_HL7_NAME = null;
	private String __PROB = null;
	private String __PROB_T = null;
	private String __PROB_HL7_NAME = null;
	private String __HL7_NAME = null;

	private Vector vt = new Vector();
	
	public THUMBNAIL() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __T != null ) pw.print(" " + "T" +  "=" + "'" + __T + "'");
			if ( __NULL != null ) pw.print(" " + "NULL" +  "=" + "'" + __NULL + "'");
			if ( __ENC != null ) pw.print(" " + "ENC" +  "=" + "'" + __ENC + "'");
			if ( __MT != null ) pw.print(" " + "MT" +  "=" + "'" + __MT + "'");
			if ( __MT_T != null ) pw.print(" " + "MT-T" +  "=" + "'" + __MT_T + "'");
			if ( __MT_DOMAIN != null ) pw.print(" " + "MT-DOMAIN" +  "=" + "'" + __MT_DOMAIN + "'");
			if ( __MT_HL7_NAME != null ) pw.print(" " + "MT-HL7_NAME" +  "=" + "'" + __MT_HL7_NAME + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");
			if ( __xmllang_T != null ) pw.print(" " + "xml:lang-T" +  "=" + "'" + __xmllang_T + "'");
			if ( __xmllang_HL7_NAME != null ) pw.print(" " + "xml:lang-HL7_NAME" +  "=" + "'" + __xmllang_HL7_NAME + "'");
			if ( __COMPN != null ) pw.print(" " + "COMPN" +  "=" + "'" + __COMPN + "'");
			if ( __COMPN_T != null ) pw.print(" " + "COMPN-T" +  "=" + "'" + __COMPN_T + "'");
			if ( __COMPN_HL7_NAME != null ) pw.print(" " + "COMPN-HL7_NAME" +  "=" + "'" + __COMPN_HL7_NAME + "'");
			if ( __COMPN_DOMAIN != null ) pw.print(" " + "COMPN-DOMAIN" +  "=" + "'" + __COMPN_DOMAIN + "'");
			if ( __IC != null ) pw.print(" " + "IC" +  "=" + "'" + __IC + "'");
			if ( __IC_T != null ) pw.print(" " + "IC-T" +  "=" + "'" + __IC_T + "'");
			if ( __IC_HL7_NAME != null ) pw.print(" " + "IC-HL7_NAME" +  "=" + "'" + __IC_HL7_NAME + "'");
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
		if (qName.equals("THUMBNAIL") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			THUMBNAIL obj = new THUMBNAIL();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((THUMBNAIL)builder.getElement()).setNamespace( getNamespace() );
			((THUMBNAIL)builder.getElement()).setLocalName( getLocalName() );
			((THUMBNAIL)builder.getElement()).setQName( getQName() );
			((THUMBNAIL)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("NULL") ) {
						set__NULL( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__NULL( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ENC") ) {
						set__ENC( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__ENC( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("MT") ) {
						set__MT( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__MT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("MT-T") ) {
						set__MT_T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__MT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("MT-DOMAIN") ) {
						set__MT_DOMAIN( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__MT_DOMAIN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("MT-HL7_NAME") ) {
						set__MT_HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__MT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang-T") ) {
						set__xmllang_T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__xmllang_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang-HL7_NAME") ) {
						set__xmllang_HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__xmllang_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("COMPN") ) {
						set__COMPN( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__COMPN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("COMPN-T") ) {
						set__COMPN_T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__COMPN_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("COMPN-HL7_NAME") ) {
						set__COMPN_HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__COMPN_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("COMPN-DOMAIN") ) {
						set__COMPN_DOMAIN( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__COMPN_DOMAIN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("IC") ) {
						set__IC( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__IC( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("IC-T") ) {
						set__IC_T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__IC_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("IC-HL7_NAME") ) {
						set__IC_HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__IC_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT") ) {
						set__VT( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__VT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-T") ) {
						set__VT_T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__VT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-HL7_NAME") ) {
						set__VT_HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__VT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB") ) {
						set__PROB( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__PROB( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-T") ) {
						set__PROB_T( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__PROB_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-HL7_NAME") ) {
						set__PROB_HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__PROB_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7_NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((THUMBNAIL)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("THUMBNAIL") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("THUMBNAIL")) {
				Vector v = ((THUMBNAIL)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (THUMBNAIL)builder.getElement() );
			}

			if (parentElement.getQName().equals("ORIGTXT")) {
				Vector v = ((ORIGTXT)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (THUMBNAIL)builder.getElement() );
			}

			if (parentElement.getQName().equals("observation_media.value")) {
				Vector v = ((observation_media__value)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (THUMBNAIL)builder.getElement() );
			}

			if (parentElement.getQName().equals("non_xml")) {
				Vector v = ((non_xml)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (THUMBNAIL)builder.getElement() );
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
		if (builder.getCurrentElement().getQName().equals("THUMBNAIL")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((THUMBNAIL)builder.getElement()).getVt().addElement(buffer.toString());
			printlnStatus(parentElement.getQName() + " " + this.getQName() + ":" + buffer.toString());
			return true;
		}
		return false;
	}
	
	
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
	public void set__ENC(String __ENC) {
		this.__ENC = __ENC;
	}
	public String get__ENC() {
		return __ENC;
	}
	public void set__MT(String __MT) {
		this.__MT = __MT;
	}
	public String get__MT() {
		return __MT;
	}
	public void set__MT_T(String __MT_T) {
		this.__MT_T = __MT_T;
	}
	public String get__MT_T() {
		return __MT_T;
	}
	public void set__MT_DOMAIN(String __MT_DOMAIN) {
		this.__MT_DOMAIN = __MT_DOMAIN;
	}
	public String get__MT_DOMAIN() {
		return __MT_DOMAIN;
	}
	public void set__MT_HL7_NAME(String __MT_HL7_NAME) {
		this.__MT_HL7_NAME = __MT_HL7_NAME;
	}
	public String get__MT_HL7_NAME() {
		return __MT_HL7_NAME;
	}
	public void set__xmllang(String __xmllang) {
		this.__xmllang = __xmllang;
	}
	public String get__xmllang() {
		return __xmllang;
	}
	public void set__xmllang_T(String __xmllang_T) {
		this.__xmllang_T = __xmllang_T;
	}
	public String get__xmllang_T() {
		return __xmllang_T;
	}
	public void set__xmllang_HL7_NAME(String __xmllang_HL7_NAME) {
		this.__xmllang_HL7_NAME = __xmllang_HL7_NAME;
	}
	public String get__xmllang_HL7_NAME() {
		return __xmllang_HL7_NAME;
	}
	public void set__COMPN(String __COMPN) {
		this.__COMPN = __COMPN;
	}
	public String get__COMPN() {
		return __COMPN;
	}
	public void set__COMPN_T(String __COMPN_T) {
		this.__COMPN_T = __COMPN_T;
	}
	public String get__COMPN_T() {
		return __COMPN_T;
	}
	public void set__COMPN_HL7_NAME(String __COMPN_HL7_NAME) {
		this.__COMPN_HL7_NAME = __COMPN_HL7_NAME;
	}
	public String get__COMPN_HL7_NAME() {
		return __COMPN_HL7_NAME;
	}
	public void set__COMPN_DOMAIN(String __COMPN_DOMAIN) {
		this.__COMPN_DOMAIN = __COMPN_DOMAIN;
	}
	public String get__COMPN_DOMAIN() {
		return __COMPN_DOMAIN;
	}
	public void set__IC(String __IC) {
		this.__IC = __IC;
	}
	public String get__IC() {
		return __IC;
	}
	public void set__IC_T(String __IC_T) {
		this.__IC_T = __IC_T;
	}
	public String get__IC_T() {
		return __IC_T;
	}
	public void set__IC_HL7_NAME(String __IC_HL7_NAME) {
		this.__IC_HL7_NAME = __IC_HL7_NAME;
	}
	public String get__IC_HL7_NAME() {
		return __IC_HL7_NAME;
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