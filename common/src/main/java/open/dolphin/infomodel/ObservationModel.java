package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Onservation
 *
 * @author Minagawa, Kazushi
 *
 */
@Entity
@Table(name = "d_observation")
public class ObservationModel extends KarteEntryBean implements java.io.Serializable {
    
    // Observation 名
    @Column(nullable = false)
    private String observation;
    
    // 現象型
    @Column(nullable = false)
    private String phenomenon;
    
    // 値
    @Column(name="c_value")
    private String value;
    
    // 単位
    private String unit;
    
    // カテゴリー値
    private String categoryValue;
    
    // 値の説明
    private String valueDesc;
    
    // 値のコード体系
    private String valueSys;
    
    // メモ
    private String memo;
    
    
    public String getObservation() {
        return observation;
    }
    
    public void setObservation(String observation) {
        this.observation = observation;
    }
    
    public String getPhenomenon() {
        return phenomenon;
    }
    
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getCategoryValue() {
        return categoryValue;
    }
    
    public void setCategoryValue(String category) {
        this.categoryValue = category;
    }
    
    public String getValueDesc() {
        return valueDesc;
    }
    
    public void setValueDesc(String valueDesc) {
        this.valueDesc = valueDesc;
    }
    
    public String getValueSys() {
        return valueSys;
    }
    
    public void setValueSys(String valueSys) {
        this.valueSys = valueSys;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
}
