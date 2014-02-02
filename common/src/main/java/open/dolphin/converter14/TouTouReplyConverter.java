package open.dolphin.converter14;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.TouTouReply;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class TouTouReplyConverter implements IInfoModelConverter {

    private TouTouReply model;

    public TouTouReplyConverter() {
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

//   public UserModel getUserModel() {
//        return model.getUserModel();
//    }
//
//    public KarteBean getKarteBean() {
//        return model.getKarteBean();
//    }
    public UserModelConverter getUserModel() {
        if (model.getUserModel()!=null) {
            UserModelConverter con = new UserModelConverter();
            con.setModel(model.getUserModel());
            return con;
        }
        return null;
    }

    public KarteBeanConverter getKarteBean() {
        if (model.getKarteBean()!=null) {
            KarteBeanConverter con = new KarteBeanConverter();
            con.setModel(model.getKarteBean());
            return con;
        }
        return null;
    }

    //-----------------------------------------------------------

    public byte[] getBeanBytes() {
        return model.getBeanBytes();
    }

    //-----------------------------------------------------------
    
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

    public String getPatientAge() {
        return model.getPatientAge();
    }

    public String getClientDept() {
        return model.getClientDept();
    }

    public String getClientDoctor() {
        return model.getClientDoctor();
    }

    public String getInformedContent() {
        return model.getInformedContent();
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

    public String getVisited() {
        return model.getVisited();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (TouTouReply)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
