package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.StampTreeList;
import open.dolphin.infomodel.StampTreeModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class StampTreeListConverter implements IInfoModelConverter {
    
    private StampTreeList model;
    
    public List<StampTreeModelConverter> getList() {
        
        List<StampTreeModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<StampTreeModelConverter> ret = new ArrayList<StampTreeModelConverter>();
        for (StampTreeModel m : list) {
            StampTreeModelConverter con = new StampTreeModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampTreeList)model;
    }
}
