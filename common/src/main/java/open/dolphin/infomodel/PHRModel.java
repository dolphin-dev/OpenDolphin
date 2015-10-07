package open.dolphin.infomodel;

/**
 *
 * @author kazushi
 */
public class PHRModel implements java.io.Serializable {
    
    // OneToOne
    private String module_Id;

    public String getModule_Id() {
        return module_Id;
    }

    public void setModule_Id(String module_Id) {
        this.module_Id = module_Id;
    }
}
