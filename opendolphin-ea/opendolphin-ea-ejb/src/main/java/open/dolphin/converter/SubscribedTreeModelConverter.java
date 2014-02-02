package open.dolphin.converter;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SubscribedTreeModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SubscribedTreeModelConverter implements IInfoModelConverter {

    private SubscribedTreeModel model;

    public SubscribedTreeModelConverter() {

    }

    public long getId() {
        return model.getId();
    }

    public long getTreeId() {
        return model.getTreeId();
    }

    public UserModel getUserMode() {
        return model.getUserModel();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (SubscribedTreeModel)model;
    }
}
