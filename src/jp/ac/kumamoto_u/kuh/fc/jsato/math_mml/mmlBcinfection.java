/**
 *
 * mmlBcinfection.java
 * Created on 2002/7/30 10:0:27
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
public class mmlBcinfection extends MMLObject {
	
	/* fields */
	private Vector _infectionItem = new Vector();
	
	public mmlBcinfection() {
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
			if (this._infectionItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._infectionItem.size(); ++i ) {
					((mmlBcinfectionItem)this._infectionItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlBc:infection") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcinfection obj = new mmlBcinfection();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcinfection)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcinfection)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcinfection)builder.getElement()).setQName( getQName() );
			((mmlBcinfection)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:infection") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:BaseClinicModule")) {
				((mmlBcBaseClinicModule)builder.getParent()).setInfection((mmlBcinfection)builder.getElement());
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
	public void setInfectionItem(Vector _infectionItem) {
		if (this._infectionItem != null) this._infectionItem.removeAllElements();
		// copy entire elements in the vector
		this._infectionItem = new Vector();
		for (int i = 0; i < _infectionItem.size(); ++i) {
			this._infectionItem.addElement( _infectionItem.elementAt(i) );
		}
	}
	public Vector getInfectionItem() {
		return _infectionItem;
	}
	
}