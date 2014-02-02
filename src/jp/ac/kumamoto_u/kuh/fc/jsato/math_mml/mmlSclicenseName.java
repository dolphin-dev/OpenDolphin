/**
 *
 * mmlSclicenseName.java
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

			if ( this.getLocalName().equals("Mml") ) {
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
				setMmlSclicenseCode( atts.getValue(namespaceURI, "licenseCode") );
				((mmlSclicenseName)builder.getElement()).setMmlSclicenseCode( atts.getValue(namespaceURI, "licenseCode") );
				setMmlSctableId( atts.getValue(namespaceURI, "tableId") );
				((mmlSclicenseName)builder.getElement()).setMmlSctableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:licenseName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSc:license")) {
				Vector v = ((mmlSclicense)builder.getParent()).getLicenseName();
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
	public void setMmlSclicenseCode(String __mmlSclicenseCode) {
		this.__mmlSclicenseCode = __mmlSclicenseCode;
	}
	public String getMmlSclicenseCode() {
		return __mmlSclicenseCode;
	}
	public void setMmlSctableId(String __mmlSctableId) {
		this.__mmlSctableId = __mmlSctableId;
	}
	public String getMmlSctableId() {
		return __mmlSctableId;
	}

	
}