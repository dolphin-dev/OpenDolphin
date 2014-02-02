/**
 *
 * mmlPsiPersonalizedInfo.java
 * Created on 2003/1/4 2:29:55
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
			if ( this.getLocalName().equals("levelone") ) {
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
				((mmlSmstaffInfo)builder.getParent()).set_PersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSm:referTo")) {
				((mmlSmreferTo)builder.getParent()).set_PersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSm:referFrom")) {
				((mmlSmreferFrom)builder.getParent()).set_PersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:staffInfo")) {
				Vector v = ((mmlSgstaffInfo)builder.getParent()).get_PersonalizedInfo();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlRe:referToPerson")) {
				((mmlRereferToPerson)builder.getParent()).set_PersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlRe:referFrom")) {
				((mmlRereferFrom)builder.getParent()).set_PersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlCi:CreatorInfo")) {
				((mmlCiCreatorInfo)builder.getParent()).set_PersonalizedInfo((mmlPsiPersonalizedInfo)builder.getElement());
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
	public void set_Id(mmlCmId _Id) {
		this._Id = _Id;
	}
	public mmlCmId get_Id() {
		return _Id;
	}
	public void set_personName(mmlPsipersonName _personName) {
		this._personName = _personName;
	}
	public mmlPsipersonName get_personName() {
		return _personName;
	}
	public void set_Facility(mmlFcFacility _Facility) {
		this._Facility = _Facility;
	}
	public mmlFcFacility get_Facility() {
		return _Facility;
	}
	public void set_Department(mmlDpDepartment _Department) {
		this._Department = _Department;
	}
	public mmlDpDepartment get_Department() {
		return _Department;
	}
	public void set_addresses(mmlPsiaddresses _addresses) {
		this._addresses = _addresses;
	}
	public mmlPsiaddresses get_addresses() {
		return _addresses;
	}
	public void set_emailAddresses(mmlPsiemailAddresses _emailAddresses) {
		this._emailAddresses = _emailAddresses;
	}
	public mmlPsiemailAddresses get_emailAddresses() {
		return _emailAddresses;
	}
	public void set_phones(mmlPsiphones _phones) {
		this._phones = _phones;
	}
	public mmlPsiphones get_phones() {
		return _phones;
	}
	
}