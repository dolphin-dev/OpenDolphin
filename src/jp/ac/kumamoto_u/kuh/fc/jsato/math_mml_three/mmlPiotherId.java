/**
 *
 * mmlPiotherId.java
 * Created on 2003/1/4 2:30:0
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlPi:type") ) {
						set__mmlPitype( atts.getValue(i) );
						((mmlPiotherId)builder.getElement()).set__mmlPitype( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPi:otherId") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPi:uniqueInfo")) {
				Vector v = ((mmlPiuniqueInfo)builder.getParent()).get_otherId();
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
	public void set__mmlPitype(String __mmlPitype) {
		this.__mmlPitype = __mmlPitype;
	}
	public String get__mmlPitype() {
		return __mmlPitype;
	}

	public void set_Id(mmlCmId _Id) {
		this._Id = _Id;
	}
	public mmlCmId get_Id() {
		return _Id;
	}
	
}