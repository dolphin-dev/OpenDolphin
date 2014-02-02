package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class RegisteredDiagnosisModelConverter implements IInfoModelConverter {

    private RegisteredDiagnosisModel model;

    public RegisteredDiagnosisModelConverter() {
    }

    //----------------------------------------------------

    public long getId() {
        return model.getId();
    }

    public Date getConfirmed() {
        return model.getConfirmed();
    }

    public Date getStarted() {
        return model.getStarted();
    }

    public Date getEnded() {
        return model.getEnded();
    }

    public Date getRecorded() {
        return model.getRecorded();
    }

    public long getLinkId() {
        return model.getLinkId();
    }

    public String getLinkRelation() {
        return model.getLinkRelation();
    }

    public String getStatus() {
        return IInfoModel.STATUS_FINAL;
        //return model.getStatus();
    }

    public UserModel getUserModel() {
        return model.getUserModel();
    }

    public KarteBean getKarteBean() {
        return model.getKarteBean();
    }

    //-----------------------------------------------------------

    public String getDiagnosis() {
        return model.getDiagnosis();
    }

    public String getDiagnosisCode() {
        return model.getDiagnosisCode();
    }

    public String getDiagnosisCodeSystem() {
        return model.getDiagnosisCodeSystem();
    }

    public String getFirstEncounterDate() {
        return model.getFirstEncounterDate();
    }

    public String getRelatedHealthInsurance() {
        return model.getRelatedHealthInsurance();
    }

    public DiagnosisCategoryModel getDiagnosisCategoryModel() {
        return model.getDiagnosisCategoryModel();
    }

    public DiagnosisOutcomeModel getDiagnosisOutcomeModel() {
        return model.getDiagnosisOutcomeModel();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (RegisteredDiagnosisModel)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
