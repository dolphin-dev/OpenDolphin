/**
 *
 * mmlCmId.java
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
public class mmlCmId extends MMLObject {
	
	/* fields */
	private String __mmlCmtype = null;
	private String __mmlCmcheckDigitSchema = null;
	private String __mmlCmcheckDigit = null;
	private String __mmlCmtableId = null;

	private String text = null;
	
	public mmlCmId() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlCmtype != null ) pw.print(" " + "mmlCm:type" +  "=" + "'" + __mmlCmtype + "'");
			if ( __mmlCmcheckDigitSchema != null ) pw.print(" " + "mmlCm:checkDigitSchema" +  "=" + "'" + __mmlCmcheckDigitSchema + "'");
			if ( __mmlCmcheckDigit != null ) pw.print(" " + "mmlCm:checkDigit" +  "=" + "'" + __mmlCmcheckDigit + "'");
			if ( __mmlCmtableId != null ) pw.print(" " + "mmlCm:tableId" +  "=" + "'" + __mmlCmtableId + "'");

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
		if (qName.equals("mmlCm:Id") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlCmId obj = new mmlCmId();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlCmId)builder.getElement()).setNamespace( getNamespace() );
			((mmlCmId)builder.getElement()).setLocalName( getLocalName() );
			((mmlCmId)builder.getElement()).setQName( getQName() );
			((mmlCmId)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlCmtype( atts.getValue(namespaceURI, "type") );
				((mmlCmId)builder.getElement()).setMmlCmtype( atts.getValue(namespaceURI, "type") );
				setMmlCmcheckDigitSchema( atts.getValue(namespaceURI, "checkDigitSchema") );
				((mmlCmId)builder.getElement()).setMmlCmcheckDigitSchema( atts.getValue(namespaceURI, "checkDigitSchema") );
				setMmlCmcheckDigit( atts.getValue(namespaceURI, "checkDigit") );
				((mmlCmId)builder.getElement()).setMmlCmcheckDigit( atts.getValue(namespaceURI, "checkDigit") );
				setMmlCmtableId( atts.getValue(namespaceURI, "tableId") );
				((mmlCmId)builder.getElement()).setMmlCmtableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlCm:Id") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPsi:PersonalizedInfo")) {
				((mmlPsiPersonalizedInfo)builder.getParent()).setId((mmlCmId)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:otherId")) {
				((mmlPiotherId)builder.getParent()).setId((mmlCmId)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPi:masterId")) {
				((mmlPimasterId)builder.getParent()).setId((mmlCmId)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlFc:Facility")) {
				((mmlFcFacility)builder.getParent()).setId((mmlCmId)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlDp:Department")) {
				((mmlDpDepartment)builder.getParent()).setId((mmlCmId)builder.getElement());
			}

			if (parentElement.getQName().equals("masterId")) {
				((masterId)builder.getParent()).setId((mmlCmId)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlCm:Id")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlCmId)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlCmtype(String __mmlCmtype) {
		this.__mmlCmtype = __mmlCmtype;
	}
	public String getMmlCmtype() {
		return __mmlCmtype;
	}
	public void setMmlCmcheckDigitSchema(String __mmlCmcheckDigitSchema) {
		this.__mmlCmcheckDigitSchema = __mmlCmcheckDigitSchema;
	}
	public String getMmlCmcheckDigitSchema() {
		return __mmlCmcheckDigitSchema;
	}
	public void setMmlCmcheckDigit(String __mmlCmcheckDigit) {
		this.__mmlCmcheckDigit = __mmlCmcheckDigit;
	}
	public String getMmlCmcheckDigit() {
		return __mmlCmcheckDigit;
	}
	public void setMmlCmtableId(String __mmlCmtableId) {
		this.__mmlCmtableId = __mmlCmtableId;
	}
	public String getMmlCmtableId() {
		return __mmlCmtableId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}