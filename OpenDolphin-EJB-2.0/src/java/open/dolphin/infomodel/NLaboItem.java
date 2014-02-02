package open.dolphin.infomodel;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name="d_nlabo_item")
public class NLaboItem extends InfoModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 患者ID fid:Pid
    @Column (nullable = false)
    private String patientId;

    // 検体採取日
    @Column (nullable = false)
    private String sampleDate;

    // Labo コード
    private String laboCode;

    // 乳ビ
    private String lipemia;

    // 溶血
    private String hemolysis;

    // 透析前後
    private String dialysis;

    // ステータス
    private String reportStatus;

    // グループコード
    @Column (nullable = false)
    private String groupCode;

    // グループ名称
    private String groupName;

    // 検査項目コード・親
    @Column (nullable = false)
    private String parentCode;

    // 検査項目コード
    @Column (nullable = false)
    private String itemCode;

    // MEDIS コード
    private String medisCode;

    // 検査項目名
    @Column (nullable = false)
    private String itemName;

    // 異常区分
    private String abnormalFlg;

    // 基準値
    private String normalValue;

    // 検査結果
    @Column(name = "c_value")
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

    @ManyToOne
    @JoinColumn(name="laboModule_id", nullable=false)
    private NLaboModule laboModule;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NLaboItem)) {
            return false;
        }
        NLaboItem other = (NLaboItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "oms.ehr.entity.AbstractEntry[id=" + id + "]";
    }

    /**
     * @return the pid
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param pid the pid to set
     */
    public void setPatientId(String pid) {
        this.patientId = pid;
    }

    /**
     * @return the laboCode
     */
    public String getLaboCode() {
        return laboCode;
    }

    /**
     * @param laboCode the laboCode to set
     */
    public void setLaboCode(String laboCode) {
        this.laboCode = laboCode;
    }

    /**
     * @return the sampleDate
     */
    public String getSampleDate() {
        return sampleDate;
    }

    /**
     * @param sampleDate the sampleDate to set
     */
    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    /**
     * @return the lipemia
     */
    public String getLipemia() {
        return lipemia;
    }

    /**
     * @param lipemia the lipemia to set
     */
    public void setLipemia(String lipemia) {
        this.lipemia = lipemia;
    }

    /**
     * @return the hemolysis
     */
    public String getHemolysis() {
        return hemolysis;
    }

    /**
     * @param hemolysis the hemolysis to set
     */
    public void setHemolysis(String hemolysis) {
        this.hemolysis = hemolysis;
    }

    /**
     * @return the dialysis
     */
    public String getDialysis() {
        return dialysis;
    }

    /**
     * @param dialysis the dialysis to set
     */
    public void setDialysis(String dialysis) {
        this.dialysis = dialysis;
    }

    /**
     * @return the reportStatus
     */
    public String getReportStatus() {
        return reportStatus;
    }

    /**
     * @param reportStatus the reportStatus to set
     */
    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    /**
     * @return the groupCode
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * @param groupCode the groupCode to set
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the parentCode
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * @param parentCode the parentCode to set
     */
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @param itemCode the itemCode to set
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the medisCode
     */
    public String getMedisCode() {
        return medisCode;
    }

    /**
     * @param medisCode the medisCode to set
     */
    public void setMedisCode(String medisCode) {
        this.medisCode = medisCode;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the abnormalFlg
     */
    public String getAbnormalFlg() {
        return abnormalFlg;
    }

    /**
     * @param abnormalFlg the abnormalFlg to set
     */
    public void setAbnormalFlg(String abnormalFlg) {
        this.abnormalFlg = abnormalFlg;
    }

    /**
     * @return the normalValue
     */
    public String getNormalValue() {
        return normalValue;
    }

    /**
     * @param normalValue the normalValue to set
     */
    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the specimenCode
     */
    public String getSpecimenCode() {
        return specimenCode;
    }

    /**
     * @param specimenCode the specimenCode to set
     */
    public void setSpecimenCode(String specimenCode) {
        this.specimenCode = specimenCode;
    }

    /**
     * @return the specimenName
     */
    public String getSpecimenName() {
        return specimenName;
    }

    /**
     * @param specimenName the specimenName to set
     */
    public void setSpecimenName(String specimenName) {
        this.specimenName = specimenName;
    }

    /**
     * @return the commentCode1
     */
    public String getCommentCode1() {
        return commentCode1;
    }

    /**
     * @param commentCode1 the commentCode1 to set
     */
    public void setCommentCode1(String commentCode1) {
        this.commentCode1 = commentCode1;
    }

    /**
     * @return the comment1
     */
    public String getComment1() {
        return comment1;
    }

    /**
     * @param comment1 the comment1 to set
     */
    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    /**
     * @return the commentCode2
     */
    public String getCommentCode2() {
        return commentCode2;
    }

    /**
     * @param commentCode2 the commentCode2 to set
     */
    public void setCommentCode2(String commentCode2) {
        this.commentCode2 = commentCode2;
    }

    /**
     * @return the comment2
     */
    public String getComment2() {
        return comment2;
    }

    /**
     * @param comment2 the comment2 to set
     */
    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    /**
     * @return the laboModule
     */
    public NLaboModule getLaboModule() {
        return laboModule;
    }

    /**
     * @param laboModule the laboModule to set
     */
    public void setLaboModule(NLaboModule laboModule) {
        this.laboModule = laboModule;
    }

}
