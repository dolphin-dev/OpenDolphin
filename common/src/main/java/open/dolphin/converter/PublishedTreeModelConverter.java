package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PublishedTreeModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PublishedTreeModelConverter implements IInfoModelConverter {

    private PublishedTreeModel model;

    public PublishedTreeModelConverter() {
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

    public byte[] getTreeBytes() {
        return model.getTreeBytes();
    }

    public String getTreeXml() {
        return model.getTreeXml();
    }

    public Date getLastUpdated() {
        return model.getLastUpdated();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PublishedTreeModel)model;
    }
}
