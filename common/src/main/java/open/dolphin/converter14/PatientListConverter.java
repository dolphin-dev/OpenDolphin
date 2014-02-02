package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class PatientListConverter implements IInfoModelConverter {
    
    private PatientList model;
    
    public List<PatientModelConverter> getList() {
        
        List<PatientModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<PatientModelConverter> ret = new ArrayList<PatientModelConverter>();
        for (PatientModel m : list) {
            PatientModelConverter con = new PatientModelConverter();
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
