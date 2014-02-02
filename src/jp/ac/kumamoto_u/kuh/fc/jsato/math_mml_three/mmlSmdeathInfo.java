/**
 *
 * mmlSmdeathInfo.java
 * Created on 2003/1/4 2:30:10
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
public class mmlSmdeathInfo extends MMLObject {
	
	/* fields */
	private String __mmlSmdate = null;
	private String __mmlSmautopsy = null;

	private String text = null;
	
	public mmlSmdeathInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSmdate != null ) pw.print(" " + "mmlSm:date" +  "=" + "'" + __mmlSmdate + "'");
			if ( __mmlSmautopsy != null ) pw.print(" " + "mmlSm:autopsy" +  "=" + "'" + __mmlSmautopsy + "'");

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
		if (qName.equals("mmlSm:deathInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmdeathInfo obj = new mmlSmdeathInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmdeathInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmdeathInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmdeathInfo)builder.getElement()).setQName( getQName() );
			((mmlSmdeathInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSm:date") ) {
						set__mmlSmdate( atts.getValue(i) );
						((mmlSmdeathInfo)builder.getElement()).set__mmlSmdate( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSm:autopsy") ) {
						set__mmlSmautopsy( atts.getValue(i) );
						((mmlSmdeathInfo)builder.getElement()).set__mmlSmautopsy( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:deathInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:SummaryModule")) {
				((mmlSmSummaryModule)builder.getParent()).set_deathInfo((mmlSmdeathInfo)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlSm:deathInfo")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlSmdeathInfo)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlSmdate(String __mmlSmdate) {
		this.__mmlSmdate = __mmlSmdate;
	}
	public String get__mmlSmdate() {
		return __mmlSmdate;
	}
	public void set__mmlSmautopsy(String __mmlSmautopsy) {
		this.__mmlSmautopsy = __mmlSmautopsy;
	}
	public String get__mmlSmautopsy() {
		return __mmlSmautopsy;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}