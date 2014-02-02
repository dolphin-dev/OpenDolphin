/**
 *
 * MmlModuleItem.java
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
public class MmlModuleItem extends MMLObject {
	
	/* fields */
	private String __type = null;

	private docInfo _docInfo = null;
	private content _content = null;
	
	public MmlModuleItem() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __type != null ) pw.print(" " + "type" +  "=" + "'" + __type + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _docInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_docInfo.printObject(pw, visitor);
			}
			if ( _content != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_content.printObject(pw, visitor);
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
		if (qName.equals("MmlModuleItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			MmlModuleItem obj = new MmlModuleItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((MmlModuleItem)builder.getElement()).setNamespace( getNamespace() );
			((MmlModuleItem)builder.getElement()).setLocalName( getLocalName() );
			((MmlModuleItem)builder.getElement()).setQName( getQName() );
			((MmlModuleItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setType( atts.getValue(namespaceURI, "type") );
				((MmlModuleItem)builder.getElement()).setType( atts.getValue(namespaceURI, "type") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("MmlModuleItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("MmlBody")) {
				Vector v = ((MmlBody)builder.getParent()).getMmlModuleItem();
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
	public void setType(String __type) {
		this.__type = __type;
	}
	public String getType() {
		return __type;
	}

	public void setDocInfo(docInfo _docInfo) {
		this._docInfo = _docInfo;
	}
	public docInfo getDocInfo() {
		return _docInfo;
	}
	public void setContent(content _content) {
		this._content = _content;
	}
	public content getContent() {
		return _content;
	}
	
}