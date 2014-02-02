package open.dolphin.infomodel;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AdminComentValue
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_admin_comment")
public class AdminComentValue extends InfoModel {
    
    private static final long serialVersionUID = -6626172474055560478L;
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String adminComent;
    
    public AdminComentValue() {
    }
    
    public String getAdminComent() {
        return adminComent;
    }
    
    public void setAdminComent(String adminComent) {
        this.adminComent = adminComent;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + id;
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
        final AdminComentValue other = (AdminComentValue) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
