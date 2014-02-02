/**
 *
 * mmldocInfo.java
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
public class mmldocInfo extends MMLObject {
	
	/* fields */
	private String __contentModuleType = null;
	private String __moduleVersion = null;

	private mmlsecurityLevel _securityLevel = null;
	private mmltitle _title = null;
	private mmldocId _docId = null;
	private mmlconfirmDate _confirmDate = null;
	private mmlCiCreatorInfo _CreatorInfo = null;
	private mmlextRefs _extRefs = null;
	
	public mmldocInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __contentModuleType != null ) pw.print(" " + "contentModuleType" +  "=" + "'" + __contentModuleType + "'");
			if ( __moduleVersion != null ) pw.print(" " + "moduleVersion" +  "=" + "'" + __moduleVersion + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _securityLevel != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_securityLevel.printObject(pw, visitor);
			}
			if ( _title != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_title.printObject(pw, visitor);
			}
			if ( _docId != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_docId.printObject(pw, visitor);
			}
			if ( _confirmDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_confirmDate.printObject(pw, visitor);
			}
			if ( _CreatorInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_CreatorInfo.printObject(pw, visitor);
			}
			if ( _extRefs != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_extRefs.printObject(pw, visitor);
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
		if (qName.equals("mml:docInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmldocInfo obj = new mmldocInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmldocInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmldocInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmldocInfo)builder.getElement()).setQName( getQName() );
			((mmldocInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("contentModuleType") ) {
						set__contentModuleType( atts.getValue(i) );
						((mmldocInfo)builder.getElement()).set__contentModuleType( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("moduleVersion") ) {
						set__moduleVersion( atts.getValue(i) );
						((mmldocInfo)builder.getElement()).set__moduleVersion( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:docInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmldocInfo)builder.getElement() );
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
	public void set__contentModuleType(String __contentModuleType) {
		this.__contentModuleType = __contentModuleType;
	}
	public String get__contentModuleType() {
		return __contentModuleType;
	}
	public void set__moduleVersion(String __moduleVersion) {
		this.__moduleVersion = __moduleVersion;
	}
	public String get__moduleVersion() {
		return __moduleVersion;
	}

	public void set_securityLevel(mmlsecurityLevel _securityLevel) {
		this._securityLevel = _securityLevel;
	}
	public mmlsecurityLevel get_securityLevel() {
		return _securityLevel;
	}
	public void set_title(mmltitle _title) {
		this._title = _title;
	}
	public mmltitle get_title() {
		return _title;
	}
	public void set_docId(mmldocId _docId) {
		this._docId = _docId;
	}
	public mmldocId get_docId() {
		return _docId;
	}
	public void set_confirmDate(mmlconfirmDate _confirmDate) {
		this._confirmDate = _confirmDate;
	}
	public mmlconfirmDate get_confirmDate() {
		return _confirmDate;
	}
	public void set_CreatorInfo(mmlCiCreatorInfo _CreatorInfo) {
		this._CreatorInfo = _CreatorInfo;
	}
	public mmlCiCreatorInfo get_CreatorInfo() {
		return _CreatorInfo;
	}
	public void set_extRefs(mmlextRefs _extRefs) {
		this._extRefs = _extRefs;
	}
	public mmlextRefs get_extRefs() {
		return _extRefs;
	}
	
}