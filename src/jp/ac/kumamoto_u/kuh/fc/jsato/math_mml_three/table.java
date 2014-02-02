/**
 *
 * table.java
 * Created on 2003/1/4 2:30:32
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
public class table extends MMLObject {
	
	/* fields */
	private String __ID = null;
	private String __originator = null;
	private String __confidentiality = null;
	private String __xmllang = null;
	private String __summary = null;
	private String __width = null;
	private String __border = null;
	private String __frame = null;
	private String __rules = null;
	private String __cellspacing = null;
	private String __cellpadding = null;

	private caption _caption = null;
	private Vector _col = new Vector();
	private Vector _colgroup = new Vector();
	private thead _thead = null;
	private tfoot _tfoot = null;
	private Vector _tbody = new Vector();
	private Vector _tr = new Vector();
	
	public table() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __ID != null ) pw.print(" " + "ID" +  "=" + "'" + __ID + "'");
			if ( __originator != null ) pw.print(" " + "originator" +  "=" + "'" + __originator + "'");
			if ( __confidentiality != null ) pw.print(" " + "confidentiality" +  "=" + "'" + __confidentiality + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");
			if ( __summary != null ) pw.print(" " + "summary" +  "=" + "'" + __summary + "'");
			if ( __width != null ) pw.print(" " + "width" +  "=" + "'" + __width + "'");
			if ( __border != null ) pw.print(" " + "border" +  "=" + "'" + __border + "'");
			if ( __frame != null ) pw.print(" " + "frame" +  "=" + "'" + __frame + "'");
			if ( __rules != null ) pw.print(" " + "rules" +  "=" + "'" + __rules + "'");
			if ( __cellspacing != null ) pw.print(" " + "cellspacing" +  "=" + "'" + __cellspacing + "'");
			if ( __cellpadding != null ) pw.print(" " + "cellpadding" +  "=" + "'" + __cellpadding + "'");

			if ( this.getLocalName().equals("levelone") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _caption != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_caption.printObject(pw, visitor);
			}
			if (this._col != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._col.size(); ++i ) {
					((col)this._col.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._colgroup != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._colgroup.size(); ++i ) {
					((colgroup)this._colgroup.elementAt(i)).printObject(pw, visitor);
				}
			}
			if ( _thead != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_thead.printObject(pw, visitor);
			}
			if ( _tfoot != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_tfoot.printObject(pw, visitor);
			}
			if (this._tbody != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._tbody.size(); ++i ) {
					((tbody)this._tbody.elementAt(i)).printObject(pw, visitor);
				}
			}
			if (this._tr != null) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				// print each element in the vector assumming that it doesn't contain String object...
				for (int i = 0; i < this._tr.size(); ++i ) {
					((tr)this._tr.elementAt(i)).printObject(pw, visitor);
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
		if (qName.equals("table") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			table obj = new table();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((table)builder.getElement()).setNamespace( getNamespace() );
			((table)builder.getElement()).setLocalName( getLocalName() );
			((table)builder.getElement()).setQName( getQName() );
			((table)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("ID") ) {
						set__ID( atts.getValue(i) );
						((table)builder.getElement()).set__ID( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("originator") ) {
						set__originator( atts.getValue(i) );
						((table)builder.getElement()).set__originator( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("confidentiality") ) {
						set__confidentiality( atts.getValue(i) );
						((table)builder.getElement()).set__confidentiality( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((table)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("summary") ) {
						set__summary( atts.getValue(i) );
						((table)builder.getElement()).set__summary( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("width") ) {
						set__width( atts.getValue(i) );
						((table)builder.getElement()).set__width( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("border") ) {
						set__border( atts.getValue(i) );
						((table)builder.getElement()).set__border( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("frame") ) {
						set__frame( atts.getValue(i) );
						((table)builder.getElement()).set__frame( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("rules") ) {
						set__rules( atts.getValue(i) );
						((table)builder.getElement()).set__rules( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("cellspacing") ) {
						set__cellspacing( atts.getValue(i) );
						((table)builder.getElement()).set__cellspacing( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("cellpadding") ) {
						set__cellpadding( atts.getValue(i) );
						((table)builder.getElement()).set__cellpadding( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("table") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("section")) {
				Vector v = ((section)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (table)builder.getElement() );
			}

			if (parentElement.getQName().equals("item")) {
				Vector v = ((item)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (table)builder.getElement() );
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
	public void set__ID(String __ID) {
		this.__ID = __ID;
	}
	public String get__ID() {
		return __ID;
	}
	public void set__originator(String __originator) {
		this.__originator = __originator;
	}
	public String get__originator() {
		return __originator;
	}
	public void set__confidentiality(String __confidentiality) {
		this.__confidentiality = __confidentiality;
	}
	public String get__confidentiality() {
		return __confidentiality;
	}
	public void set__xmllang(String __xmllang) {
		this.__xmllang = __xmllang;
	}
	public String get__xmllang() {
		return __xmllang;
	}
	public void set__summary(String __summary) {
		this.__summary = __summary;
	}
	public String get__summary() {
		return __summary;
	}
	public void set__width(String __width) {
		this.__width = __width;
	}
	public String get__width() {
		return __width;
	}
	public void set__border(String __border) {
		this.__border = __border;
	}
	public String get__border() {
		return __border;
	}
	public void set__frame(String __frame) {
		this.__frame = __frame;
	}
	public String get__frame() {
		return __frame;
	}
	public void set__rules(String __rules) {
		this.__rules = __rules;
	}
	public String get__rules() {
		return __rules;
	}
	public void set__cellspacing(String __cellspacing) {
		this.__cellspacing = __cellspacing;
	}
	public String get__cellspacing() {
		return __cellspacing;
	}
	public void set__cellpadding(String __cellpadding) {
		this.__cellpadding = __cellpadding;
	}
	public String get__cellpadding() {
		return __cellpadding;
	}

	public void set_caption(caption _caption) {
		this._caption = _caption;
	}
	public caption get_caption() {
		return _caption;
	}
	public void set_col(Vector _col) {
		if (this._col != null) this._col.removeAllElements();
		// copy entire elements in the vector
		this._col = new Vector();
		for (int i = 0; i < _col.size(); ++i) {
			this._col.addElement( _col.elementAt(i) );
		}
	}
	public Vector get_col() {
		return _col;
	}
	public void set_colgroup(Vector _colgroup) {
		if (this._colgroup != null) this._colgroup.removeAllElements();
		// copy entire elements in the vector
		this._colgroup = new Vector();
		for (int i = 0; i < _colgroup.size(); ++i) {
			this._colgroup.addElement( _colgroup.elementAt(i) );
		}
	}
	public Vector get_colgroup() {
		return _colgroup;
	}
	public void set_thead(thead _thead) {
		this._thead = _thead;
	}
	public thead get_thead() {
		return _thead;
	}
	public void set_tfoot(tfoot _tfoot) {
		this._tfoot = _tfoot;
	}
	public tfoot get_tfoot() {
		return _tfoot;
	}
	public void set_tbody(Vector _tbody) {
		if (this._tbody != null) this._tbody.removeAllElements();
		// copy entire elements in the vector
		this._tbody = new Vector();
		for (int i = 0; i < _tbody.size(); ++i) {
			this._tbody.addElement( _tbody.elementAt(i) );
		}
	}
	public Vector get_tbody() {
		return _tbody;
	}
	public void set_tr(Vector _tr) {
		if (this._tr != null) this._tr.removeAllElements();
		// copy entire elements in the vector
		this._tr = new Vector();
		for (int i = 0; i < _tr.size(); ++i) {
			this._tr.addElement( _tr.elementAt(i) );
		}
	}
	public Vector get_tr() {
		return _tr;
	}
	
}