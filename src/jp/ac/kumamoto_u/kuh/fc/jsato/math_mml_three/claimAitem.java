/**
 *
 * claimAitem.java
 * Created on 2003/1/4 2:30:28
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
public class claimAitem extends MMLObject {
	
	/* fields */
	private String __claimAsubclassCode = null;
	private String __claimAsubclassCodeId = null;
	private String __claimAcode = null;
	private String __claimAtableId = null;
	private String __claimAaliasCode = null;
	private String __claimAaliasTableId = null;

	private claimAname _name = null;
	private Vector _number = new Vector();
	private claimAclaimPoint _claimPoint = null;
	private claimAclaimRate _claimRate = null;
	private claimAduration _duration = null;
	private Vector _location = new Vector();
	private Vector _film = new Vector();
	private claimAevent _event = null;
	private claimAmemo _memo = null;
	
	public claimAitem() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAsubclassCode != null ) pw.print(" " + "claimA:subclassCode" +  "=" + "'" + __claimAsubclassCode + "'");
			if ( __claimAsubclassCodeId != null ) pw.print(" " + "claimA:subclassCodeId" +  "=" + "'" + __claimAsubclassCodeId + "'");
			if ( __claimAcode != null ) pw.print(" " + "claimA:code" +  "=" + "'" + __claimAcode + "'");
			if ( __claimAtableId != null ) pw.print(" " + "claimA:tableId" +  "=" + "'" + __claimAtableId + "'");
			if ( __claimAaliasCode != null ) pw.print(" " + "claimA:aliasCode" +  "=" + "'" + __claimAaliasCode + "'");
			if ( __claimAaliasTableId != null ) pw.print(" " + "claimA:aliasTableId" +  "=" + "'" + __claimAaliasTableId + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _name != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_name.printObject(pw, visitor);
			}
			if (this._number != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._number.size(); ++i ) {
					((claimAnumber)this._number.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _claimPoint != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_claimPoint.printObject(pw, visitor);
			}
			if ( _claimRate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_claimRate.printObject(pw, visitor);
			}
			if ( _duration != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_duration.printObject(pw, visitor);
			}
			if (this._location != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._location.size(); ++i ) {
					((claimAlocation)this._location.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._film != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._film.size(); ++i ) {
					((claimAfilm)this._film.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _event != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_event.printObject(pw, visitor);
			}
			if ( _memo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_memo.printObject(pw, visitor);
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
		if (qName.equals("claimA:item") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAitem obj = new claimAitem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAitem)builder.getElement()).setNamespace( getNamespace() );
			((claimAitem)builder.getElement()).setLocalName( getLocalName() );
			((claimAitem)builder.getElement()).setQName( getQName() );
			((claimAitem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claimA:subclassCode") ) {
						set__claimAsubclassCode( atts.getValue(i) );
						((claimAitem)builder.getElement()).set__claimAsubclassCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:subclassCodeId") ) {
						set__claimAsubclassCodeId( atts.getValue(i) );
						((claimAitem)builder.getElement()).set__claimAsubclassCodeId( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:code") ) {
						set__claimAcode( atts.getValue(i) );
						((claimAitem)builder.getElement()).set__claimAcode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:tableId") ) {
						set__claimAtableId( atts.getValue(i) );
						((claimAitem)builder.getElement()).set__claimAtableId( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:aliasCode") ) {
						set__claimAaliasCode( atts.getValue(i) );
						((claimAitem)builder.getElement()).set__claimAaliasCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:aliasTableId") ) {
						set__claimAaliasTableId( atts.getValue(i) );
						((claimAitem)builder.getElement()).set__claimAaliasTableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:item") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:bundle")) {
				Vector v = ((claimAbundle)builder.getParent()).get_item();
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
	public void set__claimAsubclassCode(String __claimAsubclassCode) {
		this.__claimAsubclassCode = __claimAsubclassCode;
	}
	public String get__claimAsubclassCode() {
		return __claimAsubclassCode;
	}
	public void set__claimAsubclassCodeId(String __claimAsubclassCodeId) {
		this.__claimAsubclassCodeId = __claimAsubclassCodeId;
	}
	public String get__claimAsubclassCodeId() {
		return __claimAsubclassCodeId;
	}
	public void set__claimAcode(String __claimAcode) {
		this.__claimAcode = __claimAcode;
	}
	public String get__claimAcode() {
		return __claimAcode;
	}
	public void set__claimAtableId(String __claimAtableId) {
		this.__claimAtableId = __claimAtableId;
	}
	public String get__claimAtableId() {
		return __claimAtableId;
	}
	public void set__claimAaliasCode(String __claimAaliasCode) {
		this.__claimAaliasCode = __claimAaliasCode;
	}
	public String get__claimAaliasCode() {
		return __claimAaliasCode;
	}
	public void set__claimAaliasTableId(String __claimAaliasTableId) {
		this.__claimAaliasTableId = __claimAaliasTableId;
	}
	public String get__claimAaliasTableId() {
		return __claimAaliasTableId;
	}

	public void set_name(claimAname _name) {
		this._name = _name;
	}
	public claimAname get_name() {
		return _name;
	}
	public void set_number(Vector _number) {
		if (this._number != null) this._number.removeAllElements();
		// copy entire elements in the vector
		this._number = new Vector();
		for (int i = 0; i < _number.size(); ++i) {
			this._number.addElement( _number.elementAt(i) );
		}
	}
	public Vector get_number() {
		return _number;
	}
	public void set_claimPoint(claimAclaimPoint _claimPoint) {
		this._claimPoint = _claimPoint;
	}
	public claimAclaimPoint get_claimPoint() {
		return _claimPoint;
	}
	public void set_claimRate(claimAclaimRate _claimRate) {
		this._claimRate = _claimRate;
	}
	public claimAclaimRate get_claimRate() {
		return _claimRate;
	}
	public void set_duration(claimAduration _duration) {
		this._duration = _duration;
	}
	public claimAduration get_duration() {
		return _duration;
	}
	public void set_location(Vector _location) {
		if (this._location != null) this._location.removeAllElements();
		// copy entire elements in the vector
		this._location = new Vector();
		for (int i = 0; i < _location.size(); ++i) {
			this._location.addElement( _location.elementAt(i) );
		}
	}
	public Vector get_location() {
		return _location;
	}
	public void set_film(Vector _film) {
		if (this._film != null) this._film.removeAllElements();
		// copy entire elements in the vector
		this._film = new Vector();
		for (int i = 0; i < _film.size(); ++i) {
			this._film.addElement( _film.elementAt(i) );
		}
	}
	public Vector get_film() {
		return _film;
	}
	public void set_event(claimAevent _event) {
		this._event = _event;
	}
	public claimAevent get_event() {
		return _event;
	}
	public void set_memo(claimAmemo _memo) {
		this._memo = _memo;
	}
	public claimAmemo get_memo() {
		return _memo;
	}
	
}