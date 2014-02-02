package open.dolphin.client;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DiagnosisEntry {
    
    private String uid;
    private String diagnosis;
    private String category;
    private String outcome;
    private String firstEncounterDate;
    private String startDate;
    private String endDate;
    private String confirmDate;
    private String firstConfirmDate;
    private boolean modified;
    
    /** Creates a new instance of DiagnosisEntry */
    public DiagnosisEntry() {
    }
    
    public String getUID() {
        return uid;
    }
    
    public void setUID(String val) {
        uid = val;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String val) {
        diagnosis = val;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String val) {
        category = val;
    }   
    
    public String getOutcome() {
        return outcome;
    }
    
    public void setOutcome(String val) {
        outcome = val;
    }  
    
    public String getFirstEncounterDate() {
        return firstEncounterDate;
    }
    
    public void setFirstEncounterDate(String val) {
        firstEncounterDate = val;
    } 
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    } 
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String val) {
        endDate = val;
    } 
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean val) {
        modified = val;
    }  
    
    public String getFirstConfirmDate() {
        return firstConfirmDate;
    }
    
    public void setFirstConfirmDate(String val) {
        firstConfirmDate = val;
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }    
}