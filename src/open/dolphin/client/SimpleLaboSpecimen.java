/*
 * SimpleLaboSpecimen.java
 *
 * Created on 2003/07/29, 21:12
 */

package open.dolphin.client;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SimpleLaboSpecimen implements Comparable {
    
    private String specimenName;
    private String specimenCode;
    private String specimenCodeID;
    private String specimenMemo;
    private String specimenMemoCodeName;
    private String specimenMemoCode;
    private String specimenMemoCodeId;
    private String specimenFreeMemo;
    
    /** Creates a new instance of SimpleLaboSpecimen */
    public SimpleLaboSpecimen() {
    }
    
    public SimpleLaboSpecimen(String name, String code, String codeID) {
        this();
        
        this.specimenName = name;
        this.specimenCode = code;
        this.specimenCodeID = codeID;
    }
    
    public String getSpecimenName() {
        return specimenName;
    }
    
    public void setSpecimenName(String val) {
        specimenName = val;
    }   
    
    public String getSpecimenCode() {
        return specimenCode;
    }
    
    public void setSpecimenCode(String val) {
        specimenCode = val;
    }
    
    public String getSpecimenCodeID() {
        return specimenCodeID;
    }
    
    public void setSpecimenCodeID(String val) {
        specimenCodeID = val;
    } 
    
    public String getSpecimenMemo() {
        return specimenMemo;
    }
    
    public void setSpecimenMemo(String val) {
        specimenMemo = val;
    }
    
    public String getSpecimenMemoCodeName() {
        return specimenMemoCodeName;
    }
    
    public void setSpecimenMemoCodeName(String val) {
        specimenMemoCodeName = val;
    }
    
    public String getSpecimenMemoCode() {
        return specimenMemoCode;
    }
    
    public void setSpecimenMemoCode(String val) {
        specimenMemoCode = val;
    }
    
    public String getSpecimenMemoCodeId() {
        return specimenMemoCodeId;
    }
    
    public void setSpecimenMemoCodeId(String val) {
        specimenMemoCodeId = val;
    }
    
    public String getSpecimenFreeMemo() {
        return specimenFreeMemo;
    }
    
    public void setSpecimenFreeMemo(String val) {
       specimenFreeMemo = val;
    }
    
    public String toString() {
        return specimenName;
    }
    
    public int hashCode() {
        
        return specimenCodeID.hashCode() + specimenCode.hashCode();
    }
    
    public boolean equals(Object other) {
        
        if (other != null && getClass() == other.getClass()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen)other;
            
            return (specimenCodeID.equals(sp.getSpecimenCodeID()) &&
                    specimenCode.equals(sp.getSpecimenCode())) ?
                    true : false;
        }
        
        return false;       
    }
    
    public int compareTo(Object obj) {
        
        SimpleLaboSpecimen other = (SimpleLaboSpecimen)obj;
        
        // コード体系を比較
        int ret = specimenCodeID.compareTo(other.getSpecimenCodeID());
        
        // コード体系が等しい場合はコードを比較
        return ret == 0 ? specimenCode.compareTo(other.getSpecimenCode()) : ret;
    }
}
