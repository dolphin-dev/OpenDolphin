/**
 *
 * mmlSgsurgicalInfo.java
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
public class mmlSgsurgicalInfo extends MMLObject {
	
	/* fields */
	private String __mmlSgtype = null;

	private mmlSgdate _date = null;
	private mmlSgstartTime _startTime = null;
	private mmlSgduration _duration = null;
	private mmlSgsurgicalDepartment _surgicalDepartment = null;
	private mmlSgpatientDepartment _patientDepartment = null;
	
	public mmlSgsurgicalInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSgtype != null ) pw.print(" " + "mmlSg:type" +  "=" + "'" + __mmlSgtype + "'");

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
			if ( _startTime != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_startTime.printObject(pw, visitor);
			}
			if ( _duration != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_duration.printObject(pw, visitor);
			}
			if ( _surgicalDepartment != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_surgicalDepartment.printObject(pw, visitor);
			}
			if ( _patientDepartment != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patientDepartment.printObject(pw, visitor);
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
		if (qName.equals("mmlSg:surgicalInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgsurgicalInfo obj = new mmlSgsurgicalInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgsurgicalInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgsurgicalInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgsurgicalInfo)builder.getElement()).setQName( getQName() );
			((mmlSgsurgicalInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlSgtype( atts.getValue(namespaceURI, "type") );
				((mmlSgsurgicalInfo)builder.getElement()).setMmlSgtype( atts.getValue(namespaceURI, "type") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:surgicalInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgeryItem")) {
				((mmlSgsurgeryItem)builder.getParent()).setSurgicalInfo((mmlSgsurgicalInfo)builder.getElement());
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
	public void setMmlSgtype(String __mmlSgtype) {
		this.__mmlSgtype = __mmlSgtype;
	}
	public String getMmlSgtype() {
		return __mmlSgtype;
	}

	public void setDate(mmlSgdate _date) {
		this._date = _date;
	}
	public mmlSgdate getDate() {
		return _date;
	}
	public void setStartTime(mmlSgstartTime _startTime) {
		this._startTime = _startTime;
	}
	public mmlSgstartTime getStartTime() {
		return _startTime;
	}
	public void setDuration(mmlSgduration _duration) {
		this._duration = _duration;
	}
	public mmlSgduration getDuration() {
		return _duration;
	}
	public void setSurgicalDepartment(mmlSgsurgicalDepartment _surgicalDepartment) {
		this._surgicalDepartment = _surgicalDepartment;
	}
	public mmlSgsurgicalDepartment getSurgicalDepartment() {
		return _surgicalDepartment;
	}
	public void setPatientDepartment(mmlSgpatientDepartment _patientDepartment) {
		this._patientDepartment = _patientDepartment;
	}
	public mmlSgpatientDepartment getPatientDepartment() {
		return _patientDepartment;
	}
	
}