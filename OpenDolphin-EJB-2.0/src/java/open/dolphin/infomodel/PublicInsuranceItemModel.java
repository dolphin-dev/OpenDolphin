/*
 * PublicInsurance.java
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
 * <!ELEMENT mmlHi:publicInsuranceItem (mmlHi:providerName?, 
 *                                      mmlHi:provider, 
 *                                      mmlHi:recipient, 
 *                                      mmlHi:startDate, 
 *                                      mmlHi:expiredDate, 
 *                                      mmlHi:paymentRatio?)> 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */

public class PublicInsuranceItemModel extends InfoModel {

    String priority;

    String providerName;

    String provider;

    String recipient;
    
    String startDate;
    
    String expiredDate;
    
    String paymentRatio;
    
    String ratioType;
    

    public PublicInsuranceItemModel() {
    }

    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String val) {
        priority = val;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String val) {
        providerName = val;
    }  
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String val) {
        provider = val;
    }   
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String val) {
        recipient = val;
    }   
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    }       
    
    public String getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(String val) {
        expiredDate = val;
    }   
    
    public String getPaymentRatio() {
        return paymentRatio;
    }
    
    public void setPaymentRatio(String val) {
        paymentRatio = val;
    }  
    
    public String getRatioType() {
        return ratioType;
    }
    
    public void setRatioType(String val) {
        ratioType = val;
    }
}    