package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class StampList  extends InfoModel implements java.io.Serializable {
    
    private List<StampModel> list;

    public List<StampModel> getList() {
        return list;
    }

    public void setList(List<StampModel> list) {
        this.list = list;
    }
}
