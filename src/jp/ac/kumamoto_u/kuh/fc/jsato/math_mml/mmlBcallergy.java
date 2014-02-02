/**
 *
 * mmlBcallergy.java
 * Created on 2002/7/30 10:0:26
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
public class mmlBcallergy extends MMLObject {
	
	/* fields */
	private Vector _allergyItem = new Vector();
	
	public mmlBcallergy() {
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
			if (this._allergyItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._allergyItem.size(); ++i ) {
					((mmlBcallergyItem)this._allergyItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlBc:allergy") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcallergy obj = new mmlBcallergy();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcallergy)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcallergy)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcallergy)builder.getElement()).setQName( getQName() );
			((mmlBcallergy)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:allergy") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:BaseClinicModule")) {
				((mmlBcBaseClinicModule)builder.getParent()).setAllergy((mmlBcallergy)builder.getElement());
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
	public void setAllergyItem(Vector _allergyItem) {
		if (this._allergyItem != null) this._allergyItem.removeAllElements();
		// copy entire elements in the vector
		this._allergyItem = new Vector();
		for (int i = 0; i < _allergyItem.size(); ++i) {
			this._allergyItem.addElement( _allergyItem.elementAt(i) );
		}
	}
	public Vector getAllergyItem() {
		return _allergyItem;
	}
	
}