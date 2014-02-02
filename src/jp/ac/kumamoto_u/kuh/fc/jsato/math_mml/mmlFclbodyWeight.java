/**
 *
 * mmlFclbodyWeight.java
 * Created on 2002/7/30 10:0:27
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
public class mmlFclbodyWeight extends MMLObject {
	
	/* fields */
	private String __mmlFclunit = null;

	private String text = null;
	
	public mmlFclbodyWeight() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlFclunit != null ) pw.print(" " + "mmlFcl:unit" +  "=" + "'" + __mmlFclunit + "'");

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
		if (qName.equals("mmlFcl:bodyWeight") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclbodyWeight obj = new mmlFclbodyWeight();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclbodyWeight)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclbodyWeight)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclbodyWeight)builder.getElement()).setQName( getQName() );
			((mmlFclbodyWeight)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlFclunit( atts.getValue(namespaceURI, "unit") );
				((mmlFclbodyWeight)builder.getElement()).setMmlFclunit( atts.getValue(namespaceURI, "unit") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:bodyWeight") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:birthInfo")) {
				((mmlFclbirthInfo)builder.getParent()).setBodyWeight((mmlFclbodyWeight)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlFcl:bodyWeight")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlFclbodyWeight)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlFclunit(String __mmlFclunit) {
		this.__mmlFclunit = __mmlFclunit;
	}
	public String getMmlFclunit() {
		return __mmlFclunit;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}