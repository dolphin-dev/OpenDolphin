package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.AppoList;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class AppoListConverter implements IInfoModelConverter {
    
    private AppoList model;
    
    public List<AppointmentModelConverter> getList() {
        
        List<AppointmentModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<AppointmentModelConverter> ret = new ArrayList<AppointmentModelConverter>();
        for (AppointmentModel m : list) {
            AppointmentModelConverter con = new AppointmentModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (AppoList)model;
    }
}
