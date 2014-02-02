package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SubscribedTreeList;
import open.dolphin.infomodel.SubscribedTreeModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class SubscribedTreeListConverter implements IInfoModelConverter {
    
    private SubscribedTreeList model;
    
    public List<SubscribedTreeModelConverter> getList() {
        
        List<SubscribedTreeModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<SubscribedTreeModelConverter> ret = new ArrayList<SubscribedTreeModelConverter>();
        for (SubscribedTreeModel m : list) {
            SubscribedTreeModelConverter con = new SubscribedTreeModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (SubscribedTreeList)model;
    }
}
