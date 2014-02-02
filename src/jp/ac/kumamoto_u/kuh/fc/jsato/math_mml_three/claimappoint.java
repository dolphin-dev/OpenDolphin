/**
 *
 * claimappoint.java
 * Created on 2003/1/4 2:30:25
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
public class claimappoint extends MMLObject {
	
	/* fields */
	private Vector _appName = new Vector();
	private claimmemo _memo = null;
	
	public claimappoint() {
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
			if (this._appName != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._appName.size(); ++i ) {
					((claimappName)this._appName.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _memo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_memo.printObject(pw, visitor);
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
		if (qName.equals("claim:appoint") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimappoint obj = new claimappoint();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimappoint)builder.getElement()).setNamespace( getNamespace() );
			((claimappoint)builder.getElement()).setLocalName( getLocalName() );
			((claimappoint)builder.getElement()).setQName( getQName() );
			((claimappoint)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:appoint") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:information")) {
				((claiminformation)builder.getParent()).set_appoint((claimappoint)builder.getElement());
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
	public void set_appName(Vector _appName) {
		if (this._appName != null) this._appName.removeAllElements();
		// copy entire elements in the vector
		this._appName = new Vector();
		for (int i = 0; i < _appName.size(); ++i) {
			this._appName.addElement( _appName.elementAt(i) );
		}
	}
	public Vector get_appName() {
		return _appName;
	}
	public void set_memo(claimmemo _memo) {
		this._memo = _memo;
	}
	public claimmemo get_memo() {
		return _memo;
	}
	
}