package open.dolphin.adm20.converter;

import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author kazushi Minagawa
 */
public class IRegisteredDiagnosis implements java.io.Serializable {
    
    // PK
    private long id;
    
    // 確定日時 Date
    private String confirmed;
    
    // 記録の有効開始日時(最初に確定した日）Date
    private String started;
    
    // 記録の終了日時（有効ではなくなった日）Date
    private String ended;
    
    // 記録日時 Date
    private String recorded;
    
    // 親エントリーの PK
    private long linkId;
    
    // 親エントリーとの関係
    private String linkRelation;
    
    // エントリーのステータス(Final,Modifyed等）
    private String status;
    
    // 記録責任者（システムの利用者）
    private UserModel userModel;
    
    // カルテへの外部参照
    private KarteBean karteBean;
    
    // 疾患名
    private String diagnosis;
    
    // 疾患コード
    private String diagnosisCode;
    
    // 疾患コード体系名
    private String diagnosisCodeSystem;
    
    // カテゴリー（コード値）
    private String category;
    
    // カテゴリー表記
    private String categoryDesc;
    
    // カテゴリー体系
    private String categoryCodeSys;
    
    // 転帰（コード値）
    private String outcome;
    
    // 転帰説明
    private String outcomeDesc;
    
    // 転帰体系
    private String outcomeCodeSys;
    
    // 疾患の初診日
    private String firstEncounterDate;
    
    // 関連健康保険情報
    private String relatedHealthInsurance;

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

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisCodeSystem() {
        return diagnosisCodeSystem;
    }

    public void setDiagnosisCodeSystem(String diagnosisCodeSystem) {
        this.diagnosisCodeSystem = diagnosisCodeSystem;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String diagnosisCategory) {
        this.category = diagnosisCategory;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String diagnosisCategoryDesc) {
        this.categoryDesc = diagnosisCategoryDesc;
    }

    public String getCategoryCodeSys() {
        return categoryCodeSys;
    }

    public void setCategoryCodeSys(String diagnosisCategoryCodeSys) {
        this.categoryCodeSys = diagnosisCategoryCodeSys;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getOutcomeDesc() {
        return outcomeDesc;
    }

    public void setOutcomeDesc(String outcomeDesc) {
        this.outcomeDesc = outcomeDesc;
    }

    public String getOutcomeCodeSys() {
        return outcomeCodeSys;
    }

    public void setOutcomeCodeSys(String outcomeCodeSys) {
        this.outcomeCodeSys = outcomeCodeSys;
    }

    public String getFirstEncounterDate() {
        return firstEncounterDate;
    }

    public void setFirstEncounterDate(String firstEncounterDate) {
        this.firstEncounterDate = firstEncounterDate;
    }

    public String getRelatedHealthInsurance() {
        return relatedHealthInsurance;
    }

    public void setRelatedHealthInsurance(String relatedHealthInsurance) {
        this.relatedHealthInsurance = relatedHealthInsurance;
    }
    
    public void fromModel(RegisteredDiagnosisModel model) {
        
        this.setId(model.getId());
        this.setStarted(IOSHelper.toDateStr(model.getStarted()));
        this.setConfirmed(IOSHelper.toDateStr(model.getConfirmed()));
        this.setRecorded(IOSHelper.toDateStr(model.getRecorded()));
        this.setEnded(IOSHelper.toDateStr(model.getEnded()));
        
        this.setLinkId(model.getLinkId());
        this.setLinkRelation(model.getLinkRelation());
        this.setStatus(model.getStatus());
        // 変換なし
        //this.setKarteBean(model.getKarteBean());
        //this.setUserModel(model.getUserModel());
        
        // 疾患
        this.setDiagnosis(model.getDiagnosis());
        
        // 疾患コード
        this.setDiagnosisCode(model.getDiagnosisCode());
        
        // 疾患コード体系名
        this.setDiagnosisCodeSystem(model.getDiagnosisCodeSystem());
        
        // カテゴリー（コード値）
        if (model.getCategory()!=null) {
            this.setCategory(model.getCategory());
            this.setCategoryDesc(model.getCategoryDesc());
            this.setCategoryCodeSys(model.getCategoryCodeSys());
        }
        
        // 転帰
        if (model.getOutcome()!=null) {
            this.setOutcome(model.getOutcome());
            this.setOutcomeDesc(model.getOutcomeDesc());
            this.setOutcomeCodeSys(model.getOutcomeCodeSys());
        }
        
        this.setFirstEncounterDate(model.getFirstEncounterDate());
        this.setRelatedHealthInsurance(model.getRelatedHealthInsurance());
    }
    
    public RegisteredDiagnosisModel toModel() {
        
        RegisteredDiagnosisModel ret = new RegisteredDiagnosisModel();
        
        // pk
        ret.setId(this.getId());
        
        // 確定日 Date
        ret.setConfirmed(IOSHelper.toDate(this.getConfirmed()));
        
        // 開始日 Date
        ret.setStarted(IOSHelper.toDate(this.getStarted()));
        
        // 終了日 Date
        ret.setEnded(IOSHelper.toDate(this.getEnded()));
        
        // 記録日 Date
        ret.setRecorded(IOSHelper.toDate(this.getRecorded()));
        
        // リンクpk
        ret.setLinkId(this.getLinkId());
        
        // リンクの関連
        ret.setLinkRelation(this.getLinkRelation());
        
        // ステータス
        ret.setStatus(this.getStatus());
        
        // UserModel 変換なし
        ret.setUserModel(this.getUserModel());
        
        // KarteBean 変換なし
        ret.setKarte(this.getKarteBean());
        
        // 疾患
        ret.setDiagnosis(this.getDiagnosis());
        
        // 疾患コード
        ret.setDiagnosisCode(this.getDiagnosisCode());
        
        // 疾患コード体系名
        ret.setDiagnosisCodeSystem(this.getDiagnosisCodeSystem());
        
        // カテゴリー（コード値）
        if (this.getCategory()!=null) {
            ret.setCategory(this.getCategory());
            ret.setCategoryDesc(this.getCategoryDesc());
            ret.setCategoryCodeSys(this.getCategoryCodeSys());
        }
        
        // 転帰
        if (this.getOutcome()!=null) {
            ret.setOutcome(this.getOutcome());
            ret.setOutcomeDesc(this.getOutcomeDesc());
            ret.setOutcomeCodeSys(this.getOutcomeCodeSys());
        }
        
        ret.setFirstEncounterDate(this.getFirstEncounterDate());
        ret.setRelatedHealthInsurance(this.getRelatedHealthInsurance());
        
        return ret;
    }
}