package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboItemList;

/**
 *
 * @author kazushi Minagawa.
 */
public class NLaboItemListConverter implements IInfoModelConverter {
    
    private NLaboItemList model;
    
    public List<NLaboItemConverter> getList() {
        
        List<NLaboItem> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<NLaboItemConverter> ret = new ArrayList<NLaboItemConverter>();
        for (NLaboItem m : list) {
            NLaboItemConverter con = new NLaboItemConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (NLaboItemList)model;
    }
}
