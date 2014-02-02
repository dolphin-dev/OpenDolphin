/**
 *
 * mmlFclfamilyHistory.java
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
public class mmlFclfamilyHistory extends MMLObject {
	
	/* fields */
	private Vector _familyHistoryItem = new Vector();
	
	public mmlFclfamilyHistory() {
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
			if (this._familyHistoryItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._familyHistoryItem.size(); ++i ) {
					((mmlFclfamilyHistoryItem)this._familyHistoryItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:familyHistory") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclfamilyHistory obj = new mmlFclfamilyHistory();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclfamilyHistory)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclfamilyHistory)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclfamilyHistory)builder.getElement()).setQName( getQName() );
			((mmlFclfamilyHistory)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:familyHistory") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:FirstClinicModule")) {
				((mmlFclFirstClinicModule)builder.getParent()).setFamilyHistory((mmlFclfamilyHistory)builder.getElement());
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
	public void setFamilyHistoryItem(Vector _familyHistoryItem) {
		if (this._familyHistoryItem != null) this._familyHistoryItem.removeAllElements();
		// copy entire elements in the vector
		this._familyHistoryItem = new Vector();
		for (int i = 0; i < _familyHistoryItem.size(); ++i) {
			this._familyHistoryItem.addElement( _familyHistoryItem.elementAt(i) );
		}
	}
	public Vector getFamilyHistoryItem() {
		return _familyHistoryItem;
	}
	
}