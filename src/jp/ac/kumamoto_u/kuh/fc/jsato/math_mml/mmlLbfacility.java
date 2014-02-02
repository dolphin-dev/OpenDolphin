/**
 *
 * mmlLbfacility.java
 * Created on 2002/7/30 10:0:35
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
public class mmlLbfacility extends MMLObject {
	
	/* fields */
	private String __mmlLbfacilityCode = null;
	private String __mmlLbfacilityCodeId = null;

	private String text = null;
	
	public mmlLbfacility() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbfacilityCode != null ) pw.print(" " + "mmlLb:facilityCode" +  "=" + "'" + __mmlLbfacilityCode + "'");
			if ( __mmlLbfacilityCodeId != null ) pw.print(" " + "mmlLb:facilityCodeId" +  "=" + "'" + __mmlLbfacilityCodeId + "'");

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
		if (qName.equals("mmlLb:facility") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbfacility obj = new mmlLbfacility();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbfacility)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbfacility)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbfacility)builder.getElement()).setQName( getQName() );
			((mmlLbfacility)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlLbfacilityCode( atts.getValue(namespaceURI, "facilityCode") );
				((mmlLbfacility)builder.getElement()).setMmlLbfacilityCode( atts.getValue(namespaceURI, "facilityCode") );
				setMmlLbfacilityCodeId( atts.getValue(namespaceURI, "facilityCodeId") );
				((mmlLbfacility)builder.getElement()).setMmlLbfacilityCodeId( atts.getValue(namespaceURI, "facilityCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:facility") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).setFacility((mmlLbfacility)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:facility")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLbfacility)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlLbfacilityCode(String __mmlLbfacilityCode) {
		this.__mmlLbfacilityCode = __mmlLbfacilityCode;
	}
	public String getMmlLbfacilityCode() {
		return __mmlLbfacilityCode;
	}
	public void setMmlLbfacilityCodeId(String __mmlLbfacilityCodeId) {
		this.__mmlLbfacilityCodeId = __mmlLbfacilityCodeId;
	}
	public String getMmlLbfacilityCodeId() {
		return __mmlLbfacilityCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}