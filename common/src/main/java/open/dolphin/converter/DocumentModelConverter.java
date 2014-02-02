package open.dolphin.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.*;

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

//    // @ManyToOne 外部制約
//    public UserModel getUserModel() {
//        return model.getUserModel();
//    }
//
//    // @ManyToOne 外部制約
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

    public DocInfoModelConverter getDocInfoModel() {
        if (model.getDocInfoModel()!=null) {
            DocInfoModelConverter con = new DocInfoModelConverter();
            con.setModel(model.getDocInfoModel());
            return con;
        }
        return null;
    }

    public List<ModuleModelConverter> getModules() {
        List<ModuleModel> list = model.getModules();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<ModuleModelConverter> ret = new ArrayList<ModuleModelConverter>();
        for (ModuleModel m : list) {
            ModuleModelConverter con = new ModuleModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<SchemaModelConverter> getSchema() {
        List<SchemaModel> list = model.getSchema();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<SchemaModelConverter> ret = new ArrayList<SchemaModelConverter>();
        for (SchemaModel m : list) {
            SchemaModelConverter con = new SchemaModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }
    
    public List<AttachmentModelConverter> getAttachment() {
        List<AttachmentModel> list = model.getAttachment();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<AttachmentModelConverter> ret = new ArrayList<AttachmentModelConverter>();
        for (AttachmentModel m : list) {
            AttachmentModelConverter con = new AttachmentModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (DocumentModel)m;
        
        // 外部制約先の全ての情報は必要ないためPK以外の属性はnullにしたダミーモデルを設定する
        KarteBean dummyKarteBean = PlistConverter.createDuumyKarteBean(model.getKarteBean().getId());
        UserModel dummyUser = PlistConverter.createDummyUserModel(model.getUserModel().getId());
        dummyUser.setCommonName(model.getUserModel().getCommonName());
        model.setKarteBean(dummyKarteBean);
        model.setUserModel(dummyUser);
    }
}
