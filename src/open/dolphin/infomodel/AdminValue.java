package open.dolphin.infomodel;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * AdminValue
 *
 * @author Minagawa,kazushi
 *
 */
@Entity
@Table(name = "d_admin")
@NamedQueries({
    @NamedQuery(name="adminClass", query="from AdminValue a where a.hierarchyCode1 >= :hc1 order by a.hierarchyCode1"),
    @NamedQuery(name="admin", query="from AdminValue a where a.hierarchyCode2 like :hc2 order by a.hierarchyCode2"),
    @NamedQuery(name="adminComment", query="from AdminComentValue a"),
    @NamedQuery(name="rdMethod", query="from RadiologyMethodValue r where r.hierarchyCode1 >= :hc1 order by r.hierarchyCode1"),
    @NamedQuery(name="rdComment", query="from RadiologyMethodValue r where r.hierarchyCode2 like :hc2 order by r.hierarchyCode2")
})
public class AdminValue extends InfoModel {
    
    private static final long serialVersionUID = -193437910248093182L;
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String hierarchyCode1;
    
    private String hierarchyCode2;
    
    private String name;
    
    private String code;
    
    private String claimClassCode;
    
    private String numberCode;
    
    private String displayName;
    
    public AdminValue() {
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String adminName) {
        this.name = adminName;
    }
    
    public String getClaimClassCode() {
        return claimClassCode;
    }
    
    public void setClaimClassCode(String claimClassCode) {
        this.claimClassCode = claimClassCode;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getHierarchyCode1() {
        return hierarchyCode1;
    }
    
    public void setHierarchyCode1(String hierarchyCode1) {
        this.hierarchyCode1 = hierarchyCode1;
    }
    
    public String getHierarchyCode2() {
        return hierarchyCode2;
    }
    
    public void setHierarchyCode2(String hierarchyCode2) {
        this.hierarchyCode2 = hierarchyCode2;
    }
    
    public String getNumberCode() {
        return numberCode;
    }
    
    public void setNumberCode(String numberCode) {
        this.numberCode = numberCode;
    }
    
    public String toString() {
        return name;
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
        final AdminValue other = (AdminValue) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
}
