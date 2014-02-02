package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientLiteList;
import open.dolphin.infomodel.PatientLiteModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class PatientLiteListConverter implements IInfoModelConverter {
    
    private PatientLiteList model;
    
    public List<PatientLiteModelConverter> getList() {
        
        List<PatientLiteModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<PatientLiteModelConverter> ret = new ArrayList<PatientLiteModelConverter>();
        for (PatientLiteModel m : list) {
            PatientLiteModelConverter con = new PatientLiteModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientLiteList)model;
    }
}
