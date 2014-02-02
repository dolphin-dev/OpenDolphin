/**
 *
 * mmlLblaboratoryCenter.java
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
public class mmlLblaboratoryCenter extends MMLObject {
	
	/* fields */
	private String __mmlLbcenterCode = null;
	private String __mmlLbcenterCodeId = null;

	private String text = null;
	
	public mmlLblaboratoryCenter() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlLbcenterCode != null ) pw.print(" " + "mmlLb:centerCode" +  "=" + "'" + __mmlLbcenterCode + "'");
			if ( __mmlLbcenterCodeId != null ) pw.print(" " + "mmlLb:centerCodeId" +  "=" + "'" + __mmlLbcenterCodeId + "'");

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
		if (qName.equals("mmlLb:laboratoryCenter") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLblaboratoryCenter obj = new mmlLblaboratoryCenter();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLblaboratoryCenter)builder.getElement()).setNamespace( getNamespace() );
			((mmlLblaboratoryCenter)builder.getElement()).setLocalName( getLocalName() );
			((mmlLblaboratoryCenter)builder.getElement()).setQName( getQName() );
			((mmlLblaboratoryCenter)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlLbcenterCode( atts.getValue(namespaceURI, "centerCode") );
				((mmlLblaboratoryCenter)builder.getElement()).setMmlLbcenterCode( atts.getValue(namespaceURI, "centerCode") );
				setMmlLbcenterCodeId( atts.getValue(namespaceURI, "centerCodeId") );
				((mmlLblaboratoryCenter)builder.getElement()).setMmlLbcenterCodeId( atts.getValue(namespaceURI, "centerCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:laboratoryCenter") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:information")) {
				((mmlLbinformation)builder.getParent()).setLaboratoryCenter((mmlLblaboratoryCenter)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlLb:laboratoryCenter")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlLblaboratoryCenter)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlLbcenterCode(String __mmlLbcenterCode) {
		this.__mmlLbcenterCode = __mmlLbcenterCode;
	}
	public String getMmlLbcenterCode() {
		return __mmlLbcenterCode;
	}
	public void setMmlLbcenterCodeId(String __mmlLbcenterCodeId) {
		this.__mmlLbcenterCodeId = __mmlLbcenterCodeId;
	}
	public String getMmlLbcenterCodeId() {
		return __mmlLbcenterCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}