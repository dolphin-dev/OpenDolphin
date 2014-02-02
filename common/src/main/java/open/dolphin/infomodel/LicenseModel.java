package open.dolphin.infomodel;


import javax.persistence.Embeddable;

/**
 * LicenseModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Embeddable
public class LicenseModel extends InfoModel implements java.io.Serializable {
    
    private String license;
    
    private String licenseDesc;
    
    private String licenseCodeSys;

    public LicenseModel() {
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseDesc() {
        return licenseDesc;
    }

    public void setLicenseDesc(String licenseDesc) {
        this.licenseDesc = licenseDesc;
    }
    
    public String getLicenseCodeSys() {
        return licenseCodeSys;
    }
    
    public void setLicenseCodeSys(String licenseCodeSys) {
        this.licenseCodeSys = licenseCodeSys;
    }
    
    @Override
    public String toString() {
        return licenseDesc;
    }
}
