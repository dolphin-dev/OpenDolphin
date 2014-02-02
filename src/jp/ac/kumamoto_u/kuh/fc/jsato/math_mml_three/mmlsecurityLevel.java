/**
 *
 * mmlsecurityLevel.java
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
public class mmlsecurityLevel extends MMLObject {
	
	/* fields */
	private Vector _accessRight = new Vector();
	
	public mmlsecurityLevel() {
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
			if (this._accessRight != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._accessRight.size(); ++i ) {
					((mmlaccessRight)this._accessRight.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mml:securityLevel") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlsecurityLevel obj = new mmlsecurityLevel();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlsecurityLevel)builder.getElement()).setNamespace( getNamespace() );
			((mmlsecurityLevel)builder.getElement()).setLocalName( getLocalName() );
			((mmlsecurityLevel)builder.getElement()).setQName( getQName() );
			((mmlsecurityLevel)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:securityLevel") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:docInfo")) {
				((mmldocInfo)builder.getParent()).set_securityLevel((mmlsecurityLevel)builder.getElement());
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
	public void set_accessRight(Vector _accessRight) {
		if (this._accessRight != null) this._accessRight.removeAllElements();
		// copy entire elements in the vector
		this._accessRight = new Vector();
		for (int i = 0; i < _accessRight.size(); ++i) {
			this._accessRight.addElement( _accessRight.elementAt(i) );
		}
	}
	public Vector get_accessRight() {
		return _accessRight;
	}
	
}