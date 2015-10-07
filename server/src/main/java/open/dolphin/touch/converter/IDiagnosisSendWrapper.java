package open.dolphin.touch.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.*;

/**
 * 病名を送信（DB保存＆CLAIM送信）をするためのラッパークラス。
 * 
 * @author kazushi Minagawa.
 */
public class IDiagnosisSendWrapper extends InfoModel implements Serializable {
    
    // flag
    private String sendClaim;
    
    // 確定日
    private String confirmDate;
    
    // MML DocInfo用の  Title
    private String title;
    
    // MML DocInfo用の  purpose
    private String purpose;
    
    // MML DocInfo用の  groupId
    private String groupId;
    
    // 患者ID
    private String patientId;
    
    // 患者氏名
    private String patientName;
    
    // 患者性別
    private String patientGender;
    
    // 施設名
    private String facilityName;
    
    // JMARI code
    private String jamariCode;
    
    // 診療科コード
    private String department;
    
    // 診療科名
    private String departmentDesc;
    
    // 担当医名
    private String creatorName;
    
    // 担当医ID
    private String creatorId;
    
    // 担当医医療資格
    private String creatorLicense;
    
    // 新規に追加された病名のリスト
    private List<IRegisteredDiagnosis> addedDiagnosis;
    
    // 更新された（転帰等）病名のリスト
    private List<IRegisteredDiagnosis> updatedDiagnosis;
    
    
    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmdate) {
        this.confirmDate = confirmdate;
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

    public void setPurpose(String purpse) {
        this.purpose = purpse;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getJamariCode() {
        return jamariCode;
    }

    public void setJamariCode(String jamariCode) {
        this.jamariCode = jamariCode;
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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorLicense() {
        return creatorLicense;
    }

    public void setCreatorLicense(String creatorLicense) {
        this.creatorLicense = creatorLicense;
    }

    public List<IRegisteredDiagnosis> getAddedDiagnosis() {
        return addedDiagnosis;
    }

    public void setAddedDiagnosis(List<IRegisteredDiagnosis> addedDiagnosis) {
        this.addedDiagnosis = addedDiagnosis;
    }

    public List<IRegisteredDiagnosis> getUpdatedDiagnosis() {
        return updatedDiagnosis;
    }

    public void setUpdatedDiagnosis(List<IRegisteredDiagnosis> updatedDiagnosis) {
        this.updatedDiagnosis = updatedDiagnosis;
    }

    public String getSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(String sendClaim) {
        this.sendClaim = sendClaim;
    }
    
    public DiagnosisSendWrapper toModel() {
        
        DiagnosisSendWrapper ret = new DiagnosisSendWrapper();
        
        // stringtoBoolean
        ret.setSendClaim(IOSHelper.toBool(this.getSendClaim()));
    
        // 確定日
        ret.setConfirmDate(this.getConfirmDate());
    
        // MML DocInfo用の  Title
        ret.setTitle(this.getTitle());

        // MML DocInfo用の  purpose
        ret.setPurpose(this.getPurpose());

        // MML DocInfo用の  groupId
        ret.setGroupId(this.getGroupId());

        // 患者ID
        ret.setPatientId(this.getPatientId());

        // 患者氏名
        ret.setPatientName(this.getPatientName());

        // 患者性別
        ret.setPatientGender(this.getPatientGender());

        // 施設名
        ret.setFacilityName(this.getFacilityName());

        // JMARI code
        ret.setJamariCode(this.getJamariCode());

        // 診療科コード
        ret.setDepartment(this.getDepartment());

        // 診療科名
        ret.setDepartmentDesc(this.getDepartmentDesc());

        // 担当医名
        ret.setCreatorName(this.getCreatorName());

        // 担当医ID
        ret.setCreatorId(this.getCreatorId());

        // 担当医医療資格
        ret.setCreatorLicense(this.getCreatorLicense());
        
        // 追加病名
        if (addedDiagnosis!=null && addedDiagnosis.size()>0) {
            List<RegisteredDiagnosisModel> list = new ArrayList();
            for (IRegisteredDiagnosis rd : addedDiagnosis) {
                list.add(rd.toModel());
            }
            ret.setAddedDiagnosis(list);
        }
        
        // 更新病名
        if (updatedDiagnosis!=null && updatedDiagnosis.size()>0) {
            List<RegisteredDiagnosisModel> list = new ArrayList();
            for (IRegisteredDiagnosis rd : updatedDiagnosis) {
                list.add(rd.toModel());
            }
            ret.setUpdatedDiagnosis(list);
        }
        
//        StringBuilder sb = new StringBuilder();
//        sb.append(ret.getSendClaim()).append("\n");
//        sb.append(ret.getConfirmDate()).append("\n");
//        // MML DocInfo用の  purpose
//        sb.append(ret.getPurpose()).append("\n");
//
//        // MML DocInfo用の  groupId
//        sb.append(ret.getGroupId()).append("\n");
//
//        // 患者ID
//        sb.append(ret.getPatientId()).append("\n");
//
//        // 患者氏名
//        sb.append(ret.getPatientName()).append("\n");
//
//        // 患者性別
//        sb.append(ret.getPatientGender()).append("\n");
//
//        // 施設名
//        sb.append(ret.getFacilityName()).append("\n");
//
//        // JMARI code
//        sb.append(ret.getJamariCode()).append("\n");
//
//        // 診療科コード
//        sb.append(ret.getDepartment()).append("\n");
//
//        // 診療科名
//        sb.append(ret.getDepartmentDesc()).append("\n");
//
//        // 担当医名
//        sb.append(ret.getCreatorName()).append("\n");
//
//        // 担当医ID
//        sb.append(ret.getCreatorId()).append("\n");
//
//        // 担当医医療資格
//        sb.append(ret.getCreatorLicense()).append("\n");
//        
//        if (ret.getAddedDiagnosis()!=null) {
//            for (RegisteredDiagnosisModel rd : ret.getAddedDiagnosis()) {
//                sb.append(rd.getDiagnosis()).append("\n");
//                sb.append(rd.getDiagnosisCode()).append("\n");
//                sb.append(rd.getCategory()).append("\n");
//                sb.append(rd.getCategoryDesc()).append("\n");
//                sb.append(rd.getStarted()).append("\n");
//            }
//        }
//        
//        if (ret.getUpdatedDiagnosis()!=null) {
//            for (RegisteredDiagnosisModel rd : ret.getUpdatedDiagnosis()) {
//                sb.append(rd.getDiagnosis()).append("\n");
//                sb.append(rd.getDiagnosisCode()).append("\n");
//                sb.append(rd.getCategory()).append("\n");
//                sb.append(rd.getCategoryDesc()).append("\n");
//                sb.append(rd.getOutcome()).append("\n");
//                sb.append(rd.getOutcomeDesc()).append("\n");
//                sb.append(rd.getStarted()).append("\n");
//                sb.append(rd.getRecorded()).append("\n");
//                sb.append(rd.getEnded()).append("\n");
//            }
//        }
//        
//        System.err.print(sb.toString());
        
        return ret;
    }
}
