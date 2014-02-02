/**
 *
 * mmlSclicenseName.java
 * Created on 2003/1/4 2:29:56
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
public class mmlSclicenseName extends MMLObject {
	
	/* fields */
	private String __mmlSclicenseCode = null;
	private String __mmlSctableId = null;

	
	public mmlSclicenseName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSclicenseCode != null ) pw.print(" " + "mmlSc:licenseCode" +  "=" + "'" + __mmlSclicenseCode + "'");
			if ( __mmlSctableId != null ) pw.print(" " + "mmlSc:tableId" +  "=" + "'" + __mmlSctableId + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */

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
		if (qName.equals("mmlSc:licenseName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSclicenseName obj = new mmlSclicenseName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSclicenseName)builder.getElement()).setNamespace( getNamespace() );
			((mmlSclicenseName)builder.getElement()).setLocalName( getLocalName() );
			((mmlSclicenseName)builder.getElement()).setQName( getQName() );
			((mmlSclicenseName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSc:licenseCode") ) {
						set__mmlSclicenseCode( atts.getValue(i) );
						((mmlSclicenseName)builder.getElement()).set__mmlSclicenseCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSc:tableId") ) {
						set__mmlSctableId( atts.getValue(i) );
						((mmlSclicenseName)builder.getElement()).set__mmlSctableId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:licenseName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSc:license")) {
				Vector v = ((mmlSclicense)builder.getParent()).get_licenseName();
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
	
	
	/* setters and getters */
	public void set__mmlSclicenseCode(String __mmlSclicenseCode) {
		this.__mmlSclicenseCode = __mmlSclicenseCode;
	}
	public String get__mmlSclicenseCode() {
		return __mmlSclicenseCode;
	}
	public void set__mmlSctableId(String __mmlSctableId) {
		this.__mmlSctableId = __mmlSctableId;
	}
	public String get__mmlSctableId() {
		return __mmlSctableId;
	}

	
}