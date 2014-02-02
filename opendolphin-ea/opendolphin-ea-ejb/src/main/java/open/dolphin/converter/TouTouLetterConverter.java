package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class TouTouLetterConverter implements IInfoModelConverter {

    private TouTouLetter model;

    public TouTouLetterConverter() {
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
        return model.getStatus();
    }

    public UserModel getUserModel() {
        return model.getUserModel();
    }

    public KarteBean getKarteBean() {
        return model.getKarteBean();
    }

    //-----------------------------------------------------------

    public byte[] getBeanBytes() {
        return model.getBeanBytes();
    }

    //-----------------------------------------------------------

    public String getConsultantHospital() {
        return model.getConsultantHospital();
    }

    public String getConsultantDept() {
        return model.getConsultantDept();
    }

    public String getConsultantDoctor() {
        return model.getConsultantDoctor();
    }

    public String getClientHospital() {
        return model.getClientHospital();
    }

    public String getPatientName() {
        return model.getPatientName();
    }

    public String getPatientGender() {
        return model.getPatientGender();
    }

    public String getPatientBirthday() {
        return model.getPatientBirthday();
    }

    public String getDisease() {
        return model.getDisease();
    }

    public String getPurpose() {
        return model.getPurpose();
    }

    public String getClientName() {
        return model.getClientName();
    }

    public String getClientAddress() {
        return model.getClientAddress();
    }

    public String getClientTelephone() {
        return model.getClientTelephone();
    }

    public String getClientFax() {
        return model.getClientFax();
    }

    public String getPatientAge() {
        return model.getPatientAge();
    }

    public String getPastFamily() {
        return model.getPastFamily();
    }

    public String getClinicalCourse() {
        return model.getClinicalCourse();
    }

    public String getMedication() {
        return model.getMedication();
    }

    public String getRemarks() {
        return model.getRemarks();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (TouTouLetter)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
