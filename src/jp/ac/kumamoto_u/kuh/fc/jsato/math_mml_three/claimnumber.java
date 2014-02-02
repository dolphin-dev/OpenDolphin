/**
 *
 * claimnumber.java
 * Created on 2003/1/4 2:30:26
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
public class claimnumber extends MMLObject {
	
	/* fields */
	private String __claimnumberCode = null;
	private String __claimnumberCodeId = null;
	private String __claimunit = null;

	private String text = null;
	
	public claimnumber() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimnumberCode != null ) pw.print(" " + "claim:numberCode" +  "=" + "'" + __claimnumberCode + "'");
			if ( __claimnumberCodeId != null ) pw.print(" " + "claim:numberCodeId" +  "=" + "'" + __claimnumberCodeId + "'");
			if ( __claimunit != null ) pw.print(" " + "claim:unit" +  "=" + "'" + __claimunit + "'");

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
		if (qName.equals("claim:number") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimnumber obj = new claimnumber();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimnumber)builder.getElement()).setNamespace( getNamespace() );
			((claimnumber)builder.getElement()).setLocalName( getLocalName() );
			((claimnumber)builder.getElement()).setQName( getQName() );
			((claimnumber)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claim:numberCode") ) {
						set__claimnumberCode( atts.getValue(i) );
						((claimnumber)builder.getElement()).set__claimnumberCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:numberCodeId") ) {
						set__claimnumberCodeId( atts.getValue(i) );
						((claimnumber)builder.getElement()).set__claimnumberCodeId( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:unit") ) {
						set__claimunit( atts.getValue(i) );
						((claimnumber)builder.getElement()).set__claimunit( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:number") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:item")) {
				Vector v = ((claimitem)builder.getParent()).get_number();
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
		if (builder.getCurrentElement().getQName().equals("claim:number")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimnumber)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__claimnumberCode(String __claimnumberCode) {
		this.__claimnumberCode = __claimnumberCode;
	}
	public String get__claimnumberCode() {
		return __claimnumberCode;
	}
	public void set__claimnumberCodeId(String __claimnumberCodeId) {
		this.__claimnumberCodeId = __claimnumberCodeId;
	}
	public String get__claimnumberCodeId() {
		return __claimnumberCodeId;
	}
	public void set__claimunit(String __claimunit) {
		this.__claimunit = __claimunit;
	}
	public String get__claimunit() {
		return __claimunit;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}