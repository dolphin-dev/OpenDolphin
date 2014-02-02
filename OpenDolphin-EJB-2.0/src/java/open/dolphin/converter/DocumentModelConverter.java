package open.dolphin.converter;

import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DocumentModelConverter implements IInfoModelConverter {

    private DocumentModel model;

    public DocumentModelConverter() {
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

    public DocInfoModel getDocInfoModel() {
        return model.getDocInfoModel();
    }

    public List<ModuleModel> getModules() {
        return model.getModules();
    }

    public List<SchemaModel> getSchema() {
        return model.getSchema();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (DocumentModel)m;
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        dummyUser.setCommonName(model.getUserModel().getCommonName());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
