/**
 *
 * mmlSgstaff.java
 * Created on 2003/1/4 2:30:8
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSg:superiority") ) {
						set__mmlSgsuperiority( atts.getValue(i) );
						((mmlSgstaff)builder.getElement()).set__mmlSgsuperiority( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSg:staffClass") ) {
						set__mmlSgstaffClass( atts.getValue(i) );
						((mmlSgstaff)builder.getElement()).set__mmlSgstaffClass( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:staff") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgicalStaffs")) {
				Vector v = ((mmlSgsurgicalStaffs)builder.getParent()).get_staff();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:anesthesiologists")) {
				Vector v = ((mmlSganesthesiologists)builder.getParent()).get_staff();
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
	public void set__mmlSgsuperiority(String __mmlSgsuperiority) {
		this.__mmlSgsuperiority = __mmlSgsuperiority;
	}
	public String get__mmlSgsuperiority() {
		return __mmlSgsuperiority;
	}
	public void set__mmlSgstaffClass(String __mmlSgstaffClass) {
		this.__mmlSgstaffClass = __mmlSgstaffClass;
	}
	public String get__mmlSgstaffClass() {
		return __mmlSgstaffClass;
	}

	public void set_staffInfo(mmlSgstaffInfo _staffInfo) {
		this._staffInfo = _staffInfo;
	}
	public mmlSgstaffInfo get_staffInfo() {
		return _staffInfo;
	}
	
}