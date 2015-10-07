package open.dolphin.infomodel;

/**
 *
 * @author kazushi Minagawa
 */
public class Factor2Spec implements java.io.Serializable {
    
    // User's primary key
    private long userPK;
    
    private String code;
    
    private String phoneNumber;
    
    private String deviceName;
    
    private String macAddress;
    
    private String entryDate;
    
    private String factor2Auth;
    
    private String backupKey;
    
    // 信頼デバイス追加時のfid:uid
    private String userId;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getBackupKey() {
        return backupKey;
    }

    public void setBackupKey(String backupKey) {
        this.backupKey = backupKey;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(phoneNumber).append(",").append(macAddress).append(",").append(entryDate).append(",").append(factor2Auth);
        return sb.toString();
    }

    public long getUserPK() {
        return userPK;
    }

    public void setUserPK(long userPK) {
        this.userPK = userPK;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getFactor2Auth() {
        return factor2Auth;
    }

    public void setFactor2Auth(String factor2Auth) {
        this.factor2Auth = factor2Auth;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
