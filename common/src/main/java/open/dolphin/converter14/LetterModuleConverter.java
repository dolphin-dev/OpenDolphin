package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.*;

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

    public UserModelConverter getUserModel() {
        if (model.getUserModel()!=null) {
            UserModelConverter conv = new UserModelConverter();
            conv.setModel(model.getUserModel());
            return conv;
        }
        return null;
    }

    public KarteBeanConverter getKarteBean() {
        if (model.getKarteBean()!=null) {
            KarteBeanConverter conv = new KarteBeanConverter();
            conv.setModel(model.getKarteBean());
            return conv;
        }
        return null;
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
    public List<LetterItemConverter> getLetterItems() {
        List<LetterItem> list = model.getLetterItems();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<LetterItemConverter> ret = new ArrayList<LetterItemConverter>();
        for (LetterItem m : list) {
            LetterItemConverter con = new LetterItemConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<LetterTextConverter> getLetterTexts() {
        List<LetterText> list = model.getLetterTexts();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<LetterTextConverter> ret = new ArrayList<LetterTextConverter>();
        for (LetterText m : list) {
            LetterTextConverter con = new LetterTextConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<LetterDateConverter> getLetterDates() {
        List<LetterDate> list = model.getLetterDates();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<LetterDateConverter> ret = new ArrayList<LetterDateConverter>();
        for (LetterDate m : list) {
            LetterDateConverter con = new LetterDateConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
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
