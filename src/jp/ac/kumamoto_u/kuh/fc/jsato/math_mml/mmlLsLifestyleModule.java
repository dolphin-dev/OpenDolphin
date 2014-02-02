/**
 *
 * mmlLsLifestyleModule.java
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
public class mmlLsLifestyleModule extends MMLObject {
	
	/* fields */
	private mmlLsoccupation _occupation = null;
	private mmlLstobacco _tobacco = null;
	private mmlLsalcohol _alcohol = null;
	private mmlLsother _other = null;
	
	public mmlLsLifestyleModule() {
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
			if ( _occupation != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_occupation.printObject(pw, visitor);
			}
			if ( _tobacco != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_tobacco.printObject(pw, visitor);
			}
			if ( _alcohol != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_alcohol.printObject(pw, visitor);
			}
			if ( _other != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_other.printObject(pw, visitor);
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
		if (qName.equals("mmlLs:LifestyleModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlLsLifestyleModule obj = new mmlLsLifestyleModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlLsLifestyleModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlLsLifestyleModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlLsLifestyleModule)builder.getElement()).setQName( getQName() );
			((mmlLsLifestyleModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlLs:LifestyleModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("content")) {
				((content)builder.getParent()).setLifestyleModule((mmlLsLifestyleModule)builder.getElement());
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
	public void setOccupation(mmlLsoccupation _occupation) {
		this._occupation = _occupation;
	}
	public mmlLsoccupation getOccupation() {
		return _occupation;
	}
	public void setTobacco(mmlLstobacco _tobacco) {
		this._tobacco = _tobacco;
	}
	public mmlLstobacco getTobacco() {
		return _tobacco;
	}
	public void setAlcohol(mmlLsalcohol _alcohol) {
		this._alcohol = _alcohol;
	}
	public mmlLsalcohol getAlcohol() {
		return _alcohol;
	}
	public void setOther(mmlLsother _other) {
		this._other = _other;
	}
	public mmlLsother getOther() {
		return _other;
	}
	
}