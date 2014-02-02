package open.dolphin.infomodel;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * 瀬田クリニック版新規診療録情報（除く患者基本情報）
 */
@Entity
@DiscriminatorValue("SETA_0")
public class FirstEncounter0Model extends FirstEncounterModel {
    
    @Transient
    private String occupation;
    
    @Transient
    private String occupationStatus;
    
    @Transient
    private String otherOccupationStatus;
    
    @Transient
    private String otherContactAddress;
    
    @Transient
    private String otherContactPerson;
    
    @Transient
    private String otherContactRelation;
    
    @Transient
    private String otherContactPhone;
    
    @Transient
    private String currentHospital;
    
    @Transient
    private String currentDept;
    
    @Transient
    private String currentDoctor;
    
    @Transient
    private boolean thisPerosnVisit;
    
    @Transient
    private String otherVisitorsName1;
    
    @Transient
    private String otherVisitorsRelation1;
    
    @Transient
    private String otherVisitorsName2;
    
    @Transient
    private String otherVisitorsRelation2;
    
    @Transient
    private String otherVisitorsName3;
    
    @Transient
    private String otherVisitorsRelation3;
    
    @Transient
    private String aboBloodType;
    
    @Transient
    private String rhdBloodType;
    
    @Transient
    private String infection;
    
    @Transient
    private String culture1;
    
    @Transient
    private String toyoryou1;
    
    @Transient
    private String culture2;
    
    @Transient
    private String toyoryou2;
    
    @Transient
    private String culture3;
    
    @Transient
    private String toyoryou3;
    
    /** Creates a new instance of FirstEncounter0 */
    public FirstEncounter0Model() {
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOccupationStatus() {
        return occupationStatus;
    }

    public void setOccupationStatus(String occupationStatus) {
        this.occupationStatus = occupationStatus;
    }

    public String getOtherOccupationStatus() {
        return otherOccupationStatus;
    }

    public void setOtherOccupationStatus(String otherOccupationStatus) {
        this.otherOccupationStatus = otherOccupationStatus;
    }

    public String getOtherContactAddress() {
        return otherContactAddress;
    }

    public void setOtherContactAddress(String otherContactAddress) {
        this.otherContactAddress = otherContactAddress;
    }

    public String getOtherContactPerson() {
        return otherContactPerson;
    }

    public void setOtherContactPerson(String otherContactPerson) {
        this.otherContactPerson = otherContactPerson;
    }

    public String getOtherContactRelation() {
        return otherContactRelation;
    }

    public void setOtherContactRelation(String otherContactRelation) {
        this.otherContactRelation = otherContactRelation;
    }

    public String getOtherContactPhone() {
        return otherContactPhone;
    }

    public void setOtherContactPhone(String otherContactPhone) {
        this.otherContactPhone = otherContactPhone;
    }

    public String getCurrentHospital() {
        return currentHospital;
    }

    public void setCurrentHospital(String currentHospital) {
        this.currentHospital = currentHospital;
    }

    public String getCurrentDept() {
        return currentDept;
    }

    public void setCurrentDept(String currentDept) {
        this.currentDept = currentDept;
    }

    public String getCurrentDoctor() {
        return currentDoctor;
    }

    public void setCurrentDoctor(String currentDoctor) {
        this.currentDoctor = currentDoctor;
    }

    public boolean isThisPerosnVisit() {
        return thisPerosnVisit;
    }

    public void setThisPerosnVisit(boolean thisPerosnVisit) {
        this.thisPerosnVisit = thisPerosnVisit;
    }

    public String getOtherVisitorsName1() {
        return otherVisitorsName1;
    }

    public void setOtherVisitorsName1(String otherVisitorsName1) {
        this.otherVisitorsName1 = otherVisitorsName1;
    }

    public String getOtherVisitorsRelation1() {
        return otherVisitorsRelation1;
    }

    public void setOtherVisitorsRelation1(String otherVisitorsRelation1) {
        this.otherVisitorsRelation1 = otherVisitorsRelation1;
    }

    public String getOtherVisitorsName2() {
        return otherVisitorsName2;
    }

    public void setOtherVisitorsName2(String otherVisitorsName2) {
        this.otherVisitorsName2 = otherVisitorsName2;
    }

    public String getOtherVisitorsRelation2() {
        return otherVisitorsRelation2;
    }

    public void setOtherVisitorsRelation2(String otherVisitorsRelation2) {
        this.otherVisitorsRelation2 = otherVisitorsRelation2;
    }

    public String getOtherVisitorsName3() {
        return otherVisitorsName3;
    }

    public void setOtherVisitorsName3(String otherVisitorsName3) {
        this.otherVisitorsName3 = otherVisitorsName3;
    }

    public String getOtherVisitorsRelation3() {
        return otherVisitorsRelation3;
    }

    public void setOtherVisitorsRelation3(String otherVisitorsRelation3) {
        this.otherVisitorsRelation3 = otherVisitorsRelation3;
    }

    public String getABOBloodType() {
        return aboBloodType;
    }

    public void setABOBloodType(String bloodType) {
        this.aboBloodType = bloodType;
    }

    public String getInfection() {
        return infection;
    }

    public void setInfection(String infection) {
        this.infection = infection;
    }

    public String getCulture1() {
        return culture1;
    }

    public void setCulture1(String culture1) {
        this.culture1 = culture1;
    }

    public String getToyoryou1() {
        return toyoryou1;
    }

    public void setToyoryou1(String toyoryou1) {
        this.toyoryou1 = toyoryou1;
    }

    public String getCulture2() {
        return culture2;
    }

    public void setCulture2(String culture2) {
        this.culture2 = culture2;
    }

    public String getToyoryou2() {
        return toyoryou2;
    }

    public void setToyoryou2(String toyoryou2) {
        this.toyoryou2 = toyoryou2;
    }

    public String getCulture3() {
        return culture3;
    }

    public void setCulture3(String culture3) {
        this.culture3 = culture3;
    }

    public String getToyoryou3() {
        return toyoryou3;
    }

    public void setToyoryou3(String toyoryou3) {
        this.toyoryou3 = toyoryou3;
    }

    public String getRHDBloodType() {
        return rhdBloodType;
    }

    public void setRHDBloodType(String rhdBloodType) {
        this.rhdBloodType = rhdBloodType;
    }
}
