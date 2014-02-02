/**
 *
 * mmlSgsurgicalDiagnosis.java
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
public class mmlSgsurgicalDiagnosis extends MMLObject {
	
	/* fields */
	private Vector _RegisteredDiagnosisModule = new Vector();
	
	public mmlSgsurgicalDiagnosis() {
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
			if (this._RegisteredDiagnosisModule != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._RegisteredDiagnosisModule.size(); ++i ) {
					((mmlRdRegisteredDiagnosisModule)this._RegisteredDiagnosisModule.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("mmlSg:surgicalDiagnosis") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlSgsurgicalDiagnosis obj = new mmlSgsurgicalDiagnosis();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlSgsurgicalDiagnosis)builder.getElement()).setNamespace( getNamespace() );
			((mmlSgsurgicalDiagnosis)builder.getElement()).setLocalName( getLocalName() );
			((mmlSgsurgicalDiagnosis)builder.getElement()).setQName( getQName() );
			((mmlSgsurgicalDiagnosis)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlSg:surgicalDiagnosis") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSg:surgeryItem")) {
				((mmlSgsurgeryItem)builder.getParent()).setSurgicalDiagnosis((mmlSgsurgicalDiagnosis)builder.getElement());
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
	public void setRegisteredDiagnosisModule(Vector _RegisteredDiagnosisModule) {
		if (this._RegisteredDiagnosisModule != null) this._RegisteredDiagnosisModule.removeAllElements();
		// copy entire elements in the vector
		this._RegisteredDiagnosisModule = new Vector();
		for (int i = 0; i < _RegisteredDiagnosisModule.size(); ++i) {
			this._RegisteredDiagnosisModule.addElement( _RegisteredDiagnosisModule.elementAt(i) );
		}
	}
	public Vector getRegisteredDiagnosisModule() {
		return _RegisteredDiagnosisModule;
	}
	
}