/**
 *
 * mmlSmoutPatient.java
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
public class mmlSmoutPatient extends MMLObject {
	
	/* fields */
	private Vector _outPatientItem = new Vector();
	
	public mmlSmoutPatient() {
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
			if (this._outPatientItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._outPatientItem.size(); ++i ) {
					((mmlSmoutPatientItem)this._outPatientItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSm:outPatient") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmoutPatient obj = new mmlSmoutPatient();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmoutPatient)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmoutPatient)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmoutPatient)builder.getElement()).setQName( getQName() );
			((mmlSmoutPatient)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:outPatient") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:serviceHistory")) {
				((mmlSmserviceHistory)builder.getParent()).setOutPatient((mmlSmoutPatient)builder.getElement());
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
	public void setOutPatientItem(Vector _outPatientItem) {
		if (this._outPatientItem != null) this._outPatientItem.removeAllElements();
		// copy entire elements in the vector
		this._outPatientItem = new Vector();
		for (int i = 0; i < _outPatientItem.size(); ++i) {
			this._outPatientItem.addElement( _outPatientItem.elementAt(i) );
		}
	}
	public Vector getOutPatientItem() {
		return _outPatientItem;
	}
	
}