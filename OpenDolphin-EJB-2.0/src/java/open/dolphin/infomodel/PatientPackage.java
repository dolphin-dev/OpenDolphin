package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class PatientPackage implements java.io.Serializable {

    private PatientModel patient;

    private List<HealthInsuranceModel> insurances;

    private List<AllergyModel> allergies;

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
     * @return the insurances
     */
    public List<HealthInsuranceModel> getInsurances() {
        return insurances;
    }

    /**
     * @param insurances the insurances to set
     */
    public void setInsurances(List<HealthInsuranceModel> insurances) {
        this.insurances = insurances;
    }

    /**
     * @return the allergies
     */
    public List<AllergyModel> getAllergies() {
        return allergies;
    }

    /**
     * @param allergies the allergies to set
     */
    public void setAllergies(List<AllergyModel> allergies) {
        this.allergies = allergies;
    }

}
