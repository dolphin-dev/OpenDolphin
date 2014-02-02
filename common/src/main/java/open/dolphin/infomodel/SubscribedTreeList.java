package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class SubscribedTreeList  extends InfoModel implements java.io.Serializable {
    
    private List<SubscribedTreeModel> list;

    public List<SubscribedTreeModel> getList() {
        return list;
    }

    public void setList(List<SubscribedTreeModel> list) {
        this.list = list;
    }
}
