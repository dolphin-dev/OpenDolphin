package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class RegisteredDiagnosisList extends InfoModel {
    
    private List<RegisteredDiagnosisModel> list;

    public List<RegisteredDiagnosisModel> getList() {
        return list;
    }

    public void setList(List<RegisteredDiagnosisModel> list) {
        this.list = list;
    }
}
