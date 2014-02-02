package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ObservationList;
import open.dolphin.infomodel.ObservationModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class ObservationListConverter implements IInfoModelConverter {
    
    private ObservationList model;
    
    public List<ObservationModelConverter> getList() {
        
        List<ObservationModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<ObservationModelConverter> ret = new ArrayList<ObservationModelConverter>();
        for (ObservationModel m : list) {
            ObservationModelConverter con = new ObservationModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (ObservationList)model;
    }
}
