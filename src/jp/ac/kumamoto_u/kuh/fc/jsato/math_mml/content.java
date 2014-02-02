/**
 *
 * content.java
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
public class content extends MMLObject {
	
	/* fields */
	private mmlPiPatientModule _PatientModule = null;
	private mmlHiHealthInsuranceModule _HealthInsuranceModule = null;
	private mmlRdRegisteredDiagnosisModule _RegisteredDiagnosisModule = null;
	private mmlLsLifestyleModule _LifestyleModule = null;
	private mmlBcBaseClinicModule _BaseClinicModule = null;
	private mmlFclFirstClinicModule _FirstClinicModule = null;
	private mmlPcProgressCourseModule _ProgressCourseModule = null;
	private mmlSgSurgeryModule _SurgeryModule = null;
	private mmlSmSummaryModule _SummaryModule = null;
	private mmlLbTestModule _TestModule = null;
	private mmlRpReportModule _ReportModule = null;
	private mmlReReferralModule _ReferralModule = null;
	private claimClaimModule _ClaimModule = null;
	private claimAClaimAmountModule _ClaimAmountModule = null;
	
	public content() {
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
			if ( _PatientModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_PatientModule.printObject(pw, visitor);
			}
			if ( _HealthInsuranceModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_HealthInsuranceModule.printObject(pw, visitor);
			}
			if ( _RegisteredDiagnosisModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_RegisteredDiagnosisModule.printObject(pw, visitor);
			}
			if ( _LifestyleModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_LifestyleModule.printObject(pw, visitor);
			}
			if ( _BaseClinicModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_BaseClinicModule.printObject(pw, visitor);
			}
			if ( _FirstClinicModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_FirstClinicModule.printObject(pw, visitor);
			}
			if ( _ProgressCourseModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ProgressCourseModule.printObject(pw, visitor);
			}
			if ( _SurgeryModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_SurgeryModule.printObject(pw, visitor);
			}
			if ( _SummaryModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_SummaryModule.printObject(pw, visitor);
			}
			if ( _TestModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_TestModule.printObject(pw, visitor);
			}
			if ( _ReportModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ReportModule.printObject(pw, visitor);
			}
			if ( _ReferralModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ReferralModule.printObject(pw, visitor);
			}
			if ( _ClaimModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ClaimModule.printObject(pw, visitor);
			}
			if ( _ClaimAmountModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ClaimAmountModule.printObject(pw, visitor);
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
		if (qName.equals("content") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			content obj = new content();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((content)builder.getElement()).setNamespace( getNamespace() );
			((content)builder.getElement()).setLocalName( getLocalName() );
			((content)builder.getElement()).setQName( getQName() );
			((content)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("content") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("MmlModuleItem")) {
				((MmlModuleItem)builder.getParent()).setContent((content)builder.getElement());
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
	public void setPatientModule(mmlPiPatientModule _PatientModule) {
		this._PatientModule = _PatientModule;
	}
	public mmlPiPatientModule getPatientModule() {
		return _PatientModule;
	}
	public void setHealthInsuranceModule(mmlHiHealthInsuranceModule _HealthInsuranceModule) {
		this._HealthInsuranceModule = _HealthInsuranceModule;
	}
	public mmlHiHealthInsuranceModule getHealthInsuranceModule() {
		return _HealthInsuranceModule;
	}
	public void setRegisteredDiagnosisModule(mmlRdRegisteredDiagnosisModule _RegisteredDiagnosisModule) {
		this._RegisteredDiagnosisModule = _RegisteredDiagnosisModule;
	}
	public mmlRdRegisteredDiagnosisModule getRegisteredDiagnosisModule() {
		return _RegisteredDiagnosisModule;
	}
	public void setLifestyleModule(mmlLsLifestyleModule _LifestyleModule) {
		this._LifestyleModule = _LifestyleModule;
	}
	public mmlLsLifestyleModule getLifestyleModule() {
		return _LifestyleModule;
	}
	public void setBaseClinicModule(mmlBcBaseClinicModule _BaseClinicModule) {
		this._BaseClinicModule = _BaseClinicModule;
	}
	public mmlBcBaseClinicModule getBaseClinicModule() {
		return _BaseClinicModule;
	}
	public void setFirstClinicModule(mmlFclFirstClinicModule _FirstClinicModule) {
		this._FirstClinicModule = _FirstClinicModule;
	}
	public mmlFclFirstClinicModule getFirstClinicModule() {
		return _FirstClinicModule;
	}
	public void setProgressCourseModule(mmlPcProgressCourseModule _ProgressCourseModule) {
		this._ProgressCourseModule = _ProgressCourseModule;
	}
	public mmlPcProgressCourseModule getProgressCourseModule() {
		return _ProgressCourseModule;
	}
	public void setSurgeryModule(mmlSgSurgeryModule _SurgeryModule) {
		this._SurgeryModule = _SurgeryModule;
	}
	public mmlSgSurgeryModule getSurgeryModule() {
		return _SurgeryModule;
	}
	public void setSummaryModule(mmlSmSummaryModule _SummaryModule) {
		this._SummaryModule = _SummaryModule;
	}
	public mmlSmSummaryModule getSummaryModule() {
		return _SummaryModule;
	}
	public void setTestModule(mmlLbTestModule _TestModule) {
		this._TestModule = _TestModule;
	}
	public mmlLbTestModule getTestModule() {
		return _TestModule;
	}
	public void setReportModule(mmlRpReportModule _ReportModule) {
		this._ReportModule = _ReportModule;
	}
	public mmlRpReportModule getReportModule() {
		return _ReportModule;
	}
	public void setReferralModule(mmlReReferralModule _ReferralModule) {
		this._ReferralModule = _ReferralModule;
	}
	public mmlReReferralModule getReferralModule() {
		return _ReferralModule;
	}
	public void setClaimModule(claimClaimModule _ClaimModule) {
		this._ClaimModule = _ClaimModule;
	}
	public claimClaimModule getClaimModule() {
		return _ClaimModule;
	}
	public void setClaimAmountModule(claimAClaimAmountModule _ClaimAmountModule) {
		this._ClaimAmountModule = _ClaimAmountModule;
	}
	public claimAClaimAmountModule getClaimAmountModule() {
		return _ClaimAmountModule;
	}
	
}