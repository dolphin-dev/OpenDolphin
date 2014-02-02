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
public class ObservationModel extends KarteEntryBean {
    
    private static final long serialVersionUID = 51827574299906303L;
    
    @Column(nullable = false)
    private String observation;
    
    @Column(nullable = false)
    private String phenomenon;
    
    @Column(name="c_value")
    private String value;
    
    private String unit;
    
    private String categoryValue;
    
    private String valueDesc;
    
    private String valueSys;
    
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
