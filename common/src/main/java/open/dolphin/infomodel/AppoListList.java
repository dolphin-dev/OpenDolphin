package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class AppoListList extends InfoModel implements java.io.Serializable {
    
    private List<AppoList> list;

    public List<AppoList> getList() {
        return list;
    }

    public void setList(List<AppoList> list) {
        this.list = list;
    }
    
    public void addList(AppoList l) {
        if (list==null) {
            list = new ArrayList<AppoList>();
        }
        list.add(l);
    }
}
