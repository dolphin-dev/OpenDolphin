/**
 *
 * mmlRdrelatedHealthInsurance.java
 * Created on 2003/1/4 2:30:3
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
public class mmlRdrelatedHealthInsurance extends MMLObject {
	
	/* fields */
	private String __mmlRduid = null;

	
	public mmlRdrelatedHealthInsurance() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRduid != null ) pw.print(" " + "mmlRd:uid" +  "=" + "'" + __mmlRduid + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */

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
		if (qName.equals("mmlRd:relatedHealthInsurance") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRdrelatedHealthInsurance obj = new mmlRdrelatedHealthInsurance();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRdrelatedHealthInsurance)builder.getElement()).setNamespace( getNamespace() );
			((mmlRdrelatedHealthInsurance)builder.getElement()).setLocalName( getLocalName() );
			((mmlRdrelatedHealthInsurance)builder.getElement()).setQName( getQName() );
			((mmlRdrelatedHealthInsurance)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRd:uid") ) {
						set__mmlRduid( atts.getValue(i) );
						((mmlRdrelatedHealthInsurance)builder.getElement()).set__mmlRduid( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRd:relatedHealthInsurance") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRd:RegisteredDiagnosisModule")) {
				((mmlRdRegisteredDiagnosisModule)builder.getParent()).set_relatedHealthInsurance((mmlRdrelatedHealthInsurance)builder.getElement());
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
	public void set__mmlRduid(String __mmlRduid) {
		this.__mmlRduid = __mmlRduid;
	}
	public String get__mmlRduid() {
		return __mmlRduid;
	}

	
}