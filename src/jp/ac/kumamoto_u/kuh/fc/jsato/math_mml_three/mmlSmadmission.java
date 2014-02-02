/**
 *
 * mmlSmadmission.java
 * Created on 2003/1/4 2:30:10
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
public class mmlSmadmission extends MMLObject {
	
	/* fields */
	private mmlSmdate _date = null;
	private mmlSmadmissionCondition _admissionCondition = null;
	private mmlSmreferFrom _referFrom = null;
	
	public mmlSmadmission() {
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
			if ( _date != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_date.printObject(pw, visitor);
			}
			if ( _admissionCondition != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_admissionCondition.printObject(pw, visitor);
			}
			if ( _referFrom != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referFrom.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:admission") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmadmission obj = new mmlSmadmission();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmadmission)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmadmission)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmadmission)builder.getElement()).setQName( getQName() );
			((mmlSmadmission)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:admission") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:inPatientItem")) {
				((mmlSminPatientItem)builder.getParent()).set_admission((mmlSmadmission)builder.getElement());
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
	public void set_date(mmlSmdate _date) {
		this._date = _date;
	}
	public mmlSmdate get_date() {
		return _date;
	}
	public void set_admissionCondition(mmlSmadmissionCondition _admissionCondition) {
		this._admissionCondition = _admissionCondition;
	}
	public mmlSmadmissionCondition get_admissionCondition() {
		return _admissionCondition;
	}
	public void set_referFrom(mmlSmreferFrom _referFrom) {
		this._referFrom = _referFrom;
	}
	public mmlSmreferFrom get_referFrom() {
		return _referFrom;
	}
	
}