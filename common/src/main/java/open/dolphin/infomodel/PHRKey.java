package open.dolphin.infomodel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author kazushi Minagawa
 */
@Entity
@Table(name = "d_phr_key")
public class PHRKey implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    // 医療機関ID
    private String facilityId;
    
    // 患者ID
    private String patientId;
    
    // Access Key
    private String accessKey;
    
    // Secret Key
    private String secretKey;
    
    /*
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastRpDate;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastLabDate;*/
    
    // 登録日
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date registered;
    
    /*
    @Transient
    private String lastRpDateString;
    
    @Transient
    private String lastLabDateString;*/
    
    @Transient
    private String registeredString;
   

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PHRKey)) {
            return false;
        }
        PHRKey other = (PHRKey) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.infomodel.PHRKey[ id=" + id + " ]";
    }

    /**
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the accessKey
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * @param accessKey the accessKey to set
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * @return the secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * @param secretKey the secretKey to set
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * @return the registered
     */
    public Date getRegistered() {
        return registered;
    }

    /**
     * @param registered the registered to set
     */
    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

//    public Date getLastRpDate() {
//        return lastRpDate;
//    }
//
//    public void setLastRpDate(Date lastRpDate) {
//        this.lastRpDate = lastRpDate;
//    }
//
//    public Date getLastLabDate() {
//        return lastLabDate;
//    }
//
//    public void setLastLabDate(Date lastLabDate) {
//        this.lastLabDate = lastLabDate;
//    }
//
//    public String getLastRpDateString() {
//        return lastRpDateString;
//    }
//
//    public void setLastRpDateString(String lastRpDateString) {
//        this.lastRpDateString = lastRpDateString;
//    }
//
//    public String getLastLabDateString() {
//        return lastLabDateString;
//    }
//
//    public void setLastLabDateString(String lastLabDateString) {
//        this.lastLabDateString = lastLabDateString;
//    }

    public String getRegisteredString() {
        return registeredString;
    }

    public void setRegisteredString(String registeredString) {
        this.registeredString = registeredString;
    }
    
    // iOS DateをStringにして返却
    public void dateToString() {
        
//        // 最終処方
//        if (this.getLastRpDate() != null) {
//            this.setLastRpDateString(stringFromDate(this.getLastRpDate()));
//            this.setLastRpDate(null);
//        }
//        
//        // 最終Lab
//        if (this.getLastLabDate() != null) {
//            this.setLastLabDateString(stringFromDate(this.getLastLabDate()));
//            this.setLastLabDate(null);
//        }
        
        // 登録日
        if (this.getRegistered() != null) {
            this.setRegisteredString(stringFromDate(this.getRegistered()));
            this.setRegistered(null);
        }
    }
    
    // iOS StringをDateにして登録
    public void stringToDate() {
        
//        // 最終処方
//        if (this.getLastRpDateString() != null) {
//            this.setLastRpDate(dateFomString(this.getLastRpDateString()));
//            this.setLastRpDateString(null);
//        }
//        
//        // 最終Lab
//        if (this.getLastLabDateString() != null) {
//            this.setLastLabDate(dateFomString(this.getLastLabDateString()));
//            this.setLastLabDateString(null);
//        }
        
        // 登録日
        if (this.getRegisteredString() != null) {
            this.setRegistered(dateFomString(this.getRegisteredString()));
            this.setRegisteredString(null);
        }
    }
    
    private Date dateFomString(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return sdf.parse(str);
        } catch (Exception e) {
        }
        return null;
    }
    
    private String stringFromDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(d);
    }
}
