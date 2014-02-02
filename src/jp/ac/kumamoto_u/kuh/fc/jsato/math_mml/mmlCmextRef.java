/**
 *
 * mmlCmextRef.java
 * Created on 2002/7/30 10:0:24
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
public class mmlCmextRef extends MMLObject {
	
	/* fields */
	private String __mmlCmcontentType = null;
	private String __mmlCmmedicalRole = null;
	private String __mmlCmtitle = null;
	private String __mmlCmhref = null;

	
	public mmlCmextRef() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlCmcontentType != null ) pw.print(" " + "mmlCm:contentType" +  "=" + "'" + __mmlCmcontentType + "'");
			if ( __mmlCmmedicalRole != null ) pw.print(" " + "mmlCm:medicalRole" +  "=" + "'" + __mmlCmmedicalRole + "'");
			if ( __mmlCmtitle != null ) pw.print(" " + "mmlCm:title" +  "=" + "'" + __mmlCmtitle + "'");
			if ( __mmlCmhref != null ) pw.print(" " + "mmlCm:href" +  "=" + "'" + __mmlCmhref + "'");

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
		if (qName.equals("mmlCm:extRef") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlCmextRef obj = new mmlCmextRef();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlCmextRef)builder.getElement()).setNamespace( getNamespace() );
			((mmlCmextRef)builder.getElement()).setLocalName( getLocalName() );
			((mmlCmextRef)builder.getElement()).setQName( getQName() );
			((mmlCmextRef)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlCmcontentType( atts.getValue(namespaceURI, "contentType") );
				((mmlCmextRef)builder.getElement()).setMmlCmcontentType( atts.getValue(namespaceURI, "contentType") );
				setMmlCmmedicalRole( atts.getValue(namespaceURI, "medicalRole") );
				((mmlCmextRef)builder.getElement()).setMmlCmmedicalRole( atts.getValue(namespaceURI, "medicalRole") );
				setMmlCmtitle( atts.getValue(namespaceURI, "title") );
				((mmlCmextRef)builder.getElement()).setMmlCmtitle( atts.getValue(namespaceURI, "title") );
				setMmlCmhref( atts.getValue(namespaceURI, "href") );
				((mmlCmextRef)builder.getElement()).setMmlCmhref( atts.getValue(namespaceURI, "href") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlCm:extRef") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlSm:testResult")) {
				Vector v = ((mmlSmtestResult)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:plan")) {
				Vector v = ((mmlSmplan)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:physicalExam")) {
				Vector v = ((mmlSmphysicalExam)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:medication")) {
				Vector v = ((mmlSmmedication)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:dischargeFindings")) {
				Vector v = ((mmlSmdischargeFindings)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSm:clinicalRecord")) {
				Vector v = ((mmlSmclinicalRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRp:testNotes")) {
				Vector v = ((mmlRptestNotes)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:testResults")) {
				Vector v = ((mmlRetestResults)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:remarks")) {
				Vector v = ((mmlReremarks)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:presentIllness")) {
				Vector v = ((mmlRepresentIllness)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:pastHistory")) {
				Vector v = ((mmlRepastHistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:medication")) {
				Vector v = ((mmlRemedication)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlRe:familyHistory")) {
				Vector v = ((mmlRefamilyHistory)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:txRecord")) {
				Vector v = ((mmlPctxRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:txOrder")) {
				Vector v = ((mmlPctxOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:testResult")) {
				Vector v = ((mmlPctestResult)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:testOrder")) {
				Vector v = ((mmlPctestOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:rxRecord")) {
				Vector v = ((mmlPcrxRecord)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:rxOrder")) {
				Vector v = ((mmlPcrxOrder)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlPc:FreeExpression")) {
				Vector v = ((mmlPcFreeExpression)builder.getParent()).getVt();
				if (v == null) printlnStatus("parent's vector is null!!!");
				v.addElement( (mmlCmextRef)builder.getElement() );
			}

			if (parentElement.getQName().equals("mmlSg:referenceInfo")) {
				Vector v = ((mmlSgreferenceInfo)builder.getParent()).getExtRef();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlPc:referenceInfo")) {
				Vector v = ((mmlPcreferenceInfo)builder.getParent()).getExtRef();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("mmlLb:referenceInfo")) {
				Vector v = ((mmlLbreferenceInfo)builder.getParent()).getExtRef();
				v.addElement(builder.getElement());
			}

			if (parentElement.getQName().equals("extRefs")) {
				Vector v = ((extRefs)builder.getParent()).getExtRef();
				v.addElement(builder.getElement());
			}

			
			printlnStatus(parentElement.getQName()+" /"+qName);

			if (false == parentElement.getQName().equals("extRefs")) {
				//external references in all contents are stored.
				if (builder.extRefInContent != null) {
					builder.extRefInContent.addElement(((mmlCmextRef)builder.getElement()));
				}
			}
			builder.restoreIndex();
			super.buildEnd(namespaceURI,localName,qName,builder);
			return true;
		}
		return false;
	}
	
	/* characters */
	
	
	/* setters and getters */
	public void setMmlCmcontentType(String __mmlCmcontentType) {
		this.__mmlCmcontentType = __mmlCmcontentType;
	}
	public String getMmlCmcontentType() {
		return __mmlCmcontentType;
	}
	public void setMmlCmmedicalRole(String __mmlCmmedicalRole) {
		this.__mmlCmmedicalRole = __mmlCmmedicalRole;
	}
	public String getMmlCmmedicalRole() {
		return __mmlCmmedicalRole;
	}
	public void setMmlCmtitle(String __mmlCmtitle) {
		this.__mmlCmtitle = __mmlCmtitle;
	}
	public String getMmlCmtitle() {
		return __mmlCmtitle;
	}
	public void setMmlCmhref(String __mmlCmhref) {
		this.__mmlCmhref = __mmlCmhref;
	}
	public String getMmlCmhref() {
		return __mmlCmhref;
	}

	
}