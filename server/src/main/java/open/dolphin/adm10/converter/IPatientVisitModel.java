package open.dolphin.adm10.converter;

import open.dolphin.converter.*;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientVisitModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class IPatientVisitModel implements IInfoModelConverter {

    private PatientVisitModel model;

    public IPatientVisitModel() {
    }

    // PK
    public long getId() {
        return model.getId();
    }

    // PatientModel
    public IPatientModel getPatientModel() {
        if (model.getPatientModel()!=null) {
            IPatientModel con = new IPatientModel();
            con.setModel(model.getPatientModel());
            return con;
        }
        return null;
    }

    // 施設ID
    public String getFacilityId() {
        return model.getFacilityId();
    }

    // 来院日時
    public String getPvtDate() {
        return model.getPvtDate();
    }

    // 予約
    public String getAppointment() {
        return model.getAppointment();
    }

    // 診療科
    //--------------------------------
    public String getDepartment() {
        return model.getDepartment();
    }
    //--------------------------------

    // 状態
    public int getState() {
        return model.getState();
    }

    // 受付した健康保険のUUID
    public String getInsuranceUid() {
        return model.getInsuranceUid();
    }

    // 診療科コード
    public String getDeptCode() {
        return model.getDeptCode();
    }

    // 診療科名
    public String getDeptName() {
        return model.getDeptName();
    }

    // 担当医コード
    public String getDoctorId() {
        return model.getDoctorId();
    }

    // 担当医名
    public String getDoctorName() {
        return model.getDoctorName();
    }

    // JMARIコード
    public String getJmariNumber() {
        return model.getJmariNumber();
    }

    // 複数ある保険の最初の保険名(受付表示に使用する便利メソッド）
    public String getFirstInsurance() {
        return model.getFirstInsurance();
    }

    // メモ
    public String getMemo() {
        return model.getMemo();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientVisitModel)model;
    }
}
