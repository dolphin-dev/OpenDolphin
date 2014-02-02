package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.UserModel;

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

    public UserModel getUserModel() {
        return model.getUserModel();
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

    public String getTreeXml() {
        return model.getTreeXml();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampTreeModel)model;
    }
}
