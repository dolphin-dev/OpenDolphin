/**
 *
 * mmlHiinsuranceClass.java
 * Created on 2002/7/30 10:0:25
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
public class mmlHiinsuranceClass extends MMLObject {
	
	/* fields */
	private String __mmlHiClassCode = null;
	private String __mmlHitableId = null;

	private String text = null;
	
	public mmlHiinsuranceClass() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlHiClassCode != null ) pw.print(" " + "mmlHi:ClassCode" +  "=" + "'" + __mmlHiClassCode + "'");
			if ( __mmlHitableId != null ) pw.print(" " + "mmlHi:tableId" +  "=" + "'" + __mmlHitableId + "'");

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
		if (qName.equals("mmlHi:insuranceClass") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHiinsuranceClass obj = new mmlHiinsuranceClass();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHiinsuranceClass)builder.getElement()).setNamespace( getNamespace() );
			((mmlHiinsuranceClass)builder.getElement()).setLocalName( getLocalName() );
			((mmlHiinsuranceClass)builder.getElement()).setQName( getQName() );
			((mmlHiinsuranceClass)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlHiClassCode( atts.getValue(namespaceURI, "ClassCode") );
				((mmlHiinsuranceClass)builder.getElement()).setMmlHiClassCode( atts.getValue(namespaceURI, "ClassCode") );
				setMmlHitableId( atts.getValue(namespaceURI, "tableId") );
				((mmlHiinsuranceClass)builder.getElement()).setMmlHitableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:insuranceClass") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:HealthInsuranceModule")) {
				((mmlHiHealthInsuranceModule)builder.getParent()).setInsuranceClass((mmlHiinsuranceClass)builder.getElement());
			}

			if (parentElement.getQName().equals("claim:information")) {
				((claiminformation)builder.getParent()).setInsuranceClass((mmlHiinsuranceClass)builder.getElement());
			}

			if (parentElement.getQName().equals("claimA:amountInformation")) {
				((claimAamountInformation)builder.getParent()).setInsuranceClass((mmlHiinsuranceClass)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlHi:insuranceClass")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlHiinsuranceClass)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlHiClassCode(String __mmlHiClassCode) {
		this.__mmlHiClassCode = __mmlHiClassCode;
	}
	public String getMmlHiClassCode() {
		return __mmlHiClassCode;
	}
	public void setMmlHitableId(String __mmlHitableId) {
		this.__mmlHitableId = __mmlHitableId;
	}
	public String getMmlHitableId() {
		return __mmlHitableId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}