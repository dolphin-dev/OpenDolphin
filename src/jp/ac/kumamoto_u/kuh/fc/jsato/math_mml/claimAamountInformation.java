/**
 *
 * claimAamountInformation.java
 * Created on 2002/7/30 10:0:40
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class claimAamountInformation extends MMLObject {
	
	/* fields */
	private String __claimAstatus = null;
	private String __claimAoderTime = null;
	private String __claimAappointTime = null;
	private String __claimAregistTime = null;
	private String __claimAperformTime = null;
	private String __claimAaccountTime = null;
	private String __claimAadmitFlag = null;
	private String __claimAtimeClass = null;
	private String __claimAinsuranceUid = null;
	private String __claimAdefaultTableId = null;

	private claimApatientDepartment _patientDepartment = null;
	private claimApatientWard _patientWard = null;
	private mmlHiinsuranceClass _insuranceClass = null;
	
	public claimAamountInformation() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAstatus != null ) pw.print(" " + "claimA:status" +  "=" + "'" + __claimAstatus + "'");
			if ( __claimAoderTime != null ) pw.print(" " + "claimA:oderTime" +  "=" + "'" + __claimAoderTime + "'");
			if ( __claimAappointTime != null ) pw.print(" " + "claimA:appointTime" +  "=" + "'" + __claimAappointTime + "'");
			if ( __claimAregistTime != null ) pw.print(" " + "claimA:registTime" +  "=" + "'" + __claimAregistTime + "'");
			if ( __claimAperformTime != null ) pw.print(" " + "claimA:performTime" +  "=" + "'" + __claimAperformTime + "'");
			if ( __claimAaccountTime != null ) pw.print(" " + "claimA:accountTime" +  "=" + "'" + __claimAaccountTime + "'");
			if ( __claimAadmitFlag != null ) pw.print(" " + "claimA:admitFlag" +  "=" + "'" + __claimAadmitFlag + "'");
			if ( __claimAtimeClass != null ) pw.print(" " + "claimA:timeClass" +  "=" + "'" + __claimAtimeClass + "'");
			if ( __claimAinsuranceUid != null ) pw.print(" " + "claimA:insuranceUid" +  "=" + "'" + __claimAinsuranceUid + "'");
			if ( __claimAdefaultTableId != null ) pw.print(" " + "claimA:defaultTableId" +  "=" + "'" + __claimAdefaultTableId + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
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
		if (qName.equals("claimA:amountInformation") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAamountInformation obj = new claimAamountInformation();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAamountInformation)builder.getElement()).setNamespace( getNamespace() );
			((claimAamountInformation)builder.getElement()).setLocalName( getLocalName() );
			((claimAamountInformation)builder.getElement()).setQName( getQName() );
			((claimAamountInformation)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimAstatus( atts.getValue(namespaceURI, "status") );
				((claimAamountInformation)builder.getElement()).setClaimAstatus( atts.getValue(namespaceURI, "status") );
				setClaimAoderTime( atts.getValue(namespaceURI, "oderTime") );
				((claimAamountInformation)builder.getElement()).setClaimAoderTime( atts.getValue(namespaceURI, "oderTime") );
				setClaimAappointTime( atts.getValue(namespaceURI, "appointTime") );
				((claimAamountInformation)builder.getElement()).setClaimAappointTime( atts.getValue(namespaceURI, "appointTime") );
				setClaimAregistTime( atts.getValue(namespaceURI, "registTime") );
				((claimAamountInformation)builder.getElement()).setClaimAregistTime( atts.getValue(namespaceURI, "registTime") );
				setClaimAperformTime( atts.getValue(namespaceURI, "performTime") );
				((claimAamountInformation)builder.getElement()).setClaimAperformTime( atts.getValue(namespaceURI, "performTime") );
				setClaimAaccountTime( atts.getValue(namespaceURI, "accountTime") );
				((claimAamountInformation)builder.getElement()).setClaimAaccountTime( atts.getValue(namespaceURI, "accountTime") );
				setClaimAadmitFlag( atts.getValue(namespaceURI, "admitFlag") );
				((claimAamountInformation)builder.getElement()).setClaimAadmitFlag( atts.getValue(namespaceURI, "admitFlag") );
				setClaimAtimeClass( atts.getValue(namespaceURI, "timeClass") );
				((claimAamountInformation)builder.getElement()).setClaimAtimeClass( atts.getValue(namespaceURI, "timeClass") );
				setClaimAinsuranceUid( atts.getValue(namespaceURI, "insuranceUid") );
				((claimAamountInformation)builder.getElement()).setClaimAinsuranceUid( atts.getValue(namespaceURI, "insuranceUid") );
				setClaimAdefaultTableId( atts.getValue(namespaceURI, "defaultTableId") );
				((claimAamountInformation)builder.getElement()).setClaimAdefaultTableId( atts.getValue(namespaceURI, "defaultTableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:amountInformation") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:ClaimAmountModule")) {
				((claimAClaimAmountModule)builder.getParent()).setAmountInformation((claimAamountInformation)builder.getElement());
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
	public void setClaimAstatus(String __claimAstatus) {
		this.__claimAstatus = __claimAstatus;
	}
	public String getClaimAstatus() {
		return __claimAstatus;
	}
	public void setClaimAoderTime(String __claimAoderTime) {
		this.__claimAoderTime = __claimAoderTime;
	}
	public String getClaimAoderTime() {
		return __claimAoderTime;
	}
	public void setClaimAappointTime(String __claimAappointTime) {
		this.__claimAappointTime = __claimAappointTime;
	}
	public String getClaimAappointTime() {
		return __claimAappointTime;
	}
	public void setClaimAregistTime(String __claimAregistTime) {
		this.__claimAregistTime = __claimAregistTime;
	}
	public String getClaimAregistTime() {
		return __claimAregistTime;
	}
	public void setClaimAperformTime(String __claimAperformTime) {
		this.__claimAperformTime = __claimAperformTime;
	}
	public String getClaimAperformTime() {
		return __claimAperformTime;
	}
	public void setClaimAaccountTime(String __claimAaccountTime) {
		this.__claimAaccountTime = __claimAaccountTime;
	}
	public String getClaimAaccountTime() {
		return __claimAaccountTime;
	}
	public void setClaimAadmitFlag(String __claimAadmitFlag) {
		this.__claimAadmitFlag = __claimAadmitFlag;
	}
	public String getClaimAadmitFlag() {
		return __claimAadmitFlag;
	}
	public void setClaimAtimeClass(String __claimAtimeClass) {
		this.__claimAtimeClass = __claimAtimeClass;
	}
	public String getClaimAtimeClass() {
		return __claimAtimeClass;
	}
	public void setClaimAinsuranceUid(String __claimAinsuranceUid) {
		this.__claimAinsuranceUid = __claimAinsuranceUid;
	}
	public String getClaimAinsuranceUid() {
		return __claimAinsuranceUid;
	}
	public void setClaimAdefaultTableId(String __claimAdefaultTableId) {
		this.__claimAdefaultTableId = __claimAdefaultTableId;
	}
	public String getClaimAdefaultTableId() {
		return __claimAdefaultTableId;
	}

	public void setPatientDepartment(claimApatientDepartment _patientDepartment) {
		this._patientDepartment = _patientDepartment;
	}
	public claimApatientDepartment getPatientDepartment() {
		return _patientDepartment;
	}
	public void setPatientWard(claimApatientWard _patientWard) {
		this._patientWard = _patientWard;
	}
	public claimApatientWard getPatientWard() {
		return _patientWard;
	}
	public void setInsuranceClass(mmlHiinsuranceClass _insuranceClass) {
		this._insuranceClass = _insuranceClass;
	}
	public mmlHiinsuranceClass getInsuranceClass() {
		return _insuranceClass;
	}
	
}