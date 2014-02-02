/*
 * PVTClaim.java
 *
 * Created on 2001/10/10, 13:57
 *
 * Last updated on 2002/12/31
 *
 */
package open.dolphin.infomodel;


import java.util.Vector;

/**
 * Simple Claim　Class used for PVT.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-I corp for adding 'claimDeptName' and related function to store/get Department name
 *
 */
public class PVTClaim extends InfoModel {
    
    private static final long serialVersionUID = -8573272136025043849L;
    private String claimStatus;
    private String claimRegistTime;
    private String claimAdmitFlag;
    private String claimDeptName;
    private String claimDeptCode;
    private String assignedDoctorId;
    private String assignedDoctorName;
    private Vector claimAppName;
    private String claimAppMemo;
    private String claimItemCode;
    private String claimItemName;
    private String insuranceUid;
    private String jmariCode;
    
    /** Creates new PVTClaim */
    public PVTClaim() {
        super();
    }
    
    public String getClaimStatus() {
        return claimStatus;
    }
    
    public void setClaimStatus(String val) {
        claimStatus = val;
    }
    
    public String getClaimRegistTime() {
        return claimRegistTime;
    }
    
    public void setClaimRegistTime(String val) {
        claimRegistTime = val;
    }
    
    public String getClaimAdmitFlag() {
        return claimAdmitFlag;
    }
    
    public void setClaimAdmitFlag(String val) {
        claimAdmitFlag = val;
    }
    
    public String getClaimDeptName() {
        return claimDeptName;
    }
    
    public void setClaimDeptName(String val) {
        claimDeptName = val;
    }
    
    public String getClaimDeptCode() {
        return claimDeptCode;
    }
    
    public void setClaimDeptCode(String val) {
        claimDeptCode = val;
    }
    
    
    public Vector getClaimAppName() {
        return claimAppName;
    }
    
    @SuppressWarnings("unchecked")
    public void addClaimAppName(String val) {
        if (claimAppName == null) {
            claimAppName = new Vector(3);
        }
        claimAppName.add(val);
    }
    
    public String getClaimAppMemo() {
        return claimAppMemo;
    }
    
    public void setClaimAppMemo(String val) {
        claimAppMemo = val;
    }
    
    public String getClaimItemCode() {
        return claimItemCode;
    }
    
    public void setClaimItemCode(String val) {
        claimItemCode = val;
    }
    
    public String getClaimItemName() {
        return claimItemName;
    }
    
    public void setClaimItemName(String val) {
        claimItemName = val;
    }
    
    @Override
    public String toString() {
        
        StringBuilder buf = new StringBuilder();
        
        if (claimStatus != null) {
            buf.append("ClaimStatus: ");
            buf.append(claimStatus);
            buf.append("\n");
        }
        
        if (claimRegistTime != null) {
            buf.append("ClaimRegistTime: ");
            buf.append(claimRegistTime);
            buf.append("\n");
        }
        
        if (claimAdmitFlag != null) {
            buf.append("ClaimAdmitFlag: ");
            buf.append(claimAdmitFlag);
            buf.append("\n");
        }
        
        // Mirror-I start
        if (claimDeptName != null) {
            buf.append("claimDeptName: ");
            buf.append(claimDeptName);
            buf.append("\n");
        }
        // Mirror-I end
        
        if (claimAppName != null) {
            int len = claimAppName.size();
            for (int i = 0; i < len; i++) {
                buf.append("ClaimAppName: ");
                buf.append((String)claimAppName.get(i));
                buf.append("\n");
            }
        }
        
        if (claimAppMemo != null) {
            buf.append("ClaimAppointMemo: ");
            buf.append(claimAppMemo);
            buf.append("\n");
        }
        
        if (claimItemCode != null) {
            buf.append("ClaimItemCode: ");
            buf.append(claimItemCode);
            buf.append("\n");
        }
        
        if (claimItemName != null) {
            buf.append("ClaimItemName: ");
            buf.append(claimItemName);
            buf.append("\n");
        }
        
        return buf.toString();
    }
    
    /**
     * @param insuranceUid The insuranceUid to set.
     */
    public void setInsuranceUid(String insuranceUid) {
        this.insuranceUid = insuranceUid;
    }
    
    /**
     * @return Returns the insuranceUid.
     */
    public String getInsuranceUid() {
        return insuranceUid;
    }
    
    public String getJmariCode() {
        return jmariCode;
    }
    
    public void setJmariCode(String jmariCode) {
        this.jmariCode = jmariCode;
    }

    public String getAssignedDoctorName() {
        return assignedDoctorName;
    }

    public void setAssignedDoctorName(String assignedDoctorName) {
        this.assignedDoctorName = assignedDoctorName;
    }
    
    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }
}
