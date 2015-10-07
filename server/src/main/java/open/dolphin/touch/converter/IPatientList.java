package open.dolphin.touch.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.converter.*;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class IPatientList implements IInfoModelConverter {
    
    private PatientList model;
    
    public List<IPatientModel> getList() {
        
        List<PatientModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<IPatientModel> ret = new ArrayList();
        for (PatientModel m : list) {
            IPatientModel con = new IPatientModel();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientList)model;
    }
}
