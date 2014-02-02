/**
 *
 * mmlBcinfectionItem.java
 * Created on 2003/1/4 2:30:4
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
public class mmlBcinfectionItem extends MMLObject {
	
	/* fields */
	private mmlBcfactor _factor = null;
	private mmlBcexamValue _examValue = null;
	private mmlBcidentifiedDate _identifiedDate = null;
	private mmlBcmemo _memo = null;
	
	public mmlBcinfectionItem() {
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
			if ( _factor != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_factor.printObject(pw, visitor);
			}
			if ( _examValue != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_examValue.printObject(pw, visitor);
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
		if (qName.equals("mmlBc:infectionItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcinfectionItem obj = new mmlBcinfectionItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcinfectionItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcinfectionItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcinfectionItem)builder.getElement()).setQName( getQName() );
			((mmlBcinfectionItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:infectionItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:infection")) {
				Vector v = ((mmlBcinfection)builder.getParent()).get_infectionItem();
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
	public void set_factor(mmlBcfactor _factor) {
		this._factor = _factor;
	}
	public mmlBcfactor get_factor() {
		return _factor;
	}
	public void set_examValue(mmlBcexamValue _examValue) {
		this._examValue = _examValue;
	}
	public mmlBcexamValue get_examValue() {
		return _examValue;
	}
	public void set_identifiedDate(mmlBcidentifiedDate _identifiedDate) {
		this._identifiedDate = _identifiedDate;
	}
	public mmlBcidentifiedDate get_identifiedDate() {
		return _identifiedDate;
	}
	public void set_memo(mmlBcmemo _memo) {
		this._memo = _memo;
	}
	public mmlBcmemo get_memo() {
		return _memo;
	}
	
}