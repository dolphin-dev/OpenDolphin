/**
 *
 * mmlPcreferenceInfo.java
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
public class mmlPcreferenceInfo extends MMLObject {
	
	/* fields */
	private Vector _extRef = new Vector();
	
	public mmlPcreferenceInfo() {
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
		if (qName.equals("mmlPc:referenceInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcreferenceInfo obj = new mmlPcreferenceInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcreferenceInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcreferenceInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcreferenceInfo)builder.getElement()).setQName( getQName() );
			((mmlPcreferenceInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:referenceInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:physicalExamItem")) {
				((mmlPcphysicalExamItem)builder.getParent()).set_referenceInfo((mmlPcreferenceInfo)builder.getElement());
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
	public void set_extRef(Vector _extRef) {
		if (this._extRef != null) this._extRef.removeAllElements();
		// copy entire elements in the vector
		this._extRef = new Vector();
		for (int i = 0; i < _extRef.size(); ++i) {
			this._extRef.addElement( _extRef.elementAt(i) );
		}
	}
	public Vector get_extRef() {
		return _extRef;
	}
	
}