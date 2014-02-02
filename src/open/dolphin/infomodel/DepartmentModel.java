package open.dolphin.infomodel;


import javax.persistence.Embeddable;

/**
 * DepartmentModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Embeddable
public class DepartmentModel extends InfoModel {
    
    private static final long serialVersionUID = -920243869556556218L;
    
    private String department;
    
    private String departmentDesc;
    
    private String departmentCodeSys;
    
    
    public DepartmentModel() {
    }
    
    /**
     * @param departmentCode The departmentCode to set.
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * @return Returns the departmentCode.
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * @param departmentDesc The departmentDesc to set.
     */
    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }
    
    /**
     * @return Returns the departmentDesc.
     */
    public String getDepartmentDesc() {
        return departmentDesc;
    }
    
    /**
     * @param departmentCodeSys The departmentCodeSys to set.
     */
    public void setDepartmentCodeSys(String departmentCodeSys) {
        this.departmentCodeSys = departmentCodeSys;
    }
    
    /**
     * @return Returns the departmentCodeSys.
     */
    public String getDepartmentCodeSys() {
        return departmentCodeSys;
    }
    
    public String toString() {
        return departmentDesc;
    }
}
