package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.TensuList;
import open.dolphin.infomodel.TensuMaster;

/**
 *
 * @author kazushi Minagawa.
 */
public class TensuListConverter implements IInfoModelConverter {
    
    private TensuList model;
    
    public List<TensuMasterConverter> getList() {
        
        List<TensuMaster> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<TensuMasterConverter> ret = new ArrayList<TensuMasterConverter>();
        for (TensuMaster m : list) {
            TensuMasterConverter con = new TensuMasterConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (TensuList)model;
    }
}
