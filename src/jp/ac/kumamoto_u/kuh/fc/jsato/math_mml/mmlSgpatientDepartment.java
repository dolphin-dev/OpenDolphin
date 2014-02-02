/**
 *
 * mmlSgpatientDepartment.java
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
public class mmlSgpatientDepartment extends MMLObject {
	
	/* fields */
	private Vector _Department = new Vector();
	
	public mmlSgpatientDepartment() {
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
			if (this._Department != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._Department.size(); ++i ) {
					((mmlDpDepartment)this._Department.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSg:patientDepartment") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgpatientDepartment obj = new mmlSgpatientDepartment();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgpatientDepartment)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgpatientDepartment)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgpatientDepartment)builder.getElement()).setQName( getQName() );
			((mmlSgpatientDepartment)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:patientDepartment") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgicalInfo")) {
				((mmlSgsurgicalInfo)builder.getParent()).setPatientDepartment((mmlSgpatientDepartment)builder.getElement());
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
	public void setDepartment(Vector _Department) {
		if (this._Department != null) this._Department.removeAllElements();
		// copy entire elements in the vector
		this._Department = new Vector();
		for (int i = 0; i < _Department.size(); ++i) {
			this._Department.addElement( _Department.elementAt(i) );
		}
	}
	public Vector getDepartment() {
		return _Department;
	}
	
}