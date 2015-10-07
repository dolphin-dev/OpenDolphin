package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi
 */
public class PHRCatch implements java.io.Serializable {
    
    // Entry情報
    private String catchId;
    private String started;
    private String confirmed;
    private String status;
    
    // 患者情報
    private String patientNumber;
    private String patientId;
    private String patientName;
    private String patientSex;
    private String patientBirthday;
    private String patientZipCode;
    private String patientAddress;
    private String patientTelephone;
    
    // 医療機関情報
    private String facilityNumber;
    private String facilityId;
    private String facilityName;
    private String facilityZipCode;
    private String facilityAddress;
    private String facilityTelephone;
    
    // 担当医情報
    private String physicianId;
    private String physicianName;
    private String department;
    private String departmentDesc;
    private String license;
    
    // 処方 Reply
    private int rpRequest;
    private int rpReply;
    private String rpReplyTo;
            
    // PHRModule oneToMany
    private List<PHRBundle> bundles;
    
    public PHRCatch() {
        bundles = new ArrayList();
    }

    public String getCatchId() {
        return catchId;
    }

    public void setCatchId(String docId) {
        this.catchId = docId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
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

    public String getPatientZipCode() {
        return patientZipCode;
    }

    public void setPatientZipCode(String patientZipCode) {
        this.patientZipCode = patientZipCode;
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

    public String getFacilityNumber() {
        return facilityNumber;
    }

    public void setFacilityNumber(String facilityNumber) {
        this.facilityNumber = facilityNumber;
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

    public String getFacilityZipCode() {
        return facilityZipCode;
    }

    public void setFacilityZipCode(String facilityZipCode) {
        this.facilityZipCode = facilityZipCode;
    }

    public String getFacilityAddress() {
        return facilityAddress;
    }

    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public String getFacilityTelephone() {
        return facilityTelephone;
    }

    public void setFacilityTelephone(String facilityTelephone) {
        this.facilityTelephone = facilityTelephone;
    }

    public String getPhysicianId() {
        return physicianId;
    }

    public void setPhysicianId(String physicianId) {
        this.physicianId = physicianId;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
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

    public List<PHRBundle> getBundles() {
        return bundles;
    }

    public void setBundles(List<PHRBundle> bundles) {
        this.bundles = bundles;
    }
    
    public void addBundle(PHRBundle b) {
        this.bundles.add(b);
    }

    public int getRpRequest() {
        return rpRequest;
    }

    public void setRpRequest(int rpRequest) {
        this.rpRequest = rpRequest;
    }

    public int getRpReply() {
        return rpReply;
    }

    public void setRpReply(int rpReply) {
        this.rpReply = rpReply;
    }

    public String getRpReplyTo() {
        return rpReplyTo;
    }

    public void setRpReplyTo(String rpReplyPhone) {
        this.rpReplyTo = rpReplyPhone;
    }
}
