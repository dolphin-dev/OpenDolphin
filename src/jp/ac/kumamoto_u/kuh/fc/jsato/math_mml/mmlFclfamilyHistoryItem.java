/**
 *
 * mmlFclfamilyHistoryItem.java
 * Created on 2002/7/30 10:0:27
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
public class mmlFclfamilyHistoryItem extends MMLObject {
	
	/* fields */
	private mmlFclrelation _relation = null;
	private mmlRdRegisteredDiagnosisModule _RegisteredDiagnosisModule = null;
	private mmlFclage _age = null;
	private mmlFclmemo _memo = null;
	
	public mmlFclfamilyHistoryItem() {
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
			if ( _relation != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_relation.printObject(pw, visitor);
			}
			if ( _RegisteredDiagnosisModule != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_RegisteredDiagnosisModule.printObject(pw, visitor);
			}
			if ( _age != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_age.printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:familyHistoryItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclfamilyHistoryItem obj = new mmlFclfamilyHistoryItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclfamilyHistoryItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclfamilyHistoryItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclfamilyHistoryItem)builder.getElement()).setQName( getQName() );
			((mmlFclfamilyHistoryItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:familyHistoryItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:familyHistory")) {
				Vector v = ((mmlFclfamilyHistory)builder.getParent()).getFamilyHistoryItem();
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
	public void setRelation(mmlFclrelation _relation) {
		this._relation = _relation;
	}
	public mmlFclrelation getRelation() {
		return _relation;
	}
	public void setRegisteredDiagnosisModule(mmlRdRegisteredDiagnosisModule _RegisteredDiagnosisModule) {
		this._RegisteredDiagnosisModule = _RegisteredDiagnosisModule;
	}
	public mmlRdRegisteredDiagnosisModule getRegisteredDiagnosisModule() {
		return _RegisteredDiagnosisModule;
	}
	public void setAge(mmlFclage _age) {
		this._age = _age;
	}
	public mmlFclage getAge() {
		return _age;
	}
	public void setMemo(mmlFclmemo _memo) {
		this._memo = _memo;
	}
	public mmlFclmemo getMemo() {
		return _memo;
	}
	
}