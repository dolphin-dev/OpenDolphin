/**
 *
 * mmlBcother.java
 * Created on 2003/1/4 2:30:4
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
			if ( this.getLocalName().equals("levelone") ) {
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
				Vector v = ((mmlBcothers)builder.getParent()).get_other();
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
	public void set_typeName(mmlBctypeName _typeName) {
		this._typeName = _typeName;
	}
	public mmlBctypeName get_typeName() {
		return _typeName;
	}
	public void set_typeJudgement(mmlBctypeJudgement _typeJudgement) {
		this._typeJudgement = _typeJudgement;
	}
	public mmlBctypeJudgement get_typeJudgement() {
		return _typeJudgement;
	}
	public void set_description(mmlBcdescription _description) {
		this._description = _description;
	}
	public mmlBcdescription get_description() {
		return _description;
	}
	
}