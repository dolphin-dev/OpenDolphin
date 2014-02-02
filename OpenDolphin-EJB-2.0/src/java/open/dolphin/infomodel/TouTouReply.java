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
@DiscriminatorValue("TOUTOU_REPLY")
public class TouTouReply extends LetterModel {
    
    private String clientHospital;
    
    private String clientDept;
    
    private String clientDoctor;
    
    private String consultantHospital;
    
    private String consultantDept;
    
    private String consultantDoctor;
    
    private String patientName;
    
    private String patientGender;
    
    private String patientBirthday;
    
    @Transient
    private String patientAge;
    
    private String visited;
    
    @Transient
    private String informedContent;
    
    
    /** Creates a new instance of TouTouLetter */
    public TouTouReply() {
    }

    public String getClientHospital() {
        return clientHospital;
    }

    public void setClientHospital(String clientHospital) {
        this.clientHospital = clientHospital;
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

    public String getClientDept() {
        return clientDept;
    }

    public void setClientDept(String clientDept) {
        this.clientDept = clientDept;
    }

    public String getClientDoctor() {
        return clientDoctor;
    }

    public void setClientDoctor(String clientDoctor) {
        this.clientDoctor = clientDoctor;
    }

    public String getInformedContent() {
        return informedContent;
    }

    public void setInformedContent(String informedContent) {
        this.informedContent = informedContent;
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

    public String getVisited() {
        return visited;
    }

    public void setVisited(String visited) {
        this.visited = visited;
    }
}
