/**
 *
 * claimadministration.java
 * Created on 2002/7/30 10:0:39
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class claimadministration extends MMLObject {
	
	/* fields */
	private String __claimadminCode = null;
	private String __claimadminCodeId = null;

	private String text = null;
	
	public claimadministration() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimadminCode != null ) pw.print(" " + "claim:adminCode" +  "=" + "'" + __claimadminCode + "'");
			if ( __claimadminCodeId != null ) pw.print(" " + "claim:adminCodeId" +  "=" + "'" + __claimadminCodeId + "'");

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
		if (qName.equals("claim:administration") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimadministration obj = new claimadministration();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimadministration)builder.getElement()).setNamespace( getNamespace() );
			((claimadministration)builder.getElement()).setLocalName( getLocalName() );
			((claimadministration)builder.getElement()).setQName( getQName() );
			((claimadministration)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimadminCode( atts.getValue(namespaceURI, "adminCode") );
				((claimadministration)builder.getElement()).setClaimadminCode( atts.getValue(namespaceURI, "adminCode") );
				setClaimadminCodeId( atts.getValue(namespaceURI, "adminCodeId") );
				((claimadministration)builder.getElement()).setClaimadminCodeId( atts.getValue(namespaceURI, "adminCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:administration") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:bundle")) {
				((claimbundle)builder.getParent()).setAdministration((claimadministration)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("claim:administration")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimadministration)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setClaimadminCode(String __claimadminCode) {
		this.__claimadminCode = __claimadminCode;
	}
	public String getClaimadminCode() {
		return __claimadminCode;
	}
	public void setClaimadminCodeId(String __claimadminCodeId) {
		this.__claimadminCodeId = __claimadminCodeId;
	}
	public String getClaimadminCodeId() {
		return __claimadminCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}