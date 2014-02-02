package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class ModuleList extends InfoModel implements java.io.Serializable {
    
    private List<ModuleModel> list;

    public List<ModuleModel> getList() {
        return list;
    }

    public void setList(List<ModuleModel> list) {
        this.list = list;
    }
}
