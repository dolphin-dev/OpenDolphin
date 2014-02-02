package open.dolphin.impl.mml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 *
 * @author kazushi
 */
public class PatientHelper {
    
    private PatientModel patient;
    private List<RegisteredDiagnosisModel> diagnosisList;
    private String confirmDate;
    private String facility;
    
    public String getPatientId() {
        return getPatient().getPatientId();
    }
    
    public String getPatientFamily(){
        return getPatient().getFamilyName();
    }
    
    public String getPatientGiven(){
        return getPatient().getGivenName();
    }
    
    public String getPatientName(){
        return getPatient().getFullName();
    }
    
    public String getPatientKanaFamily(){
        return getPatient().getKanaFamilyName();
    }
    
    public String getPatientKanaGiven(){
        return getPatient().getKanaGivenName();
    }
    
    public String getPatientKanaName(){
        return getPatient().getKanaName();
    }
    
    public String getPatientBirthday(){
        return getPatient().getBirthday();
    }
    
    public String getPatientGender(){
        return getPatient().getGender();
    }
    
    public String getPatientAddress(){
        return getPatient().getSimpleAddressModel()!=null ? getPatient().getSimpleAddressModel().getAddress() : null;
    }
    
    public String getPatientZip(){
        return getPatient().getSimpleAddressModel()!=null ? getPatient().getSimpleAddressModel().getZipCode() : null;
    }
    
    public String getPatientTelephone(){
        return getPatient().getTelephone();
    }
    
    public List<PVTHealthInsuranceModel> getInsurances() {
        return getPatient().getPvtHealthInsurances();
    }
    
    public List<RegisteredDiagnosisModel> getDiagnosisModuleItems() {
        return getDiagnosisList();
    }

    public PatientModel getPatient() {
        return patient;
    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    public List<RegisteredDiagnosisModel> getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(List<RegisteredDiagnosisModel> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }
    
    public void setFacility(String f) {
        this.facility = f;
    }
    
    public String getCreatorId() {
        return facility;
    }
    
    public String getCreatorName() {
        return facility;
    }
    
    public String getGenerationPurpose() {
        return "データ移行";
    }
    
    public String getConfirmDate() {
        if (confirmDate==null) {
            confirmDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        }
        return confirmDate;
    }
    
    public String getDocId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
}
