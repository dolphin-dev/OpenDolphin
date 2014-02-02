/**
 *
 * mmlSmoutPatientItem.java
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
public class mmlSmoutPatientItem extends MMLObject {
	
	/* fields */
	private mmlSmdate _date = null;
	private mmlSmoutPatientCondition _outPatientCondition = null;
	private mmlSmstaffs _staffs = null;
	
	public mmlSmoutPatientItem() {
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
			if ( _date != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_date.printObject(pw, visitor);
			}
			if ( _outPatientCondition != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_outPatientCondition.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:outPatientItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmoutPatientItem obj = new mmlSmoutPatientItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmoutPatientItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmoutPatientItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmoutPatientItem)builder.getElement()).setQName( getQName() );
			((mmlSmoutPatientItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:outPatientItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:outPatient")) {
				Vector v = ((mmlSmoutPatient)builder.getParent()).getOutPatientItem();
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
	public void setDate(mmlSmdate _date) {
		this._date = _date;
	}
	public mmlSmdate getDate() {
		return _date;
	}
	public void setOutPatientCondition(mmlSmoutPatientCondition _outPatientCondition) {
		this._outPatientCondition = _outPatientCondition;
	}
	public mmlSmoutPatientCondition getOutPatientCondition() {
		return _outPatientCondition;
	}
	public void setStaffs(mmlSmstaffs _staffs) {
		this._staffs = _staffs;
	}
	public mmlSmstaffs getStaffs() {
		return _staffs;
	}
	
}