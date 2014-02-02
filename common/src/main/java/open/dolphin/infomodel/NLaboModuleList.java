package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class NLaboModuleList  extends InfoModel implements java.io.Serializable {
    
    private List<NLaboModule> list;

    public List<NLaboModule> getList() {
        return list;
    }

    public void setList(List<NLaboModule> list) {
        this.list = list;
    }
}
