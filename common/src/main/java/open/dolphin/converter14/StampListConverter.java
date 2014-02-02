package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.StampList;
import open.dolphin.infomodel.StampModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class StampListConverter implements IInfoModelConverter {
    
    private StampList model;
    
    public List<StampModelConverter> getList() {
        
        List<StampModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<StampModelConverter> ret = new ArrayList<StampModelConverter>();
        for (StampModel m : list) {
            StampModelConverter con = new StampModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampList)model;
    }
}
