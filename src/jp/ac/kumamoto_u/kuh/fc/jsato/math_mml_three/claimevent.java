/**
 *
 * claimevent.java
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
public class claimevent extends MMLObject {
	
	/* fields */
	private String __claimeventStart = null;
	private String __claimeventEnd = null;

	private String text = null;
	
	public claimevent() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimeventStart != null ) pw.print(" " + "claim:eventStart" +  "=" + "'" + __claimeventStart + "'");
			if ( __claimeventEnd != null ) pw.print(" " + "claim:eventEnd" +  "=" + "'" + __claimeventEnd + "'");

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
		if (qName.equals("claim:event") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimevent obj = new claimevent();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimevent)builder.getElement()).setNamespace( getNamespace() );
			((claimevent)builder.getElement()).setLocalName( getLocalName() );
			((claimevent)builder.getElement()).setQName( getQName() );
			((claimevent)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claim:eventStart") ) {
						set__claimeventStart( atts.getValue(i) );
						((claimevent)builder.getElement()).set__claimeventStart( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:eventEnd") ) {
						set__claimeventEnd( atts.getValue(i) );
						((claimevent)builder.getElement()).set__claimeventEnd( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:event") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:item")) {
				((claimitem)builder.getParent()).set_event((claimevent)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("claim:event")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimevent)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__claimeventStart(String __claimeventStart) {
		this.__claimeventStart = __claimeventStart;
	}
	public String get__claimeventStart() {
		return __claimeventStart;
	}
	public void set__claimeventEnd(String __claimeventEnd) {
		this.__claimeventEnd = __claimeventEnd;
	}
	public String get__claimeventEnd() {
		return __claimeventEnd;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}