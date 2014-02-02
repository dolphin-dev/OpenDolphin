/**
 *
 * mmlSmSummaryModule.java
 * Created on 2003/1/4 2:30:9
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
public class mmlSmSummaryModule extends MMLObject {
	
	/* fields */
	private mmlSmserviceHistory _serviceHistory = null;
	private Vector _RegisteredDiagnosisModule = new Vector();
	private mmlSmdeathInfo _deathInfo = null;
	private Vector _SurgeryModule = new Vector();
	private mmlSmchiefComplaints _chiefComplaints = null;
	private mmlSmpatientProfile _patientProfile = null;
	private mmlSmhistory _history = null;
	private mmlSmphysicalExam _physicalExam = null;
	private mmlSmclinicalCourse _clinicalCourse = null;
	private mmlSmdischargeFindings _dischargeFindings = null;
	private mmlSmmedication _medication = null;
	private mmlSmtestResults _testResults = null;
	private mmlSmplan _plan = null;
	private mmlSmremarks _remarks = null;
	
	public mmlSmSummaryModule() {
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
			if ( _serviceHistory != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_serviceHistory.printObject(pw, visitor);
			}
			if (this._RegisteredDiagnosisModule != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._RegisteredDiagnosisModule.size(); ++i ) {
					((mmlRdRegisteredDiagnosisModule)this._RegisteredDiagnosisModule.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _deathInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_deathInfo.printObject(pw, visitor);
			}
			if (this._SurgeryModule != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._SurgeryModule.size(); ++i ) {
					((mmlSgSurgeryModule)this._SurgeryModule.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _chiefComplaints != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_chiefComplaints.printObject(pw, visitor);
			}
			if ( _patientProfile != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patientProfile.printObject(pw, visitor);
			}
			if ( _history != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_history.printObject(pw, visitor);
			}
			if ( _physicalExam != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_physicalExam.printObject(pw, visitor);
			}
			if ( _clinicalCourse != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_clinicalCourse.printObject(pw, visitor);
			}
			if ( _dischargeFindings != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_dischargeFindings.printObject(pw, visitor);
			}
			if ( _medication != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_medication.printObject(pw, visitor);
			}
			if ( _testResults != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testResults.printObject(pw, visitor);
			}
			if ( _plan != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_plan.printObject(pw, visitor);
			}
			if ( _remarks != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_remarks.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:SummaryModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmSummaryModule obj = new mmlSmSummaryModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmSummaryModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmSummaryModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmSummaryModule)builder.getElement()).setQName( getQName() );
			((mmlSmSummaryModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:SummaryModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlSmSummaryModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_SummaryModule((mmlSmSummaryModule)builder.getElement());
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
	public void set_serviceHistory(mmlSmserviceHistory _serviceHistory) {
		this._serviceHistory = _serviceHistory;
	}
	public mmlSmserviceHistory get_serviceHistory() {
		return _serviceHistory;
	}
	public void set_RegisteredDiagnosisModule(Vector _RegisteredDiagnosisModule) {
		if (this._RegisteredDiagnosisModule != null) this._RegisteredDiagnosisModule.removeAllElements();
		// copy entire elements in the vector
		this._RegisteredDiagnosisModule = new Vector();
		for (int i = 0; i < _RegisteredDiagnosisModule.size(); ++i) {
			this._RegisteredDiagnosisModule.addElement( _RegisteredDiagnosisModule.elementAt(i) );
		}
	}
	public Vector get_RegisteredDiagnosisModule() {
		return _RegisteredDiagnosisModule;
	}
	public void set_deathInfo(mmlSmdeathInfo _deathInfo) {
		this._deathInfo = _deathInfo;
	}
	public mmlSmdeathInfo get_deathInfo() {
		return _deathInfo;
	}
	public void set_SurgeryModule(Vector _SurgeryModule) {
		if (this._SurgeryModule != null) this._SurgeryModule.removeAllElements();
		// copy entire elements in the vector
		this._SurgeryModule = new Vector();
		for (int i = 0; i < _SurgeryModule.size(); ++i) {
			this._SurgeryModule.addElement( _SurgeryModule.elementAt(i) );
		}
	}
	public Vector get_SurgeryModule() {
		return _SurgeryModule;
	}
	public void set_chiefComplaints(mmlSmchiefComplaints _chiefComplaints) {
		this._chiefComplaints = _chiefComplaints;
	}
	public mmlSmchiefComplaints get_chiefComplaints() {
		return _chiefComplaints;
	}
	public void set_patientProfile(mmlSmpatientProfile _patientProfile) {
		this._patientProfile = _patientProfile;
	}
	public mmlSmpatientProfile get_patientProfile() {
		return _patientProfile;
	}
	public void set_history(mmlSmhistory _history) {
		this._history = _history;
	}
	public mmlSmhistory get_history() {
		return _history;
	}
	public void set_physicalExam(mmlSmphysicalExam _physicalExam) {
		this._physicalExam = _physicalExam;
	}
	public mmlSmphysicalExam get_physicalExam() {
		return _physicalExam;
	}
	public void set_clinicalCourse(mmlSmclinicalCourse _clinicalCourse) {
		this._clinicalCourse = _clinicalCourse;
	}
	public mmlSmclinicalCourse get_clinicalCourse() {
		return _clinicalCourse;
	}
	public void set_dischargeFindings(mmlSmdischargeFindings _dischargeFindings) {
		this._dischargeFindings = _dischargeFindings;
	}
	public mmlSmdischargeFindings get_dischargeFindings() {
		return _dischargeFindings;
	}
	public void set_medication(mmlSmmedication _medication) {
		this._medication = _medication;
	}
	public mmlSmmedication get_medication() {
		return _medication;
	}
	public void set_testResults(mmlSmtestResults _testResults) {
		this._testResults = _testResults;
	}
	public mmlSmtestResults get_testResults() {
		return _testResults;
	}
	public void set_plan(mmlSmplan _plan) {
		this._plan = _plan;
	}
	public mmlSmplan get_plan() {
		return _plan;
	}
	public void set_remarks(mmlSmremarks _remarks) {
		this._remarks = _remarks;
	}
	public mmlSmremarks get_remarks() {
		return _remarks;
	}
	
}