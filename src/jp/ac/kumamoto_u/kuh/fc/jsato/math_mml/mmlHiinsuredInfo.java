/**
 *
 * mmlHiinsuredInfo.java
 * Created on 2002/7/30 10:0:25
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
public class mmlHiinsuredInfo extends MMLObject {
	
	/* fields */
	private mmlHifacility _facility = null;
	private mmlHiaddresses _addresses = null;
	private mmlHiphones _phones = null;
	
	public mmlHiinsuredInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _facility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_facility.printObject(pw, visitor);
			}
			if ( _addresses != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_addresses.printObject(pw, visitor);
			}
			if ( _phones != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_phones.printObject(pw, visitor);
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
		if (qName.equals("mmlHi:insuredInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHiinsuredInfo obj = new mmlHiinsuredInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHiinsuredInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlHiinsuredInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlHiinsuredInfo)builder.getElement()).setQName( getQName() );
			((mmlHiinsuredInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:insuredInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:HealthInsuranceModule")) {
				((mmlHiHealthInsuranceModule)builder.getParent()).setInsuredInfo((mmlHiinsuredInfo)builder.getElement());
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
	public void setFacility(mmlHifacility _facility) {
		this._facility = _facility;
	}
	public mmlHifacility getFacility() {
		return _facility;
	}
	public void setAddresses(mmlHiaddresses _addresses) {
		this._addresses = _addresses;
	}
	public mmlHiaddresses getAddresses() {
		return _addresses;
	}
	public void setPhones(mmlHiphones _phones) {
		this._phones = _phones;
	}
	public mmlHiphones getPhones() {
		return _phones;
	}
	
}