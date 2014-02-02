package open.dolphin.converter;

import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.StringList;

/**
 *
 * @author kazushi
 */
public class StringListConverter implements IInfoModelConverter {
    
    private StringList model;
    
    public List<String> getList() {
        return model.getList();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (StringList)model;
    }
}
