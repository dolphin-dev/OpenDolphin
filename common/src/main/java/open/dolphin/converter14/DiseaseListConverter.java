package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.DiseaseEntry;
import open.dolphin.infomodel.DiseaseList;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class DiseaseListConverter implements IInfoModelConverter {
    
    private DiseaseList model;
    
    public List<DiseaseEntryConverter> getList() {
        
        List<DiseaseEntry> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<DiseaseEntryConverter> ret = new ArrayList<DiseaseEntryConverter>();
        for (DiseaseEntry m : list) {
            DiseaseEntryConverter con = new DiseaseEntryConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiseaseList)model;
    }
}
