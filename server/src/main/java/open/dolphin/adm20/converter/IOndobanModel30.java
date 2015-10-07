/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm20.converter;

import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.OndobanModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author kazushi
 */
public class IOndobanModel30 implements java.io.Serializable {
    
    //---------------------------
    // iOS アダプター
    private long id;
    private String started;
    private String confirmed;
    private String recorded;
    private String status;
    //--------------------------
    
    // 測定項目の名前 BT, BPH, BPL etc.
    private String seriesName;
    
    // 測定項目につける Index
    private int seriesIndex;
    
    // １日に複数回測定するのでそのIndex
    private int dayIndex;
    
    // 測定値
    private float value;
    
    // 単位
    private String unit;
    
    // メモ
    private String memo;
    
    // 記録責任者（システムの利用者）
    private UserModel userModel;
    
    private IUserModel iuser;
    
    // カルテへの外部参照
    private KarteBean karteBean;
    
    // iOS への返却
    public void fromModel(OndobanModel model) {
        
        // Entry data
        this.setId(model.getId());
        this.setStarted(IOSHelper.toDateStr(model.getStarted()));
        this.setConfirmed(IOSHelper.toDateStr(model.getConfirmed()));
        this.setRecorded(IOSHelper.toDateStr(model.getRecorded()));    
        this.setStatus(model.getStatus());
        
        // Ondoban data
        this.setSeriesName(model.getSeriesName());
        this.setSeriesIndex(model.getSeriesIndex());
        this.setDayIndex(model.getDayIndex());
        this.setValue(model.getValue());
        this.setUnit(model.getUnit());
        this.setMemo(model.getMemo());
        
        // 記録者
        if (model.getUserModel()!=null) {
            IUserModel u = new IUserModel();
            u.setModel(model.getUserModel());
            this.setIuser(u);
        }
        
        // userModel,karteBeanは変換しない -> iOS ではnull
    }
    
    // iOS to JavaEE
    public OndobanModel toModel() {
        
        OndobanModel ret = new OndobanModel();
        
        // ID
        ret.setId(this.getId());
        
        // 確定日 Date
        ret.setConfirmed(IOSHelper.toDate(this.getConfirmed()));
        
        // 開始日 Date
        ret.setStarted(IOSHelper.toDate(this.getStarted()));
        
        // 記録日 Date
        ret.setRecorded(IOSHelper.toDate(this.getRecorded()));
        
        // ステータス
        ret.setStatus(this.getStatus());
        
        // UserModel 変換なし
        ret.setUserModel(this.getUserModel());
        
        // KarteBean 変換なし
        ret.setKarte(this.getKarteBean());
        
        // Ondoban data
        ret.setSeriesName(this.getSeriesName());
        ret.setSeriesIndex(this.getSeriesIndex());
        ret.setDayIndex(this.getDayIndex());
        ret.setValue(this.getValue());
        ret.setUnit(this.getUnit());
        ret.setMemo(this.getMemo());
        
        return ret;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public int getSeriesIndex() {
        return seriesIndex;
    }

    public void setSeriesIndex(int seriesIndex) {
        this.seriesIndex = seriesIndex;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public IUserModel getIuser() {
        return iuser;
    }

    public void setIuser(IUserModel iuser) {
        this.iuser = iuser;
    }

    public KarteBean getKarteBean() {
        return karteBean;
    }

    public void setKarteBean(KarteBean karteBean) {
        this.karteBean = karteBean;
    }
}
