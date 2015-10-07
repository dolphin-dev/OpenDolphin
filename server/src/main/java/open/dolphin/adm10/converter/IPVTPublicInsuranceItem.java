package open.dolphin.adm10.converter;

import open.dolphin.infomodel.PVTPublicInsuranceItemModel;


/**
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class IPVTPublicInsuranceItem implements java.io.Serializable {

    // 優先順位
    private String priority;

    // 公費負担名称
    private String providerName;

    // 負担者番号
    private String provider;

    // 受給者番号
    private String recipient;
    
    // 開始日
    private String startDate;
    
    // 有効期限
    private String expiredDate;
    
    // 負担率または負担金
    private String paymentRatio;
    
    // 負担率のタイプ
    private String paymentRatioType;
    

    public IPVTPublicInsuranceItem() {
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
    
    public String getPaymentRatioType() {
        return paymentRatioType;
    }

    public void setPaymentRatioType(String paymentRatioType) {
        this.paymentRatioType = paymentRatioType;
    }
       
    public void fromModel(PVTPublicInsuranceItemModel model) {
        this.setPriority(model.getPriority());
        this.setProviderName(model.getProviderName());
        this.setProvider(model.getProvider());
        this.setRecipient(model.getRecipient());
        this.setStartDate(model.getStartDate());
        this.setExpiredDate(model.getExpiredDate());
        this.setPaymentRatio(model.getPaymentRatio());
        this.setPaymentRatioType(model.getPaymentRatioType());
    }
    
    public PVTPublicInsuranceItemModel toModel() {
        PVTPublicInsuranceItemModel ret = new PVTPublicInsuranceItemModel();
        ret.setPriority(this.getPriority());
        ret.setProviderName(this.getProviderName());
        ret.setProvider(this.getProvider());
        ret.setRecipient(this.getRecipient());
        ret.setStartDate(this.getStartDate());
        ret.setExpiredDate(this.getExpiredDate());
        ret.setPaymentRatio(this.getPaymentRatio());
        ret.setPaymentRatioType(this.getPaymentRatioType());
        return ret;
    }
}    