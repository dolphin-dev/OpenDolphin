/**
 *
 * mmlSmtestResults.java
 * Created on 2002/7/30 10:0:34
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
public class mmlSmtestResults extends MMLObject {
	
	/* fields */
	private Vector _testResult = new Vector();
	
	public mmlSmtestResults() {
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
			if (this._testResult != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._testResult.size(); ++i ) {
					((mmlSmtestResult)this._testResult.elementAt(i)).printObject(pw, visitor);
				}
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
		if (qName.equals("mmlSm:testResults") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSmtestResults obj = new mmlSmtestResults();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSmtestResults)builder.getElement()).setNamespace( getNamespace() );
			((mmlSmtestResults)builder.getElement()).setLocalName( getLocalName() );
			((mmlSmtestResults)builder.getElement()).setQName( getQName() );
			((mmlSmtestResults)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSm:testResults") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:SummaryModule")) {
				((mmlSmSummaryModule)builder.getParent()).setTestResults((mmlSmtestResults)builder.getElement());
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
	public void setTestResult(Vector _testResult) {
		if (this._testResult != null) this._testResult.removeAllElements();
		// copy entire elements in the vector
		this._testResult = new Vector();
		for (int i = 0; i < _testResult.size(); ++i) {
			this._testResult.addElement( _testResult.elementAt(i) );
		}
	}
	public Vector getTestResult() {
		return _testResult;
	}
	
}