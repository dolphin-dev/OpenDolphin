/**
 *
 * mmldocId.java
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
public class mmldocId extends MMLObject {
	
	/* fields */
	private mmluid _uid = null;
	private Vector _parentId = new Vector();
	private Vector _groupId = new Vector();
	
	public mmldocId() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _uid != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_uid.printObject(pw, visitor);
			}
			if (this._parentId != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._parentId.size(); ++i ) {
					((mmlparentId)this._parentId.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._groupId != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._groupId.size(); ++i ) {
					((mmlgroupId)this._groupId.elementAt(i)).printObject(pw, visitor);
				}
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
		if (qName.equals("mml:docId") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmldocId obj = new mmldocId();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmldocId)builder.getElement()).setNamespace( getNamespace() );
			((mmldocId)builder.getElement()).setLocalName( getLocalName() );
			((mmldocId)builder.getElement()).setQName( getQName() );
			((mmldocId)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:docId") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:docInfo")) {
				((mmldocInfo)builder.getParent()).set_docId((mmldocId)builder.getElement());
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
	public void set_uid(mmluid _uid) {
		this._uid = _uid;
	}
	public mmluid get_uid() {
		return _uid;
	}
	public void set_parentId(Vector _parentId) {
		if (this._parentId != null) this._parentId.removeAllElements();
		// copy entire elements in the vector
		this._parentId = new Vector();
		for (int i = 0; i < _parentId.size(); ++i) {
			this._parentId.addElement( _parentId.elementAt(i) );
		}
	}
	public Vector get_parentId() {
		return _parentId;
	}
	public void set_groupId(Vector _groupId) {
		if (this._groupId != null) this._groupId.removeAllElements();
		// copy entire elements in the vector
		this._groupId = new Vector();
		for (int i = 0; i < _groupId.size(); ++i) {
			this._groupId.addElement( _groupId.elementAt(i) );
		}
	}
	public Vector get_groupId() {
		return _groupId;
	}
	
}