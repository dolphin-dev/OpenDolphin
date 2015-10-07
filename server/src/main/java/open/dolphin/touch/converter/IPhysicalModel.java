/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.touch.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PhysicalModel;
import open.dolphin.infomodel.UserModel;

/**
 * PhysicalModel
 * @author Life Sciences Computing Corporation.
 */
public class IPhysicalModel implements java.io.Serializable {
    
    // iOS アダプター
    private long kartePK;
    private long userPK;
    private String started;
    private String confirmed;
    private String recorded;
    
    private long heightId;
    private long weightId;
    
    // 身長
    private String height;
    
    // 体重
    private String weight;
    
    // 同定日
    private String identifiedDate;
    
    // メモ
    private String memo;
    
    /**
     * デフォルトコンストラクタ
     */
    public IPhysicalModel() {
    }
    
    public long getHeightId() {
        return heightId;
    }
    
    public void setHeightId(long heightId) {
        this.heightId = heightId;
    }
    
    public long getWeightId() {
        return weightId;
    }
    
    public void setWeightId(long weightId) {
        this.weightId = weightId;
    }
    
    // factor
    public String getHeight() {
        return height;
    }
    public void setHeight(String value) {
        height = value;
    }
    
    // identifiedDate
    public String getIdentifiedDate() {
        return identifiedDate;
    }
    
    public void setIdentifiedDate(String value) {
        identifiedDate = value;
    }
    
    // memo
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String value) {
        memo = value;
    }
    
    public void setWeight(String severity) {
        this.weight = severity;
    }
    
    public String getWeight() {
        return weight;
    }

    public long getKartePK() {
        return kartePK;
    }

    public void setKartePK(long kartePK) {
        this.kartePK = kartePK;
    }

    public long getUserPK() {
        return userPK;
    }

    public void setUserPK(long userPK) {
        this.userPK = userPK;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }
    
//    public void fromModel(PhysicalModel model) {
//        this.setHeightId(model.getHeightId());
//        this.setWeightId(model.getWeightId());
//        this.setHeight(model.getHeight());
//        this.setIdentifiedDate(model.getIdentifiedDate());
//        this.setMemo(model.getMemo());
//        this.setWeight(model.getWeight());
//    }
//    
//    public PhysicalModel toModel() {
//        PhysicalModel ret = new PhysicalModel();
//        ret.setHeightId(this.getHeightId());
//        ret.setWeightId(this.getWeightId());
//        ret.setHeight(this.getHeight());
//        ret.setIdentifiedDate(this.getIdentifiedDate());
//        ret.setMemo(this.getMemo());
//        ret.setWeight(this.getWeight());
//        return ret;
//    }
    
    public void fromObservationModel(ObservationModel observation) {
        if(observation.getValue() != null && observation.getValue().length() > 0) {
            if(observation.getPhenomenon().equals(IInfoModel.PHENOMENON_BODY_HEIGHT)) {
                this.setHeightId(observation.getId());
                this.setHeight(observation.getValue());
            }else if(observation.getPhenomenon().equals(IInfoModel.PHENOMENON_BODY_WEIGHT)) {
                this.setWeightId(observation.getId());
                this.setWeight(observation.getValue());
            }
            this.setIdentifiedDate(observation.confirmDateAsString());
            this.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
        }
    }
    
    // iOSからIAllergyModelで送信
    // REST でObservationModelに変換、persist | merge
    // iOS からは ptPK を送信、session で Karteを検索し karteBeanをセットする
    public List<ObservationModel> toObservationModel() {
        
        List<ObservationModel> observations = new ArrayList<ObservationModel>();
        
        if (this.getHeight() != null) {
            ObservationModel observation = new ObservationModel();
            if (this.getKartePK()!=0L) {
                KarteBean karte = new KarteBean();
                karte.setId(this.getKartePK());
                observation.setKarteBean(karte);
            }
            UserModel user = new UserModel();
            user.setId(this.getUserPK());
            observation.setUserModel(user);
            observation.setObservation(IInfoModel.OBSERVATION_PHYSICAL_EXAM);
            observation.setPhenomenon(IInfoModel.PHENOMENON_BODY_HEIGHT);
            observation.setValue(this.getHeight());
            observation.setUnit(IInfoModel.UNIT_BODY_HEIGHT);
            Date date = ModelUtils.getDateTimeAsObject(this.getIdentifiedDate()+"T00:00:00");
            observation.setConfirmed(date);
            observation.setStarted(date);
            observation.setRecorded(new Date());
            observation.setStatus(IInfoModel.STATUS_FINAL);
            observations.add(observation);
        }
        
        if (this.getWeight()!= null) {
            ObservationModel observation = new ObservationModel();
            if (this.getKartePK()!=0L) {
                KarteBean karte = new KarteBean();
                karte.setId(this.getKartePK());
                observation.setKarteBean(karte);
            }
            UserModel user = new UserModel();
            user.setId(this.getUserPK());
            observation.setUserModel(user);
            observation.setObservation(IInfoModel.OBSERVATION_PHYSICAL_EXAM);
            observation.setPhenomenon(IInfoModel.PHENOMENON_BODY_WEIGHT);
            observation.setValue(this.getWeight());
            observation.setUnit(IInfoModel.UNIT_BODY_WEIGHT);
            Date date = ModelUtils.getDateTimeAsObject(this.getIdentifiedDate()+"T00:00:00");
            observation.setConfirmed(date);
            observation.setStarted(date);
            observation.setRecorded(new Date());
            observation.setStatus(IInfoModel.STATUS_FINAL);
            observations.add(observation);
        }
        
        return observations;
    }
}
