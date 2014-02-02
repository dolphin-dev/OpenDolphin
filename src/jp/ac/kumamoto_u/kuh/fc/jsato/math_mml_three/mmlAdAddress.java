/**
 *
 * mmlAdAddress.java
 * Created on 2003/1/4 2:29:53
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlAd:repCode") ) {
						set__mmlAdrepCode( atts.getValue(i) );
						((mmlAdAddress)builder.getElement()).set__mmlAdrepCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlAd:addressClass") ) {
						set__mmlAdaddressClass( atts.getValue(i) );
						((mmlAdAddress)builder.getElement()).set__mmlAdaddressClass( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlAd:tableId") ) {
						set__mmlAdtableId( atts.getValue(i) );
						((mmlAdAddress)builder.getElement()).set__mmlAdtableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlAd:Address") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:addresses")) {
				Vector v = ((mmlPsiaddresses)builder.getParent()).get_Address();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:addresses")) {
				Vector v = ((mmlPiaddresses)builder.getParent()).get_Address();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:addresses")) {
				Vector v = ((mmlHiaddresses)builder.getParent()).get_Address();
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
	public void set__mmlAdrepCode(String __mmlAdrepCode) {
		this.__mmlAdrepCode = __mmlAdrepCode;
	}
	public String get__mmlAdrepCode() {
		return __mmlAdrepCode;
	}
	public void set__mmlAdaddressClass(String __mmlAdaddressClass) {
		this.__mmlAdaddressClass = __mmlAdaddressClass;
	}
	public String get__mmlAdaddressClass() {
		return __mmlAdaddressClass;
	}
	public void set__mmlAdtableId(String __mmlAdtableId) {
		this.__mmlAdtableId = __mmlAdtableId;
	}
	public String get__mmlAdtableId() {
		return __mmlAdtableId;
	}

	public void set_full(mmlAdfull _full) {
		this._full = _full;
	}
	public mmlAdfull get_full() {
		return _full;
	}
	public void set_prefecture(mmlAdprefecture _prefecture) {
		this._prefecture = _prefecture;
	}
	public mmlAdprefecture get_prefecture() {
		return _prefecture;
	}
	public void set_city(mmlAdcity _city) {
		this._city = _city;
	}
	public mmlAdcity get_city() {
		return _city;
	}
	public void set_town(mmlAdtown _town) {
		this._town = _town;
	}
	public mmlAdtown get_town() {
		return _town;
	}
	public void set_homeNumber(mmlAdhomeNumber _homeNumber) {
		this._homeNumber = _homeNumber;
	}
	public mmlAdhomeNumber get_homeNumber() {
		return _homeNumber;
	}
	public void set_zip(mmlAdzip _zip) {
		this._zip = _zip;
	}
	public mmlAdzip get_zip() {
		return _zip;
	}
	public void set_countryCode(mmlAdcountryCode _countryCode) {
		this._countryCode = _countryCode;
	}
	public mmlAdcountryCode get_countryCode() {
		return _countryCode;
	}
	
}