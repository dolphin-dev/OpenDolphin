/**
 *
 * loginResult.java
 * Created on 2002/11/29 8:49:38
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
public class loginResult extends MMLObject {
	
	/* fields */
	private resultCode _resultCode = null;
	private resultString _resultString = null;
	
	public loginResult() {
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
			if ( _resultCode != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_resultCode.printObject(pw, visitor);
			}
			if ( _resultString != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_resultString.printObject(pw, visitor);
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
		if (qName.equals("loginResult") == true) {
			super.buildStart(namespaceURI,localName,qName,atts, builder);
			// Mml,loginRequest,loginResult,and MmlAddendum appear as root element.
			
			/* create tree node */
			loginResult obj = new loginResult();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((loginResult)builder.getElement()).setNamespace( getNamespace() );
			((loginResult)builder.getElement()).setLocalName( getLocalName() );
			((loginResult)builder.getElement()).setQName( getQName() );
			((loginResult)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("loginResult") == true) {
			
			/* connection */
			
			// Mml,loginRequest,loginResult,and MmlAddendum appear as root element.

			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	
	
	/* setters and getters */
	public void setResultCode(resultCode _resultCode) {
		this._resultCode = _resultCode;
	}
	public resultCode getResultCode() {
		return _resultCode;
	}
	public void setResultString(resultString _resultString) {
		this._resultString = _resultString;
	}
	public resultString getResultString() {
		return _resultString;
	}
	
}