/**
 *
 * mmlHipaymentRatio.java
 * Created on 2003/1/4 2:30:2
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
public class mmlHipaymentRatio extends MMLObject {
	
	/* fields */
	private String __mmlHiratioType = null;

	private String text = null;
	
	public mmlHipaymentRatio() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlHiratioType != null ) pw.print(" " + "mmlHi:ratioType" +  "=" + "'" + __mmlHiratioType + "'");

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
		if (qName.equals("mmlHi:paymentRatio") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHipaymentRatio obj = new mmlHipaymentRatio();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHipaymentRatio)builder.getElement()).setNamespace( getNamespace() );
			((mmlHipaymentRatio)builder.getElement()).setLocalName( getLocalName() );
			((mmlHipaymentRatio)builder.getElement()).setQName( getQName() );
			((mmlHipaymentRatio)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlHi:ratioType") ) {
						set__mmlHiratioType( atts.getValue(i) );
						((mmlHipaymentRatio)builder.getElement()).set__mmlHiratioType( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:paymentRatio") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:publicInsuranceItem")) {
				((mmlHipublicInsuranceItem)builder.getParent()).set_paymentRatio((mmlHipaymentRatio)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlHi:paymentRatio")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlHipaymentRatio)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlHiratioType(String __mmlHiratioType) {
		this.__mmlHiratioType = __mmlHiratioType;
	}
	public String get__mmlHiratioType() {
		return __mmlHiratioType;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}