/**
 *
 * claimbundle.java
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("claim:classCode") ) {
						set__claimclassCode( atts.getValue(i) );
						((claimbundle)builder.getElement()).set__claimclassCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("claim:classCodeId") ) {
						set__claimclassCodeId( atts.getValue(i) );
						((claimbundle)builder.getElement()).set__claimclassCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claim:bundle") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claim:ClaimModule")) {
				Vector v = ((claimClaimModule)builder.getParent()).get_bundle();
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
	public void set__claimclassCode(String __claimclassCode) {
		this.__claimclassCode = __claimclassCode;
	}
	public String get__claimclassCode() {
		return __claimclassCode;
	}
	public void set__claimclassCodeId(String __claimclassCodeId) {
		this.__claimclassCodeId = __claimclassCodeId;
	}
	public String get__claimclassCodeId() {
		return __claimclassCodeId;
	}

	public void set_className(claimclassName _className) {
		this._className = _className;
	}
	public claimclassName get_className() {
		return _className;
	}
	public void set_administration(claimadministration _administration) {
		this._administration = _administration;
	}
	public claimadministration get_administration() {
		return _administration;
	}
	public void set_admMemo(claimadmMemo _admMemo) {
		this._admMemo = _admMemo;
	}
	public claimadmMemo get_admMemo() {
		return _admMemo;
	}
	public void set_bundleNumber(claimbundleNumber _bundleNumber) {
		this._bundleNumber = _bundleNumber;
	}
	public claimbundleNumber get_bundleNumber() {
		return _bundleNumber;
	}
	public void set_item(Vector _item) {
		if (this._item != null) this._item.removeAllElements();
		// copy entire elements in the vector
		this._item = new Vector();
		for (int i = 0; i < _item.size(); ++i) {
			this._item.addElement( _item.elementAt(i) );
		}
	}
	public Vector get_item() {
		return _item;
	}
	public void set_memo(claimmemo _memo) {
		this._memo = _memo;
	}
	public claimmemo get_memo() {
		return _memo;
	}
	
}