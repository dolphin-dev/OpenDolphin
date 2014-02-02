package open.dolphin.converter14;

import java.util.Date;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class FacilityModelConverter implements IInfoModelConverter {

    private FacilityModel model;

    public FacilityModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public String getFacilityId() {
        return model.getFacilityId();
    }

    public String getFacilityName() {
        return model.getFacilityName();
    }

    public String getZipCode() {
        return model.getZipCode();
    }

    public String getAddress() {
        return model.getAddress();
    }

    public String getTelephone() {
        return model.getTelephone();
    }

    public String getFacsimile() {
        return model.getFacsimile();
    }

    public String getUrl() {
        return model.getUrl();
    }

    public Date getRegisteredDate() {
        return model.getRegisteredDate();
    }

    public String getMemberType() {
        return model.getMemberType();
    }

    public String getS3URL() {
        return model.getS3URL();
    }

    public String getS3AccessKey() {
        return model.getS3AccessKey();
    }

    public String getS3SecretKey() {
        return model.getS3SecretKey();
    }
    
//    public String getInsuraceFacilityId() {
//        return model.getInsuraceFacilityId();
//    }
//
//    public String getJmariCode() {
//        return model.getJmariCode();
//    }


    @Override
    public void setModel(IInfoModel model) {
        this.model = (FacilityModel)model;
    }
}
