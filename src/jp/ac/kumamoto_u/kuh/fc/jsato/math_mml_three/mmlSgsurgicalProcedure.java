/**
 *
 * mmlSgsurgicalProcedure.java
 * Created on 2003/1/4 2:30:8
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
public class mmlSgsurgicalProcedure extends MMLObject {
	
	/* fields */
	private Vector _procedureItem = new Vector();
	
	public mmlSgsurgicalProcedure() {
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
			if (this._procedureItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._procedureItem.size(); ++i ) {
					((mmlSgprocedureItem)this._procedureItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSg:surgicalProcedure") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgsurgicalProcedure obj = new mmlSgsurgicalProcedure();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgsurgicalProcedure)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgsurgicalProcedure)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgsurgicalProcedure)builder.getElement()).setQName( getQName() );
			((mmlSgsurgicalProcedure)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:surgicalProcedure") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgeryItem")) {
				((mmlSgsurgeryItem)builder.getParent()).set_surgicalProcedure((mmlSgsurgicalProcedure)builder.getElement());
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
	public void set_procedureItem(Vector _procedureItem) {
		if (this._procedureItem != null) this._procedureItem.removeAllElements();
		// copy entire elements in the vector
		this._procedureItem = new Vector();
		for (int i = 0; i < _procedureItem.size(); ++i) {
			this._procedureItem.addElement( _procedureItem.elementAt(i) );
		}
	}
	public Vector get_procedureItem() {
		return _procedureItem;
	}
	
}