/**
 *
 * related_document.java
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
public class related_document extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private id _id = null;
	private set_id _set_id = null;
	private version_nbr _version_nbr = null;
	private Vector _local_header = new Vector();
	
	public related_document() {
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
		if (qName.equals("related_document") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			related_document obj = new related_document();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((related_document)builder.getElement()).setNamespace( getNamespace() );
			((related_document)builder.getElement()).setLocalName( getLocalName() );
			((related_document)builder.getElement()).setQName( getQName() );
			((related_document)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((related_document)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((related_document)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((related_document)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("related_document") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("document_relationship")) {
				((document_relationship)builder.getParent()).set_related_document((related_document)builder.getElement());
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