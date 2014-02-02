/**
 *
 * mmlPcsubjectiveItem.java
 * Created on 2003/1/4 2:30:6
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
public class mmlPcsubjectiveItem extends MMLObject {
	
	/* fields */
	private mmlPctimeExpression _timeExpression = null;
	private Vector _eventExpression = new Vector();
	
	public mmlPcsubjectiveItem() {
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
			if ( _timeExpression != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_timeExpression.printObject(pw, visitor);
			}
			if (this._eventExpression != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._eventExpression.size(); ++i ) {
					((mmlPceventExpression)this._eventExpression.elementAt(i)).printObject(pw, visitor);
				}
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
		if (qName.equals("mmlPc:subjectiveItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcsubjectiveItem obj = new mmlPcsubjectiveItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcsubjectiveItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcsubjectiveItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcsubjectiveItem)builder.getElement()).setQName( getQName() );
			((mmlPcsubjectiveItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:subjectiveItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:subjective")) {
				Vector v = ((mmlPcsubjective)builder.getParent()).get_subjectiveItem();
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
	public void set_timeExpression(mmlPctimeExpression _timeExpression) {
		this._timeExpression = _timeExpression;
	}
	public mmlPctimeExpression get_timeExpression() {
		return _timeExpression;
	}
	public void set_eventExpression(Vector _eventExpression) {
		if (this._eventExpression != null) this._eventExpression.removeAllElements();
		// copy entire elements in the vector
		this._eventExpression = new Vector();
		for (int i = 0; i < _eventExpression.size(); ++i) {
			this._eventExpression.addElement( _eventExpression.elementAt(i) );
		}
	}
	public Vector get_eventExpression() {
		return _eventExpression;
	}
	
}