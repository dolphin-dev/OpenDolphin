package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PhysicalModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PhysicalModelConverter implements IInfoModelConverter {

    private PhysicalModel model;

    public PhysicalModelConverter() {
    }

    public long getHeightId() {
        return model.getHeightId();
    }

    public long getWeightId() {
        return model.getWeightId();
    }

    // factor
    public String getHeight() {
        return model.getHeight();
    }

    // identifiedDate
    public String getIdentifiedDate() {
        return model.getIdentifiedDate();
    }

    // memo
    public String getMemo() {
        return model.getMemo();
    }

    public String getWeight() {
        return model.getWeight();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PhysicalModel)model;
    }
}
