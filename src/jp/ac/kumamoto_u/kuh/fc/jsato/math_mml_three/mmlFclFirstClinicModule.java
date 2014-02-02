/**
 *
 * mmlFclFirstClinicModule.java
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
public class mmlFclFirstClinicModule extends MMLObject {
	
	/* fields */
	private mmlFclfamilyHistory _familyHistory = null;
	private mmlFclchildhood _childhood = null;
	private mmlFclpastHistory _pastHistory = null;
	private mmlFclchiefComplaints _chiefComplaints = null;
	private mmlFclpresentIllnessNotes _presentIllnessNotes = null;
	
	public mmlFclFirstClinicModule() {
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
			if ( _familyHistory != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_familyHistory.printObject(pw, visitor);
			}
			if ( _childhood != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_childhood.printObject(pw, visitor);
			}
			if ( _pastHistory != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_pastHistory.printObject(pw, visitor);
			}
			if ( _chiefComplaints != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_chiefComplaints.printObject(pw, visitor);
			}
			if ( _presentIllnessNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_presentIllnessNotes.printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:FirstClinicModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclFirstClinicModule obj = new mmlFclFirstClinicModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclFirstClinicModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclFirstClinicModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclFirstClinicModule)builder.getElement()).setQName( getQName() );
			((mmlFclFirstClinicModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:FirstClinicModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlFclFirstClinicModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_FirstClinicModule((mmlFclFirstClinicModule)builder.getElement());
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
	public void set_familyHistory(mmlFclfamilyHistory _familyHistory) {
		this._familyHistory = _familyHistory;
	}
	public mmlFclfamilyHistory get_familyHistory() {
		return _familyHistory;
	}
	public void set_childhood(mmlFclchildhood _childhood) {
		this._childhood = _childhood;
	}
	public mmlFclchildhood get_childhood() {
		return _childhood;
	}
	public void set_pastHistory(mmlFclpastHistory _pastHistory) {
		this._pastHistory = _pastHistory;
	}
	public mmlFclpastHistory get_pastHistory() {
		return _pastHistory;
	}
	public void set_chiefComplaints(mmlFclchiefComplaints _chiefComplaints) {
		this._chiefComplaints = _chiefComplaints;
	}
	public mmlFclchiefComplaints get_chiefComplaints() {
		return _chiefComplaints;
	}
	public void set_presentIllnessNotes(mmlFclpresentIllnessNotes _presentIllnessNotes) {
		this._presentIllnessNotes = _presentIllnessNotes;
	}
	public mmlFclpresentIllnessNotes get_presentIllnessNotes() {
		return _presentIllnessNotes;
	}
	
}