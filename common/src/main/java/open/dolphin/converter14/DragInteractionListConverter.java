package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.DrugInteractionList;
import open.dolphin.infomodel.DrugInteractionModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class DragInteractionListConverter implements IInfoModelConverter {
    
    private DrugInteractionList model;
    
    public List<DrugInteractionModelConverter> getList() {
        
        List<DrugInteractionModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<DrugInteractionModelConverter> ret = new ArrayList<DrugInteractionModelConverter>();
        for (DrugInteractionModel m : list) {
            DrugInteractionModelConverter con = new DrugInteractionModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DrugInteractionList)model;
    }
}
