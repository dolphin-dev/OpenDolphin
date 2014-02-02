package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;

/**
 *
 * @author kazushi
 */
public final class PVTPublicInsuranceItemModelConverter implements IInfoModelConverter {

    private PVTPublicInsuranceItemModel model;

    public PVTPublicInsuranceItemModelConverter() {
    }

    public String getPriority() {
        return model.getPriority();
    }

    public String getProviderName() {
        return model.getProviderName();
    }

    public String getProvider() {
        return model.getProvider();
    }

    public String getRecipient() {
        return model.getRecipient();
    }

    public String getStartDate() {
        return model.getStartDate();
    }

    public String getExpiredDate() {
        return model.getExpiredDate();
    }

    public String getPaymentRatio() {
        return model.getPaymentRatio();
    }

    public String getPaymentRatioType() {
        return model.getPaymentRatioType();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PVTPublicInsuranceItemModel)model;
    }
}
