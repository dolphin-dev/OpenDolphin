/**
 *
 * mmlBcothers.java
 * Created on 2003/1/4 2:30:4
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
public class mmlBcothers extends MMLObject {
	
	/* fields */
	private Vector _other = new Vector();
	
	public mmlBcothers() {
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
			if (this._other != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._other.size(); ++i ) {
					((mmlBcother)this._other.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlBc:others") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcothers obj = new mmlBcothers();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcothers)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcothers)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcothers)builder.getElement()).setQName( getQName() );
			((mmlBcothers)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:others") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:bloodtype")) {
				((mmlBcbloodtype)builder.getParent()).set_others((mmlBcothers)builder.getElement());
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
	public void set_other(Vector _other) {
		if (this._other != null) this._other.removeAllElements();
		// copy entire elements in the vector
		this._other = new Vector();
		for (int i = 0; i < _other.size(); ++i) {
			this._other.addElement( _other.elementAt(i) );
		}
	}
	public Vector get_other() {
		return _other;
	}
	
}