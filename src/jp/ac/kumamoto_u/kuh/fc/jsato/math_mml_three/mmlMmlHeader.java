/**
 *
 * mmlMmlHeader.java
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
public class mmlMmlHeader extends MMLObject {
	
	/* fields */
	private mmlCiCreatorInfo _CreatorInfo = null;
	private mmlmasterId _masterId = null;
	private mmltoc _toc = null;
	private mmlscopePeriod _scopePeriod = null;
	private mmlencryptInfo _encryptInfo = null;
	
	public mmlMmlHeader() {
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
		if (qName.equals("mml:MmlHeader") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlMmlHeader obj = new mmlMmlHeader();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlMmlHeader)builder.getElement()).setNamespace( getNamespace() );
			((mmlMmlHeader)builder.getElement()).setLocalName( getLocalName() );
			((mmlMmlHeader)builder.getElement()).setQName( getQName() );
			((mmlMmlHeader)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:MmlHeader") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_header")) {
				Vector v = ((local_header)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlMmlHeader)builder.getElement() );
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
	public void set_CreatorInfo(mmlCiCreatorInfo _CreatorInfo) {
		this._CreatorInfo = _CreatorInfo;
	}
	public mmlCiCreatorInfo get_CreatorInfo() {
		return _CreatorInfo;
	}
	public void set_masterId(mmlmasterId _masterId) {
		this._masterId = _masterId;
	}
	public mmlmasterId get_masterId() {
		return _masterId;
	}
	public void set_toc(mmltoc _toc) {
		this._toc = _toc;
	}
	public mmltoc get_toc() {
		return _toc;
	}
	public void set_scopePeriod(mmlscopePeriod _scopePeriod) {
		this._scopePeriod = _scopePeriod;
	}
	public mmlscopePeriod get_scopePeriod() {
		return _scopePeriod;
	}
	public void set_encryptInfo(mmlencryptInfo _encryptInfo) {
		this._encryptInfo = _encryptInfo;
	}
	public mmlencryptInfo get_encryptInfo() {
		return _encryptInfo;
	}
	
}