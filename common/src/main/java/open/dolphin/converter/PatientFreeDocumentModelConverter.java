/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.converter;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientFreeDocumentModel;

/**
 * サマリー対応
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class PatientFreeDocumentModelConverter implements IInfoModelConverter {

    private PatientFreeDocumentModel model;

    public PatientFreeDocumentModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public String getFacilityPatId() {
        return model.getFacilityPatId();
    }

    public Date getConfirmed() {
        return model.getConfirmed();
    }

    public String getComment() {
        return model.getComment();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientFreeDocumentModel)model;
    }
}
