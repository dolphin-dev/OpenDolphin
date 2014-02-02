/*
 * HealthInsuranceModule.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.infomodel;

/**
 * åíçNï€åØÉÇÉWÉÖÅ[Éã
 *<!ELEMENT mmlHi:HealthInsuranceModule (mmlHi:insuranceClass?, 
 *                                       mmlHi:insuranceNumber, 
 *                                       mmlHi:clientId, 
 *                                       mmlHi:familyClass, 
 *                                       mmlHi:clientInfo?, 
 *                                       mmlHi:continuedDiseases?, 
 *                                       mmlHi:startDate, 
 *                                       mmlHi:expiredDate, 
 *                                       mmlHi:paymentInRatio?, 
 *                                       mmlHi:paymentOutRatio?, 
 *                                       mmlHi:insuredInfo?, 
 *                                       mmlHi:workInfo?, 
 *                                       mmlHi:publicInsurance?) > 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class HealthInsuranceModule extends InfoModel {

    // Attribute
    String countryType;
    
    String insuranceClass;
    
    String insuranceClassCode;
    
    String insuranceClassCodeTableId;
    
    String insuranceNumber;
    
    ClientId clientId;
    
    String familyClass;
    
    ClientInfo clientInfo;
    
    String[] continuedDiseases;
    
    String startDate;
    
    String expiredDate;
    
    String paymentInRatio;
    
    String paymentOutRatio;
    
    MMLOrganization insuredInfo;
    
    MMLOrganization workInfo;
    
    PublicInsuranceItem[] publicInsuranceItems;
    
    /** Creates new HealthInsuranceModule */
    public HealthInsuranceModule() {
    }
        
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    
    public void setInsuranceNumber(String value) {
        insuranceNumber = value;
    }
    
    public ClientId getClientId() {
        return clientId;
    }
    
    public void setClientId(ClientId value) {
        clientId = value;
    }

    public String getFamilyClass() {
        return familyClass;
    }
    
    public void setFamilyClass(String value) {
        familyClass = value;
    }
    
    public ClientInfo getClientInfo() {
        return clientInfo;
    }
    
    public void setClientInfo(ClientInfo val) {
        clientInfo = val;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String value) {
        startDate = value;
    }
    
    public String getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(String value) {
        expiredDate = value;
    }
    
    public String[] getContinuedDisease() {
        return continuedDiseases;
    }
    
    public void setContinuedDisease(String[] val) {
        continuedDiseases = val;
    }    
    
    public void addContinuedDisease(String value) {
        
        int len = 0;
        
        if (continuedDiseases == null) {
            continuedDiseases = new String[1];
        }
        else {
            len = continuedDiseases.length;
            String[] dest = new String[len + 1];
            System.arraycopy(continuedDiseases,0,dest,0,len);
            continuedDiseases = dest;
        }
        continuedDiseases[len] = value;         
    }
     
    public String getPaymentInRatio() {
        return paymentInRatio;
    }
    
    public void setPaymentInRatio(String value) {
        paymentInRatio = value;
    }
    
    public String getPaymentOutRatio() {
        return paymentOutRatio;
    }
    
    public void setPaymentOutRatio(String value) {
        paymentOutRatio = value;
    }    
    
    public MMLOrganization getInsuredInfo() {
        return insuredInfo;
    }
    
    public void getInsuredInfo(MMLOrganization val) {
        insuredInfo = val;
    }
    
    public MMLOrganization getWorkInfo() {
        return workInfo;
    }
    
    public void setWorkInfo(MMLOrganization val) {
        workInfo = val;
    }    
    
    public PublicInsuranceItem[] getPublicInsuranceItem() {
        return publicInsuranceItems;
    }
    
    public void setPublicInsuranceItem(PublicInsuranceItem[] val) {
        publicInsuranceItems = val;
    }    
    
    public void addPublicInsuranceItem(PublicInsuranceItem val) {
                
        int len = 0;
        
        if (publicInsuranceItems == null) {
            publicInsuranceItems = new PublicInsuranceItem[1];
        }
        else {
            len = publicInsuranceItems.length;
            PublicInsuranceItem[] dest = new PublicInsuranceItem[len + 1];
            System.arraycopy(publicInsuranceItems,0,dest,0,len);
            publicInsuranceItems = dest;
        }
        publicInsuranceItems[len] = val;
    }

	public void setInsuranceClass(String insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	public String getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClassCode(String insuranceClassCode) {
		this.insuranceClassCode = insuranceClassCode;
	}

	public String getInsuranceClassCode() {
		return insuranceClassCode;
	}

	public void setInsuranceClassCodeTableId(String insuranceClassCodeTableId) {
		this.insuranceClassCodeTableId = insuranceClassCodeTableId;
	}

	public String getInsuranceClassCodeTableId() {
		return insuranceClassCodeTableId;
	}
}
