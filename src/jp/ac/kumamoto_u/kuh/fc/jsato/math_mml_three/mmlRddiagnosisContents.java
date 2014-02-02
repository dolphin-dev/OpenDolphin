/**
 *
 * mmlRddiagnosisContents.java
 * Created on 2003/1/4 2:30:2
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
public class mmlRddiagnosisContents extends MMLObject {
	
	/* fields */
	private Vector _dxItem = new Vector();
	
	public mmlRddiagnosisContents() {
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
			if (this._dxItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._dxItem.size(); ++i ) {
					((mmlRddxItem)this._dxItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlRd:diagnosisContents") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRddiagnosisContents obj = new mmlRddiagnosisContents();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRddiagnosisContents)builder.getElement()).setNamespace( getNamespace() );
			((mmlRddiagnosisContents)builder.getElement()).setLocalName( getLocalName() );
			((mmlRddiagnosisContents)builder.getElement()).setQName( getQName() );
			((mmlRddiagnosisContents)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRd:diagnosisContents") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRd:RegisteredDiagnosisModule")) {
				((mmlRdRegisteredDiagnosisModule)builder.getParent()).set_diagnosisContents((mmlRddiagnosisContents)builder.getElement());
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
	public void set_dxItem(Vector _dxItem) {
		if (this._dxItem != null) this._dxItem.removeAllElements();
		// copy entire elements in the vector
		this._dxItem = new Vector();
		for (int i = 0; i < _dxItem.size(); ++i) {
			this._dxItem.addElement( _dxItem.elementAt(i) );
		}
	}
	public Vector get_dxItem() {
		return _dxItem;
	}
	
}