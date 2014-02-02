package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class DocInfoList extends InfoModel implements java.io.Serializable {
    
    private List<DocInfoModel> list;

    public List<DocInfoModel> getList() {
        return list;
    }

    public void setList(List<DocInfoModel> list) {
        this.list = list;
    }
}
