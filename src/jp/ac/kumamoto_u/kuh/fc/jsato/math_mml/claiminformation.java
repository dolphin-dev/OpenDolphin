/**
 *
 * claiminformation.java
 * Created on 2002/7/30 10:0:39
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
public class claiminformation extends MMLObject {
	
	/* fields */
	private String __claimstatus = null;
	private String __claimorderTime = null;
	private String __claimappointTime = null;
	private String __claimregistTime = null;
	private String __claimperformTime = null;
	private String __claimadmitFlag = null;
	private String __claimtimeClass = null;
	private String __claiminsuranceUid = null;
	private String __claimdefaultTableId = null;

	private claimappoint _appoint = null;
	private claimpatientDepartment _patientDepartment = null;
	private claimpatientWard _patientWard = null;
	private mmlHiinsuranceClass _insuranceClass = null;
	
	public claiminformation() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimstatus != null ) pw.print(" " + "claim:status" +  "=" + "'" + __claimstatus + "'");
			if ( __claimorderTime != null ) pw.print(" " + "claim:orderTime" +  "=" + "'" + __claimorderTime + "'");
			if ( __claimappointTime != null ) pw.print(" " + "claim:appointTime" +  "=" + "'" + __claimappointTime + "'");
			if ( __claimregistTime != null ) pw.print(" " + "claim:registTime" +  "=" + "'" + __claimregistTime + "'");
			if ( __claimperformTime != null ) pw.print(" " + "claim:performTime" +  "=" + "'" + __claimperformTime + "'");
			if ( __claimadmitFlag != null ) pw.print(" " + "claim:admitFlag" +  "=" + "'" + __claimadmitFlag + "'");
			if ( __claimtimeClass != null ) pw.print(" " + "claim:timeClass" +  "=" + "'" + __claimtimeClass + "'");
			if ( __claiminsuranceUid != null ) pw.print(" " + "claim:insuranceUid" +  "=" + "'" + __claiminsuranceUid + "'");
			if ( __claimdefaultTableId != null ) pw.print(" " + "claim:defaultTableId" +  "=" + "'" + __claimdefaultTableId + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _appoint != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_appoint.printObject(pw, visitor);
			}
			if ( _patientDepartment != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patientDepartment.printObject(pw, visitor);
			}
			if ( _patientWard != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_patientWard.printObject(pw, visitor);
			}
			if ( _insuranceClass != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_insuranceClass.printObject(pw, visitor);
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
		if (qName.equals("claim:information") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claiminformation obj = new claiminformation();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claiminformation)builder.getElement()).setNamespace( getNamespace() );
			((claiminformation)builder.getElement()).setLocalName( getLocalName() );
			((claiminformation)builder.getElement()).setQName( getQName() );
			((claiminformation)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimstatus( atts.getValue(namespaceURI, "status") );
				((claiminformation)builder.getElement()).setClaimstatus( atts.getValue(namespaceURI, "status") );
				setClaimorderTime( atts.getValue(namespaceURI, "orderTime") );
				((claiminformation)builder.getElement()).setClaimorderTime( atts.getValue(namespaceURI, "orderTime") );
				setClaimappointTime( atts.getValue(namespaceURI, "appointTime") );
				((claiminformation)builder.getElement()).setClaimappointTime( atts.getValue(namespaceURI, "appointTime") );
				setClaimregistTime( atts.getValue(namespaceURI, "registTime") );
				((claiminformation)builder.getElement()).setClaimregistTime( atts.getValue(namespaceURI, "registTime") );
				setClaimperformTime( atts.getValue(namespaceURI, "performTime") );
				((claiminformation)builder.getElement()).setClaimperformTime( atts.getValue(namespaceURI, "performTime") );
				setClaimadmitFlag( atts.getValue(namespaceURI, "admitFlag") );
				((claiminformation)builder.getElement()).setClaimadmitFlag( atts.getValue(namespaceURI, "admitFlag") );
				setClaimtimeClass( atts.getValue(namespaceURI, "timeClass") );
				((claiminformation)builder.getElement()).setClaimtimeClass( atts.getValue(namespaceURI, "timeClass") );
				setClaiminsuranceUid( atts.getValue(namespaceURI, "insuranceUid") );
				((claiminformation)builder.getElement()).setClaiminsuranceUid( atts.getValue(namespaceURI, "insuranceUid") );
				setClaimdefaultTableId( atts.getValue(namespaceURI, "defaultTableId") );
				((claiminformation)builder.getElement()).setClaimdefaultTableId( atts.getValue(namespaceURI, "defaultTableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:information") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:ClaimModule")) {
				((claimClaimModule)builder.getParent()).setInformation((claiminformation)builder.getElement());
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
	public void setClaimstatus(String __claimstatus) {
		this.__claimstatus = __claimstatus;
	}
	public String getClaimstatus() {
		return __claimstatus;
	}
	public void setClaimorderTime(String __claimorderTime) {
		this.__claimorderTime = __claimorderTime;
	}
	public String getClaimorderTime() {
		return __claimorderTime;
	}
	public void setClaimappointTime(String __claimappointTime) {
		this.__claimappointTime = __claimappointTime;
	}
	public String getClaimappointTime() {
		return __claimappointTime;
	}
	public void setClaimregistTime(String __claimregistTime) {
		this.__claimregistTime = __claimregistTime;
	}
	public String getClaimregistTime() {
		return __claimregistTime;
	}
	public void setClaimperformTime(String __claimperformTime) {
		this.__claimperformTime = __claimperformTime;
	}
	public String getClaimperformTime() {
		return __claimperformTime;
	}
	public void setClaimadmitFlag(String __claimadmitFlag) {
		this.__claimadmitFlag = __claimadmitFlag;
	}
	public String getClaimadmitFlag() {
		return __claimadmitFlag;
	}
	public void setClaimtimeClass(String __claimtimeClass) {
		this.__claimtimeClass = __claimtimeClass;
	}
	public String getClaimtimeClass() {
		return __claimtimeClass;
	}
	public void setClaiminsuranceUid(String __claiminsuranceUid) {
		this.__claiminsuranceUid = __claiminsuranceUid;
	}
	public String getClaiminsuranceUid() {
		return __claiminsuranceUid;
	}
	public void setClaimdefaultTableId(String __claimdefaultTableId) {
		this.__claimdefaultTableId = __claimdefaultTableId;
	}
	public String getClaimdefaultTableId() {
		return __claimdefaultTableId;
	}

	public void setAppoint(claimappoint _appoint) {
		this._appoint = _appoint;
	}
	public claimappoint getAppoint() {
		return _appoint;
	}
	public void setPatientDepartment(claimpatientDepartment _patientDepartment) {
		this._patientDepartment = _patientDepartment;
	}
	public claimpatientDepartment getPatientDepartment() {
		return _patientDepartment;
	}
	public void setPatientWard(claimpatientWard _patientWard) {
		this._patientWard = _patientWard;
	}
	public claimpatientWard getPatientWard() {
		return _patientWard;
	}
	public void setInsuranceClass(mmlHiinsuranceClass _insuranceClass) {
		this._insuranceClass = _insuranceClass;
	}
	public mmlHiinsuranceClass getInsuranceClass() {
		return _insuranceClass;
	}
	
}