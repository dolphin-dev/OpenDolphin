/**
 *
 * mmlRppDepartment.java
 * Created on 2003/1/4 2:30:20
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
public class mmlRppDepartment extends MMLObject {
	
	/* fields */
	private String __mmlRpdepCode = null;
	private String __mmlRpdepCodeId = null;

	private String text = null;
	
	public mmlRppDepartment() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpdepCode != null ) pw.print(" " + "mmlRp:depCode" +  "=" + "'" + __mmlRpdepCode + "'");
			if ( __mmlRpdepCodeId != null ) pw.print(" " + "mmlRp:depCodeId" +  "=" + "'" + __mmlRpdepCodeId + "'");

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
		if (qName.equals("mmlRp:pDepartment") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRppDepartment obj = new mmlRppDepartment();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRppDepartment)builder.getElement()).setNamespace( getNamespace() );
			((mmlRppDepartment)builder.getElement()).setLocalName( getLocalName() );
			((mmlRppDepartment)builder.getElement()).setQName( getQName() );
			((mmlRppDepartment)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRp:depCode") ) {
						set__mmlRpdepCode( atts.getValue(i) );
						((mmlRppDepartment)builder.getElement()).set__mmlRpdepCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:depCodeId") ) {
						set__mmlRpdepCodeId( atts.getValue(i) );
						((mmlRppDepartment)builder.getElement()).set__mmlRpdepCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:pDepartment") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:perform")) {
				((mmlRpperform)builder.getParent()).set_pDepartment((mmlRppDepartment)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:pDepartment")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRppDepartment)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlRpdepCode(String __mmlRpdepCode) {
		this.__mmlRpdepCode = __mmlRpdepCode;
	}
	public String get__mmlRpdepCode() {
		return __mmlRpdepCode;
	}
	public void set__mmlRpdepCodeId(String __mmlRpdepCodeId) {
		this.__mmlRpdepCodeId = __mmlRpdepCodeId;
	}
	public String get__mmlRpdepCodeId() {
		return __mmlRpdepCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}