package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class DrugInteractionList extends InfoModel implements java.io.Serializable {
    
    private List<DrugInteractionModel> list;

    public List<DrugInteractionModel> getList() {
        return list;
    }

    public void setList(List<DrugInteractionModel> list) {
        this.list = list;
    } 
}
