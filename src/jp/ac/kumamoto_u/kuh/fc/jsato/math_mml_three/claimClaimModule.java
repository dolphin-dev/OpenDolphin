/**
 *
 * claimClaimModule.java
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
public class claimClaimModule extends MMLObject {
	
	/* fields */
	private claiminformation _information = null;
	private Vector _bundle = new Vector();
	
	public claimClaimModule() {
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
			if ( _information != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_information.printObject(pw, visitor);
			}
			if (this._bundle != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._bundle.size(); ++i ) {
					((claimbundle)this._bundle.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("claim:ClaimModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimClaimModule obj = new claimClaimModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimClaimModule)builder.getElement()).setNamespace( getNamespace() );
			((claimClaimModule)builder.getElement()).setLocalName( getLocalName() );
			((claimClaimModule)builder.getElement()).setQName( getQName() );
			((claimClaimModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:ClaimModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (claimClaimModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_ClaimModule((claimClaimModule)builder.getElement());
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
	public void set_information(claiminformation _information) {
		this._information = _information;
	}
	public claiminformation get_information() {
		return _information;
	}
	public void set_bundle(Vector _bundle) {
		if (this._bundle != null) this._bundle.removeAllElements();
		// copy entire elements in the vector
		this._bundle = new Vector();
		for (int i = 0; i < _bundle.size(); ++i) {
			this._bundle.addElement( _bundle.elementAt(i) );
		}
	}
	public Vector get_bundle() {
		return _bundle;
	}
	
}