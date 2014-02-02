/**
 *
 * mmlLbspecimen.java
 * Created on 2002/7/30 10:0:35
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
public class mmlLbspecimen extends MMLObject {
	
	/* fields */
	private mmlLbspecimenName _specimenName = null;
	private Vector _spcMemo = new Vector();
	private mmlLbspcMemoF _spcMemoF = null;
	
	public mmlLbspecimen() {
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
			if ( _specimenName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_specimenName.printObject(pw, visitor);
			}
			if (this._spcMemo != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._spcMemo.size(); ++i ) {
					((mmlLbspcMemo)this._spcMemo.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _spcMemoF != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_spcMemoF.printObject(pw, visitor);
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
		if (qName.equals("mmlLb:specimen") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLbspecimen obj = new mmlLbspecimen();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLbspecimen)builder.getElement()).setNamespace( getNamespace() );
			((mmlLbspecimen)builder.getElement()).setLocalName( getLocalName() );
			((mmlLbspecimen)builder.getElement()).setQName( getQName() );
			((mmlLbspecimen)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:specimen") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:laboTest")) {
				((mmlLblaboTest)builder.getParent()).setSpecimen((mmlLbspecimen)builder.getElement());
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
	public void setSpecimenName(mmlLbspecimenName _specimenName) {
		this._specimenName = _specimenName;
	}
	public mmlLbspecimenName getSpecimenName() {
		return _specimenName;
	}
	public void setSpcMemo(Vector _spcMemo) {
		if (this._spcMemo != null) this._spcMemo.removeAllElements();
		// copy entire elements in the vector
		this._spcMemo = new Vector();
		for (int i = 0; i < _spcMemo.size(); ++i) {
			this._spcMemo.addElement( _spcMemo.elementAt(i) );
		}
	}
	public Vector getSpcMemo() {
		return _spcMemo;
	}
	public void setSpcMemoF(mmlLbspcMemoF _spcMemoF) {
		this._spcMemoF = _spcMemoF;
	}
	public mmlLbspcMemoF getSpcMemoF() {
		return _spcMemoF;
	}
	
}