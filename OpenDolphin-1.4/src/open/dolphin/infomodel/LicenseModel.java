package open.dolphin.infomodel;


import javax.persistence.Embeddable;

/**
 * LicenseModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Embeddable
public class LicenseModel extends InfoModel {
    
    private static final long serialVersionUID = 5120402348495916132L;
    
    private String license;
    
    private String licenseDesc;
    
    private String licenseCodeSys;
    
    /**
     * @param license The license to set.
     */
    public void setLicense(String license) {
        this.license = license;
    }
    /**
     * @return Returns the license.
     */
    public String getLicense() {
        return license;
    }
    /**
     * @param licenseDesc The licenseDesc to set.
     */
    public void setLicenseDesc(String licenseDesc) {
        this.licenseDesc = licenseDesc;
    }
    /**
     * @return Returns the licenseDesc.
     */
    public String getLicenseDesc() {
        return licenseDesc;
    }
    /**
     * @param licenseCodeSys The licenseCodeSys to set.
     */
    public void setLicenseCodeSys(String licenseCodeSys) {
        this.licenseCodeSys = licenseCodeSys;
    }
    /**
     * @return Returns the licenseCodeSys.
     */
    public String getLicenseCodeSys() {
        return licenseCodeSys;
    }
    
    public String toString() {
        return licenseDesc;
    }
}
