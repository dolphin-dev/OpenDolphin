/**
 *
 * accessRight.java
 * Created on 2002/7/30 10:0:25
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import java.util.*;
import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class accessRight extends MMLObject {
	
	/* fields */
	private String __permit = null;
	private String __startDate = null;
	private String __endDate = null;

	private mmlScfacility _facility = null;
	private mmlScperson _person = null;
	private mmlSclicense _license = null;
	private mmlScdepartment _department = null;
	
	public accessRight() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __permit != null ) pw.print(" " + "permit" +  "=" + "'" + __permit + "'");
			if ( __startDate != null ) pw.print(" " + "startDate" +  "=" + "'" + __startDate + "'");
			if ( __endDate != null ) pw.print(" " + "endDate" +  "=" + "'" + __endDate + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _facility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_facility.printObject(pw, visitor);
			}
			if ( _person != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_person.printObject(pw, visitor);
			}
			if ( _license != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_license.printObject(pw, visitor);
			}
			if ( _department != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_department.printObject(pw, visitor);
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
		if (qName.equals("accessRight") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			accessRight obj = new accessRight();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((accessRight)builder.getElement()).setNamespace( getNamespace() );
			((accessRight)builder.getElement()).setLocalName( getLocalName() );
			((accessRight)builder.getElement()).setQName( getQName() );
			((accessRight)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setPermit( atts.getValue(namespaceURI, "permit") );
				((accessRight)builder.getElement()).setPermit( atts.getValue(namespaceURI, "permit") );
				setStartDate( atts.getValue(namespaceURI, "startDate") );
				((accessRight)builder.getElement()).setStartDate( atts.getValue(namespaceURI, "startDate") );
				setEndDate( atts.getValue(namespaceURI, "endDate") );
				((accessRight)builder.getElement()).setEndDate( atts.getValue(namespaceURI, "endDate") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("accessRight") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("securityLevel")) {
				Vector v = ((securityLevel)builder.getParent()).getAccessRight();
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
	public void setPermit(String __permit) {
		this.__permit = __permit;
	}
	public String getPermit() {
		return __permit;
	}
	public void setStartDate(String __startDate) {
		this.__startDate = __startDate;
	}
	public String getStartDate() {
		return __startDate;
	}
	public void setEndDate(String __endDate) {
		this.__endDate = __endDate;
	}
	public String getEndDate() {
		return __endDate;
	}

	public void setFacility(mmlScfacility _facility) {
		this._facility = _facility;
	}
	public mmlScfacility getFacility() {
		return _facility;
	}
	public void setPerson(mmlScperson _person) {
		this._person = _person;
	}
	public mmlScperson getPerson() {
		return _person;
	}
	public void setLicense(mmlSclicense _license) {
		this._license = _license;
	}
	public mmlSclicense getLicense() {
		return _license;
	}
	public void setDepartment(mmlScdepartment _department) {
		this._department = _department;
	}
	public mmlScdepartment getDepartment() {
		return _department;
	}
	
}