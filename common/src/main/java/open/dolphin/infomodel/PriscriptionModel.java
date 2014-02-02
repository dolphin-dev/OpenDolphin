package open.dolphin.infomodel;

import java.util.Date;
import java.util.List;

/**
 *
 * @author kazushi Minagawa
 */
public class PriscriptionModel extends InfoModel {
    
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
    private List<BundleMed> priscriptionList;
    
    // 適用保険
    private PVTHealthInsuranceModel applyedInsurance;
    
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
    private Date issuanceDate;
    
    // 使用期間
    private Date period;
    
    // 備考欄患者住所、氏名転記フラグ
    private boolean chkPatientInfo;
    
    // 備考欄患者麻薬施用者転記フラグ
    private boolean chkUseDrugInfo;
    
    // 備考欄に「在宅」を記載するかどうかのフラグ
    private boolean chkHomeMedical;
    
    private boolean useGeneraklName;

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

    public List<BundleMed> getPriscriptionList() {
        return priscriptionList;
    }

    public void setPriscriptionList(List<BundleMed> priscriptionList) {
        this.priscriptionList = priscriptionList;
    }

    public PVTHealthInsuranceModel getApplyedInsurance() {
        return applyedInsurance;
    }

    public void setApplyedInsurance(PVTHealthInsuranceModel applyedInsurance) {
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

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public boolean isChkPatientInfo() {
        return chkPatientInfo;
    }

    public void setChkPatientInfo(boolean chkPatientInfo) {
        this.chkPatientInfo = chkPatientInfo;
    }

    public boolean isChkUseDrugInfo() {
        return chkUseDrugInfo;
    }

    public void setChkUseDrugInfo(boolean chkUseDrugInfo) {
        this.chkUseDrugInfo = chkUseDrugInfo;
    }

    public boolean isChkHomeMedical() {
        return chkHomeMedical;
    }

    public void setChkHomeMedical(boolean chkHomeMedical) {
        this.chkHomeMedical = chkHomeMedical;
    }

    public boolean isUseGeneraklName() {
        return useGeneraklName;
    }

    public void setUseGeneraklName(boolean useGeneraklName) {
        this.useGeneraklName = useGeneraklName;
    }
}
