package open.dolphin.touch.converter;

import open.dolphin.infomodel.ClaimItem;


/**
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc. 
 */
public class IClaimItem implements java.io.Serializable {

    // 名称
    private String name;
    
    // コード
    private String code;
    
    // コード体系
    private String codeSystem;
    
    // 種別コード（薬剤｜手技｜材料）
    private String classCode;
    
    // 種別コード体系
    private String classCodeSystem;
    
    // 数量
    private String number;
    
    // 単位
    private String unit;
    
    // 数量コード
    private String numberCode;
    
    // 数量コード体系
    private String numberCodeSystem;
    
    // メモ
    private String memo;
    
    // 薬剤区分
    private String ykzKbn;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassCodeSystem() {
        return classCodeSystem;
    }

    public void setClassCodeSystem(String classCodeSystem) {
        this.classCodeSystem = classCodeSystem;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNumberCode() {
        return numberCode;
    }

    public void setNumberCode(String numberCode) {
        this.numberCode = numberCode;
    }

    public String getNumberCodeSystem() {
        return numberCodeSystem;
    }

    public void setNumberCodeSystem(String numberCodeSystem) {
        this.numberCodeSystem = numberCodeSystem;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getYkzKbn() {
        return ykzKbn;
    }

    public void setYkzKbn(String ykzKbn) {
        this.ykzKbn = ykzKbn;
    }
    
    public void fromModel(ClaimItem model) {
        this.setName(model.getName());
        this.setCode(model.getCode());
        this.setCodeSystem(model.getCodeSystem());
        this.setClassCode(model.getClassCode());
        this.setClassCodeSystem(model.getClassCodeSystem());
        this.setNumber(model.getNumber());
        this.setUnit(model.getUnit());
        this.setNumberCode(model.getNumberCode());
        this.setNumberCodeSystem(model.getNumberCodeSystem());
        this.setMemo(model.getMemo());
        this.setYkzKbn(model.getYkzKbn());
    }
    
    public ClaimItem toModel() {
        ClaimItem ret = new ClaimItem();
        ret.setName(this.getName());
        ret.setCode(this.getCode());
        ret.setCodeSystem(this.getCodeSystem());
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setNumber(this.getNumber());
        ret.setUnit(this.getUnit());
        ret.setNumberCode(this.getNumberCode());
        ret.setNumberCodeSystem(this.getNumberCodeSystem());
        ret.setMemo(this.getMemo());
        ret.setYkzKbn(this.getYkzKbn());
        return ret;
    }
}