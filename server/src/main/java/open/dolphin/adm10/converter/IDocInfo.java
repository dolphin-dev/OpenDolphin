package open.dolphin.adm10.converter;

import open.dolphin.infomodel.DocInfoModel;

/**
 * DocInfoModel
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 *
 */
public class IDocInfo implements java.io.Serializable {
    
    // DocumentModel.id
    private long docPk;
    
    // Parent DocumentModel.id
    private long parentPk;
    
    // DocId
    private String docId;
    
    // 文書種別
    private String docType;
    
    // タイトル
    private String title;
    
    // 生成目的
    private String purpose;
    
    // 最初の確定日 String
    private String firstConfirmDate;
    
    // 確定日 String
    private String confirmDate;
    
    // 診療科
    private String department;
    
    // 診療科説明
    private String departmentDesc;
    
    // 健康保険
    private String healthInsurance;
    
    // 健康保険説明
    private String healthInsuranceDesc;
    
    // 健康保険GUID
    private String healthInsuranceGUID;
    
    // 注意フラグ boolean
    private String hasMark;
    
    // 画像フラグ boolean
    private String hasImage;
    
    // RPフラグ boolean
    private String hasRp;
    
    // 処置フラグ boolean
    private String hasTreatment;
    
    // 検体検査フラグ boolean
    private String hasLaboTest;
    
    // バージョン番号
    private String versionNumber;
    
    // 親文書ID
    private String parentId;
    
    // 親文書との関係
    private String parentIdRelation;
    
    // 検体検査オーダー番号
    private String labtestOrderNumber;
    
    // CLAIM送信フラグ  boolean
    private String sendClaim;
    
//minagawa^ CLAIM送信日
    private String claimDate;
//minagawa$   

    // 検体検査オーダー送信フラグ  boolean
    private String sendLabtest;

    // MML送信フラグ boolean
    private String sendMml;
    
    // 処方せん出力  boolean
    private String priscriptionOutput;
    
    //--------------------------------------
    // CLAIM で必要な情報
    //--------------------------------------
    // 適用保険 丸ごと
    private IPVTHealthInsurance pvtHealthInsurance;
    
    // 施設（病院）名
    private String facilityName;
    
    // 医療資格
    private String createrLisence;
    
    // 患者ID
    private String patientId;
    
    // 患者氏名
    private String patientName;
    
    // 患者性別
    private String patientGender;
    
    //----------------------------------
    // 処方せん出力に必要な情報
    //  担当医情報: Document->creatorから検索
    //  患者情報; Document->karteIdから検索
    //----------------------------------
    // 交付日
    private String issuanceDate;
    
    // 保険医療機関番号
    private String institutionNumber;
    
    // 使用期間
    private String period;
    
    // 備考欄患者住所、氏名転記フラグ boolean
    private String chkPatientInfo;
    
    // 備考欄患者麻薬施用者転記フラグ boolean
    private String chkUseDrugInfo;
    
    // 備考欄に「在宅」を記載するかどうかのフラグ boolean
    private String chkHomeMedical;
    
    // 一般名を使用するかどうか
    private String useGeneralName;
    
//minagawa^ EHT add    
    private String status;
    
////minagawa^ 入院対応    
//    private String admFlag;
////minagawa$  

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
//minagawa$    

    public long getDocPk() {
        return docPk;
    }

    public void setDocPk(long docPk) {
        this.docPk = docPk;
    }

    public long getParentPk() {
        return parentPk;
    }

