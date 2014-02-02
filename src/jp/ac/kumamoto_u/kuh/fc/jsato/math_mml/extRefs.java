/**
 *
 * extRefs.java
 * Created on 2002/7/30 10:0:25
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
public class extRefs extends MMLObject {
	
	/* fields */
	private Vector _extRef = new Vector();
	
	public extRefs() {
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
			if (this._extRef != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._extRef.size(); ++i ) {
					((mmlCmextRef)this._extRef.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("extRefs") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			extRefs obj = new extRefs();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((extRefs)builder.getElement()).setNamespace( getNamespace() );
			((extRefs)builder.getElement()).setLocalName( getLocalName() );
			((extRefs)builder.getElement()).setQName( getQName() );
			((extRefs)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("extRefs") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("docInfo")) {
				((docInfo)builder.getParent()).setExtRefs((extRefs)builder.getElement());
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
	public void setExtRef(Vector _extRef) {
		if (this._extRef != null) this._extRef.removeAllElements();
		// copy entire elements in the vector
		this._extRef = new Vector();
		for (int i = 0; i < _extRef.size(); ++i) {
			this._extRef.addElement( _extRef.elementAt(i) );
		}
	}
	public Vector getExtRef() {
		return _extRef;
	}
	
}