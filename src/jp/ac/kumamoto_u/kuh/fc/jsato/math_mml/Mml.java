/**
 *
 * Mml.java
 * Created on 2002/7/30 10:0:24
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
public class Mml extends MMLObject {
	
	/* fields */
	private String __version = null;
	private String __createDate = null;

	private MmlHeader _MmlHeader = null;
	private MmlBody _MmlBody = null;
	
	public Mml() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __version != null ) pw.print(" " + "version" +  "=" + "'" + __version + "'");
			if ( __createDate != null ) pw.print(" " + "createDate" +  "=" + "'" + __createDate + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _MmlHeader != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_MmlHeader.printObject(pw, visitor);
			}
			if ( _MmlBody != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_MmlBody.printObject(pw, visitor);
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
		if (qName.equals("Mml") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			// Mml is root element.
			
			/* create tree node */
			Mml obj = new Mml();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((Mml)builder.getElement()).setNamespace( getNamespace() );
			((Mml)builder.getElement()).setLocalName( getLocalName() );
			((Mml)builder.getElement()).setQName( getQName() );
			((Mml)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setVersion( atts.getValue(namespaceURI, "version") );
				((Mml)builder.getElement()).setVersion( atts.getValue(namespaceURI, "version") );
				setCreateDate( atts.getValue(namespaceURI, "createDate") );
				((Mml)builder.getElement()).setCreateDate( atts.getValue(namespaceURI, "createDate") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("Mml") == true) {
			
			/* connection */
			
			// Mml is root element.


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	
	
	/* setters and getters */
	public void setVersion(String __version) {
		this.__version = __version;
	}
	public String getVersion() {
		return __version;
	}
	public void setCreateDate(String __createDate) {
		this.__createDate = __createDate;
	}
	public String getCreateDate() {
		return __createDate;
	}

	public void setMmlHeader(MmlHeader _MmlHeader) {
		this._MmlHeader = _MmlHeader;
	}
	public MmlHeader getMmlHeader() {
		return _MmlHeader;
	}
	public void setMmlBody(MmlBody _MmlBody) {
		this._MmlBody = _MmlBody;
	}
	public MmlBody getMmlBody() {
		return _MmlBody;
	}
	
}