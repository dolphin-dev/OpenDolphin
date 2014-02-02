/**
 *
 * securityLevel.java
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
public class securityLevel extends MMLObject {
	
	/* fields */
	private Vector _accessRight = new Vector();
	
	public securityLevel() {
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
			if (this._accessRight != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._accessRight.size(); ++i ) {
					((accessRight)this._accessRight.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("securityLevel") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			securityLevel obj = new securityLevel();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((securityLevel)builder.getElement()).setNamespace( getNamespace() );
			((securityLevel)builder.getElement()).setLocalName( getLocalName() );
			((securityLevel)builder.getElement()).setQName( getQName() );
			((securityLevel)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("securityLevel") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("docInfo")) {
				((docInfo)builder.getParent()).setSecurityLevel((securityLevel)builder.getElement());
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
	public void setAccessRight(Vector _accessRight) {
		if (this._accessRight != null) this._accessRight.removeAllElements();
		// copy entire elements in the vector
		this._accessRight = new Vector();
		for (int i = 0; i < _accessRight.size(); ++i) {
			this._accessRight.addElement( _accessRight.elementAt(i) );
		}
	}
	public Vector getAccessRight() {
		return _accessRight;
	}
	
}