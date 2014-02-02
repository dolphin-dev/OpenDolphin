/**
 *
 * claimappName.java
 * Created on 2003/1/4 2:30:25
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
public class claimappName extends MMLObject {
	
	/* fields */
	private String __claimappCode = null;
	private String __claimappCodeId = null;

	private String text = null;
	
	public claimappName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimappCode != null ) pw.print(" " + "claim:appCode" +  "=" + "'" + __claimappCode + "'");
			if ( __claimappCodeId != null ) pw.print(" " + "claim:appCodeId" +  "=" + "'" + __claimappCodeId + "'");

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
		if (qName.equals("claim:appName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimappName obj = new claimappName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimappName)builder.getElement()).setNamespace( getNamespace() );
			((claimappName)builder.getElement()).setLocalName( getLocalName() );
			((claimappName)builder.getElement()).setQName( getQName() );
			((claimappName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claim:appCode") ) {
						set__claimappCode( atts.getValue(i) );
						((claimappName)builder.getElement()).set__claimappCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:appCodeId") ) {
						set__claimappCodeId( atts.getValue(i) );
						((claimappName)builder.getElement()).set__claimappCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:appName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:appoint")) {
				Vector v = ((claimappoint)builder.getParent()).get_appName();
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
		if (builder.getCurrentElement().getQName().equals("claim:appName")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((claimappName)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__claimappCode(String __claimappCode) {
		this.__claimappCode = __claimappCode;
	}
	public String get__claimappCode() {
		return __claimappCode;
	}
	public void set__claimappCodeId(String __claimappCodeId) {
		this.__claimappCodeId = __claimappCodeId;
	}
	public String get__claimappCodeId() {
		return __claimappCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}