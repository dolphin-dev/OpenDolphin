/**
 *
 * mmlRdRegisteredDiagnosisModule.java
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
public class mmlRdRegisteredDiagnosisModule extends MMLObject {
	
	/* fields */
	private mmlRddiagnosis _diagnosis = null;
	private mmlRddiagnosisContents _diagnosisContents = null;
	private mmlRdcategories _categories = null;
	private mmlRdstartDate _startDate = null;
	private mmlRdendDate _endDate = null;
	private mmlRdoutcome _outcome = null;
	private mmlRdfirstEncounterDate _firstEncounterDate = null;
	private mmlRdrelatedHealthInsurance _relatedHealthInsurance = null;
	
	public mmlRdRegisteredDiagnosisModule() {
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
			if ( _diagnosis != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_diagnosis.printObject(pw, visitor);
			}
			if ( _diagnosisContents != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_diagnosisContents.printObject(pw, visitor);
			}
			if ( _categories != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_categories.printObject(pw, visitor);
			}
			if ( _startDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_startDate.printObject(pw, visitor);
			}
			if ( _endDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_endDate.printObject(pw, visitor);
			}
			if ( _outcome != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_outcome.printObject(pw, visitor);
			}
			if ( _firstEncounterDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_firstEncounterDate.printObject(pw, visitor);
			}
			if ( _relatedHealthInsurance != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_relatedHealthInsurance.printObject(pw, visitor);
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
		if (qName.equals("mmlRd:RegisteredDiagnosisModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRdRegisteredDiagnosisModule obj = new mmlRdRegisteredDiagnosisModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRdRegisteredDiagnosisModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlRdRegisteredDiagnosisModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlRdRegisteredDiagnosisModule)builder.getElement()).setQName( getQName() );
			((mmlRdRegisteredDiagnosisModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRd:RegisteredDiagnosisModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:SummaryModule")) {
				Vector v = ((mmlSmSummaryModule)builder.getParent()).getRegisteredDiagnosisModule();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:surgicalDiagnosis")) {
				Vector v = ((mmlSgsurgicalDiagnosis)builder.getParent()).getRegisteredDiagnosisModule();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlFcl:familyHistoryItem")) {
				((mmlFclfamilyHistoryItem)builder.getParent()).setRegisteredDiagnosisModule((mmlRdRegisteredDiagnosisModule)builder.getElement());
			}

			if (parentElement.getQName().equals("content")) {
				((content)builder.getParent()).setRegisteredDiagnosisModule((mmlRdRegisteredDiagnosisModule)builder.getElement());
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
	public void setDiagnosis(mmlRddiagnosis _diagnosis) {
		this._diagnosis = _diagnosis;
	}
	public mmlRddiagnosis getDiagnosis() {
		return _diagnosis;
	}
	public void setDiagnosisContents(mmlRddiagnosisContents _diagnosisContents) {
		this._diagnosisContents = _diagnosisContents;
	}
	public mmlRddiagnosisContents getDiagnosisContents() {
		return _diagnosisContents;
	}
	public void setCategories(mmlRdcategories _categories) {
		this._categories = _categories;
	}
	public mmlRdcategories getCategories() {
		return _categories;
	}
	public void setStartDate(mmlRdstartDate _startDate) {
		this._startDate = _startDate;
	}
	public mmlRdstartDate getStartDate() {
		return _startDate;
	}
	public void setEndDate(mmlRdendDate _endDate) {
		this._endDate = _endDate;
	}
	public mmlRdendDate getEndDate() {
		return _endDate;
	}
	public void setOutcome(mmlRdoutcome _outcome) {
		this._outcome = _outcome;
	}
	public mmlRdoutcome getOutcome() {
		return _outcome;
	}
	public void setFirstEncounterDate(mmlRdfirstEncounterDate _firstEncounterDate) {
		this._firstEncounterDate = _firstEncounterDate;
	}
	public mmlRdfirstEncounterDate getFirstEncounterDate() {
		return _firstEncounterDate;
	}
	public void setRelatedHealthInsurance(mmlRdrelatedHealthInsurance _relatedHealthInsurance) {
		this._relatedHealthInsurance = _relatedHealthInsurance;
	}
	public mmlRdrelatedHealthInsurance getRelatedHealthInsurance() {
		return _relatedHealthInsurance;
	}
	
}