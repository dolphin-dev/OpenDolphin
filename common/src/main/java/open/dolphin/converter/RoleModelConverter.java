package open.dolphin.converter;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.RoleModel;

/**
 * RoleModel
 *
 * @author Minagawa,Kazushi
 */
public final class RoleModelConverter implements IInfoModelConverter {
    
    private RoleModel model;

    public RoleModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public String getUserId() {
        return model.getUserId();
    }

    public String getRole() {
        return model.getRole();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (RoleModel)model;
    }
}
