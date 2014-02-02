/**
 *
 * xhtmlfont.java
 * Created on 2003/1/4 2:29:56
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
public class xhtmlfont extends MMLObject {
	
	/* fields */
	private String __id = null;
	private String __class = null;
	private String __style = null;
	private String __title = null;
	private String __lang = null;
	private String __xmllang = null;
	private String __dir = null;
	private String __size = null;
	private String __color = null;
	private String __face = null;

	private Vector vt = new Vector();
	
	public xhtmlfont() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __id != null ) pw.print(" " + "id" +  "=" + "'" + __id + "'");
			if ( __class != null ) pw.print(" " + "class" +  "=" + "'" + __class + "'");
			if ( __style != null ) pw.print(" " + "style" +  "=" + "'" + __style + "'");
			if ( __title != null ) pw.print(" " + "title" +  "=" + "'" + __title + "'");
			if ( __lang != null ) pw.print(" " + "lang" +  "=" + "'" + __lang + "'");
			if ( __xmllang != null ) pw.print(" " + "xml:lang" +  "=" + "'" + __xmllang + "'");
			if ( __dir != null ) pw.print(" " + "dir" +  "=" + "'" + __dir + "'");
			if ( __size != null ) pw.print(" " + "size" +  "=" + "'" + __size + "'");
			if ( __color != null ) pw.print(" " + "color" +  "=" + "'" + __color + "'");
			if ( __face != null ) pw.print(" " + "face" +  "=" + "'" + __face + "'");

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
		if (qName.equals("xhtml:font") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			xhtmlfont obj = new xhtmlfont();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((xhtmlfont)builder.getElement()).setNamespace( getNamespace() );
			((xhtmlfont)builder.getElement()).setLocalName( getLocalName() );
			((xhtmlfont)builder.getElement()).setQName( getQName() );
			((xhtmlfont)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("id") ) {
						set__id( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__id( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("class") ) {
						set__class( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__class( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("style") ) {
						set__style( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__style( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("title") ) {
						set__title( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__title( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("lang") ) {
						set__lang( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__lang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("xml:lang") ) {
						set__xmllang( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__xmllang( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("dir") ) {
						set__dir( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__dir( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("size") ) {
						set__size( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__size( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("color") ) {
						set__color( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__color( atts.getValue(i) );
					}
					if ( ((String)atts.getQName(i)).equals("face") ) {
						set__face( atts.getValue(i) );
						((xhtmlfont)builder.getElement()).set__face( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("xhtml:font") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("xhtml:u")) {
				Vector v = ((xhtmlu)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("xhtml:i")) {
				Vector v = ((xhtmli)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("xhtml:font")) {
				Vector v = ((xhtmlfont)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("xhtml:b")) {
				Vector v = ((xhtmlb)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:testResult")) {
				Vector v = ((mmlSmtestResult)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:remarks")) {
				Vector v = ((mmlSmremarks)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:plan")) {
				Vector v = ((mmlSmplan)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:physicalExam")) {
				Vector v = ((mmlSmphysicalExam)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:patientProfile")) {
				Vector v = ((mmlSmpatientProfile)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:medication")) {
				Vector v = ((mmlSmmedication)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:history")) {
				Vector v = ((mmlSmhistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:dischargeFindings")) {
				Vector v = ((mmlSmdischargeFindings)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:clinicalRecord")) {
				Vector v = ((mmlSmclinicalRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:chiefComplaints")) {
				Vector v = ((mmlSmchiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSg:operativeNotes")) {
				Vector v = ((mmlSgoperativeNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testPurpose")) {
				Vector v = ((mmlRptestPurpose)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testNotes")) {
				Vector v = ((mmlRptestNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testDx")) {
				Vector v = ((mmlRptestDx)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:chiefComplaints")) {
				Vector v = ((mmlRpchiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:testResults")) {
				Vector v = ((mmlRetestResults)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:remarks")) {
				Vector v = ((mmlReremarks)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:referPurpose")) {
				Vector v = ((mmlRereferPurpose)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:presentIllness")) {
				Vector v = ((mmlRepresentIllness)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:pastHistory")) {
				Vector v = ((mmlRepastHistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:occupation")) {
				Vector v = ((mmlReoccupation)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:medication")) {
				Vector v = ((mmlRemedication)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:greeting")) {
				Vector v = ((mmlRegreeting)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:familyHistory")) {
				Vector v = ((mmlRefamilyHistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:clinicalDiagnosis")) {
				Vector v = ((mmlReclinicalDiagnosis)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:chiefComplaints")) {
				Vector v = ((mmlRechiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:txRecord")) {
				Vector v = ((mmlPctxRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:txOrder")) {
				Vector v = ((mmlPctxOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:testResult")) {
				Vector v = ((mmlPctestResult)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:testOrder")) {
				Vector v = ((mmlPctestOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:rxRecord")) {
				Vector v = ((mmlPcrxRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:rxOrder")) {
				Vector v = ((mmlPcrxOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:problem")) {
				Vector v = ((mmlPcproblem)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:planNotes")) {
				Vector v = ((mmlPcplanNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:objectiveNotes")) {
				Vector v = ((mmlPcobjectiveNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:interpretation")) {
				Vector v = ((mmlPcinterpretation)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:freeNotes")) {
				Vector v = ((mmlPcfreeNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:FreeExpression")) {
				Vector v = ((mmlPcFreeExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:eventExpression")) {
				Vector v = ((mmlPceventExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:assessmentItem")) {
				Vector v = ((mmlPcassessmentItem)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:tobacco")) {
				Vector v = ((mmlLstobacco)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:other")) {
				Vector v = ((mmlLsother)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:occupation")) {
				Vector v = ((mmlLsoccupation)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:alcohol")) {
				Vector v = ((mmlLsalcohol)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:presentIllnessNotes")) {
				Vector v = ((mmlFclpresentIllnessNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:memo")) {
				Vector v = ((mmlFclmemo)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:freeNotes")) {
				Vector v = ((mmlFclfreeNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:eventExpression")) {
				Vector v = ((mmlFcleventExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:chiefComplaints")) {
				Vector v = ((mmlFclchiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlBc:memo")) {
				Vector v = ((mmlBcmemo)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlBc:description")) {
				Vector v = ((mmlBcdescription)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlfont)builder.getElement() );
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
		if (builder.getCurrentElement().getQName().equals("xhtml:font")) {
			StringBuffer buffer = new StringBuffer(length);
			buffer.append(ch, start, length);
			vt.addElement(buffer.toString());
			((xhtmlfont)builder.getElement()).getVt().addElement(buffer.toString());
			printlnStatus(parentElement.getQName() + " " + this.getQName() + ":" + buffer.toString());
			return true;
		}
		return false;
	}
	
	
	/* setters and getters */
	public void set__id(String __id) {
		this.__id = __id;
	}
	public String get__id() {
		return __id;
	}
	public void set__class(String __class) {
		this.__class = __class;
	}
	public String get__class() {
		return __class;
	}
	public void set__style(String __style) {
		this.__style = __style;
	}
	public String get__style() {
		return __style;
	}
	public void set__title(String __title) {
		this.__title = __title;
	}
	public String get__title() {
		return __title;
	}
	public void set__lang(String __lang) {
		this.__lang = __lang;
	}
	public String get__lang() {
		return __lang;
	}
	public void set__xmllang(String __xmllang) {
		this.__xmllang = __xmllang;
	}
	public String get__xmllang() {
		return __xmllang;
	}
	public void set__dir(String __dir) {
		this.__dir = __dir;
	}
	public String get__dir() {
		return __dir;
	}
	public void set__size(String __size) {
		this.__size = __size;
	}
	public String get__size() {
		return __size;
	}
	public void set__color(String __color) {
		this.__color = __color;
	}
	public String get__color() {
		return __color;
	}
	public void set__face(String __face) {
		this.__face = __face;
	}
	public String get__face() {
		return __face;
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