/**
 *
 * mmlRpreportBody.java
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
public class mmlRpreportBody extends MMLObject {
	
	/* fields */
	private mmlRpchiefComplaints _chiefComplaints = null;
	private mmlRptestPurpose _testPurpose = null;
	private mmlRptestDx _testDx = null;
	private mmlRptestNotes _testNotes = null;
	private Vector _testMemo = new Vector();
	private mmlRptestMemoF _testMemoF = null;
	
	public mmlRpreportBody() {
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
			if ( _chiefComplaints != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_chiefComplaints.printObject(pw, visitor);
			}
			if ( _testPurpose != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testPurpose.printObject(pw, visitor);
			}
			if ( _testDx != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testDx.printObject(pw, visitor);
			}
			if ( _testNotes != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testNotes.printObject(pw, visitor);
			}
			if (this._testMemo != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._testMemo.size(); ++i ) {
					((mmlRptestMemo)this._testMemo.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _testMemoF != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_testMemoF.printObject(pw, visitor);
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
		if (qName.equals("mmlRp:reportBody") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpreportBody obj = new mmlRpreportBody();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpreportBody)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpreportBody)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpreportBody)builder.getElement()).setQName( getQName() );
			((mmlRpreportBody)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:reportBody") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:ReportModule")) {
				((mmlRpReportModule)builder.getParent()).setReportBody((mmlRpreportBody)builder.getElement());
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
	public void setChiefComplaints(mmlRpchiefComplaints _chiefComplaints) {
		this._chiefComplaints = _chiefComplaints;
	}
	public mmlRpchiefComplaints getChiefComplaints() {
		return _chiefComplaints;
	}
	public void setTestPurpose(mmlRptestPurpose _testPurpose) {
		this._testPurpose = _testPurpose;
	}
	public mmlRptestPurpose getTestPurpose() {
		return _testPurpose;
	}
	public void setTestDx(mmlRptestDx _testDx) {
		this._testDx = _testDx;
	}
	public mmlRptestDx getTestDx() {
		return _testDx;
	}
	public void setTestNotes(mmlRptestNotes _testNotes) {
		this._testNotes = _testNotes;
	}
	public mmlRptestNotes getTestNotes() {
		return _testNotes;
	}
	public void setTestMemo(Vector _testMemo) {
		if (this._testMemo != null) this._testMemo.removeAllElements();
		// copy entire elements in the vector
		this._testMemo = new Vector();
		for (int i = 0; i < _testMemo.size(); ++i) {
			this._testMemo.addElement( _testMemo.elementAt(i) );
		}
	}
	public Vector getTestMemo() {
		return _testMemo;
	}
	public void setTestMemoF(mmlRptestMemoF _testMemoF) {
		this._testMemoF = _testMemoF;
	}
	public mmlRptestMemoF getTestMemoF() {
		return _testMemoF;
	}
	
}