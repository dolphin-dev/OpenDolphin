/**
 *
 * mmlHiHealthInsuranceModule.java
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

			if ( this.getLocalName().equals("Mml") ) {
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
				setMmlHicountryType( atts.getValue(namespaceURI, "countryType") );
				((mmlHiHealthInsuranceModule)builder.getElement()).setMmlHicountryType( atts.getValue(namespaceURI, "countryType") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:HealthInsuranceModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("content")) {
				((content)builder.getParent()).setHealthInsuranceModule((mmlHiHealthInsuranceModule)builder.getElement());
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
	public void setMmlHicountryType(String __mmlHicountryType) {
		this.__mmlHicountryType = __mmlHicountryType;
	}
	public String getMmlHicountryType() {
		return __mmlHicountryType;
	}

	public void setInsuranceClass(mmlHiinsuranceClass _insuranceClass) {
		this._insuranceClass = _insuranceClass;
	}
	public mmlHiinsuranceClass getInsuranceClass() {
		return _insuranceClass;
	}
	public void setInsuranceNumber(mmlHiinsuranceNumber _insuranceNumber) {
		this._insuranceNumber = _insuranceNumber;
	}
	public mmlHiinsuranceNumber getInsuranceNumber() {
		return _insuranceNumber;
	}
	public void setClientId(mmlHiclientId _clientId) {
		this._clientId = _clientId;
	}
	public mmlHiclientId getClientId() {
		return _clientId;
	}
	public void setFamilyClass(mmlHifamilyClass _familyClass) {
		this._familyClass = _familyClass;
	}
	public mmlHifamilyClass getFamilyClass() {
		return _familyClass;
	}
	public void setClientInfo(mmlHiclientInfo _clientInfo) {
		this._clientInfo = _clientInfo;
	}
	public mmlHiclientInfo getClientInfo() {
		return _clientInfo;
	}
	public void setContinuedDiseases(mmlHicontinuedDiseases _continuedDiseases) {
		this._continuedDiseases = _continuedDiseases;
	}
	public mmlHicontinuedDiseases getContinuedDiseases() {
		return _continuedDiseases;
	}
	public void setStartDate(mmlHistartDate _startDate) {
		this._startDate = _startDate;
	}
	public mmlHistartDate getStartDate() {
		return _startDate;
	}
	public void setExpiredDate(mmlHiexpiredDate _expiredDate) {
		this._expiredDate = _expiredDate;
	}
	public mmlHiexpiredDate getExpiredDate() {
		return _expiredDate;
	}
	public void setPaymentInRatio(mmlHipaymentInRatio _paymentInRatio) {
		this._paymentInRatio = _paymentInRatio;
	}
	public mmlHipaymentInRatio getPaymentInRatio() {
		return _paymentInRatio;
	}
	public void setPaymentOutRatio(mmlHipaymentOutRatio _paymentOutRatio) {
		this._paymentOutRatio = _paymentOutRatio;
	}
	public mmlHipaymentOutRatio getPaymentOutRatio() {
		return _paymentOutRatio;
	}
	public void setInsuredInfo(mmlHiinsuredInfo _insuredInfo) {
		this._insuredInfo = _insuredInfo;
	}
	public mmlHiinsuredInfo getInsuredInfo() {
		return _insuredInfo;
	}
	public void setWorkInfo(mmlHiworkInfo _workInfo) {
		this._workInfo = _workInfo;
	}
	public mmlHiworkInfo getWorkInfo() {
		return _workInfo;
	}
	public void setPublicInsurance(mmlHipublicInsurance _publicInsurance) {
		this._publicInsurance = _publicInsurance;
	}
	public mmlHipublicInsurance getPublicInsurance() {
		return _publicInsurance;
	}
	
}