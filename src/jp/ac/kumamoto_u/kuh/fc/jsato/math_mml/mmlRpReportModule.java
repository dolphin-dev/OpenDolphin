/**
 *
 * mmlRpReportModule.java
 * Created on 2002/7/30 10:0:35
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
public class mmlRpReportModule extends MMLObject {
	
	/* fields */
	private mmlRpinformation _information = null;
	private mmlRpreportBody _reportBody = null;
	
	public mmlRpReportModule() {
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
			if ( _information != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_information.printObject(pw, visitor);
			}
			if ( _reportBody != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_reportBody.printObject(pw, visitor);
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
		if (qName.equals("mmlRp:ReportModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpReportModule obj = new mmlRpReportModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpReportModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpReportModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpReportModule)builder.getElement()).setQName( getQName() );
			((mmlRpReportModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:ReportModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("content")) {
				((content)builder.getParent()).setReportModule((mmlRpReportModule)builder.getElement());
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
	public void setInformation(mmlRpinformation _information) {
		this._information = _information;
	}
	public mmlRpinformation getInformation() {
		return _information;
	}
	public void setReportBody(mmlRpreportBody _reportBody) {
		this._reportBody = _reportBody;
	}
	public mmlRpreportBody getReportBody() {
		return _reportBody;
	}
	
}