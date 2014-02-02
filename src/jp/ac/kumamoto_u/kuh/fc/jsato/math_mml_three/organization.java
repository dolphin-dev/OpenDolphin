/**
 *
 * organization.java
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
public class organization extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private Vector _id = new Vector();
	private Vector _organization__nm = new Vector();
	private Vector _addr = new Vector();
	private Vector _local_header = new Vector();
	
	public organization() {
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
			if (this._organization__nm != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._organization__nm.size(); ++i ) {
					((organization__nm)this._organization__nm.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("organization") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			organization obj = new organization();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((organization)builder.getElement()).setNamespace( getNamespace() );
			((organization)builder.getElement()).setLocalName( getLocalName() );
			((organization)builder.getElement()).setQName( getQName() );
			((organization)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((organization)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((organization)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((organization)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("organization") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("service_actor")) {
				((service_actor)builder.getParent()).set_organization((organization)builder.getElement());
			}

			if (parentElement.getQName().equals("originating_organization")) {
				((originating_organization)builder.getParent()).set_organization((organization)builder.getElement());
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
	public void set_organization__nm(Vector _organization__nm) {
		if (this._organization__nm != null) this._organization__nm.removeAllElements();
		// copy entire elements in the vector
		this._organization__nm = new Vector();
		for (int i = 0; i < _organization__nm.size(); ++i) {
			this._organization__nm.addElement( _organization__nm.elementAt(i) );
		}
	}
	public Vector get_organization__nm() {
		return _organization__nm;
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