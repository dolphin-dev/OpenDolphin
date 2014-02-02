/**
 *
 * mmlPiPatientModule.java
 * Created on 2003/1/4 2:30:0
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
public class mmlPiPatientModule extends MMLObject {
	
	/* fields */
	private mmlPiuniqueInfo _uniqueInfo = null;
	private mmlPipersonName _personName = null;
	private mmlPibirthday _birthday = null;
	private mmlPisex _sex = null;
	private mmlPinationality _nationality = null;
	private mmlPirace _race = null;
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
			if ( this.getLocalName().equals("levelone") ) {
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
			if ( _race != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_race.printObject(pw, visitor);
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
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlPiPatientModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:ReferralModule")) {
				((mmlReReferralModule)builder.getParent()).set_PatientModule((mmlPiPatientModule)builder.getElement());
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_PatientModule((mmlPiPatientModule)builder.getElement());
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
	public void set_uniqueInfo(mmlPiuniqueInfo _uniqueInfo) {
		this._uniqueInfo = _uniqueInfo;
	}
	public mmlPiuniqueInfo get_uniqueInfo() {
		return _uniqueInfo;
	}
	public void set_personName(mmlPipersonName _personName) {
		this._personName = _personName;
	}
	public mmlPipersonName get_personName() {
		return _personName;
	}
	public void set_birthday(mmlPibirthday _birthday) {
		this._birthday = _birthday;
	}
	public mmlPibirthday get_birthday() {
		return _birthday;
	}
	public void set_sex(mmlPisex _sex) {
		this._sex = _sex;
	}
	public mmlPisex get_sex() {
		return _sex;
	}
	public void set_nationality(mmlPinationality _nationality) {
		this._nationality = _nationality;
	}
	public mmlPinationality get_nationality() {
		return _nationality;
	}
	public void set_race(mmlPirace _race) {
		this._race = _race;
	}
	public mmlPirace get_race() {
		return _race;
	}
	public void set_marital(mmlPimarital _marital) {
		this._marital = _marital;
	}
	public mmlPimarital get_marital() {
		return _marital;
	}
	public void set_addresses(mmlPiaddresses _addresses) {
		this._addresses = _addresses;
	}
	public mmlPiaddresses get_addresses() {
		return _addresses;
	}
	public void set_emailAddresses(mmlPiemailAddresses _emailAddresses) {
		this._emailAddresses = _emailAddresses;
	}
	public mmlPiemailAddresses get_emailAddresses() {
		return _emailAddresses;
	}
	public void set_phones(mmlPiphones _phones) {
		this._phones = _phones;
	}
	public mmlPiphones get_phones() {
		return _phones;
	}
	public void set_accountNumber(mmlPiaccountNumber _accountNumber) {
		this._accountNumber = _accountNumber;
	}
	public mmlPiaccountNumber get_accountNumber() {
		return _accountNumber;
	}
	public void set_socialIdentification(mmlPisocialIdentification _socialIdentification) {
		this._socialIdentification = _socialIdentification;
	}
	public mmlPisocialIdentification get_socialIdentification() {
		return _socialIdentification;
	}
	public void set_death(mmlPideath _death) {
		this._death = _death;
	}
	public mmlPideath get_death() {
		return _death;
	}
	
}