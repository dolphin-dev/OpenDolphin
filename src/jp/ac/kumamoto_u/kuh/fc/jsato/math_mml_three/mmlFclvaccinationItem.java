/**
 *
 * mmlFclvaccinationItem.java
 * Created on 2003/1/4 2:30:5
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
			if ( this.getLocalName().equals("levelone") ) {
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
				Vector v = ((mmlFclvaccination)builder.getParent()).get_vaccinationItem();
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
	public void set_vaccine(mmlFclvaccine _vaccine) {
		this._vaccine = _vaccine;
	}
	public mmlFclvaccine get_vaccine() {
		return _vaccine;
	}
	public void set_injected(mmlFclinjected _injected) {
		this._injected = _injected;
	}
	public mmlFclinjected get_injected() {
		return _injected;
	}
	public void set_age(mmlFclage _age) {
		this._age = _age;
	}
	public mmlFclage get_age() {
		return _age;
	}
	public void set_memo(mmlFclmemo _memo) {
		this._memo = _memo;
	}
	public mmlFclmemo get_memo() {
		return _memo;
	}
	
}