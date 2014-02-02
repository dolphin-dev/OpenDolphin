/**
 *
 * mmlRddiagnosis.java
 * Created on 2002/7/30 10:0:25
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
public class mmlRddiagnosis extends MMLObject {
	
	/* fields */
	private String __mmlRdcode = null;
	private String __mmlRdsystem = null;

	private String text = null;
	
	public mmlRddiagnosis() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRdcode != null ) pw.print(" " + "mmlRd:code" +  "=" + "'" + __mmlRdcode + "'");
			if ( __mmlRdsystem != null ) pw.print(" " + "mmlRd:system" +  "=" + "'" + __mmlRdsystem + "'");

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
		if (qName.equals("mmlRd:diagnosis") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRddiagnosis obj = new mmlRddiagnosis();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRddiagnosis)builder.getElement()).setNamespace( getNamespace() );
			((mmlRddiagnosis)builder.getElement()).setLocalName( getLocalName() );
			((mmlRddiagnosis)builder.getElement()).setQName( getQName() );
			((mmlRddiagnosis)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRdcode( atts.getValue(namespaceURI, "code") );
				((mmlRddiagnosis)builder.getElement()).setMmlRdcode( atts.getValue(namespaceURI, "code") );
				setMmlRdsystem( atts.getValue(namespaceURI, "system") );
				((mmlRddiagnosis)builder.getElement()).setMmlRdsystem( atts.getValue(namespaceURI, "system") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRd:diagnosis") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRd:RegisteredDiagnosisModule")) {
				((mmlRdRegisteredDiagnosisModule)builder.getParent()).setDiagnosis((mmlRddiagnosis)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRd:diagnosis")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRddiagnosis)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlRdcode(String __mmlRdcode) {
		this.__mmlRdcode = __mmlRdcode;
	}
	public String getMmlRdcode() {
		return __mmlRdcode;
	}
	public void setMmlRdsystem(String __mmlRdsystem) {
		this.__mmlRdsystem = __mmlRdsystem;
	}
	public String getMmlRdsystem() {
		return __mmlRdsystem;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}