package open.dolphin.adm20.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.converter.AllergyModelConverter;
import open.dolphin.converter.IInfoModelConverter;
import open.dolphin.converter.PatientMemoModelConverter;
import open.dolphin.converter.RegisteredDiagnosisModelConverter;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.VisitPackage;

/**
 *
 * @author kazushi Minagawa
 */
public class IVisitPackage implements IInfoModelConverter {
    
    private VisitPackage model;
    
    public long getKartePk() {
        return model.getKartePk();
    }
    
    public String getNumber() {
        return model.getNumber();
    }
    
    public IPatientVisitModel getPatientVisitModel() {
        // PVTHealthInsurance にするためIPatientModelへ変更
        if (model.getPatientVisitModel()!=null) {
            IPatientVisitModel conv = new IPatientVisitModel();
            conv.setModel(model.getPatientVisitModel());
            return conv;
        }
        return null;
    }
    
    public IPatientModel getPatientModel() {
        // PVTHealthInsurance にするためIPatientModelへ変更
        if (model.getPatientModel()!=null) {
            IPatientModel ipm = new IPatientModel();
            ipm.setModel(model.getPatientModel());
            return ipm;
        }
        
        return null;
    }
    
    public IDocument getDocumentModel() {
        if (model.getDocumenModel()!=null) {
            IDocument conv = new IDocument();
            conv.fromModel(model.getDocumenModel());
            return conv;
        }
        return null;
    }
    
    public List<AllergyModelConverter> getAllergies() {
        if (model.getAllergies()!=null && model.getAllergies().size()>0) {
            List<AllergyModelConverter> conv = new ArrayList();
            for (AllergyModel m : model.getAllergies()) {
                AllergyModelConverter ac = new AllergyModelConverter();
                ac.setModel(m);
                conv.add(ac);
            }
            return conv;
        }
        return null;
    }
    
    public PatientMemoModelConverter getPatientMemo() {
        if (model.getPatientMemoModel()!=null) {
            //model.getPatientMemoModel().setUserModel(null);
            //model.getPatientMemoModel().setKarte(null);
            PatientMemoModelConverter conv = new PatientMemoModelConverter();
            conv.setModel(model.getPatientMemoModel());
            return conv;
        }
        return null;
    }
    
    public List<IRegisteredDiagnosis> getDisease() {
        if (model.getDisease()!=null && model.getDisease().size()>0) {
            List<IRegisteredDiagnosis> ret = new ArrayList();
            for (RegisteredDiagnosisModel rd : model.getDisease()) {
                IRegisteredDiagnosis conv = new IRegisteredDiagnosis();
                conv.fromModel(rd);
                ret.add(conv);
            }
            return ret;
        }
        return null;
    }
    
    @Override
    public void setModel(IInfoModel m) {
        this.model = (VisitPackage)m;
    }
}
