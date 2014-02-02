/**
 *
 * mmlScdepartmentName.java
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
public class mmlScdepartmentName extends MMLObject {
	
	/* fields */
	private String __mmlScdepartmentCode = null;
	private String __mmlSctableId = null;

	
	public mmlScdepartmentName() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlScdepartmentCode != null ) pw.print(" " + "mmlSc:departmentCode" +  "=" + "'" + __mmlScdepartmentCode + "'");
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
		if (qName.equals("mmlSc:departmentName") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlScdepartmentName obj = new mmlScdepartmentName();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlScdepartmentName)builder.getElement()).setNamespace( getNamespace() );
			((mmlScdepartmentName)builder.getElement()).setLocalName( getLocalName() );
			((mmlScdepartmentName)builder.getElement()).setQName( getQName() );
			((mmlScdepartmentName)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlScdepartmentCode( atts.getValue(namespaceURI, "departmentCode") );
				((mmlScdepartmentName)builder.getElement()).setMmlScdepartmentCode( atts.getValue(namespaceURI, "departmentCode") );
				setMmlSctableId( atts.getValue(namespaceURI, "tableId") );
				((mmlScdepartmentName)builder.getElement()).setMmlSctableId( atts.getValue(namespaceURI, "tableId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSc:departmentName") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSc:department")) {
				Vector v = ((mmlScdepartment)builder.getParent()).getDepartmentName();
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
	public void setMmlScdepartmentCode(String __mmlScdepartmentCode) {
		this.__mmlScdepartmentCode = __mmlScdepartmentCode;
	}
	public String getMmlScdepartmentCode() {
		return __mmlScdepartmentCode;
	}
	public void setMmlSctableId(String __mmlSctableId) {
		this.__mmlSctableId = __mmlSctableId;
	}
	public String getMmlSctableId() {
		return __mmlSctableId;
	}

	
}