/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm20.converter;

import java.util.List;

/**
 *
 * @author kazushi
 */
public final class IPHRClaimModule implements java.io.Serializable {
    
    // 文書関連
    private String docId;           // 文書ID MML用
    private String started;         // 開始日
    private String confirmed;       // 確定日
    
    // 患者関連
    private String patientId;       // 患者ID
    private String patientName;     // 患者氏名
    private String patientSex;      // 患者性別
    private String patientBirthday; // 患者生年月日
    
    // MMLの creator関連
    private String userId;          // creator ID
    private String commonName;      // creator Name
    private String department;      // 診療科コード
    private String departmentDesc;  // 診療科名称
    private String license;         // 医療資格
    
    // MMLの施設関連
    private String facilityId;      // 施設ID
    private String facilityName;    // 施設名称
    private String jmariCode;       // JMARI code
    
    // CLAIM バンドル
    private List<IClaimBundle> bundles;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public List<IClaimBundle> getBundles() {
        return bundles;
    }

    public void setBundles(List<IClaimBundle> bundles) {
        this.bundles = bundles;
    }

    public String getJmariCode() {
        return jmariCode;
    }

    public void setJmariCode(String jmariCode) {
        this.jmariCode = jmariCode;
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
}
