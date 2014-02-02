/**
 *
 * mmlaccessRight.java
 * Created on 2003/1/4 2:29:56
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
public class mmlaccessRight extends MMLObject {
	
	/* fields */
	private String __permit = null;
	private String __startDate = null;
	private String __endDate = null;

	private mmlScfacility _facility = null;
	private mmlScperson _person = null;
	private mmlSclicense _license = null;
	private mmlScdepartment _department = null;
	
	public mmlaccessRight() {
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

			if ( this.getLocalName().equals("levelone") ) {
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
		if (qName.equals("mml:accessRight") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlaccessRight obj = new mmlaccessRight();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlaccessRight)builder.getElement()).setNamespace( getNamespace() );
			((mmlaccessRight)builder.getElement()).setLocalName( getLocalName() );
			((mmlaccessRight)builder.getElement()).setQName( getQName() );
			((mmlaccessRight)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("permit") ) {
						set__permit( atts.getValue(i) );
						((mmlaccessRight)builder.getElement()).set__permit( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("startDate") ) {
						set__startDate( atts.getValue(i) );
						((mmlaccessRight)builder.getElement()).set__startDate( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("endDate") ) {
						set__endDate( atts.getValue(i) );
						((mmlaccessRight)builder.getElement()).set__endDate( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:accessRight") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:securityLevel")) {
				Vector v = ((mmlsecurityLevel)builder.getParent()).get_accessRight();
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
	public void set__permit(String __permit) {
		this.__permit = __permit;
	}
	public String get__permit() {
		return __permit;
	}
	public void set__startDate(String __startDate) {
		this.__startDate = __startDate;
	}
	public String get__startDate() {
		return __startDate;
	}
	public void set__endDate(String __endDate) {
		this.__endDate = __endDate;
	}
	public String get__endDate() {
		return __endDate;
	}

	public void set_facility(mmlScfacility _facility) {
		this._facility = _facility;
	}
	public mmlScfacility get_facility() {
		return _facility;
	}
	public void set_person(mmlScperson _person) {
		this._person = _person;
	}
	public mmlScperson get_person() {
		return _person;
	}
	public void set_license(mmlSclicense _license) {
		this._license = _license;
	}
	public mmlSclicense get_license() {
		return _license;
	}
	public void set_department(mmlScdepartment _department) {
		this._department = _department;
	}
	public mmlScdepartment get_department() {
		return _department;
	}
	
}