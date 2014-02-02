/**
 *
 * mmlFclchildhood.java
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
public class mmlFclchildhood extends MMLObject {
	
	/* fields */
	private mmlFclbirthInfo _birthInfo = null;
	private mmlFclvaccination _vaccination = null;
	
	public mmlFclchildhood() {
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
			if ( _birthInfo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_birthInfo.printObject(pw, visitor);
			}
			if ( _vaccination != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_vaccination.printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:childhood") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclchildhood obj = new mmlFclchildhood();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclchildhood)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclchildhood)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclchildhood)builder.getElement()).setQName( getQName() );
			((mmlFclchildhood)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:childhood") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:FirstClinicModule")) {
				((mmlFclFirstClinicModule)builder.getParent()).set_childhood((mmlFclchildhood)builder.getElement());
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
	public void set_birthInfo(mmlFclbirthInfo _birthInfo) {
		this._birthInfo = _birthInfo;
	}
	public mmlFclbirthInfo get_birthInfo() {
		return _birthInfo;
	}
	public void set_vaccination(mmlFclvaccination _vaccination) {
		this._vaccination = _vaccination;
	}
	public mmlFclvaccination get_vaccination() {
		return _vaccination;
	}
	
}