package open.dolphin.converter;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientVisitModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PatientVisitModelConverter implements IInfoModelConverter {

    private PatientVisitModel model;

    public PatientVisitModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public PatientModelConverter getPatientModel() {
        if (model.getPatientModel()!=null) {
            PatientModelConverter con = new PatientModelConverter();
            con.setModel(model.getPatientModel());
            return con;
        }
        return null;
    }

    public String getFacilityId() {
        return model.getFacilityId();
    }

    public String getPvtDate() {
        return model.getPvtDate();
    }

    public String getAppointment() {
        return model.getAppointment();
    }

    //--------------------------------
    public String getDepartment() {
        return model.getDepartment();
    }
    //--------------------------------

    public int getState() {
        return model.getState();
    }

    public String getInsuranceUid() {
        return model.getInsuranceUid();
    }

    public String getDeptCode() {
        return model.getDeptCode();
    }

    public String getDeptName() {
        return model.getDeptName();
    }

    public String getDoctorId() {
        return model.getDoctorId();
    }

    public String getDoctorName() {
        return model.getDoctorName();
    }

    public String getJmariNumber() {
        return model.getJmariNumber();
    }

    public String getFirstInsurance() {
        return model.getFirstInsurance();
    }

    public String getMemo() {
        return model.getMemo();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientVisitModel)model;
    }
}
