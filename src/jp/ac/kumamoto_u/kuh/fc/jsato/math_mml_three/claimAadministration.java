/**
 *
 * claimAadministration.java
 * Created on 2003/1/4 2:30:27
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
public class claimAadministration extends MMLObject {
	
	/* fields */
	private String __claimAadminCode = null;
	private String __claimAadminCodeId = null;

	private String text = null;
	
	public claimAadministration() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAadminCode != null ) pw.print(" " + "claimA:adminCode" +  "=" + "'" + __claimAadminCode + "'");
			if ( __claimAadminCodeId != null ) pw.print(" " + "claimA:adminCodeId" +  "=" + "'" + __claimAadminCodeId + "'");

			if ( this.getLocalName().equals("levelone") ) {
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
		if (qName.equals("claimA:administration") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAadministration obj = new claimAadministration();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAadministration)builder.getElement()).setNamespace( getNamespace() );
			((claimAadministration)builder.getElement()).setLocalName( getLocalName() );
			((claimAadministration)builder.getElement()).setQName( getQName() );
			((claimAadministration)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claimA:adminCode") ) {
						set__claimAadminCode( atts.getValue(i) );
						((claimAadministration)builder.getElement()).set__claimAadminCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:adminCodeId") ) {
						set__claimAadminCodeId( atts.getValue(i) );
						((claimAadministration)builder.getElement()).set__claimAadminCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:administration") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:bundle")) {
				((claimAbundle)builder.getParent()).set_administration((claimAadministration)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("claimA:administration")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimAadministration)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__claimAadminCode(String __claimAadminCode) {
		this.__claimAadminCode = __claimAadminCode;
	}
	public String get__claimAadminCode() {
		return __claimAadminCode;
	}
	public void set__claimAadminCodeId(String __claimAadminCodeId) {
		this.__claimAadminCodeId = __claimAadminCodeId;
	}
	public String get__claimAadminCodeId() {
		return __claimAadminCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}