/**
 *
 * mmlPcphysicalExam.java
 * Created on 2003/1/4 2:30:6
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
public class mmlPcphysicalExam extends MMLObject {
	
	/* fields */
	private Vector _physicalExamItem = new Vector();
	
	public mmlPcphysicalExam() {
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
			if (this._physicalExamItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._physicalExamItem.size(); ++i ) {
					((mmlPcphysicalExamItem)this._physicalExamItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlPc:physicalExam") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcphysicalExam obj = new mmlPcphysicalExam();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcphysicalExam)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcphysicalExam)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcphysicalExam)builder.getElement()).setQName( getQName() );
			((mmlPcphysicalExam)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:physicalExam") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:objective")) {
				((mmlPcobjective)builder.getParent()).set_physicalExam((mmlPcphysicalExam)builder.getElement());
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
	public void set_physicalExamItem(Vector _physicalExamItem) {
		if (this._physicalExamItem != null) this._physicalExamItem.removeAllElements();
		// copy entire elements in the vector
		this._physicalExamItem = new Vector();
		for (int i = 0; i < _physicalExamItem.size(); ++i) {
			this._physicalExamItem.addElement( _physicalExamItem.elementAt(i) );
		}
	}
	public Vector get_physicalExamItem() {
		return _physicalExamItem;
	}
	
}