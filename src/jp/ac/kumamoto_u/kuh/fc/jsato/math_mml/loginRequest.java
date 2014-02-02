/**
 *
 * loginRequest.java
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
public class loginRequest extends MMLObject {
	
	/* fields */
	private userId _userId = null;
	private userPassword _userPassword = null;
	
	public loginRequest() {
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
			if ( _userId != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_userId.printObject(pw, visitor);
			}
			if ( _userPassword != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_userPassword.printObject(pw, visitor);
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
		if (qName.equals("loginRequest") == true) {
			super.buildStart(namespaceURI,localName,qName,atts, builder);
			// Mml,loginRequest,loginResult,and MmlAddendum appear as root element.
			
			/* create tree node */
			loginRequest obj = new loginRequest();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((loginRequest)builder.getElement()).setNamespace( getNamespace() );
			((loginRequest)builder.getElement()).setLocalName( getLocalName() );
			((loginRequest)builder.getElement()).setQName( getQName() );
			((loginRequest)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("loginRequest") == true) {
			
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
	public void setUserId(userId _userId) {
		this._userId = _userId;
	}
	public userId getUserId() {
		return _userId;
	}
	public void setUserPassword(userPassword _userPassword) {
		this._userPassword = _userPassword;
	}
	public userPassword getUserPassword() {
		return _userPassword;
	}
	
}