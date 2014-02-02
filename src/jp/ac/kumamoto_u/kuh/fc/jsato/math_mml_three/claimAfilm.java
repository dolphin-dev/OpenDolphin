/**
 *
 * claimAfilm.java
 * Created on 2003/1/4 2:30:28
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
public class claimAfilm extends MMLObject {
	
	/* fields */
	private claimAfilmSize _filmSize = null;
	private claimAfilmNumber _filmNumber = null;
	
	public claimAfilm() {
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
			if ( _filmSize != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_filmSize.printObject(pw, visitor);
			}
			if ( _filmNumber != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_filmNumber.printObject(pw, visitor);
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
		if (qName.equals("claimA:film") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAfilm obj = new claimAfilm();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAfilm)builder.getElement()).setNamespace( getNamespace() );
			((claimAfilm)builder.getElement()).setLocalName( getLocalName() );
			((claimAfilm)builder.getElement()).setQName( getQName() );
			((claimAfilm)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:film") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:item")) {
				Vector v = ((claimAitem)builder.getParent()).get_film();
				v.addElement(builder.getElement());
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
	public void set_filmSize(claimAfilmSize _filmSize) {
		this._filmSize = _filmSize;
	}
	public claimAfilmSize get_filmSize() {
		return _filmSize;
	}
	public void set_filmNumber(claimAfilmNumber _filmNumber) {
		this._filmNumber = _filmNumber;
	}
	public claimAfilmNumber get_filmNumber() {
		return _filmNumber;
	}
	
}