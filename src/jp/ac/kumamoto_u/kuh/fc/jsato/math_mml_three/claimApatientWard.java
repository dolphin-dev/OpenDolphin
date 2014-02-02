/**
 *
 * claimApatientWard.java
 * Created on 2003/1/4 2:30:27
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
public class claimApatientWard extends MMLObject {
	
	/* fields */
	private mmlDpDepartment _Department = null;
	
	public claimApatientWard() {
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
			if ( _Department != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_Department.printObject(pw, visitor);
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
		if (qName.equals("claimA:patientWard") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			claimApatientWard obj = new claimApatientWard();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((claimApatientWard)builder.getElement()).setNamespace( getNamespace() );
			((claimApatientWard)builder.getElement()).setLocalName( getLocalName() );
			((claimApatientWard)builder.getElement()).setQName( getQName() );
			((claimApatientWard)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("claimA:patientWard") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("claimA:amountInformation")) {
				((claimAamountInformation)builder.getParent()).set_patientWard((claimApatientWard)builder.getElement());
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
	public void set_Department(mmlDpDepartment _Department) {
		this._Department = _Department;
	}
	public mmlDpDepartment get_Department() {
		return _Department;
	}
	
}