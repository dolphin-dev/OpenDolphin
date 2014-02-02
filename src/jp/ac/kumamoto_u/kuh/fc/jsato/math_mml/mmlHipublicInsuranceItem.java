/**
 *
 * mmlHipublicInsuranceItem.java
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
public class mmlHipublicInsuranceItem extends MMLObject {
	
	/* fields */
	private String __mmlHipriority = null;

	private mmlHiproviderName _providerName = null;
	private mmlHiprovider _provider = null;
	private mmlHirecipient _recipient = null;
	private mmlHistartDate _startDate = null;
	private mmlHiexpiredDate _expiredDate = null;
	private mmlHipaymentRatio _paymentRatio = null;
	
	public mmlHipublicInsuranceItem() {
	}
	
	
	/* print */
	public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
		if ( this.getQName() != null ) {
			visitor.goDown();// adjust tab
			pw.print( visitor.getTabPadding() + "<" + this.getQName() );
			/* print atts */
			if ( __mmlHipriority != null ) pw.print(" " + "mmlHi:priority" +  "=" + "'" + __mmlHipriority + "'");

			if ( this.getLocalName().equals("Mml") ) {
				visitor.printNamespaces(pw);
			}
			pw.print( ">" );
			/* print content */
			if ( _providerName != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_providerName.printObject(pw, visitor);
			}
			if ( _provider != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_provider.printObject(pw, visitor);
			}
			if ( _recipient != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_recipient.printObject(pw, visitor);
			}
			if ( _startDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_startDate.printObject(pw, visitor);
			}
			if ( _expiredDate != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_expiredDate.printObject(pw, visitor);
			}
			if ( _paymentRatio != null ) {
				visitor.setIgnoreTab( false );
				pw.print( "\n" );
				_paymentRatio.printObject(pw, visitor);
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
		if (qName.equals("mmlHi:publicInsuranceItem") == true) {
			super.buildStart(namespaceURI,localName,qName,atts,builder);
			printlnStatus(parentElement.getQName() + " " + qName);
			
			/* create tree node */
			mmlHipublicInsuranceItem obj = new mmlHipublicInsuranceItem();
			builder.getMmlTree().addElement( obj );
			obj.setParentIndex( builder.mmlTreeIndex );
			builder.adjustIndex();
			((mmlHipublicInsuranceItem)builder.getElement()).setNamespace( getNamespace() );
			((mmlHipublicInsuranceItem)builder.getElement()).setLocalName( getLocalName() );
			((mmlHipublicInsuranceItem)builder.getElement()).setQName( getQName() );
			((mmlHipublicInsuranceItem)builder.getElement()).setAtts( getAtts() );/* :-) */
			/* atts */
			if (atts != null) {
				setMmlHipriority( atts.getValue(namespaceURI, "priority") );
				((mmlHipublicInsuranceItem)builder.getElement()).setMmlHipriority( atts.getValue(namespaceURI, "priority") );
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:publicInsuranceItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:publicInsurance")) {
				Vector v = ((mmlHipublicInsurance)builder.getParent()).getPublicInsuranceItem();
				v.addElement(builder.getElement());
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
	public void setMmlHipriority(String __mmlHipriority) {
		this.__mmlHipriority = __mmlHipriority;
	}
	public String getMmlHipriority() {
		return __mmlHipriority;
	}

	public void setProviderName(mmlHiproviderName _providerName) {
		this._providerName = _providerName;
	}
	public mmlHiproviderName getProviderName() {
		return _providerName;
	}
	public void setProvider(mmlHiprovider _provider) {
		this._provider = _provider;
	}
	public mmlHiprovider getProvider() {
		return _provider;
	}
	public void setRecipient(mmlHirecipient _recipient) {
		this._recipient = _recipient;
	}
	public mmlHirecipient getRecipient() {
		return _recipient;
	}
	public void setStartDate(mmlHistartDate _startDate) {
		this._startDate = _startDate;
	}
	public mmlHistartDate getStartDate() {
		return _startDate;
	}
	public void setExpiredDate(mmlHiexpiredDate _expiredDate) {
		this._expiredDate = _expiredDate;
	}
	public mmlHiexpiredDate getExpiredDate() {
		return _expiredDate;
	}
	public void setPaymentRatio(mmlHipaymentRatio _paymentRatio) {
		this._paymentRatio = _paymentRatio;
	}
	public mmlHipaymentRatio getPaymentRatio() {
		return _paymentRatio;
	}
	
}