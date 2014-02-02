/**
 *
 * mmlFclbirthInfo.java
 * Created on 2002/7/30 10:0:27
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
public class mmlFclbirthInfo extends MMLObject {
	
	/* fields */
	private mmlFcFacility _Facility = null;
	private mmlFcldeliveryWeeks _deliveryWeeks = null;
	private mmlFcldeliveryMethod _deliveryMethod = null;
	private mmlFclbodyWeight _bodyWeight = null;
	private mmlFclbodyHeight _bodyHeight = null;
	private mmlFclchestCircumference _chestCircumference = null;
	private mmlFclheadCircumference _headCircumference = null;
	private mmlFclmemo _memo = null;
	
	public mmlFclbirthInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _Facility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Facility.printObject(pw, visitor);
			}
			if ( _deliveryWeeks != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_deliveryWeeks.printObject(pw, visitor);
			}
			if ( _deliveryMethod != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_deliveryMethod.printObject(pw, visitor);
			}
			if ( _bodyWeight != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_bodyWeight.printObject(pw, visitor);
			}
			if ( _bodyHeight != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_bodyHeight.printObject(pw, visitor);
			}
			if ( _chestCircumference != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_chestCircumference.printObject(pw, visitor);
			}
			if ( _headCircumference != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_headCircumference.printObject(pw, visitor);
			}
			if ( _memo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_memo.printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:birthInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclbirthInfo obj = new mmlFclbirthInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclbirthInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclbirthInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclbirthInfo)builder.getElement()).setQName( getQName() );
			((mmlFclbirthInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:birthInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:childhood")) {
				((mmlFclchildhood)builder.getParent()).setBirthInfo((mmlFclbirthInfo)builder.getElement());
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
	public void setFacility(mmlFcFacility _Facility) {
		this._Facility = _Facility;
	}
	public mmlFcFacility getFacility() {
		return _Facility;
	}
	public void setDeliveryWeeks(mmlFcldeliveryWeeks _deliveryWeeks) {
		this._deliveryWeeks = _deliveryWeeks;
	}
	public mmlFcldeliveryWeeks getDeliveryWeeks() {
		return _deliveryWeeks;
	}
	public void setDeliveryMethod(mmlFcldeliveryMethod _deliveryMethod) {
		this._deliveryMethod = _deliveryMethod;
	}
	public mmlFcldeliveryMethod getDeliveryMethod() {
		return _deliveryMethod;
	}
	public void setBodyWeight(mmlFclbodyWeight _bodyWeight) {
		this._bodyWeight = _bodyWeight;
	}
	public mmlFclbodyWeight getBodyWeight() {
		return _bodyWeight;
	}
	public void setBodyHeight(mmlFclbodyHeight _bodyHeight) {
		this._bodyHeight = _bodyHeight;
	}
	public mmlFclbodyHeight getBodyHeight() {
		return _bodyHeight;
	}
	public void setChestCircumference(mmlFclchestCircumference _chestCircumference) {
		this._chestCircumference = _chestCircumference;
	}
	public mmlFclchestCircumference getChestCircumference() {
		return _chestCircumference;
	}
	public void setHeadCircumference(mmlFclheadCircumference _headCircumference) {
		this._headCircumference = _headCircumference;
	}
	public mmlFclheadCircumference getHeadCircumference() {
		return _headCircumference;
	}
	public void setMemo(mmlFclmemo _memo) {
		this._memo = _memo;
	}
	public mmlFclmemo getMemo() {
		return _memo;
	}
	
}