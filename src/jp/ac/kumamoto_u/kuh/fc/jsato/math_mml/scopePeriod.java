/**
 *
 * scopePeriod.java
 * Created on 2002/7/30 10:0:25
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
public class scopePeriod extends MMLObject {
	
	/* fields */
	private String __start = null;
	private String __end = null;
	private String __hasOtherInfo = null;
	private String __isExtract = null;
	private String __extractPolicy = null;

	
	public scopePeriod() {
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

			if ( this.getLocalName().equals("Mml") ) {
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
		if (qName.equals("scopePeriod") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			scopePeriod obj = new scopePeriod();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((scopePeriod)builder.getElement()).setNamespace( getNamespace() );
			((scopePeriod)builder.getElement()).setLocalName( getLocalName() );
			((scopePeriod)builder.getElement()).setQName( getQName() );
			((scopePeriod)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setStart( atts.getValue(namespaceURI, "start") );
				((scopePeriod)builder.getElement()).setStart( atts.getValue(namespaceURI, "start") );
				setEnd( atts.getValue(namespaceURI, "end") );
				((scopePeriod)builder.getElement()).setEnd( atts.getValue(namespaceURI, "end") );
				setHasOtherInfo( atts.getValue(namespaceURI, "hasOtherInfo") );
				((scopePeriod)builder.getElement()).setHasOtherInfo( atts.getValue(namespaceURI, "hasOtherInfo") );
				setIsExtract( atts.getValue(namespaceURI, "isExtract") );
				((scopePeriod)builder.getElement()).setIsExtract( atts.getValue(namespaceURI, "isExtract") );
				setExtractPolicy( atts.getValue(namespaceURI, "extractPolicy") );
				((scopePeriod)builder.getElement()).setExtractPolicy( atts.getValue(namespaceURI, "extractPolicy") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("scopePeriod") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("MmlHeader")) {
				((MmlHeader)builder.getParent()).setScopePeriod((scopePeriod)builder.getElement());
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
	public void setStart(String __start) {
		this.__start = __start;
	}
	public String getStart() {
		return __start;
	}
	public void setEnd(String __end) {
		this.__end = __end;
	}
	public String getEnd() {
		return __end;
	}
	public void setHasOtherInfo(String __hasOtherInfo) {
		this.__hasOtherInfo = __hasOtherInfo;
	}
	public String getHasOtherInfo() {
		return __hasOtherInfo;
	}
	public void setIsExtract(String __isExtract) {
		this.__isExtract = __isExtract;
	}
	public String getIsExtract() {
		return __isExtract;
	}
	public void setExtractPolicy(String __extractPolicy) {
		this.__extractPolicy = __extractPolicy;
	}
	public String getExtractPolicy() {
		return __extractPolicy;
	}

	
}