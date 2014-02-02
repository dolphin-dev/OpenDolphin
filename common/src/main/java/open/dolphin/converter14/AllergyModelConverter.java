package open.dolphin.converter14;

import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class AllergyModelConverter implements IInfoModelConverter {

    private AllergyModel model;

    public AllergyModelConverter() {
    }

    public String getFactor() {
        return model.getFactor();
    }

    public String getIdentifiedDate() {
        return model.getIdentifiedDate();
    }

    public String getMemo() {
        return model.getMemo();
    }

    public String getSeverity() {
        return model.getSeverity();
    }

    public String getSeverityTableId() {
        return model.getSeverityTableId();
    }

    public long getObservationId() {
        return model.getObservationId();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (AllergyModel)model;
    }
}
