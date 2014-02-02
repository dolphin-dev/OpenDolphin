/**
 *
 * mmlSmdischarge.java
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
public class mmlSmdischarge extends MMLObject {
	
	/* fields */
	private mmlSmdate _date = null;
	private mmlSmdischargeCondition _dischargeCondition = null;
	private mmlSmreferTo _referTo = null;
	
	public mmlSmdischarge() {
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
			if ( _dischargeCondition != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_dischargeCondition.printObject(pw, visitor);
			}
			if ( _referTo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referTo.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:discharge") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmdischarge obj = new mmlSmdischarge();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmdischarge)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmdischarge)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmdischarge)builder.getElement()).setQName( getQName() );
			((mmlSmdischarge)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:discharge") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:inPatientItem")) {
				((mmlSminPatientItem)builder.getParent()).set_discharge((mmlSmdischarge)builder.getElement());
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
	public void set_dischargeCondition(mmlSmdischargeCondition _dischargeCondition) {
		this._dischargeCondition = _dischargeCondition;
	}
	public mmlSmdischargeCondition get_dischargeCondition() {
		return _dischargeCondition;
	}
	public void set_referTo(mmlSmreferTo _referTo) {
		this._referTo = _referTo;
	}
	public mmlSmreferTo get_referTo() {
		return _referTo;
	}
	
}