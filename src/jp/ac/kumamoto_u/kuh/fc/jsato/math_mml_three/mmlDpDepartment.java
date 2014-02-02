/**
 *
 * mmlDpDepartment.java
 * Created on 2003/1/4 2:29:54
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
public class mmlDpDepartment extends MMLObject {
	
	/* fields */
	private Vector _name = new Vector();
	private mmlCmId _Id = null;
	
	public mmlDpDepartment() {
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
			if (this._name != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._name.size(); ++i ) {
					((mmlDpname)this._name.elementAt(i)).printObject(pw, visitor);
				}
			}
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
		if (qName.equals("mmlDp:Department") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlDpDepartment obj = new mmlDpDepartment();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlDpDepartment)builder.getElement()).setNamespace( getNamespace() );
			((mmlDpDepartment)builder.getElement()).setLocalName( getLocalName() );
			((mmlDpDepartment)builder.getElement()).setQName( getQName() );
			((mmlDpDepartment)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlDp:Department") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgicalDepartment")) {
				Vector v = ((mmlSgsurgicalDepartment)builder.getParent()).get_Department();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlSg:patientDepartment")) {
				Vector v = ((mmlSgpatientDepartment)builder.getParent()).get_Department();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlRe:referToFacility")) {
				((mmlRereferToFacility)builder.getParent()).set_Department((mmlDpDepartment)builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPsi:PersonalizedInfo")) {
				((mmlPsiPersonalizedInfo)builder.getParent()).set_Department((mmlDpDepartment)builder.getElement());
			}

			if (parentElement.getQName().equals("claim:patientWard")) {
				((claimpatientWard)builder.getParent()).set_Department((mmlDpDepartment)builder.getElement());
			}

			if (parentElement.getQName().equals("claim:patientDepartment")) {
				((claimpatientDepartment)builder.getParent()).set_Department((mmlDpDepartment)builder.getElement());
			}

			if (parentElement.getQName().equals("claimA:patientWard")) {
				((claimApatientWard)builder.getParent()).set_Department((mmlDpDepartment)builder.getElement());
			}

			if (parentElement.getQName().equals("claimA:patientDepartment")) {
				((claimApatientDepartment)builder.getParent()).set_Department((mmlDpDepartment)builder.getElement());
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
	public void set_name(Vector _name) {
		if (this._name != null) this._name.removeAllElements();
		// copy entire elements in the vector
		this._name = new Vector();
		for (int i = 0; i < _name.size(); ++i) {
			this._name.addElement( _name.elementAt(i) );
		}
	}
	public Vector get_name() {
		return _name;
	}
	public void set_Id(mmlCmId _Id) {
		this._Id = _Id;
	}
	public mmlCmId get_Id() {
		return _Id;
	}
	
}