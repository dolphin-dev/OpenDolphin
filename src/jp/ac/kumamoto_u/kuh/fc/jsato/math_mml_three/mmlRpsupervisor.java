/**
 *
 * mmlRpsupervisor.java
 * Created on 2003/1/4 2:30:23
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
public class mmlRpsupervisor extends MMLObject {
	
	/* fields */
	private String __mmlRpsupervisorCode = null;
	private String __mmlRpsupervisorCodeId = null;

	private String text = null;
	
	public mmlRpsupervisor() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlRpsupervisorCode != null ) pw.print(" " + "mmlRp:supervisorCode" +  "=" + "'" + __mmlRpsupervisorCode + "'");
			if ( __mmlRpsupervisorCodeId != null ) pw.print(" " + "mmlRp:supervisorCodeId" +  "=" + "'" + __mmlRpsupervisorCodeId + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			// this element need not to print tab padding before the closing tag.
			visitor.setIgnoreTab( true );
			if (text != null) {
				if ( this.getText().equals("") == false ) pw.print( this.getText() );
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
		if (qName.equals("mmlRp:supervisor") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlRpsupervisor obj = new mmlRpsupervisor();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlRpsupervisor)builder.getElement()).setNamespace( getNamespace() );
			((mmlRpsupervisor)builder.getElement()).setLocalName( getLocalName() );
			((mmlRpsupervisor)builder.getElement()).setQName( getQName() );
			((mmlRpsupervisor)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlRp:supervisorCode") ) {
						set__mmlRpsupervisorCode( atts.getValue(i) );
						((mmlRpsupervisor)builder.getElement()).set__mmlRpsupervisorCode( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("mmlRp:supervisorCodeId") ) {
						set__mmlRpsupervisorCodeId( atts.getValue(i) );
						((mmlRpsupervisor)builder.getElement()).set__mmlRpsupervisorCodeId( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlRp:supervisor") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlRp:perform")) {
				((mmlRpperform)builder.getParent()).set_supervisor((mmlRpsupervisor)builder.getElement());
			}

			
			printlnStatus(parentElement.getQName()+" /"+qName);


			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
		if (builder.getCurrentElement().getQName().equals("mmlRp:supervisor")) {
			StringBuffer buffer=new StringBuffer(length);
			buffer.append(ch, start, length);
			setText(buffer.toString());
			((mmlRpsupervisor)builder.getElement()).setText( getText() );
			printlnStatus(parentElement.getQName()+" "+this.getQName()+":"+this.getText());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__mmlRpsupervisorCode(String __mmlRpsupervisorCode) {
		this.__mmlRpsupervisorCode = __mmlRpsupervisorCode;
	}
	public String get__mmlRpsupervisorCode() {
		return __mmlRpsupervisorCode;
	}
	public void set__mmlRpsupervisorCodeId(String __mmlRpsupervisorCodeId) {
		this.__mmlRpsupervisorCodeId = __mmlRpsupervisorCodeId;
	}
	public String get__mmlRpsupervisorCodeId() {
		return __mmlRpsupervisorCodeId;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
}