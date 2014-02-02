package open.dolphin.infomodel;

/**
 * AccessRightModel
 *
 * @author  Kazushi Minagawa
 */
public class AccessRightModel extends InfoModel {
    
    // 許可
    private String permission;
    
    // 開始日
    private String startDate;
    
    // 終了日
    private String endDate;
    
    // 医療資格コード
    private String licenseeCode;
    
    // 医療資格名
    private String licenseeName;
    
    // 医療資格コード体系
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
