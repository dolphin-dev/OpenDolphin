package open.dolphin.infomodel;

/**
 * AccessRightModel
 *
 * @author  Kazushi Minagawa
 */
public class AccessRightModel extends InfoModel {
    
    private String permission;
    private String startDate;
    private String endDate;
    private String licenseeCode;
    private String licenseeName;
    private String licenseeCodeType;
    
    /** Creates a new instance of AccessRight */
    public AccessRightModel() {
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String val) {
        permission = val;
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
    
    public void setLicenseeCode(String licenseeCode) {
        this.licenseeCode = licenseeCode;
    }
    
    public String getLicenseeCode() {
        return licenseeCode;
    }
    
    public void setLicenseeName(String licenseeName) {
        this.licenseeName = licenseeName;
    }
    
    public String getLicenseeName() {
        return licenseeName;
    }
    
    public void setLicenseeCodeType(String licenseeCodeType) {
        this.licenseeCodeType = licenseeCodeType;
    }
    
    public String getLicenseeCodeType() {
        return licenseeCodeType;
    }
}
