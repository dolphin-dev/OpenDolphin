/*
 * PublicHealthInsuranceitem.java
 *
 * Created on 2001/10/30, 11:20
 */

package open.dolphin.infomodel;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PVTPublicInsuranceItemModel extends InfoModel {

    private static final long serialVersionUID = 7141232138488822853L;
	
    private String priority;
    private String providerName;
    private String provider;
    private String recipient;
    private String startDate;
    private String expiredDate;
    private String paymentRatio;
    private String paymentRatioType;

    public PVTPublicInsuranceItemModel() {
        super();
    }
    
    public String getPaymentRatioType() {
        return paymentRatioType;
    }
    
    public void setPaymentRatioType(String val) {
        paymentRatioType = val;
    }    
    
    public String getPaymentRatio() {
        return paymentRatio;
    }
    
    public void setPaymentRatio(String val) {
        paymentRatio = val;
    }    
    
    public String getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(String val) {
        expiredDate = val;
    }     
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    }       
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String val) {
        recipient = val;
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
    
    public String toString() {

        StringBuffer buf = new StringBuffer();

        if (providerName != null) {
            //buf.append("InsurancePubProviderName: ");
            buf.append(providerName);
            //buf.append("\n");  
        } else if (provider != null) {
            //buf.append("InsurancePubProvider: ");
            buf.append(provider);
            //buf.append("\n");  
        }        

        return buf.toString();
    }  

    /*public String toString() {

        StringBuffer buf = new StringBuffer();

        if (publicInsurancePriority != null) {
            buf.append("InsurancePubPriority: ");
            buf.append(publicInsurancePriority);
            buf.append("\n");  
        } 

        if (publicInsuranceProviderName != null) {
            buf.append("InsurancePubProviderName: ");
            buf.append(publicInsuranceProviderName);
            buf.append("\n");  
        }         

        if (publicInsuranceProvider != null) {
            buf.append("InsurancePubProvider: ");
            buf.append(publicInsuranceProvider);
            buf.append("\n");  
        }        

        if (publicInsuranceRecipient != null) {
            buf.append("InsurancePubRecipient: ");
            buf.append(publicInsuranceRecipient);
            buf.append("\n");  
        }         

        if (publicInsuranceStartDate != null) {
            buf.append("InsurancePubStartDate: ");
            buf.append(publicInsuranceStartDate);
            buf.append("\n");  
        }         

        if (publicInsuranceExpiredDate != null) {
            buf.append("InsurancePubExpiredDate: ");
            buf.append(publicInsuranceExpiredDate);
            buf.append("\n");  
        } 

        if (publicInsurancePaymentRatio != null) {
            buf.append("InsurancePubPaymentRatio: ");
            buf.append(publicInsurancePaymentRatio);
            buf.append("\n");  
        } 

        if (publicInsurancePaymentRatioType != null) {
            buf.append("insurancePubPaymentRatioType: ");
            buf.append(publicInsurancePaymentRatioType);
            buf.append("\n");  
        } 

        return buf.toString();
    }  */      
}