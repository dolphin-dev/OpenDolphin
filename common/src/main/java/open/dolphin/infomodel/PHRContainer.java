package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class PHRContainer implements java.io.Serializable {
    
    private List<PHRCatch> docList;
    
    private List<PHRLabModule> labList;

    public List<PHRCatch> getDocList() {
        return docList;
    }

    public void setDocList(List<PHRCatch> docList) {
        this.docList = docList;
    }

    public List<PHRLabModule> getLabList() {
        return labList;
    }

    public void setLabList(List<PHRLabModule> labList) {
        this.labList = labList;
    }
}
