package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class InteractionCodeList extends InfoModel implements java.io.Serializable {
    
    private List<String> codes1;
    
    private List<String> codes2;

    public List<String> getCodes1() {
        return codes1;
    }

    public void setCodes1(List<String> codes1) {
        this.codes1 = codes1;
    }

    public List<String> getCodes2() {
        return codes2;
    }

    public void setCodes2(List<String> codes2) {
        this.codes2 = codes2;
    }
}
