/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * バイタル対応
 * 
 * @author Life Sciences Computing Corporation.
 */
@Entity
@Table(name="d_vital")
public class VitalModel extends InfoModel implements java.io.Serializable, Comparable {
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    // 施設ID:患者ID
    @Column(nullable=false)
    private String facilityPatId;
    
    // カルテID
    private String karteID;
    
    // 体温 ℃
    private String bodyTemperature;
    
    // 血圧（収縮） mmHg
    private String bloodPressureSystolic;
    
    // 血圧（拡張） mmHg
    private String bloodPressureDiastolic;
    
    // 心拍数 回/分
    private String pulseRate;
    
    // SpO2 %
    private String spo2;
    
    // 呼吸数 回/分
    private String respirationRate;
    
    // 疼痛 5段階
    private String algia;
    
    // 気分 5段階
    private String feel;
    
    // 睡眠 5段階
    private String sleep;
    
    // 食事 5段階
    private String meal;
    
    // 排泄 5段階
    private String egestion;
    
    // PS 5段階
    private String ps;
    
    // 日付(yyyy-MM-dd)
    private String vitalDate;
    
    // 時間(HH:mm:ss)
    private String vitalTime;
    
    // 身長 cm
    private String height;
    
    // 体重 kg
    private String weight;
    
    // 保存日時
    private String saveDate;
    
    /**
     * デフォルトコンストラクタ
     */
    public VitalModel() {
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public String getFacilityPatId() {
        return facilityPatId;
    }
    
    public void setFacilityPatId(String facilityPatId) {
        this.facilityPatId = facilityPatId;
    }
    
    public String getKarteID() {
        return karteID;
    }
    
    public void setKarteID(String karteID) {
        this.karteID = karteID;
    }
    
    public String getBodyTemperature() {
        return bodyTemperature;
    }
    
    public void setBodyTemperature(String bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }
    
    public String getBloodPressureSystolic() {
        return bloodPressureSystolic;
    }
    
    public void setBloodPressureSystolic(String bloodPressureSystolic) {
        this.bloodPressureSystolic = bloodPressureSystolic;
    }
    
    public String getBloodPressureDiastolic() {
        return bloodPressureDiastolic;
    }
    
    public void setBloodPressureDiastolic(String bloodPressureDiastolic) {
        this.bloodPressureDiastolic = bloodPressureDiastolic;
    }
    
    public String getPulseRate() {
        return pulseRate;
    }
    
    public void setPulseRate(String pulseRate) {
        this.pulseRate = pulseRate;
    }
    
    public String getSpO2() {
        return spo2;
    }
    
    public void setSpO2(String spo2) {
        this.spo2 = spo2;
    }
    
    public String getRespirationRate() {
        return respirationRate;
    }
    
    public void setRespirationRate(String respirationRate) {
        this.respirationRate = respirationRate;
    }
    
    public String getAlgia() {
        return algia;
    }
    
    public void setAlgia(String algia) {
        this.algia = algia;
    }
    
    public String getFeel() {
        return feel;
    }
    
    public void setFeel(String feel) {
        this.feel = feel;
    }
    
    public String getSleep() {
        return sleep;
    }
    
    public void setSleep(String sleep) {
        this.sleep = sleep;
    }
    
    public String getMeal() {
        return meal;
    }
    
    public void setMeal(String meal) {
        this.meal = meal;
    }
    
    public String getEgestion() {
        return egestion;
    }
    
    public void setEgestion(String egestion) {
        this.egestion = egestion;
    }
    
    public String getPS() {
        return ps;
    }
    
    public void setPS(String ps) {
        this.ps = ps;
    }
    
    public String getDate() {
        return vitalDate;
    }
    
    public void setDate(String vitalDate) {
        this.vitalDate = vitalDate;
    }
    
    public String getTime() {
        return vitalTime;
    }
    
    public void setTime(String vitalTime) {
        this.vitalTime = vitalTime;
    }
    
    public String getHeight() {
        return height;
    }
    
    public void setHeight(String height) {
        this.height = height;
    }
    
    public String getWeight() {
        return weight;
    }
    
    public void setWeight(String weight) {
        this.weight = weight;
    }
    
    public String getSaveDate() {
        return saveDate;
    }
    
    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }
    
    @Override
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String val1 = getDate() + getTime();
            String val2 = ((VitalModel)other).getDate() + ((VitalModel)other).getTime();
            return val1.compareTo(val2);
        }
        return 1;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("日時: ").append(this.getDate()).append(" ").append(this.getTime()).append("\n");
        if(this.getBodyTemperature() != null) {
            sb.append("体温: ").append(this.getBodyTemperature()).append("℃").append("\n");
        }
        //sb.append("血圧（収縮）: ").append(this.getBloodPressureSystolic()).append(" mmHg").append("\n");
        //sb.append("血圧（拡張）: ").append(this.getBloodPressureDiastolic()).append(" mmHg").append("\n");
        String bloodPressureSystolic = this.getBloodPressureSystolic();
        if(bloodPressureSystolic == null) bloodPressureSystolic = "";
        String bloodPressureDiastolic = this.getBloodPressureDiastolic();
        if(bloodPressureDiastolic == null) bloodPressureDiastolic = "";
        if(!bloodPressureSystolic.equals("") || !bloodPressureDiastolic.equals("")) {
            sb.append("血圧: ").append(bloodPressureSystolic).append(" / ").append(bloodPressureDiastolic).append(" mmHg").append("\n");
        }
        if(this.getPulseRate() != null) {
            sb.append("心拍数: ").append(this.getPulseRate()).append(" 回/分").append("\n");
        }
        if(this.getSpO2() != null) {
            sb.append("SpO2: ").append(this.getSpO2()).append(" %").append("\n");
        }
        if(this.getRespirationRate() != null) {
            sb.append("呼吸数: ").append(this.getRespirationRate()).append(" 回/分").append("\n");
        }
        if(this.getHeight() != null && this.getHeight().length() > 0) {
            sb.append("身長: ").append(this.getHeight()).append(" cm").append("\n");
        }
        if(this.getWeight() != null && this.getWeight().length() > 0) {
            sb.append("体重: ").append(this.getWeight()).append(" kg").append("\n");
        }
        if(this.getAlgia() != null) {
            sb.append("疼痛: ").append(this.getAlgia()).append(" /5段階").append("\n");
        }
        if(this.getFeel() != null) {
            sb.append("気分: ").append(this.getFeel()).append(" /5段階").append("\n");
        }
        if(this.getSleep() != null) {
            sb.append("睡眠: ").append(this.getSleep()).append(" /5段階").append("\n");
        }
        if(this.getMeal() != null) {
            sb.append("食事: ").append(this.getMeal()).append(" /5段階").append("\n");
        }
        if(this.getEgestion() != null) {
            sb.append("排泄: ").append(this.getEgestion()).append(" /5段階").append("\n");
        }
        if(this.getPS() != null) {
            sb.append("PS: ").append(this.getPS()).append(" /5段階").append("\n");
        }
        return sb.toString();
    }
}
