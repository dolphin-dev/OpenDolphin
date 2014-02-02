package open.dolphin.infomodel;

import javax.persistence.*;

/**
 * StampModel
 *
 * @author Minagawa,Kazushi
 */
@Entity
@Table(name="d_stamp")
public class StampModel extends InfoModel implements java.io.Serializable {
    
    @Id
    private String id;
    
    // UserPK
    @Column(nullable=false)
    private long userId;
    
    @Column(nullable=false)
    private String entity;
    
    @Column(nullable=false)
    @Lob
    private byte[] stampBytes;
    
//    @Version
//    private int version;
    
    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    /**
     * @param userId The userId to set.
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }
    /**
     * @return Returns the userId.
     */
    public long getUserId() {
        return userId;
    }
    /**
     * @param category The category to set.
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }
    /**
     * @return Returns the category.
     */
    public String getEntity() {
        return entity;
    }
    /**
     * @param stampXml The stampXml to set.
     */
    public void setStampBytes(byte[] stampBytes) {
        this.stampBytes = stampBytes;
    }
    /**
     * @return Returns the stampXml.
     */
    public byte[] getStampBytes() {
        return stampBytes;
    }
    
//    public int getVersion() {
//        return version;
//    }
//    
//    public void setVersion(int version) {
//        this.version = version;
//    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
    
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final StampModel other = (StampModel) obj;
        if (!id.equals(other.id)) {
            return false;
        }
        
        return true;
    }
}
