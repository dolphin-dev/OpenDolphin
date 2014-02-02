package open.dolphin.converter;

import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.*;


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

    public LicenseModel getLicenseModel() {
        return model.getLicenseModel();
    }

    public DepartmentModel getDepartmentModel() {
        return model.getDepartmentModel();
    }

    public FacilityModel getFacilityModel() {
        return model.getFacilityModel();
    }

    public List<RoleModel> getRoles() {
        return model.getRoles();
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

    @Override
    public void setModel(IInfoModel model) {
        this.model = (UserModel)model;
    }
}
