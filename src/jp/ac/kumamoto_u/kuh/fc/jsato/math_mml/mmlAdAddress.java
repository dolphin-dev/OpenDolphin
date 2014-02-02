/**
 *
 * mmlAdAddress.java
 * Created on 2002/7/30 10:0:24
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
public class mmlAdAddress extends MMLObject {
	
	/* fields */
	private String __mmlAdrepCode = null;
	private String __mmlAdaddressClass = null;
	private String __mmlAdtableId = null;

	private mmlAdfull _full = null;
	private mmlAdprefecture _prefecture = null;
	private mmlAdcity _city = null;
	private mmlAdtown _town = null;
	private mmlAdhomeNumber _homeNumber = null;
	private mmlAdzip _zip = null;
	private mmlAdcountryCode _countryCode = null;
	
	public mmlAdAddress() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlAdrepCode != null ) pw.print(" " + "mmlAd:repCode" +  "=" + "'" + __mmlAdrepCode + "'");
			if ( __mmlAdaddressClass != null ) pw.print(" " + "mmlAd:addressClass" +  "=" + "'" + __mmlAdaddressClass + "'");
			if ( __mmlAdtableId != null ) pw.print(" " + "mmlAd:tableId" +  "=" + "'" + __mmlAdtableId + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _full != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_full.printObject(pw, visitor);
			}
			if ( _prefecture != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_prefecture.printObject(pw, visitor);
			}
			if ( _city != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_city.printObject(pw, visitor);
			}
			if ( _town != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_town.printObject(pw, visitor);
			}
			if ( _homeNumber != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_homeNumber.printObject(pw, visitor);
			}
			if ( _zip != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_zip.printObject(pw, visitor);
			}
			if ( _countryCode != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_countryCode.printObject(pw, visitor);
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
		if (qName.equals("mmlAd:Address") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlAdAddress obj = new mmlAdAddress();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlAdAddress)builder.getElement()).setNamespace( getNamespace() );
			((mmlAdAddress)builder.getElement()).setLocalName( getLocalName() );
			((mmlAdAddress)builder.getElement()).setQName( getQName() );
			((mmlAdAddress)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlAdrepCode( atts.getValue(namespaceURI, "repCode") );
				((mmlAdAddress)builder.getElement()).setMmlAdrepCode( atts.getValue(namespaceURI, "repCode") );
				setMmlAdaddressClass( atts.getValue(namespaceURI, "addressClass") );
				((mmlAdAddress)builder.getElement()).setMmlAdaddressClass( atts.getValue(namespaceURI, "addressClass") );
				setMmlAdtableId( atts.getValue(namespaceURI, "tableId") );
				((mmlAdAddress)builder.getElement()).setMmlAdtableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlAd:Address") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:addresses")) {
				Vector v = ((mmlPsiaddresses)builder.getParent()).getAddress();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:addresses")) {
				Vector v = ((mmlPiaddresses)builder.getParent()).getAddress();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:addresses")) {
				Vector v = ((mmlHiaddresses)builder.getParent()).getAddress();
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
	public void setMmlAdrepCode(String __mmlAdrepCode) {
		this.__mmlAdrepCode = __mmlAdrepCode;
	}
	public String getMmlAdrepCode() {
		return __mmlAdrepCode;
	}
	public void setMmlAdaddressClass(String __mmlAdaddressClass) {
		this.__mmlAdaddressClass = __mmlAdaddressClass;
	}
	public String getMmlAdaddressClass() {
		return __mmlAdaddressClass;
	}
	public void setMmlAdtableId(String __mmlAdtableId) {
		this.__mmlAdtableId = __mmlAdtableId;
	}
	public String getMmlAdtableId() {
		return __mmlAdtableId;
	}

	public void setFull(mmlAdfull _full) {
		this._full = _full;
	}
	public mmlAdfull getFull() {
		return _full;
	}
	public void setPrefecture(mmlAdprefecture _prefecture) {
		this._prefecture = _prefecture;
	}
	public mmlAdprefecture getPrefecture() {
		return _prefecture;
	}
	public void setCity(mmlAdcity _city) {
		this._city = _city;
	}
	public mmlAdcity getCity() {
		return _city;
	}
	public void setTown(mmlAdtown _town) {
		this._town = _town;
	}
	public mmlAdtown getTown() {
		return _town;
	}
	public void setHomeNumber(mmlAdhomeNumber _homeNumber) {
		this._homeNumber = _homeNumber;
	}
	public mmlAdhomeNumber getHomeNumber() {
		return _homeNumber;
	}
	public void setZip(mmlAdzip _zip) {
		this._zip = _zip;
	}
	public mmlAdzip getZip() {
		return _zip;
	}
	public void setCountryCode(mmlAdcountryCode _countryCode) {
		this._countryCode = _countryCode;
	}
	public mmlAdcountryCode getCountryCode() {
		return _countryCode;
	}
	
}