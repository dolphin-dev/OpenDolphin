/**
 *
 * mmlSgprocedureItem.java
 * Created on 2003/1/4 2:30:8
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
public class mmlSgprocedureItem extends MMLObject {
	
	/* fields */
	private mmlSgoperation _operation = null;
	private mmlSgoperationElement _operationElement = null;
	private mmlSgprocedureMemo _procedureMemo = null;
	
	public mmlSgprocedureItem() {
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
			if ( _operation != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_operation.printObject(pw, visitor);
			}
			if ( _operationElement != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_operationElement.printObject(pw, visitor);
			}
			if ( _procedureMemo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_procedureMemo.printObject(pw, visitor);
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
		if (qName.equals("mmlSg:procedureItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgprocedureItem obj = new mmlSgprocedureItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgprocedureItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgprocedureItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgprocedureItem)builder.getElement()).setQName( getQName() );
			((mmlSgprocedureItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:procedureItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgicalProcedure")) {
				Vector v = ((mmlSgsurgicalProcedure)builder.getParent()).get_procedureItem();
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
	public void set_operation(mmlSgoperation _operation) {
		this._operation = _operation;
	}
	public mmlSgoperation get_operation() {
		return _operation;
	}
	public void set_operationElement(mmlSgoperationElement _operationElement) {
		this._operationElement = _operationElement;
	}
	public mmlSgoperationElement get_operationElement() {
		return _operationElement;
	}
	public void set_procedureMemo(mmlSgprocedureMemo _procedureMemo) {
		this._procedureMemo = _procedureMemo;
	}
	public mmlSgprocedureMemo get_procedureMemo() {
		return _procedureMemo;
	}
	
}