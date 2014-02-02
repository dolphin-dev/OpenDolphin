/**
 *
 * mmlFcname.java
 * Created on 2003/1/4 2:29:54
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
public class mmlFcname extends MMLObject {
	
	/* fields */
	private String __mmlFcrepCode = null;
	private String __mmlFctableId = null;

	private String text = null;
	
	public mmlFcname() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlFcrepCode != null ) pw.print(" " + "mmlFc:repCode" +  "=" + "'" + __mmlFcrepCode + "'");
			if ( __mmlFctableId != null ) pw.print(" " + "mmlFc:tableId" +  "=" + "'" + __mmlFctableId + "'");

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
		if (qName.equals("mmlFc:name") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFcname obj = new mmlFcname();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFcname)builder.getElement()).setNamespace( getNamespace() );
			((mmlFcname)builder.getElement()).setLocalName( getLocalName() );
			((mmlFcname)builder.getElement()).setQName( getQName() );
			((mmlFcname)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlFc:repCode") ) {
						set__mmlFcrepCode( atts.getValue(i) );
						((mmlFcname)builder.getElement()).set__mmlFcrepCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlFc:tableId") ) {
						set__mmlFctableId( atts.getValue(i) );
						((mmlFcname)builder.getElement()).set__mmlFctableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFc:name") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFc:Facility")) {
				Vector v = ((mmlFcFacility)builder.getParent()).get_name();
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
		if (builder.getCurrentElement().getQName().equals("mmlFc:name")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlFcname)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlFcrepCode(String __mmlFcrepCode) {
		this.__mmlFcrepCode = __mmlFcrepCode;
	}
	public String get__mmlFcrepCode() {
		return __mmlFcrepCode;
	}
	public void set__mmlFctableId(String __mmlFctableId) {
		this.__mmlFctableId = __mmlFctableId;
	}
	public String get__mmlFctableId() {
		return __mmlFctableId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}