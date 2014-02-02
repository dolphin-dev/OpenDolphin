/*
 * PublicHealthInsuranceitem.java
 *
 * Created on 2001/10/30, 11:20
 */

package mirrorI.dolphin.server;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PvtPublicInsuranceItem {

    private String publicInsurancePriority;
    private String publicInsuranceProviderName;
    private String publicInsuranceProvider;
    private String publicInsuranceRecipient;
    private String publicInsuranceStartDate;
    private String publicInsuranceExpiredDate;
    private String publicInsurancePaymentRatio;
    private String publicInsurancePaymentRatioType;

    public PvtPublicInsuranceItem() {
        super();
    }
    
    public String getPublicInsurancePaymentRatioType() {
        return publicInsurancePaymentRatioType;
    }
    
    public void setPublicInsurancePaymentRatioType(String val) {
        publicInsurancePaymentRatioType = val;
    }    
    
    public String getPublicInsurancePaymentRatio() {
        return publicInsurancePaymentRatio;
    }
    
    public void setPublicInsurancePaymentRatio(String val) {
        publicInsurancePaymentRatio = val;
    }    
    
    public String getPublicInsuranceExpiredDate() {
        return publicInsuranceExpiredDate;
    }
    
    public void setPublicInsuranceExpiredDate(String val) {
        publicInsuranceExpiredDate = val;
    }     
    
    public String getPublicInsuranceStartDate() {
        return publicInsuranceStartDate;
    }
    
    public void setPublicInsuranceStartDate(String val) {
        publicInsuranceStartDate = val;
    }       
    
    public String getPublicInsuranceRecipient() {
        return publicInsuranceRecipient;
    }
    
    public void setPublicInsuranceRecipient(String val) {
        publicInsuranceRecipient = val;
    }    
    
    public String getPublicInsurancePriority() {
        return publicInsurancePriority;
    }
    
    public void setPublicInsurancePriority(String val) {
        publicInsurancePriority = val;
    }
    
    public String getPublicInsuranceProviderName() {
        return publicInsuranceProviderName;
    }
    
    public void setPublicInsuranceProviderName(String val) {
        publicInsuranceProviderName = val;
    } 
    
    public String getPublicInsuranceProvider() {
        return publicInsuranceProvider;
    }
    
    public void setPublicInsuranceProvider(String val) {
        publicInsuranceProvider = val;
    }   
    
    public String toString() {

        StringBuffer buf = new StringBuffer();

        if (publicInsuranceProviderName != null) {
            //buf.append("InsurancePubProviderName: ");
            buf.append(publicInsuranceProviderName);
            //buf.append("\n");  
        } else if (publicInsuranceProvider != null) {
            //buf.append("InsurancePubProvider: ");
            buf.append(publicInsuranceProvider);
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