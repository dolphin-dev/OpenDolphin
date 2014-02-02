package open.dolphin.infomodel;

import javax.persistence.*;

/**
 * RoleModel
 *
 * @author Minagawa,Kazushi
 */
@Entity
@Table(name="d_roles")
public class RoleModel extends InfoModel implements java.io.Serializable {
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @Column(name="user_id", nullable=false)
    private String userId;
    
    @Column(name="c_role", nullable=false)
    private String role;
    
    @ManyToOne
    @JoinColumn(name="c_user", nullable=false)
    private UserModel user;

    public RoleModel() {
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public UserModel getUserModel() {
        return user;
    }
    
    public void setUserModel(UserModel user) {
        this.user = user;
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
        final RoleModel other = (RoleModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
