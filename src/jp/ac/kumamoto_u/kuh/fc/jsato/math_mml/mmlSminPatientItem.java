/**
 *
 * mmlSminPatientItem.java
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
public class mmlSminPatientItem extends MMLObject {
	
	/* fields */
	private mmlSmadmission _admission = null;
	private mmlSmdischarge _discharge = null;
	private mmlSmstaffs _staffs = null;
	
	public mmlSminPatientItem() {
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
			if ( _admission != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_admission.printObject(pw, visitor);
			}
			if ( _discharge != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_discharge.printObject(pw, visitor);
			}
			if ( _staffs != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_staffs.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:inPatientItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSminPatientItem obj = new mmlSminPatientItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSminPatientItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlSminPatientItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlSminPatientItem)builder.getElement()).setQName( getQName() );
			((mmlSminPatientItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:inPatientItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:inPatient")) {
				Vector v = ((mmlSminPatient)builder.getParent()).getInPatientItem();
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
	public void setAdmission(mmlSmadmission _admission) {
		this._admission = _admission;
	}
	public mmlSmadmission getAdmission() {
		return _admission;
	}
	public void setDischarge(mmlSmdischarge _discharge) {
		this._discharge = _discharge;
	}
	public mmlSmdischarge getDischarge() {
		return _discharge;
	}
	public void setStaffs(mmlSmstaffs _staffs) {
		this._staffs = _staffs;
	}
	public mmlSmstaffs getStaffs() {
		return _staffs;
	}
	
}