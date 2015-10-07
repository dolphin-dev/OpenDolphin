/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.touch.converter;

import open.dolphin.infomodel.VitalModel;

/**
 * バイタル対応
 * @author Life Sciences Computing Corporation.
 */
public class IVitalModel implements java.io.Serializable {
    private long vitalId;
    
    // 施設ID:患者ID
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
    public IVitalModel() {
    }

    public long getVitalId() {
        return vitalId;
    }
    
    public void setVitalId(long id) {
        this.vitalId = id;
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
    
    public void fromModel(VitalModel model) {
        this.setVitalId(model.getId());
        this.setFacilityPatId(model.getFacilityPatId());
        this.setKarteID(model.getKarteID());
        this.setBodyTemperature(model.getBodyTemperature());
        this.setBloodPressureSystolic(model.getBloodPressureSystolic());
        this.setBloodPressureDiastolic(model.getBloodPressureDiastolic());
        this.setPulseRate(model.getPulseRate());
        this.setSpO2(model.getSpO2());
        this.setRespirationRate(model.getRespirationRate());
        this.setAlgia(model.getAlgia());
        this.setFeel(model.getFeel());
        this.setSleep(model.getSleep());
        this.setMeal(model.getMeal());
        this.setEgestion(model.getEgestion());
        this.setPS(model.getPS());
        this.setDate(model.getDate());
        this.setTime(model.getTime());
        this.setHeight(model.getHeight());
        this.setWeight(model.getWeight());
        this.setSaveDate(model.getSaveDate());
    }
    
    public VitalModel toModel() {
        VitalModel ret = new VitalModel();
        //ret.setId(this.getId());  // DBがセットするため必要ない
        ret.setFacilityPatId(this.getFacilityPatId());
        ret.setKarteID(this.getKarteID());
        ret.setBodyTemperature(this.getBodyTemperature());
        ret.setBloodPressureSystolic(this.getBloodPressureSystolic());
        ret.setBloodPressureDiastolic(this.getBloodPressureDiastolic());
        ret.setPulseRate(this.getPulseRate());
        ret.setSpO2(this.getSpO2());
        ret.setRespirationRate(this.getRespirationRate());
        ret.setAlgia(this.getAlgia());
        ret.setFeel(this.getFeel());
        ret.setSleep(this.getSleep());
        ret.setMeal(this.getMeal());
        ret.setEgestion(this.getEgestion());
        ret.setPS(this.getPS());
        ret.setDate(this.getDate());
        ret.setTime(this.getTime());
        ret.setHeight(this.getHeight());
        ret.setWeight(this.getWeight());
        ret.setSaveDate(this.getSaveDate());
        return ret;
    }
}
