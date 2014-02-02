/**
 *
 * MmlAddendum.java
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
public class MmlAddendum extends MMLObject {
	
	/* fields */
	private String __contentName = null;
	private String __contentEncoding = null;
	private String __contentType = null;
	private String __medicalRole = null;
	private String __title = null;

	private String text = null;
	
	public MmlAddendum() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __contentName != null ) pw.print(" " + "contentName" +  "=" + "'" + __contentName + "'");
			if ( __contentEncoding != null ) pw.print(" " + "contentEncoding" +  "=" + "'" + __contentEncoding + "'");
			if ( __contentType != null ) pw.print(" " + "contentType" +  "=" + "'" + __contentType + "'");
			if ( __medicalRole != null ) pw.print(" " + "medicalRole" +  "=" + "'" + __medicalRole + "'");
			if ( __title != null ) pw.print(" " + "title" +  "=" + "'" + __title + "'");

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
		if (qName.equals("MmlAddendum") == true) {
			super.buildStart(namespaceURI,localName,qName,atts, builder);
			// Mml,loginRequest,loginResult,and MmlAddendum appear as root element.
			
			/* create tree node */
			MmlAddendum obj = new MmlAddendum();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((MmlAddendum)builder.getElement()).setNamespace( getNamespace() );
			((MmlAddendum)builder.getElement()).setLocalName( getLocalName() );
			((MmlAddendum)builder.getElement()).setQName( getQName() );
			((MmlAddendum)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setContentName( atts.getValue(namespaceURI, "contentName") );
				((MmlAddendum)builder.getElement()).setContentName( atts.getValue(namespaceURI, "contentName") );
				setContentEncoding( atts.getValue(namespaceURI, "contentEncoding") );
				((MmlAddendum)builder.getElement()).setContentEncoding( atts.getValue(namespaceURI, "contentEncoding") );
				setContentType( atts.getValue(namespaceURI, "contentType") );
				((MmlAddendum)builder.getElement()).setContentType( atts.getValue(namespaceURI, "contentType") );
				setMedicalRole( atts.getValue(namespaceURI, "medicalRole") );
				((MmlAddendum)builder.getElement()).setMedicalRole( atts.getValue(namespaceURI, "medicalRole") );
				setTitle( atts.getValue(namespaceURI, "title") );
				((MmlAddendum)builder.getElement()).setTitle( atts.getValue(namespaceURI, "title") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("MmlAddendum") == true) {
			
			/* connection */
			
			// Mml,loginRequest,loginResult,and MmlAddendum appear as root element.

			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
		if (builder.getCurrentElement().getQName().equals("MmlAddendum")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			// Mml,loginRequest,loginResult,and MmlAddendum appear as root element.
			//
			//setText(buffer.toString());
			//((MmlAddendum)builder.getElement()).setText(getText());
			//Mml,loginRequest,loginResult,and MmlAddendum appear as root element.
			//******************************************************
			//DEBUG
			//System.out.println("****** DEBUG START ******");
			//System.out.println(getText());
			//System.out.println("****** DEBUG END ******");
			//******************************************************
			//concatenate string each time the characters() for MmlAddendum body is called.
			//note:
			//because the SAX parser does not always detect whole body in MmlAddendum in one characters() call,
			//all strings should be joined.
			//
			setText(buffer.toString());
			if (((MmlAddendum)builder.getElement()).getText() == null) 
			{
				((MmlAddendum)builder.getElement()).setText(
					getText()
				);
			} else {
				((MmlAddendum)builder.getElement()).setText(
					((MmlAddendum)builder.getElement()).getText()+getText()
				);
			}
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setContentName(String __contentName) {
		this.__contentName = __contentName;
	}
	public String getContentName() {
		return __contentName;
	}
	public void setContentEncoding(String __contentEncoding) {
		this.__contentEncoding = __contentEncoding;
	}
	public String getContentEncoding() {
		return __contentEncoding;
	}
	public void setContentType(String __contentType) {
		this.__contentType = __contentType;
	}
	public String getContentType() {
		return __contentType;
	}
	public void setMedicalRole(String __medicalRole) {
		this.__medicalRole = __medicalRole;
	}
	public String getMedicalRole() {
		return __medicalRole;
	}
	public void setTitle(String __title) {
		this.__title = __title;
	}
	public String getTitle() {
		return __title;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}