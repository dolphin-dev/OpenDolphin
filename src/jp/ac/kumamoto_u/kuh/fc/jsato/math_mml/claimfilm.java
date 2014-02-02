/**
 *
 * claimfilm.java
 * Created on 2002/7/30 10:0:40
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
public class claimfilm extends MMLObject {
	
	/* fields */
	private claimfilmSize _filmSize = null;
	private claimfilmNumber _filmNumber = null;
	
	public claimfilm() {
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
		if (qName.equals("claim:film") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimfilm obj = new claimfilm();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimfilm)builder.getElement()).setNamespace( getNamespace() );
			((claimfilm)builder.getElement()).setLocalName( getLocalName() );
			((claimfilm)builder.getElement()).setQName( getQName() );
			((claimfilm)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:film") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:item")) {
				Vector v = ((claimitem)builder.getParent()).getFilm();
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
	public void setFilmSize(claimfilmSize _filmSize) {
		this._filmSize = _filmSize;
	}
	public claimfilmSize getFilmSize() {
		return _filmSize;
	}
	public void setFilmNumber(claimfilmNumber _filmNumber) {
		this._filmNumber = _filmNumber;
	}
	public claimfilmNumber getFilmNumber() {
		return _filmNumber;
	}
	
}