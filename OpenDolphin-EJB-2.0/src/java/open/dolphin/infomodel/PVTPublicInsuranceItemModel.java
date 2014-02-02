package open.dolphin.infomodel;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PVTPublicInsuranceItemModel extends InfoModel {
	
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
    
    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();

        if (providerName != null) {
            buf.append(providerName);
        } else if (provider != null) {
            buf.append(provider);
        }        

        return buf.toString();
    }

    public String toClaim() {

        StringBuilder sb = new StringBuilder();

        sb.append("<mmlHi:publicInsuranceItem ");

        // åˆîÔÇÃóDêÊèáà  attribute
        if (getPriority()!=null) {
            sb.append("mmlHi:priority=");
            sb.append(addQuote(getPriority()));
            sb.append(">");
        }

        // åˆîÔïâíSñºèÃ ?
        if (getProviderName()!=null) {
            sb.append("<mmlHi:providerName>");
            sb.append(getProviderName());
            sb.append("</mmlHi:providerName>");
        }

        // ïâíSé“î‘çÜ
        if (getProvider()!=null) {
            sb.append("<mmlHi:provider>");
            sb.append(getProvider());
            sb.append("</mmlHi:provider>");
        }

        // éÛããé“î‘çÜ
        if (getRecipient()!=null) {
            sb.append("<mmlHi:recipient>");
            sb.append(getRecipient());
            sb.append("</mmlHi:recipient>");
        }

        // äJénì˙
        if (getStartDate()!=null) {
            sb.append("<mmlHi:startDate>");
            sb.append(getStartDate());
            sb.append("</mmlHi:startDate>");
        }

        // óLå¯ä˙å¿
        if (getExpiredDate()!=null) {
            sb.append("<mmlHi:expiredDate>");
            sb.append(getExpiredDate());
            sb.append("</mmlHi:expiredDate>");
        }

        // ïâíSó¶ ?
        if (getPaymentRatio()!=null && getPaymentRatioType()!=null) {
            sb.append("<mmlHi:paymentRatio mmlHi:RatioType=");
            sb.append(addQuote(getPaymentRatioType()));
            sb.append(">");
            sb.append(getPaymentRatio());
            sb.append("</mmlHi:paymentRatio>");
        }

        sb.append("</mmlHi:publicInsuranceItem>");

        return sb.toString();
    }

    private String addQuote(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(str);
        sb.append("\"");
        return sb.toString();
    }
}