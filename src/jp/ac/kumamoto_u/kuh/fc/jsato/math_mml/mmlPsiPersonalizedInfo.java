/**
 *
 * mmlPsiPersonalizedInfo.java
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
public class mmlPsiPersonalizedInfo extends MMLObject {
	
	/* fields */
	private mmlCmId _Id = null;
	private mmlPsipersonName _personName = null;
	private mmlFcFacility _Facility = null;
	private mmlDpDepartment _Department = null;
	private mmlPsiaddresses _addresses = null;
	private mmlPsiemailAddresses _emailAddresses = null;
	private mmlPsiphones _phones = null;
	
	public mmlPsiPersonalizedInfo() {
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
			if ( _Id != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Id.printObject(pw, visitor);
			}
			if ( _personName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_personName.printObject(pw, visitor);
			}
			if ( _Facility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Facility.printObject(pw, visitor);
			}
			if ( _Department != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Department.printObject(pw, visitor);
			}
			if ( _addresses != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_addresses.printObject(pw, visitor);
			}
			if ( _emailAddresses != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_emailAddresses.printObject(pw, visitor);
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
		if (qName.equals("mmlPsi:PersonalizedInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPsiPersonalizedInfo obj = new mmlPsiPersonalizedInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPsiPersonalizedInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlPsiPersonalizedInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlPsiPersonalizedInfo)builder.getElement()).setQName( getQName() );
			((mmlPsiPersonalizedInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPsi:PersonalizedInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:staffInfo")) {
				((mmlSmstaffInfo)builder.getParent()).setPersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSm:referTo")) {
				((mmlSmreferTo)builder.getParent()).setPersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSm:referFrom")) {
				((mmlSmreferFrom)builder.getParent()).setPersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:staffInfo")) {
				Vector v = ((mmlSgstaffInfo)builder.getParent()).getPersonalizedInfo();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlRe:referToPerson")) {
				((mmlRereferToPerson)builder.getParent()).setPersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlRe:referFrom")) {
				((mmlRereferFrom)builder.getParent()).setPersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlCi:CreatorInfo")) {
				((mmlCiCreatorInfo)builder.getParent()).setPersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
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
	public void setId(mmlCmId _Id) {
		this._Id = _Id;
	}
	public mmlCmId getId() {
		return _Id;
	}
	public void setPersonName(mmlPsipersonName _personName) {
		this._personName = _personName;
	}
	public mmlPsipersonName getPersonName() {
		return _personName;
	}
	public void setFacility(mmlFcFacility _Facility) {
		this._Facility = _Facility;
	}
	public mmlFcFacility getFacility() {
		return _Facility;
	}
	public void setDepartment(mmlDpDepartment _Department) {
		this._Department = _Department;
	}
	public mmlDpDepartment getDepartment() {
		return _Department;
	}
	public void setAddresses(mmlPsiaddresses _addresses) {
		this._addresses = _addresses;
	}
	public mmlPsiaddresses getAddresses() {
		return _addresses;
	}
	public void setEmailAddresses(mmlPsiemailAddresses _emailAddresses) {
		this._emailAddresses = _emailAddresses;
	}
	public mmlPsiemailAddresses getEmailAddresses() {
		return _emailAddresses;
	}
	public void setPhones(mmlPsiphones _phones) {
		this._phones = _phones;
	}
	public mmlPsiphones getPhones() {
		return _phones;
	}
	
}