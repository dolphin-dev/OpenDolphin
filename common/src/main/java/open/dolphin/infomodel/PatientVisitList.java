package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class PatientVisitList  extends InfoModel implements java.io.Serializable {
    
    private List<PatientVisitModel> list;

    public List<PatientVisitModel> getList() {
        return list;
    }

    public void setList(List<PatientVisitModel> list) {
        this.list = list;
    }
}
