/**
 *
 * mmlCicreatorLicense.java
 * Created on 2002/7/30 10:0:24
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
public class mmlCicreatorLicense extends MMLObject {
	
	/* fields */
	private String __mmlCitableId = null;

	private String text = null;
	
	public mmlCicreatorLicense() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlCitableId != null ) pw.print(" " + "mmlCi:tableId" +  "=" + "'" + __mmlCitableId + "'");

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
		if (qName.equals("mmlCi:creatorLicense") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlCicreatorLicense obj = new mmlCicreatorLicense();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlCicreatorLicense)builder.getElement()).setNamespace( getNamespace() );
			((mmlCicreatorLicense)builder.getElement()).setLocalName( getLocalName() );
			((mmlCicreatorLicense)builder.getElement()).setQName( getQName() );
			((mmlCicreatorLicense)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlCitableId( atts.getValue(namespaceURI, "tableId") );
				((mmlCicreatorLicense)builder.getElement()).setMmlCitableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlCi:creatorLicense") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:staffInfo")) {
				Vector v = ((mmlSmstaffInfo)builder.getParent()).getCreatorLicense();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlCi:CreatorInfo")) {
				Vector v = ((mmlCiCreatorInfo)builder.getParent()).getCreatorLicense();
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
		if (builder.getCurrentElement().getQName().equals("mmlCi:creatorLicense")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlCicreatorLicense)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void setMmlCitableId(String __mmlCitableId) {
		this.__mmlCitableId = __mmlCitableId;
	}
	public String getMmlCitableId() {
		return __mmlCitableId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}