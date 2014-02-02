/**
 *
 * service_actor.java
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
public class service_actor extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private service_actor__type_cd _service_actor__type_cd = null;
	private participation_tmr _participation_tmr = null;
	private signature_cd _signature_cd = null;
	private person _person = null;
	private organization _organization = null;
	private Vector _local_header = new Vector();
	
	public service_actor() {
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
			if ( _service_actor__type_cd != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_service_actor__type_cd.printObject(pw, visitor);
			}
			if ( _participation_tmr != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_participation_tmr.printObject(pw, visitor);
			}
			if ( _signature_cd != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_signature_cd.printObject(pw, visitor);
			}
			if ( _person != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_person.printObject(pw, visitor);
			}
			if ( _organization != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_organization.printObject(pw, visitor);
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
		if (qName.equals("service_actor") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			service_actor obj = new service_actor();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((service_actor)builder.getElement()).setNamespace( getNamespace() );
			((service_actor)builder.getElement()).setLocalName( getLocalName() );
			((service_actor)builder.getElement()).setQName( getQName() );
			((service_actor)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((service_actor)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((service_actor)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((service_actor)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("service_actor") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("clinical_document_header")) {
				Vector v = ((clinical_document_header)builder.getParent()).get_service_actor();
				v.addElement(builder.getElement());
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

	public void set_service_actor__type_cd(service_actor__type_cd _service_actor__type_cd) {
		this._service_actor__type_cd = _service_actor__type_cd;
	}
	public service_actor__type_cd get_service_actor__type_cd() {
		return _service_actor__type_cd;
	}
	public void set_participation_tmr(participation_tmr _participation_tmr) {
		this._participation_tmr = _participation_tmr;
	}
	public participation_tmr get_participation_tmr() {
		return _participation_tmr;
	}
	public void set_signature_cd(signature_cd _signature_cd) {
		this._signature_cd = _signature_cd;
	}
	public signature_cd get_signature_cd() {
		return _signature_cd;
	}
	public void set_person(person _person) {
		this._person = _person;
	}
	public person get_person() {
		return _person;
	}
	public void set_organization(organization _organization) {
		this._organization = _organization;
	}
	public organization get_organization() {
		return _organization;
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