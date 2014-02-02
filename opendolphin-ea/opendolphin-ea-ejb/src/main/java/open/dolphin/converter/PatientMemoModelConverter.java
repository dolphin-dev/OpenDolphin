package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PatientMemoModelConverter implements IInfoModelConverter {

    private PatientMemoModel model;

    public PatientMemoModelConverter() {
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

    public String getMemo() {
//        if (model.getMemo()!=null) {
//            return model.getMemo().trim();
//        }
//        return null;
        return model.getMemo();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (PatientMemoModel)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
