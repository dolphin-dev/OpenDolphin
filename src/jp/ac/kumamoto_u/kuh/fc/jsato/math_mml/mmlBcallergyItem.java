/**
 *
 * mmlBcallergyItem.java
 * Created on 2002/7/30 10:0:26
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
public class mmlBcallergyItem extends MMLObject {
	
	/* fields */
	private mmlBcfactor _factor = null;
	private mmlBcseverity _severity = null;
	private mmlBcidentifiedDate _identifiedDate = null;
	private mmlBcmemo _memo = null;
	
	public mmlBcallergyItem() {
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
			if ( _factor != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_factor.printObject(pw, visitor);
			}
			if ( _severity != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_severity.printObject(pw, visitor);
			}
			if ( _identifiedDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_identifiedDate.printObject(pw, visitor);
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
		if (qName.equals("mmlBc:allergyItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcallergyItem obj = new mmlBcallergyItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcallergyItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcallergyItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcallergyItem)builder.getElement()).setQName( getQName() );
			((mmlBcallergyItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:allergyItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:allergy")) {
				Vector v = ((mmlBcallergy)builder.getParent()).getAllergyItem();
				v.addElement(builder.getElement());
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
	public void setFactor(mmlBcfactor _factor) {
		this._factor = _factor;
	}
	public mmlBcfactor getFactor() {
		return _factor;
	}
	public void setSeverity(mmlBcseverity _severity) {
		this._severity = _severity;
	}
	public mmlBcseverity getSeverity() {
		return _severity;
	}
	public void setIdentifiedDate(mmlBcidentifiedDate _identifiedDate) {
		this._identifiedDate = _identifiedDate;
	}
	public mmlBcidentifiedDate getIdentifiedDate() {
		return _identifiedDate;
	}
	public void setMemo(mmlBcmemo _memo) {
		this._memo = _memo;
	}
	public mmlBcmemo getMemo() {
		return _memo;
	}
	
}