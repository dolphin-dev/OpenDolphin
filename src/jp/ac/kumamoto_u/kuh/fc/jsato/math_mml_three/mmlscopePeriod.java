/**
 *
 * mmlscopePeriod.java
 * Created on 2003/1/4 2:29:55
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
public class mmlscopePeriod extends MMLObject {
	
	/* fields */
	private String __start = null;
	private String __end = null;
	private String __hasOtherInfo = null;
	private String __isExtract = null;
	private String __extractPolicy = null;

	
	public mmlscopePeriod() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __start != null ) pw.print(" " + "start" +  "=" + "'" + __start + "'");
			if ( __end != null ) pw.print(" " + "end" +  "=" + "'" + __end + "'");
			if ( __hasOtherInfo != null ) pw.print(" " + "hasOtherInfo" +  "=" + "'" + __hasOtherInfo + "'");
			if ( __isExtract != null ) pw.print(" " + "isExtract" +  "=" + "'" + __isExtract + "'");
			if ( __extractPolicy != null ) pw.print(" " + "extractPolicy" +  "=" + "'" + __extractPolicy + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */

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
		if (qName.equals("mml:scopePeriod") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlscopePeriod obj = new mmlscopePeriod();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlscopePeriod)builder.getElement()).setNamespace( getNamespace() );
			((mmlscopePeriod)builder.getElement()).setLocalName( getLocalName() );
			((mmlscopePeriod)builder.getElement()).setQName( getQName() );
			((mmlscopePeriod)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("start") ) {
						set__start( atts.getValue(i) );
						((mmlscopePeriod)builder.getElement()).set__start( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("end") ) {
						set__end( atts.getValue(i) );
						((mmlscopePeriod)builder.getElement()).set__end( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("hasOtherInfo") ) {
						set__hasOtherInfo( atts.getValue(i) );
						((mmlscopePeriod)builder.getElement()).set__hasOtherInfo( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("isExtract") ) {
						set__isExtract( atts.getValue(i) );
						((mmlscopePeriod)builder.getElement()).set__isExtract( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("extractPolicy") ) {
						set__extractPolicy( atts.getValue(i) );
						((mmlscopePeriod)builder.getElement()).set__extractPolicy( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mml:scopePeriod") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mml:MmlHeader")) {
				((mmlMmlHeader)builder.getParent()).set_scopePeriod((mmlscopePeriod)builder.getElement());
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
	public void set__start(String __start) {
		this.__start = __start;
	}
	public String get__start() {
		return __start;
	}
	public void set__end(String __end) {
		this.__end = __end;
	}
	public String get__end() {
		return __end;
	}
	public void set__hasOtherInfo(String __hasOtherInfo) {
		this.__hasOtherInfo = __hasOtherInfo;
	}
	public String get__hasOtherInfo() {
		return __hasOtherInfo;
	}
	public void set__isExtract(String __isExtract) {
		this.__isExtract = __isExtract;
	}
	public String get__isExtract() {
		return __isExtract;
	}
	public void set__extractPolicy(String __extractPolicy) {
		this.__extractPolicy = __extractPolicy;
	}
	public String get__extractPolicy() {
		return __extractPolicy;
	}

	
}