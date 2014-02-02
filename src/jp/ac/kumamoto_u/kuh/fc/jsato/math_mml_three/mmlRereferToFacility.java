/**
 *
 * mmlRereferToFacility.java
 * Created on 2003/1/4 2:30:25
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
public class mmlRereferToFacility extends MMLObject {
	
	/* fields */
	private mmlFcFacility _Facility = null;
	private mmlDpDepartment _Department = null;
	
	public mmlRereferToFacility() {
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
			if ( _Facility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Facility.printObject(pw, visitor);
			}
			if ( _Department != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Department.printObject(pw, visitor);
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
		if (qName.equals("mmlRe:referToFacility") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRereferToFacility obj = new mmlRereferToFacility();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRereferToFacility)builder.getElement()).setNamespace( getNamespace() );
			((mmlRereferToFacility)builder.getElement()).setLocalName( getLocalName() );
			((mmlRereferToFacility)builder.getElement()).setQName( getQName() );
			((mmlRereferToFacility)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRe:referToFacility") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRe:ReferralModule")) {
				((mmlReReferralModule)builder.getParent()).set_referToFacility((mmlRereferToFacility)builder.getElement());
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
	public void set_Facility(mmlFcFacility _Facility) {
		this._Facility = _Facility;
	}
	public mmlFcFacility get_Facility() {
		return _Facility;
	}
	public void set_Department(mmlDpDepartment _Department) {
		this._Department = _Department;
	}
	public mmlDpDepartment get_Department() {
		return _Department;
	}
	
}