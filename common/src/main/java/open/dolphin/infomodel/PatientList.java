package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class PatientList  extends InfoModel implements java.io.Serializable {
    
    private List<PatientModel> list;

    public List<PatientModel> getList() {
        return list;
    }

    public void setList(List<PatientModel> list) {
        this.list = list;
    }
}
