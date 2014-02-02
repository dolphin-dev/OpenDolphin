package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class DiseaseList  extends InfoModel implements java.io.Serializable {
    
    private List<DiseaseEntry> list;

    public List<DiseaseEntry> getList() {
        return list;
    }

    public void setList(List<DiseaseEntry> list) {
        this.list = list;
    }
}
