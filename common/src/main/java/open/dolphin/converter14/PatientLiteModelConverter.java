package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientLiteModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PatientLiteModelConverter implements IInfoModelConverter {

    private PatientLiteModel model;

    public PatientLiteModelConverter() {
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getFullName() {
        return model.getFullName();
    }

    public String getKanaName() {
        return model.getKanaName();
    }

    public String getGender() {
        return model.getGender();
    }

    public String getGenderDesc() {
        return model.getGenderDesc();
    }

    public String getBirthday() {
        return model.getBirthday();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientLiteModel)model;
    }
}
