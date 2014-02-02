package open.dolphin.infomodel;

import javax.persistence.*;

/**
 * Digital Globe OID.
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name="d_oid")
public class DgOid extends InfoModel implements java.io.Serializable {
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @Column(nullable=false)
    private String baseOid;
    
    @Column(nullable=false)
    private int nextNumber;
    
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getBaseOid() {
        return baseOid;
    }
    
    public void setBaseOid(String baseOid) {
        this.baseOid = baseOid;
    }
    
    public int getNextNumber() {
        return nextNumber;
    }
    
    public void setNextNumber(int nextOid) {
        this.nextNumber = nextOid;
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
        final DgOid other = (DgOid) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
