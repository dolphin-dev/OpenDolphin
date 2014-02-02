/**
 *
 * mmlcontent.java
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
public class mmlcontent extends MMLObject {
	
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
	
	public mmlcontent() {
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
		if (qName.equals("mml:content") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlcontent obj = new mmlcontent();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlcontent)builder.getElement()).setNamespace( getNamespace() );
			((mmlcontent)builder.getElement()).setLocalName( getLocalName() );
			((mmlcontent)builder.getElement()).setQName( getQName() );
			((mmlcontent)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:content") == true) {
			
			/* connection */
			
			printlnStatus(parentElement.getQName()+" /"+qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	
	
	/* setters and getters */
	public void set_PatientModule(mmlPiPatientModule _PatientModule) {
		this._PatientModule = _PatientModule;
	}
	public mmlPiPatientModule get_PatientModule() {
		return _PatientModule;
	}
	public void set_HealthInsuranceModule(mmlHiHealthInsuranceModule _HealthInsuranceModule) {
		this._HealthInsuranceModule = _HealthInsuranceModule;
	}
	public mmlHiHealthInsuranceModule get_HealthInsuranceModule() {
		return _HealthInsuranceModule;
	}
	public void set_RegisteredDiagnosisModule(mmlRdRegisteredDiagnosisModule _RegisteredDiagnosisModule) {
		this._RegisteredDiagnosisModule = _RegisteredDiagnosisModule;
	}
	public mmlRdRegisteredDiagnosisModule get_RegisteredDiagnosisModule() {
		return _RegisteredDiagnosisModule;
	}
	public void set_LifestyleModule(mmlLsLifestyleModule _LifestyleModule) {
		this._LifestyleModule = _LifestyleModule;
	}
	public mmlLsLifestyleModule get_LifestyleModule() {
		return _LifestyleModule;
	}
	public void set_BaseClinicModule(mmlBcBaseClinicModule _BaseClinicModule) {
		this._BaseClinicModule = _BaseClinicModule;
	}
	public mmlBcBaseClinicModule get_BaseClinicModule() {
		return _BaseClinicModule;
	}
	public void set_FirstClinicModule(mmlFclFirstClinicModule _FirstClinicModule) {
		this._FirstClinicModule = _FirstClinicModule;
	}
	public mmlFclFirstClinicModule get_FirstClinicModule() {
		return _FirstClinicModule;
	}
	public void set_ProgressCourseModule(mmlPcProgressCourseModule _ProgressCourseModule) {
		this._ProgressCourseModule = _ProgressCourseModule;
	}
	public mmlPcProgressCourseModule get_ProgressCourseModule() {
		return _ProgressCourseModule;
	}
	public void set_SurgeryModule(mmlSgSurgeryModule _SurgeryModule) {
		this._SurgeryModule = _SurgeryModule;
	}
	public mmlSgSurgeryModule get_SurgeryModule() {
		return _SurgeryModule;
	}
	public void set_SummaryModule(mmlSmSummaryModule _SummaryModule) {
		this._SummaryModule = _SummaryModule;
	}
	public mmlSmSummaryModule get_SummaryModule() {
		return _SummaryModule;
	}
	public void set_TestModule(mmlLbTestModule _TestModule) {
		this._TestModule = _TestModule;
	}
	public mmlLbTestModule get_TestModule() {
		return _TestModule;
	}
	public void set_ReportModule(mmlRpReportModule _ReportModule) {
		this._ReportModule = _ReportModule;
	}
	public mmlRpReportModule get_ReportModule() {
		return _ReportModule;
	}
	public void set_ReferralModule(mmlReReferralModule _ReferralModule) {
		this._ReferralModule = _ReferralModule;
	}
	public mmlReReferralModule get_ReferralModule() {
		return _ReferralModule;
	}
	public void set_ClaimModule(claimClaimModule _ClaimModule) {
		this._ClaimModule = _ClaimModule;
	}
	public claimClaimModule get_ClaimModule() {
		return _ClaimModule;
	}
	public void set_ClaimAmountModule(claimAClaimAmountModule _ClaimAmountModule) {
		this._ClaimAmountModule = _ClaimAmountModule;
	}
	public claimAClaimAmountModule get_ClaimAmountModule() {
		return _ClaimAmountModule;
	}
	
}