package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ObservationModelConverter implements IInfoModelConverter {

    private ObservationModel model;

    public ObservationModelConverter() {
        
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

    public String getObservation() {
        return model.getObservation();
    }

    public String getPhenomenon() {
        return model.getPhenomenon();
    }

    public String getValue() {
        return model.getValue();
    }

    public String getUnit() {
        return model.getUnit();
    }

    public String getCategoryValue() {
        return model.getCategoryValue();
    }

    public String getValueDesc() {
        return model.getValueDesc();
    }

    public String getValueSys() {
        return model.getValueSys();
    }

    public String getMemo() {
        return model.getMemo();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (ObservationModel)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
