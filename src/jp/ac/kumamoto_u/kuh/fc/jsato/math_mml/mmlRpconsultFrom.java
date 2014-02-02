/**
 *
 * mmlRpconsultFrom.java
 * Created on 2002/7/30 10:0:36
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
public class mmlRpconsultFrom extends MMLObject {
	
	/* fields */
	private mmlRpconFacility _conFacility = null;
	private mmlRpconDepartment _conDepartment = null;
	private mmlRpconWard _conWard = null;
	private mmlRpclient _client = null;
	
	public mmlRpconsultFrom() {
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
			if ( _conFacility != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_conFacility.printObject(pw, visitor);
			}
			if ( _conDepartment != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_conDepartment.printObject(pw, visitor);
			}
			if ( _conWard != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_conWard.printObject(pw, visitor);
			}
			if ( _client != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_client.printObject(pw, visitor);
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
		if (qName.equals("mmlRp:consultFrom") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpconsultFrom obj = new mmlRpconsultFrom();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpconsultFrom)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpconsultFrom)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpconsultFrom)builder.getElement()).setQName( getQName() );
			((mmlRpconsultFrom)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:consultFrom") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:information")) {
				((mmlRpinformation)builder.getParent()).setConsultFrom((mmlRpconsultFrom)builder.getElement());
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
	public void setConFacility(mmlRpconFacility _conFacility) {
		this._conFacility = _conFacility;
	}
	public mmlRpconFacility getConFacility() {
		return _conFacility;
	}
	public void setConDepartment(mmlRpconDepartment _conDepartment) {
		this._conDepartment = _conDepartment;
	}
	public mmlRpconDepartment getConDepartment() {
		return _conDepartment;
	}
	public void setConWard(mmlRpconWard _conWard) {
		this._conWard = _conWard;
	}
	public mmlRpconWard getConWard() {
		return _conWard;
	}
	public void setClient(mmlRpclient _client) {
		this._client = _client;
	}
	public mmlRpclient getClient() {
		return _client;
	}
	
}