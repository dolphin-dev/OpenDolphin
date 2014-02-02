/**
 *
 * mmlScfacilityName.java
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
public class mmlScfacilityName extends MMLObject {
	
	/* fields */
	private String __mmlScfacilityCode = null;
	private String __mmlSctableId = null;
	private String __mmlScfacilityId = null;
	private String __mmlScfacilityIdType = null;

	private String text = null;
	
	public mmlScfacilityName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlScfacilityCode != null ) pw.print(" " + "mmlSc:facilityCode" +  "=" + "'" + __mmlScfacilityCode + "'");
			if ( __mmlSctableId != null ) pw.print(" " + "mmlSc:tableId" +  "=" + "'" + __mmlSctableId + "'");
			if ( __mmlScfacilityId != null ) pw.print(" " + "mmlSc:facilityId" +  "=" + "'" + __mmlScfacilityId + "'");
			if ( __mmlScfacilityIdType != null ) pw.print(" " + "mmlSc:facilityIdType" +  "=" + "'" + __mmlScfacilityIdType + "'");

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
		if (qName.equals("mmlSc:facilityName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlScfacilityName obj = new mmlScfacilityName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlScfacilityName)builder.getElement()).setNamespace( getNamespace() );
			((mmlScfacilityName)builder.getElement()).setLocalName( getLocalName() );
			((mmlScfacilityName)builder.getElement()).setQName( getQName() );
			((mmlScfacilityName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSc:facilityCode") ) {
						set__mmlScfacilityCode( atts.getValue(i) );
						((mmlScfacilityName)builder.getElement()).set__mmlScfacilityCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSc:tableId") ) {
						set__mmlSctableId( atts.getValue(i) );
						((mmlScfacilityName)builder.getElement()).set__mmlSctableId( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSc:facilityId") ) {
						set__mmlScfacilityId( atts.getValue(i) );
						((mmlScfacilityName)builder.getElement()).set__mmlScfacilityId( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSc:facilityIdType") ) {
						set__mmlScfacilityIdType( atts.getValue(i) );
						((mmlScfacilityName)builder.getElement()).set__mmlScfacilityIdType( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:facilityName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSc:facility")) {
				Vector v = ((mmlScfacility)builder.getParent()).get_facilityName();
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
		if (builder.getCurrentElement().getQName().equals("mmlSc:facilityName")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlScfacilityName)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlScfacilityCode(String __mmlScfacilityCode) {
		this.__mmlScfacilityCode = __mmlScfacilityCode;
	}
	public String get__mmlScfacilityCode() {
		return __mmlScfacilityCode;
	}
	public void set__mmlSctableId(String __mmlSctableId) {
		this.__mmlSctableId = __mmlSctableId;
	}
	public String get__mmlSctableId() {
		return __mmlSctableId;
	}
	public void set__mmlScfacilityId(String __mmlScfacilityId) {
		this.__mmlScfacilityId = __mmlScfacilityId;
	}
	public String get__mmlScfacilityId() {
		return __mmlScfacilityId;
	}
	public void set__mmlScfacilityIdType(String __mmlScfacilityIdType) {
		this.__mmlScfacilityIdType = __mmlScfacilityIdType;
	}
	public String get__mmlScfacilityIdType() {
		return __mmlScfacilityIdType;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}