package open.dolphin.infomodel;

/**
 *
 * @author Kazushi Minagawa.
 */
public class CodeNamePack extends InfoModel {
    
    private String code;
    
    private String name;
    
    public CodeNamePack() {
    }
    
    public CodeNamePack(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
