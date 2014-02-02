/**
 *
 * NOTE.java
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
public class NOTE extends MMLObject {
	
	/* fields */
	private String __T = null;
	private String __NULL = null;
	private String __V = null;
	private String __V_T = null;
	private String __V_HL7_NAME = null;
	private String __DN = null;
	private String __DN_T = null;
	private String __DN_HL7_NAME = null;
	private String __S = null;
	private String __S_T = null;
	private String __S_HL7_NAME = null;
	private String __SN = null;
	private String __SN_T = null;
	private String __SN_HL7_NAME = null;
	private String __SV = null;
	private String __SV_T = null;
	private String __SV_HL7_NAME = null;
	private String __ORIGTXT = null;
	private String __ORIGTXT_T = null;
	private String __ORIGTXT_HL7_NAME = null;
	private String __VT = null;
	private String __VT_T = null;
	private String __VT_HL7_NAME = null;
	private String __PROB = null;
	private String __PROB_T = null;
	private String __PROB_HL7_NAME = null;
	private String __HL7_NAME = null;

	private ORIGTXT _ORIGTXT = null;
	private Vector _TRANSLTN = new Vector();
	private NOTE _NOTE = null;
	private CONFID _CONFID = null;
	
	public NOTE() {
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
			if ( __DN != null ) pw.print(" " + "DN" +  "=" + "'" + __DN + "'");
			if ( __DN_T != null ) pw.print(" " + "DN-T" +  "=" + "'" + __DN_T + "'");
			if ( __DN_HL7_NAME != null ) pw.print(" " + "DN-HL7_NAME" +  "=" + "'" + __DN_HL7_NAME + "'");
			if ( __S != null ) pw.print(" " + "S" +  "=" + "'" + __S + "'");
			if ( __S_T != null ) pw.print(" " + "S-T" +  "=" + "'" + __S_T + "'");
			if ( __S_HL7_NAME != null ) pw.print(" " + "S-HL7_NAME" +  "=" + "'" + __S_HL7_NAME + "'");
			if ( __SN != null ) pw.print(" " + "SN" +  "=" + "'" + __SN + "'");
			if ( __SN_T != null ) pw.print(" " + "SN-T" +  "=" + "'" + __SN_T + "'");
			if ( __SN_HL7_NAME != null ) pw.print(" " + "SN-HL7_NAME" +  "=" + "'" + __SN_HL7_NAME + "'");
			if ( __SV != null ) pw.print(" " + "SV" +  "=" + "'" + __SV + "'");
			if ( __SV_T != null ) pw.print(" " + "SV-T" +  "=" + "'" + __SV_T + "'");
			if ( __SV_HL7_NAME != null ) pw.print(" " + "SV-HL7_NAME" +  "=" + "'" + __SV_HL7_NAME + "'");
			if ( __ORIGTXT != null ) pw.print(" " + "ORIGTXT" +  "=" + "'" + __ORIGTXT + "'");
			if ( __ORIGTXT_T != null ) pw.print(" " + "ORIGTXT-T" +  "=" + "'" + __ORIGTXT_T + "'");
			if ( __ORIGTXT_HL7_NAME != null ) pw.print(" " + "ORIGTXT-HL7_NAME" +  "=" + "'" + __ORIGTXT_HL7_NAME + "'");
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
			if ( _ORIGTXT != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ORIGTXT.printObject(pw, visitor);
			}
			if (this._TRANSLTN != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._TRANSLTN.size(); ++i ) {
					((TRANSLTN)this._TRANSLTN.elementAt(i)).printObject(pw, visitor);
				}
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
		if (qName.equals("NOTE") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			NOTE obj = new NOTE();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((NOTE)builder.getElement()).setNamespace( getNamespace() );
			((NOTE)builder.getElement()).setLocalName( getLocalName() );
			((NOTE)builder.getElement()).setQName( getQName() );
			((NOTE)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("NULL") ) {
						set__NULL( atts.getValue(i) );
						((NOTE)builder.getElement()).set__NULL( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V") ) {
						set__V( atts.getValue(i) );
						((NOTE)builder.getElement()).set__V( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-T") ) {
						set__V_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__V_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("V-HL7_NAME") ) {
						set__V_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__V_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("DN") ) {
						set__DN( atts.getValue(i) );
						((NOTE)builder.getElement()).set__DN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("DN-T") ) {
						set__DN_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__DN_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("DN-HL7_NAME") ) {
						set__DN_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__DN_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("S") ) {
						set__S( atts.getValue(i) );
						((NOTE)builder.getElement()).set__S( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("S-T") ) {
						set__S_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__S_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("S-HL7_NAME") ) {
						set__S_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__S_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("SN") ) {
						set__SN( atts.getValue(i) );
						((NOTE)builder.getElement()).set__SN( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("SN-T") ) {
						set__SN_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__SN_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("SN-HL7_NAME") ) {
						set__SN_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__SN_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("SV") ) {
						set__SV( atts.getValue(i) );
						((NOTE)builder.getElement()).set__SV( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("SV-T") ) {
						set__SV_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__SV_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("SV-HL7_NAME") ) {
						set__SV_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__SV_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ORIGTXT") ) {
						set__ORIGTXT( atts.getValue(i) );
						((NOTE)builder.getElement()).set__ORIGTXT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ORIGTXT-T") ) {
						set__ORIGTXT_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__ORIGTXT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("ORIGTXT-HL7_NAME") ) {
						set__ORIGTXT_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__ORIGTXT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT") ) {
						set__VT( atts.getValue(i) );
						((NOTE)builder.getElement()).set__VT( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-T") ) {
						set__VT_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__VT_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("VT-HL7_NAME") ) {
						set__VT_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__VT_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB") ) {
						set__PROB( atts.getValue(i) );
						((NOTE)builder.getElement()).set__PROB( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-T") ) {
						set__PROB_T( atts.getValue(i) );
						((NOTE)builder.getElement()).set__PROB_T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("PROB-HL7_NAME") ) {
						set__PROB_HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__PROB_HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7_NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((NOTE)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("NOTE") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("THUMBNAIL")) {
				Vector v = ((THUMBNAIL)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (NOTE)builder.getElement() );
			}

			if (parentElement.getQName().equals("ORIGTXT")) {
				Vector v = ((ORIGTXT)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (NOTE)builder.getElement() );
			}

			if (parentElement.getQName().equals("observation_media.value")) {
				Vector v = ((observation_media__value)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (NOTE)builder.getElement() );
			}

			if (parentElement.getQName().equals("non_xml")) {
				Vector v = ((non_xml)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (NOTE)builder.getElement() );
			}

			if (parentElement.getQName().equals("nm")) {
				Vector v = ((nm)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (NOTE)builder.getElement() );
			}

			if (parentElement.getQName().equals("addr")) {
				Vector v = ((addr)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (NOTE)builder.getElement() );
			}

			if (parentElement.getQName().equals("ZIP")) {
				((ZIP)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("version_nbr")) {
				((version_nbr)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("TYPE")) {
				((TYPE)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("TRANSLTN")) {
				((TRANSLTN)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("transcriptionist.type_cd")) {
				((transcriptionist__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("telecom")) {
				((telecom)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("STR")) {
				((STR)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("STA")) {
				((STA)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("signature_cd")) {
				((signature_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("SFX")) {
				((SFX)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("set_id")) {
				((set_id)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("service_tmr")) {
				((service_tmr)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("service_target.type_cd")) {
				((service_target__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("service_actor.type_cd")) {
				((service_actor__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("responsibility.type_cd")) {
				((responsibility__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("responsibility_tmr")) {
				((responsibility_tmr)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("REF")) {
				((REF)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("provider.type_cd")) {
				((provider__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("practice_setting_cd")) {
				((practice_setting_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("POB")) {
				((POB)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("PN.DEL")) {
				((PN__DEL)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("PFX")) {
				((PFX)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("person_name.type_cd")) {
				((person_name__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("patient.type_cd")) {
				((patient__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("participation_tmr")) {
				((participation_tmr)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("originator.type_cd")) {
				((originator__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("origination_dttm")) {
				((origination_dttm)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("originating_organization.type_cd")) {
				((originating_organization__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("originating_device.type_cd")) {
				((originating_device__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("organization.nm")) {
				((organization__nm)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("observation_media.id")) {
				((observation_media__id)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("NUM")) {
				((NUM)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("NOTE")) {
				((NOTE)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("NAME")) {
				((NAME)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("MODIFIER")) {
				((MODIFIER)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("MID")) {
				((MID)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("LIT")) {
				((LIT)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("legal_authenticator.type_cd")) {
				((legal_authenticator__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("intended_recipient.type_cd")) {
				((intended_recipient__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("id")) {
				((id)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("HNR")) {
				((HNR)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("GIV")) {
				((GIV)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("function_cd")) {
				((function_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("fulfills_order.type_cd")) {
				((fulfills_order__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("FAM")) {
				((FAM)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("encounter_tmr")) {
				((encounter_tmr)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("effective_tmr")) {
				((effective_tmr)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("document_type_cd")) {
				((document_type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("document_relationship.type_cd")) {
				((document_relationship__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("DIR")) {
				((DIR)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("DENOM")) {
				((DENOM)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("DEL")) {
				((DEL)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("CTY")) {
				((CTY)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("copy_dttm")) {
				((copy_dttm)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("confidentiality_cd")) {
				((confidentiality_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("CONFID")) {
				((CONFID)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("coded_entry.value")) {
				((coded_entry__value)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("coded_entry.id")) {
				((coded_entry__id)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("CNT")) {
				((CNT)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("caption_cd")) {
				((caption_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("birth_dttm")) {
				((birth_dttm)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("authenticator.type_cd")) {
				((authenticator__type_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("administrative_gender_cd")) {
				((administrative_gender_cd)builder.getParent()).set_NOTE((NOTE)builder.getElement());
			}

			if (parentElement.getQName().equals("ADL")) {
				((ADL)builder.getParent()).set_NOTE((NOTE)builder.getElement());
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
	public void set__S(String __S) {
		this.__S = __S;
	}
	public String get__S() {
		return __S;
	}
	public void set__S_T(String __S_T) {
		this.__S_T = __S_T;
	}
	public String get__S_T() {
		return __S_T;
	}
	public void set__S_HL7_NAME(String __S_HL7_NAME) {
		this.__S_HL7_NAME = __S_HL7_NAME;
	}
	public String get__S_HL7_NAME() {
		return __S_HL7_NAME;
	}
	public void set__SN(String __SN) {
		this.__SN = __SN;
	}
	public String get__SN() {
		return __SN;
	}
	public void set__SN_T(String __SN_T) {
		this.__SN_T = __SN_T;
	}
	public String get__SN_T() {
		return __SN_T;
	}
	public void set__SN_HL7_NAME(String __SN_HL7_NAME) {
		this.__SN_HL7_NAME = __SN_HL7_NAME;
	}
	public String get__SN_HL7_NAME() {
		return __SN_HL7_NAME;
	}
	public void set__SV(String __SV) {
		this.__SV = __SV;
	}
	public String get__SV() {
		return __SV;
	}
	public void set__SV_T(String __SV_T) {
		this.__SV_T = __SV_T;
	}
	public String get__SV_T() {
		return __SV_T;
	}
	public void set__SV_HL7_NAME(String __SV_HL7_NAME) {
		this.__SV_HL7_NAME = __SV_HL7_NAME;
	}
	public String get__SV_HL7_NAME() {
		return __SV_HL7_NAME;
	}
	public void set__ORIGTXT(String __ORIGTXT) {
		this.__ORIGTXT = __ORIGTXT;
	}
	public String get__ORIGTXT() {
		return __ORIGTXT;
	}
	public void set__ORIGTXT_T(String __ORIGTXT_T) {
		this.__ORIGTXT_T = __ORIGTXT_T;
	}
	public String get__ORIGTXT_T() {
		return __ORIGTXT_T;
	}
	public void set__ORIGTXT_HL7_NAME(String __ORIGTXT_HL7_NAME) {
		this.__ORIGTXT_HL7_NAME = __ORIGTXT_HL7_NAME;
	}
	public String get__ORIGTXT_HL7_NAME() {
		return __ORIGTXT_HL7_NAME;
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

	public void set_ORIGTXT(ORIGTXT _ORIGTXT) {
		this._ORIGTXT = _ORIGTXT;
	}
	public ORIGTXT get_ORIGTXT() {
		return _ORIGTXT;
	}
	public void set_TRANSLTN(Vector _TRANSLTN) {
		if (this._TRANSLTN != null) this._TRANSLTN.removeAllElements();
		// copy entire elements in the vector
		this._TRANSLTN = new Vector();
		for (int i = 0; i < _TRANSLTN.size(); ++i) {
			this._TRANSLTN.addElement( _TRANSLTN.elementAt(i) );
		}
	}
	public Vector get_TRANSLTN() {
		return _TRANSLTN;
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