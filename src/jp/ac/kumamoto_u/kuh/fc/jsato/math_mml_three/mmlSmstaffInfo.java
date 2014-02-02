/**
 *
 * mmlSmstaffInfo.java
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
public class mmlSmstaffInfo extends MMLObject {
	
	/* fields */
	private mmlPsiPersonalizedInfo _PersonalizedInfo = null;
	private Vector _creatorLicense = new Vector();
	
	public mmlSmstaffInfo() {
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
			if (this._creatorLicense != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._creatorLicense.size(); ++i ) {
					((mmlCicreatorLicense)this._creatorLicense.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSm:staffInfo") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmstaffInfo obj = new mmlSmstaffInfo();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmstaffInfo)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmstaffInfo)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmstaffInfo)builder.getElement()).setQName( getQName() );
			((mmlSmstaffInfo)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:staffInfo") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:staffs")) {
				Vector v = ((mmlSmstaffs)builder.getParent()).get_staffInfo();
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
	public void set_PersonalizedInfo(mmlPsiPersonalizedInfo _PersonalizedInfo) {
		this._PersonalizedInfo = _PersonalizedInfo;
	}
	public mmlPsiPersonalizedInfo get_PersonalizedInfo() {
		return _PersonalizedInfo;
	}
	public void set_creatorLicense(Vector _creatorLicense) {
		if (this._creatorLicense != null) this._creatorLicense.removeAllElements();
		// copy entire elements in the vector
		this._creatorLicense = new Vector();
		for (int i = 0; i < _creatorLicense.size(); ++i) {
			this._creatorLicense.addElement( _creatorLicense.elementAt(i) );
		}
	}
	public Vector get_creatorLicense() {
		return _creatorLicense;
	}
	
}