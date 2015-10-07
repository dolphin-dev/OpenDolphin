package open.dolphin.adm20.converter;

import open.dolphin.infomodel.ModuleInfoBean;

/**
 * Stamp 及び Module の属性を保持するクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class IModuleInfo implements java.io.Serializable {
    
    // 名称
    private String stampName;
    
    // Role
    private String stampRole;
    
    // Document内の順番
    private int stampNumber;
    
    // 実態
    private String entity;
    
//minagawa^ 入院対応
    private String performFlag;
//minagawa$    
    
    public IModuleInfo() {
    }

    public String getStampName() {
        return stampName;
    }

    public void setStampName(String stampName) {
        this.stampName = stampName;
    }

    public String getStampRole() {
        return stampRole;
    }

    public void setStampRole(String stampRole) {
        this.stampRole = stampRole;
    }

    public int getStampNumber() {
        return stampNumber;
    }

    public void setStampNumber(int stampNumber) {
        this.stampNumber = stampNumber;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
    
    public void fromModel(ModuleInfoBean model) {
        this.setStampName(model.getStampName());
        this.setStampRole(model.getStampRole());
        this.setEntity(model.getEntity());
        this.setStampNumber(model.getStampNumber());
//minagawa^ 入院対応
        this.setPerformFlag(model.getPerformFlag());
//minagawa$          
    }
    
    public ModuleInfoBean toModel() {
        ModuleInfoBean ret = new ModuleInfoBean();
        ret.setStampName(this.getStampName());
        ret.setStampRole(this.getStampRole());
        ret.setEntity(this.getEntity());
        ret.setStampNumber(this.getStampNumber());
//minagawa^ 入院対応
        ret.setPerformFlag(this.getPerformFlag());
//minagawa$           
        return ret;
    }

    public String getPerformFlag() {
        return performFlag;
    }

    public void setPerformFlag(String performFlag) {
        this.performFlag = performFlag;
    }
}
