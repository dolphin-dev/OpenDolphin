/**
 *
 * claimAfilmSize.java
 * Created on 2002/7/30 10:0:40
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class claimAfilmSize extends MMLObject {
	
	/* fields */
	private String __claimAsizeCode = null;
	private String __claimAsizeCodeId = null;

	private String text = null;
	
	public claimAfilmSize() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAsizeCode != null ) pw.print(" " + "claimA:sizeCode" +  "=" + "'" + __claimAsizeCode + "'");
			if ( __claimAsizeCodeId != null ) pw.print(" " + "claimA:sizeCodeId" +  "=" + "'" + __claimAsizeCodeId + "'");

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
		if (qName.equals("claimA:filmSize") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAfilmSize obj = new claimAfilmSize();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAfilmSize)builder.getElement()).setNamespace( getNamespace() );
			((claimAfilmSize)builder.getElement()).setLocalName( getLocalName() );
			((claimAfilmSize)builder.getElement()).setQName( getQName() );
			((claimAfilmSize)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimAsizeCode( atts.getValue(namespaceURI, "sizeCode") );
				((claimAfilmSize)builder.getElement()).setClaimAsizeCode( atts.getValue(namespaceURI, "sizeCode") );
				setClaimAsizeCodeId( atts.getValue(namespaceURI, "sizeCodeId") );
				((claimAfilmSize)builder.getElement()).setClaimAsizeCodeId( atts.getValue(namespaceURI, "sizeCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:filmSize") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:film")) {
				((claimAfilm)builder.getParent()).setFilmSize((claimAfilmSize)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("claimA:filmSize")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimAfilmSize)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setClaimAsizeCode(String __claimAsizeCode) {
		this.__claimAsizeCode = __claimAsizeCode;
	}
	public String getClaimAsizeCode() {
		return __claimAsizeCode;
	}
	public void setClaimAsizeCodeId(String __claimAsizeCodeId) {
		this.__claimAsizeCodeId = __claimAsizeCodeId;
	}
	public String getClaimAsizeCodeId() {
		return __claimAsizeCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}