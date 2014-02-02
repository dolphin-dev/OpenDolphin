/**
 *
 * clinical_document_header.java
 * Created on 2003/1/4 2:29:56
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
public class clinical_document_header extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;
	private String __RIM_VERSION = null;

	private id _id = null;
	private set_id _set_id = null;
	private version_nbr _version_nbr = null;
	private document_type_cd _document_type_cd = null;
	private service_tmr _service_tmr = null;
	private origination_dttm _origination_dttm = null;
	private copy_dttm _copy_dttm = null;
	private Vector _confidentiality_cd = new Vector();
	private Vector _document_relationship = new Vector();
	private fulfills_order _fulfills_order = null;
	private patient_encounter _patient_encounter = null;
	private Vector _authenticator = new Vector();
	private legal_authenticator _legal_authenticator = null;
	private Vector _intended_recipient = new Vector();
	private Vector _originator = new Vector();
	private originating_organization _originating_organization = null;
	private transcriptionist _transcriptionist = null;
	private Vector _provider = new Vector();
	private Vector _service_actor = new Vector();
	private patient _patient = null;
	private Vector _originating_device = new Vector();
	private Vector _service_target = new Vector();
	private Vector _local_header = new Vector();
	
	public clinical_document_header() {
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
			if ( __RIM_VERSION != null ) pw.print(" " + "RIM-VERSION" +  "=" + "'" + __RIM_VERSION + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _id != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_id.printObject(pw, visitor);
			}
			if ( _set_id != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_set_id.printObject(pw, visitor);
			}
			if ( _version_nbr != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_version_nbr.printObject(pw, visitor);
			}
			if ( _document_type_cd != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_document_type_cd.printObject(pw, visitor);
			}
			if ( _service_tmr != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_service_tmr.printObject(pw, visitor);
			}
			if ( _origination_dttm != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_origination_dttm.printObject(pw, visitor);
			}
			if ( _copy_dttm != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_copy_dttm.printObject(pw, visitor);
			}
			if (this._confidentiality_cd != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._confidentiality_cd.size(); ++i ) {
					((confidentiality_cd)this._confidentiality_cd.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._document_relationship != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._document_relationship.size(); ++i ) {
					((document_relationship)this._document_relationship.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _fulfills_order != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_fulfills_order.printObject(pw, visitor);
			}
			if ( _patient_encounter != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patient_encounter.printObject(pw, visitor);
			}
			if (this._authenticator != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._authenticator.size(); ++i ) {
					((authenticator)this._authenticator.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _legal_authenticator != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_legal_authenticator.printObject(pw, visitor);
			}
			if (this._intended_recipient != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._intended_recipient.size(); ++i ) {
					((intended_recipient)this._intended_recipient.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._originator != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._originator.size(); ++i ) {
					((originator)this._originator.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _originating_organization != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_originating_organization.printObject(pw, visitor);
			}
			if ( _transcriptionist != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_transcriptionist.printObject(pw, visitor);
			}
			if (this._provider != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._provider.size(); ++i ) {
					((provider)this._provider.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._service_actor != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._service_actor.size(); ++i ) {
					((service_actor)this._service_actor.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _patient != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patient.printObject(pw, visitor);
			}
			if (this._originating_device != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._originating_device.size(); ++i ) {
					((originating_device)this._originating_device.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._service_target != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._service_target.size(); ++i ) {
					((service_target)this._service_target.elementAt(i)).printObject(pw, visitor);
				}
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
		if (qName.equals("clinical_document_header") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			clinical_document_header obj = new clinical_document_header();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((clinical_document_header)builder.getElement()).setNamespace( getNamespace() );
			((clinical_document_header)builder.getElement()).setLocalName( getLocalName() );
			((clinical_document_header)builder.getElement()).setQName( getQName() );
			((clinical_document_header)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((clinical_document_header)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((clinical_document_header)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((clinical_document_header)builder.getElement()).set__T( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("RIM-VERSION") ) {
						set__RIM_VERSION( atts.getValue(i) );
						((clinical_document_header)builder.getElement()).set__RIM_VERSION( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("clinical_document_header") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("levelone")) {
				((levelone)builder.getParent()).set_clinical_document_header((clinical_document_header)builder.getElement());
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
	public void set__RIM_VERSION(String __RIM_VERSION) {
		this.__RIM_VERSION = __RIM_VERSION;
	}
	public String get__RIM_VERSION() {
		return __RIM_VERSION;
	}

	public void set_id(id _id) {
		this._id = _id;
	}
	public id get_id() {
		return _id;
	}
	public void set_set_id(set_id _set_id) {
		this._set_id = _set_id;
	}
	public set_id get_set_id() {
		return _set_id;
	}
	public void set_version_nbr(version_nbr _version_nbr) {
		this._version_nbr = _version_nbr;
	}
	public version_nbr get_version_nbr() {
		return _version_nbr;
	}
	public void set_document_type_cd(document_type_cd _document_type_cd) {
		this._document_type_cd = _document_type_cd;
	}
	public document_type_cd get_document_type_cd() {
		return _document_type_cd;
	}
	public void set_service_tmr(service_tmr _service_tmr) {
		this._service_tmr = _service_tmr;
	}
	public service_tmr get_service_tmr() {
		return _service_tmr;
	}
	public void set_origination_dttm(origination_dttm _origination_dttm) {
		this._origination_dttm = _origination_dttm;
	}
	public origination_dttm get_origination_dttm() {
		return _origination_dttm;
	}
	public void set_copy_dttm(copy_dttm _copy_dttm) {
		this._copy_dttm = _copy_dttm;
	}
	public copy_dttm get_copy_dttm() {
		return _copy_dttm;
	}
	public void set_confidentiality_cd(Vector _confidentiality_cd) {
		if (this._confidentiality_cd != null) this._confidentiality_cd.removeAllElements();
		// copy entire elements in the vector
		this._confidentiality_cd = new Vector();
		for (int i = 0; i < _confidentiality_cd.size(); ++i) {
			this._confidentiality_cd.addElement( _confidentiality_cd.elementAt(i) );
		}
	}
	public Vector get_confidentiality_cd() {
		return _confidentiality_cd;
	}
	public void set_document_relationship(Vector _document_relationship) {
		if (this._document_relationship != null) this._document_relationship.removeAllElements();
		// copy entire elements in the vector
		this._document_relationship = new Vector();
		for (int i = 0; i < _document_relationship.size(); ++i) {
			this._document_relationship.addElement( _document_relationship.elementAt(i) );
		}
	}
	public Vector get_document_relationship() {
		return _document_relationship;
	}
	public void set_fulfills_order(fulfills_order _fulfills_order) {
		this._fulfills_order = _fulfills_order;
	}
	public fulfills_order get_fulfills_order() {
		return _fulfills_order;
	}
	public void set_patient_encounter(patient_encounter _patient_encounter) {
		this._patient_encounter = _patient_encounter;
	}
	public patient_encounter get_patient_encounter() {
		return _patient_encounter;
	}
	public void set_authenticator(Vector _authenticator) {
		if (this._authenticator != null) this._authenticator.removeAllElements();
		// copy entire elements in the vector
		this._authenticator = new Vector();
		for (int i = 0; i < _authenticator.size(); ++i) {
			this._authenticator.addElement( _authenticator.elementAt(i) );
		}
	}
	public Vector get_authenticator() {
		return _authenticator;
	}
	public void set_legal_authenticator(legal_authenticator _legal_authenticator) {
		this._legal_authenticator = _legal_authenticator;
	}
	public legal_authenticator get_legal_authenticator() {
		return _legal_authenticator;
	}
	public void set_intended_recipient(Vector _intended_recipient) {
		if (this._intended_recipient != null) this._intended_recipient.removeAllElements();
		// copy entire elements in the vector
		this._intended_recipient = new Vector();
		for (int i = 0; i < _intended_recipient.size(); ++i) {
			this._intended_recipient.addElement( _intended_recipient.elementAt(i) );
		}
	}
	public Vector get_intended_recipient() {
		return _intended_recipient;
	}
	public void set_originator(Vector _originator) {
		if (this._originator != null) this._originator.removeAllElements();
		// copy entire elements in the vector
		this._originator = new Vector();
		for (int i = 0; i < _originator.size(); ++i) {
			this._originator.addElement( _originator.elementAt(i) );
		}
	}
	public Vector get_originator() {
		return _originator;
	}
	public void set_originating_organization(originating_organization _originating_organization) {
		this._originating_organization = _originating_organization;
	}
	public originating_organization get_originating_organization() {
		return _originating_organization;
	}
	public void set_transcriptionist(transcriptionist _transcriptionist) {
		this._transcriptionist = _transcriptionist;
	}
	public transcriptionist get_transcriptionist() {
		return _transcriptionist;
	}
	public void set_provider(Vector _provider) {
		if (this._provider != null) this._provider.removeAllElements();
		// copy entire elements in the vector
		this._provider = new Vector();
		for (int i = 0; i < _provider.size(); ++i) {
			this._provider.addElement( _provider.elementAt(i) );
		}
	}
	public Vector get_provider() {
		return _provider;
	}
	public void set_service_actor(Vector _service_actor) {
		if (this._service_actor != null) this._service_actor.removeAllElements();
		// copy entire elements in the vector
		this._service_actor = new Vector();
		for (int i = 0; i < _service_actor.size(); ++i) {
			this._service_actor.addElement( _service_actor.elementAt(i) );
		}
	}
	public Vector get_service_actor() {
		return _service_actor;
	}
	public void set_patient(patient _patient) {
		this._patient = _patient;
	}
	public patient get_patient() {
		return _patient;
	}
	public void set_originating_device(Vector _originating_device) {
		if (this._originating_device != null) this._originating_device.removeAllElements();
		// copy entire elements in the vector
		this._originating_device = new Vector();
		for (int i = 0; i < _originating_device.size(); ++i) {
			this._originating_device.addElement( _originating_device.elementAt(i) );
		}
	}
	public Vector get_originating_device() {
		return _originating_device;
	}
	public void set_service_target(Vector _service_target) {
		if (this._service_target != null) this._service_target.removeAllElements();
		// copy entire elements in the vector
		this._service_target = new Vector();
		for (int i = 0; i < _service_target.size(); ++i) {
			this._service_target.addElement( _service_target.elementAt(i) );
		}
	}
	public Vector get_service_target() {
		return _service_target;
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