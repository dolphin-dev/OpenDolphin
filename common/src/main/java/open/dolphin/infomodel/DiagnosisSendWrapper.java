package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.List;

/**
 * 病名を送信（DB保存＆CLAIM送信）をするためのラッパークラス。
 * 
 * @author kazushi Minagawa.
 */
public class DiagnosisSendWrapper extends InfoModel implements Serializable {
    
    // flag
    private boolean sendClaim;
    
    // 確定日
    private String confirmDate;
    
    // MML DocInfo用の  Title
    private String title;
    
    // MML DocInfo用の  purpose
    private String purpose;
    
    // MML DocInfo用の  groupId
    private String groupId;
    
    // 患者ID
    private String patientId;
    
    // 患者氏名
    private String patientName;
    
    // 患者性別
    private String patientGender;
    
    // 施設名
    private String facilityName;
    
    // JMARI code
    private String jamariCode;
    
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
    
    // 新規に追加された病名のリスト
    private List<RegisteredDiagnosisModel> addedDiagnosis;
    
    // 更新された（転帰等）病名のリスト
    private List<RegisteredDiagnosisModel> updatedDiagnosis;
    
    
    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmdate) {
        this.confirmDate = confirmdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpse) {
        this.purpose = purpse;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getJamariCode() {
        return jamariCode;
    }

    public void setJamariCode(String jamariCode) {
        this.jamariCode = jamariCode;
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

    public List<RegisteredDiagnosisModel> getAddedDiagnosis() {
        return addedDiagnosis;
    }

    public void setAddedDiagnosis(List<RegisteredDiagnosisModel> addedDiagnosis) {
        this.addedDiagnosis = addedDiagnosis;
    }

    public List<RegisteredDiagnosisModel> getUpdatedDiagnosis() {
        return updatedDiagnosis;
    }

    public void setUpdatedDiagnosis(List<RegisteredDiagnosisModel> updatedDiagnosis) {
        this.updatedDiagnosis = updatedDiagnosis;
    }

    public boolean getSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(boolean sendClaim) {
        this.sendClaim = sendClaim;
    }
}
