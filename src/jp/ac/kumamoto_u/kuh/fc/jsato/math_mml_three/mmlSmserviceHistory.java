/**
 *
 * mmlSmserviceHistory.java
 * Created on 2003/1/4 2:30:9
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlSm:start") ) {
						set__mmlSmstart( atts.getValue(i) );
						((mmlSmserviceHistory)builder.getElement()).set__mmlSmstart( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlSm:end") ) {
						set__mmlSmend( atts.getValue(i) );
						((mmlSmserviceHistory)builder.getElement()).set__mmlSmend( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:serviceHistory") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:SummaryModule")) {
				((mmlSmSummaryModule)builder.getParent()).set_serviceHistory((mmlSmserviceHistory)builder.getElement());
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
	public void set__mmlSmstart(String __mmlSmstart) {
		this.__mmlSmstart = __mmlSmstart;
	}
	public String get__mmlSmstart() {
		return __mmlSmstart;
	}
	public void set__mmlSmend(String __mmlSmend) {
		this.__mmlSmend = __mmlSmend;
	}
	public String get__mmlSmend() {
		return __mmlSmend;
	}

	public void set_outPatient(mmlSmoutPatient _outPatient) {
		this._outPatient = _outPatient;
	}
	public mmlSmoutPatient get_outPatient() {
		return _outPatient;
	}
	public void set_inPatient(mmlSminPatient _inPatient) {
		this._inPatient = _inPatient;
	}
	public mmlSminPatient get_inPatient() {
		return _inPatient;
	}
	
}