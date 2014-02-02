/**
 *
 * claimAamountInformation.java
 * Created on 2003/1/4 2:30:27
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claimA:status") ) {
						set__claimAstatus( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAstatus( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:oderTime") ) {
						set__claimAoderTime( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAoderTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:appointTime") ) {
						set__claimAappointTime( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAappointTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:registTime") ) {
						set__claimAregistTime( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAregistTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:performTime") ) {
						set__claimAperformTime( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAperformTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:accountTime") ) {
						set__claimAaccountTime( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAaccountTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:admitFlag") ) {
						set__claimAadmitFlag( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAadmitFlag( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:timeClass") ) {
						set__claimAtimeClass( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAtimeClass( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:insuranceUid") ) {
						set__claimAinsuranceUid( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAinsuranceUid( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:defaultTableId") ) {
						set__claimAdefaultTableId( atts.getValue(i) );
						((claimAamountInformation)builder.getElement()).set__claimAdefaultTableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:amountInformation") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:ClaimAmountModule")) {
				((claimAClaimAmountModule)builder.getParent()).set_amountInformation((claimAamountInformation)builder.getElement());
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
	public void set__claimAstatus(String __claimAstatus) {
		this.__claimAstatus = __claimAstatus;
	}
	public String get__claimAstatus() {
		return __claimAstatus;
	}
	public void set__claimAoderTime(String __claimAoderTime) {
		this.__claimAoderTime = __claimAoderTime;
	}
	public String get__claimAoderTime() {
		return __claimAoderTime;
	}
	public void set__claimAappointTime(String __claimAappointTime) {
		this.__claimAappointTime = __claimAappointTime;
	}
	public String get__claimAappointTime() {
		return __claimAappointTime;
	}
	public void set__claimAregistTime(String __claimAregistTime) {
		this.__claimAregistTime = __claimAregistTime;
	}
	public String get__claimAregistTime() {
		return __claimAregistTime;
	}
	public void set__claimAperformTime(String __claimAperformTime) {
		this.__claimAperformTime = __claimAperformTime;
	}
	public String get__claimAperformTime() {
		return __claimAperformTime;
	}
	public void set__claimAaccountTime(String __claimAaccountTime) {
		this.__claimAaccountTime = __claimAaccountTime;
	}
	public String get__claimAaccountTime() {
		return __claimAaccountTime;
	}
	public void set__claimAadmitFlag(String __claimAadmitFlag) {
		this.__claimAadmitFlag = __claimAadmitFlag;
	}
	public String get__claimAadmitFlag() {
		return __claimAadmitFlag;
	}
	public void set__claimAtimeClass(String __claimAtimeClass) {
		this.__claimAtimeClass = __claimAtimeClass;
	}
	public String get__claimAtimeClass() {
		return __claimAtimeClass;
	}
	public void set__claimAinsuranceUid(String __claimAinsuranceUid) {
		this.__claimAinsuranceUid = __claimAinsuranceUid;
	}
	public String get__claimAinsuranceUid() {
		return __claimAinsuranceUid;
	}
	public void set__claimAdefaultTableId(String __claimAdefaultTableId) {
		this.__claimAdefaultTableId = __claimAdefaultTableId;
	}
	public String get__claimAdefaultTableId() {
		return __claimAdefaultTableId;
	}

	public void set_patientDepartment(claimApatientDepartment _patientDepartment) {
		this._patientDepartment = _patientDepartment;
	}
	public claimApatientDepartment get_patientDepartment() {
		return _patientDepartment;
	}
	public void set_patientWard(claimApatientWard _patientWard) {
		this._patientWard = _patientWard;
	}
	public claimApatientWard get_patientWard() {
		return _patientWard;
	}
	public void set_insuranceClass(mmlHiinsuranceClass _insuranceClass) {
		this._insuranceClass = _insuranceClass;
	}
	public mmlHiinsuranceClass get_insuranceClass() {
		return _insuranceClass;
	}
	
}