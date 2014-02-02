/**
 *
 * mmlPiPatientModule.java
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
public class mmlPiPatientModule extends MMLObject {
	
	/* fields */
	private mmlPiuniqueInfo _uniqueInfo = null;
	private mmlPipersonName _personName = null;
	private mmlPibirthday _birthday = null;
	private mmlPisex _sex = null;
	private mmlPinationality _nationality = null;
	private mmlPimarital _marital = null;
	private mmlPiaddresses _addresses = null;
	private mmlPiemailAddresses _emailAddresses = null;
	private mmlPiphones _phones = null;
	private mmlPiaccountNumber _accountNumber = null;
	private mmlPisocialIdentification _socialIdentification = null;
	private mmlPideath _death = null;
	
	public mmlPiPatientModule() {
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
			if ( _uniqueInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_uniqueInfo.printObject(pw, visitor);
			}
			if ( _personName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_personName.printObject(pw, visitor);
			}
			if ( _birthday != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_birthday.printObject(pw, visitor);
			}
			if ( _sex != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_sex.printObject(pw, visitor);
			}
			if ( _nationality != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_nationality.printObject(pw, visitor);
			}
			if ( _marital != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_marital.printObject(pw, visitor);
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
			if ( _accountNumber != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_accountNumber.printObject(pw, visitor);
			}
			if ( _socialIdentification != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_socialIdentification.printObject(pw, visitor);
			}
			if ( _death != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_death.printObject(pw, visitor);
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
		if (qName.equals("mmlPi:PatientModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPiPatientModule obj = new mmlPiPatientModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPiPatientModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlPiPatientModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlPiPatientModule)builder.getElement()).setQName( getQName() );
			((mmlPiPatientModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPi:PatientModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRe:ReferralModule")) {
				((mmlReReferralModule)builder.getParent()).setPatientModule((mmlPiPatientModule)builder.getElement());
			}

			if (parentElement.getQName().equals("content")) {
				((content)builder.getParent()).setPatientModule((mmlPiPatientModule)builder.getElement());
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
	public void setUniqueInfo(mmlPiuniqueInfo _uniqueInfo) {
		this._uniqueInfo = _uniqueInfo;
	}
	public mmlPiuniqueInfo getUniqueInfo() {
		return _uniqueInfo;
	}
	public void setPersonName(mmlPipersonName _personName) {
		this._personName = _personName;
	}
	public mmlPipersonName getPersonName() {
		return _personName;
	}
	public void setBirthday(mmlPibirthday _birthday) {
		this._birthday = _birthday;
	}
	public mmlPibirthday getBirthday() {
		return _birthday;
	}
	public void setSex(mmlPisex _sex) {
		this._sex = _sex;
	}
	public mmlPisex getSex() {
		return _sex;
	}
	public void setNationality(mmlPinationality _nationality) {
		this._nationality = _nationality;
	}
	public mmlPinationality getNationality() {
		return _nationality;
	}
	public void setMarital(mmlPimarital _marital) {
		this._marital = _marital;
	}
	public mmlPimarital getMarital() {
		return _marital;
	}
	public void setAddresses(mmlPiaddresses _addresses) {
		this._addresses = _addresses;
	}
	public mmlPiaddresses getAddresses() {
		return _addresses;
	}
	public void setEmailAddresses(mmlPiemailAddresses _emailAddresses) {
		this._emailAddresses = _emailAddresses;
	}
	public mmlPiemailAddresses getEmailAddresses() {
		return _emailAddresses;
	}
	public void setPhones(mmlPiphones _phones) {
		this._phones = _phones;
	}
	public mmlPiphones getPhones() {
		return _phones;
	}
	public void setAccountNumber(mmlPiaccountNumber _accountNumber) {
		this._accountNumber = _accountNumber;
	}
	public mmlPiaccountNumber getAccountNumber() {
		return _accountNumber;
	}
	public void setSocialIdentification(mmlPisocialIdentification _socialIdentification) {
		this._socialIdentification = _socialIdentification;
	}
	public mmlPisocialIdentification getSocialIdentification() {
		return _socialIdentification;
	}
	public void setDeath(mmlPideath _death) {
		this._death = _death;
	}
	public mmlPideath getDeath() {
		return _death;
	}
	
}