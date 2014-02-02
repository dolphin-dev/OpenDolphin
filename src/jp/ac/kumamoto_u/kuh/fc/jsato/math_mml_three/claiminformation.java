/**
 *
 * claiminformation.java
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claim:status") ) {
						set__claimstatus( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimstatus( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:orderTime") ) {
						set__claimorderTime( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimorderTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:appointTime") ) {
						set__claimappointTime( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimappointTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:registTime") ) {
						set__claimregistTime( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimregistTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:performTime") ) {
						set__claimperformTime( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimperformTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:admitFlag") ) {
						set__claimadmitFlag( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimadmitFlag( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:timeClass") ) {
						set__claimtimeClass( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimtimeClass( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:insuranceUid") ) {
						set__claiminsuranceUid( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claiminsuranceUid( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:defaultTableId") ) {
						set__claimdefaultTableId( atts.getValue(i) );
						((claiminformation)builder.getElement()).set__claimdefaultTableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:information") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:ClaimModule")) {
				((claimClaimModule)builder.getParent()).set_information((claiminformation)builder.getElement());
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
	public void set__claimstatus(String __claimstatus) {
		this.__claimstatus = __claimstatus;
	}
	public String get__claimstatus() {
		return __claimstatus;
	}
	public void set__claimorderTime(String __claimorderTime) {
		this.__claimorderTime = __claimorderTime;
	}
	public String get__claimorderTime() {
		return __claimorderTime;
	}
	public void set__claimappointTime(String __claimappointTime) {
		this.__claimappointTime = __claimappointTime;
	}
	public String get__claimappointTime() {
		return __claimappointTime;
	}
	public void set__claimregistTime(String __claimregistTime) {
		this.__claimregistTime = __claimregistTime;
	}
	public String get__claimregistTime() {
		return __claimregistTime;
	}
	public void set__claimperformTime(String __claimperformTime) {
		this.__claimperformTime = __claimperformTime;
	}
	public String get__claimperformTime() {
		return __claimperformTime;
	}
	public void set__claimadmitFlag(String __claimadmitFlag) {
		this.__claimadmitFlag = __claimadmitFlag;
	}
	public String get__claimadmitFlag() {
		return __claimadmitFlag;
	}
	public void set__claimtimeClass(String __claimtimeClass) {
		this.__claimtimeClass = __claimtimeClass;
	}
	public String get__claimtimeClass() {
		return __claimtimeClass;
	}
	public void set__claiminsuranceUid(String __claiminsuranceUid) {
		this.__claiminsuranceUid = __claiminsuranceUid;
	}
	public String get__claiminsuranceUid() {
		return __claiminsuranceUid;
	}
	public void set__claimdefaultTableId(String __claimdefaultTableId) {
		this.__claimdefaultTableId = __claimdefaultTableId;
	}
	public String get__claimdefaultTableId() {
		return __claimdefaultTableId;
	}

	public void set_appoint(claimappoint _appoint) {
		this._appoint = _appoint;
	}
	public claimappoint get_appoint() {
		return _appoint;
	}
	public void set_patientDepartment(claimpatientDepartment _patientDepartment) {
		this._patientDepartment = _patientDepartment;
	}
	public claimpatientDepartment get_patientDepartment() {
		return _patientDepartment;
	}
	public void set_patientWard(claimpatientWard _patientWard) {
		this._patientWard = _patientWard;
	}
	public claimpatientWard get_patientWard() {
		return _patientWard;
	}
	public void set_insuranceClass(mmlHiinsuranceClass _insuranceClass) {
		this._insuranceClass = _insuranceClass;
	}
	public mmlHiinsuranceClass get_insuranceClass() {
		return _insuranceClass;
	}
	
}