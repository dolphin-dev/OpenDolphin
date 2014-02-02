/**
 *
 * mmlBcother.java
 * Created on 2002/7/30 10:0:26
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
public class mmlBcother extends MMLObject {
	
	/* fields */
	private mmlBctypeName _typeName = null;
	private mmlBctypeJudgement _typeJudgement = null;
	private mmlBcdescription _description = null;
	
	public mmlBcother() {
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
			if ( _typeName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_typeName.printObject(pw, visitor);
			}
			if ( _typeJudgement != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_typeJudgement.printObject(pw, visitor);
			}
			if ( _description != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_description.printObject(pw, visitor);
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
		if (qName.equals("mmlBc:other") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcother obj = new mmlBcother();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcother)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcother)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcother)builder.getElement()).setQName( getQName() );
			((mmlBcother)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:other") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlBc:others")) {
				Vector v = ((mmlBcothers)builder.getParent()).getOther();
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
	public void setTypeName(mmlBctypeName _typeName) {
		this._typeName = _typeName;
	}
	public mmlBctypeName getTypeName() {
		return _typeName;
	}
	public void setTypeJudgement(mmlBctypeJudgement _typeJudgement) {
		this._typeJudgement = _typeJudgement;
	}
	public mmlBctypeJudgement getTypeJudgement() {
		return _typeJudgement;
	}
	public void setDescription(mmlBcdescription _description) {
		this._description = _description;
	}
	public mmlBcdescription getDescription() {
		return _description;
	}
	
}