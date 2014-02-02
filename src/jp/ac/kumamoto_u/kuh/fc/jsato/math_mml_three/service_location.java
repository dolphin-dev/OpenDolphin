/**
 *
 * service_location.java
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
public class service_location extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private id _id = null;
	private addr _addr = null;
	private Vector _local_header = new Vector();
	
	public service_location() {
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
			if ( _id != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_id.printObject(pw, visitor);
			}
			if ( _addr != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_addr.printObject(pw, visitor);
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
		if (qName.equals("service_location") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			service_location obj = new service_location();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((service_location)builder.getElement()).setNamespace( getNamespace() );
			((service_location)builder.getElement()).setLocalName( getLocalName() );
			((service_location)builder.getElement()).setQName( getQName() );
			((service_location)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((service_location)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((service_location)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((service_location)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("service_location") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("patient_encounter")) {
				((patient_encounter)builder.getParent()).set_service_location((service_location)builder.getElement());
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

	public void set_id(id _id) {
		this._id = _id;
	}
	public id get_id() {
		return _id;
	}
	public void set_addr(addr _addr) {
		this._addr = _addr;
	}
	public addr get_addr() {
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