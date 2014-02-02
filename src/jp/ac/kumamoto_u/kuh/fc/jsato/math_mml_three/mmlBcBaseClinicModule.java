/**
 *
 * mmlBcBaseClinicModule.java
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
public class mmlBcBaseClinicModule extends MMLObject {
	
	/* fields */
	private mmlBcallergy _allergy = null;
	private mmlBcbloodtype _bloodtype = null;
	private mmlBcinfection _infection = null;
	
	public mmlBcBaseClinicModule() {
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
			if ( _allergy != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_allergy.printObject(pw, visitor);
			}
			if ( _bloodtype != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_bloodtype.printObject(pw, visitor);
			}
			if ( _infection != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_infection.printObject(pw, visitor);
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
		if (qName.equals("mmlBc:BaseClinicModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlBcBaseClinicModule obj = new mmlBcBaseClinicModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlBcBaseClinicModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlBcBaseClinicModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlBcBaseClinicModule)builder.getElement()).setQName( getQName() );
			((mmlBcBaseClinicModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlBc:BaseClinicModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlBcBaseClinicModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_BaseClinicModule((mmlBcBaseClinicModule)builder.getElement());
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
	public void set_allergy(mmlBcallergy _allergy) {
		this._allergy = _allergy;
	}
	public mmlBcallergy get_allergy() {
		return _allergy;
	}
	public void set_bloodtype(mmlBcbloodtype _bloodtype) {
		this._bloodtype = _bloodtype;
	}
	public mmlBcbloodtype get_bloodtype() {
		return _bloodtype;
	}
	public void set_infection(mmlBcinfection _infection) {
		this._infection = _infection;
	}
	public mmlBcinfection get_infection() {
		return _infection;
	}
	
}