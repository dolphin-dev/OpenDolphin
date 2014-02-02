/**
 *
 * mmlLblaboTest.java
 * Created on 2003/1/4 2:30:11
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
public class mmlLblaboTest extends MMLObject {
	
	/* fields */
	private mmlLbspecimen _specimen = null;
	private Vector _item = new Vector();
	
	public mmlLblaboTest() {
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
			if ( _specimen != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_specimen.printObject(pw, visitor);
			}
			if (this._item != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._item.size(); ++i ) {
					((mmlLbitem)this._item.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlLb:laboTest") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLblaboTest obj = new mmlLblaboTest();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLblaboTest)builder.getElement()).setNamespace( getNamespace() );
			((mmlLblaboTest)builder.getElement()).setLocalName( getLocalName() );
			((mmlLblaboTest)builder.getElement()).setQName( getQName() );
			((mmlLblaboTest)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLb:laboTest") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlLb:TestModule")) {
				Vector v = ((mmlLbTestModule)builder.getParent()).get_laboTest();
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
	public void set_specimen(mmlLbspecimen _specimen) {
		this._specimen = _specimen;
	}
	public mmlLbspecimen get_specimen() {
		return _specimen;
	}
	public void set_item(Vector _item) {
		if (this._item != null) this._item.removeAllElements();
		// copy entire elements in the vector
		this._item = new Vector();
		for (int i = 0; i < _item.size(); ++i) {
			this._item.addElement( _item.elementAt(i) );
		}
	}
	public Vector get_item() {
		return _item;
	}
	
}