package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class DocumentList  extends InfoModel implements java.io.Serializable {
    
    private List<DocumentModel> list;

    public List<DocumentModel> getList() {
        return list;
    }

    public void setList(List<DocumentModel> list) {
        this.list = list;
    }
}
