/**
 *
 * mmlPcstructuredExpression.java
 * Created on 2003/1/4 2:30:7
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
public class mmlPcstructuredExpression extends MMLObject {
	
	/* fields */
	private Vector _problemItem = new Vector();
	
	public mmlPcstructuredExpression() {
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
			if (this._problemItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._problemItem.size(); ++i ) {
					((mmlPcproblemItem)this._problemItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlPc:structuredExpression") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcstructuredExpression obj = new mmlPcstructuredExpression();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcstructuredExpression)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcstructuredExpression)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcstructuredExpression)builder.getElement()).setQName( getQName() );
			((mmlPcstructuredExpression)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:structuredExpression") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:ProgressCourseModule")) {
				((mmlPcProgressCourseModule)builder.getParent()).set_structuredExpression((mmlPcstructuredExpression)builder.getElement());
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
	public void set_problemItem(Vector _problemItem) {
		if (this._problemItem != null) this._problemItem.removeAllElements();
		// copy entire elements in the vector
		this._problemItem = new Vector();
		for (int i = 0; i < _problemItem.size(); ++i) {
			this._problemItem.addElement( _problemItem.elementAt(i) );
		}
	}
	public Vector get_problemItem() {
		return _problemItem;
	}
	
}