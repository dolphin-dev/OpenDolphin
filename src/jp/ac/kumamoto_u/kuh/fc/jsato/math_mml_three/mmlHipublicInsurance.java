/**
 *
 * mmlHipublicInsurance.java
 * Created on 2003/1/4 2:30:2
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
public class mmlHipublicInsurance extends MMLObject {
	
	/* fields */
	private Vector _publicInsuranceItem = new Vector();
	
	public mmlHipublicInsurance() {
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
			if (this._publicInsuranceItem != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._publicInsuranceItem.size(); ++i ) {
					((mmlHipublicInsuranceItem)this._publicInsuranceItem.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlHi:publicInsurance") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHipublicInsurance obj = new mmlHipublicInsurance();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHipublicInsurance)builder.getElement()).setNamespace( getNamespace() );
			((mmlHipublicInsurance)builder.getElement()).setLocalName( getLocalName() );
			((mmlHipublicInsurance)builder.getElement()).setQName( getQName() );
			((mmlHipublicInsurance)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:publicInsurance") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:HealthInsuranceModule")) {
				((mmlHiHealthInsuranceModule)builder.getParent()).set_publicInsurance((mmlHipublicInsurance)builder.getElement());
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
	public void set_publicInsuranceItem(Vector _publicInsuranceItem) {
		if (this._publicInsuranceItem != null) this._publicInsuranceItem.removeAllElements();
		// copy entire elements in the vector
		this._publicInsuranceItem = new Vector();
		for (int i = 0; i < _publicInsuranceItem.size(); ++i) {
			this._publicInsuranceItem.addElement( _publicInsuranceItem.elementAt(i) );
		}
	}
	public Vector get_publicInsuranceItem() {
		return _publicInsuranceItem;
	}
	
}