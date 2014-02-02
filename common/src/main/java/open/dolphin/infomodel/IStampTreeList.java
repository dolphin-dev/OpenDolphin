package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class IStampTreeList  extends InfoModel implements java.io.Serializable {
    
    private List<IStampTreeModel> list;

    public List<IStampTreeModel> getList() {
        return list;
    }

    public void setList(List<IStampTreeModel> list) {
        this.list = list;
    }
}
