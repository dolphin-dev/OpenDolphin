package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.StampModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampModelConverter implements IInfoModelConverter {

    private StampModel model;

    public StampModelConverter() {
    }

    public String getId() {
        return model.getId();
    }

    public long getUserId() {
        return model.getUserId();
    }

    public String getEntity() {
        return model.getEntity();
    }

    public byte[] getStampBytes() {
        return model.getStampBytes();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampModel)model;
    }
}
