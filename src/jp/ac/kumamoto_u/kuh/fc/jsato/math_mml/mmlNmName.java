/**
 *
 * mmlNmName.java
 * Created on 2002/7/30 10:0:24
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
public class mmlNmName extends MMLObject {
	
	/* fields */
	private String __mmlNmrepCode = null;
	private String __mmlNmtableId = null;

	private mmlNmfamily _family = null;
	private mmlNmgiven _given = null;
	private mmlNmmiddle _middle = null;
	private mmlNmfullname _fullname = null;
	private mmlNmprefix _prefix = null;
	private mmlNmdegree _degree = null;
	
	public mmlNmName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlNmrepCode != null ) pw.print(" " + "mmlNm:repCode" +  "=" + "'" + __mmlNmrepCode + "'");
			if ( __mmlNmtableId != null ) pw.print(" " + "mmlNm:tableId" +  "=" + "'" + __mmlNmtableId + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _family != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_family.printObject(pw, visitor);
			}
			if ( _given != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_given.printObject(pw, visitor);
			}
			if ( _middle != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_middle.printObject(pw, visitor);
			}
			if ( _fullname != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_fullname.printObject(pw, visitor);
			}
			if ( _prefix != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_prefix.printObject(pw, visitor);
			}
			if ( _degree != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_degree.printObject(pw, visitor);
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
		if (qName.equals("mmlNm:Name") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlNmName obj = new mmlNmName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlNmName)builder.getElement()).setNamespace( getNamespace() );
			((mmlNmName)builder.getElement()).setLocalName( getLocalName() );
			((mmlNmName)builder.getElement()).setQName( getQName() );
			((mmlNmName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlNmrepCode( atts.getValue(namespaceURI, "repCode") );
				((mmlNmName)builder.getElement()).setMmlNmrepCode( atts.getValue(namespaceURI, "repCode") );
				setMmlNmtableId( atts.getValue(namespaceURI, "tableId") );
				((mmlNmName)builder.getElement()).setMmlNmtableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlNm:Name") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:personName")) {
				Vector v = ((mmlPsipersonName)builder.getParent()).getName();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:personName")) {
				Vector v = ((mmlPipersonName)builder.getParent()).getName();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:personName")) {
				Vector v = ((mmlHipersonName)builder.getParent()).getName();
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
	public void setMmlNmrepCode(String __mmlNmrepCode) {
		this.__mmlNmrepCode = __mmlNmrepCode;
	}
	public String getMmlNmrepCode() {
		return __mmlNmrepCode;
	}
	public void setMmlNmtableId(String __mmlNmtableId) {
		this.__mmlNmtableId = __mmlNmtableId;
	}
	public String getMmlNmtableId() {
		return __mmlNmtableId;
	}

	public void setFamily(mmlNmfamily _family) {
		this._family = _family;
	}
	public mmlNmfamily getFamily() {
		return _family;
	}
	public void setGiven(mmlNmgiven _given) {
		this._given = _given;
	}
	public mmlNmgiven getGiven() {
		return _given;
	}
	public void setMiddle(mmlNmmiddle _middle) {
		this._middle = _middle;
	}
	public mmlNmmiddle getMiddle() {
		return _middle;
	}
	public void setFullname(mmlNmfullname _fullname) {
		this._fullname = _fullname;
	}
	public mmlNmfullname getFullname() {
		return _fullname;
	}
	public void setPrefix(mmlNmprefix _prefix) {
		this._prefix = _prefix;
	}
	public mmlNmprefix getPrefix() {
		return _prefix;
	}
	public void setDegree(mmlNmdegree _degree) {
		this._degree = _degree;
	}
	public mmlNmdegree getDegree() {
		return _degree;
	}
	
}