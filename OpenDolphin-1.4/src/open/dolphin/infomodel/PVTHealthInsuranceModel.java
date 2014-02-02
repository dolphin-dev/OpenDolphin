/*
 * PVTHealthInsurance.java
 *
 * Created on 2001/10/10, 13:23
 *
 * Last updated on 2002/12/31.
 * Revised on 2003/01/06 for Null Pointer Exception at item.toString() (publicInsurances).
 * Revised on 2003/01/07 for initializing publicInsurances object first time.
 * Revised on 2003/01/08 renamed pvtPublicInsuranceItem from publicInsurances.
 *                                    added 'insuranceClassCodeTableId' in toString()
 *
 */
// Mirror-i        -start
//package open.dolphin.server;
package open.dolphin.infomodel;

// Mirror-i        -end

//import java.util.logging.*;

//import mirrorI.dolphin.server.PVTServer;

/**
 * Health-Insurance class to be parsed.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-I corp for adding 'PvtPublicInsuranceItem' and related function to store/get PvtPublicInsuranceItem name
 */
public class PVTHealthInsuranceModel extends InfoModel  {
    
    private static final long serialVersionUID = 6119471803755585233L;
    
    private String uuid;
    private String insuranceClass;
    private String insuranceClassCode;
    private String insuranceClassCodeSys;
    private String insuranceNumber;
    private String clientGroup;
    private String clientNumber;
    private String familyClass;
    private String startDate;
    private String expiredDate;
    private String[] continuedDisease;
    private String payInRatio;
    private String payOutRatio;
    
    private PVTPublicInsuranceItemModel[] pvtPublicInsuranceItem;
    
    /** Creates new PVTHealthInsurance */
    public PVTHealthInsuranceModel() {
        super();
    }
    
    public String getGUID() {
        return uuid;
    }
    
    public void setGUID(String val) {
        uuid = val;
    }
    
    public String getInsuranceClass() {
        return insuranceClass;
    }
    
    public void setInsuranceClass(String val) {
        insuranceClass = val;
    }
    
    public String getInsuranceClassCode() {
        return insuranceClassCode;
    }
    
    public void setInsuranceClassCode(String val) {
        insuranceClassCode = val;
    }
    
    public String getInsuranceClassCodeSys() {
        return insuranceClassCodeSys;
    }
    
    public void setInsuranceClassCodeSys(String val) {
        insuranceClassCodeSys = val;
    }
    
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    
    public void setInsuranceNumber(String val) {
        insuranceNumber = val;
    }
    
    public String getClientGroup() {
        return clientGroup;
    }
    
    public void setClientGroup(String val) {
        clientGroup = val;
    }
    
    public String getClientNumber() {
        return clientNumber;
    }
    
    public void setClientNumber(String val) {
        clientNumber = val;
    }
    
    public String getFamilyClass() {
        return familyClass;
    }
    
    public void setFamilyClass(String val) {
        familyClass = val;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    }
    
    public String getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(String val) {
        expiredDate = val;
    }
    
    public String[] getContinuedDisease() {
        return continuedDisease;
    }
    
    public void setContinuedDisease(String[] val) {
        continuedDisease = val;
    }
    
    public void addContinuedDisease(String val) {
        
        int len = 0;
        
        if (continuedDisease == null) {
            continuedDisease = new String[1];
        } else {
            len = continuedDisease.length;
            String[] dest = new String[len + 1];
            System.arraycopy(continuedDisease, 0, dest, 0, len);
            continuedDisease = dest;
        }
        continuedDisease[len] = val;
    }
    
    public String getPayInRatio() {
        return payInRatio;
    }
    
    public void setPayInRatio(String val) {
        payInRatio = val;
    }
    
    public String getPayOutRatio() {
        return payOutRatio;
    }
    
    public void setPayOutRatio(String val) {
        payOutRatio = val;
    }
    
    public PVTPublicInsuranceItemModel[] getPVTPublicInsuranceItem() {
        return pvtPublicInsuranceItem;
    }
    
    public void setPVTPublicInsuranceItem(PVTPublicInsuranceItemModel[] val) {
        pvtPublicInsuranceItem = val;
    }
    
    public void addPvtPublicInsuranceItem(PVTPublicInsuranceItemModel value) {
        if (pvtPublicInsuranceItem == null) {
            pvtPublicInsuranceItem = new PVTPublicInsuranceItemModel[1];
            pvtPublicInsuranceItem[0] = value;
            return;
        }
        int len = pvtPublicInsuranceItem.length;
        PVTPublicInsuranceItemModel[] dest = new PVTPublicInsuranceItemModel[len + 1];
        System.arraycopy(pvtPublicInsuranceItem, 0, dest, 0, len);
        pvtPublicInsuranceItem = dest;
        pvtPublicInsuranceItem[len] = value;
    }
    
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        
        if (insuranceNumber != null && insuranceClass != null ) {
            buf.append(insuranceNumber);
            buf.append("  ");
            buf.append(insuranceClass);
        }
        
        else if (insuranceNumber != null) {
            buf.append(insuranceNumber);
        }
        
        else if (insuranceClass != null) {
            buf.append(insuranceClass);
        }
        
        else {
            buf.append("Ž©”ï");
        }
        
        if (pvtPublicInsuranceItem != null) {
            int len = pvtPublicInsuranceItem.length;
            buf.append(" ");
            for (int i = 0; i < len; i++) {
                PVTPublicInsuranceItemModel item = pvtPublicInsuranceItem[i];
                if (item != null) {
                    if (i != 0) {
                        buf.append("E");
                    }
                    buf.append(item.toString());
                }
            }
            buf.append(" ");
        }
        
        return buf.toString();
    }
}
