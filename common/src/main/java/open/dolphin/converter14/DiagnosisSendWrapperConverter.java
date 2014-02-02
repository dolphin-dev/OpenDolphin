package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.DiagnosisSendWrapper;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 *
 * @author Kazushi Minagawa.
 */
public class DiagnosisSendWrapperConverter implements IInfoModelConverter {
    
    private DiagnosisSendWrapper model;
    
    public boolean getSendClaim() {
        return model.getSendClaim();
    }
        
    public String getConfirmDate() {
        return model.getConfirmDate();
    }

    public String getTitle() {
        return model.getTitle();
    }

    public String getPurpose() {
        return model.getPurpose();
    }

    public String getGroupId() {
        return model.getGroupId();
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getPatientName() {
        return model.getPatientName();
    }

    public String getPatientGender() {
        return model.getPatientGender();
    }

    public String getFacilityName() {
        return model.getFacilityName();
    }

    public String getJamariCode() {
        return model.getJamariCode();
    }

    public String getDepartment() {
        return model.getDepartment();
    }

    public String getDepartmentDesc() {
        return model.getDepartmentDesc();
    }

    public String getCreatorName() {
        return model.getCreatorName();
    }

    public String getCreatorId() {
        return model.getCreatorId();
    }

    public String getCreatorLicense() {
        return model.getCreatorLicense();
    }

    public List<RegisteredDiagnosisModelConverter> getAddedDiagnosis() {
        List<RegisteredDiagnosisModel> list = model.getAddedDiagnosis();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<RegisteredDiagnosisModelConverter> ret = new ArrayList<RegisteredDiagnosisModelConverter>();
        for (RegisteredDiagnosisModel m : list) {
            RegisteredDiagnosisModelConverter con = new RegisteredDiagnosisModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<RegisteredDiagnosisModelConverter> getUpdatedDiagnosis() {
        List<RegisteredDiagnosisModel> list = model.getUpdatedDiagnosis();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<RegisteredDiagnosisModelConverter> ret = new ArrayList<RegisteredDiagnosisModelConverter>();
        for (RegisteredDiagnosisModel m : list) {
            RegisteredDiagnosisModelConverter con = new RegisteredDiagnosisModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiagnosisSendWrapper)model;
    }
}
