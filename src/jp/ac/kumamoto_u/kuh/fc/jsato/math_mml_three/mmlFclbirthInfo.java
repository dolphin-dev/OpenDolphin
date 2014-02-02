/**
 *
 * mmlFclbirthInfo.java
 * Created on 2003/1/4 2:30:5
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
				((mmlFclchildhood)builder.getParent()).set_birthInfo((mmlFclbirthInfo)builder.getElement());
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
	public void set_deliveryWeeks(mmlFcldeliveryWeeks _deliveryWeeks) {
		this._deliveryWeeks = _deliveryWeeks;
	}
	public mmlFcldeliveryWeeks get_deliveryWeeks() {
		return _deliveryWeeks;
	}
	public void set_deliveryMethod(mmlFcldeliveryMethod _deliveryMethod) {
		this._deliveryMethod = _deliveryMethod;
	}
	public mmlFcldeliveryMethod get_deliveryMethod() {
		return _deliveryMethod;
	}
	public void set_bodyWeight(mmlFclbodyWeight _bodyWeight) {
		this._bodyWeight = _bodyWeight;
	}
	public mmlFclbodyWeight get_bodyWeight() {
		return _bodyWeight;
	}
	public void set_bodyHeight(mmlFclbodyHeight _bodyHeight) {
		this._bodyHeight = _bodyHeight;
	}
	public mmlFclbodyHeight get_bodyHeight() {
		return _bodyHeight;
	}
	public void set_chestCircumference(mmlFclchestCircumference _chestCircumference) {
		this._chestCircumference = _chestCircumference;
	}
	public mmlFclchestCircumference get_chestCircumference() {
		return _chestCircumference;
	}
	public void set_headCircumference(mmlFclheadCircumference _headCircumference) {
		this._headCircumference = _headCircumference;
	}
	public mmlFclheadCircumference get_headCircumference() {
		return _headCircumference;
	}
	public void set_memo(mmlFclmemo _memo) {
		this._memo = _memo;
	}
	public mmlFclmemo get_memo() {
		return _memo;
	}
	
}