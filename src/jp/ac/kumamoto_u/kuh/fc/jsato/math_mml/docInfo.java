/**
 *
 * docInfo.java
 * Created on 2002/7/30 10:0:24
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
public class docInfo extends MMLObject {
	
	/* fields */
	private String __contentModuleType = null;
	private String __moduleVersion = null;

	private securityLevel _securityLevel = null;
	private title _title = null;
	private docId _docId = null;
	private confirmDate _confirmDate = null;
	private mmlCiCreatorInfo _CreatorInfo = null;
	private extRefs _extRefs = null;
	
	public docInfo() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __contentModuleType != null ) pw.print(" " + "contentModuleType" +  "=" + "'" + __contentModuleType + "'");
			if ( __moduleVersion != null ) pw.print(" " + "moduleVersion" +  "=" + "'" + __moduleVersion + "'");

			if ( this.getLocalName().equals("Mml") ) {
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
		if (qName.equals("docInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			docInfo obj = new docInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((docInfo)builder.getElement()).setNamespace( getNamespace() );
			((docInfo)builder.getElement()).setLocalName( getLocalName() );
			((docInfo)builder.getElement()).setQName( getQName() );
			((docInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setContentModuleType( atts.getValue(namespaceURI, "contentModuleType") );
				((docInfo)builder.getElement()).setContentModuleType( atts.getValue(namespaceURI, "contentModuleType") );
				setModuleVersion( atts.getValue(namespaceURI, "moduleVersion") );
				((docInfo)builder.getElement()).setModuleVersion( atts.getValue(namespaceURI, "moduleVersion") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("docInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("MmlModuleItem")) {
				((MmlModuleItem)builder.getParent()).setDocInfo((docInfo)builder.getElement());
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
	public void setContentModuleType(String __contentModuleType) {
		this.__contentModuleType = __contentModuleType;
	}
	public String getContentModuleType() {
		return __contentModuleType;
	}
	public void setModuleVersion(String __moduleVersion) {
		this.__moduleVersion = __moduleVersion;
	}
	public String getModuleVersion() {
		return __moduleVersion;
	}

	public void setSecurityLevel(securityLevel _securityLevel) {
		this._securityLevel = _securityLevel;
	}
	public securityLevel getSecurityLevel() {
		return _securityLevel;
	}
	public void setTitle(title _title) {
		this._title = _title;
	}
	public title getTitle() {
		return _title;
	}
	public void setDocId(docId _docId) {
		this._docId = _docId;
	}
	public docId getDocId() {
		return _docId;
	}
	public void setConfirmDate(confirmDate _confirmDate) {
		this._confirmDate = _confirmDate;
	}
	public confirmDate getConfirmDate() {
		return _confirmDate;
	}
	public void setCreatorInfo(mmlCiCreatorInfo _CreatorInfo) {
		this._CreatorInfo = _CreatorInfo;
	}
	public mmlCiCreatorInfo getCreatorInfo() {
		return _CreatorInfo;
	}
	public void setExtRefs(extRefs _extRefs) {
		this._extRefs = _extRefs;
	}
	public extRefs getExtRefs() {
		return _extRefs;
	}
	
}