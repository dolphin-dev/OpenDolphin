/**
 *
 * patient.java
 * Created on 2003/1/4 2:29:59
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
public class patient extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private patient__type_cd _patient__type_cd = null;
	private participation_tmr _participation_tmr = null;
	private person _person = null;
	private Vector _is_known_by = new Vector();
	private birth_dttm _birth_dttm = null;
	private administrative_gender_cd _administrative_gender_cd = null;
	private Vector _local_header = new Vector();
	
	public patient() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __HL7_NAME != null ) pw.print(" " + "HL7-NAME" +  "=" + "'" + __HL7_NAME + "'");
			if ( __T != null ) pw.print(" " + "T" +  "=" + "'" + __T + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _patient__type_cd != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patient__type_cd.printObject(pw, visitor);
			}
			if ( _participation_tmr != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_participation_tmr.printObject(pw, visitor);
			}
			if ( _person != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_person.printObject(pw, visitor);
			}
			if (this._is_known_by != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._is_known_by.size(); ++i ) {
					((is_known_by)this._is_known_by.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _birth_dttm != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_birth_dttm.printObject(pw, visitor);
			}
			if ( _administrative_gender_cd != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_administrative_gender_cd.printObject(pw, visitor);
			}
			if (this._local_header != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._local_header.size(); ++i ) {
					((local_header)this._local_header.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("patient") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			patient obj = new patient();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((patient)builder.getElement()).setNamespace( getNamespace() );
			((patient)builder.getElement()).setLocalName( getLocalName() );
			((patient)builder.getElement()).setQName( getQName() );
			((patient)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((patient)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((patient)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((patient)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("patient") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("clinical_document_header")) {
				((clinical_document_header)builder.getParent()).set_patient((patient)builder.getElement());
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
	public void set__HL7_NAME(String __HL7_NAME) {
		this.__HL7_NAME = __HL7_NAME;
	}
	public String get__HL7_NAME() {
		return __HL7_NAME;
	}
	public void set__T(String __T) {
		this.__T = __T;
	}
	public String get__T() {
		return __T;
	}

	public void set_patient__type_cd(patient__type_cd _patient__type_cd) {
		this._patient__type_cd = _patient__type_cd;
	}
	public patient__type_cd get_patient__type_cd() {
		return _patient__type_cd;
	}
	public void set_participation_tmr(participation_tmr _participation_tmr) {
		this._participation_tmr = _participation_tmr;
	}
	public participation_tmr get_participation_tmr() {
		return _participation_tmr;
	}
	public void set_person(person _person) {
		this._person = _person;
	}
	public person get_person() {
		return _person;
	}
	public void set_is_known_by(Vector _is_known_by) {
		if (this._is_known_by != null) this._is_known_by.removeAllElements();
		// copy entire elements in the vector
		this._is_known_by = new Vector();
		for (int i = 0; i < _is_known_by.size(); ++i) {
			this._is_known_by.addElement( _is_known_by.elementAt(i) );
		}
	}
	public Vector get_is_known_by() {
		return _is_known_by;
	}
	public void set_birth_dttm(birth_dttm _birth_dttm) {
		this._birth_dttm = _birth_dttm;
	}
	public birth_dttm get_birth_dttm() {
		return _birth_dttm;
	}
	public void set_administrative_gender_cd(administrative_gender_cd _administrative_gender_cd) {
		this._administrative_gender_cd = _administrative_gender_cd;
	}
	public administrative_gender_cd get_administrative_gender_cd() {
		return _administrative_gender_cd;
	}
	public void set_local_header(Vector _local_header) {
		if (this._local_header != null) this._local_header.removeAllElements();
		// copy entire elements in the vector
		this._local_header = new Vector();
		for (int i = 0; i < _local_header.size(); ++i) {
			this._local_header.addElement( _local_header.elementAt(i) );
		}
	}
	public Vector get_local_header() {
		return _local_header;
	}
	
}