package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;


/**
 * UserModel
 *
 * @author Minagawa,Kazushi
 *
 */
public final class UserModelConverter implements IInfoModelConverter {
    
    private UserModel model;

    public UserModelConverter(){
    }

    public long getId() {
        return model.getId();
    }

    public String getUserId() {
        return model.getUserId();
    }

    public String getPassword() {
        return model.getPassword();
    }

    public String getSirName() {
        return model.getSirName();
    }

    public String getGivenName() {
        return model.getGivenName();
    }

    public String getCommonName() {
        return model.getCommonName();
    }

    public LicenseModelConverter getLicenseModel() {
        if (model.getLicenseModel()!=null) {
            LicenseModelConverter con = new LicenseModelConverter();
            con.setModel(model.getLicenseModel());
            return con;
        }
        return null;
    }

    public DepartmentModelConverter getDepartmentModel() {
        if (model.getDepartmentModel()!=null) {
            DepartmentModelConverter con = new DepartmentModelConverter();
            con.setModel(model.getDepartmentModel());
            return con;
        }
        return null;
    }

    public FacilityModelConverter getFacilityModel() {
        if (model.getFacilityModel()!=null) {
            FacilityModelConverter con = new FacilityModelConverter();
            con.setModel(model.getFacilityModel());
            return con;
        }
        return null;
    }

    public List<RoleModelConverter> getRoles() {
        List<RoleModel> list = model.getRoles();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<RoleModelConverter> ret = new ArrayList<RoleModelConverter>();
        for (RoleModel m : list) {
            RoleModelConverter con = new RoleModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public String getMemberType() {
        return model.getMemberType();
    }

    public String getMemo() {
        return model.getMemo();
    }

    public Date getRegisteredDate() {
        return model.getRegisteredDate();
    }

    public String getEmail() {
        return model.getEmail();
    }

    public String getOrcaId() {
        return model.getOrcaId();
    }
    
//ヒロクリニック^    
    public String getUseDrugId() {
        return model.getUseDrugId();
    }
//ヒロクリニック$
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (UserModel)model;
    }
}
