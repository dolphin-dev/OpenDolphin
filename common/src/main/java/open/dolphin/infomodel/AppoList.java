package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public final class AppoList extends InfoModel implements java.io.Serializable {
    
    private List<AppointmentModel> list;
    
    public List<AppointmentModel> getList() {
        return list;
    }

    public void setList(List<AppointmentModel> list) {
        this.list = list;
    }
}
