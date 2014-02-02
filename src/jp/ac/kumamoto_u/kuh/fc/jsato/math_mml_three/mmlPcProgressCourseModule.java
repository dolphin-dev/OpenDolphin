/**
 *
 * mmlPcProgressCourseModule.java
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
public class mmlPcProgressCourseModule extends MMLObject {
	
	/* fields */
	private mmlPcFreeExpression _FreeExpression = null;
	private mmlPcstructuredExpression _structuredExpression = null;
	
	public mmlPcProgressCourseModule() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _FreeExpression != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_FreeExpression.printObject(pw, visitor);
			}
			if ( _structuredExpression != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_structuredExpression.printObject(pw, visitor);
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
		if (qName.equals("mmlPc:ProgressCourseModule") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlPcProgressCourseModule obj = new mmlPcProgressCourseModule();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlPcProgressCourseModule)builder.getElement()).setNamespace( getNamespace() );
			((mmlPcProgressCourseModule)builder.getElement()).setLocalName( getLocalName() );
			((mmlPcProgressCourseModule)builder.getElement()).setQName( getQName() );
			((mmlPcProgressCourseModule)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlPc:ProgressCourseModule") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("local_markup")) {
				Vector v = ((local_markup)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlPcProgressCourseModule)builder.getElement() );
			}

			if (parentElement.getQName().equals("mml:content")) {
				((mmlcontent)builder.getParent()).set_ProgressCourseModule((mmlPcProgressCourseModule)builder.getElement());
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
	public void set_FreeExpression(mmlPcFreeExpression _FreeExpression) {
		this._FreeExpression = _FreeExpression;
	}
	public mmlPcFreeExpression get_FreeExpression() {
		return _FreeExpression;
	}
	public void set_structuredExpression(mmlPcstructuredExpression _structuredExpression) {
		this._structuredExpression = _structuredExpression;
	}
	public mmlPcstructuredExpression get_structuredExpression() {
		return _structuredExpression;
	}
	
}