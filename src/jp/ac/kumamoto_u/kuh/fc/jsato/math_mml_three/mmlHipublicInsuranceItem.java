/**
 *
 * mmlHipublicInsuranceItem.java
 * Created on 2003/1/4 2:30:2
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

			if ( this.getLocalName().equals("levelone") ) {
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
				for (int i=0; i < atts.getLength(); ++i) {
					if ( ((String)atts.getQName(i)).equals("mmlHi:priority") ) {
						set__mmlHipriority( atts.getValue(i) );
						((mmlHipublicInsuranceItem)builder.getElement()).set__mmlHipriority( atts.getValue(i) );
					}
				}
			}

			
			return true;
		}
		return false;
	}
	
	public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
		if (qName.equals("mmlHi:publicInsuranceItem") == true) {
			
			/* connection */
			if (parentElement.getQName().equals("mmlHi:publicInsurance")) {
				Vector v = ((mmlHipublicInsurance)builder.getParent()).get_publicInsuranceItem();
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
	public void set__mmlHipriority(String __mmlHipriority) {
		this.__mmlHipriority = __mmlHipriority;
	}
	public String get__mmlHipriority() {
		return __mmlHipriority;
	}

	public void set_providerName(mmlHiproviderName _providerName) {
		this._providerName = _providerName;
	}
	public mmlHiproviderName get_providerName() {
		return _providerName;
	}
	public void set_provider(mmlHiprovider _provider) {
		this._provider = _provider;
	}
	public mmlHiprovider get_provider() {
		return _provider;
	}
	public void set_recipient(mmlHirecipient _recipient) {
		this._recipient = _recipient;
	}
	public mmlHirecipient get_recipient() {
		return _recipient;
	}
	public void set_startDate(mmlHistartDate _startDate) {
		this._startDate = _startDate;
	}
	public mmlHistartDate get_startDate() {
		return _startDate;
	}
	public void set_expiredDate(mmlHiexpiredDate _expiredDate) {
		this._expiredDate = _expiredDate;
	}
	public mmlHiexpiredDate get_expiredDate() {
		return _expiredDate;
	}
	public void set_paymentRatio(mmlHipaymentRatio _paymentRatio) {
		this._paymentRatio = _paymentRatio;
	}
	public mmlHipaymentRatio get_paymentRatio() {
		return _paymentRatio;
	}
	
}