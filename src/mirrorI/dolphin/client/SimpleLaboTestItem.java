/*
 * SimpleLaboTestItem.java
 *
 * Created on 2003/07/30, 10:41
 */

package mirrorI.dolphin.client;

import java.awt.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SimpleLaboTestItem implements Comparable {
    
    private String itemCodeID;
    private String itemCode;
    private String itemName;
    private String itemValue;
    private String itemUnit;
    private String low;
    private String up;
    private String normal;
    private String out;
    private String itemMemo;
    private String itemMemoCodeName;
    private String itemMemoCode;
    private String itemMemoCodeId;
    private String itemFreeMemo;
    private String extRef;
    
    /** Creates a new instance of SimpleLaboTestItem */
    public SimpleLaboTestItem() {
    }
    
    public Color getStatusColor() {
        if (out == null) {
            return Color.black;
        }
        
        if (out.equals("L")) {
            return Color.blue;
        } else if (out.equals("N")) {
            return new Color(0,200,0);//Color.green;
        } else if (out.equals("H")) {
            return Color.red;
        } else {
            return Color.black;
        }
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
    
    public String getItemValue() {
        return itemValue;
    }
    
    public void setItemValue(String val) {
        itemValue = val;
    }
    
    public String getItemUnit() {
        return itemUnit;
    }
    
    public void setItemUnit(String val) {
        itemUnit = val;
    } 
    
    public String getLow() {
        return low;
    }
    
    public void setLow(String val) {
        low = val;
    } 
    
    public String getUp() {
        return up;
    }
    
    public void setUp(String val) {
        up = val;
    } 
    
    public String getNormal() {
        return normal;
    }
    
    public void setNormal(String val) {
        normal = val;
    } 
    
    public String getOut() {
        return out;
    }
    
    public void setOut(String val) {
        out = val;
    } 
    
    public String getItemMemo() {
        return itemMemo;
    }
    
    public void setItemMemo(String val) {
        itemMemo = val;
    } 
    
    public String getItemMemoCodeName() {
        return itemMemoCodeName;
    }
    
    public void setItemMemoCodeName(String val) {
        itemMemoCodeName = val;
    } 
    
    public String getItemMemoCode() {
        return itemMemoCode;
    }
    
    public void setItemMemoCode(String val) {
        itemMemoCode = val;
    }
    
    public String getItemMemoCodeId() {
        return itemMemoCodeId;
    }
    
    public void setItemMemoCodeId(String val) {
        itemMemoCodeId = val;
    }
    
    public String getItemFreeMemo() {
        return itemFreeMemo;
    }
    
    public void setItemFreeMemo(String val) {
        itemFreeMemo = val;
    }
    
    public String getExtRef() {
        return extRef;
    }
    
    public void setExtRef(String val) {
        extRef = val;
    }    
    
    public int compareTo(Object o) {
        
        SimpleLaboTestItem other = (SimpleLaboTestItem)o;
        
        return itemCode.compareTo(other.getItemCode());
        //return itemName.compareTo(other.getItemName());
    }
    
    public boolean isTest(LaboTestItemID testID) {
        return ( itemCodeID.equals(testID.getItemCodeID()) && itemCode.equals(testID.getItemCode()) )
               ?
               true : false;
    }
    
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        //buf.append(itemName);
        //buf.append(": ");
        buf.append(itemValue);
        buf.append(" ");
        if (itemUnit != null) {
            buf.append(itemUnit);
        }
        return buf.toString();
    }
    
    /*public String toString() {
        
        StringBuffer buf = new StringBuffer();
        buf.append(itemName);
        buf.append(": ");
        buf.append(itemValue);
        buf.append(" ");
        if (itemUnit != null) {
            buf.append(itemUnit);
        }
        return buf.toString();
    }*/
}
