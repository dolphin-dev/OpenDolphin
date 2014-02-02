package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class AttachmentModelConverter implements IInfoModelConverter {

    private AttachmentModel model;

    public AttachmentModelConverter() {
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
    
    public String getFileName() {
        return model.getFileName();
    }
    
    public String getContentType() {
        return model.getContentType();
    }
    
    public long getContentSize() {
        return model.getContentSize();
    }
    
    public long getLastModified() {
        return model.getLastModified();
    }
    
    public String getDigest() {
        return model.getDigest();
    }

    public String getTitle() {
        return model.getTitle();
    }

    public String getExtension() {
        return model.getExtension();
    }

    public String getUri() {
        return model.getUri();
    }

    public String getMemo() {
        return model.getMemo();
    }

    public byte[] getBytes() {
        return model.getBytes();
    }
    
    @Override
    public void setModel(IInfoModel m) {
        this.model = (AttachmentModel)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
