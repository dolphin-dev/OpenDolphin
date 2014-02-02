package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * DocInfoModel
 * 
 * 文書履歴のレコード及びCLAIM送信のコンテナとして使用するクラス。
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 *
 */
@Embeddable
public class DocInfoModel extends InfoModel
        implements Comparable,java.io.Serializable {
    
    // = DocumentModel.id
    @Transient
    private long docPk;
    
    // = 親DocumentModel.id
    @Transient
    private long parentPk;
    
    // 32bit GUID (MML ouput)
    @Column(nullable=false, length=32)
    private String docId;
    
    // 文書種別(Dolphin固有）
    @Column(nullable=false)
    private String docType;
    
    // タイトル
    @Column(nullable=false)
    private String title;
    
    // 生成目的 MML
    @Column(nullable=false)
    private String purpose;
    
    // 生成目的説明 MML
    @Transient
    private String purposeDesc;
    
    // 生成目的コード体系 MML
    @Transient
    private String purposeCodeSys;
    
    // = DocumentModel.started（最初の確定日）
    @Transient
    private Date firstConfirmDate;
    
    // 確定日
    @Transient
    private Date confirmDate;
    
    // 診療科
    private String department;
    
    //--------------------------------------------------------------
    // 診療科説明
    // 診療科名、コード、担当医名、担当医コード、JMARIコード（カンマ連結）
    private String departmentDesc;
    //--------------------------------------------------------------
    
    // 診療科コード体系
    @Transient
    private String departmentCodeSys;
    
    // 健康保険
    private String healthInsurance;
    
    // 健康保険説明（名称）
    private String healthInsuranceDesc;
    
    // 健康保険コード体系
    @Transient
    private String healthInsuranceCodeSys;
    
    // 健康保険GUID （ORCAからの受付受信時に設定されている）
    private String healthInsuranceGUID;
    
    // 注意フラグ
    private boolean hasMark;
    
    // イメージ（シェーマ）フラグ
    private boolean hasImage;
    
    //　RPフラグ
    private boolean hasRp;
    
    // 処置フラグ
    private boolean hasTreatment;
    
    // 検体検査フラグ
    private boolean hasLaboTest;
    
    // 文書のバージョン番号（修正時++）
    private String versionNumber;
    
    // バージョン説明
    @Transient
    private String versionNotes;
    
    // 親文書 32bit ID
    private String parentId;
    
    // 親文書との関係
    private String parentIdRelation;
    
    // 親文書との説明
    @Transient
    private String parentIdDesc;
    
    // 親文書との関係コード体系
    @Transient
    private String parentIdCodeSys;
    
    // アクセス権
    @Transient
    private Collection<AccessRightModel> accessRights;
    
    // ステータス = DocumentModel.status
    @Transient
    private String status;

    // この文書を表示するクラス（紹介状等で使用）
    @Transient
    private String handleClass;

    //----------------------------------
    // Flag and param for senders
    //----------------------------------
    // 検体検査オーダー番号
    private String labtestOrderNumber;
    
    // 検体検査オーダー送信フラグ
    @Transient
    private boolean sendLabtest;

    // CLAIM送信フラグ
    @Transient
    private boolean sendClaim;

    // MML送信フラグ
    @Transient
    private boolean sendMml;
    
    // 処方せん出力
    @Transient
    private boolean priscriptionOutput;
    
    //----------------------------------
    // Claim Sender for JMS+MDB
    //----------------------------------
    // 診断に適用した健康保険
    @Transient
    private PVTHealthInsuranceModel pVTHealthInsuranceModel;
    
    // 施設（病院）名
    @Transient
    private String facilityName;
    
    // 医療資格
    @Transient
    private String createrLisence;
    
    // 患者ID
    @Transient
    private String patientId;
    
    // 患者氏名
    @Transient
    private String patientName;
    
    // 患者性別
    @Transient
    private String patientGender;
    
//minagawa^ 会計上送信日を変更(予定カルテ対応)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date claimDate;
//minagawa$    
    //----------------------------------
    // 処方せん出力に必要な情報
    //  担当医情報: Document->creatorから検索
    //  患者情報; Document->karteIdから検索
    //----------------------------------
    // 交付日
    @Transient
    private Date issuanceDate;
    
    // 保険医療機関番号
    @Transient
    private String InstitutionNumber;
    
    // 使用期間
    @Transient
    private Date period;
    
    // 備考欄患者住所、氏名転記フラグ
    @Transient
    private boolean chkPatientInfo;
    
    // 備考欄患者麻薬施用者転記フラグ
    @Transient
    private boolean chkUseDrugInfo;
    
    // 備考欄に「在宅」を記載するかどうかのフラグ
    @Transient
    private boolean chkHomeMedical;
    
    // 一般名を使用するかどうか
    @Transient
    private boolean useGeneralName;
    
    //----------------------------------
    
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
    
    public void setDocId(String docId) {
        this.docId = docId;
    }
    
    public String getDocId() {
        return docId;
    }
    
    public void setDocType(String docType) {
        this.docType = docType;
    }
    
    public String getDocType() {
        return docType;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurposeDesc(String purposeDesc) {
        this.purposeDesc = purposeDesc;
    }
    
    public String getPurposeDesc() {
        return purposeDesc;
    }
    
    public void setPurposeCodeSys(String purposeCodeSys) {
        this.purposeCodeSys = purposeCodeSys;
    }
    
    public String getPurposeCodeSys() {
        return purposeCodeSys;
    }
    
    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }
    
    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }
    
    public String getFirstConfirmDateTrimTime() {
        return ModelUtils.getDateAsString(getFirstConfirmDate());
    }
    
    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }
    
    public Date getConfirmDate() {
        return confirmDate;
    }
    
    public String getConfirmDateTrimTime() {
        return ModelUtils.getDateAsString(getConfirmDate());
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }
    
    public String getDepartmentDesc() {
        return departmentDesc;
    }
    
    /********************************************/
    public String getDepartmentName() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[0];
    }
    
    public String getDepartmentCode() {
        String[] tokens = tokenizeDept(departmentDesc);
        if (tokens[1] != null) {
            return tokens[1];
        }
        return getDepartment();
    }
    
    public String getAssignedDoctorName() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[2];
    }
    
    public String getAssignedDoctorId() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[3];
    }
    
    public String getJMARICode() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[4];
    }
    
    private String[] tokenizeDept(String dept) {
        
        // 診療科名、コード、担当医名、担当医コード、JMARI コード
        // を格納する配列を生成する
        String[] ret = new String[5];
        Arrays.fill(ret, null);
        
        if (dept != null) {
            try {
                String[] params = dept.split("\\s*,\\s*");
                System.arraycopy(params, 0, ret, 0, params.length);

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        
        return ret;
    }
    /********************************************/
    
    public void setDepartmentCodeSys(String departmentCodeSys) {
        this.departmentCodeSys = departmentCodeSys;
    }
    
    public String getDepartmentCodeSys() {
        return departmentCodeSys;
    }
    
    public void setHealthInsurance(String healthInsurance) {
        this.healthInsurance = healthInsurance;
    }
    
    public String getHealthInsurance() {
        return healthInsurance;
    }
    
    public void setHealthInsuranceDesc(String healthInsuranceDesc) {
        this.healthInsuranceDesc = healthInsuranceDesc;
    }
    
    public String getHealthInsuranceDesc() {
        return healthInsuranceDesc;
    }
    
    public void setHealthInsuranceCodeSys(String healthInsuranceCodeSys) {
        this.healthInsuranceCodeSys = healthInsuranceCodeSys;
    }
    
    public String getHealthInsuranceCodeSys() {
        return healthInsuranceCodeSys;
    }
    
    public void setHealthInsuranceGUID(String healthInsuranceGUID) {
        this.healthInsuranceGUID = healthInsuranceGUID;
    }
    
    public String getHealthInsuranceGUID() {
        return healthInsuranceGUID;
    }
    
    public void setHasMark(boolean hasMark) {
        this.hasMark = hasMark;
    }
    
    public boolean isHasMark() {
        return hasMark;
    }
    
    public Boolean isHasMarkBoolean() {
        return hasMark;
    }
    
    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
    
    public boolean isHasImage() {
        return hasImage;
    }
    
    public Boolean isHasImageBoolean() {
        return hasImage;
    }
    
    public void setHasRp(boolean hasRp) {
        this.hasRp = hasRp;
    }
    
    public boolean isHasRp() {
        return hasRp;
    }
    
    public Boolean isHasRpBoolean() {
        return hasRp;
    }
    
    public void setHasTreatment(boolean hasTreatment) {
        this.hasTreatment = hasTreatment;
    }
    
    public boolean isHasTreatment() {
        return hasTreatment;
    }
    
    public Boolean isHasTreatmentBoolean() {
        return hasTreatment;
    }
    
    public void setHasLaboTest(boolean hasLaboTest) {
        this.hasLaboTest = hasLaboTest;
    }
    
    public boolean isHasLaboTest() {
        return hasLaboTest;
    }
    
    public Boolean isHasLaboTestBoolean() {
        return hasLaboTest;
    }
    
    public void setVersionNumber(String version) {
        this.versionNumber = version;
    }
    
    public String getVersionNumber() {
        return versionNumber;
    }
    
    public void setVersionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
    }
    
    public String getVersionNotes() {
        return versionNotes;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentIdRelation(String parentIdRelation) {
        this.parentIdRelation = parentIdRelation;
    }
    
    public String getParentIdRelation() {
        return parentIdRelation;
    }
    
    public void setParentIdDesc(String relationDesc) {
        this.parentIdDesc = relationDesc;
    }
    
    public String getParentIdDesc() {
        return parentIdDesc;
    }
    
    public void setParentIdCodeSys(String relationCodeSys) {
        this.parentIdCodeSys = relationCodeSys;
    }
    
    public String getParentIdCodeSys() {
        return parentIdCodeSys;
    }
    
    public Collection<AccessRightModel> getAccessRights() {
        return accessRights;
    }
    
    public void setAccessRights(Collection<AccessRightModel> ac) {
        this.accessRights=ac;
    }
    
    public void addAccessRight(AccessRightModel accessRight) {
        if (accessRights == null) {
            setAccessRights(new ArrayList<AccessRightModel>(3));
        }
        accessRights.add(accessRight);
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getHandleClass() {
        return handleClass;
    }

    public void setHandleClass(String handleClass) {
        this.handleClass = handleClass;
    }
    
    @Override
    public int hashCode() {
        return docId.hashCode() + 11;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other != null && getClass() == other.getClass()) {
            return getDocId().equals(((DocInfoModel) other).getDocId());
        }
        return false;
    }
    
    @Override
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            Date val1 = getFirstConfirmDate();
            Date val2 = ((DocInfoModel) other).getFirstConfirmDate();
            int result = (val1!=null && val2!=null) ? val1.compareTo(val2) : 0;
            if (result == 0) {
                val1 = getConfirmDate();
                val2 = ((DocInfoModel) other).getConfirmDate();
                result = (val1!=null && val2!=null) ? val1.compareTo(val2) : 0;
            }
            return result;
        }
        return -1;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DocInfoModel ret = new DocInfoModel();
//        ret.setAccessRights(this.getAccessRights());
        ret.setConfirmDate(this.getConfirmDate());
        ret.setDepartment(this.getDepartment());
        ret.setDepartmentCodeSys(this.getDepartmentCodeSys());
        ret.setDepartmentDesc(this.getDepartmentDesc());
//        ret.setDocPk(this.getDocPk());
//        ret.setDocId(this.getDocId());  //
        ret.setDocType(this.getDocType());
        ret.setFirstConfirmDate(this.getFirstConfirmDate());
        ret.setHandleClass(this.getHandleClass());
        ret.setHasImage(this.isHasImage());
        ret.setHasLaboTest(this.isHasLaboTest());
        ret.setHasMark(this.isHasMark());
        ret.setHasRp(this.isHasRp());
        ret.setHasTreatment(this.isHasTreatment());
        ret.setHealthInsurance(this.getHealthInsurance());
        ret.setHealthInsuranceCodeSys(this.getHealthInsuranceCodeSys());
        ret.setHealthInsuranceDesc(this.getHealthInsuranceDesc());
        ret.setHealthInsuranceGUID(this.getHealthInsuranceGUID());
//        ret.setParentId(this.getParentId());
//        ret.setParentIdCodeSys(this.getParentIdCodeSys());
//        ret.setParentIdDesc(this.getParentIdDesc());
//        ret.setParentIdRelation(this.getParentIdRelation());
//        ret.setParentPk(this.getParentPk()); //
        ret.setPurpose(this.getPurpose());
        ret.setPurposeCodeSys(this.getPurposeCodeSys());
        ret.setPurposeDesc(this.getPurposeDesc());
        ret.setStatus(this.getStatus());
        ret.setTitle(this.getTitle());
        ret.setVersionNotes(this.getVersionNotes());
        ret.setVersionNumber(this.getVersionNumber());
        return ret;

        // ret.setDocPk(this.getDocPk());
        // ret.setDocId(this.getDocId());
        // ret.setParentPk(this.getParentPk());
    }

    public String getLabtestOrderNumber() {
        return labtestOrderNumber;
    }

    public void setLabtestOrderNumber(String labtestOrderNumber) {
        this.labtestOrderNumber = labtestOrderNumber;
    }

    public boolean isSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(boolean sendClaim) {
        this.sendClaim = sendClaim;
    }

    public boolean isSendLabtest() {
        return sendLabtest;
    }

    public void setSendLabtest(boolean sendLabtest) {
        this.sendLabtest = sendLabtest;
    }

    public boolean isSendMml() {
        return sendMml;
    }

    public void setSendMml(boolean sendMml) {
        this.sendMml = sendMml;
    }

    public PVTHealthInsuranceModel getPVTHealthInsuranceModel() {
        return getpVTHealthInsuranceModel();
    }

    public void setPVTHealthInsuranceModel(PVTHealthInsuranceModel selectedInsurance) {
        this.setpVTHealthInsuranceModel(selectedInsurance);
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
    
//minagawa^ CLAIM送信    (予定カルテ対応)
    public Date getClaimDate() {
        return claimDate;
    }
    
    public void setClaimDate(Date date) {
        claimDate = date;
    }
//minagawa$    

    public boolean isPriscriptionOutput() {
        return priscriptionOutput;
    }

    public void setPriscriptionOutput(boolean priscriptionOutput) {
        this.priscriptionOutput = priscriptionOutput;
    }

    public PVTHealthInsuranceModel getpVTHealthInsuranceModel() {
        return pVTHealthInsuranceModel;
    }

    public void setpVTHealthInsuranceModel(PVTHealthInsuranceModel pVTHealthInsuranceModel) {
        this.pVTHealthInsuranceModel = pVTHealthInsuranceModel;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getInstitutionNumber() {
        return InstitutionNumber;
    }

    public void setInstitutionNumber(String InstitutionNumber) {
        this.InstitutionNumber = InstitutionNumber;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public boolean isChkPatientInfo() {
        return chkPatientInfo;
    }

    public void setChkPatientInfo(boolean chkPatientInfo) {
        this.chkPatientInfo = chkPatientInfo;
    }

    public boolean isChkUseDrugInfo() {
        return chkUseDrugInfo;
    }

    public void setChkUseDrugInfo(boolean chkUseDrugInfo) {
        this.chkUseDrugInfo = chkUseDrugInfo;
    }

    public boolean isChkHomeMedical() {
        return chkHomeMedical;
    }

    public void setChkHomeMedical(boolean chkHomeMedical) {
        this.chkHomeMedical = chkHomeMedical;
    }

    public boolean isUseGeneralName() {
        return useGeneralName;
    }

    public void setUseGeneralName(boolean useGeneralName) {
        this.useGeneralName = useGeneralName;
    }
    
//minagawa^ 予定カルテ(予定カルテ対応)
    public boolean isScheduled() {
        boolean ret = (this.status!= null &&
                       this.status.equals(IInfoModel.STATUS_TMP) &&
                       this.getFirstConfirmDate()!= null &&
                       this.getConfirmDate()!=null &&
                       this.getFirstConfirmDate().after(this.getConfirmDate()));
        return ret;
    }
//minagawa$    
}
