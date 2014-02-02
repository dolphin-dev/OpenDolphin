package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DocInfoModelConverter implements IInfoModelConverter {

    private DocInfoModel model;

    public DocInfoModelConverter() {
    }

    public long getDocPk() {
        return model.getDocPk();
    }

    public long getParentPk() {
        return model.getParentPk();
    }

    public String getDocId() {
        return model.getDocId();
    }

    public String getDocType() {
        return model.getDocType();
    }

    public String getTitle() {
        return model.getTitle();
    }

    public String getPurpose() {
        return model.getPurpose();
    }

    public String getPurposeDesc() {
        return model.getPurposeDesc();
    }

    public String getPurposeCodeSys() {
        return model.getPurposeCodeSys();
    }

    public Date getFirstConfirmDate() {
        return model.getFirstConfirmDate();
    }

    public Date getConfirmDate() {
        return model.getConfirmDate();
    }

    public String getDepartment() {
        return model.getDepartment();
    }

    public String getDepartmentDesc() {
        return model.getDepartmentDesc();
    }

    public String getDepartmentCodeSys() {
        return model.getDepartmentCodeSys();
    }

    public String getHealthInsurance() {
        return model.getHealthInsurance();
    }

    public String getHealthInsuranceDesc() {
        return model.getHealthInsuranceDesc();
    }

    public String getHealthInsuranceCodeSys() {
        return model.getHealthInsuranceCodeSys();
    }

    public String getHealthInsuranceGUID() {
        return model.getHealthInsuranceGUID();
    }

    public boolean getHasMark() {
        return model.isHasMark();
    }

    public void setHasMark(boolean hasMark) {
        model.setHasMark(hasMark);
    }

    public boolean getHasImage() {
        return model.isHasImage();
    }

    public void setHasImage(boolean hasImage) {
        model.setHasImage(hasImage);
    }

    public boolean getHasRp() {
        return model.isHasRp();
    }

    public void setHasRp(boolean hasRp) {
        model.setHasRp(hasRp);
    }

    public boolean getHasTreatment() {
        return model.isHasTreatment();
    }

    public void setHasTreatment(boolean hasTreatment) {
        model.setHasTreatment(hasTreatment);
    }

    public boolean getHasLaboTest() {
        return model.isHasLaboTest();
    }

    public void setHasLaboTest(boolean hasLaboTest) {
        model.setHasLaboTest(hasLaboTest);
    }

    public String getVersionNumber() {
        return model.getVersionNumber();
    }

    public String getVersionNotes() {
        return model.getVersionNotes();
    }

//    public long getPvtId() {
//        return model.getPvtId();
//    }

    public String getParentId() {
        return model.getParentId();
    }

    public String getParentIdRelation() {
        return model.getParentIdRelation();
    }

    public String getParentIdDesc() {
        return model.getParentIdDesc();
    }

    public String getParentIdCodeSys() {
        return model.getParentIdCodeSys();
    }

    public String getStatus() {
        return model.getStatus();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (DocInfoModel)model;
    }
}
