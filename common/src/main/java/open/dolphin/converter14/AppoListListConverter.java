package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.AppoList;
import open.dolphin.infomodel.AppoListList;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class AppoListListConverter implements IInfoModelConverter {
    
    private AppoListList model;
    
    public List<AppoListConverter> getList() {
        
        List<AppoList> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<AppoListConverter> ret = new ArrayList<AppoListConverter>();
        for (AppoList m : list) {
            AppoListConverter con = new AppoListConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (AppoListList)model;
    }
}
