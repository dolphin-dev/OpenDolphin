/**
 *
 * mmlFclvaccination.java
 * Created on 2003/1/4 2:30:5
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
public class mmlFclvaccination extends MMLObject {
	
	/* fields */
	private Vector _vaccinationItem = new Vector();
	
	public mmlFclvaccination() {
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
			if (this._vaccinationItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._vaccinationItem.size(); ++i ) {
					((mmlFclvaccinationItem)this._vaccinationItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:vaccination") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclvaccination obj = new mmlFclvaccination();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclvaccination)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclvaccination)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclvaccination)builder.getElement()).setQName( getQName() );
			((mmlFclvaccination)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:vaccination") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:childhood")) {
				((mmlFclchildhood)builder.getParent()).set_vaccination((mmlFclvaccination)builder.getElement());
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
	public void set_vaccinationItem(Vector _vaccinationItem) {
		if (this._vaccinationItem != null) this._vaccinationItem.removeAllElements();
		// copy entire elements in the vector
		this._vaccinationItem = new Vector();
		for (int i = 0; i < _vaccinationItem.size(); ++i) {
			this._vaccinationItem.addElement( _vaccinationItem.elementAt(i) );
		}
	}
	public Vector get_vaccinationItem() {
		return _vaccinationItem;
	}
	
}