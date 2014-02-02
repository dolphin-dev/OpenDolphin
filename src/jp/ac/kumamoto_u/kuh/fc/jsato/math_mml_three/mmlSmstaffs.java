/**
 *
 * mmlSmstaffs.java
 * Created on 2003/1/4 2:30:9
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
public class mmlSmstaffs extends MMLObject {
	
	/* fields */
	private Vector _staffInfo = new Vector();
	
	public mmlSmstaffs() {
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
			if (this._staffInfo != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._staffInfo.size(); ++i ) {
					((mmlSmstaffInfo)this._staffInfo.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSm:staffs") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmstaffs obj = new mmlSmstaffs();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmstaffs)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmstaffs)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmstaffs)builder.getElement()).setQName( getQName() );
			((mmlSmstaffs)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:staffs") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:outPatientItem")) {
				((mmlSmoutPatientItem)builder.getParent()).set_staffs((mmlSmstaffs)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSm:inPatientItem")) {
				((mmlSminPatientItem)builder.getParent()).set_staffs((mmlSmstaffs)builder.getElement());
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
	public void set_staffInfo(Vector _staffInfo) {
		if (this._staffInfo != null) this._staffInfo.removeAllElements();
		// copy entire elements in the vector
		this._staffInfo = new Vector();
		for (int i = 0; i < _staffInfo.size(); ++i) {
			this._staffInfo.addElement( _staffInfo.elementAt(i) );
		}
	}
	public Vector get_staffInfo() {
		return _staffInfo;
	}
	
}