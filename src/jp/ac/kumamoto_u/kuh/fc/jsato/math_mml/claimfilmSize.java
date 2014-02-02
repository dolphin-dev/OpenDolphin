/**
 *
 * claimfilmSize.java
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
public class claimfilmSize extends MMLObject {
	
	/* fields */
	private String __claimsizeCode = null;
	private String __claimsizeCodeId = null;

	private String text = null;
	
	public claimfilmSize() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimsizeCode != null ) pw.print(" " + "claim:sizeCode" +  "=" + "'" + __claimsizeCode + "'");
			if ( __claimsizeCodeId != null ) pw.print(" " + "claim:sizeCodeId" +  "=" + "'" + __claimsizeCodeId + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			// this element need not to print tab padding before the closing tag.
			visitor.setIgnoreTab( true );
			if (text != null) {
				if ( this.getText().equals("") == false ) pw.print( this.getText() );
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
		if (qName.equals("claim:filmSize") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimfilmSize obj = new claimfilmSize();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimfilmSize)builder.getElement()).setNamespace( getNamespace() );
			((claimfilmSize)builder.getElement()).setLocalName( getLocalName() );
			((claimfilmSize)builder.getElement()).setQName( getQName() );
			((claimfilmSize)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimsizeCode( atts.getValue(namespaceURI, "sizeCode") );
				((claimfilmSize)builder.getElement()).setClaimsizeCode( atts.getValue(namespaceURI, "sizeCode") );
				setClaimsizeCodeId( atts.getValue(namespaceURI, "sizeCodeId") );
				((claimfilmSize)builder.getElement()).setClaimsizeCodeId( atts.getValue(namespaceURI, "sizeCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:filmSize") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:film")) {
				((claimfilm)builder.getParent()).setFilmSize((claimfilmSize)builder.getElement());
			}

			
			printlnStatus(parentElement.getQName()+" /"+qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
		if (builder.getCurrentElement().getQName().equals("claim:filmSize")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimfilmSize)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setClaimsizeCode(String __claimsizeCode) {
		this.__claimsizeCode = __claimsizeCode;
	}
	public String getClaimsizeCode() {
		return __claimsizeCode;
	}
	public void setClaimsizeCodeId(String __claimsizeCodeId) {
		this.__claimsizeCodeId = __claimsizeCodeId;
	}
	public String getClaimsizeCodeId() {
		return __claimsizeCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}