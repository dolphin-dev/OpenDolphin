/**
 *
 * mmlRpinformation.java
 * Created on 2003/1/4 2:30:14
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRp:performTime") ) {
						set__mmlRpperformTime( atts.getValue(i) );
						((mmlRpinformation)builder.getElement()).set__mmlRpperformTime( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:reportTime") ) {
						set__mmlRpreportTime( atts.getValue(i) );
						((mmlRpinformation)builder.getElement()).set__mmlRpreportTime( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:information") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:ReportModule")) {
				((mmlRpReportModule)builder.getParent()).set_information((mmlRpinformation)builder.getElement());
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
	public void set__mmlRpperformTime(String __mmlRpperformTime) {
		this.__mmlRpperformTime = __mmlRpperformTime;
	}
	public String get__mmlRpperformTime() {
		return __mmlRpperformTime;
	}
	public void set__mmlRpreportTime(String __mmlRpreportTime) {
		this.__mmlRpreportTime = __mmlRpreportTime;
	}
	public String get__mmlRpreportTime() {
		return __mmlRpreportTime;
	}

	public void set_reportStatus(mmlRpreportStatus _reportStatus) {
		this._reportStatus = _reportStatus;
	}
	public mmlRpreportStatus get_reportStatus() {
		return _reportStatus;
	}
	public void set_testClass(mmlRptestClass _testClass) {
		this._testClass = _testClass;
	}
	public mmlRptestClass get_testClass() {
		return _testClass;
	}
	public void set_testSubclass(mmlRptestSubclass _testSubclass) {
		this._testSubclass = _testSubclass;
	}
	public mmlRptestSubclass get_testSubclass() {
		return _testSubclass;
	}
	public void set_organ(mmlRporgan _organ) {
		this._organ = _organ;
	}
	public mmlRporgan get_organ() {
		return _organ;
	}
	public void set_consultFrom(mmlRpconsultFrom _consultFrom) {
		this._consultFrom = _consultFrom;
	}
	public mmlRpconsultFrom get_consultFrom() {
		return _consultFrom;
	}
	public void set_perform(mmlRpperform _perform) {
		this._perform = _perform;
	}
	public mmlRpperform get_perform() {
		return _perform;
	}
	
}