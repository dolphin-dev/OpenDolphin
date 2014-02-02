/**
 *
 * mmlPcplan.java
 * Created on 2002/7/30 10:0:30
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
public class mmlPcplan extends MMLObject {
	
	/* fields */
	private mmlPctestOrder _testOrder = null;
	private mmlPcrxOrder _rxOrder = null;
	private mmlPctxOrder _txOrder = null;
	private mmlPcplanNotes _planNotes = null;
	
	public mmlPcplan() {
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
			if ( _testOrder != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testOrder.printObject(pw, visitor);
			}
			if ( _rxOrder != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_rxOrder.printObject(pw, visitor);
			}
			if ( _txOrder != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_txOrder.printObject(pw, visitor);
			}
			if ( _planNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_planNotes.printObject(pw, visitor);
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
		if (qName.equals("mmlPc:plan") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcplan obj = new mmlPcplan();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcplan)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcplan)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcplan)builder.getElement()).setQName( getQName() );
			((mmlPcplan)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:plan") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:problemItem")) {
				((mmlPcproblemItem)builder.getParent()).setPlan((mmlPcplan)builder.getElement());
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
	public void setTestOrder(mmlPctestOrder _testOrder) {
		this._testOrder = _testOrder;
	}
	public mmlPctestOrder getTestOrder() {
		return _testOrder;
	}
	public void setRxOrder(mmlPcrxOrder _rxOrder) {
		this._rxOrder = _rxOrder;
	}
	public mmlPcrxOrder getRxOrder() {
		return _rxOrder;
	}
	public void setTxOrder(mmlPctxOrder _txOrder) {
		this._txOrder = _txOrder;
	}
	public mmlPctxOrder getTxOrder() {
		return _txOrder;
	}
	public void setPlanNotes(mmlPcplanNotes _planNotes) {
		this._planNotes = _planNotes;
	}
	public mmlPcplanNotes getPlanNotes() {
		return _planNotes;
	}
	
}