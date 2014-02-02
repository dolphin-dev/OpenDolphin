package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class StampTreeList  extends InfoModel implements java.io.Serializable {
    
    private List<StampTreeModel> list;

    public List<StampTreeModel> getList() {
        return list;
    }

    public void setList(List<StampTreeModel> list) {
        this.list = list;
    }
}
