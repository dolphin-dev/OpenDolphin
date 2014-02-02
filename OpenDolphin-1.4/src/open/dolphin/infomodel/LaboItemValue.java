package open.dolphin.infomodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * LaboItemValue
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_labo_item")
public class LaboItemValue extends InfoModel {
    
    private static final long serialVersionUID = -6582278876758760228L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    @JoinColumn(name="specimen_id", nullable=false)
    private LaboSpecimenValue laboSpecimen;
    
    private String itemName;
    
    private String itemCode;
    
    private String itemCodeId;
    
    private String acode;
    
    private String icode;
    
    private String scode;
    
    private String mcode;
    
    private String rcode;
    
    private String itemValue;
    
    private String up;
    
    private String low;
    
    private String normal;
    
    private String nout;
    
    private String unit;
    
    private String unitCode;
    
    private String unitCodeId;
    
    
    public LaboItemValue() {
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public LaboSpecimenValue getLaboSpecimen() {
        return laboSpecimen;
    }
    
    public void setLaboSpecimen(LaboSpecimenValue laboSpecimen) {
        this.laboSpecimen = laboSpecimen;
    }
    
    public String getAcode() {
        return acode;
    }
    
    public void setAcode(String acode) {
        this.acode = acode;
    }
    
    public String getIcode() {
        return icode;
    }
    
    public void setIcode(String icode) {
        this.icode = icode;
    }
    
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    
    public String getItemCodeId() {
        return itemCodeId;
    }
    
    public void setItemCodeId(String itemCodeId) {
        this.itemCodeId = itemCodeId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemValue() {
        return itemValue;
    }
    
    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
    
    public String getLow() {
        return low;
    }
    
    public void setLow(String low) {
        this.low = low;
    }
    
    public String getMcode() {
        return mcode;
    }
    
    public void setMcode(String mcode) {
        this.mcode = mcode;
    }
    
    public String getNormal() {
        return normal;
    }
    
    public void setNormal(String normal) {
        this.normal = normal;
    }
    
    public String getNout() {
        return nout;
    }
    
    public void setNout(String nout) {
        this.nout = nout;
    }
    
    public String getRcode() {
        return rcode;
    }
    
    public void setRcode(String rcode) {
        this.rcode = rcode;
    }
    
    public String getScode() {
        return scode;
    }
    
    public void setScode(String scode) {
        this.scode = scode;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getUnitCode() {
        return unitCode;
    }
    
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    
    public String getUnitCodeId() {
        return unitCodeId;
    }
    
    public void setUnitCodeId(String unitCodeId) {
        this.unitCodeId = unitCodeId;
    }
    
    public String getUp() {
        return up;
    }
    
    public void setUp(String up) {
        this.up = up;
    }
}
