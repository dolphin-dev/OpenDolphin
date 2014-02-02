/**
 *
 * mmlRpconFacility.java
 * Created on 2003/1/4 2:30:18
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
public class mmlRpconFacility extends MMLObject {
	
	/* fields */
	private String __mmlRpfacilityCode = null;
	private String __mmlRpfacilityCodeId = null;

	private String text = null;
	
	public mmlRpconFacility() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpfacilityCode != null ) pw.print(" " + "mmlRp:facilityCode" +  "=" + "'" + __mmlRpfacilityCode + "'");
			if ( __mmlRpfacilityCodeId != null ) pw.print(" " + "mmlRp:facilityCodeId" +  "=" + "'" + __mmlRpfacilityCodeId + "'");

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
		if (qName.equals("mmlRp:conFacility") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpconFacility obj = new mmlRpconFacility();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpconFacility)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpconFacility)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpconFacility)builder.getElement()).setQName( getQName() );
			((mmlRpconFacility)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRp:facilityCode") ) {
						set__mmlRpfacilityCode( atts.getValue(i) );
						((mmlRpconFacility)builder.getElement()).set__mmlRpfacilityCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:facilityCodeId") ) {
						set__mmlRpfacilityCodeId( atts.getValue(i) );
						((mmlRpconFacility)builder.getElement()).set__mmlRpfacilityCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:conFacility") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:consultFrom")) {
				((mmlRpconsultFrom)builder.getParent()).set_conFacility((mmlRpconFacility)builder.getElement());
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
		if (builder.getCurrentElement().getQName().equals("mmlRp:conFacility")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRpconFacility)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlRpfacilityCode(String __mmlRpfacilityCode) {
		this.__mmlRpfacilityCode = __mmlRpfacilityCode;
	}
	public String get__mmlRpfacilityCode() {
		return __mmlRpfacilityCode;
	}
	public void set__mmlRpfacilityCodeId(String __mmlRpfacilityCodeId) {
		this.__mmlRpfacilityCodeId = __mmlRpfacilityCodeId;
	}
	public String get__mmlRpfacilityCodeId() {
		return __mmlRpfacilityCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}