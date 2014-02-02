package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class NLaboItemList  extends InfoModel implements java.io.Serializable {
    
    private List<NLaboItem> list;

    public List<NLaboItem> getList() {
        return list;
    }

    public void setList(List<NLaboItem> list) {
        this.list = list;
    }
}
