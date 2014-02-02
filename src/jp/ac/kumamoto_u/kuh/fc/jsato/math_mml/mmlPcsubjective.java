/**
 *
 * mmlPcsubjective.java
 * Created on 2002/7/30 10:0:28
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
public class mmlPcsubjective extends MMLObject {
	
	/* fields */
	private mmlPcfreeNotes _freeNotes = null;
	private Vector _subjectiveItem = new Vector();
	
	public mmlPcsubjective() {
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
			if ( _freeNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_freeNotes.printObject(pw, visitor);
			}
			if (this._subjectiveItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._subjectiveItem.size(); ++i ) {
					((mmlPcsubjectiveItem)this._subjectiveItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlPc:subjective") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcsubjective obj = new mmlPcsubjective();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcsubjective)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcsubjective)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcsubjective)builder.getElement()).setQName( getQName() );
			((mmlPcsubjective)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:subjective") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:problemItem")) {
				((mmlPcproblemItem)builder.getParent()).setSubjective((mmlPcsubjective)builder.getElement());
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
	public void setFreeNotes(mmlPcfreeNotes _freeNotes) {
		this._freeNotes = _freeNotes;
	}
	public mmlPcfreeNotes getFreeNotes() {
		return _freeNotes;
	}
	public void setSubjectiveItem(Vector _subjectiveItem) {
		if (this._subjectiveItem != null) this._subjectiveItem.removeAllElements();
		// copy entire elements in the vector
		this._subjectiveItem = new Vector();
		for (int i = 0; i < _subjectiveItem.size(); ++i) {
			this._subjectiveItem.addElement( _subjectiveItem.elementAt(i) );
		}
	}
	public Vector getSubjectiveItem() {
		return _subjectiveItem;
	}
	
}