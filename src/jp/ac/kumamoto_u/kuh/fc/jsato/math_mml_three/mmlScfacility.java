/**
 *
 * mmlScfacility.java
 * Created on 2003/1/4 2:29:56
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
public class mmlScfacility extends MMLObject {
	
	/* fields */
	private Vector _facilityName = new Vector();
	
	public mmlScfacility() {
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
			if (this._facilityName != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._facilityName.size(); ++i ) {
					((mmlScfacilityName)this._facilityName.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSc:facility") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlScfacility obj = new mmlScfacility();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlScfacility)builder.getElement()).setNamespace( getNamespace() );
			((mmlScfacility)builder.getElement()).setLocalName( getLocalName() );
			((mmlScfacility)builder.getElement()).setQName( getQName() );
			((mmlScfacility)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:facility") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:accessRight")) {
				((mmlaccessRight)builder.getParent()).set_facility((mmlScfacility)builder.getElement());
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
	public void set_facilityName(Vector _facilityName) {
		if (this._facilityName != null) this._facilityName.removeAllElements();
		// copy entire elements in the vector
		this._facilityName = new Vector();
		for (int i = 0; i < _facilityName.size(); ++i) {
			this._facilityName.addElement( _facilityName.elementAt(i) );
		}
	}
	public Vector get_facilityName() {
		return _facilityName;
	}
	
}