/**
 *
 * claimbundle.java
 * Created on 2002/7/30 10:0:39
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
public class claimbundle extends MMLObject {
	
	/* fields */
	private String __claimclassCode = null;
	private String __claimclassCodeId = null;

	private claimclassName _className = null;
	private claimadministration _administration = null;
	private claimadmMemo _admMemo = null;
	private claimbundleNumber _bundleNumber = null;
	private Vector _item = new Vector();
	private claimmemo _memo = null;
	
	public claimbundle() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimclassCode != null ) pw.print(" " + "claim:classCode" +  "=" + "'" + __claimclassCode + "'");
			if ( __claimclassCodeId != null ) pw.print(" " + "claim:classCodeId" +  "=" + "'" + __claimclassCodeId + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _className != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_className.printObject(pw, visitor);
			}
			if ( _administration != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_administration.printObject(pw, visitor);
			}
			if ( _admMemo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_admMemo.printObject(pw, visitor);
			}
			if ( _bundleNumber != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_bundleNumber.printObject(pw, visitor);
			}
			if (this._item != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._item.size(); ++i ) {
					((claimitem)this._item.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("claim:bundle") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimbundle obj = new claimbundle();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimbundle)builder.getElement()).setNamespace( getNamespace() );
			((claimbundle)builder.getElement()).setLocalName( getLocalName() );
			((claimbundle)builder.getElement()).setQName( getQName() );
			((claimbundle)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimclassCode( atts.getValue(namespaceURI, "classCode") );
				((claimbundle)builder.getElement()).setClaimclassCode( atts.getValue(namespaceURI, "classCode") );
				setClaimclassCodeId( atts.getValue(namespaceURI, "classCodeId") );
				((claimbundle)builder.getElement()).setClaimclassCodeId( atts.getValue(namespaceURI, "classCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:bundle") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:ClaimModule")) {
				Vector v = ((claimClaimModule)builder.getParent()).getBundle();
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
	public void setClaimclassCode(String __claimclassCode) {
		this.__claimclassCode = __claimclassCode;
	}
	public String getClaimclassCode() {
		return __claimclassCode;
	}
	public void setClaimclassCodeId(String __claimclassCodeId) {
		this.__claimclassCodeId = __claimclassCodeId;
	}
	public String getClaimclassCodeId() {
		return __claimclassCodeId;
	}

	public void setClassName(claimclassName _className) {
		this._className = _className;
	}
	public claimclassName getClassName() {
		return _className;
	}
	public void setAdministration(claimadministration _administration) {
		this._administration = _administration;
	}
	public claimadministration getAdministration() {
		return _administration;
	}
	public void setAdmMemo(claimadmMemo _admMemo) {
		this._admMemo = _admMemo;
	}
	public claimadmMemo getAdmMemo() {
		return _admMemo;
	}
	public void setBundleNumber(claimbundleNumber _bundleNumber) {
		this._bundleNumber = _bundleNumber;
	}
	public claimbundleNumber getBundleNumber() {
		return _bundleNumber;
	}
	public void setItem(Vector _item) {
		if (this._item != null) this._item.removeAllElements();
		// copy entire elements in the vector
		this._item = new Vector();
		for (int i = 0; i < _item.size(); ++i) {
			this._item.addElement( _item.elementAt(i) );
		}
	}
	public Vector getItem() {
		return _item;
	}
	public void setMemo(claimmemo _memo) {
		this._memo = _memo;
	}
	public claimmemo getMemo() {
		return _memo;
	}
	
}