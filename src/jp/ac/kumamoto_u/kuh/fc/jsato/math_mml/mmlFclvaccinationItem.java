/**
 *
 * mmlFclvaccinationItem.java
 * Created on 2002/7/30 10:0:27
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
public class mmlFclvaccinationItem extends MMLObject {
	
	/* fields */
	private mmlFclvaccine _vaccine = null;
	private mmlFclinjected _injected = null;
	private mmlFclage _age = null;
	private mmlFclmemo _memo = null;
	
	public mmlFclvaccinationItem() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _vaccine != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_vaccine.printObject(pw, visitor);
			}
			if ( _injected != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_injected.printObject(pw, visitor);
			}
			if ( _age != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_age.printObject(pw, visitor);
			}
			if ( _memo != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_memo.printObject(pw, visitor);
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
		if (qName.equals("mmlFcl:vaccinationItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlFclvaccinationItem obj = new mmlFclvaccinationItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlFclvaccinationItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlFclvaccinationItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlFclvaccinationItem)builder.getElement()).setQName( getQName() );
			((mmlFclvaccinationItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlFcl:vaccinationItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlFcl:vaccination")) {
				Vector v = ((mmlFclvaccination)builder.getParent()).getVaccinationItem();
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
	public void setVaccine(mmlFclvaccine _vaccine) {
		this._vaccine = _vaccine;
	}
	public mmlFclvaccine getVaccine() {
		return _vaccine;
	}
	public void setInjected(mmlFclinjected _injected) {
		this._injected = _injected;
	}
	public mmlFclinjected getInjected() {
		return _injected;
	}
	public void setAge(mmlFclage _age) {
		this._age = _age;
	}
	public mmlFclage getAge() {
		return _age;
	}
	public void setMemo(mmlFclmemo _memo) {
		this._memo = _memo;
	}
	public mmlFclmemo getMemo() {
		return _memo;
	}
	
}