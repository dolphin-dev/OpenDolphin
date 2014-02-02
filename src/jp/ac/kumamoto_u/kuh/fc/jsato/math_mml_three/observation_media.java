/**
 *
 * observation_media.java
 * Created on 2003/1/4 2:30:31
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
public class observation_media extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;
	private String __HL7_NAME = null;
	private String __T = null;

	private observation_media__id _observation_media__id = null;
	private observation_media__value _observation_media__value = null;
	private Vector _local_markup = new Vector();
	
	public observation_media() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __originator != null ) pw.print(" " + "originator" +  "=" + "'" + __originator + "'");
			if ( __confidentiality != null ) pw.print(" " + "confidentiality" +  "=" + "'" + __confidentiality + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");
			if ( __HL7_NAME != null ) pw.print(" " + "HL7-NAME" +  "=" + "'" + __HL7_NAME + "'");
			if ( __T != null ) pw.print(" " + "T" +  "=" + "'" + __T + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _observation_media__id != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_observation_media__id.printObject(pw, visitor);
			}
			if ( _observation_media__value != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_observation_media__value.printObject(pw, visitor);
			}
			if (this._local_markup != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._local_markup.size(); ++i ) {
					((local_markup)this._local_markup.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("observation_media") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			observation_media obj = new observation_media();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((observation_media)builder.getElement()).setNamespace( getNamespace() );
			((observation_media)builder.getElement()).setLocalName( getLocalName() );
			((observation_media)builder.getElement()).setQName( getQName() );
			((observation_media)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((observation_media)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((observation_media)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((observation_media)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((observation_media)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("HL7-NAME") ) {
						set__HL7_NAME( atts.getValue(i) );
						((observation_media)builder.getElement()).set__HL7_NAME( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("T") ) {
						set__T( atts.getValue(i) );
						((observation_media)builder.getElement()).set__T( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("observation_media") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("td")) {
				Vector v = ((td)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (observation_media)builder.getElement() );
			}

			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (observation_media)builder.getElement() );
			}

			if (parentElement.getQName().equals("content")) {
				Vector v = ((content)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (observation_media)builder.getElement() );
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
	public void set__originator(String __originator) {
		this.__originator = __originator;
	}
	public String get__originator() {
		return __originator;
	}
	public void set__confidentiality(String __confidentiality) {
		this.__confidentiality = __confidentiality;
	}
	public String get__confidentiality() {
		return __confidentiality;
	}
	public void set__xmllang(String __xmllang) {
		this.__xmllang = __xmllang;
	}
	public String get__xmllang() {
		return __xmllang;
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

	public void set_observation_media__id(observation_media__id _observation_media__id) {
		this._observation_media__id = _observation_media__id;
	}
	public observation_media__id get_observation_media__id() {
		return _observation_media__id;
	}
	public void set_observation_media__value(observation_media__value _observation_media__value) {
		this._observation_media__value = _observation_media__value;
	}
	public observation_media__value get_observation_media__value() {
		return _observation_media__value;
	}
	public void set_local_markup(Vector _local_markup) {
		if (this._local_markup != null) this._local_markup.removeAllElements();
		// copy entire elements in the vector
		this._local_markup = new Vector();
		for (int i = 0; i < _local_markup.size(); ++i) {
			this._local_markup.addElement( _local_markup.elementAt(i) );
		}
	}
	public Vector get_local_markup() {
		return _local_markup;
	}
	
}