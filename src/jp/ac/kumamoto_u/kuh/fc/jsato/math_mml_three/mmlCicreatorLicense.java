/**
 *
 * mmlCicreatorLicense.java
 * Created on 2003/1/4 2:29:55
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlCi:tableId") ) {
						set__mmlCitableId( atts.getValue(i) );
						((mmlCicreatorLicense)builder.getElement()).set__mmlCitableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlCi:creatorLicense") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:staffInfo")) {
				Vector v = ((mmlSmstaffInfo)builder.getParent()).get_creatorLicense();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlCi:CreatorInfo")) {
				Vector v = ((mmlCiCreatorInfo)builder.getParent()).get_creatorLicense();
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
	public void set__mmlCitableId(String __mmlCitableId) {
		this.__mmlCitableId = __mmlCitableId;
	}
	public String get__mmlCitableId() {
		return __mmlCitableId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}