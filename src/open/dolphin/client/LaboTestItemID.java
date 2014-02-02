/*
 * LaboTestItemID.java
 *
 * Created on 2003/08/01, 8:51
 */

package open.dolphin.client;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LaboTestItemID implements Comparable {
    
    private String itemCodeID;
    private String itemCode;
    private String itemName;
    
    /** Creates a new instance of LaboTestItemID */
    public LaboTestItemID() {
    }
    
    public LaboTestItemID(String codeID, String code, String name) {
        this();
        
        itemCodeID = codeID;
        itemCode = code;
        itemName = name;
    }
    
    public String getItemCodeID() {
        return itemCodeID;
    }
    
    public void setItemCodeID(String val) {
        itemCodeID = val;
    }

    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String val) {
        itemCode = val;
    } 
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String val) {
        itemName = val;
    }  
    
    public int hashCode() {
        
        return itemCodeID.hashCode() + itemCode.hashCode();
    }
    
    public boolean equals(Object other) {
        
        if (other != null && getClass() == other.getClass()) {
            
            LaboTestItemID sp = (LaboTestItemID)other;
            
            return (itemCodeID.equals(sp.getItemCodeID()) &&
                    itemCode.equals(sp.getItemCode())) ?
                    true : false;
        }
        
        return false;       
    }    
    
    public int compareTo(Object obj) {
        
        LaboTestItemID other = (LaboTestItemID)obj;
        
        int ret = itemCodeID.compareTo(other.getItemCodeID());
        
        return ret == 0 ? itemCode.compareTo(other.getItemCode()) : ret;
    }
    
    public String toString() {
        return itemName;
    }
}
