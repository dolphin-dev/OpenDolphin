package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ModuleModelConverter implements IInfoModelConverter {

    private ModuleModel model;

    public ModuleModelConverter() {
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

//    public UserModel getUserModel() {
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

    public ModuleInfoBeanConverter getModuleInfoBean() {
        if (model.getModuleInfoBean()!=null) {
            ModuleInfoBeanConverter con = new ModuleInfoBeanConverter();
            con.setModel(model.getModuleInfoBean());
            return con;
        }
        return null;
    }

    public byte[] getBeanBytes() {
        return model.getBeanBytes();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (ModuleModel)m;
        
        if (model.getKarteBean()!=null && model.getUserModel()!=null) {
            KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
            UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
            model.setKarteBean(dummyKarteBean);
            model.setUserModel(dummyUser);
        }
    }
}
