package open.dolphin.infomodel;


import javax.persistence.Embeddable;

/**
 * DepartmentModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Embeddable
public class DepartmentModel extends InfoModel implements java.io.Serializable {
    
    private String department;
    
    private String departmentDesc;
    
    private String departmentCodeSys;
    
    public DepartmentModel() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentDesc() {
        return departmentDesc;
    }

    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }
    
    public String getDepartmentCodeSys() {
        return departmentCodeSys;
    }
    
    public void setDepartmentCodeSys(String departmentCodeSys) {
        this.departmentCodeSys = departmentCodeSys;
    }
    
    @Override
    public String toString() {
        return departmentDesc;
    }
}
