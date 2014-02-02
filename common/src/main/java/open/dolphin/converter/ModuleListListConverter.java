package open.dolphin.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleList;
import open.dolphin.infomodel.ModuleListList;

/**
 *
 * @author kazushi Minagawa.
 */
public class ModuleListListConverter implements IInfoModelConverter {
    
    private ModuleListList model;
    
    public List<ModuleListConverter> getList() {
        
        List<ModuleList> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<ModuleListConverter> ret = new ArrayList<ModuleListConverter>();
        for (ModuleList m : list) {
            ModuleListConverter con = new ModuleListConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (ModuleListList)model;
    }
}
