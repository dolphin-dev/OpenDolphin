package open.dolphin.message;

import java.util.List;

/**
 * StringBuilder
 *
 * @author Kazushi Minagawa.
 *
 */
public final class DiseaseHelper {
    
    // 患者ID
    private String patientId;
    
    // 確定日時 YYYY-MM-DDTHH:mm:ss
    private String confirmDate;
    
    // MMLの groupId
    private String groupId;
    
    // 診療科コード
    private String department;
    
    // 診療科名
    private String departmentDesc;
    
    // 担当医名
    private String creatorName;
    
    // 担当医ID
    private String creatorId;
    
    // 担当医医療資格
    private String creatorLicense;
    
    // 施設名
    private String facilityName;
    
    // JMARIコード
    private String jmariCode;
    
    // 病名モジュール(docInfo+RegisteredDiagnosis)のリスト
    private List diagnosisModuleItems;
    

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentDesc() {
        return departmentDesc;
    }

    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorLicense() {
        return creatorLicense;
    }

    public void setCreatorLicense(String creatorLicense) {
        this.creatorLicense = creatorLicense;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getJmariCode() {
        return jmariCode;
    }

    public void setJmariCode(String jmariCode) {
        this.jmariCode = jmariCode;
    }

    public List getDiagnosisModuleItems() {
        return diagnosisModuleItems;
    }

    public void setDiagnosisModuleItems(List diagnosisModuleItems) {
        this.diagnosisModuleItems = diagnosisModuleItems;
    }
}
