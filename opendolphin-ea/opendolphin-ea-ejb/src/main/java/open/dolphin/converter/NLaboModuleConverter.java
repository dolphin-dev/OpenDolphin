package open.dolphin.converter;

import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;

/**
 *
 * @author kazushi
 */
public final class NLaboModuleConverter implements IInfoModelConverter {

    private NLaboModule model;

    public NLaboModuleConverter() {
    }

    public Long getId() {
        return model.getId();
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getPatientName() {
        return model.getPatientName();
    }

    public String getPatientSex() {
        return model.getPatientSex();
    }

    public String getSampleDate() {
        return model.getSampleDate();
    }

    public String getNumOfItems() {
        return model.getNumOfItems();
    }

    public List<NLaboItem> getItems() {
        return model.getItems();
    }

    public Boolean getProgressState() {
        return model.getProgressState();
    }

    public void setProgressState(Boolean progressState) {
        model.setProgressState(progressState);
    }

    public String getLaboCenterCode() {
        return model.getLaboCenterCode();
    }

    public String getModuleKey() {
        return model.getModuleKey();
    }

    public String getReportFormat() {
        return model.getReportFormat();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (NLaboModule)model;
    }
}
