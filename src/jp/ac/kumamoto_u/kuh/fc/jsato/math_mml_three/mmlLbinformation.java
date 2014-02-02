/**
 *
 * mmlLbinformation.java
 * Created on 2003/1/4 2:30:11
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
public class mmlLbinformation extends MMLObject {
	
	/* fields */
	private String __mmlLbregistId = null;
	private String __mmlLbsampleTime = null;
	private String __mmlLbregistTime = null;
	private String __mmlLbreportTime = null;

	private mmlLbreportStatus _reportStatus = null;
	private mmlLbset _set = null;
	private mmlLbfacility _facility = null;
	private mmlLbdepartment _department = null;
	private mmlLbward _ward = null;
	private mmlLbclient _client = null;
	private mmlLblaboratoryCenter _laboratoryCenter = null;
	private mmlLbtechnician _technician = null;
	private Vector _repMemo = new Vector();
	private mmlLbrepMemoF _repMemoF = null;
	
	public mmlLbinformation() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbregistId != null ) pw.print(" " + "mmlLb:registId" +  "=" + "'" + __mmlLbregistId + "'");
			if ( __mmlLbsampleTime != null ) pw.print(" " + "mmlLb:sampleTime" +  "=" + "'" + __mmlLbsampleTime + "'");
			if ( __mmlLbregistTime != null ) pw.print(" " + "mmlLb:registTime" +  "=" + "'" + __mmlLbregistTime + "'");
			if ( __mmlLbreportTime != null ) pw.print(" " + "mmlLb:reportTime" +  "=" + "'" + __mmlLbreportTime + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _reportStatus != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_reportStatus.printObject(pw, visitor);
			}
			if ( _set != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_set.printObject(pw, visitor);
			}
			if ( _facility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_facility.printObject(pw, visitor);
			}
			if ( _department != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_department.printObject(pw, visitor);
			}
			if ( _ward != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ward.printObject(pw, visitor);
			}
			if ( _client != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_client.printObject(pw, visitor);
			}
			if ( _laboratoryCenter != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_laboratoryCenter.printObject(pw, visitor);
			}
			if ( _technician != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_technician.printObject(pw, visitor);
			}
			if (this._repMemo != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._repMemo.size(); ++i ) {
					((mmlLbrepMemo)this._repMemo.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _repMemoF != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_repMemoF.printObject(pw, visitor);
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
		if (qName.equals("mmlLb:information") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbinformation obj = new mmlLbinformation();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbinformation)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbinformation)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbinformation)builder.getElement()).setQName( getQName() );
			((mmlLbinformation)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlLb:registId") ) {
						set__mmlLbregistId( atts.getValue(i) );
						((mmlLbinformation)builder.getElement()).set__mmlLbregistId( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:sampleTime") ) {
						set__mmlLbsampleTime( atts.getValue(i) );
						((mmlLbinformation)builder.getElement()).set__mmlLbsampleTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:registTime") ) {
						set__mmlLbregistTime( atts.getValue(i) );
						((mmlLbinformation)builder.getElement()).set__mmlLbregistTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlLb:reportTime") ) {
						set__mmlLbreportTime( atts.getValue(i) );
						((mmlLbinformation)builder.getElement()).set__mmlLbreportTime( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:information") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:TestModule")) {
				((mmlLbTestModule)builder.getParent()).set_information((mmlLbinformation)builder.getElement());
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
	public void set__mmlLbregistId(String __mmlLbregistId) {
		this.__mmlLbregistId = __mmlLbregistId;
	}
	public String get__mmlLbregistId() {
		return __mmlLbregistId;
	}
	public void set__mmlLbsampleTime(String __mmlLbsampleTime) {
		this.__mmlLbsampleTime = __mmlLbsampleTime;
	}
	public String get__mmlLbsampleTime() {
		return __mmlLbsampleTime;
	}
	public void set__mmlLbregistTime(String __mmlLbregistTime) {
		this.__mmlLbregistTime = __mmlLbregistTime;
	}
	public String get__mmlLbregistTime() {
		return __mmlLbregistTime;
	}
	public void set__mmlLbreportTime(String __mmlLbreportTime) {
		this.__mmlLbreportTime = __mmlLbreportTime;
	}
	public String get__mmlLbreportTime() {
		return __mmlLbreportTime;
	}

	public void set_reportStatus(mmlLbreportStatus _reportStatus) {
		this._reportStatus = _reportStatus;
	}
	public mmlLbreportStatus get_reportStatus() {
		return _reportStatus;
	}
	public void set_set(mmlLbset _set) {
		this._set = _set;
	}
	public mmlLbset get_set() {
		return _set;
	}
	public void set_facility(mmlLbfacility _facility) {
		this._facility = _facility;
	}
	public mmlLbfacility get_facility() {
		return _facility;
	}
	public void set_department(mmlLbdepartment _department) {
		this._department = _department;
	}
	public mmlLbdepartment get_department() {
		return _department;
	}
	public void set_ward(mmlLbward _ward) {
		this._ward = _ward;
	}
	public mmlLbward get_ward() {
		return _ward;
	}
	public void set_client(mmlLbclient _client) {
		this._client = _client;
	}
	public mmlLbclient get_client() {
		return _client;
	}
	public void set_laboratoryCenter(mmlLblaboratoryCenter _laboratoryCenter) {
		this._laboratoryCenter = _laboratoryCenter;
	}
	public mmlLblaboratoryCenter get_laboratoryCenter() {
		return _laboratoryCenter;
	}
	public void set_technician(mmlLbtechnician _technician) {
		this._technician = _technician;
	}
	public mmlLbtechnician get_technician() {
		return _technician;
	}
	public void set_repMemo(Vector _repMemo) {
		if (this._repMemo != null) this._repMemo.removeAllElements();
		// copy entire elements in the vector
		this._repMemo = new Vector();
		for (int i = 0; i < _repMemo.size(); ++i) {
			this._repMemo.addElement( _repMemo.elementAt(i) );
		}
	}
	public Vector get_repMemo() {
		return _repMemo;
	}
	public void set_repMemoF(mmlLbrepMemoF _repMemoF) {
		this._repMemoF = _repMemoF;
	}
	public mmlLbrepMemoF get_repMemoF() {
		return _repMemoF;
	}
	
}