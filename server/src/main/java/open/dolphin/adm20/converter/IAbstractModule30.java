package open.dolphin.adm20.converter;

import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class IAbstractModule30 implements java.io.Serializable {

    // pk
    private long id;
    
    // 確定日 Date
    private String confirmed;
    
    // ドキュメント開始日 Date
    private String started;
    
    // ドキュメント終了日 Date
    private String ended;
    
    // 記録日 Date
    private String recorded;
    
    // リンク先pk
    private long linkId;
    
    // リンク先との関連
    private String linkRelation;
    
    // ステータス
    private String status;
    
    // 生成責任者（=システムのログインユーザー）
    private UserModel userModel;
    
    // Karte
    private KarteBean karteBean;
    
    // モジュール情報
    private IModuleInfo moduleInfo;
    
//minagawa^ Documentへの参照    
    private long docPK;
//minagawa$    
    
    public IAbstractModule30() {
        moduleInfo = new IModuleInfo();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public String getLinkRelation() {
        return linkRelation;
    }

    public void setLinkRelation(String linkRelation) {
        this.linkRelation = linkRelation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public KarteBean getKarteBean() {
        return karteBean;
    }

    public void setKarteBean(KarteBean karteBean) {
        this.karteBean = karteBean;
    }

    public IModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(IModuleInfo moduleInfoBean) {
        this.moduleInfo = moduleInfoBean;
    }

    /**
     * @return the docPK
     */
    public long getDocPK() {
        return docPK;
    }

    /**
     * @param docPK the docPK to set
     */
    public void setDocPK(long docPK) {
        this.docPK = docPK;
    }
}
