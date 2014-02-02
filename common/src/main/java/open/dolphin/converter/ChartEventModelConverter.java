/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.converter;

import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi
 */
public class ChartEventModelConverter implements IInfoModelConverter {
    
    private ChartEventModel model;
    
    public int getEventType() {
        return model.getEventType();
    }
    public long getPvtPk() {
        return model.getPvtPk();
    }
    public int getState() {
        return model.getState();
    }
    public int getByomeiCount() {
        return model.getByomeiCount();
    }
    public int getByomeiCountToday() {
        return model.getByomeiCountToday();
    }
    
    public String getIssuerUUID() {
        return model.getIssuerUUID();
    }
    public String getOwnerUUID() {
        return model.getOwnerUUID();
    }
    
    public PatientVisitModelConverter getPatientVisitModel() {
        if (model.getPatientVisitModel()!=null) {
            PatientVisitModelConverter con = new PatientVisitModelConverter();
            con.setModel(model.getPatientVisitModel());
            return con;
        }
        return null;
    }
    
    public long getPtPk() {
        return model.getPtPk();
    }
    public PatientModelConverter getPatientModel() {
        if (model.getPatientModel()!=null) {
            PatientModelConverter con = new PatientModelConverter();
            con.setModel(model.getPatientModel());
            return con;
        }
        return null;
    }
    public String getMemo() {
        return model.getMemo();
    }
    public String getFacilityId() {
        return model.getFacilityId();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (ChartEventModel)model;
    }   
}
