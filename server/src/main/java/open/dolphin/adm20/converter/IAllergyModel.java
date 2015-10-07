/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm20.converter;

import java.util.Date;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author kazushi
 */
public class IAllergyModel implements java.io.Serializable {
    
    // iOS アダプター
    private long kartePK;
    private long userPK;
    private String started;
    private String confirmed;
    private String recorded;
    
    // Observation ID
    private long observationId;
    
    // 要因
    private String factor;
    
    // 反応程度
    private String severity;
    
    // コード体系
    private String severityTableId;
    
    // 同定日
    private String identifiedDate;
    
    // メモ
    private String memo;

    public long getObservationId() {
        return observationId;
    }

    public void setObservationId(long observationId) {
        this.observationId = observationId;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverityTableId() {
        return severityTableId;
    }

    public void setSeverityTableId(String severityTableId) {
        this.severityTableId = severityTableId;
    }

    public String getIdentifiedDate() {
        return identifiedDate;
    }

    public void setIdentifiedDate(String identifiedDate) {
        this.identifiedDate = identifiedDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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
    
    public void fromModel(AllergyModel model) {
        this.setObservationId(model.getObservationId());
        this.setFactor(model.getFactor());
        this.setSeverity(model.getSeverity());
        this.setSeverityTableId(model.getSeverityTableId());
        this.setIdentifiedDate(model.getIdentifiedDate());
        this.setMemo(model.getMemo());
    }
    
    public AllergyModel toModel() {
        AllergyModel ret = new AllergyModel();
        ret.setObservationId(this.getObservationId());
        ret.setFactor(this.getFactor());
        ret.setSeverity(this.getSeverity());
        ret.setSeverityTableId(this.getSeverityTableId());
        ret.setIdentifiedDate(this.getIdentifiedDate());
        ret.setMemo(this.getMemo());
        return ret;
    }
    
    // iOSからIAllergyModelで送信
    // REST でObservationModelに変換、persist | merge
    // iOS からは ptPK を送信、session で Karteを検索し karteBeanをセットする
    public ObservationModel toObservationModel() {
        
        ObservationModel observation = new ObservationModel();
        
        observation.setId(this.getObservationId());
        
        if (this.getKartePK()!=0L) {
            KarteBean karte = new KarteBean();
            karte.setId(this.getKartePK());
            observation.setKarteBean(karte);
        }
        
        UserModel user = new UserModel();
        user.setId(getUserPK());
        observation.setUserModel(user);
        
        observation.setObservation(IInfoModel.OBSERVATION_ALLERGY);
        observation.setPhenomenon(this.getFactor());
        observation.setCategoryValue(this.getSeverity());
        
        Date date = ModelUtils.getDateTimeAsObject(this.getIdentifiedDate()+"T00:00:00");
        observation.setConfirmed(date);
        observation.setRecorded(new Date());
        observation.setStarted(date);
        observation.setStatus(IInfoModel.STATUS_FINAL);
        observation.setMemo(this.getMemo());
        
//        System.err.println(observation.getId());
//        System.err.println(observation.getStarted());
//        System.err.println(observation.getConfirmed());
//        System.err.println(observation.getRecorded());
//        System.err.println(observation.getKarteBean().getId());
//        System.err.println(observation.getUserModel().getId());
//        System.err.println(observation.getStatus());
//        System.err.println(observation.getObservation());
//        System.err.println(observation.getPhenomenon());
//        System.err.println(observation.getCategoryValue());
//        System.err.println(observation.getMemo());
        
        return observation;
    }
}
