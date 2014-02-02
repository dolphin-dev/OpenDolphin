/**
 *
 * mmlPhPhone.java
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
public class mmlPhPhone extends MMLObject {
	
	/* fields */
	private String __mmlPhtelEquipType = null;

	private mmlPharea _area = null;
	private mmlPhcity _city = null;
	private mmlPhnumber _number = null;
	private mmlPhextension _extension = null;
	private mmlPhcountry _country = null;
	private mmlPhmemo _memo = null;
	
	public mmlPhPhone() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlPhtelEquipType != null ) pw.print(" " + "mmlPh:telEquipType" +  "=" + "'" + __mmlPhtelEquipType + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _area != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_area.printObject(pw, visitor);
			}
			if ( _city != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_city.printObject(pw, visitor);
			}
			if ( _number != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_number.printObject(pw, visitor);
			}
			if ( _extension != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_extension.printObject(pw, visitor);
			}
			if ( _country != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_country.printObject(pw, visitor);
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
		if (qName.equals("mmlPh:Phone") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPhPhone obj = new mmlPhPhone();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPhPhone)builder.getElement()).setNamespace( getNamespace() );
			((mmlPhPhone)builder.getElement()).setLocalName( getLocalName() );
			((mmlPhPhone)builder.getElement()).setQName( getQName() );
			((mmlPhPhone)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlPhtelEquipType( atts.getValue(namespaceURI, "telEquipType") );
				((mmlPhPhone)builder.getElement()).setMmlPhtelEquipType( atts.getValue(namespaceURI, "telEquipType") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPh:Phone") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:phones")) {
				Vector v = ((mmlPsiphones)builder.getParent()).getPhone();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:phones")) {
				Vector v = ((mmlPiphones)builder.getParent()).getPhone();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:phones")) {
				Vector v = ((mmlHiphones)builder.getParent()).getPhone();
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
	public void setMmlPhtelEquipType(String __mmlPhtelEquipType) {
		this.__mmlPhtelEquipType = __mmlPhtelEquipType;
	}
	public String getMmlPhtelEquipType() {
		return __mmlPhtelEquipType;
	}

	public void setArea(mmlPharea _area) {
		this._area = _area;
	}
	public mmlPharea getArea() {
		return _area;
	}
	public void setCity(mmlPhcity _city) {
		this._city = _city;
	}
	public mmlPhcity getCity() {
		return _city;
	}
	public void setNumber(mmlPhnumber _number) {
		this._number = _number;
	}
	public mmlPhnumber getNumber() {
		return _number;
	}
	public void setExtension(mmlPhextension _extension) {
		this._extension = _extension;
	}
	public mmlPhextension getExtension() {
		return _extension;
	}
	public void setCountry(mmlPhcountry _country) {
		this._country = _country;
	}
	public mmlPhcountry getCountry() {
		return _country;
	}
	public void setMemo(mmlPhmemo _memo) {
		this._memo = _memo;
	}
	public mmlPhmemo getMemo() {
		return _memo;
	}
	
}