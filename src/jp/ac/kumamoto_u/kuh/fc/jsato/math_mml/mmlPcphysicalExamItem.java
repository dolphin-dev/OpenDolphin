/**
 *
 * mmlPcphysicalExamItem.java
 * Created on 2002/7/30 10:0:29
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
public class mmlPcphysicalExamItem extends MMLObject {
	
	/* fields */
	private mmlPctitle _title = null;
	private mmlPcresult _result = null;
	private mmlPcinterpretation _interpretation = null;
	private mmlPcreferenceInfo _referenceInfo = null;
	
	public mmlPcphysicalExamItem() {
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
			if ( _title != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_title.printObject(pw, visitor);
			}
			if ( _result != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_result.printObject(pw, visitor);
			}
			if ( _interpretation != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_interpretation.printObject(pw, visitor);
			}
			if ( _referenceInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_referenceInfo.printObject(pw, visitor);
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
		if (qName.equals("mmlPc:physicalExamItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcphysicalExamItem obj = new mmlPcphysicalExamItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcphysicalExamItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcphysicalExamItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcphysicalExamItem)builder.getElement()).setQName( getQName() );
			((mmlPcphysicalExamItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:physicalExamItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:physicalExam")) {
				Vector v = ((mmlPcphysicalExam)builder.getParent()).getPhysicalExamItem();
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
	public void setTitle(mmlPctitle _title) {
		this._title = _title;
	}
	public mmlPctitle getTitle() {
		return _title;
	}
	public void setResult(mmlPcresult _result) {
		this._result = _result;
	}
	public mmlPcresult getResult() {
		return _result;
	}
	public void setInterpretation(mmlPcinterpretation _interpretation) {
		this._interpretation = _interpretation;
	}
	public mmlPcinterpretation getInterpretation() {
		return _interpretation;
	}
	public void setReferenceInfo(mmlPcreferenceInfo _referenceInfo) {
		this._referenceInfo = _referenceInfo;
	}
	public mmlPcreferenceInfo getReferenceInfo() {
		return _referenceInfo;
	}
	
}