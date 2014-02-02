package open.dolphin.infomodel;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * 瀬田クリニック版初診時情報1
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="docType",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("SETA_1")
public class FirstEncounter1Model extends FirstEncounterModel implements java.io.Serializable {
    
    @Transient
    private String disease;
    
    @Transient
    private String metastatic1;
    
    @Transient
    private String metastatic2;
    
    @Transient
    private String metastatic3;
    
    @Transient
    private String metastatic4;
    
    @Transient
    private String tissueType;
    
    @Transient
    private String understandingRank;
    
    @Transient
    private String understanding;
    
    @Transient
    private String t;
    
    @Transient
    private String n;
    
    @Transient
    private String m;
    
    @Transient
    private String stage;
    
    @Transient
    private String firstState;
    
    @Transient
    private String firstPs;
    
    @Transient
    private String totsuRank;
    
    @Transient
    private String sleepRank;
    
    @Transient
    private String mindRank;
    
    @Transient
    private String mealRank;
    
    @Transient
    private String subjectiveSymptom;
    
    @Transient
    private String pastHistory;
    
    
    /** Creates a new instance of FirstEncounter1 */
    public FirstEncounter1Model() {
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMetastatic1() {
        return metastatic1;
    }

    public void setMetastatic1(String metastatic1) {
        this.metastatic1 = metastatic1;
    }

    public String getMetastatic2() {
        return metastatic2;
    }

    public void setMetastatic2(String metastatic2) {
        this.metastatic2 = metastatic2;
    }

    public String getMetastatic3() {
        return metastatic3;
    }

    public void setMetastatic3(String metastatic3) {
        this.metastatic3 = metastatic3;
    }

    public String getMetastatic4() {
        return metastatic4;
    }

    public void setMetastatic4(String metastatic4) {
        this.metastatic4 = metastatic4;
    }

    public String getTissueType() {
        return tissueType;
    }

    public void setTissueType(String tissueType) {
        this.tissueType = tissueType;
    }

    public String getUnderstandingRank() {
        return understandingRank;
    }

    public void setUnderstandingRank(String understandingRank) {
        this.understandingRank = understandingRank;
    }

    public String getUnderstanding() {
        return understanding;
    }

    public void setUnderstanding(String understanding) {
        this.understanding = understanding;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getFirstState() {
        return firstState;
    }

    public void setFirstState(String firstState) {
        this.firstState = firstState;
    }

    public String getFirstPs() {
        return firstPs;
    }

    public void setFirstPs(String firstPs) {
        this.firstPs = firstPs;
    }

    public String getTotsuRank() {
        return totsuRank;
    }

    public void setTotsuRank(String tsuRank) {
        this.totsuRank = tsuRank;
    }

    public String getSleepRank() {
        return sleepRank;
    }

    public void setSleepRank(String sleepRank) {
        this.sleepRank = sleepRank;
    }

    public String getMindRank() {
        return mindRank;
    }

    public void setMindRank(String mindRank) {
        this.mindRank = mindRank;
    }

    public String getMealRank() {
        return mealRank;
    }

    public void setMealRank(String mealRank) {
        this.mealRank = mealRank;
    }

    public String getSubjectiveSymptom() {
        return subjectiveSymptom;
    }

    public void setSubjectiveSymptom(String subjectiveSymptom) {
        this.subjectiveSymptom = subjectiveSymptom;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public void setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
    }
}
