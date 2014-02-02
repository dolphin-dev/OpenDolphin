/**
 *
 * mmlSgsurgeryItem.java
 * Created on 2002/7/30 10:0:32
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
public class mmlSgsurgeryItem extends MMLObject {
	
	/* fields */
	private mmlSgsurgicalInfo _surgicalInfo = null;
	private mmlSgsurgicalDiagnosis _surgicalDiagnosis = null;
	private mmlSgsurgicalProcedure _surgicalProcedure = null;
	private mmlSgsurgicalStaffs _surgicalStaffs = null;
	private mmlSganesthesiaProcedure _anesthesiaProcedure = null;
	private mmlSganesthesiologists _anesthesiologists = null;
	private mmlSganesthesiaDuration _anesthesiaDuration = null;
	private mmlSgoperativeNotes _operativeNotes = null;
	private mmlSgreferenceInfo _referenceInfo = null;
	private mmlSgmemo _memo = null;
	
	public mmlSgsurgeryItem() {
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
			if ( _surgicalInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_surgicalInfo.printObject(pw, visitor);
			}
			if ( _surgicalDiagnosis != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_surgicalDiagnosis.printObject(pw, visitor);
			}
			if ( _surgicalProcedure != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_surgicalProcedure.printObject(pw, visitor);
			}
			if ( _surgicalStaffs != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_surgicalStaffs.printObject(pw, visitor);
			}
			if ( _anesthesiaProcedure != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_anesthesiaProcedure.printObject(pw, visitor);
			}
			if ( _anesthesiologists != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_anesthesiologists.printObject(pw, visitor);
			}
			if ( _anesthesiaDuration != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_anesthesiaDuration.printObject(pw, visitor);
			}
			if ( _operativeNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_operativeNotes.printObject(pw, visitor);
			}
			if ( _referenceInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referenceInfo.printObject(pw, visitor);
			}
			if ( _memo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_memo.printObject(pw, visitor);
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
		if (qName.equals("mmlSg:surgeryItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgsurgeryItem obj = new mmlSgsurgeryItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgsurgeryItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgsurgeryItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgsurgeryItem)builder.getElement()).setQName( getQName() );
			((mmlSgsurgeryItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:surgeryItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:SurgeryModule")) {
				Vector v = ((mmlSgSurgeryModule)builder.getParent()).getSurgeryItem();
				v.addElement(builder.getElement());
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
	public void setSurgicalInfo(mmlSgsurgicalInfo _surgicalInfo) {
		this._surgicalInfo = _surgicalInfo;
	}
	public mmlSgsurgicalInfo getSurgicalInfo() {
		return _surgicalInfo;
	}
	public void setSurgicalDiagnosis(mmlSgsurgicalDiagnosis _surgicalDiagnosis) {
		this._surgicalDiagnosis = _surgicalDiagnosis;
	}
	public mmlSgsurgicalDiagnosis getSurgicalDiagnosis() {
		return _surgicalDiagnosis;
	}
	public void setSurgicalProcedure(mmlSgsurgicalProcedure _surgicalProcedure) {
		this._surgicalProcedure = _surgicalProcedure;
	}
	public mmlSgsurgicalProcedure getSurgicalProcedure() {
		return _surgicalProcedure;
	}
	public void setSurgicalStaffs(mmlSgsurgicalStaffs _surgicalStaffs) {
		this._surgicalStaffs = _surgicalStaffs;
	}
	public mmlSgsurgicalStaffs getSurgicalStaffs() {
		return _surgicalStaffs;
	}
	public void setAnesthesiaProcedure(mmlSganesthesiaProcedure _anesthesiaProcedure) {
		this._anesthesiaProcedure = _anesthesiaProcedure;
	}
	public mmlSganesthesiaProcedure getAnesthesiaProcedure() {
		return _anesthesiaProcedure;
	}
	public void setAnesthesiologists(mmlSganesthesiologists _anesthesiologists) {
		this._anesthesiologists = _anesthesiologists;
	}
	public mmlSganesthesiologists getAnesthesiologists() {
		return _anesthesiologists;
	}
	public void setAnesthesiaDuration(mmlSganesthesiaDuration _anesthesiaDuration) {
		this._anesthesiaDuration = _anesthesiaDuration;
	}
	public mmlSganesthesiaDuration getAnesthesiaDuration() {
		return _anesthesiaDuration;
	}
	public void setOperativeNotes(mmlSgoperativeNotes _operativeNotes) {
		this._operativeNotes = _operativeNotes;
	}
	public mmlSgoperativeNotes getOperativeNotes() {
		return _operativeNotes;
	}
	public void setReferenceInfo(mmlSgreferenceInfo _referenceInfo) {
		this._referenceInfo = _referenceInfo;
	}
	public mmlSgreferenceInfo getReferenceInfo() {
		return _referenceInfo;
	}
	public void setMemo(mmlSgmemo _memo) {
		this._memo = _memo;
	}
	public mmlSgmemo getMemo() {
		return _memo;
	}
	
}