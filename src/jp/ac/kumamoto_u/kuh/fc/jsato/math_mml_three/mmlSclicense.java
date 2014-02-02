/**
 *
 * mmlSclicense.java
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
public class mmlSclicense extends MMLObject {
	
	/* fields */
	private Vector _licenseName = new Vector();
	
	public mmlSclicense() {
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
			if (this._licenseName != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._licenseName.size(); ++i ) {
					((mmlSclicenseName)this._licenseName.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSc:license") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSclicense obj = new mmlSclicense();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSclicense)builder.getElement()).setNamespace( getNamespace() );
			((mmlSclicense)builder.getElement()).setLocalName( getLocalName() );
			((mmlSclicense)builder.getElement()).setQName( getQName() );
			((mmlSclicense)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:license") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:accessRight")) {
				((mmlaccessRight)builder.getParent()).set_license((mmlSclicense)builder.getElement());
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
	public void set_licenseName(Vector _licenseName) {
		if (this._licenseName != null) this._licenseName.removeAllElements();
		// copy entire elements in the vector
		this._licenseName = new Vector();
		for (int i = 0; i < _licenseName.size(); ++i) {
			this._licenseName.addElement( _licenseName.elementAt(i) );
		}
	}
	public Vector get_licenseName() {
		return _licenseName;
	}
	
}