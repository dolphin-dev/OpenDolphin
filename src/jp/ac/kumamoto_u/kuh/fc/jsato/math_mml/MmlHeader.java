/**
 *
 * MmlHeader.java
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
public class MmlHeader extends MMLObject {
	
	/* fields */
	private mmlCiCreatorInfo _CreatorInfo = null;
	private masterId _masterId = null;
	private toc _toc = null;
	private scopePeriod _scopePeriod = null;
	private encryptInfo _encryptInfo = null;
	
	public MmlHeader() {
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
			if ( _CreatorInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_CreatorInfo.printObject(pw, visitor);
			}
			if ( _masterId != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_masterId.printObject(pw, visitor);
			}
			if ( _toc != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_toc.printObject(pw, visitor);
			}
			if ( _scopePeriod != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_scopePeriod.printObject(pw, visitor);
			}
			if ( _encryptInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_encryptInfo.printObject(pw, visitor);
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
		if (qName.equals("MmlHeader") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			MmlHeader obj = new MmlHeader();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((MmlHeader)builder.getElement()).setNamespace( getNamespace() );
			((MmlHeader)builder.getElement()).setLocalName( getLocalName() );
			((MmlHeader)builder.getElement()).setQName( getQName() );
			((MmlHeader)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("MmlHeader") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("Mml")) {
				((Mml)builder.getParent()).setMmlHeader((MmlHeader)builder.getElement());
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
	public void setCreatorInfo(mmlCiCreatorInfo _CreatorInfo) {
		this._CreatorInfo = _CreatorInfo;
	}
	public mmlCiCreatorInfo getCreatorInfo() {
		return _CreatorInfo;
	}
	public void setMasterId(masterId _masterId) {
		this._masterId = _masterId;
	}
	public masterId getMasterId() {
		return _masterId;
	}
	public void setToc(toc _toc) {
		this._toc = _toc;
	}
	public toc getToc() {
		return _toc;
	}
	public void setScopePeriod(scopePeriod _scopePeriod) {
		this._scopePeriod = _scopePeriod;
	}
	public scopePeriod getScopePeriod() {
		return _scopePeriod;
	}
	public void setEncryptInfo(encryptInfo _encryptInfo) {
		this._encryptInfo = _encryptInfo;
	}
	public encryptInfo getEncryptInfo() {
		return _encryptInfo;
	}
	
}