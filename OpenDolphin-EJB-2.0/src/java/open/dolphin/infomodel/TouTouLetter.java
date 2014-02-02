package open.dolphin.infomodel;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * がん相談「蕩蕩」の紹介状フォーム。
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="docType",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("TOUTOU")
public class TouTouLetter extends LetterModel {
    
    private String consultantHospital;
    
    private String consultantDept;
    
    private String consultantDoctor;
    
    private String clientHospital;
    
    @Transient
    private String clientName;
    
    @Transient
    private String clientAddress;
    
    @Transient
    private String clientTelephone;
    
    @Transient
    private String clientFax;
    
    private String patientName;
    
    private String patientGender;
    
    private String patientBirthday;
    
    @Transient
    private String patientAge;
    
    private String disease;
    
    private String purpose;
    
    @Transient
    private String pastFamily;
    
    @Transient
    private String clinicalCourse;
    
    @Transient
    private String medication;
    
    @Transient
    private String remarks;
    
    /** Creates a new instance of TouTouLetter */
    public TouTouLetter() {
    }

    public String getConsultantHospital() {
        return consultantHospital;
    }

    public void setConsultantHospital(String consultantHospital) {
        this.consultantHospital = consultantHospital;
    }

    public String getConsultantDept() {
        return consultantDept;
    }

    public void setConsultantDept(String consultantDept) {
        this.consultantDept = consultantDept;
    }

    public String getConsultantDoctor() {
        return consultantDoctor;
    }

    public void setConsultantDoctor(String consultantDoctor) {
        this.consultantDoctor = consultantDoctor;
    }

    public String getClientHospital() {
        return clientHospital;
    }

    public void setClientHospital(String clientHospital) {
        this.clientHospital = clientHospital;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
    }

    public String getClientFax() {
        return clientFax;
    }

    public void setClientFax(String clientFax) {
        this.clientFax = clientFax;
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

    public String getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(String patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPastFamily() {
        return pastFamily;
    }

    public void setPastFamily(String pastFamily) {
        this.pastFamily = pastFamily;
    }

    public String getClinicalCourse() {
        return clinicalCourse;
    }

    public void setClinicalCourse(String clinicalCourse) {
        this.clinicalCourse = clinicalCourse;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
