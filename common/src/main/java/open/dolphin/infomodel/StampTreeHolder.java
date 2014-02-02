package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi
 */
public class StampTreeHolder extends InfoModel implements java.io.Serializable {
    
    // 個人用のtree
    private StampTreeModel personalTree;
    
    // import しているtreeのリスト
    private List<PublishedTreeModel> subscribedList;

    public StampTreeModel getPersonalTree() {
        return personalTree;
    }

    public void setPersonalTree(StampTreeModel personalTree) {
        this.personalTree = personalTree;
    }

    public List<PublishedTreeModel> getSubscribedList() {
        return subscribedList;
    }

    public void setSubscribedList(List<PublishedTreeModel> subscribedList) {
        this.subscribedList = subscribedList;
    }
    
    public void addSubscribedTree(PublishedTreeModel tree) {
        if (this.subscribedList==null) {
            this.subscribedList = new ArrayList<PublishedTreeModel>();
        }
        this.subscribedList.add(tree);
    }
}
