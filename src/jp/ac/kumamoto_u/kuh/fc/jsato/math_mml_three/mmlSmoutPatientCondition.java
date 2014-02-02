/**
 *
 * mmlSmoutPatientCondition.java
 * Created on 2003/1/4 2:30:9
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
public class mmlSmoutPatientCondition extends MMLObject {
	
	/* fields */
	private String __mmlSmfirst = null;
	private String __mmlSmemergency = null;

	private String text = null;
	
	public mmlSmoutPatientCondition() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSmfirst != null ) pw.print(" " + "mmlSm:first" +  "=" + "'" + __mmlSmfirst + "'");
			if ( __mmlSmemergency != null ) pw.print(" " + "mmlSm:emergency" +  "=" + "'" + __mmlSmemergency + "'");

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
		if (qName.equals("mmlSm:outPatientCondition") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmoutPatientCondition obj = new mmlSmoutPatientCondition();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmoutPatientCondition)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmoutPatientCondition)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmoutPatientCondition)builder.getElement()).setQName( getQName() );
			((mmlSmoutPatientCondition)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSm:first") ) {
						set__mmlSmfirst( atts.getValue(i) );
						((mmlSmoutPatientCondition)builder.getElement()).set__mmlSmfirst( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSm:emergency") ) {
						set__mmlSmemergency( atts.getValue(i) );
						((mmlSmoutPatientCondition)builder.getElement()).set__mmlSmemergency( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:outPatientCondition") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:outPatientItem")) {
				((mmlSmoutPatientItem)builder.getParent()).set_outPatientCondition((mmlSmoutPatientCondition)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlSm:outPatientCondition")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlSmoutPatientCondition)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlSmfirst(String __mmlSmfirst) {
		this.__mmlSmfirst = __mmlSmfirst;
	}
	public String get__mmlSmfirst() {
		return __mmlSmfirst;
	}
	public void set__mmlSmemergency(String __mmlSmemergency) {
		this.__mmlSmemergency = __mmlSmemergency;
	}
	public String get__mmlSmemergency() {
		return __mmlSmemergency;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}