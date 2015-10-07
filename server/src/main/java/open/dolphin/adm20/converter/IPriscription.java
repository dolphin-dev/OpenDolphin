package open.dolphin.adm20.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.PriscriptionModel;

/**
 *
 * @author kazushi Minagawa
 */
public class IPriscription implements java.io.Serializable {
    
    // 患者ID
    private String patientId;
    
    // 患者氏名
    private String patientName;
    
    // 患者カナ
    private String patientKana;
    
    // 患者性別(男｜女)
    private String patientSex;
    
    // 患者生年月日 yyyy-MM-dd
    private String patientBirthday;
    
    // 患者郵便番号
    private String patientZipcode;
    
    // 患者住所
    private String patientAddress;
    
    // 患者電話
    private String patientTelephone;
    
    // 処方リスト
    private List<IClaimBundle> priscriptionList;
    
    // 適用保険
    private IPVTHealthInsurance applyedInsurance;
    
    // 責任医師
    private String physicianName;
    
    // 麻薬免許
    private String drugLicenseNumber;
    
    // 医療機関名
    private String institutionName;
    
    // 医療機関郵便番号
    private String institutionZipcode;
    
    // 医療機関住所
    private String institutionAddress;
    
    // 医療機関電話番号
    private String institutionTelephone;
    
    // 保険医療機関番号
    private String InstitutionNumber;
    
    // 交付日
    private String issuanceDate;
    
    // 使用期間
    private String period;
    
    // 備考欄患者住所、氏名転記フラグ
    private String chkPatientInfo;
    
    // 備考欄患者麻薬施用者転記フラグ
    private String chkUseDrugInfo;
    
    // 備考欄に「在宅」を記載するかどうかのフラグ
    private String chkHomeMedical;
    
    // 一般名を使用するかどうか
    private String useGeneralName;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientKana() {
        return patientKana;
    }

    public void setPatientKana(String patientKana) {
        this.patientKana = patientKana;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(String patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public String getPatientZipcode() {
        return patientZipcode;
    }

    public void setPatientZipcode(String patientZipcode) {
        this.patientZipcode = patientZipcode;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientTelephone() {
        return patientTelephone;
    }

    public void setPatientTelephone(String patientTelephone) {
        this.patientTelephone = patientTelephone;
    }

    public List<IClaimBundle> getPriscriptionList() {
        return priscriptionList;
    }

    public void setPriscriptionList(List<IClaimBundle> priscriptionList) {
        this.priscriptionList = priscriptionList;
    }

    public IPVTHealthInsurance getApplyedInsurance() {
        return applyedInsurance;
    }

    public void setApplyedInsurance(IPVTHealthInsurance applyedInsurance) {
        this.applyedInsurance = applyedInsurance;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
    }

    public String getDrugLicenseNumber() {
        return drugLicenseNumber;
    }

    public void setDrugLicenseNumber(String drugLicenseNumber) {
        this.drugLicenseNumber = drugLicenseNumber;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionZipcode() {
        return institutionZipcode;
    }

    public void setInstitutionZipcode(String institutionZipcode) {
        this.institutionZipcode = institutionZipcode;
    }

    public String getInstitutionAddress() {
        return institutionAddress;
    }

    public void setInstitutionAddress(String institutionAddress) {
        this.institutionAddress = institutionAddress;
    }

    public String getInstitutionTelephone() {
        return institutionTelephone;
    }

    public void setInstitutionTelephone(String institutionTelephone) {
        this.institutionTelephone = institutionTelephone;
    }

    public String getInstitutionNumber() {
        return InstitutionNumber;
    }

    public void setInstitutionNumber(String InstitutionNumber) {
        this.InstitutionNumber = InstitutionNumber;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getChkPatientInfo() {
        return chkPatientInfo;
    }

    public void setChkPatientInfo(String chkPatientInfo) {
        this.chkPatientInfo = chkPatientInfo;
    }

    public String getChkUseDrugInfo() {
        return chkUseDrugInfo;
    }

    public void setChkUseDrugInfo(String chkUseDrugInfo) {
        this.chkUseDrugInfo = chkUseDrugInfo;
    }

    public String getChkHomeMedical() {
        return chkHomeMedical;
    }

    public void setChkHomeMedical(String chkHomeMedical) {
        this.chkHomeMedical = chkHomeMedical;
    }
    
    public String getUseGeneralName() {
        return useGeneralName;
    }

    public void setUseGeneralName(String useGeneralName) {
        this.useGeneralName = useGeneralName;
    }

    public PriscriptionModel toModel() {
        
        PriscriptionModel ret = new PriscriptionModel();
        
        // 患者
        ret.setPatientId(this.getPatientId());
        ret.setPatientName(this.getPatientName());
        ret.setPatientKana(this.getPatientKana());
        ret.setPatientSex(this.getPatientSex());
        ret.setPatientBirthday(this.getPatientBirthday());
        ret.setPatientZipcode(this.getPatientZipcode());
        ret.setPatientAddress(this.getPatientAddress());
        ret.setPatientTelephone(this.getPatientTelephone());
        
        // 医師
        ret.setPhysicianName(this.getPhysicianName());
        ret.setDrugLicenseNumber(this.getDrugLicenseNumber());
        
        // 医療機関
        ret.setInstitutionName(this.getInstitutionName());
        ret.setInstitutionZipcode(this.getInstitutionZipcode());
        ret.setInstitutionAddress(this.getInstitutionAddress());
        ret.setInstitutionTelephone(this.getInstitutionTelephone());
        ret.setInstitutionNumber(this.getInstitutionNumber());
      
        // 発行日
        ret.setIssuanceDate(IOSHelper.toDate(this.getIssuanceDate()));
        ret.setPeriod(IOSHelper.toDate(this.getPeriod()));
        
        // フラグ
        ret.setChkHomeMedical(IOSHelper.toBool(this.getChkHomeMedical()));
        ret.setChkPatientInfo(IOSHelper.toBool(this.getChkPatientInfo()));
        ret.setChkUseDrugInfo(IOSHelper.toBool(this.getChkUseDrugInfo()));

        // 適用保険
        ret.setApplyedInsurance(this.getApplyedInsurance().toModel());
        
        // 処方リスト medOrdrで判別?
        List<BundleMed> list = new ArrayList<BundleMed>();
        for (IClaimBundle icb : priscriptionList) {
            list.add((BundleMed)icb.toModel());
        }
        ret.setPriscriptionList(list);
        
        // 一般名使用
        ret.setUseGeneraklName(IOSHelper.toBool(this.getUseGeneralName()));
        
        return ret;
    }
}
