/**
 *
 * mmlSgtitle.java
 * Created on 2003/1/4 2:30:8
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
public class mmlSgtitle extends MMLObject {
	
	/* fields */
	private String __mmlSgcode = null;
	private String __mmlSgsystem = null;

	private String text = null;
	
	public mmlSgtitle() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSgcode != null ) pw.print(" " + "mmlSg:code" +  "=" + "'" + __mmlSgcode + "'");
			if ( __mmlSgsystem != null ) pw.print(" " + "mmlSg:system" +  "=" + "'" + __mmlSgsystem + "'");

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
		if (qName.equals("mmlSg:title") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgtitle obj = new mmlSgtitle();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgtitle)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgtitle)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgtitle)builder.getElement()).setQName( getQName() );
			((mmlSgtitle)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSg:code") ) {
						set__mmlSgcode( atts.getValue(i) );
						((mmlSgtitle)builder.getElement()).set__mmlSgcode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSg:system") ) {
						set__mmlSgsystem( atts.getValue(i) );
						((mmlSgtitle)builder.getElement()).set__mmlSgsystem( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:title") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:operationElementItem")) {
				((mmlSgoperationElementItem)builder.getParent()).set_title((mmlSgtitle)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:anesthesiaProcedure")) {
				Vector v = ((mmlSganesthesiaProcedure)builder.getParent()).get_title();
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
		if (builder.getCurrentElement().getQName().equals("mmlSg:title")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlSgtitle)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlSgcode(String __mmlSgcode) {
		this.__mmlSgcode = __mmlSgcode;
	}
	public String get__mmlSgcode() {
		return __mmlSgcode;
	}
	public void set__mmlSgsystem(String __mmlSgsystem) {
		this.__mmlSgsystem = __mmlSgsystem;
	}
	public String get__mmlSgsystem() {
		return __mmlSgsystem;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}