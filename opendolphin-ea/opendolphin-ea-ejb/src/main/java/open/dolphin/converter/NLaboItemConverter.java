package open.dolphin.converter;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.NLaboItem;

/**
 *
 * @author kazushi
 */
public final class NLaboItemConverter implements IInfoModelConverter {

    private NLaboItem model;
    
    public NLaboItemConverter() {
    }

    public Long getId() {
        return model.getId();
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getLaboCode() {
        return model.getLaboCode();
    }

    public String getSampleDate() {
        return model.getSampleDate();
    }

    public String getLipemia() {
        return model.getLipemia();
    }

    public String getHemolysis() {
        return model.getHemolysis();
    }

    public String getDialysis() {
        return model.getDialysis();
    }

    public String getReportStatus() {
        return model.getReportStatus();
    }

    public String getGroupCode() {
        return model.getGroupCode();
    }

    public String getGroupName() {
        return model.getGroupName();
    }

    public String getParentCode() {
        return model.getParentCode();
    }

    public String getItemCode() {
        return model.getItemCode();
    }

    public String getMedisCode() {
        return model.getMedisCode();
    }

    public String getItemName() {
        return model.getItemName();
    }

    public String getAbnormalFlg() {
        return model.getAbnormalFlg();
    }

    public String getNormalValue() {
        return model.getNormalValue();
    }

    public String getValue() {
        return model.getValue();
    }

    public String getUnit() {
        return model.getUnit();
    }

    public String getSpecimenCode() {
        return model.getSpecimenCode();
    }

    public String getSpecimenName() {
        return model.getSpecimenName();
    }

    public String getCommentCode1() {
        return model.getCommentCode1();
    }

    public String getComment1() {
        return model.getComment1();
    }

    public String getCommentCode2() {
        return model.getCommentCode2();
    }

    public String getComment2() {
        return model.getComment2();
    }

    public String getSortKey() {
        return model.getSortKey();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (NLaboItem)model;
    }
}
