package open.dolphin.infomodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc. 
 */
@Entity
@Table(name = "d_care_plan_item")
public class CarePlanItem extends InfoModel implements java.io.Serializable {
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    // 名称
    private String name;
    
    // コード
    private String code;
    
    // コード体系
    private String codeSystem;
    
    // 種別コード（薬剤｜手技｜材料）
    private String classCode;
    
    // 種別コードn体系
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
    
    // 薬剤区分 2011-02-10 追加
    private String ykzKbn;

    @ManyToOne
    @JoinColumn(name="carePlan_id", nullable=false)
    private CarePlanModel carePlan; // mappedBy

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public CarePlanModel getCarePlan() {
        return carePlan;
    }

    public void setCarePlan(CarePlanModel carePlan) {
        this.carePlan = carePlan;
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
        final CarePlanItem other = (CarePlanItem) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    public ClaimItem toClaimItem() {
        
        ClaimItem result = new ClaimItem();
        
        result.setClassCode(this.getClassCode());
        result.setClassCodeSystem(this.getClassCodeSystem());
        result.setCode(this.getCode());
        result.setCodeSystem(this.getCodeSystem());
        result.setName(this.getName());
        result.setNumber(this.getNumber());
        result.setNumberCode(this.getNumberCode());
        result.setNumberCodeSystem(this.getNumberCodeSystem());
        result.setUnit(this.getUnit());
        result.setYkzKbn(this.getYkzKbn());
        result.setMemo(this.getMemo());
        
        return result;
    }
}