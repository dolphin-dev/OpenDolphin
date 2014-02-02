/**
 *
 * mmlFclpastHistory.java
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
public class mmlFclpastHistory extends MMLObject {
	
	/* fields */
	private mmlFclfreeNotes _freeNotes = null;
	private Vector _pastHistoryItem = new Vector();
	
	public mmlFclpastHistory() {
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
			if ( _freeNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_freeNotes.printObject(pw, visitor);
			}
			if (this._pastHistoryItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._pastHistoryItem.size(); ++i ) {
					((mmlFclpastHistoryItem)this._pastHistoryItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:pastHistory") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclpastHistory obj = new mmlFclpastHistory();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclpastHistory)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclpastHistory)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclpastHistory)builder.getElement()).setQName( getQName() );
			((mmlFclpastHistory)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:pastHistory") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:FirstClinicModule")) {
				((mmlFclFirstClinicModule)builder.getParent()).set_pastHistory((mmlFclpastHistory)builder.getElement());
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
	public void set_freeNotes(mmlFclfreeNotes _freeNotes) {
		this._freeNotes = _freeNotes;
	}
	public mmlFclfreeNotes get_freeNotes() {
		return _freeNotes;
	}
	public void set_pastHistoryItem(Vector _pastHistoryItem) {
		if (this._pastHistoryItem != null) this._pastHistoryItem.removeAllElements();
		// copy entire elements in the vector
		this._pastHistoryItem = new Vector();
		for (int i = 0; i < _pastHistoryItem.size(); ++i) {
			this._pastHistoryItem.addElement( _pastHistoryItem.elementAt(i) );
		}
	}
	public Vector get_pastHistoryItem() {
		return _pastHistoryItem;
	}
	
}