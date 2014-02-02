package open.dolphin.converter;


import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LicenseModel;

/**
 * LicenseModel
 *
 * @author Minagawa,Kazushi
 *
 */
public final class LicenseModelConverter implements IInfoModelConverter {
    
    private LicenseModel model;

    public LicenseModelConverter() {
    }

    public String getLicense() {
        return model.getLicense();
    }

    public String getLicenseDesc() {
        return model.getLicenseDesc();
    }

    public String getLicenseCodeSys() {
        return model.getLicenseCodeSys();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (LicenseModel)model;
    }
}
