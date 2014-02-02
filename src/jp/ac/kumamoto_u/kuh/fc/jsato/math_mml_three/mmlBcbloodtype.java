/**
 *
 * mmlBcbloodtype.java
 * Created on 2003/1/4 2:30:3
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
public class mmlBcbloodtype extends MMLObject {
	
	/* fields */
	private mmlBcabo _abo = null;
	private mmlBcrh _rh = null;
	private mmlBcothers _others = null;
	private mmlBcmemo _memo = null;
	
	public mmlBcbloodtype() {
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
			if ( _abo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_abo.printObject(pw, visitor);
			}
			if ( _rh != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_rh.printObject(pw, visitor);
			}
			if ( _others != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_others.printObject(pw, visitor);
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
		if (qName.equals("mmlBc:bloodtype") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcbloodtype obj = new mmlBcbloodtype();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcbloodtype)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcbloodtype)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcbloodtype)builder.getElement()).setQName( getQName() );
			((mmlBcbloodtype)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:bloodtype") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:BaseClinicModule")) {
				((mmlBcBaseClinicModule)builder.getParent()).set_bloodtype((mmlBcbloodtype)builder.getElement());
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
	public void set_abo(mmlBcabo _abo) {
		this._abo = _abo;
	}
	public mmlBcabo get_abo() {
		return _abo;
	}
	public void set_rh(mmlBcrh _rh) {
		this._rh = _rh;
	}
	public mmlBcrh get_rh() {
		return _rh;
	}
	public void set_others(mmlBcothers _others) {
		this._others = _others;
	}
	public mmlBcothers get_others() {
		return _others;
	}
	public void set_memo(mmlBcmemo _memo) {
		this._memo = _memo;
	}
	public mmlBcmemo get_memo() {
		return _memo;
	}
	
}