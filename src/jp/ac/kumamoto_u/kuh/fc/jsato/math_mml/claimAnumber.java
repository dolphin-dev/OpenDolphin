/**
 *
 * claimAnumber.java
 * Created on 2002/7/30 10:0:40
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import java.util.*;
import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class claimAnumber extends MMLObject {
	
	/* fields */
	private String __claimAnumberCode = null;
	private String __claimAnumberCodeId = null;
	private String __claimAunit = null;

	private String text = null;
	
	public claimAnumber() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAnumberCode != null ) pw.print(" " + "claimA:numberCode" +  "=" + "'" + __claimAnumberCode + "'");
			if ( __claimAnumberCodeId != null ) pw.print(" " + "claimA:numberCodeId" +  "=" + "'" + __claimAnumberCodeId + "'");
			if ( __claimAunit != null ) pw.print(" " + "claimA:unit" +  "=" + "'" + __claimAunit + "'");

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
		if (qName.equals("claimA:number") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAnumber obj = new claimAnumber();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAnumber)builder.getElement()).setNamespace( getNamespace() );
			((claimAnumber)builder.getElement()).setLocalName( getLocalName() );
			((claimAnumber)builder.getElement()).setQName( getQName() );
			((claimAnumber)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimAnumberCode( atts.getValue(namespaceURI, "numberCode") );
				((claimAnumber)builder.getElement()).setClaimAnumberCode( atts.getValue(namespaceURI, "numberCode") );
				setClaimAnumberCodeId( atts.getValue(namespaceURI, "numberCodeId") );
				((claimAnumber)builder.getElement()).setClaimAnumberCodeId( atts.getValue(namespaceURI, "numberCodeId") );
				setClaimAunit( atts.getValue(namespaceURI, "unit") );
				((claimAnumber)builder.getElement()).setClaimAunit( atts.getValue(namespaceURI, "unit") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:number") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:item")) {
				Vector v = ((claimAitem)builder.getParent()).getNumber();
				v.addElement(builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("claimA:number")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimAnumber)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setClaimAnumberCode(String __claimAnumberCode) {
		this.__claimAnumberCode = __claimAnumberCode;
	}
	public String getClaimAnumberCode() {
		return __claimAnumberCode;
	}
	public void setClaimAnumberCodeId(String __claimAnumberCodeId) {
		this.__claimAnumberCodeId = __claimAnumberCodeId;
	}
	public String getClaimAnumberCodeId() {
		return __claimAnumberCodeId;
	}
	public void setClaimAunit(String __claimAunit) {
		this.__claimAunit = __claimAunit;
	}
	public String getClaimAunit() {
		return __claimAunit;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}