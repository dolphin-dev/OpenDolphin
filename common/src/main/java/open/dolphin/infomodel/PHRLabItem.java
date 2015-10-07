package open.dolphin.infomodel;

import java.io.Serializable;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PHRLabItem implements Serializable {

    // 患者ID fid:Pid
    private String patientId;

    // 検体採取日
    private String sampleDate;

    // Labo コード
    private String labCode;

    // 乳ビ
    private String lipemia;

    // 溶血
    private String hemolysis;

    // 透析前後
    private String dialysis;

    // ステータス
    private String reportStatus;

    // グループコード
    private String groupCode;

    // グループ名称
    private String groupName;

    // 検査項目コード・親
    private String parentCode;

    // 検査項目コード
    private String itemCode;

    // MEDIS コード
    private String medisCode;

    // 検査項目名
    private String itemName;

    // 異常区分
    private String abnormalFlg;

    // 基準値
    private String normalValue;

    // 検査結果
    private String value;

    // 単位
    private String unit;

    // 検査材料コード
    private String specimenCode;

    // 検査材料名
    private String specimenName;

    // コメントコード1
    private String commentCode1;

    // コメント1
    private String comment1;

    // コメントコード2
    private String commentCode2;

    // コメント2
    private String comment2;

    // Sort Key
    private String sortKey;
    
    private String module_id;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String laboCode) {
        this.labCode = laboCode;
    }

    public String getLipemia() {
        return lipemia;
    }

    public void setLipemia(String lipemia) {
        this.lipemia = lipemia;
    }

    public String getHemolysis() {
        return hemolysis;
    }

    public void setHemolysis(String hemolysis) {
        this.hemolysis = hemolysis;
    }

    public String getDialysis() {
        return dialysis;
    }

    public void setDialysis(String dialysis) {
        this.dialysis = dialysis;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getMedisCode() {
        return medisCode;
    }

    public void setMedisCode(String medisCode) {
        this.medisCode = medisCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAbnormalFlg() {
        return abnormalFlg;
    }

    public void setAbnormalFlg(String abnormalFlg) {
        this.abnormalFlg = abnormalFlg;
    }

    public String getNormalValue() {
        return normalValue;
    }

    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSpecimenCode() {
        return specimenCode;
    }

    public void setSpecimenCode(String specimenCode) {
        this.specimenCode = specimenCode;
    }

    public String getSpecimenName() {
        return specimenName;
    }

    public void setSpecimenName(String specimenName) {
        this.specimenName = specimenName;
    }

    public String getCommentCode1() {
        return commentCode1;
    }

    public void setCommentCode1(String commentCode1) {
        this.commentCode1 = commentCode1;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getCommentCode2() {
        return commentCode2;
    }

    public void setCommentCode2(String commentCode2) {
        this.commentCode2 = commentCode2;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }
    
    
}
