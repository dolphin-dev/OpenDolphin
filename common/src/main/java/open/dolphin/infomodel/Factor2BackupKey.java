package open.dolphin.infomodel;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author kazushi Minagawa
 */
@Entity
@Table(name = "d_factor2_backupkey")
public class Factor2BackupKey implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private long userPK;
    
    private String backupKey;

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
        if (!(object instanceof Factor2BackupKey)) {
            return false;
        }
        Factor2BackupKey other = (Factor2BackupKey) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return backupKey;
    }

    public long getUserPK() {
        return userPK;
    }

    public void setUserPK(long userPK) {
        this.userPK = userPK;
    }

    public String getBackupKey() {
        return backupKey;
    }

    public void setBackupKey(String backupKey) {
        this.backupKey = backupKey;
    }
}
