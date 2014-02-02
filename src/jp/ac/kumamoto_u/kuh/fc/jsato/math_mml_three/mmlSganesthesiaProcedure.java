/**
 *
 * mmlSganesthesiaProcedure.java
 * Created on 2003/1/4 2:30:9
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
public class mmlSganesthesiaProcedure extends MMLObject {
	
	/* fields */
	private Vector _title = new Vector();
	
	public mmlSganesthesiaProcedure() {
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
			if (this._title != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._title.size(); ++i ) {
					((mmlSgtitle)this._title.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSg:anesthesiaProcedure") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSganesthesiaProcedure obj = new mmlSganesthesiaProcedure();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSganesthesiaProcedure)builder.getElement()).setNamespace( getNamespace() );
			((mmlSganesthesiaProcedure)builder.getElement()).setLocalName( getLocalName() );
			((mmlSganesthesiaProcedure)builder.getElement()).setQName( getQName() );
			((mmlSganesthesiaProcedure)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:anesthesiaProcedure") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgeryItem")) {
				((mmlSgsurgeryItem)builder.getParent()).set_anesthesiaProcedure((mmlSganesthesiaProcedure)builder.getElement());
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
	public void set_title(Vector _title) {
		if (this._title != null) this._title.removeAllElements();
		// copy entire elements in the vector
		this._title = new Vector();
		for (int i = 0; i < _title.size(); ++i) {
			this._title.addElement( _title.elementAt(i) );
		}
	}
	public Vector get_title() {
		return _title;
	}
	
}