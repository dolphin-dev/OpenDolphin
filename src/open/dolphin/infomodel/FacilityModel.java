package open.dolphin.infomodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * FacilityModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_facility")
public class FacilityModel extends InfoModel {
    
    private static final long serialVersionUID = 3142760011378628588L;
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    /** Business Key */
    @Column(nullable=false, unique=true)
    private String facilityId;
    
    @Column(nullable=false)
    private String facilityName;
    
    @Column(nullable=false)
    private String zipCode;
    
    @Column(nullable=false)
    private String address;
    
    @Column(nullable=false)
    private String telephone;
    
    private String url;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.DATE)
    private Date registeredDate;
    
    @Column(nullable= false)
    private String memberType;
    
    
    /**
     * Id‚ð•Ô‚·B
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * Id‚ðÝ’è‚·‚éB
     * @param id Database pk
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * FacilityModelƒIƒuƒWƒFƒNƒg‚ð‚¹‚¢‚¹‚¢‚·‚éB
     */
    public FacilityModel() {
    }
    
    /**
     * Ž{ÝID‚ðÝ’è‚·‚éB
     *
     * @param facilityId
     *            Ž{ÝID
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    /**
     * Ž{ÝID‚ð•Ô‚·B
     *
     * @return Ž{ÝID
     */
    public String getFacilityId() {
        return facilityId;
    }
    
    /**
     * Ž{Ý–¼‚ðÝ’è‚·‚éB
     *
     * @param name
     *            Ž{Ý–¼
     */
    public void setFacilityName(String name) {
        this.facilityName = name;
    }
    
    /**
     * Ž{Ý–¼‚ð•Ô‚·B
     *
     * @return Ž{Ý–¼
     */
    public String getFacilityName() {
        return facilityName;
    }
    
    /**
     * —X•Ö”Ô†‚ðÝ’è‚·‚éB
     *
     * @param zipCode
     *            —X•Ö”Ô†
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    /**
     * —X•Ö”Ô†‚ð•Ô‚·B
     *
     * @return —X•Ö”Ô†
     */
    public String getZipCode() {
        return zipCode;
    }
    
    /**
     * ZŠ‚ðÝ’è‚·‚éB
     *
     * @param address
     *            ZŠ
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * ZŠ‚ð•Ô‚·B
     *
     * @return ZŠ
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * “d˜b”Ô†‚ðÝ’è‚·‚éB
     *
     * @param telephone
     *            “d˜b”Ô†
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    /**
     * “d˜b”Ô†‚ð•Ô‚·B
     *
     * @return “d˜b”Ô†
     */
    public String getTelephone() {
        return telephone;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * “o˜^“ú‚ðÝ’è‚·‚éB
     *
     * @param registeredDate
     *            “o˜^“ú
     */
    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }
    
    /**
     * “o˜^“ú‚ð•Ô‚·B
     *
     * @return “o˜^“ú
     */
    public Date getRegisteredDate() {
        return registeredDate;
    }
        
    /**
     * ƒƒ“ƒo[ƒ^ƒCƒv‚ðÝ’è‚·‚éB
     * @param memberType ƒƒ“ƒo[ƒ^ƒCƒv
     */
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }
    
    /**
     * ƒƒ“ƒo[ƒ^ƒCƒv‚ð•Ô‚·B
     * @return ƒƒ“ƒo[ƒ^ƒCƒv
     */
    public String getMemberType() {
        return memberType;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FacilityModel other = (FacilityModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}