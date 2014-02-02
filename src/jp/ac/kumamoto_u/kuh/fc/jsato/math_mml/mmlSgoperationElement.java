/**
 *
 * mmlSgoperationElement.java
 * Created on 2002/7/30 10:0:32
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
public class mmlSgoperationElement extends MMLObject {
	
	/* fields */
	private Vector _operationElementItem = new Vector();
	
	public mmlSgoperationElement() {
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
			if (this._operationElementItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._operationElementItem.size(); ++i ) {
					((mmlSgoperationElementItem)this._operationElementItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSg:operationElement") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgoperationElement obj = new mmlSgoperationElement();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgoperationElement)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgoperationElement)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgoperationElement)builder.getElement()).setQName( getQName() );
			((mmlSgoperationElement)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:operationElement") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:procedureItem")) {
				((mmlSgprocedureItem)builder.getParent()).setOperationElement((mmlSgoperationElement)builder.getElement());
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
	public void setOperationElementItem(Vector _operationElementItem) {
		if (this._operationElementItem != null) this._operationElementItem.removeAllElements();
		// copy entire elements in the vector
		this._operationElementItem = new Vector();
		for (int i = 0; i < _operationElementItem.size(); ++i) {
			this._operationElementItem.addElement( _operationElementItem.elementAt(i) );
		}
	}
	public Vector getOperationElementItem() {
		return _operationElementItem;
	}
	
}