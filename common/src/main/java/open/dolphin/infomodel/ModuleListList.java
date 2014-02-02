package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class ModuleListList extends InfoModel implements java.io.Serializable {
    
    private List<ModuleList> list;

    public List<ModuleList> getList() {
        return list;
    }

    public void setList(List<ModuleList> list) {
        this.list = list;
    }
    
    public void addList(ModuleList l) {
        if (list==null) {
            list = new ArrayList<ModuleList>();
        }
        list.add(l);
    }
}
