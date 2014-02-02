/**
 *
 * claimitem.java
 * Created on 2002/7/30 10:0:39
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import java.awt.*;
import java.util.*;
import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class claimitem extends MMLObject {
	
	/* fields */
	private String __claimsubclassCode = null;
	private String __claimsubclassCodeId = null;
	private String __claimcode = null;
	private String __claimtableId = null;
	private String __claimaliasCode = null;
	private String __claimaliasTableId = null;

	private claimname _name = null;
	private Vector _number = new Vector();
	private claimduration _duration = null;
	private Vector _location = new Vector();
	private Vector _film = new Vector();
	private claimevent _event = null;
	private claimmemo _memo = null;
	
	public claimitem() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimsubclassCode != null ) pw.print(" " + "claim:subclassCode" +  "=" + "'" + __claimsubclassCode + "'");
			if ( __claimsubclassCodeId != null ) pw.print(" " + "claim:subclassCodeId" +  "=" + "'" + __claimsubclassCodeId + "'");
			if ( __claimcode != null ) pw.print(" " + "claim:code" +  "=" + "'" + __claimcode + "'");
			if ( __claimtableId != null ) pw.print(" " + "claim:tableId" +  "=" + "'" + __claimtableId + "'");
			if ( __claimaliasCode != null ) pw.print(" " + "claim:aliasCode" +  "=" + "'" + __claimaliasCode + "'");
			if ( __claimaliasTableId != null ) pw.print(" " + "claim:aliasTableId" +  "=" + "'" + __claimaliasTableId + "'");

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
					((claimnumber)this._number.elementAt(i)).printObject(pw, visitor);
				}
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
					((claimlocation)this._location.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._film != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._film.size(); ++i ) {
					((claimfilm)this._film.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("claim:item") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimitem obj = new claimitem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimitem)builder.getElement()).setNamespace( getNamespace() );
			((claimitem)builder.getElement()).setLocalName( getLocalName() );
			((claimitem)builder.getElement()).setQName( getQName() );
			((claimitem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimsubclassCode( atts.getValue(namespaceURI, "subclassCode") );
				((claimitem)builder.getElement()).setClaimsubclassCode( atts.getValue(namespaceURI, "subclassCode") );
				setClaimsubclassCodeId( atts.getValue(namespaceURI, "subclassCodeId") );
				((claimitem)builder.getElement()).setClaimsubclassCodeId( atts.getValue(namespaceURI, "subclassCodeId") );
				setClaimcode( atts.getValue(namespaceURI, "code") );
				((claimitem)builder.getElement()).setClaimcode( atts.getValue(namespaceURI, "code") );
				setClaimtableId( atts.getValue(namespaceURI, "tableId") );
				((claimitem)builder.getElement()).setClaimtableId( atts.getValue(namespaceURI, "tableId") );
				setClaimaliasCode( atts.getValue(namespaceURI, "aliasCode") );
				((claimitem)builder.getElement()).setClaimaliasCode( atts.getValue(namespaceURI, "aliasCode") );
				setClaimaliasTableId( atts.getValue(namespaceURI, "aliasTableId") );
				((claimitem)builder.getElement()).setClaimaliasTableId( atts.getValue(namespaceURI, "aliasTableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:item") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:bundle")) {
				Vector v = ((claimbundle)builder.getParent()).getItem();
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
	public void setClaimsubclassCode(String __claimsubclassCode) {
		this.__claimsubclassCode = __claimsubclassCode;
	}
	public String getClaimsubclassCode() {
		return __claimsubclassCode;
	}
	public void setClaimsubclassCodeId(String __claimsubclassCodeId) {
		this.__claimsubclassCodeId = __claimsubclassCodeId;
	}
	public String getClaimsubclassCodeId() {
		return __claimsubclassCodeId;
	}
	public void setClaimcode(String __claimcode) {
		this.__claimcode = __claimcode;
	}
	public String getClaimcode() {
		return __claimcode;
	}
	public void setClaimtableId(String __claimtableId) {
		this.__claimtableId = __claimtableId;
	}
	public String getClaimtableId() {
		return __claimtableId;
	}
	public void setClaimaliasCode(String __claimaliasCode) {
		this.__claimaliasCode = __claimaliasCode;
	}
	public String getClaimaliasCode() {
		return __claimaliasCode;
	}
	public void setClaimaliasTableId(String __claimaliasTableId) {
		this.__claimaliasTableId = __claimaliasTableId;
	}
	public String getClaimaliasTableId() {
		return __claimaliasTableId;
	}

	public void setName(claimname _name) {
		this._name = _name;
	}
	public claimname getName() {
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
	public void setDuration(claimduration _duration) {
		this._duration = _duration;
	}
	public claimduration getDuration() {
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
	public void setEvent(claimevent _event) {
		this._event = _event;
	}
	public claimevent getEvent() {
		return _event;
	}
	public void setMemo(claimmemo _memo) {
		this._memo = _memo;
	}
	public claimmemo getMemo() {
		return _memo;
	}
	
}