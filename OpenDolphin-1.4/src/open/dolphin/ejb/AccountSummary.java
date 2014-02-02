package open.dolphin.ejb;

import java.text.DateFormat;
import java.util.Date;

/**
 * AccountSummary
 *
 */
public class AccountSummary implements java.io.Serializable {
    
    private static final long serialVersionUID = 7760417813091593924L;
    
    private String facilityId;
    
    private String facilityName;
    
    private String facilityZipCode;
    
    private String facilityAddress;
    
    private String FacilityTelephone;
    
    private String userName;
    
    private String userId;
    
    private String userEmail;
    
    private String memberType;
    
    private Date registeredDate;
    
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String adminEmail) {
        this.userEmail = adminEmail;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String adminName) {
        this.userName = adminName;
    }
    
    public String getFacilityAddress() {
        return facilityAddress;
    }
    
    public void setFacilityAddress(String facilityAddrees) {
        this.facilityAddress = facilityAddrees;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public String getFacilityTelephone() {
        return FacilityTelephone;
    }
    
    public void setFacilityTelephone(String facilityTelephone) {
        FacilityTelephone = facilityTelephone;
    }
    
    public String getFacilityZipCode() {
        return facilityZipCode;
    }
    
    public void setFacilityZipCode(String facilityZipCode) {
        this.facilityZipCode = facilityZipCode;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }
    
    public String getRdDate() {
        return DateFormat.getDateInstance().format(registeredDate);
        
    }
}
