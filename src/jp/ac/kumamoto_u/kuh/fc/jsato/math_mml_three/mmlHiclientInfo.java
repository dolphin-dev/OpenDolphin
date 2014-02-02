/**
 *
 * mmlHiclientInfo.java
 * Created on 2003/1/4 2:30:1
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
public class mmlHiclientInfo extends MMLObject {
	
	/* fields */
	private mmlHipersonName _personName = null;
	private mmlHiaddresses _addresses = null;
	private mmlHiphones _phones = null;
	
	public mmlHiclientInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _personName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_personName.printObject(pw, visitor);
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
		if (qName.equals("mmlHi:clientInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHiclientInfo obj = new mmlHiclientInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHiclientInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlHiclientInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlHiclientInfo)builder.getElement()).setQName( getQName() );
			((mmlHiclientInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:clientInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:HealthInsuranceModule")) {
				((mmlHiHealthInsuranceModule)builder.getParent()).set_clientInfo((mmlHiclientInfo)builder.getElement());
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
	public void set_personName(mmlHipersonName _personName) {
		this._personName = _personName;
	}
	public mmlHipersonName get_personName() {
		return _personName;
	}
	public void set_addresses(mmlHiaddresses _addresses) {
		this._addresses = _addresses;
	}
	public mmlHiaddresses get_addresses() {
		return _addresses;
	}
	public void set_phones(mmlHiphones _phones) {
		this._phones = _phones;
	}
	public mmlHiphones get_phones() {
		return _phones;
	}
	
}