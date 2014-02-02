/**
 *
 * claimAevent.java
 * Created on 2003/1/4 2:30:28
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
public class claimAevent extends MMLObject {
	
	/* fields */
	private String __claimAeventStart = null;
	private String __claimAeventEnd = null;

	private String text = null;
	
	public claimAevent() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAeventStart != null ) pw.print(" " + "claimA:eventStart" +  "=" + "'" + __claimAeventStart + "'");
			if ( __claimAeventEnd != null ) pw.print(" " + "claimA:eventEnd" +  "=" + "'" + __claimAeventEnd + "'");

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
		if (qName.equals("claimA:event") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAevent obj = new claimAevent();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAevent)builder.getElement()).setNamespace( getNamespace() );
			((claimAevent)builder.getElement()).setLocalName( getLocalName() );
			((claimAevent)builder.getElement()).setQName( getQName() );
			((claimAevent)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claimA:eventStart") ) {
						set__claimAeventStart( atts.getValue(i) );
						((claimAevent)builder.getElement()).set__claimAeventStart( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claimA:eventEnd") ) {
						set__claimAeventEnd( atts.getValue(i) );
						((claimAevent)builder.getElement()).set__claimAeventEnd( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:event") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:item")) {
				((claimAitem)builder.getParent()).set_event((claimAevent)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("claimA:event")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimAevent)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__claimAeventStart(String __claimAeventStart) {
		this.__claimAeventStart = __claimAeventStart;
	}
	public String get__claimAeventStart() {
		return __claimAeventStart;
	}
	public void set__claimAeventEnd(String __claimAeventEnd) {
		this.__claimAeventEnd = __claimAeventEnd;
	}
	public String get__claimAeventEnd() {
		return __claimAeventEnd;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}