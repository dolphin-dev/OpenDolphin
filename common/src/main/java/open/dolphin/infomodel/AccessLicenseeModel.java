package open.dolphin.infomodel;

/**
 * AccessLicenseeModel
 *
 * @author Kazushi Minagawa
 *
 */
public class AccessLicenseeModel extends InfoModel {
    
    private String code;
    private String name;
    private String type;
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
}
