/**
 *
 * mmltoc.java
 * Created on 2003/1/4 2:29:55
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
public class mmltoc extends MMLObject {
	
	/* fields */
	private Vector _tocItem = new Vector();
	
	public mmltoc() {
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
			if (this._tocItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._tocItem.size(); ++i ) {
					((mmltocItem)this._tocItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mml:toc") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmltoc obj = new mmltoc();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmltoc)builder.getElement()).setNamespace( getNamespace() );
			((mmltoc)builder.getElement()).setLocalName( getLocalName() );
			((mmltoc)builder.getElement()).setQName( getQName() );
			((mmltoc)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:toc") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:MmlHeader")) {
				((mmlMmlHeader)builder.getParent()).set_toc((mmltoc)builder.getElement());
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
	public void set_tocItem(Vector _tocItem) {
		if (this._tocItem != null) this._tocItem.removeAllElements();
		// copy entire elements in the vector
		this._tocItem = new Vector();
		for (int i = 0; i < _tocItem.size(); ++i) {
			this._tocItem.addElement( _tocItem.elementAt(i) );
		}
	}
	public Vector get_tocItem() {
		return _tocItem;
	}
	
}