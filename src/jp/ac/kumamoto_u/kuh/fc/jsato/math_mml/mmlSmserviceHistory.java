/**
 *
 * mmlSmserviceHistory.java
 * Created on 2002/7/30 10:0:32
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
public class mmlSmserviceHistory extends MMLObject {
	
	/* fields */
	private String __mmlSmstart = null;
	private String __mmlSmend = null;

	private mmlSmoutPatient _outPatient = null;
	private mmlSminPatient _inPatient = null;
	
	public mmlSmserviceHistory() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlSmstart != null ) pw.print(" " + "mmlSm:start" +  "=" + "'" + __mmlSmstart + "'");
			if ( __mmlSmend != null ) pw.print(" " + "mmlSm:end" +  "=" + "'" + __mmlSmend + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _outPatient != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_outPatient.printObject(pw, visitor);
			}
			if ( _inPatient != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_inPatient.printObject(pw, visitor);
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
		if (qName.equals("mmlSm:serviceHistory") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmserviceHistory obj = new mmlSmserviceHistory();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmserviceHistory)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmserviceHistory)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmserviceHistory)builder.getElement()).setQName( getQName() );
			((mmlSmserviceHistory)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlSmstart( atts.getValue(namespaceURI, "start") );
				((mmlSmserviceHistory)builder.getElement()).setMmlSmstart( atts.getValue(namespaceURI, "start") );
				setMmlSmend( atts.getValue(namespaceURI, "end") );
				((mmlSmserviceHistory)builder.getElement()).setMmlSmend( atts.getValue(namespaceURI, "end") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:serviceHistory") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:SummaryModule")) {
				((mmlSmSummaryModule)builder.getParent()).setServiceHistory((mmlSmserviceHistory)builder.getElement());
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
	public void setMmlSmstart(String __mmlSmstart) {
		this.__mmlSmstart = __mmlSmstart;
	}
	public String getMmlSmstart() {
		return __mmlSmstart;
	}
	public void setMmlSmend(String __mmlSmend) {
		this.__mmlSmend = __mmlSmend;
	}
	public String getMmlSmend() {
		return __mmlSmend;
	}

	public void setOutPatient(mmlSmoutPatient _outPatient) {
		this._outPatient = _outPatient;
	}
	public mmlSmoutPatient getOutPatient() {
		return _outPatient;
	}
	public void setInPatient(mmlSminPatient _inPatient) {
		this._inPatient = _inPatient;
	}
	public mmlSminPatient getInPatient() {
		return _inPatient;
	}
	
}