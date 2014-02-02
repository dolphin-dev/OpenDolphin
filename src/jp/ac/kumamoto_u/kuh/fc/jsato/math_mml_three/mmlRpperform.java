/**
 *
 * mmlRpperform.java
 * Created on 2003/1/4 2:30:20
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
public class mmlRpperform extends MMLObject {
	
	/* fields */
	private mmlRppFacility _pFacility = null;
	private mmlRppDepartment _pDepartment = null;
	private mmlRppWard _pWard = null;
	private mmlRpperformer _performer = null;
	private mmlRpsupervisor _supervisor = null;
	
	public mmlRpperform() {
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
			if ( _pFacility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_pFacility.printObject(pw, visitor);
			}
			if ( _pDepartment != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_pDepartment.printObject(pw, visitor);
			}
			if ( _pWard != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_pWard.printObject(pw, visitor);
			}
			if ( _performer != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_performer.printObject(pw, visitor);
			}
			if ( _supervisor != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_supervisor.printObject(pw, visitor);
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
		if (qName.equals("mmlRp:perform") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpperform obj = new mmlRpperform();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpperform)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpperform)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpperform)builder.getElement()).setQName( getQName() );
			((mmlRpperform)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:perform") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:information")) {
				((mmlRpinformation)builder.getParent()).set_perform((mmlRpperform)builder.getElement());
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
	public void set_pFacility(mmlRppFacility _pFacility) {
		this._pFacility = _pFacility;
	}
	public mmlRppFacility get_pFacility() {
		return _pFacility;
	}
	public void set_pDepartment(mmlRppDepartment _pDepartment) {
		this._pDepartment = _pDepartment;
	}
	public mmlRppDepartment get_pDepartment() {
		return _pDepartment;
	}
	public void set_pWard(mmlRppWard _pWard) {
		this._pWard = _pWard;
	}
	public mmlRppWard get_pWard() {
		return _pWard;
	}
	public void set_performer(mmlRpperformer _performer) {
		this._performer = _performer;
	}
	public mmlRpperformer get_performer() {
		return _performer;
	}
	public void set_supervisor(mmlRpsupervisor _supervisor) {
		this._supervisor = _supervisor;
	}
	public mmlRpsupervisor get_supervisor() {
		return _supervisor;
	}
	
}