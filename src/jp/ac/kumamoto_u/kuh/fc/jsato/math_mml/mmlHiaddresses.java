/**
 *
 * mmlHiaddresses.java
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
public class mmlHiaddresses extends MMLObject {
	
	/* fields */
	private Vector _Address = new Vector();
	
	public mmlHiaddresses() {
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
			if (this._Address != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._Address.size(); ++i ) {
					((mmlAdAddress)this._Address.elementAt(i)).printObject(pw, visitor);
				}
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
		if (qName.equals("mmlHi:addresses") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHiaddresses obj = new mmlHiaddresses();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHiaddresses)builder.getElement()).setNamespace( getNamespace() );
			((mmlHiaddresses)builder.getElement()).setLocalName( getLocalName() );
			((mmlHiaddresses)builder.getElement()).setQName( getQName() );
			((mmlHiaddresses)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:addresses") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:workInfo")) {
				((mmlHiworkInfo)builder.getParent()).setAddresses((mmlHiaddresses)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:insuredInfo")) {
				((mmlHiinsuredInfo)builder.getParent()).setAddresses((mmlHiaddresses)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlHi:clientInfo")) {
				((mmlHiclientInfo)builder.getParent()).setAddresses((mmlHiaddresses)builder.getElement());
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
	public void setAddress(Vector _Address) {
		if (this._Address != null) this._Address.removeAllElements();
		// copy entire elements in the vector
		this._Address = new Vector();
		for (int i = 0; i < _Address.size(); ++i) {
			this._Address.addElement( _Address.elementAt(i) );
		}
	}
	public Vector getAddress() {
		return _Address;
	}
	
}