package open.dolphin.converter;

import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LetterDate;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterItem;
import open.dolphin.infomodel.LetterText;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LetterModuleConverter implements IInfoModelConverter {

    private LetterModule model;

    public LetterModuleConverter() {
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

    public String getTitle() {
        return model.getTitle();
    }
    
    public String getLetterType() {
        return model.getLetterType();
    }

    public String getHandleClass() {
        return model.getHandleClass();
    }

    public String getClientHospital() {
        return model.getClientHospital();
    }

    public String getClientDept() {
        return model.getClientDept();
    }

    public String getClientDoctor() {
        return model.getClientDoctor();
    }

    public String getClientZipCode() {
        return model.getClientZipCode();
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

    public String getConsultantHospital() {
        return model.getConsultantHospital();
    }

    public String getConsultantDept() {
        return model.getConsultantDept();
    }

    public String getConsultantDoctor() {
        return model.getConsultantDoctor();
    }

    public String getConsultantZipCode() {
        return model.getConsultantZipCode();
    }

    public String getConsultantAddress() {
        return model.getConsultantAddress();
    }

    public String getConsultantTelephone() {
        return model.getConsultantTelephone();
    }

    public String getConsultantFax() {
        return model.getConsultantFax();
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getPatientName() {
        return model.getPatientName();
    }

    public String getPatientKana() {
        return model.getPatientKana();
    }

    public String getPatientGender() {
        return model.getPatientGender();
    }

    public String getPatientBirthday() {
        return model.getPatientBirthday();
    }

    public String getPatientAge() {
        return model.getPatientAge();
    }

    public String getPatientOccupation() {
        return model.getPatientOccupation();
    }

    public String getPatientZipCode() {
        return model.getPatientZipCode();
    }

    public String getPatientAddress() {
        return model.getPatientAddress();
    }

    public String getPatientTelephone() {
        return model.getPatientTelephone();
    }

    public String getPatientMobilePhone() {
        return model.getPatientMobilePhone();
    }

    public String getPatientFaxNumber() {
        return model.getPatientFaxNumber();
    }

    //------------------------------------------------
    public List<LetterItem> getLetterItems() {
        return model.getLetterItems();
    }

    public List<LetterText> getLetterTexts() {
        return model.getLetterTexts();
    }

    public List<LetterDate> getLetterDates() {
        return model.getLetterDates();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (LetterModule)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
