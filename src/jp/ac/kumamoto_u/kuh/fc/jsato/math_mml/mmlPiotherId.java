/**
 *
 * mmlPiotherId.java
 * Created on 2002/7/30 10:0:25
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
public class mmlPiotherId extends MMLObject {
	
	/* fields */
	private String __mmlPitype = null;

	private mmlCmId _Id = null;
	
	public mmlPiotherId() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlPitype != null ) pw.print(" " + "mmlPi:type" +  "=" + "'" + __mmlPitype + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _Id != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Id.printObject(pw, visitor);
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
		if (qName.equals("mmlPi:otherId") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPiotherId obj = new mmlPiotherId();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPiotherId)builder.getElement()).setNamespace( getNamespace() );
			((mmlPiotherId)builder.getElement()).setLocalName( getLocalName() );
			((mmlPiotherId)builder.getElement()).setQName( getQName() );
			((mmlPiotherId)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlPitype( atts.getValue(namespaceURI, "type") );
				((mmlPiotherId)builder.getElement()).setMmlPitype( atts.getValue(namespaceURI, "type") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPi:otherId") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPi:uniqueInfo")) {
				Vector v = ((mmlPiuniqueInfo)builder.getParent()).getOtherId();
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
	public void setMmlPitype(String __mmlPitype) {
		this.__mmlPitype = __mmlPitype;
	}
	public String getMmlPitype() {
		return __mmlPitype;
	}

	public void setId(mmlCmId _Id) {
		this._Id = _Id;
	}
	public mmlCmId getId() {
		return _Id;
	}
	
}