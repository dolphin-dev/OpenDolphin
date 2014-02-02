package open.dolphin.infomodel;

import java.io.Serializable;

/**
 * LaboImportReply
 */
public class LaboImportReply implements Serializable {
	
    private static final long serialVersionUID = 4916041527411972913L;

    private long karteId;
    private String patientName;
    private String patinetGender;
    private String patientBirthday;

    public long getKarteId() {
            return karteId;
    }
    public void setKarteId(long karteId) {
            this.karteId = karteId;
    }
    public String getPatientBirthday() {
            return patientBirthday;
    }
    public void setPatientBirthday(String patientBirthday) {
            this.patientBirthday = patientBirthday;
    }
    public String getPatientName() {
            return patientName;
    }
    public void setPatientName(String patientName) {
            this.patientName = patientName;
    }
    public String getPatinetGender() {
            return patinetGender;
    }
    public void setPatinetGender(String patinetGender) {
            this.patinetGender = patinetGender;
    }
}
