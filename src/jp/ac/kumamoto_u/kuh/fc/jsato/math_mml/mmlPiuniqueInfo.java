/**
 *
 * mmlPiuniqueInfo.java
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
public class mmlPiuniqueInfo extends MMLObject {
	
	/* fields */
	private mmlPimasterId _masterId = null;
	private Vector _otherId = new Vector();
	
	public mmlPiuniqueInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _masterId != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_masterId.printObject(pw, visitor);
			}
			if (this._otherId != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._otherId.size(); ++i ) {
					((mmlPiotherId)this._otherId.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlPi:uniqueInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPiuniqueInfo obj = new mmlPiuniqueInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPiuniqueInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlPiuniqueInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlPiuniqueInfo)builder.getElement()).setQName( getQName() );
			((mmlPiuniqueInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPi:uniqueInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPi:PatientModule")) {
				((mmlPiPatientModule)builder.getParent()).setUniqueInfo((mmlPiuniqueInfo)builder.getElement());
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
	public void setMasterId(mmlPimasterId _masterId) {
		this._masterId = _masterId;
	}
	public mmlPimasterId getMasterId() {
		return _masterId;
	}
	public void setOtherId(Vector _otherId) {
		if (this._otherId != null) this._otherId.removeAllElements();
		// copy entire elements in the vector
		this._otherId = new Vector();
		for (int i = 0; i < _otherId.size(); ++i) {
			this._otherId.addElement( _otherId.elementAt(i) );
		}
	}
	public Vector getOtherId() {
		return _otherId;
	}
	
}