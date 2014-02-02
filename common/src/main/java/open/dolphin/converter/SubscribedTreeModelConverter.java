package open.dolphin.converter;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SubscribedTreeModel;

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

    public UserModelConverter getUserModel() {
        if (model.getUserModel()!=null) {
            UserModelConverter con = new UserModelConverter();
            con.setModel(model.getUserModel());
            return con;
        }
        return null;
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (SubscribedTreeModel)model;
    }
}
