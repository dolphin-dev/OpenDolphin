/**
 *
 * mmlReReferralModule.java
 * Created on 2002/7/30 10:0:37
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
public class mmlReReferralModule extends MMLObject {
	
	/* fields */
	private mmlPiPatientModule _PatientModule = null;
	private mmlReoccupation _occupation = null;
	private mmlRereferFrom _referFrom = null;
	private mmlRetitle _title = null;
	private mmlRegreeting _greeting = null;
	private mmlRechiefComplaints _chiefComplaints = null;
	private mmlReclinicalDiagnosis _clinicalDiagnosis = null;
	private mmlRepastHistory _pastHistory = null;
	private mmlRefamilyHistory _familyHistory = null;
	private mmlRepresentIllness _presentIllness = null;
	private mmlRetestResults _testResults = null;
	private mmlSmclinicalCourse _clinicalCourse = null;
	private mmlRemedication _medication = null;
	private mmlRereferPurpose _referPurpose = null;
	private mmlReremarks _remarks = null;
	private mmlRereferToFacility _referToFacility = null;
	private mmlRereferToPerson _referToPerson = null;
	private mmlRereferToUnknownName _referToUnknownName = null;
	
	public mmlReReferralModule() {
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
			if ( _occupation != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_occupation.printObject(pw, visitor);
			}
			if ( _referFrom != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referFrom.printObject(pw, visitor);
			}
			if ( _title != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_title.printObject(pw, visitor);
			}
			if ( _greeting != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_greeting.printObject(pw, visitor);
			}
			if ( _chiefComplaints != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_chiefComplaints.printObject(pw, visitor);
			}
			if ( _clinicalDiagnosis != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_clinicalDiagnosis.printObject(pw, visitor);
			}
			if ( _pastHistory != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_pastHistory.printObject(pw, visitor);
			}
			if ( _familyHistory != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_familyHistory.printObject(pw, visitor);
			}
			if ( _presentIllness != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_presentIllness.printObject(pw, visitor);
			}
			if ( _testResults != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testResults.printObject(pw, visitor);
			}
			if ( _clinicalCourse != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_clinicalCourse.printObject(pw, visitor);
			}
			if ( _medication != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_medication.printObject(pw, visitor);
			}
			if ( _referPurpose != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referPurpose.printObject(pw, visitor);
			}
			if ( _remarks != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_remarks.printObject(pw, visitor);
			}
			if ( _referToFacility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referToFacility.printObject(pw, visitor);
			}
			if ( _referToPerson != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referToPerson.printObject(pw, visitor);
			}
			if ( _referToUnknownName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referToUnknownName.printObject(pw, visitor);
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
		if (qName.equals("mmlRe:ReferralModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlReReferralModule obj = new mmlReReferralModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlReReferralModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlReReferralModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlReReferralModule)builder.getElement()).setQName( getQName() );
			((mmlReReferralModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRe:ReferralModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("content")) {
				((content)builder.getParent()).setReferralModule((mmlReReferralModule)builder.getElement());
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
	public void setOccupation(mmlReoccupation _occupation) {
		this._occupation = _occupation;
	}
	public mmlReoccupation getOccupation() {
		return _occupation;
	}
	public void setReferFrom(mmlRereferFrom _referFrom) {
		this._referFrom = _referFrom;
	}
	public mmlRereferFrom getReferFrom() {
		return _referFrom;
	}
	public void setTitle(mmlRetitle _title) {
		this._title = _title;
	}
	public mmlRetitle getTitle() {
		return _title;
	}
	public void setGreeting(mmlRegreeting _greeting) {
		this._greeting = _greeting;
	}
	public mmlRegreeting getGreeting() {
		return _greeting;
	}
	public void setChiefComplaints(mmlRechiefComplaints _chiefComplaints) {
		this._chiefComplaints = _chiefComplaints;
	}
	public mmlRechiefComplaints getChiefComplaints() {
		return _chiefComplaints;
	}
	public void setClinicalDiagnosis(mmlReclinicalDiagnosis _clinicalDiagnosis) {
		this._clinicalDiagnosis = _clinicalDiagnosis;
	}
	public mmlReclinicalDiagnosis getClinicalDiagnosis() {
		return _clinicalDiagnosis;
	}
	public void setPastHistory(mmlRepastHistory _pastHistory) {
		this._pastHistory = _pastHistory;
	}
	public mmlRepastHistory getPastHistory() {
		return _pastHistory;
	}
	public void setFamilyHistory(mmlRefamilyHistory _familyHistory) {
		this._familyHistory = _familyHistory;
	}
	public mmlRefamilyHistory getFamilyHistory() {
		return _familyHistory;
	}
	public void setPresentIllness(mmlRepresentIllness _presentIllness) {
		this._presentIllness = _presentIllness;
	}
	public mmlRepresentIllness getPresentIllness() {
		return _presentIllness;
	}
	public void setTestResults(mmlRetestResults _testResults) {
		this._testResults = _testResults;
	}
	public mmlRetestResults getTestResults() {
		return _testResults;
	}
	public void setClinicalCourse(mmlSmclinicalCourse _clinicalCourse) {
		this._clinicalCourse = _clinicalCourse;
	}
	public mmlSmclinicalCourse getClinicalCourse() {
		return _clinicalCourse;
	}
	public void setMedication(mmlRemedication _medication) {
		this._medication = _medication;
	}
	public mmlRemedication getMedication() {
		return _medication;
	}
	public void setReferPurpose(mmlRereferPurpose _referPurpose) {
		this._referPurpose = _referPurpose;
	}
	public mmlRereferPurpose getReferPurpose() {
		return _referPurpose;
	}
	public void setRemarks(mmlReremarks _remarks) {
		this._remarks = _remarks;
	}
	public mmlReremarks getRemarks() {
		return _remarks;
	}
	public void setReferToFacility(mmlRereferToFacility _referToFacility) {
		this._referToFacility = _referToFacility;
	}
	public mmlRereferToFacility getReferToFacility() {
		return _referToFacility;
	}
	public void setReferToPerson(mmlRereferToPerson _referToPerson) {
		this._referToPerson = _referToPerson;
	}
	public mmlRereferToPerson getReferToPerson() {
		return _referToPerson;
	}
	public void setReferToUnknownName(mmlRereferToUnknownName _referToUnknownName) {
		this._referToUnknownName = _referToUnknownName;
	}
	public mmlRereferToUnknownName getReferToUnknownName() {
		return _referToUnknownName;
	}
	
}