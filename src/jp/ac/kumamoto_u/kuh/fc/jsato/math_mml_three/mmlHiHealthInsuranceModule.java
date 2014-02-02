/**
 *
 * mmlHiHealthInsuranceModule.java
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
public class mmlHiHealthInsuranceModule extends MMLObject {
	
	/* fields */
	private String __mmlHicountryType = null;

	private mmlHiinsuranceClass _insuranceClass = null;
	private mmlHiinsuranceNumber _insuranceNumber = null;
	private mmlHiclientId _clientId = null;
	private mmlHifamilyClass _familyClass = null;
	private mmlHiclientInfo _clientInfo = null;
	private mmlHicontinuedDiseases _continuedDiseases = null;
	private mmlHistartDate _startDate = null;
	private mmlHiexpiredDate _expiredDate = null;
	private mmlHipaymentInRatio _paymentInRatio = null;
	private mmlHipaymentOutRatio _paymentOutRatio = null;
	private mmlHiinsuredInfo _insuredInfo = null;
	private mmlHiworkInfo _workInfo = null;
	private mmlHipublicInsurance _publicInsurance = null;
	
	public mmlHiHealthInsuranceModule() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlHicountryType != null ) pw.print(" " + "mmlHi:countryType" +  "=" + "'" + __mmlHicountryType + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _insuranceClass != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_insuranceClass.printObject(pw, visitor);
			}
			if ( _insuranceNumber != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_insuranceNumber.printObject(pw, visitor);
			}
			if ( _clientId != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_clientId.printObject(pw, visitor);
			}
			if ( _familyClass != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_familyClass.printObject(pw, visitor);
			}
			if ( _clientInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_clientInfo.printObject(pw, visitor);
			}
			if ( _continuedDiseases != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_continuedDiseases.printObject(pw, visitor);
			}
			if ( _startDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_startDate.printObject(pw, visitor);
			}
			if ( _expiredDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_expiredDate.printObject(pw, visitor);
			}
			if ( _paymentInRatio != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_paymentInRatio.printObject(pw, visitor);
			}
			if ( _paymentOutRatio != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_paymentOutRatio.printObject(pw, visitor);
			}
			if ( _insuredInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_insuredInfo.printObject(pw, visitor);
			}
			if ( _workInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_workInfo.printObject(pw, visitor);
			}
			if ( _publicInsurance != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_publicInsurance.printObject(pw, visitor);
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
		if (qName.equals("mmlHi:HealthInsuranceModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHiHealthInsuranceModule obj = new mmlHiHealthInsuranceModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHiHealthInsuranceModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlHiHealthInsuranceModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlHiHealthInsuranceModule)builder.getElement()).setQName( getQName() );
			((mmlHiHealthInsuranceModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlHi:countryType") ) {
						set__mmlHicountryType( atts.getValue(i) );
						((mmlHiHealthInsuranceModule)builder.getElement()).set__mmlHicountryType( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:HealthInsuranceModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlHiHealthInsuranceModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_HealthInsuranceModule((mmlHiHealthInsuranceModule)builder.getElement());
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
	public void set__mmlHicountryType(String __mmlHicountryType) {
		this.__mmlHicountryType = __mmlHicountryType;
	}
	public String get__mmlHicountryType() {
		return __mmlHicountryType;
	}

	public void set_insuranceClass(mmlHiinsuranceClass _insuranceClass) {
		this._insuranceClass = _insuranceClass;
	}
	public mmlHiinsuranceClass get_insuranceClass() {
		return _insuranceClass;
	}
	public void set_insuranceNumber(mmlHiinsuranceNumber _insuranceNumber) {
		this._insuranceNumber = _insuranceNumber;
	}
	public mmlHiinsuranceNumber get_insuranceNumber() {
		return _insuranceNumber;
	}
	public void set_clientId(mmlHiclientId _clientId) {
		this._clientId = _clientId;
	}
	public mmlHiclientId get_clientId() {
		return _clientId;
	}
	public void set_familyClass(mmlHifamilyClass _familyClass) {
		this._familyClass = _familyClass;
	}
	public mmlHifamilyClass get_familyClass() {
		return _familyClass;
	}
	public void set_clientInfo(mmlHiclientInfo _clientInfo) {
		this._clientInfo = _clientInfo;
	}
	public mmlHiclientInfo get_clientInfo() {
		return _clientInfo;
	}
	public void set_continuedDiseases(mmlHicontinuedDiseases _continuedDiseases) {
		this._continuedDiseases = _continuedDiseases;
	}
	public mmlHicontinuedDiseases get_continuedDiseases() {
		return _continuedDiseases;
	}
	public void set_startDate(mmlHistartDate _startDate) {
		this._startDate = _startDate;
	}
	public mmlHistartDate get_startDate() {
		return _startDate;
	}
	public void set_expiredDate(mmlHiexpiredDate _expiredDate) {
		this._expiredDate = _expiredDate;
	}
	public mmlHiexpiredDate get_expiredDate() {
		return _expiredDate;
	}
	public void set_paymentInRatio(mmlHipaymentInRatio _paymentInRatio) {
		this._paymentInRatio = _paymentInRatio;
	}
	public mmlHipaymentInRatio get_paymentInRatio() {
		return _paymentInRatio;
	}
	public void set_paymentOutRatio(mmlHipaymentOutRatio _paymentOutRatio) {
		this._paymentOutRatio = _paymentOutRatio;
	}
	public mmlHipaymentOutRatio get_paymentOutRatio() {
		return _paymentOutRatio;
	}
	public void set_insuredInfo(mmlHiinsuredInfo _insuredInfo) {
		this._insuredInfo = _insuredInfo;
	}
	public mmlHiinsuredInfo get_insuredInfo() {
		return _insuredInfo;
	}
	public void set_workInfo(mmlHiworkInfo _workInfo) {
		this._workInfo = _workInfo;
	}
	public mmlHiworkInfo get_workInfo() {
		return _workInfo;
	}
	public void set_publicInsurance(mmlHipublicInsurance _publicInsurance) {
		this._publicInsurance = _publicInsurance;
	}
	public mmlHipublicInsurance get_publicInsurance() {
		return _publicInsurance;
	}
	
}