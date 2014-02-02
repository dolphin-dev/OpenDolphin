package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa. 
 */
public class LetterModuleList  extends InfoModel implements java.io.Serializable {
    
    private List<LetterModule> list;

    public List<LetterModule> getList() {
        return list;
    }

    public void setList(List<LetterModule> list) {
        this.list = list;
    }
}
