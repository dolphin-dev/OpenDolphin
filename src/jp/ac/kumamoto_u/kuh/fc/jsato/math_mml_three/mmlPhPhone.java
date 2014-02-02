/**
 *
 * mmlPhPhone.java
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
public class mmlPhPhone extends MMLObject {
	
	/* fields */
	private String __mmlPhtelEquipType = null;

	private mmlPhfull _full = null;
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlPh:telEquipType") ) {
						set__mmlPhtelEquipType( atts.getValue(i) );
						((mmlPhPhone)builder.getElement()).set__mmlPhtelEquipType( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPh:Phone") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:phones")) {
				Vector v = ((mmlPsiphones)builder.getParent()).get_Phone();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:phones")) {
				Vector v = ((mmlPiphones)builder.getParent()).get_Phone();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:phones")) {
				Vector v = ((mmlHiphones)builder.getParent()).get_Phone();
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
	public void set__mmlPhtelEquipType(String __mmlPhtelEquipType) {
		this.__mmlPhtelEquipType = __mmlPhtelEquipType;
	}
	public String get__mmlPhtelEquipType() {
		return __mmlPhtelEquipType;
	}

	public void set_full(mmlPhfull _full) {
		this._full = _full;
	}
	public mmlPhfull get_full() {
		return _full;
	}
	public void set_area(mmlPharea _area) {
		this._area = _area;
	}
	public mmlPharea get_area() {
		return _area;
	}
	public void set_city(mmlPhcity _city) {
		this._city = _city;
	}
	public mmlPhcity get_city() {
		return _city;
	}
	public void set_number(mmlPhnumber _number) {
		this._number = _number;
	}
	public mmlPhnumber get_number() {
		return _number;
	}
	public void set_extension(mmlPhextension _extension) {
		this._extension = _extension;
	}
	public mmlPhextension get_extension() {
		return _extension;
	}
	public void set_country(mmlPhcountry _country) {
		this._country = _country;
	}
	public mmlPhcountry get_country() {
		return _country;
	}
	public void set_memo(mmlPhmemo _memo) {
		this._memo = _memo;
	}
	public mmlPhmemo get_memo() {
		return _memo;
	}
	
}