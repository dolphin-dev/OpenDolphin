/**
 *
 * mmlPsipersonName.java
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
public class mmlPsipersonName extends MMLObject {
	
	/* fields */
	private Vector _Name = new Vector();
	
	public mmlPsipersonName() {
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
			if (this._Name != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._Name.size(); ++i ) {
					((mmlNmName)this._Name.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlPsi:personName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPsipersonName obj = new mmlPsipersonName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPsipersonName)builder.getElement()).setNamespace( getNamespace() );
			((mmlPsipersonName)builder.getElement()).setLocalName( getLocalName() );
			((mmlPsipersonName)builder.getElement()).setQName( getQName() );
			((mmlPsipersonName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPsi:personName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:PersonalizedInfo")) {
				((mmlPsiPersonalizedInfo)builder.getParent()).set_personName((mmlPsipersonName)builder.getElement());
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
	public void set_Name(Vector _Name) {
		if (this._Name != null) this._Name.removeAllElements();
		// copy entire elements in the vector
		this._Name = new Vector();
		for (int i = 0; i < _Name.size(); ++i) {
			this._Name.addElement( _Name.elementAt(i) );
		}
	}
	public Vector get_Name() {
		return _Name;
	}
	
}