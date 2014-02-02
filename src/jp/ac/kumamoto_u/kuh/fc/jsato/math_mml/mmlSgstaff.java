/**
 *
 * mmlSgstaff.java
 * Created on 2002/7/30 10:0:32
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
public class mmlSgstaff extends MMLObject {
	
	/* fields */
	private String __mmlSgsuperiority = null;
	private String __mmlSgstaffClass = null;

	private mmlSgstaffInfo _staffInfo = null;
	
	public mmlSgstaff() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSgsuperiority != null ) pw.print(" " + "mmlSg:superiority" +  "=" + "'" + __mmlSgsuperiority + "'");
			if ( __mmlSgstaffClass != null ) pw.print(" " + "mmlSg:staffClass" +  "=" + "'" + __mmlSgstaffClass + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _staffInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_staffInfo.printObject(pw, visitor);
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
		if (qName.equals("mmlSg:staff") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgstaff obj = new mmlSgstaff();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgstaff)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgstaff)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgstaff)builder.getElement()).setQName( getQName() );
			((mmlSgstaff)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlSgsuperiority( atts.getValue(namespaceURI, "superiority") );
				((mmlSgstaff)builder.getElement()).setMmlSgsuperiority( atts.getValue(namespaceURI, "superiority") );
				setMmlSgstaffClass( atts.getValue(namespaceURI, "staffClass") );
				((mmlSgstaff)builder.getElement()).setMmlSgstaffClass( atts.getValue(namespaceURI, "staffClass") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:staff") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgicalStaffs")) {
				Vector v = ((mmlSgsurgicalStaffs)builder.getParent()).getStaff();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:anesthesiologists")) {
				Vector v = ((mmlSganesthesiologists)builder.getParent()).getStaff();
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
	public void setMmlSgsuperiority(String __mmlSgsuperiority) {
		this.__mmlSgsuperiority = __mmlSgsuperiority;
	}
	public String getMmlSgsuperiority() {
		return __mmlSgsuperiority;
	}
	public void setMmlSgstaffClass(String __mmlSgstaffClass) {
		this.__mmlSgstaffClass = __mmlSgstaffClass;
	}
	public String getMmlSgstaffClass() {
		return __mmlSgstaffClass;
	}

	public void setStaffInfo(mmlSgstaffInfo _staffInfo) {
		this._staffInfo = _staffInfo;
	}
	public mmlSgstaffInfo getStaffInfo() {
		return _staffInfo;
	}
	
}