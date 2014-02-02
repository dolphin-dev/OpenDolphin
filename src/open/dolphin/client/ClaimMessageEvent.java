package open.dolphin.client;

/**
 * CLAIM インスタンスを通知するイベント。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimMessageEvent extends java.util.EventObject {
    
    private String patientId;
    private String patientName;
    private String patientSex;
    private String title;
    private String instance;
    private int number;
    private String confirmDate;
    
    /** Creates new ClaimEvent */
    public ClaimMessageEvent(Object source) {
        super(source);
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String val) {
        patientId = val;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String val) {
        patientName = val;
    }
    
    public String getPatientSex() {
        return patientSex;
    }
    
    public void setPatientSex(String val) {
        patientSex = val;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        title = val;
    }
    
    public String getClaimInsutance() {
        return instance;
    }
    
    public void setClaimInstance(String val) {
        instance = val;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int val) {
        number = val;
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }
}