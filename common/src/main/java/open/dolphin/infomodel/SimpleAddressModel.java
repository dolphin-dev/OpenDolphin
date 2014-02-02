package open.dolphin.infomodel;


import javax.persistence.Embeddable;

/**
 * SimpleAddressModel
 *
 * @author kazm
 *
 */
@Embeddable
public class SimpleAddressModel extends InfoModel implements java.io.Serializable {
    
    private String zipCode;
    
    private String address;
    
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}
