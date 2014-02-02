/**
 *
 * mmlSgstaffInfo.java
 * Created on 2003/1/4 2:30:9
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
public class mmlSgstaffInfo extends MMLObject {
	
	/* fields */
	private Vector _PersonalizedInfo = new Vector();
	
	public mmlSgstaffInfo() {
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
			if (this._PersonalizedInfo != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._PersonalizedInfo.size(); ++i ) {
					((mmlPsiPersonalizedInfo)this._PersonalizedInfo.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSg:staffInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgstaffInfo obj = new mmlSgstaffInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgstaffInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgstaffInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgstaffInfo)builder.getElement()).setQName( getQName() );
			((mmlSgstaffInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:staffInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:staff")) {
				((mmlSgstaff)builder.getParent()).set_staffInfo((mmlSgstaffInfo)builder.getElement());
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
	public void set_PersonalizedInfo(Vector _PersonalizedInfo) {
		if (this._PersonalizedInfo != null) this._PersonalizedInfo.removeAllElements();
		// copy entire elements in the vector
		this._PersonalizedInfo = new Vector();
		for (int i = 0; i < _PersonalizedInfo.size(); ++i) {
			this._PersonalizedInfo.addElement( _PersonalizedInfo.elementAt(i) );
		}
	}
	public Vector get_PersonalizedInfo() {
		return _PersonalizedInfo;
	}
	
}