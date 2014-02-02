package open.dolphin.infomodel;


/**
 * UserLiteModel
 * 
 * @author Minagawa,Kazushi
 */
public class UserLiteModel extends InfoModel {
	
    private String userId;
    private String commonName;
    private LicenseModel licenseModel;

    public UserLiteModel(){
    }

    /**
     * @param userId The userId to set.
     */
    public void setUserId(String creatorId) {
            this.userId = creatorId;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
            return userId;
    }

    /**
     * @param name The name to set.
     */
    public void setCommonName(String name) {
            this.commonName = name;
    }

    /**
     * @return Returns the name.
     */
    public String getCommonName() {
            return commonName;
    }

    /**
     * @param licenseModel The licenseModel to set.
     */
    public void setLicenseModel(LicenseModel licenseModel) {
            this.licenseModel = licenseModel;
    }

    /**
     * @return Returns the licenseModel.
     */
    public LicenseModel getLicenseModel() {
            return licenseModel;
    }
}
