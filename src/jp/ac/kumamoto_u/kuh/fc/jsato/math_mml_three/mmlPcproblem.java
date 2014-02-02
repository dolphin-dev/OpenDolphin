/**
 *
 * mmlPcproblem.java
 * Created on 2003/1/4 2:30:6
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
public class mmlPcproblem extends MMLObject {
	
	/* fields */
	private String __mmlPcdxUid = null;

	private Vector vt = new Vector();
	
	public mmlPcproblem() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlPcdxUid != null ) pw.print(" " + "mmlPc:dxUid" +  "=" + "'" + __mmlPcdxUid + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if (vt != null) {
				pw.print( "\n" );
				for (int i = 0; i < vt.size(); ++i) {
					visitor.setIgnoreTab( false );
					pw.print("\n");
					if (vt.elementAt(i).getClass().getName().equals("java.lang.String")) {
						//#PCDATA
						if ( ((String)vt.elementAt(i)).equals("") == false ) {
							pw.print( visitor.getTabPadding() + vt.elementAt(i) );
						}
					} else {
						//MMLObject
						((MMLObject)vt.elementAt(i)).printObject(pw, visitor);
					}
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
		if (qName.equals("mmlPc:problem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcproblem obj = new mmlPcproblem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcproblem)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcproblem)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcproblem)builder.getElement()).setQName( getQName() );
			((mmlPcproblem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlPc:dxUid") ) {
						set__mmlPcdxUid( atts.getValue(i) );
						((mmlPcproblem)builder.getElement()).set__mmlPcdxUid( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:problem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlPc:problemItem")) {
				((mmlPcproblemItem)builder.getParent()).set_problem((mmlPcproblem)builder.getElement());
			}

			
			printlnStatus(parentElement.getQName() + " /" + qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
		if (builder.getCurrentElement().getQName().equals("mmlPc:problem")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((mmlPcproblem)builder.getElement()).getVt().addElement(buffer.toString());
			printlnStatus(parentElement.getQName() + " " + this.getQName() + ":" + buffer.toString());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlPcdxUid(String __mmlPcdxUid) {
		this.__mmlPcdxUid = __mmlPcdxUid;
	}
	public String get__mmlPcdxUid() {
		return __mmlPcdxUid;
	}

	public void setVt(Vector vt) {
		// copy entire elements in the vector
		if (this.vt != null) this.vt.removeAllElements();
		this.vt = new Vector();
		for (int i = 0; i < vt.size(); ++i) {
			this.vt.addElement( vt.elementAt(i) );
		}
	}
	public Vector getVt() {
		return vt;
	}
	
}