package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.StampTreeModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampTreeModelConverter implements IInfoModelConverter {

    private StampTreeModel model;

    public StampTreeModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public UserModelConverter getUserModel() {
        if (model.getUserModel()!=null) {
            UserModelConverter con = new UserModelConverter();
            con.setModel(model.getUserModel());
            return con;
        }
        return null;
    }

    public String getName() {
        return model.getName();
    }

    public String getPublishType() {
        return model.getPublishType();
    }

    public String getCategory() {
        return model.getCategory();
    }

    public String getPartyName() {
        return model.getPartyName();
    }

    public String getUrl() {
        return model.getUrl();
    }

    public String getDescription() {
        return model.getDescription();
    }

    public Date getPublishedDate() {
        return model.getPublishedDate();
    }

    public Date getLastUpdated() {
        return model.getLastUpdated();
    }

    public String getPublished() {
        return model.getPublished();
    }

    public byte[] getTreeBytes() {
        return model.getTreeBytes();
    }

// XML 送信不可    
//    public String getTreeXml() {
//        return model.getTreeXml();
//    }
 
//minagawa^ 排他制御    
    public String getVersionNumber() {
        return model.getVersionNumber();
    }
//minagawa$    

    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampTreeModel)model;
    }
}
