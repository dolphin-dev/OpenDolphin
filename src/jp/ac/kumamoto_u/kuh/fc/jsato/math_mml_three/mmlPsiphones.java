/**
 *
 * mmlPsiphones.java
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
public class mmlPsiphones extends MMLObject {
	
	/* fields */
	private Vector _Phone = new Vector();
	
	public mmlPsiphones() {
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
			if (this._Phone != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._Phone.size(); ++i ) {
					((mmlPhPhone)this._Phone.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlPsi:phones") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPsiphones obj = new mmlPsiphones();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPsiphones)builder.getElement()).setNamespace( getNamespace() );
			((mmlPsiphones)builder.getElement()).setLocalName( getLocalName() );
			((mmlPsiphones)builder.getElement()).setQName( getQName() );
			((mmlPsiphones)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPsi:phones") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:PersonalizedInfo")) {
				((mmlPsiPersonalizedInfo)builder.getParent()).set_phones((mmlPsiphones)builder.getElement());
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
	public void set_Phone(Vector _Phone) {
		if (this._Phone != null) this._Phone.removeAllElements();
		// copy entire elements in the vector
		this._Phone = new Vector();
		for (int i = 0; i < _Phone.size(); ++i) {
			this._Phone.addElement( _Phone.elementAt(i) );
		}
	}
	public Vector get_Phone() {
		return _Phone;
	}
	
}