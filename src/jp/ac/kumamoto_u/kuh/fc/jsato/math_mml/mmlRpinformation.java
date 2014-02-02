/**
 *
 * mmlRpinformation.java
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
public class mmlRpinformation extends MMLObject {
	
	/* fields */
	private String __mmlRpperformTime = null;
	private String __mmlRpreportTime = null;

	private mmlRpreportStatus _reportStatus = null;
	private mmlRptestClass _testClass = null;
	private mmlRptestSubclass _testSubclass = null;
	private mmlRporgan _organ = null;
	private mmlRpconsultFrom _consultFrom = null;
	private mmlRpperform _perform = null;
	
	public mmlRpinformation() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpperformTime != null ) pw.print(" " + "mmlRp:performTime" +  "=" + "'" + __mmlRpperformTime + "'");
			if ( __mmlRpreportTime != null ) pw.print(" " + "mmlRp:reportTime" +  "=" + "'" + __mmlRpreportTime + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _reportStatus != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_reportStatus.printObject(pw, visitor);
			}
			if ( _testClass != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testClass.printObject(pw, visitor);
			}
			if ( _testSubclass != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testSubclass.printObject(pw, visitor);
			}
			if ( _organ != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_organ.printObject(pw, visitor);
			}
			if ( _consultFrom != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_consultFrom.printObject(pw, visitor);
			}
			if ( _perform != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_perform.printObject(pw, visitor);
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
		if (qName.equals("mmlRp:information") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpinformation obj = new mmlRpinformation();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpinformation)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpinformation)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpinformation)builder.getElement()).setQName( getQName() );
			((mmlRpinformation)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlRpperformTime( atts.getValue(namespaceURI, "performTime") );
				((mmlRpinformation)builder.getElement()).setMmlRpperformTime( atts.getValue(namespaceURI, "performTime") );
				setMmlRpreportTime( atts.getValue(namespaceURI, "reportTime") );
				((mmlRpinformation)builder.getElement()).setMmlRpreportTime( atts.getValue(namespaceURI, "reportTime") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:information") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:ReportModule")) {
				((mmlRpReportModule)builder.getParent()).setInformation((mmlRpinformation)builder.getElement());
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
	public void setMmlRpperformTime(String __mmlRpperformTime) {
		this.__mmlRpperformTime = __mmlRpperformTime;
	}
	public String getMmlRpperformTime() {
		return __mmlRpperformTime;
	}
	public void setMmlRpreportTime(String __mmlRpreportTime) {
		this.__mmlRpreportTime = __mmlRpreportTime;
	}
	public String getMmlRpreportTime() {
		return __mmlRpreportTime;
	}

	public void setReportStatus(mmlRpreportStatus _reportStatus) {
		this._reportStatus = _reportStatus;
	}
	public mmlRpreportStatus getReportStatus() {
		return _reportStatus;
	}
	public void setTestClass(mmlRptestClass _testClass) {
		this._testClass = _testClass;
	}
	public mmlRptestClass getTestClass() {
		return _testClass;
	}
	public void setTestSubclass(mmlRptestSubclass _testSubclass) {
		this._testSubclass = _testSubclass;
	}
	public mmlRptestSubclass getTestSubclass() {
		return _testSubclass;
	}
	public void setOrgan(mmlRporgan _organ) {
		this._organ = _organ;
	}
	public mmlRporgan getOrgan() {
		return _organ;
	}
	public void setConsultFrom(mmlRpconsultFrom _consultFrom) {
		this._consultFrom = _consultFrom;
	}
	public mmlRpconsultFrom getConsultFrom() {
		return _consultFrom;
	}
	public void setPerform(mmlRpperform _perform) {
		this._perform = _perform;
	}
	public mmlRpperform getPerform() {
		return _perform;
	}
	
}