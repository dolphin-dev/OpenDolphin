package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class OrcaInputCdList extends InfoModel implements java.io.Serializable {
    
    private List<OrcaInputCd> list;

    public List<OrcaInputCd> getList() {
        return list;
    }

    public void setList(List<OrcaInputCd> list) {
        this.list = list;
    }
}
