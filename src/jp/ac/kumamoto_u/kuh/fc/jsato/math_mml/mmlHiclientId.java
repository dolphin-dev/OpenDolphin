/**
 *
 * mmlHiclientId.java
 * Created on 2002/7/30 10:0:25
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
public class mmlHiclientId extends MMLObject {
	
	/* fields */
	private mmlHigroup _group = null;
	private mmlHinumber _number = null;
	
	public mmlHiclientId() {
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
			if ( _group != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_group.printObject(pw, visitor);
			}
			if ( _number != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_number.printObject(pw, visitor);
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
		if (qName.equals("mmlHi:clientId") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHiclientId obj = new mmlHiclientId();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHiclientId)builder.getElement()).setNamespace( getNamespace() );
			((mmlHiclientId)builder.getElement()).setLocalName( getLocalName() );
			((mmlHiclientId)builder.getElement()).setQName( getQName() );
			((mmlHiclientId)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:clientId") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:HealthInsuranceModule")) {
				((mmlHiHealthInsuranceModule)builder.getParent()).setClientId((mmlHiclientId)builder.getElement());
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
	public void setGroup(mmlHigroup _group) {
		this._group = _group;
	}
	public mmlHigroup getGroup() {
		return _group;
	}
	public void setNumber(mmlHinumber _number) {
		this._number = _number;
	}
	public mmlHinumber getNumber() {
		return _number;
	}
	
}