    public void setParentPk(long parentPk) {
        this.parentPk = parentPk;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public String getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(String firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentDesc() {
        return departmentDesc;
    }

    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    public String getHealthInsurance() {
        return healthInsurance;
    }

    public void setHealthInsurance(String healthInsurance) {
        this.healthInsurance = healthInsurance;
    }

    public String getHealthInsuranceDesc() {
        return healthInsuranceDesc;
    }

    public void setHealthInsuranceDesc(String healthInsuranceDesc) {
        this.healthInsuranceDesc = healthInsuranceDesc;
    }

    public String getHealthInsuranceGUID() {
        return healthInsuranceGUID;
    }

    public void setHealthInsuranceGUID(String healthInsuranceGUID) {
        this.healthInsuranceGUID = healthInsuranceGUID;
    }

    public String getHasMark() {
        return hasMark;
    }

    public void setHasMark(String hasMark) {
        this.hasMark = hasMark;
    }

    public String getHasImage() {
        return hasImage;
    }

    public void setHasImage(String hasImage) {
        this.hasImage = hasImage;
    }

    public String getHasRp() {
        return hasRp;
    }

    public void setHasRp(String hasRp) {
        this.hasRp = hasRp;
    }

    public String getHasTreatment() {
        return hasTreatment;
    }

    public void setHasTreatment(String hasTreatment) {
        this.hasTreatment = hasTreatment;
    }

    public String getHasLaboTest() {
        return hasLaboTest;
    }

    public void setHasLaboTest(String hasLaboTest) {
        this.hasLaboTest = hasLaboTest;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentIdRelation() {
        return parentIdRelation;
    }

    public void setParentIdRelation(String parentIdRelation) {
        this.parentIdRelation = parentIdRelation;
    }
    
    public String getLabtestOrderNumber() {
        return labtestOrderNumber;
    }

    public void setLabtestOrderNumber(String labtestOrderNumber) {
        this.labtestOrderNumber = labtestOrderNumber;
    }
    
    public String getSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(String sendClaim) {
        this.sendClaim = sendClaim;
    }
//minagawa^ EHRTouch    
    public String getClaimDate() {
        return claimDate;
    }
    public void setClaimDate(String claimDate) {
        this.claimDate = claimDate;
    }
//minagawa$
    public String getSendLabtest() {
        return sendLabtest;
    }

    public void setSendLabtest(String sendLabtest) {
        this.sendLabtest = sendLabtest;
    }

    public String getSendMml() {
        return sendMml;
    }

    public void setSendMml(String sendMml) {
        this.sendMml = sendMml;
    }

    public IPVTHealthInsurance getPvtHealthInsuranceModel() {
        return pvtHealthInsurance;
    }

    public void setPvtHealthInsuranceModel(IPVTHealthInsurance pvtHealthInsurance) {
        this.pvtHealthInsurance = pvtHealthInsurance;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getCreaterLisence() {
        return createrLisence;
    }

    public void setCreaterLisence(String createrLisence) {
        this.createrLisence = createrLisence;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPriscriptionOutput() {
        return priscriptionOutput;
    }

    public void setPriscriptionOutput(String priscriptionOutput) {
        this.priscriptionOutput = priscriptionOutput;
    }

//    public IPVTHealthInsurance getPvtHealthInsurance() {
//        return pvtHealthInsurance;
//    }
//
//    public void setPvtHealthInsurance(IPVTHealthInsurance pvtHealthInsurance) {
//        this.pvtHealthInsurance = pvtHealthInsurance;
//    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getInstitutionNumber() {
        return institutionNumber;
    }

    public void setInstitutionNumber(String institutionNumber) {
        this.institutionNumber = institutionNumber;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getChkPatientInfo() {
        return chkPatientInfo;
    }

    public void setChkPatientInfo(String chkPatientInfo) {
        this.chkPatientInfo = chkPatientInfo;
    }

    public String getChkUseDrugInfo() {
        return chkUseDrugInfo;
    }

    public void setChkUseDrugInfo(String chkUseDrugInfo) {
        this.chkUseDrugInfo = chkUseDrugInfo;
    }

    public String getChkHomeMedical() {
        return chkHomeMedical;
    }

    public void setChkHomeMedical(String chkHomeMedical) {
        this.chkHomeMedical = chkHomeMedical;
    }

    public String getUseGeneralName() {
        return useGeneralName;
    }

    public void setUseGeneralName(String useGeneralName) {
        this.useGeneralName = useGeneralName;
    }
    
    public void fromModel(DocInfoModel model) {
                
        this.setDocPk(model.getDocPk());
        this.setParentPk(model.getParentPk());
        this.setDocId(model.getDocId());
        this.setDocType(model.getDocType());
        this.setTitle(model.getTitle());
        this.setPurpose(model.getPurpose());
        
        // IOS 注意
        this.setFirstConfirmDate(IOSHelper.toDateStr(model.getFirstConfirmDate()));
        this.setConfirmDate(IOSHelper.toDateStr(model.getConfirmDate()));
        
        this.setDepartment(model.getDepartment());
        
//minagawa^ EHT add
        this.setStatus(model.getStatus());
//minagawa$
        
        // IOS  注意
        this.setDepartmentDesc(model.getDepartmentDesc());
        this.setHealthInsurance(model.getHealthInsurance());
        this.setHealthInsuranceDesc(model.getHealthInsuranceDesc());
        this.setHealthInsuranceGUID(model.getHealthInsuranceGUID());
        
        this.setHasMark(IOSHelper.toBoolStr(model.isHasMark()));
        this.setHasImage(IOSHelper.toBoolStr(model.isHasImage()));
        this.setHasRp(IOSHelper.toBoolStr(model.isHasRp()));
        this.setHasTreatment(IOSHelper.toBoolStr(model.isHasTreatment()));
        this.setHasLaboTest(IOSHelper.toBoolStr(model.isHasLaboTest()));
        
        this.setVersionNumber(model.getVersionNumber());
        this.setParentId(model.getParentId());
        this.setParentIdRelation(model.getParentIdRelation());
        this.setLabtestOrderNumber(model.getLabtestOrderNumber());
        
        this.setSendClaim(IOSHelper.toBoolStr(model.isSendClaim()));
//minagawa^ EHRTouch        
        this.setClaimDate(IOSHelper.toDateStr(model.getClaimDate()));
//minagawa$        
        this.setSendLabtest(IOSHelper.toBoolStr(model.isSendLabtest()));
        this.setSendMml(IOSHelper.toBoolStr(model.isSendMml()));
        this.setPriscriptionOutput(IOSHelper.toBoolStr(model.isPriscriptionOutput()));
        
        // IOS 注意
        if (model.getPVTHealthInsuranceModel()!=null) {
            IPVTHealthInsurance pvtIns = new IPVTHealthInsurance();
            pvtIns.fromModel(model.getPVTHealthInsuranceModel());
            this.setPvtHealthInsuranceModel(pvtIns);
        }
        
        // IOS 注意
        this.setFacilityName(model.getFacilityName());
        this.setCreaterLisence(model.getCreaterLisence());
        this.setPatientId(model.getPatientId());
        this.setPatientName(model.getPatientName());
        this.setPatientGender(model.getPatientGender());
        
        // IOS 注意
        this.setIssuanceDate(IOSHelper.toDateStr(model.getIssuanceDate()));
        this.setInstitutionNumber(model.getInstitutionNumber());
        this.setPeriod(IOSHelper.toDateStr(model.getPeriod()));
        this.setChkHomeMedical(IOSHelper.toBoolStr(model.isChkHomeMedical()));
        this.setChkPatientInfo(IOSHelper.toBoolStr(model.isChkPatientInfo()));
        this.setChkUseDrugInfo(IOSHelper.toBoolStr(model.isChkUseDrugInfo()));
        this.setUseGeneralName(IOSHelper.toBoolStr(model.isUseGeneralName()));
        
////minagawa^ 入院対応
//        this.setAdmFlag(model.getAdmFlag());
////minagawa$         
    }
        
    public DocInfoModel toModel() {
        
        DocInfoModel ret = new DocInfoModel();
        ret.setDocPk(this.getDocPk());
        ret.setParentPk(this.getParentPk());
        ret.setDocId(this.getDocId());
        ret.setDocType(this.getDocType());
        ret.setTitle(this.getTitle());
        ret.setPurpose(this.getPurpose());
        
        // IOS 注意
        ret.setFirstConfirmDate(IOSHelper.toDate(this.getFirstConfirmDate()));
        ret.setConfirmDate(IOSHelper.toDate(this.getConfirmDate()));
        
//minagawa^ EHT add
        ret.setStatus(this.getStatus());
//minagawa$
        
        ret.setDepartment(this.getDepartment());    
        
        // IOS  注意
        ret.setDepartmentDesc(this.getDepartmentDesc());
        ret.setHealthInsurance(this.getHealthInsurance());
        ret.setHealthInsuranceDesc(this.getHealthInsuranceDesc());
        ret.setHealthInsuranceGUID(this.getHealthInsuranceGUID());
        
        ret.setHasMark(IOSHelper.toBool(this.getHasMark()));
        ret.setHasImage(IOSHelper.toBool(this.getHasImage()));
        ret.setHasRp(IOSHelper.toBool(this.getHasRp()));
        ret.setHasTreatment(IOSHelper.toBool(this.getHasTreatment()));
        ret.setHasLaboTest(IOSHelper.toBool(this.getHasLaboTest()));
        
        ret.setVersionNumber(this.getVersionNumber());
        ret.setParentId(this.getParentId());
        ret.setParentIdRelation(this.getParentIdRelation());
        ret.setLabtestOrderNumber(this.getLabtestOrderNumber());
        
        ret.setSendClaim(IOSHelper.toBool(this.getSendClaim()));
//minagawa EHRTouch
        ret.setClaimDate(IOSHelper.toDate(this.getClaimDate()));
//minagawa$        
        ret.setSendLabtest(IOSHelper.toBool(this.getSendLabtest()));
        ret.setSendMml(IOSHelper.toBool(this.getSendMml()));
        ret.setPriscriptionOutput(IOSHelper.toBool(this.getPriscriptionOutput()));
        
        // IOS 注意
        ret.setPVTHealthInsuranceModel(this.getPvtHealthInsuranceModel().toModel());
        
        // IOS 注意
        ret.setFacilityName(this.getFacilityName());
        ret.setCreaterLisence(this.getCreaterLisence());
        ret.setPatientId(this.getPatientId());
        ret.setPatientName(this.getPatientName());
        ret.setPatientGender(this.getPatientGender());
        
        // IOS 注意
        ret.setIssuanceDate(IOSHelper.toDate(this.getIssuanceDate()));
        ret.setInstitutionNumber(this.getInstitutionNumber());
        ret.setPeriod(IOSHelper.toDate(this.getPeriod()));
        ret.setChkHomeMedical(IOSHelper.toBool(this.getChkHomeMedical()));
        ret.setChkPatientInfo(IOSHelper.toBool(this.getChkPatientInfo()));
        ret.setChkUseDrugInfo(IOSHelper.toBool(this.getChkUseDrugInfo()));
        ret.setUseGeneralName(IOSHelper.toBool(this.getUseGeneralName()));
        
////minagawa^ 入院対応
//        ret.setAdmFlag(this.getAdmFlag());
////minagawa$   
        
        return ret;
    }

//    public String getAdmFlag() {
//        return admFlag;
//    }
//
//    public void setAdmFlag(String admFlag) {
//        this.admFlag = admFlag;
//    }
}