package open.dolphin.order;

/**
 * Class to hold selected master item information.
 *
 * @author  Kazuhi Minagawa, Digital Globe, Inc.
 */
public class MasterItem implements java.io.Serializable {
    
    private static final long serialVersionUID = -6359300744722498857L;
    
    /** Claim subclass code マスタ項目の種別 */
    // 0: 手技  1: 材料  2: 薬剤 3: 用法 4: 部位
    private int classCode = -1;
    
    /** 項目名 */
    private String name;
    
    /** 項目コード */
    private String code;
    
    /** コード体系名 */
    private String masterTableId;
    
    /** 数量 */
    private String number;
    
    /** 単位 */
    private String unit;
    
    /** 医事用病名コード */
    private String claimDiseaseCode;
    
    /** 診療行為区分(007)・点数集計先 */
    private String claimClassCode;
    
    /** 薬剤の場合の区分 内用1、外用6、注射薬4 */
    private String ykzKbn;
    
    private String dummy;
    
    private String bundleNumber;
    
    
    /**
     * Creates new MasterItem
     */
    public MasterItem() {
    }
    
    public MasterItem(int classCode, String name, String code) {
        this();
        setClassCode(classCode);
        setName(name);
        setCode(code);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    /**
     * @param classCode The classCode to set.
     */
    public void setClassCode(int classCode) {
        this.classCode = classCode;
    }
    
    /**
     * @return Returns the classCode.
     */
    public int getClassCode() {
        return classCode;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }
    
    /**
     * @param masterTableId The masterTableId to set.
     */
    public void setMasterTableId(String masterTableId) {
        this.masterTableId = masterTableId;
    }
    
    /**
     * @return Returns the masterTableId.
     */
    public String getMasterTableId() {
        return masterTableId;
    }
    
    /**
     * @param number The number to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }
    
    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return number;
    }
    
    /**
     * @param unit The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    /**
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * @param claimDiseaseCode The claimDiseaseCode to set.
     */
    public void setClaimDiseaseCode(String claimDiseaseCode) {
        this.claimDiseaseCode = claimDiseaseCode;
    }
    
    /**
     * @return Returns the claimDiseaseCode.
     */
    public String getClaimDiseaseCode() {
        return claimDiseaseCode;
    }
    
    /**
     * @param claimClassCode The claimClassCode to set.
     */
    public void setClaimClassCode(String claimClassCode) {
        this.claimClassCode = claimClassCode;
    }
    
    /**
     * @return Returns the claimClassCode.
     */
    public String getClaimClassCode() {
        return claimClassCode;
    }

    public String getYkzKbn() {
        return ykzKbn;
    }

    public void setYkzKbn(String ykzKbn) {
        this.ykzKbn = ykzKbn;
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public String getBundleNumber() {
        return bundleNumber;
    }

    public void setBundleNumber(String bundleNumber) {
        this.bundleNumber = bundleNumber;
    }
}