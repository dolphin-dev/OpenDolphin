package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.DocInfoList;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class DocInfoListConverter implements IInfoModelConverter {
    
    private DocInfoList model;
    
    public List<DocInfoModelConverter> getList() {
        
        List<DocInfoModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<DocInfoModelConverter> ret = new ArrayList<DocInfoModelConverter>();
        for (DocInfoModel m : list) {
            DocInfoModelConverter con = new DocInfoModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DocInfoList)model;
    }
}
