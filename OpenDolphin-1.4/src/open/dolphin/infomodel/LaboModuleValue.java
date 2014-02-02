package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * LaboModuleValue
 *
 */
@Entity
@Table(name = "d_labo_module")
public class LaboModuleValue extends KarteEntryBean {
    
    private static final long serialVersionUID = 582445940772745523L;
    
    // MMLのUID
    @Column(nullable=false, unique=true, length=32)
    private String docId;
    
    @Transient
    private String patientId;
    
    @Transient
    private String patientIdType;
    
    @Transient
    private String patientIdTypeCodeSys;
    
    private String registId;
    
    private String sampleTime;
    
    private String registTime;
    
    private String reportTime;
    
    private String reportStatus;
    
    private String reportStatusCode;
    
    private String reportStatusCodeId;
    
    private String setName;
    
    private String setCode;
    
    private String setCodeId;
    
    private String clientFacility;
    
    private String clientFacilityCode;
    
    private String clientFacilityCodeId;
    
    private String laboratoryCenter;
    
    private String laboratoryCenterCode;
    
    private String laboratoryCenterCodeId;
    
    //private String confirmDate;
    
    @OneToMany(mappedBy="laboModule", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<LaboSpecimenValue> laboSpecimens;
    
    
    public LaboModuleValue() {
    }
    
    public String getDocId() {
        return docId;
    }
    
    public void setDocId(String id) {
        this.docId = id;
    }
    
    public String getClientFacility() {
        return clientFacility;
    }
    
    public void setClientFacility(String clientFacility) {
        this.clientFacility = clientFacility;
    }
    
    public String getClientFacilityCode() {
        return clientFacilityCode;
    }
    
    public void setClientFacilityCode(String clientFacilityCode) {
        this.clientFacilityCode = clientFacilityCode;
    }
    
    public String getClientFacilityCodeId() {
        return clientFacilityCodeId;
    }
    
    public void setClientFacilityCodeId(String clientFacilityCodeId) {
        this.clientFacilityCodeId = clientFacilityCodeId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientIdType() {
        return patientIdType;
    }
    
    public void setPatientIdType(String patientIdType) {
        this.patientIdType = patientIdType;
    }
    
    public String getPatientIdTypeCodeSys() {
        return patientIdTypeCodeSys;
    }
    
    public void setPatientIdTypeCodeSys(String patientIdTypeCodeSys) {
        this.patientIdTypeCodeSys = patientIdTypeCodeSys;
    }
    
//    public String getConfirmDate() {
//        return confirmDate;
//    }
//    
//    public void setConfirmDate(String confirmDate) {
//        this.confirmDate = confirmDate;
//    }
    
    public String getLaboratoryCenter() {
        return laboratoryCenter;
    }
    
    public void setLaboratoryCenter(String laboratoryCenter) {
        this.laboratoryCenter = laboratoryCenter;
    }
    
    public String getLaboratoryCenterCode() {
        return laboratoryCenterCode;
    }
    
    public void setLaboratoryCenterCode(String laboratoryCenterCode) {
        this.laboratoryCenterCode = laboratoryCenterCode;
    }
    
    public String getLaboratoryCenterCodeId() {
        return laboratoryCenterCodeId;
    }
    
    public void setLaboratoryCenterCodeId(String laboratoryCenterCodeId) {
        this.laboratoryCenterCodeId = laboratoryCenterCodeId;
    }
    
    public Collection<LaboSpecimenValue> getLaboSpecimens() {
        return laboSpecimens;
    }
    
    public void setLaboSpecimens(Collection<LaboSpecimenValue> laboSpecimens) {
        this.laboSpecimens = laboSpecimens;
    }
    
    public void addLaboSpecimen(LaboSpecimenValue specimen) {
        if (laboSpecimens == null) {
            laboSpecimens = new ArrayList<LaboSpecimenValue>();
        }
        laboSpecimens.add(specimen);
    }
    
    public String getRegistId() {
        return registId;
    }
    
    public void setRegistId(String registId) {
        this.registId = registId;
    }
    
    public String getRegistTime() {
        return registTime;
    }
    
    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }
    
    public String getReportStatus() {
        return reportStatus;
    }
    
    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }
    
    public String getReportStatusCode() {
        return reportStatusCode;
    }
    
    public void setReportStatusCode(String reportStatusCode) {
        this.reportStatusCode = reportStatusCode;
    }
    
    public String getReportStatusCodeId() {
        return reportStatusCodeId;
    }
    
    public void setReportStatusCodeId(String reportStatusCodeId) {
        this.reportStatusCodeId = reportStatusCodeId;
    }
    
    public String getReportTime() {
        return reportTime;
    }
    
    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }
    
    public String getSampleTime() {
        return sampleTime;
    }
    
    public void setSampleTime(String sampleTime) {
        this.sampleTime = sampleTime;
    }
    
    public String getSetCode() {
        return setCode;
    }
    
    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }
    
    public String getSetCodeId() {
        return setCodeId;
    }
    
    public void setSetCodeId(String setCodeId) {
        this.setCodeId = setCodeId;
    }
    
    public String getSetName() {
        return setName;
    }
    
    public void setSetName(String setName) {
        this.setName = setName;
    }
    
    /**
     * サンプルタイムで比較する。
     * @return 比較値
     */
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String sampleTime1 = getSampleTime();
            String sampleTime2 = ((LaboModuleValue) other).getSampleTime();
            if (sampleTime1 != null && sampleTime2 != null) {
                return sampleTime1.compareTo(sampleTime2);
            } else {
                String cf1 = getConfirmDate();
                String cf2 = ((LaboModuleValue) other).getConfirmDate();
                return cf1.compareTo(cf2);
            }
        }
        return -1;
    }
}
