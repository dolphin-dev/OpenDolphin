/**
 *
 * claimAbundle.java
 * Created on 2002/7/30 10:0:40
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import java.util.*;
import org.xml.sax.*;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */
public class claimAbundle extends MMLObject {
	
	/* fields */
	private String __claimAclassCode = null;
	private String __claimAclassCodeId = null;

	private claimAclassName _className = null;
	private claimAclaimBundlePoint _claimBundlePoint = null;
	private claimAclaimBundleRate _claimBundleRate = null;
	private claimAadministration _administration = null;
	private claimAadmMemo _admMemo = null;
	private claimAbundleNumber _bundleNumber = null;
	private claimAmethodPoint _methodPoint = null;
	private claimAmaterialPoint _materialPoint = null;
	private claimAdrugPoint _drugPoint = null;
	private claimAppsClass _ppsClass = null;
	private Vector _item = new Vector();
	private claimAmemo _memo = null;
	
	public claimAbundle() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __claimAclassCode != null ) pw.print(" " + "claimA:classCode" +  "=" + "'" + __claimAclassCode + "'");
			if ( __claimAclassCodeId != null ) pw.print(" " + "claimA:classCodeId" +  "=" + "'" + __claimAclassCodeId + "'");

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
			if ( _claimBundlePoint != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_claimBundlePoint.printObject(pw, visitor);
			}
			if ( _claimBundleRate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_claimBundleRate.printObject(pw, visitor);
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
			if ( _methodPoint != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_methodPoint.printObject(pw, visitor);
			}
			if ( _materialPoint != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_materialPoint.printObject(pw, visitor);
			}
			if ( _drugPoint != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_drugPoint.printObject(pw, visitor);
			}
			if ( _ppsClass != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_ppsClass.printObject(pw, visitor);
			}
			if (this._item != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._item.size(); ++i ) {
					((claimAitem)this._item.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("claimA:bundle") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimAbundle obj = new claimAbundle();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimAbundle)builder.getElement()).setNamespace( getNamespace() );
			((claimAbundle)builder.getElement()).setLocalName( getLocalName() );
			((claimAbundle)builder.getElement()).setQName( getQName() );
			((claimAbundle)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setClaimAclassCode( atts.getValue(namespaceURI, "classCode") );
				((claimAbundle)builder.getElement()).setClaimAclassCode( atts.getValue(namespaceURI, "classCode") );
				setClaimAclassCodeId( atts.getValue(namespaceURI, "classCodeId") );
				((claimAbundle)builder.getElement()).setClaimAclassCodeId( atts.getValue(namespaceURI, "classCodeId") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:bundle") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:ClaimAmountModule")) {
				Vector v = ((claimAClaimAmountModule)builder.getParent()).getBundle();
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
	public void setClaimAclassCode(String __claimAclassCode) {
		this.__claimAclassCode = __claimAclassCode;
	}
	public String getClaimAclassCode() {
		return __claimAclassCode;
	}
	public void setClaimAclassCodeId(String __claimAclassCodeId) {
		this.__claimAclassCodeId = __claimAclassCodeId;
	}
	public String getClaimAclassCodeId() {
		return __claimAclassCodeId;
	}

	public void setClassName(claimAclassName _className) {
		this._className = _className;
	}
	public claimAclassName getClassName() {
		return _className;
	}
	public void setClaimBundlePoint(claimAclaimBundlePoint _claimBundlePoint) {
		this._claimBundlePoint = _claimBundlePoint;
	}
	public claimAclaimBundlePoint getClaimBundlePoint() {
		return _claimBundlePoint;
	}
	public void setClaimBundleRate(claimAclaimBundleRate _claimBundleRate) {
		this._claimBundleRate = _claimBundleRate;
	}
	public claimAclaimBundleRate getClaimBundleRate() {
		return _claimBundleRate;
	}
	public void setAdministration(claimAadministration _administration) {
		this._administration = _administration;
	}
	public claimAadministration getAdministration() {
		return _administration;
	}
	public void setAdmMemo(claimAadmMemo _admMemo) {
		this._admMemo = _admMemo;
	}
	public claimAadmMemo getAdmMemo() {
		return _admMemo;
	}
	public void setBundleNumber(claimAbundleNumber _bundleNumber) {
		this._bundleNumber = _bundleNumber;
	}
	public claimAbundleNumber getBundleNumber() {
		return _bundleNumber;
	}
	public void setMethodPoint(claimAmethodPoint _methodPoint) {
		this._methodPoint = _methodPoint;
	}
	public claimAmethodPoint getMethodPoint() {
		return _methodPoint;
	}
	public void setMaterialPoint(claimAmaterialPoint _materialPoint) {
		this._materialPoint = _materialPoint;
	}
	public claimAmaterialPoint getMaterialPoint() {
		return _materialPoint;
	}
	public void setDrugPoint(claimAdrugPoint _drugPoint) {
		this._drugPoint = _drugPoint;
	}
	public claimAdrugPoint getDrugPoint() {
		return _drugPoint;
	}
	public void setPpsClass(claimAppsClass _ppsClass) {
		this._ppsClass = _ppsClass;
	}
	public claimAppsClass getPpsClass() {
		return _ppsClass;
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
	public void setMemo(claimAmemo _memo) {
		this._memo = _memo;
	}
	public claimAmemo getMemo() {
		return _memo;
	}
	
}