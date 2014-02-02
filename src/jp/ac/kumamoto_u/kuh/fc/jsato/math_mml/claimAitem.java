/**
 *
 * claimAitem.java
 * Created on 2002/7/30 10:0:40
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

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

			if ( this.getLocalName().equals("Mml") ) {
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
				setClaimAsubclassCode( atts.getValue(namespaceURI, "subclassCode") );
				((claimAitem)builder.getElement()).setClaimAsubclassCode( atts.getValue(namespaceURI, "subclassCode") );
				setClaimAsubclassCodeId( atts.getValue(namespaceURI, "subclassCodeId") );
				((claimAitem)builder.getElement()).setClaimAsubclassCodeId( atts.getValue(namespaceURI, "subclassCodeId") );
				setClaimAcode( atts.getValue(namespaceURI, "code") );
				((claimAitem)builder.getElement()).setClaimAcode( atts.getValue(namespaceURI, "code") );
				setClaimAtableId( atts.getValue(namespaceURI, "tableId") );
				((claimAitem)builder.getElement()).setClaimAtableId( atts.getValue(namespaceURI, "tableId") );
				setClaimAaliasCode( atts.getValue(namespaceURI, "aliasCode") );
				((claimAitem)builder.getElement()).setClaimAaliasCode( atts.getValue(namespaceURI, "aliasCode") );
				setClaimAaliasTableId( atts.getValue(namespaceURI, "aliasTableId") );
				((claimAitem)builder.getElement()).setClaimAaliasTableId( atts.getValue(namespaceURI, "aliasTableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:item") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:bundle")) {
				Vector v = ((claimAbundle)builder.getParent()).getItem();
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
	public void setClaimAsubclassCode(String __claimAsubclassCode) {
		this.__claimAsubclassCode = __claimAsubclassCode;
	}
	public String getClaimAsubclassCode() {
		return __claimAsubclassCode;
	}
	public void setClaimAsubclassCodeId(String __claimAsubclassCodeId) {
		this.__claimAsubclassCodeId = __claimAsubclassCodeId;
	}
	public String getClaimAsubclassCodeId() {
		return __claimAsubclassCodeId;
	}
	public void setClaimAcode(String __claimAcode) {
		this.__claimAcode = __claimAcode;
	}
	public String getClaimAcode() {
		return __claimAcode;
	}
	public void setClaimAtableId(String __claimAtableId) {
		this.__claimAtableId = __claimAtableId;
	}
	public String getClaimAtableId() {
		return __claimAtableId;
	}
	public void setClaimAaliasCode(String __claimAaliasCode) {
		this.__claimAaliasCode = __claimAaliasCode;
	}
	public String getClaimAaliasCode() {
		return __claimAaliasCode;
	}
	public void setClaimAaliasTableId(String __claimAaliasTableId) {
		this.__claimAaliasTableId = __claimAaliasTableId;
	}
	public String getClaimAaliasTableId() {
		return __claimAaliasTableId;
	}

	public void setName(claimAname _name) {
		this._name = _name;
	}
	public claimAname getName() {
		return _name;
	}
	public void setNumber(Vector _number) {
		if (this._number != null) this._number.removeAllElements();
		// copy entire elements in the vector
		this._number = new Vector();
		for (int i = 0; i < _number.size(); ++i) {
			this._number.addElement( _number.elementAt(i) );
		}
	}
	public Vector getNumber() {
		return _number;
	}
	public void setClaimPoint(claimAclaimPoint _claimPoint) {
		this._claimPoint = _claimPoint;
	}
	public claimAclaimPoint getClaimPoint() {
		return _claimPoint;
	}
	public void setClaimRate(claimAclaimRate _claimRate) {
		this._claimRate = _claimRate;
	}
	public claimAclaimRate getClaimRate() {
		return _claimRate;
	}
	public void setDuration(claimAduration _duration) {
		this._duration = _duration;
	}
	public claimAduration getDuration() {
		return _duration;
	}
	public void setLocation(Vector _location) {
		if (this._location != null) this._location.removeAllElements();
		// copy entire elements in the vector
		this._location = new Vector();
		for (int i = 0; i < _location.size(); ++i) {
			this._location.addElement( _location.elementAt(i) );
		}
	}
	public Vector getLocation() {
		return _location;
	}
	public void setFilm(Vector _film) {
		if (this._film != null) this._film.removeAllElements();
		// copy entire elements in the vector
		this._film = new Vector();
		for (int i = 0; i < _film.size(); ++i) {
			this._film.addElement( _film.elementAt(i) );
		}
	}
	public Vector getFilm() {
		return _film;
	}
	public void setEvent(claimAevent _event) {
		this._event = _event;
	}
	public claimAevent getEvent() {
		return _event;
	}
	public void setMemo(claimAmemo _memo) {
		this._memo = _memo;
	}
	public claimAmemo getMemo() {
		return _memo;
	}
	
}