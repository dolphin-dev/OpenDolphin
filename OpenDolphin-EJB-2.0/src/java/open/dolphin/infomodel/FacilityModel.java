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
public class FacilityModel extends InfoModel implements java.io.Serializable {
    
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

    private String facsimile;
    
    private String url;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.DATE)
    private Date registeredDate;
    
    @Column(nullable= false)
    private String memberType;

    private String s3URL;

    private String s3AccessKey;

    private String s3SecretKey;

    /**
     * FacilityModelオブジェクトをせいせいする。
     */
    public FacilityModel() {
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
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

    public void setFacilityName(String name) {
        this.facilityName = name;
    }

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

    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFacsimile() {
        return facsimile;
    }

    public void setFacsimile(String facsimile) {
        this.facsimile = facsimile;
    }

    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }
    
    public String getMemberType() {
        return memberType;
    }
        
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getS3URL() {
        return s3URL;
    }

    public void setS3URL(String s3URL) {
        this.s3URL = s3URL;
    }

    public String getS3AccessKey() {
        return s3AccessKey;
    }

    public void setS3AccessKey(String s3AccessKey) {
        this.s3AccessKey = s3AccessKey;
    }

    public String getS3SecretKey() {
        return s3SecretKey;
    }

    public void setS3SecretKey(String s3SecretKey) {
        this.s3SecretKey = s3SecretKey;
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