/**
 *
 * mmlSminPatient.java
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
public class mmlSminPatient extends MMLObject {
	
	/* fields */
	private Vector _inPatientItem = new Vector();
	
	public mmlSminPatient() {
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
			if (this._inPatientItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._inPatientItem.size(); ++i ) {
					((mmlSminPatientItem)this._inPatientItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSm:inPatient") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSminPatient obj = new mmlSminPatient();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSminPatient)builder.getElement()).setNamespace( getNamespace() );
			((mmlSminPatient)builder.getElement()).setLocalName( getLocalName() );
			((mmlSminPatient)builder.getElement()).setQName( getQName() );
			((mmlSminPatient)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:inPatient") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:serviceHistory")) {
				((mmlSmserviceHistory)builder.getParent()).setInPatient((mmlSminPatient)builder.getElement());
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
	public void setInPatientItem(Vector _inPatientItem) {
		if (this._inPatientItem != null) this._inPatientItem.removeAllElements();
		// copy entire elements in the vector
		this._inPatientItem = new Vector();
		for (int i = 0; i < _inPatientItem.size(); ++i) {
			this._inPatientItem.addElement( _inPatientItem.elementAt(i) );
		}
	}
	public Vector getInPatientItem() {
		return _inPatientItem;
	}
	
}