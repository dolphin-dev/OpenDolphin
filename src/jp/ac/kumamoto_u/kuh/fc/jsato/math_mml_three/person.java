/**
 *
 * person.java
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
public class person extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private Vector _id = new Vector();
	private Vector _person_name = new Vector();
	private Vector _addr = new Vector();
	private Vector _telecom = new Vector();
	private Vector _local_header = new Vector();
	
	public person() {
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
			if (this._id != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._id.size(); ++i ) {
					((id)this._id.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._person_name != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._person_name.size(); ++i ) {
					((person_name)this._person_name.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._addr != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._addr.size(); ++i ) {
					((addr)this._addr.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._telecom != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._telecom.size(); ++i ) {
					((telecom)this._telecom.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("person") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			person obj = new person();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((person)builder.getElement()).setNamespace( getNamespace() );
			((person)builder.getElement()).setLocalName( getLocalName() );
			((person)builder.getElement()).setQName( getQName() );
			((person)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((person)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((person)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((person)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("person") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("transcriptionist")) {
				((transcriptionist)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("service_target")) {
				((service_target)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("service_actor")) {
				((service_actor)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("responsibility")) {
				((responsibility)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("provider")) {
				((provider)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("patient")) {
				((patient)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("originator")) {
				((originator)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("legal_authenticator")) {
				((legal_authenticator)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("intended_recipient")) {
				((intended_recipient)builder.getParent()).set_person((person)builder.getElement());
			}

			if (parentElement.getQName().equals("authenticator")) {
				((authenticator)builder.getParent()).set_person((person)builder.getElement());
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

	public void set_id(Vector _id) {
		if (this._id != null) this._id.removeAllElements();
		// copy entire elements in the vector
		this._id = new Vector();
		for (int i = 0; i < _id.size(); ++i) {
			this._id.addElement( _id.elementAt(i) );
		}
	}
	public Vector get_id() {
		return _id;
	}
	public void set_person_name(Vector _person_name) {
		if (this._person_name != null) this._person_name.removeAllElements();
		// copy entire elements in the vector
		this._person_name = new Vector();
		for (int i = 0; i < _person_name.size(); ++i) {
			this._person_name.addElement( _person_name.elementAt(i) );
		}
	}
	public Vector get_person_name() {
		return _person_name;
	}
	public void set_addr(Vector _addr) {
		if (this._addr != null) this._addr.removeAllElements();
		// copy entire elements in the vector
		this._addr = new Vector();
		for (int i = 0; i < _addr.size(); ++i) {
			this._addr.addElement( _addr.elementAt(i) );
		}
	}
	public Vector get_addr() {
		return _addr;
	}
	public void set_telecom(Vector _telecom) {
		if (this._telecom != null) this._telecom.removeAllElements();
		// copy entire elements in the vector
		this._telecom = new Vector();
		for (int i = 0; i < _telecom.size(); ++i) {
			this._telecom.addElement( _telecom.elementAt(i) );
		}
	}
	public Vector get_telecom() {
		return _telecom;
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