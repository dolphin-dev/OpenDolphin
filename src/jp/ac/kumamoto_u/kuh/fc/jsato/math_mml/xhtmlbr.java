/**
 *
 * xhtmlbr.java
 * Created on 2002/7/30 10:0:23
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
public class xhtmlbr extends MMLObject {
	
	/* fields */
	private String __xhtmlid = null;
	private String __xhtmlclass = null;
	private String __xhtmlstyle = null;
	private String __xhtmltitle = null;
	private String __xhtmlclear = null;

	
	public xhtmlbr() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __xhtmlid != null ) pw.print(" " + "xhtml:id" +  "=" + "'" + __xhtmlid + "'");
			if ( __xhtmlclass != null ) pw.print(" " + "xhtml:class" +  "=" + "'" + __xhtmlclass + "'");
			if ( __xhtmlstyle != null ) pw.print(" " + "xhtml:style" +  "=" + "'" + __xhtmlstyle + "'");
			if ( __xhtmltitle != null ) pw.print(" " + "xhtml:title" +  "=" + "'" + __xhtmltitle + "'");
			if ( __xhtmlclear != null ) pw.print(" " + "xhtml:clear" +  "=" + "'" + __xhtmlclear + "'");

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
		if (qName.equals("xhtml:br") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			xhtmlbr obj = new xhtmlbr();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((xhtmlbr)builder.getElement()).setNamespace( getNamespace() );
			((xhtmlbr)builder.getElement()).setLocalName( getLocalName() );
			((xhtmlbr)builder.getElement()).setQName( getQName() );
			((xhtmlbr)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setXhtmlid( atts.getValue(namespaceURI, "id") );
				((xhtmlbr)builder.getElement()).setXhtmlid( atts.getValue(namespaceURI, "id") );
				setXhtmlclass( atts.getValue(namespaceURI, "class") );
				((xhtmlbr)builder.getElement()).setXhtmlclass( atts.getValue(namespaceURI, "class") );
				setXhtmlstyle( atts.getValue(namespaceURI, "style") );
				((xhtmlbr)builder.getElement()).setXhtmlstyle( atts.getValue(namespaceURI, "style") );
				setXhtmltitle( atts.getValue(namespaceURI, "title") );
				((xhtmlbr)builder.getElement()).setXhtmltitle( atts.getValue(namespaceURI, "title") );
				setXhtmlclear( atts.getValue(namespaceURI, "clear") );
				((xhtmlbr)builder.getElement()).setXhtmlclear( atts.getValue(namespaceURI, "clear") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("xhtml:br") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("xhtml:u")) {
				Vector v = ((xhtmlu)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("xhtml:i")) {
				Vector v = ((xhtmli)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("xhtml:font")) {
				Vector v = ((xhtmlfont)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("xhtml:b")) {
				Vector v = ((xhtmlb)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:testResult")) {
				Vector v = ((mmlSmtestResult)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:remarks")) {
				Vector v = ((mmlSmremarks)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:plan")) {
				Vector v = ((mmlSmplan)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:physicalExam")) {
				Vector v = ((mmlSmphysicalExam)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:patientProfile")) {
				Vector v = ((mmlSmpatientProfile)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:medication")) {
				Vector v = ((mmlSmmedication)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:history")) {
				Vector v = ((mmlSmhistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:dischargeFindings")) {
				Vector v = ((mmlSmdischargeFindings)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:clinicalRecord")) {
				Vector v = ((mmlSmclinicalRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:chiefComplaints")) {
				Vector v = ((mmlSmchiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSg:operativeNotes")) {
				Vector v = ((mmlSgoperativeNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testPurpose")) {
				Vector v = ((mmlRptestPurpose)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testNotes")) {
				Vector v = ((mmlRptestNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testDx")) {
				Vector v = ((mmlRptestDx)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:chiefComplaints")) {
				Vector v = ((mmlRpchiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:testResults")) {
				Vector v = ((mmlRetestResults)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:remarks")) {
				Vector v = ((mmlReremarks)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:referPurpose")) {
				Vector v = ((mmlRereferPurpose)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:pastHistory")) {
				Vector v = ((mmlRepastHistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:occupation")) {
				Vector v = ((mmlReoccupation)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:medication")) {
				Vector v = ((mmlRemedication)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:greeting")) {
				Vector v = ((mmlRegreeting)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:familyHistory")) {
				Vector v = ((mmlRefamilyHistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:clinicalDiagnosis")) {
				Vector v = ((mmlReclinicalDiagnosis)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:chiefComplaints")) {
				Vector v = ((mmlRechiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:txRecord")) {
				Vector v = ((mmlPctxRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:txOrder")) {
				Vector v = ((mmlPctxOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:testResult")) {
				Vector v = ((mmlPctestResult)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:testOrder")) {
				Vector v = ((mmlPctestOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:rxRecord")) {
				Vector v = ((mmlPcrxRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:rxOrder")) {
				Vector v = ((mmlPcrxOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:problem")) {
				Vector v = ((mmlPcproblem)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:planNotes")) {
				Vector v = ((mmlPcplanNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:objectiveNotes")) {
				Vector v = ((mmlPcobjectiveNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:interpretation")) {
				Vector v = ((mmlPcinterpretation)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:freeNotes")) {
				Vector v = ((mmlPcfreeNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:FreeExpression")) {
				Vector v = ((mmlPcFreeExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:eventExpression")) {
				Vector v = ((mmlPceventExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:assessmentItem")) {
				Vector v = ((mmlPcassessmentItem)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:tobacco")) {
				Vector v = ((mmlLstobacco)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:other")) {
				Vector v = ((mmlLsother)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:occupation")) {
				Vector v = ((mmlLsoccupation)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlLs:alcohol")) {
				Vector v = ((mmlLsalcohol)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:presentIllnessNotes")) {
				Vector v = ((mmlFclpresentIllnessNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:memo")) {
				Vector v = ((mmlFclmemo)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:freeNotes")) {
				Vector v = ((mmlFclfreeNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:eventExpression")) {
				Vector v = ((mmlFcleventExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlFcl:chiefComplaints")) {
				Vector v = ((mmlFclchiefComplaints)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlBc:memo")) {
				Vector v = ((mmlBcmemo)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlBc:description")) {
				Vector v = ((mmlBcdescription)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (xhtmlbr)builder.getElement() );
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
	public void setXhtmlid(String __xhtmlid) {
		this.__xhtmlid = __xhtmlid;
	}
	public String getXhtmlid() {
		return __xhtmlid;
	}
	public void setXhtmlclass(String __xhtmlclass) {
		this.__xhtmlclass = __xhtmlclass;
	}
	public String getXhtmlclass() {
		return __xhtmlclass;
	}
	public void setXhtmlstyle(String __xhtmlstyle) {
		this.__xhtmlstyle = __xhtmlstyle;
	}
	public String getXhtmlstyle() {
		return __xhtmlstyle;
	}
	public void setXhtmltitle(String __xhtmltitle) {
		this.__xhtmltitle = __xhtmltitle;
	}
	public String getXhtmltitle() {
		return __xhtmltitle;
	}
	public void setXhtmlclear(String __xhtmlclear) {
		this.__xhtmlclear = __xhtmlclear;
	}
	public String getXhtmlclear() {
		return __xhtmlclear;
	}

	
}