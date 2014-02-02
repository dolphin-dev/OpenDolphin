/**
 *
 * set_id.java
 * Created on 2003/1/4 2:29:57
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
public class set_id extends MMLObject {
	
	/* fields */
	private String __T = null;
	private String __NULL = null;
	private String __EX = null;
	private String __EX_T = null;
	private String __EX_HL7_NAME = null;
	private String __RT = null;
	private String __RT_T = null;
	private String __RT_HL7_NAME = null;
	private String __AAN = null;
	private String __AAN_T = null;
	private String __AAN_HL7_NAME = null;
	private String __VT = null;
	private String __VT_T = null;
	private String __VT_HL7_NAME = null;
	private String __PROB = null;
	private String __PROB_T = null;
	private String __PROB_HL7_NAME = null;
	private String __ID = null;
	private String __HL7_NAME = null;

	private TYPE _TYPE = null;
	private NOTE _NOTE = null;
	private CONFID _CONFID = null;
	
	public set_id() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __T != null ) pw.print(" " + "T" +  "=" + "'" + __T + "'");
			if ( __NULL != null ) pw.print(" " + "NULL" +  "=" + "'" + __NULL + "'");
			if ( __EX != null ) pw.print(" " + "EX" +  "=" + "'" + __EX + "'");
			if ( __EX_T != null ) pw.print(" " + "EX-T" +  "=" + "'" + __EX_T + "'");
			if ( __EX_HL7_NAME != null ) pw.print(" " + "EX-HL7_NAME" +  "=" + "'" + __EX_HL7_NAME + "'");
			if ( __RT != null ) pw.print(" " + "RT" +  "=" + "'" + __RT + "'");
			if ( __RT_T != null ) pw.print(" " + "RT-T" +  "=" + "'" + __RT_T + "'");
			if ( __RT_HL7_NAME != null ) pw.print(" " + "RT-HL7_NAME" +  "=" + "'" + __RT_HL7_NAME + "'");
			if ( __AAN != null ) pw.print(" " + "AAN" +  "=" + "'" + __AAN + "'");
			if ( __AAN_T != null ) pw.print(" " + "AAN-T" +  "=" + "'" + __AAN_T + "'");
			if ( __AAN_HL7_NAME != null ) pw.print(" " + "AAN-HL7_NAME" +  "=" + "'" + __AAN_HL7_NAME + "'");
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
			if ( _TYPE != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_TYPE.printObject(pw, visitor);
			}
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
		if (qName.equals("set_id") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			set_id obj = new set_id();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((set_id)builder.getElement()).setNamespace( getNamespace() );
			((set_id)builder.getElement()).setLocalName( getLocalName() );
			((set_id)builder.getElement()).setQName( getQName() );
			((set_id)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((set_id)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("NULL") ) {
						set__NULL( atts.getValue(i) );
						((set_id)builder.getElement()).set__NULL( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("EX") ) {
						set__EX( atts.getValue(i) );
						((set_id)builder.getElement()).set__EX( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("EX-T") ) {
						set__EX_T( atts.getValue(i) );
						((set_id)builder.getElement()).set__EX_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("EX-HL7_NAME") ) {
						set__EX_HL7_NAME( atts.getValue(i) );
						((set_id)builder.getElement()).set__EX_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("RT") ) {
						set__RT( atts.getValue(i) );
						((set_id)builder.getElement()).set__RT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("RT-T") ) {
						set__RT_T( atts.getValue(i) );
						((set_id)builder.getElement()).set__RT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("RT-HL7_NAME") ) {
						set__RT_HL7_NAME( atts.getValue(i) );
						((set_id)builder.getElement()).set__RT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("AAN") ) {
						set__AAN( atts.getValue(i) );
						((set_id)builder.getElement()).set__AAN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("AAN-T") ) {
						set__AAN_T( atts.getValue(i) );
						((set_id)builder.getElement()).set__AAN_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("AAN-HL7_NAME") ) {
						set__AAN_HL7_NAME( atts.getValue(i) );
						((set_id)builder.getElement()).set__AAN_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT") ) {
						set__VT( atts.getValue(i) );
						((set_id)builder.getElement()).set__VT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-T") ) {
						set__VT_T( atts.getValue(i) );
						((set_id)builder.getElement()).set__VT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-HL7_NAME") ) {
						set__VT_HL7_NAME( atts.getValue(i) );
						((set_id)builder.getElement()).set__VT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB") ) {
						set__PROB( atts.getValue(i) );
						((set_id)builder.getElement()).set__PROB( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-T") ) {
						set__PROB_T( atts.getValue(i) );
						((set_id)builder.getElement()).set__PROB_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-HL7_NAME") ) {
						set__PROB_HL7_NAME( atts.getValue(i) );
						((set_id)builder.getElement()).set__PROB_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((set_id)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((set_id)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("set_id") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("related_document")) {
				((related_document)builder.getParent()).set_set_id((set_id)builder.getElement());
			}

			if (parentElement.getQName().equals("clinical_document_header")) {
				((clinical_document_header)builder.getParent()).set_set_id((set_id)builder.getElement());
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
	public void set__EX(String __EX) {
		this.__EX = __EX;
	}
	public String get__EX() {
		return __EX;
	}
	public void set__EX_T(String __EX_T) {
		this.__EX_T = __EX_T;
	}
	public String get__EX_T() {
		return __EX_T;
	}
	public void set__EX_HL7_NAME(String __EX_HL7_NAME) {
		this.__EX_HL7_NAME = __EX_HL7_NAME;
	}
	public String get__EX_HL7_NAME() {
		return __EX_HL7_NAME;
	}
	public void set__RT(String __RT) {
		this.__RT = __RT;
	}
	public String get__RT() {
		return __RT;
	}
	public void set__RT_T(String __RT_T) {
		this.__RT_T = __RT_T;
	}
	public String get__RT_T() {
		return __RT_T;
	}
	public void set__RT_HL7_NAME(String __RT_HL7_NAME) {
		this.__RT_HL7_NAME = __RT_HL7_NAME;
	}
	public String get__RT_HL7_NAME() {
		return __RT_HL7_NAME;
	}
	public void set__AAN(String __AAN) {
		this.__AAN = __AAN;
	}
	public String get__AAN() {
		return __AAN;
	}
	public void set__AAN_T(String __AAN_T) {
		this.__AAN_T = __AAN_T;
	}
	public String get__AAN_T() {
		return __AAN_T;
	}
	public void set__AAN_HL7_NAME(String __AAN_HL7_NAME) {
		this.__AAN_HL7_NAME = __AAN_HL7_NAME;
	}
	public String get__AAN_HL7_NAME() {
		return __AAN_HL7_NAME;
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

	public void set_TYPE(TYPE _TYPE) {
		this._TYPE = _TYPE;
	}
	public TYPE get_TYPE() {
		return _TYPE;
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