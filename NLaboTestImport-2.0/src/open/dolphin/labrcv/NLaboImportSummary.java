package open.dolphin.labrcv;

import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */
public class NLaboImportSummary {

    private String laboCode;

    private String patientId;

    private String patientName;

    private String patientSex;

    private String sampleDate;

    private String numOfTestItems;

    private String status;

    private String result;

    private NLaboModule module;

    private PatientModel patient;

    private String karteId;
    private String karteName;
    private String karteKanaName;
    private String karteSex;
    private String karteBirthday;

    /**
     * @return the laboCode
     */
    public String getLaboCode() {
        return laboCode;
    }

    /**
     * @param laboCode the laboCode to set
     */
    public void setLaboCode(String laboCode) {
        this.laboCode = laboCode;
    }

    /**
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the patientSex
     */
    public String getPatientSex() {
        return patientSex;
    }

    /**
     * @param patientSex the patientSex to set
     */
    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    /**
     * @return the sampleDate
     */
    public String getSampleDate() {
        return sampleDate;
    }

    /**
     * @param sampleDate the sampleDate to set
     */
    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    /**
     * @return the numOfTestItems
     */
    public String getNumOfTestItems() {
        return numOfTestItems;
    }

    /**
     * @param numOfTestItems the numOfTestItems to set
     */
    public void setNumOfTestItems(String numOfTestItems) {
        this.numOfTestItems = numOfTestItems;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the patient
     */
    public PatientModel getPatient() {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    /**
     * @return the module
     */
    public NLaboModule getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(NLaboModule module) {
        this.module = module;
    }

    /**
     * @return the karteName
     */
    public String getKarteName() {
        return karteName;
    }

    /**
     * @param karteName the karteName to set
     */
    public void setKarteName(String karteName) {
        this.karteName = karteName;
    }

    /**
     * @return the karteKanaName
     */
    public String getKarteKanaName() {
        return karteKanaName;
    }

    /**
     * @param karteKanaName the karteKanaName to set
     */
    public void setKarteKanaName(String karteKanaName) {
        this.karteKanaName = karteKanaName;
    }

    /**
     * @return the karteSex
     */
    public String getKarteSex() {
        return karteSex;
    }

    /**
     * @param karteSex the karteSex to set
     */
    public void setKarteSex(String karteSex) {
        this.karteSex = karteSex;
    }

    /**
     * @return the karteBirthday
     */
    public String getKarteBirthday() {
        return karteBirthday;
    }

    /**
     * @param karteBirthday the karteBirthday to set
     */
    public void setKarteBirthday(String karteBirthday) {
        this.karteBirthday = karteBirthday;
    }

    /**
     * @return the karteId
     */
    public String getKarteId() {
        return karteId;
    }

    /**
     * @param karteId the karteId to set
     */
    public void setKarteId(String karteId) {
        this.karteId = karteId;
    }

}
