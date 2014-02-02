/**
 *
 * mmlPcobjective.java
 * Created on 2003/1/4 2:30:6
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
public class mmlPcobjective extends MMLObject {
	
	/* fields */
	private mmlPcobjectiveNotes _objectiveNotes = null;
	private mmlPcphysicalExam _physicalExam = null;
	private mmlPctestResult _testResult = null;
	private mmlPcrxRecord _rxRecord = null;
	private mmlPctxRecord _txRecord = null;
	
	public mmlPcobjective() {
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
			if ( _objectiveNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_objectiveNotes.printObject(pw, visitor);
			}
			if ( _physicalExam != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_physicalExam.printObject(pw, visitor);
			}
			if ( _testResult != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testResult.printObject(pw, visitor);
			}
			if ( _rxRecord != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_rxRecord.printObject(pw, visitor);
			}
			if ( _txRecord != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_txRecord.printObject(pw, visitor);
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
		if (qName.equals("mmlPc:objective") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcobjective obj = new mmlPcobjective();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcobjective)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcobjective)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcobjective)builder.getElement()).setQName( getQName() );
			((mmlPcobjective)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:objective") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:problemItem")) {
				((mmlPcproblemItem)builder.getParent()).set_objective((mmlPcobjective)builder.getElement());
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
	public void set_objectiveNotes(mmlPcobjectiveNotes _objectiveNotes) {
		this._objectiveNotes = _objectiveNotes;
	}
	public mmlPcobjectiveNotes get_objectiveNotes() {
		return _objectiveNotes;
	}
	public void set_physicalExam(mmlPcphysicalExam _physicalExam) {
		this._physicalExam = _physicalExam;
	}
	public mmlPcphysicalExam get_physicalExam() {
		return _physicalExam;
	}
	public void set_testResult(mmlPctestResult _testResult) {
		this._testResult = _testResult;
	}
	public mmlPctestResult get_testResult() {
		return _testResult;
	}
	public void set_rxRecord(mmlPcrxRecord _rxRecord) {
		this._rxRecord = _rxRecord;
	}
	public mmlPcrxRecord get_rxRecord() {
		return _rxRecord;
	}
	public void set_txRecord(mmlPctxRecord _txRecord) {
		this._txRecord = _txRecord;
	}
	public mmlPctxRecord get_txRecord() {
		return _txRecord;
	}
	
}