/**
 *
 * nm.java
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
public class nm extends MMLObject {
	
	/* fields */
	private String __T = null;
	private String __NULL = null;
	private String __V = null;
	private String __V_T = null;
	private String __V_HL7_NAME = null;
	private String __VT = null;
	private String __VT_T = null;
	private String __VT_HL7_NAME = null;
	private String __PROB = null;
	private String __PROB_T = null;
	private String __PROB_HL7_NAME = null;
	private String __ID = null;
	private String __HL7_NAME = null;

	private Vector vt = new Vector();
	
	public nm() {
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
			if ( __VT != null ) pw.print(" " + "VT" +  "=" + "'" + __VT + "'");
			if ( __VT_T != null ) pw.print(" " + "VT-T" +  "=" + "'" + __VT_T + "'");
			if ( __VT_HL7_NAME != null ) pw.print(" " + "VT-HL7_NAME" +  "=" + "'" + __VT_HL7_NAME + "'");
			if ( __PROB != null ) pw.print(" " + "PROB" +  "=" + "'" + __PROB + "'");
			if ( __PROB_T != null ) pw.print(" " + "PROB-T" +  "=" + "'" + __PROB_T + "'");
			if ( __PROB_HL7_NAME != null ) pw.print(" " + "PROB-HL7_NAME" +  "=" + "'" + __PROB_HL7_NAME + "'");
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __HL7_NAME != null ) pw.print(" " + "HL7-NAME" +  "=" + "'" + __HL7_NAME + "'");

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
		if (qName.equals("nm") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			nm obj = new nm();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((nm)builder.getElement()).setNamespace( getNamespace() );
			((nm)builder.getElement()).setLocalName( getLocalName() );
			((nm)builder.getElement()).setQName( getQName() );
			((nm)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((nm)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("NULL") ) {
						set__NULL( atts.getValue(i) );
						((nm)builder.getElement()).set__NULL( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V") ) {
						set__V( atts.getValue(i) );
						((nm)builder.getElement()).set__V( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-T") ) {
						set__V_T( atts.getValue(i) );
						((nm)builder.getElement()).set__V_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-HL7_NAME") ) {
						set__V_HL7_NAME( atts.getValue(i) );
						((nm)builder.getElement()).set__V_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT") ) {
						set__VT( atts.getValue(i) );
						((nm)builder.getElement()).set__VT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-T") ) {
						set__VT_T( atts.getValue(i) );
						((nm)builder.getElement()).set__VT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-HL7_NAME") ) {
						set__VT_HL7_NAME( atts.getValue(i) );
						((nm)builder.getElement()).set__VT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB") ) {
						set__PROB( atts.getValue(i) );
						((nm)builder.getElement()).set__PROB( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-T") ) {
						set__PROB_T( atts.getValue(i) );
						((nm)builder.getElement()).set__PROB_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-HL7_NAME") ) {
						set__PROB_HL7_NAME( atts.getValue(i) );
						((nm)builder.getElement()).set__PROB_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((nm)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((nm)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("nm") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("person_name")) {
				((person_name)builder.getParent()).set_nm((nm)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("nm")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((nm)builder.getElement()).getVt().addElement(buffer.toString());
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