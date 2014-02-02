/**
 *
 * mmlSmreferTo.java
 * Created on 2003/1/4 2:30:10
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
public class mmlSmreferTo extends MMLObject {
	
	/* fields */
	private mmlPsiPersonalizedInfo _PersonalizedInfo = null;
	
	public mmlSmreferTo() {
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
			if ( _PersonalizedInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_PersonalizedInfo.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:referTo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmreferTo obj = new mmlSmreferTo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmreferTo)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmreferTo)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmreferTo)builder.getElement()).setQName( getQName() );
			((mmlSmreferTo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:referTo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:discharge")) {
				((mmlSmdischarge)builder.getParent()).set_referTo((mmlSmreferTo)builder.getElement());
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
	public void set_PersonalizedInfo(mmlPsiPersonalizedInfo _PersonalizedInfo) {
		this._PersonalizedInfo = _PersonalizedInfo;
	}
	public mmlPsiPersonalizedInfo get_PersonalizedInfo() {
		return _PersonalizedInfo;
	}
	
}